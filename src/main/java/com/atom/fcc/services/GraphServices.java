package com.atom.fcc.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.atom.fcc.FlightCostCalculator;
import com.atom.fcc.model.Edge;
import com.atom.fcc.model.Graph;
import com.atom.fcc.model.Vertex;

public class GraphServices {

	private final List<Vertex> nodes;
	private final List<Edge> edges;
	private Set<Vertex> settledNodes;
	private Set<Vertex> unSettledNodes;
	private Map<Vertex, Vertex> predecessors;
	private Map<Vertex, Integer> distance;
	private Set<List<Vertex>> allPaths;
	final static Logger logger = Logger.getLogger(FlightCostCalculator.class);

	public GraphServices(Graph graph) {
		this.nodes = new ArrayList<Vertex>(graph.getVertexes());
		this.edges = new ArrayList<Edge>(graph.getEdges());
	}

	public void execute(Vertex source) {

		logger.debug("source:" + source.getName());
		settledNodes = new HashSet<Vertex>();
		unSettledNodes = new HashSet<Vertex>();
		distance = new HashMap<Vertex, Integer>();
		predecessors = new HashMap<Vertex, Vertex>();
		distance.put(source, 0);
		unSettledNodes.add(source);
		while (unSettledNodes.size() > 0) {
			Vertex node = getMinimum(unSettledNodes);
			settledNodes.add(node);
			unSettledNodes.remove(node);
			findMinimalDistances(node);
		}
	}

	private void findMinimalDistances(Vertex node) {
		List<Vertex> adjacentNodes = getNeighbors(node);
		for (Vertex target : adjacentNodes) {
			if (getShortestDistance(target) > getShortestDistance(node) + getDistance(node, target)) {
				distance.put(target, getShortestDistance(node) + getDistance(node, target));
				predecessors.put(target, node);
				unSettledNodes.add(target);
			}
		}

	}

	private int getDistance(Vertex node, Vertex target) {
		for (Edge edge : edges) {
			if (edge.getSource().equals(node) && edge.getDestination().equals(target)) {
				return edge.getCost();
			}
		}
		throw new RuntimeException("Should not happen");
	}

	private List<Vertex> getNeighbors(Vertex node) {
		List<Vertex> neighbors = new ArrayList<Vertex>();
		for (Edge edge : edges) {
			if (edge.getSource().equals(node) && !isSettled(edge.getDestination())) {
				neighbors.add(edge.getDestination());
			}
		}
		return neighbors;
	}

	private Vertex getMinimum(Set<Vertex> vertexes) {
		Vertex minimum = null;
		for (Vertex vertex : vertexes) {
			if (minimum == null) {
				minimum = vertex;
			} else {
				if (getShortestDistance(vertex) < getShortestDistance(minimum)) {
					minimum = vertex;
				}
			}
		}
		return minimum;
	}

	private boolean isSettled(Vertex vertex) {
		return settledNodes.contains(vertex);
	}

	private int getShortestDistance(Vertex destination) {
		Integer d = distance.get(destination);
		if (d == null) {
			return Integer.MAX_VALUE;
		} else {
			return d;
		}
	}

	/*
	 * This method returns the path from the source to the selected target and
	 * NULL if no path exists
	 */
	public LinkedList<Vertex> getPath(Vertex target) {

		logger.debug("target:" + target.getName());

		LinkedList<Vertex> path = new LinkedList<Vertex>();
		Vertex step = target;
		// check if a path exists
		if (predecessors.get(step) == null) {
			return null;
		}
		path.add(step);
		while (predecessors.get(step) != null) {
			step = predecessors.get(step);
			path.add(step);
		}
		// Put it into the correct order
		Collections.reverse(path);
		return path;
	}

	/*
	 * This method returns all the possible paths from the source to the
	 * selected target and NULL if no path exists
	 */

	public Set<List<Vertex>> getAllPathsTo(Vertex source, Vertex target) {
		allPaths = new HashSet<List<Vertex>>();
		settledNodes = new HashSet<Vertex>();
		unSettledNodes = new HashSet<Vertex>();

		List<Vertex> initialPath = new ArrayList<Vertex>();
		initialPath.add(source);

		List<Vertex> endpath = getPath(initialPath, source, target);
		logger.debug("endpath" + endpath);

		return allPaths;
	}

	List<Vertex> updatedPath = null;

	private List<Vertex> getPath(List<Vertex> shortestPath, Vertex source, Vertex target) {

		List<Vertex> prev = getNeighbors(source);
		logger.debug("getNeighbors:" + source + ":" + prev);
		// no Neighbors
		if (prev == null) {
			// already have the source and target on the path
			if (updatedPath.contains(source) && updatedPath.contains(target)) {

				allPaths.add(updatedPath);
				updatedPath = new ArrayList<Vertex>(shortestPath);

			} else {
				// blocked route
				updatedPath = new ArrayList<Vertex>(shortestPath);
			}

		} else {

			updatedPath = new ArrayList<Vertex>(shortestPath);
			// loop on all the Neighbors
			for (Iterator<Vertex> iterator = prev.iterator(); iterator.hasNext();) {
				Vertex vertex = iterator.next();
				logger.debug("neighbour: " + vertex + " source: " + source + "target: " + target);
				// if we reach the target
				if ((vertex.getId().equals(target.getId()))) {

					updatedPath.add(vertex);
					allPaths.add(updatedPath);

					logger.debug("updatedPath: " + updatedPath);
					logger.debug("shortestPath: " + shortestPath);
					updatedPath = new ArrayList<Vertex>(shortestPath);
				} 
				// if vertex is already on the path
				else if (updatedPath.contains(vertex)) {
					
					logger.debug("vertex already exit");
				}
				// vertex is not on the path, recursive call
				else if (!(updatedPath.contains(vertex)) && (!(vertex.getId()).equals(target.getId()))
						&& (!(vertex.getId()).equals(source.getId())) && !(settledNodes.contains(vertex))) {
					updatedPath.add(vertex);
					getPath(updatedPath, vertex, target);
				}
			}
			updatedPath.remove(source);
		}

		return shortestPath;
	}
}
