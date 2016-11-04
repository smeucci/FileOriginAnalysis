package videoclass;

import java.util.List;

import static utils.Utils.*;

public class VideoClass {

	private String name;
	private String listUrl;
	private List<String> xmlfiles;
	
	public VideoClass(String name, String listUrl) {
		this.name = name;
		this.listUrl = listUrl;
		this.xmlfiles = parseClassFilesList(listUrl);
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