package com.atom.fcc.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.atom.fcc.FlightCostCalculator;
import com.atom.fcc.model.Edge;
import com.atom.fcc.model.Graph;
import com.atom.fcc.model.Vertex;
import com.atom.fcc.services.GraphServices;

public class NodeEdgeDAO implements INodeEdgeDAO{

	static private List<Vertex> nodes;
	static private List<Edge> edges;
	final static Logger logger = Logger.getLogger(NodeEdgeDAO.class);
	
/*
 * Load the input file in the graph
 * (non-Javadoc)
 * @see com.adidas.fcc.dao.INodeEdgeDAO#loadModel(java.io.File)
 */
	public void loadModel(File file) {
		nodes = new ArrayList<Vertex>();
		edges = new ArrayList<Edge>();

		try {

			FileReader fr = new FileReader(file);

			BufferedReader br = new BufferedReader(fr);
			String line;

			while ((line = br.readLine()) != null) {
				if (line.contains("Connection:")) {

					String[] elements = line.substring(12).split(", ");
					String[] details;
					for (String element : elements) {
						details = element.split("-");

						Vertex source = new Vertex(details[0], details[0]);
						if (!(nodes.contains(details[0]))) {
							nodes.add(source);
						}

						Vertex destination = new Vertex(details[1], details[1]);
						if (!(nodes.contains(details[1]))) {
							nodes.add(destination);
						}

						Edge lane = new Edge(details[0] + details[1], source, destination,
								(Integer.valueOf(details[2])).intValue());
						edges.add(lane);

					}

				}
			}

			br.close();
			fr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*
	 * Get the price of a connection
	 * (non-Javadoc)
	 * @see com.adidas.fcc.dao.INodeEdgeDAO#getPriceDirectConnection(java.lang.String, java.lang.String)
	 */
	public int getPriceDirectConnection(String source, String target) {

		for (Edge edge : edges) {
			if (edge.getSource().getName().equals(source) && edge.getDestination().getName().equals(target)) {

				return edge.getCost();
			}

		}

		return 0;

	}

	/*
	 * Retrieve a node by providing a name
	 * (non-Javadoc)
	 * @see com.adidas.fcc.dao.INodeEdgeDAO#getNodebyName(java.lang.String)
	 */
	public Vertex getNodebyName(String name) {

		for (Vertex node : nodes) {

			if (node.getName().equals(name)) {

				return node;
			}
		}

		return null;

	}

	/*
	 * Retrieve an Edge by providing a source and a target
	 * (non-Javadoc)
	 * @see com.adidas.fcc.dao.INodeEdgeDAO#getEdgebySourceDestination(com.adidas.fcc.model.Vertex, com.adidas.fcc.model.Vertex)
	 */
	public Edge getEdgebySourceDestination(Vertex source, Vertex target) {
		
		logger.debug(source.getId()+target.getId());
		for (Edge edge : edges) {
			logger.debug(edge.getSource()+"::"+edge.getDestination());
			if (((edge.getSource().getId()).equals(source.getName()))
					&& ((edge.getDestination().getId()).equals(target.getName()))) {

				// System.out.println(edge);
				return edge;
			}
		}
		return null;
	}

	/*
	 * Retrieve the cheapest connection between source and destination
	 * (non-Javadoc)
	 * @see com.adidas.fcc.dao.INodeEdgeDAO#getCheapestConnection(java.lang.String, java.lang.String)
	 */
	public String getCheapestConnection(String source, String target) {
		String cheapestConnection = "";

		Graph graph = new Graph(nodes, edges);
		GraphServices graphServices = new GraphServices(graph);
		
		// Find out the cheapest path (Dijkstra)
		graphServices.execute(getNodebyName(source));
		LinkedList<Vertex> path = graphServices.getPath(getNodebyName(target));

		int cost = 0;
		if (path == null) {
			return "";
		}

		Vertex previous = null;
		
		// loop on the path and retrieve the cost
		for (Vertex v : path) {
			if (previous == null) {
				previous = v;
				cheapestConnection = v.getName();
			} else {
				logger.debug("previous"+previous);
				cost = cost + (getEdgebySourceDestination(previous, v)).getCost();
				cheapestConnection = cheapestConnection + "-" + v.getName();
				previous = v;
			}

		}

		return cheapestConnection + "-" + String.valueOf(cost);
	}

	/*
	 * Retrieve all the possible connections with a number of stops as conditions
	 * (non-Javadoc)
	 * @see com.adidas.fcc.dao.INodeEdgeDAO#getAllConnectionwithStepCondition(java.lang.String, java.lang.String, int, java.lang.String)
	 */
	
	public int getAllConnectionwithStepCondition(String source, String target, int condition, String type) {

		logger.debug(source + condition + target);
		Graph graph = new Graph(nodes, edges);
		GraphServices dijkstra = new GraphServices(graph);

		Set<List<Vertex>> path = dijkstra.getAllPathsTo(getNodebyName(source), getNodebyName(target));
		if (path == null) {
			return 0;
		}
		
		int count = 0;
		int i = 0;
		// if the condition is minimum
		if (type.equals("minimum")) {
			for (Iterator<List<Vertex>> iter = path.iterator(); iter.hasNext(); i++) {
				List<Vertex> p = iter.next();
				logger.debug("Path " + i + ": " + p);
				if (p.size()-2 >= condition) {
					count++;
				}

			}
		// if the condition is maximum
		} else if (type.equals("maximum")) {
			for (Iterator<List<Vertex>> iter = path.iterator(); iter.hasNext(); i++) {
				List<Vertex> p = iter.next();
				logger.debug("Path " + i + ": " + p);
				if (p.size()-2 <= condition) {
					count++;
				}
			}
			
		// if the condition is equal to
		} else if (type.equals("exactly")) {
			for (Iterator<List<Vertex>> iter = path.iterator(); iter.hasNext(); i++) {
				List<Vertex> p = iter.next();
				logger.debug("Path " + i + ": " + p);
				if (p.size()-2 == condition) {
					count++;
				}
			}
		} else {
			logger.debug("wrong operator");
		}

		return count;

	}

	
	/*
	 * Retrieve all the possible connections with cost condition
	 * (non-Javadoc)
	 * @see com.adidas.fcc.dao.INodeEdgeDAO#getAllConnectionwithCostCondition(java.lang.String, java.lang.String, int, java.lang.String)
	 */
	public List<String> getAllConnectionwithCostCondition(String source, String target, int condition, String type) {
		String allsteps = "";
		List<String> lallsteps = new ArrayList<String>();

		logger.debug(source + condition + target);
		Graph graph = new Graph(nodes, edges);
		GraphServices dijkstra = new GraphServices(graph);
		Set<List<Vertex>> path = dijkstra.getAllPathsTo(getNodebyName(source), getNodebyName(target));
		int i = 0;
		int cost = 0;
		if (type.equals("below")) {
			for (Iterator<List<Vertex>> iter = path.iterator(); iter.hasNext(); i++) {
				allsteps = "";
				List<Vertex> p = iter.next();
				logger.debug("Path " + i + ": " + p);
				Vertex previous = null;
				for (Vertex v : p) {
					if (previous == null) {
						previous = v;
						allsteps=v.getId()+"-";
					} else {
						logger.debug("previous"+previous);
						cost = cost + (getEdgebySourceDestination(previous, v)).getCost();
						allsteps = allsteps + v.getId() + "-";
						previous = v;
					}

				}
				logger.debug("allsteps:"+allsteps);
				if (cost <= condition) {
					allsteps = allsteps + String.valueOf(cost);
					lallsteps.add(allsteps);
				}

			}
		} else if (type.equals("above")) {
			for (Iterator<List<Vertex>> iter = path.iterator(); iter.hasNext(); i++) {
				List<Vertex> p = iter.next();
				logger.debug("Path " + i + ": " + p);
				allsteps = "";
				Vertex previous = null;
				for (Vertex v : p) {
					if (previous == null) {
						previous = v;
						allsteps=v.getId()+"-";
					} else {
						logger.debug("previous"+previous);
						cost = cost + (getEdgebySourceDestination(previous, v)).getCost();
						allsteps = allsteps + v.getId() + "-";
						previous = v;
					}

				}

				if (cost >= condition) {
					allsteps = allsteps + String.valueOf(cost);
					lallsteps.add(allsteps);
				}

			}

		} else if (type.equals("exactly")) {
			for (Iterator<List<Vertex>> iter = path.iterator(); iter.hasNext(); i++) {
				allsteps = "";
				List<Vertex> p = iter.next();
				logger.debug("Path " + i + ": " + p);
				Vertex previous = null;
				for (Vertex v : p) {
					if (previous == null) {
						previous = v;
						allsteps=v.getId()+"-";
					} else {
						logger.debug("previous"+previous);
						cost = cost + (getEdgebySourceDestination(previous, v)).getCost();
						allsteps = allsteps + v.getId() + "-";
						previous = v;
					}

				}

				if (cost == condition) {
					allsteps = allsteps + String.valueOf(cost);
					lallsteps.add(allsteps);
				}

			}

		} else {
			logger.debug("wrong operator");
		}

		return lallsteps;

	}

}
