package utils;

import static com.vftlite.core.VFT.*;

import com.vftlite.io.FileReaderSaver;
import com.vftlite.tree.Node;
import com.vftlite.tree.Tree;
import com.vftlite.tree.Field;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.jdom2.Document;

public class Utils {

	private static Logger logger = new Logger();
	
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
		if (tree.getName().contains("udta") && tree.getNumChildren() == 1 && tree.getChildByName("xyz-4") != null) return true; 
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
	
}