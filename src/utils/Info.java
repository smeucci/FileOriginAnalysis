package utils;

import org.jdom2.Document;
import org.jdom2.Element;

import com.vftlite.io.FileReaderSaver;

public class Info {

	String deviceID;
	String manufacturer;
	String model;
	String os;
	String version;
	String title;
	String pathtofile;
	String pathtoxml;
	String pathtoinfo;
	
	public Info(String url) throws Exception {
		this.pathtofile = url;	
		this.pathtoxml = url + ".xml";
		this.pathtoinfo = url.replaceAll("\\.mp4|\\.MP4|\\.mov|\\.MOV", ".xml");
		
		FileReaderSaver fileReader = new FileReaderSaver(this.pathtoinfo);
		Document document = fileReader.getDocumentFromXMLFile();
		Element root = document.getRootElement();
		Element title = root.getChild("title");
		this.title = title.getContent(0).getValue();
		Element device = root.getChild("device");
		Element deviceID = device.getChild("deviceID");
		this.deviceID = deviceID.getContent(0).getValue();
		Element manufacturer = device.getChild("manufacturer");
		this.manufacturer = manufacturer.getContent(0).getValue();
		Element model = device.getChild("model");
		this.model = model.getContent(0).getValue();
		try {
			Element os = device.getChild("os");
			Element name = os.getChild("name");
			this.os = name.getContent(0).getValue();
			Element release = os.getChild("release");
			this.version = release.getContent(0).getValue();
		} catch (Exception e) {
			this.os = "null";
			this.version = "null";
		}
		
	}
	
	public String getDeviceID() {
		return this.deviceID;
	}
	
	public void setDeviceID(String value) {
		this.deviceID = value;
	}
		
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
