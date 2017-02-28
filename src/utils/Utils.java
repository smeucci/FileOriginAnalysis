package utils;

import static com.vftlite.core.VFT.*;

import com.vftlite.io.FileReaderSaver;
import com.vftlite.tree.Node;
import com.vftlite.tree.Tree;
import com.vftlite.tree.Field;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.jdom2.Document;
import org.jdom2.Element;
import org.json.*;

public class Utils {

	private static Logger logger = new Logger();
	
	public static List<String> parseJSONClassFilesList(String url) throws Exception {
		String text = new String(Files.readAllBytes(Paths.get(url)), StandardCharsets.UTF_8);
		JSONObject json = new JSONObject(text);
		JSONArray list = (JSONArray) json.get("list");
		Iterator<?> itr = list.iterator();
		List<String> videos = new ArrayList<String>();
		while (itr.hasNext()) {
			JSONObject video = (JSONObject) itr.next();
			String filename = video.getString("video");
			videos.add(filename);
		}
		return videos;
	}
	
	public static List<String> parseClassFilesList(String url) {
		//TODO if files in the list are not xml but mp4, convert them
		List<String> lines = new ArrayList<String>();
		try (Stream<String> stream = Files.lines(Paths.get(url))) {
	        stream.forEach(str -> lines.add(str));
		} catch (Exception e) {
			logger.handleException(e);
		}
		return lines;
	}
	
	public static Map<String, String> parseTestFilesList(String url) {
		Map<String, String> urls = new HashMap<String, String>();
		List<String> lines = new ArrayList<String>();
		try (Stream<String> stream = Files.lines(Paths.get(url))) {
	        stream.forEach(str -> lines.add(str));
	        for (String line: lines) {
	        	String[] splits = line.split(",");
	        	urls.put(splits[0], splits[1]);
	        }    
		} catch (Exception e) {
			logger.handleException(e);
		}
		return urls;
	}
	
	public static Tree createRootTree() {
		List<Field> fields = new ArrayList<Field>();
		fields.add(new Field("modelName", "phoneBrandName"));		
		return new Node(0, "root", 0, null, fields);
	}
	
	public static double[] parseWeights(String str) {
		String[] splits = str.replaceAll("\\[|\\]", "").split("\\,");
		double[] weights = new double[splits.length];
		for (int i = 0; i < splits.length; i++) {
			weights[i] = Double.parseDouble(splits[i]);
		}
		return weights;
	}
	
	public static void saveTree(Tree tree, String name, String outputPath) throws Exception {
		Document document = buildXMLDocumentFromTree(tree);
		FileReaderSaver fileSaver = new FileReaderSaver(name, outputPath);
		fileSaver.saveOnFile(document);
		logger.handleSaveTree(fileSaver.getDestinationPath());
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();
	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	public static Boolean unusedField(String field) {
		if (field.matches("stuff|creationTime|modificationTime|size|duration|entryCount|sampleCount")) {
			return true;
		} else {
			return false;
		}
	}
	
	public static Boolean unusedTag(Tree tree) {
		if (tree.getName().contains("xyz")) return true;
		if (tree.getName().contains("unkn-5")) return true;
		if (tree.getName().contains("udta") && tree.getNumChildren() == 1 && tree.getChildren().get(0).getName().contains("xyz")) return true;
		return false;
	}
	
	public static List<Pair<String, Double>> parseValueWeightCouples(String str) {
		String[] splits = str.split("\\;");
		String[] values = splits[0].replaceAll("\\[|\\]", "").split("\\,");
		double[] weights = parseWeights(splits[1]);
		
		List<Pair<String, Double>> couples = new ArrayList<Pair<String,Double>>();
		for (int i = 0; i < values.length; i++) {
			couples.add(new Pair<String, Double>(values[i], weights[i]));
		}
		return couples;
	}
	
	public static void convert(String datasetPath, String outputPath) throws Exception {
		File datasetFolder = new File(datasetPath);
		if (!datasetFolder.exists() || !datasetFolder.isDirectory()) {
			System.err.println("Could not find the dataset folder at '" + datasetPath + "'");
		} else if (!new File(outputPath).exists() || !new File(outputPath).isDirectory()) {
			System.err.println("Could not find the output folder at '" + outputPath + "'");
		} else {
			System.out.println("Converting the dataset at '" + datasetPath + "'");
			convertDataset(datasetFolder, outputPath, 0);
		}
	}
	
	public static int convertDataset(File folder, String outputPath, int id) throws Exception {
		File[] files = folder.listFiles();
		for (File f: files) {
			if (f.isFile() && !f.getName().startsWith(".")) {
				id = createInfo(f.getAbsolutePath(), f.getParentFile().getParent(), outputPath, id);
			} else if (f.isDirectory() && !f.getName().toLowerCase().endsWith(".not") && !f.getName().toLowerCase().endsWith("youtube")) {
				File subfolder = new File(f.getAbsolutePath());
				id = convertDataset(subfolder, outputPath, id);
			}
		}
		return id;
	}
	
	public static int createInfo(String filename, String type, String output, int id) throws Exception {
		String[] splits = type.split("/");
		String[] codes = decode(splits[splits.length-1]);
		
		Element track = new Element("track");
		Element device = new Element("device");
		track.addContent(device);
		
		
		Element deviceID = new Element("deviceID");
		deviceID.addContent("" + id + "");
		Element manufacturer = new Element("manufacturer");
		manufacturer.addContent(codes[0]);
		Element model = new Element("model");
		model.addContent(codes[1]);
		device.addContent(deviceID);
		device.addContent(manufacturer);
		device.addContent(model);
		
		Element title = new Element("title");
		title.addContent(filename);
		track.addContent(title);
		
		FileReaderSaver fileSaver = new FileReaderSaver("" + id + "", output);
		fileSaver.saveOnFile(new Document(track));
		
		File source = new File(filename);
		String[] spl = filename.split("\\.");
		String ext = spl[spl.length-1];
		File dest = new File(output + "/" + id + "." + ext);
		try {
			Files.copy(source.toPath(), dest.toPath());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println(codes[0] + " " + codes[1]);
		System.out.println("-- " + filename);
		id++;
		return id;
	}
	
	public static String[] decode(String name) {
		
		String[] codes = {"", ""};
		
		switch (name) {
		case "galaxys3_dasara":
			codes[0] = "samsung";
			codes[1] = "galaxys3";
			break;
		case "galaxys3mini_giulia":
			codes[0] = "samsung";
			codes[1] = "galaxys3mini";
			break;
		case "galaxys3mini_ilaria":
			codes[0] = "samsung";
			codes[1] = "galaxys3mini";
			break;
		case "galaxys4mini_alessia":
			codes[0] = "samsung";
			codes[1] = "galaxys4mini";
			break;
		case "galaxytab3_marco":
			codes[0] = "samsung";
			codes[1] = "galaxytab3";
			break;
		case "galaxytaba_ilaria":
			codes[0] = "samsung";
			codes[1] = "galaxytaba";
			break;
		case "galaxytrendplus_davide":
			codes[0] = "samsung";
			codes[1] = "galaxytrendplus";
			break;
		case "huaweig6_rossana":
			codes[0] = "huawei";
			codes[1] = "g6";
			break;
		case "ipad2_giulia":
			codes[0] = "apple";
			codes[1] = "ipad2";
			break;
		case "ipadmini_marco":
			codes[0] = "apple";
			codes[1] = "ipadmini";
			break;
		case "iphone4s_davide":
			codes[0] = "apple";
			codes[1] = "iphone4s";
			break;
		case "iphone5c_bianca":
			codes[0] = "apple";
			codes[1] = "iphone5c";
			break;
		case "iphone5_giovanni":
			codes[0] = "apple";
			codes[1] = "iphone5";
			break;
		case "iphone6_marco":
			codes[0] = "apple";
			codes[1] = "iphone6";
			break;
		}
		
		return codes;
	}
	
}