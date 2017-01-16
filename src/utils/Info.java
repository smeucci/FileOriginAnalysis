package utils;

public class Info {

	String manufacturer;
	String model;
	String os;
	String version;
	String title;
	String pathtofile;
	String pathtoxml;
	String pathtoinfo;
		
	public String getManufacturer() {
		return this.manufacturer;
	}
	
	public void setManufacturer(String value) {
		this.manufacturer = value;
	}
	
	public String getModel() {
		return this.model;
	}
	
	public void setModel(String value) {
		this.model = value;
	}
	
	public String getOS() {
		return this.os;
	}
	
	public void setOS(String value) {
		this.os = value;
	}
	
	public String getVersion() {
		return this.version;
	}
	
	public void setVersion(String value) {
		this.version = value;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public void setTitle(String value) {
		this.title = value;
	}
	
	public String getPathToFile() {
		return this.pathtofile;
	}
	
	public void setPathToFile(String value) {
		this.pathtofile = value;
	}
	
	public String getPathToXml() {
		return this.pathtoxml;
	}
	
	public void setPathToXml(String value) {
		this.pathtoxml = value;
	}
	
	public String getPathToInfo() {
		return this.pathtoinfo;
	}
	
	public void setPathToInfo(String value) {
		this.pathtoinfo = value;
	}
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("{");
		result.append("manufacturer=").append(this.manufacturer);
		result.append(";");
		result.append("model=").append(this.model);
		result.append(";");
		result.append("os=").append(this.os);
		result.append(";");
		result.append("version=").append(this.version);
		result.append(";");
		result.append("title=").append(this.title);
		result.append(";");
		result.append("pathtofile=").append(this.pathtofile);
		result.append(";");
		result.append("pathtoxml=").append(this.pathtoxml);
		result.append(";");
		result.append("pathtoinfo=").append(this.pathtoinfo);
		result.append("}");
		return result.toString();	
	}
	
}
