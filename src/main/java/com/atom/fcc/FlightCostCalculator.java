package com.atom.fcc;

import java.io.File;

import org.apache.log4j.Logger;

import com.atom.fcc.dao.INodeEdgeDAO;
import com.atom.fcc.dao.NodeEdgeDAO;
import com.atom.fcc.services.ISearchServices;
import com.atom.fcc.services.SearchServices;

/*
 * Main class to execute the calculator
 * 
 */
public class FlightCostCalculator {

	final static Logger logger = Logger.getLogger(FlightCostCalculator.class);


	public static void main(String args[]) throws Exception {
		
		
		if (args.length > 0) {
			File file = new File(args[0]);
			
			if (!(file.exists())) {
				
				logger.info("file not found");
				
				return;
			}
			
			ISearchServices searchServices = new SearchServices();
			INodeEdgeDAO connectionDAO = new NodeEdgeDAO();
			// load the text file
			connectionDAO.loadModel(file);
			
			// Search the required informations
			searchServices.executeSearch(file);
		}

	}

}
