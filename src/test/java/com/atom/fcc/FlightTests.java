package com.atom.fcc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.atom.fcc.dao.INodeEdgeDAO;
import com.atom.fcc.dao.NodeEdgeDAO;
import com.atom.fcc.model.Edge;
import com.atom.fcc.model.Vertex;
import com.atom.fcc.services.ISearchServices;
import com.atom.fcc.services.SearchServices;

public class FlightTests {

	@Test
	public void testfindPC() {

		List<Vertex> nodes = new ArrayList<Vertex>();
		List<Edge> edges = new ArrayList<Edge>();
		ISearchServices searchServices = new SearchServices();
		INodeEdgeDAO nodeEdgeDAO = new NodeEdgeDAO();
		String line = "Connection: NUE-FRA-43, AMS-NUE-67, FRA-AMS-17, FRA-LHR-27, LHR-NUE-23, NUE-DXB-89, DXB-FRA-105, HKG-NUE-109, PDX-NUE-108, AMS-DXB-99, HKG-AMS-101, LHR-AMS-97, FRA-HKG-100, HKG-FRA-105";

		File file = null;
		try {
			String filename = "flighttest.txt";
			FileWriter fw = new FileWriter(filename, false);
			fw.write(line);// appends the string to the file
			fw.close();

			file = new File("flighttest.txt");
			nodeEdgeDAO.loadModel(file);

			int output = searchServices.findPC("NUE-FRA-LHR");
			file.deleteOnExit();
			assertEquals(output, 70);

		} catch (IOException ioe) {
			System.err.println("IOException: " + ioe.getMessage());
		}
	}

	@Test
	public void testfindAP() {

		List<Vertex> nodes = new ArrayList<Vertex>();
		List<Edge> edges = new ArrayList<Edge>();
		ISearchServices searchServices = new SearchServices();
		INodeEdgeDAO nodeEdgeDAO = new NodeEdgeDAO();
		String line = "Connection: NUE-FRA-43, AMS-NUE-67, FRA-AMS-17, FRA-LHR-27, LHR-NUE-23, NUE-DXB-89, DXB-FRA-105, HKG-NUE-109, PDX-NUE-108, AMS-DXB-99, HKG-AMS-101, LHR-AMS-97, FRA-HKG-100, HKG-FRA-105";

		File file = null;
		try {
			String filename = "flighttest.txt";
			FileWriter fw = new FileWriter(filename, false);
			fw.write(line);// appends the string to the file
			fw.close();

			file = new File("flighttest.txt");
			nodeEdgeDAO.loadModel(file);

			List<String> output = searchServices.findAP("below", 200, "NUE","LHR");
			file.deleteOnExit();
			assertEquals(output.get(0), "NUE-FRA-LHR-70");

		} catch (IOException ioe) {
			System.err.println("IOException: " + ioe.getMessage());
		}
	}

	@Test
	public void testfindCC() {

		List<Vertex> nodes = new ArrayList<Vertex>();
		List<Edge> edges = new ArrayList<Edge>();
		ISearchServices searchServices = new SearchServices();
		INodeEdgeDAO nodeEdgeDAO = new NodeEdgeDAO();
		String line = "Connection: NUE-FRA-43, AMS-NUE-67, FRA-AMS-17, FRA-LHR-27, LHR-NUE-23, NUE-DXB-89, DXB-FRA-105, HKG-NUE-109, PDX-NUE-108, AMS-DXB-99, HKG-AMS-101, LHR-AMS-97, FRA-HKG-100, HKG-FRA-105";

		File file = null;
		try {
			String filename = "flighttest.txt";
			FileWriter fw = new FileWriter(filename, false);
			fw.write(line);// appends the string to the file
			fw.close();

			file = new File("flighttest.txt");
			nodeEdgeDAO.loadModel(file);

			String output = searchServices.findCC("NUE to AMS");
			file.deleteOnExit();
			assertEquals(output, "NUE-FRA-AMS-60");

		} catch (IOException ioe) {
			System.err.println("IOException: " + ioe.getMessage());
		}
	}

	@Test
	public void testfindDC() {

		List<Vertex> nodes = new ArrayList<Vertex>();
		List<Edge> edges = new ArrayList<Edge>();
		ISearchServices searchServices = new SearchServices();
		INodeEdgeDAO nodeEdgeDAO = new NodeEdgeDAO();
		String line = "Connection: NUE-FRA-43, AMS-NUE-67, FRA-AMS-17, FRA-LHR-27, LHR-NUE-23, NUE-DXB-89, DXB-FRA-105, HKG-NUE-109, PDX-NUE-108, AMS-DXB-99, HKG-AMS-101, LHR-AMS-97, FRA-HKG-100, HKG-FRA-105";

		File file = null;
		try {
			String filename = "flighttest.txt";
			FileWriter fw = new FileWriter(filename, false);
			fw.write(line);// appends the string to the file
			fw.close();

			file = new File("flighttest.txt");
			nodeEdgeDAO.loadModel(file);
			int output = searchServices.findDC("maximum", 3, "AMS", "FRA");
			file.deleteOnExit();
			assertEquals(output, 3);

		} catch (IOException ioe) {
			System.err.println("IOException: " + ioe.getMessage());
		}
	}
}
