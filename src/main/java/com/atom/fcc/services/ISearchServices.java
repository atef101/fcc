package com.atom.fcc.services;

import java.io.File;
import java.util.List;


public interface ISearchServices {

	public void executeSearch(File file);
	public List<String> findAP(String type, int cost, String source, String target);
	public String findCC(String key);
	public int findDC(String type, int stepcounts, String source, String target);
	public int findPC(String key);
}
