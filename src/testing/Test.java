package testing;

import static com.vftlite.core.VFT.*;
import static utils.Utils.parseWeights;

import java.util.Iterator;

import com.vftlite.tree.Field;
import com.vftlite.tree.Tree;

public class Test {

	private String video;
	private String config_A;
	private String config_B;
	private int numA;
	private int numB;
	private static double likelihood = 1;
	
	public Test(String video, String config_A, String config_B, int numA, int numB) {
		this.video = video;
		this.config_A = config_A;
		this.config_B = config_B;
		this.numA = numA;
		this.numB = numB;
	}
	
	public void test() throws Exception {
		
		Tree tree = buildTreeFromXMLFile(this.video);
		Tree configA = buildTreeFromXMLFile(this.config_A);
		Tree configB = buildTreeFromXMLFile(this.config_B);
		
		computeLikelihood(tree, configA, configB);
		System.out.println("Likelihood of " + this.video + " for class A: " + likelihood);
		
	}
	
	protected void computeLikelihood(Tree tree, Tree configA, Tree configB) {
		if (tree.getNumChildren() > 0) {
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
				
				if (!nodeFieldName.equals("stuff")) {
					//System.out.println(nodeFieldName);
					String configFieldValuesA = configA.getFieldByName(nodeFieldName).getValue();
					String configFieldValuesB = configB.getFieldByName(nodeFieldName).getValue();
					
					String[] splitsA = configFieldValuesA.split("\\;");
					String[] valuesA = splitsA[0].replaceAll("\\[|\\]", "").split("\\,");
					double[] weightsA = parseWeights(splitsA[1]);
					
					String[] splitsB = configFieldValuesB.split("\\;");
					//String[] valuesB = splitsB[0].replaceAll("\\[|\\]", "").split("\\,");
					double[] weightsB = parseWeights(splitsB[1]);
					
					double numerator = 0;
					double denominator = 0;
					for (int i = 0; i < valuesA.length; i++) {
						if (nodeFieldValue.equals(valuesA[i])) {
							numerator = weightsA[i];
							denominator = weightsB[i];
						}	
					}
					
					if (denominator == 0 && numerator != 0) {
						likelihood = likelihood * (numerator / (1 / (double) this.numB));
						//System.out.println(likelihood + " - " + (numerator / (1 / (double) this.numB)));
					} else if (numerator == 0 && denominator != 0) {
						likelihood = likelihood * ((1 / (double) this.numA) / denominator);
						//System.out.println(likelihood + " - " + ((1 / (double) this.numA) / denominator));
					} else if (numerator == 0 && denominator == 0) {
						likelihood = likelihood * (1 / (double) this.numA);
						//System.out.println(likelihood + " - " + (1 / (double) this.numA));
					} else {
						likelihood = likelihood * (numerator / denominator);
						//System.out.println(likelihood + " - " + (numerator / denominator));
					}
				}
			}
			
		} else {
			likelihood = likelihood * (1 / (double) this.numA);
			//System.out.println(likelihood + " - " + (1 / (double) this.numA));
		}
	}
	
}