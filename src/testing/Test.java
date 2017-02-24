package testing;

import static com.vftlite.core.VFT.*;
import static utils.Utils.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import likelihood.Likelihood;
import likelihood.StandardLikelihood;
import utils.Logger;
import utils.Pair;

import com.vftlite.tree.Tree;

public class Test {

	private String video;
	private String config_A;
	private String config_B;
	private Map<String, String> videos;
	private int numA;
	private int numB;
	private boolean verbose;
	private Likelihood LK;
	private static Logger logger = new Logger();
	
	public Test(String url, String config_A, String config_B, boolean verbose) {
		try {
			if (url.endsWith("txt")) { this.videos = parseTestFilesList(url); } 
			else { this.video = url; }
		} catch (Exception e) {
			logger.handleException(e);
		}
		this.config_A = config_A;
		this.config_B = config_B;
		this.verbose = verbose;
	}
	
	protected void run() throws Exception {
		Tree tree = buildTreeFromXMLFile(this.video);
		
		Pair<Tree, Tree> config = new Pair<Tree, Tree>();
		config.setFirst(buildTreeFromXMLFile(this.config_A));
		config.setSecond(buildTreeFromXMLFile(this.config_B));
		
		this.numA = Integer.parseInt(config.getFirst().getFieldValue("numOfVideos"));
		this.numB = Integer.parseInt(config.getSecond().getFieldValue("numOfVideos"));
		this.LK = new StandardLikelihood(numA, numB, verbose);
		
		LK.computeLikelihood(tree, config);
	}
	
	public void test() throws Exception {
		run();
		logger.handleTestResult(this.video, LK.getLikelihood());
	}
	
	public void batchTest() throws Exception {
		List<Pair<String, String>> predictions = new ArrayList<Pair<String, String>>();
		for (Map.Entry<String, String> entry : this.videos.entrySet()) {
			this.video = entry.getKey();
			String label = entry.getValue();
			run();
			String predictedLabel = (LK.getLikelihood() > 1) ? "A" : "B";

			logger.handleTestResult(this.video, label, predictedLabel, LK.getLikelihood());
			
			predictions.add(new Pair<String, String>(label, predictedLabel));	
			LK.resetLikelihood();
		}
		accuracy(predictions);
	}
	
	protected void accuracy(List<Pair<String, String>> predictions) {
		int correctlyPredicted = 0;
		for (Pair<String, String> prediction: predictions) {
			if (prediction.getFirst().equals(prediction.getSecond())) {
				correctlyPredicted++;
			}
		}
		logger.handleAccuracy(correctlyPredicted, predictions.size());
	}
	
}