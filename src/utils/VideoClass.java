package utils;

import java.util.List;

import static utils.Utils.*;

public class VideoClass {

	private String name;
	private String listUrl;
	private List<String> xmlfiles;
	
	public VideoClass(String name, String listUrl) throws Exception {
		this.name = name;
		this.listUrl = listUrl;
		if (listUrl.endsWith(".txt")) {
			this.xmlfiles = parseClassFilesList(listUrl);
		} else if (listUrl.endsWith(".json")) {
			this.xmlfiles = parseJSONClassFilesList(listUrl);
		}
	}
	
	public VideoClass(String name, List<String> xmlfiles) {
		this.name = name;
		this.listUrl = null;
		this.xmlfiles = xmlfiles;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getListUrl() {
		return this.listUrl;
	}
	
	public void setListUrl(String listUrl) {
		this.listUrl = listUrl;
	}
	
	public List<String> getXmlFiles() {
		return this.xmlfiles;
	}
	
	public int getNumXmlFiles() {
		return this.xmlfiles.size();
	}
}