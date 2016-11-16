package testing;

import static com.vftlite.core.VFT.*;
import static utils.Utils.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.Math.log;

import com.vftlite.tree.Field;
import com.vftlite.tree.Tree;

public class Test {

	private String video;
	private String config_A;
	private String config_B;
	private int numA;
	private int numB;
	private boolean verbose;
	private static double likelihood = 1;
	
	public Test(String video, String config_A, String config_B, boolean verbose) {
		this.video = video;
		this.config_A = config_A;
		this.config_B = config_B;
		this.verbose = verbose;
	}
	
	public void test() throws Exception {
		Tree tree = buildTreeFromXMLFile(this.video);
		Tree configA = buildTreeFromXMLFile(this.config_A);
		Tree configB = buildTreeFromXMLFile(this.config_B);
		
		this.numA = Integer.parseInt(configA.getFieldValue("numOfVideos"));
		this.numB = Integer.parseInt(configB.getFieldValue("numOfVideos"));
		
		computeLikelihood(tree, configA, configB);
		System.out.println("# Is video '" + this.video + "' of class A?");
		System.out.println("# Likelihood: " + likelihood + ", Loglikelihood: " + log(likelihood));
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
		//TODO compute entropy
		if (configA != null && configB != null) {
			if (verbose) System.out.println("## " + node.getName() + " ##");
			List<Double> ratios = new ArrayList<Double>();
			for (Field nodeField: node.getFieldsList()) {
				String nodeFieldName = nodeField.getName();
				String nodeFieldValue = nodeField.getValue();
				
				if (unusedField(nodeFieldName) == false) {
					if (verbose) System.out.println(nodeFieldName);					
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
						likelihood(numerator, denominator);
						
						ratios.add(ratio(numerator, denominator));
					} else {
						if (verbose) System.out.println("NEW field " + nodeFieldName);
						likelihood(0, 0);
						ratios.add(ratio(0, 0));
					}
				}
			}
			
			if (verbose) System.out.println("## " + node.getName() + " ENTROPY: " + entropy(ratios) + " ##");
			
		} else {
			if (verbose) System.out.println("## NEW: " + node.getName() + " ##");
			likelihood(0, 0);
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
	
	protected void likelihood(double numerator, double denominator) {
		if (denominator == 0 && numerator != 0) {
			double ratio = round((numerator / (1 / (double) this.numB)), 3);
			if (verbose) printDebug(likelihood, ratio, numerator, denominator, 1);
			likelihood = likelihood * ratio;
		} else if (numerator == 0 && denominator != 0) {
			double ratio = round(((1 / (double) this.numA) / denominator), 3);
			if (verbose) printDebug(likelihood, ratio, numerator, denominator, 2);
			likelihood = likelihood * ratio;
		} else if (numerator == 0 && denominator == 0) {
			double ratio = round((1 / (double) this.numA), 3);
			if (verbose) printDebug(likelihood, ratio, numerator, denominator, 34);
			likelihood = likelihood * ratio;
		} else {
			double ratio = round((numerator / denominator), 3);
			if (verbose) printDebug(likelihood, ratio, numerator, denominator, 0);
			likelihood = likelihood * ratio;
		}
	}
	
	protected double ratio(double numerator, double denominator) {
		if (denominator == 0 && numerator != 0) {
			return round((numerator / (1 / (double) this.numB)), 3);
		} else if (numerator == 0 && denominator != 0) {
			return round(((1 / (double) this.numA) / denominator), 3);
		} else if (numerator == 0 && denominator == 0) {
			return round((1 / (double) this.numA), 3);
		} else {
			return round((numerator / denominator), 3);
		}
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