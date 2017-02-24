package likelihood;

import utils.Pair;

import com.vftlite.tree.Tree;

public interface Likelihood {
	
	public void computeLikelihood (Tree tree, Pair<Tree, Tree> config);

	public void resetLikelihood();
	
	public double getLikelihood();
	
}
