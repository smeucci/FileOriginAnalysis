package utils;

import static tool.VFT.buildXMLDocumentFromTree;
import io.FileReaderSaver;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.jdom2.Document;

import tree.*;

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
	
	public static int[] parseWeights(String str) {
		String[] splits = str.replaceAll("\\[|\\]", "").split("\\,");
		int[] weights = new int[splits.length];
		for (int i = 0; i < splits.length; i++) {
			weights[i] = Integer.parseInt(splits[i]);
		}
		return weights;
	}
	
	public static void saveTree(Tree tree, String name, String outputPath) throws Exception {
		Document document = buildXMLDocumentFromTree(tree);
		FileReaderSaver fileSaver = new FileReaderSaver(name, outputPath);
		fileSaver.saveOnFile(document);
		System.out.println("Saved '" + fileSaver.getDestinationPath() + "'");
	}
	
}