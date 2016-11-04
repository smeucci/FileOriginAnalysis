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
import java.util.List;
import java.util.stream.Stream;

import org.jdom2.Document;

public class Utils {

	public static List<String> parseClassFilesList(String url) {
		//TODO if files in the list are not xml but mp4, convert them
		List<String> lines = new ArrayList<String>();
		try (Stream<String> stream = Files.lines(Paths.get(url))) {
	        stream.forEach(str -> lines.add(str));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		return lines;
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
		System.out.println("Saved '" + fileSaver.getDestinationPath() + "'");
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();
	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
}