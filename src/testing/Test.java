package testing;

import static com.vftlite.core.VFT.*;

import java.util.Iterator;

import com.vftlite.tree.Tree;

public class Test {

	String video;
	String config_A;
	String config_B;
	
	public Test(String video, String config_A, String config_B) {
		this.video = video;
		this.config_A = config_A;
		this.config_B = config_B;
	}
	
	public void test() throws Exception {
		
		Tree tree = buildTreeFromXMLFile(video);
		Tree configA = buildTreeFromXMLFile(config_A);
		Tree configB = buildTreeFromXMLFile(config_B);
		
		double likelihood = 1;
		computeLikelihood(likelihood, tree, configA, configB);
		
	}
	
	protected void computeLikelihood(double likelihood, Tree tree, Tree configA, Tree configB) {
		if (tree.getNumChildren() > 0) {
			Iterator<Tree> treeIterator = tree.iterator();
			while (treeIterator.hasNext()) {
				Tree treeChild = treeIterator.next();
				Tree toCheckA = getCorrespondingChildTree(treeChild, configA);
				Tree toCheckB = getCorrespondingChildTree(treeChild, configB);
				
				if (toCheckA != null && toCheckB != null) {
					updateLikelihood(likelihood, tree, configA, configB);
					computeLikelihood(likelihood, treeChild, toCheckA, toCheckB);
				}
			}
		}
	}
	
	protected void updateLikelihood(double likelihood, Tree tree, Tree configA, Tree configB) {
		//TODO compute likelihood for each attributes of the tree and update likelihood
	}
	
}