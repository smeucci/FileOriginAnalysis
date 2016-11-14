package testing;

import static com.vftlite.core.VFT.*;
import static utils.Utils.*;

import java.util.Iterator;

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
		System.out.println("Likelihood of " + this.video + " for class A: \n" + likelihood);
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
			for (Field nodeField: node.getFieldsList()) {
				String nodeFieldName = nodeField.getName();
				String nodeFieldValue = nodeField.getValue();
				
				if (unusedField(nodeFieldName) == false) {
					if (verbose) System.out.println(nodeFieldName);
					String configFieldValuesA = configA.getFieldByName(nodeFieldName).getValue();
					String configFieldValuesB = configB.getFieldByName(nodeFieldName).getValue();
					
					String[] splitsA = configFieldValuesA.split("\\;");
					String[] valuesA = splitsA[0].replaceAll("\\[|\\]", "").split("\\,");
					double[] weightsA = parseWeights(splitsA[1]);
					
					String[] splitsB = configFieldValuesB.split("\\;");
					String[] valuesB = splitsB[0].replaceAll("\\[|\\]", "").split("\\,");
					double[] weightsB = parseWeights(splitsB[1]);
					
					double numerator = getValueWeight(nodeFieldValue, valuesA, weightsA);
					double denominator = getValueWeight(nodeFieldValue, valuesB, weightsB);
					likelihood(numerator, denominator);
				}
			}
		} else {
			if (verbose) System.out.println("NEW: " + node.getName());
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
			likelihood = likelihood * (numerator / (1 / (double) this.numB));
			if (verbose) System.out.println(likelihood + " - " + (numerator / (1 / (double) this.numB)) + " - " + 1);
		} else if (numerator == 0 && denominator != 0) {
			likelihood = likelihood * ((1 / (double) this.numA) / denominator);
			if (verbose) System.out.println(likelihood + " - " + ((1 / (double) this.numA) / denominator) + " - " + 2);
		} else if (numerator == 0 && denominator == 0) {
			likelihood = likelihood * (1 / (double) this.numA);
			if (verbose) System.out.println(likelihood + " - " + (1 / (double) this.numA) + " - " + 34);
		} else {
			likelihood = likelihood * (numerator / denominator);
			if (verbose) System.out.println(likelihood + " - " + (numerator / denominator) + " - " + 0);
		}
	}
	
}