package com.atom.fcc.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.atom.fcc.dao.NodeEdgeDAO;


public class SearchServices implements ISearchServices {

	final static Logger logger = Logger.getLogger(SearchServices.class);

	/*
	 * Main method to search the path
	 * (non-Javadoc)
	 * @see com.adidas.fcc.services.ISearchServices#executeSearch(java.io.File)
	 */
	
	public void executeSearch(File file) {
		// TODO Auto-generated method stub
		try {
			
			FileReader fr = new FileReader(file);

			BufferedReader br = new BufferedReader(fr);
			String line;

			while ((line = br.readLine()) != null) {
				
				// Find the price of a specific connection
				if (line.contains("What is the price of the connection")) {
					String[] elements = line.split("What is the price of the connection");
					int output = findPC(elements[1].substring(0, elements[1].length() - 1));

					if (output == 0) {
						logger.info(elements[0] + "No such connection found!");
					} else {
						logger.info(elements[0] + output);
					}
				// Find the cheapest connection between source and destination	
				} else if (line.contains("What is the cheapest connection from")) {
					String[] elements = line.split("What is the cheapest connection from");
					logger.debug(elements[1].substring(0, elements[1].length()- 1));
					String output = findCC(elements[1].substring(0, elements[1].length() - 1));

					if (output.equals("")) {
						logger.info(elements[0] + "No such connection found!");
					} else {
						logger.info(elements[0] + output);
					}
					// find all the options between source and destination with number of stops as a condition
				} else if (line.contains("How many different connections with")) {
					String[] elements = line.split("How many different connections with");
					logger.debug("elements1" + elements[1].trim());
					String[] els = elements[1].trim().split(" ");

					int output = findDC(els[0], Integer.valueOf(els[1]).intValue(), els[5],
							els[7].substring(0, els[7].length() - 1));
					logger.info(elements[0] + output);
					// find all the options between source and destination with a cost as a condition
				} else if (line.contains("Find all conenctions from")) {
					String[] elements = line.split("Find all conenctions from");
					String[] els = elements[1].trim().split(" ");
					logger.debug(els[4]);
					List<String> output = findAP(els[3],
							Integer.valueOf(els[4].split("Euro")[0].trim()).intValue(), els[0], els[2]);
					
					if (output.isEmpty()) {
					 logger.info(elements[0] + "No such connection found!");
					}
					
					for (String out : output) {
						logger.info(elements[0] + out);
					}
					logger.debug(elements[0]+output);

				}

			}

			br.close();
			fr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public List<String> findAP(String type, int cost, String source, String target) {
		NodeEdgeDAO nodeEdgeDAO = new NodeEdgeDAO();
		return nodeEdgeDAO.getAllConnectionwithCostCondition(source, target, cost, type);
	}
	
	public String findCC(String key) {

		String[] details = key.split("to");
		NodeEdgeDAO nodeEdgeDAO = new NodeEdgeDAO();
		HashMap<Integer, String> history = new HashMap<Integer, String>();
		history.put(0, details[0].trim());
		return nodeEdgeDAO.getCheapestConnection(details[0].trim(), details[1].trim());

	}
	
	public int findDC(String type, int stepcounts, String source, String target) {
		NodeEdgeDAO nodeEdgeDAO = new NodeEdgeDAO();
		return nodeEdgeDAO.getAllConnectionwithStepCondition(source, target, stepcounts, type);
	}
	
	public int findPC(String key) {

		String[] details = key.split("-");
		String previous = null;
		NodeEdgeDAO nodeEdgeDAO = new NodeEdgeDAO();
		int cost = 0;
		for (String detail : details) {

			if (!(previous == null)) {

				if (nodeEdgeDAO.getPriceDirectConnection(previous.toString().trim(), detail.toString().trim()) == 0) {

					return 0;
				}

				else {
					cost = cost + nodeEdgeDAO.getPriceDirectConnection(previous.toString().trim(),
							detail.toString().trim());
				}
			}
			previous = detail;
		}
		return cost;
	}
	
}
