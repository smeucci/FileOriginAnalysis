package testing;

import static com.vftlite.core.VFT.*;
import static utils.Utils.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import utils.Pair;
import static java.lang.Math.log;
import static java.lang.Math.pow;

import com.vftlite.tree.Field;
import com.vftlite.tree.Tree;

public class Test {

	private String video;
	private String config_A;
	private String config_B;
	private Map<String, String> videos;
	private int numA;
	private int numB;
	private boolean verbose;
	private static double likelihood = 1;
	
	public Test(String url, String config_A, String config_B, boolean verbose) {
		try {
			if (url.endsWith("txt")) {
				this.videos = parseTestFilesList(url);
				this.verbose = false;
			} else {
				this.video = url;
				this.verbose = verbose;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
			System.err.println("The parameter url should be a path to a xml file or a txt file containing a list of xml files to test."
					+ "(path and class comma separated).");
		}
		this.config_A = config_A;
		this.config_B = config_B;
	}
	
	public void test() throws Exception {
		run();
		System.out.println("# Is video '" + this.video + "' of class A?");
		System.out.println("# Likelihood: " + likelihood + ", Loglikelihood: " + log(likelihood));
	}
	
	public void batchTest() throws Exception {
		List<Pair<String>> labels = new ArrayList<Pair<String>>();
		for (Map.Entry<String, String> entry : this.videos.entrySet()) {
			this.video = entry.getKey();
			String label = entry.getValue();
			run();
			String predictedLabel = (likelihood > 1) ? "A" : "B";

			System.out.println("# Test video '" + this.video + "':");
			System.out.println(" - predicted class: " + predictedLabel + "\n"
							  + " - true class: " + label + "\n"
							  + " - likelihood: " + likelihood + "\n"
							  + " - Loglikelihood: " + log(likelihood));
			
			labels.add(new Pair<String>(label, predictedLabel));	
			likelihood = 1;
		}
		accuracy(labels);
	}
	
	protected void accuracy(List<Pair<String>> labels) {
		double correctlyPredicted = 0;
		for (Pair<String> pair: labels) {
			if (pair.getKey().equals(pair.getValue())) {
				correctlyPredicted++;
			}
		}
		System.out.println("Accuracy: " + correctlyPredicted + "/" + labels.size() + ", " 
							+ round(100 * correctlyPredicted / labels.size(), 3) + " %");
	}
	
	protected void run() throws Exception {
		Tree tree = buildTreeFromXMLFile(this.video);
		Tree configA = buildTreeFromXMLFile(this.config_A);
		Tree configB = buildTreeFromXMLFile(this.config_B);
		
		this.numA = Integer.parseInt(configA.getFieldValue("numOfVideos"));
		this.numB = Integer.parseInt(configB.getFieldValue("numOfVideos"));
		
		computeLikelihood(tree, configA, configB);
	}
	
	protected void computeLikelihood(Tree tree, Tree configA, Tree configB) {
		if (tree.isLeaf() == false) {
			Iterator<Tree> treeIterator = tree.iterator();
			while (treeIterator.hasNext()) {
				Tree treeChild = treeIterator.next();
				Tree toCheckA = getCorrespondingChildTree(treeChild, configA);
				Tree toCheckB = getCorrespondingChildTree(treeChild, configB);
				
				updateLikelihood(treeChild, toCheckA, toCheckB);
				computeLikelihood(treeChild, toCheckA, toCheckB);
			}
		}
	}
	
	protected void updateLikelihood(Tree node, Tree configA, Tree configB) {
		if (configA != null && configB != null) {
			if (verbose) System.out.println("## BEGIN " + node.getName().toUpperCase() + " ##");
			List<Double> ratios = new ArrayList<Double>();
			for (Field nodeField: node.getFieldsList()) {
				String nodeFieldName = nodeField.getName();
				String nodeFieldValue = nodeField.getValue();
				
				if (unusedField(nodeFieldName) == false) {
					if (verbose) System.out.println("- " + nodeFieldName);					
					Field configFieldA = configA.getFieldByName(nodeFieldName);
					Field configFieldB = configB.getFieldByName(nodeFieldName);
					
					if (configFieldA != null && configFieldB != null) {
						String configFieldValuesA = configFieldA.getValue();
						String configFieldValuesB = configFieldB.getValue();
						
						String[] splitsA = configFieldValuesA.split("\\;");
						String[] valuesA = splitsA[0].replaceAll("\\[|\\]", "").split("\\,");
						double[] weightsA = parseWeights(splitsA[1]);
						
						String[] splitsB = configFieldValuesB.split("\\;");
						String[] valuesB = splitsB[0].replaceAll("\\[|\\]", "").split("\\,");
						double[] weightsB = parseWeights(splitsB[1]);
						
						double numerator = getValueWeight(nodeFieldValue, valuesA, weightsA);
						double denominator = getValueWeight(nodeFieldValue, valuesB, weightsB);						
						ratios.add(ratio(numerator, denominator));
					} else {
						if (verbose) System.out.println("- NEW " + nodeFieldName);
						ratios.add(ratio(0, 0));
					}
				}
			}
			
			double entropy = entropy(ratios);
			double factor = multiplyRatios(ratios, entropy);
			likelihood(factor);
			if (verbose) System.out.println("## END " + node.getName().toUpperCase() + " - entropy: " + entropy 
					+ ", factor: " + factor + ", likelihood: " + likelihood + " ##");
		} else {
			if (verbose) System.out.println("## NEW: " + node.getName() + " ##");
			likelihood(ratio(0, 0));
		}
	}
	
	protected double getValueWeight(String value, String[] values, double[] weights) {
		for (int i = 0; i < values.length; i++) {
			if (value.equals(values[i])) {
				return weights[i];
			}
		}
		return 0;
	}
	
	protected double multiplyRatios(List<Double> ratios, double entropy) {
		double mult = 1;
		for (double ratio: ratios) {
			mult = mult * ratio;
		}
		double exp = (entropy == 0) ? (1 / ratios.size()) : 1;
		return pow(mult, exp); //TODO optimize
	}
	
	protected void likelihood(double value) {
		likelihood = likelihood * value;
	}
	
	protected double ratio(double numerator, double denominator) {
		double ratio;
		if (denominator == 0 && numerator != 0) {
			ratio = round((numerator / (1 / (double) this.numB)), 3);
			if (verbose) printDebug(ratio, numerator, denominator, 1);
		} else if (numerator == 0 && denominator != 0) {
			ratio = round(((1 / (double) this.numA) / denominator), 3);
			if (verbose) printDebug(ratio, numerator, denominator, 2);
		} else if (numerator == 0 && denominator == 0) {
			ratio = round((1 / (double) this.numA), 3);
			if (verbose) printDebug(ratio, numerator, denominator, 34);
		} else {
			ratio = round((numerator / denominator), 3);
			if (verbose) printDebug(ratio, numerator, denominator, 0);
		}
		return ratio;
	}
	
	protected double entropy(List<Double> list) {
		Map<Double, Long> counts =
			    list.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));
		double entropy = 0;
		for (double count: counts.values()) {
			double x = count / list.size();
			entropy = entropy - (x * log(x));
		}
		return entropy;
	}
	
}