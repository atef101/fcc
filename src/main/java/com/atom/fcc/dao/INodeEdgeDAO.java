package com.atom.fcc.dao;

import java.util.List;

import com.atom.fcc.model.Edge;
import com.atom.fcc.model.Vertex;

import java.io.File;

public interface INodeEdgeDAO {

	public void loadModel(File file);
	public int getPriceDirectConnection(String source, String target);
	public Vertex getNodebyName(String name);
	public Edge getEdgebySourceDestination(Vertex source, Vertex target);
	public String getCheapestConnection(String source, String target);
	public int getAllConnectionwithStepCondition(String source, String target, int condition, String type);
	public List<String> getAllConnectionwithCostCondition(String source, String target, int condition, String type);
	
}
