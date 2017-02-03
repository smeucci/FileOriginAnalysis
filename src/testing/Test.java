package testing;

import static com.vftlite.core.VFT.*;
import static utils.Utils.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import utils.Logger;
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
	private static Logger logger = new Logger();
	
	public Test(String url, String config_A, String config_B, boolean verbose) {
		try {
			if (url.endsWith("txt")) {
				this.videos = parseTestFilesList(url);
			} else {
				this.video = url;
			}
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
		
		computeLikelihood(tree, config);
	}
	
	public void test() throws Exception {
		run();
		logger.handleTestResult(this.video, likelihood);
	}
	
	public void batchTest() throws Exception {
		List<Pair<String, String>> predictions = new ArrayList<Pair<String, String>>();
		for (Map.Entry<String, String> entry : this.videos.entrySet()) {
			this.video = entry.getKey();
			String label = entry.getValue();
			run();
			String predictedLabel = (likelihood > 1) ? "A" : "B";

			logger.handleTestResult(this.video, label, predictedLabel, likelihood);
			
			predictions.add(new Pair<String, String>(label, predictedLabel));	
			likelihood = 1;
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
	
	protected void computeLikelihood(Tree tree, Pair<Tree, Tree> config) {
		if (tree.isLeaf() == false) {
			Iterator<Tree> treeIterator = tree.iterator();
			while (treeIterator.hasNext()) {
				Tree treeChild = treeIterator.next();

				if (unusedTag(treeChild) == false) {
					Pair<Tree, Tree> toCheck = new Pair<Tree, Tree>();
					toCheck.setFirst(getCorrespondingChildTree(treeChild, config.getFirst()));
					toCheck.setSecond(getCorrespondingChildTree(treeChild, config.getSecond()));
					
					updateLikelihood(treeChild, toCheck);
					computeLikelihood(treeChild, toCheck);
				}
			}
		}
	}
	
	protected void updateLikelihood(Tree node, Pair<Tree, Tree> config) {
		double entropy, factor;
		if (config.isNull() == false) {
			if (verbose) logger.handleBeginTag(node.getName());
			List<Double> ratios = computeRatios(node.getFieldsList(), config);		
			entropy = entropy(ratios);
			factor = attributesLikelihood(ratios);
		} else {	
			if (verbose) logger.handleNewTag(node.getName());
			entropy = 0;
			factor = ratio(0, 0);
		}
		likelihood(factor);
		if (verbose) logger.handleEndTag(node.getName(), entropy, factor, likelihood);
	}
	
	protected List<Double> computeRatios(List<Field> fields, Pair<Tree, Tree> config) {
		List<Double> ratios = new ArrayList<Double>();
		for (Field nodeField: fields) {
			if (unusedField(nodeField.getName()) == false) {
				double ratio = computeRatio(nodeField.getName(), nodeField.getValue(), config);
				ratios.add(ratio);
			}
		}
		return ratios;
	}
	
	protected double computeRatio(String nodeFieldName, String nodeFieldValue, Pair<Tree, Tree> config) {		
		if (verbose) logger.handleField(nodeFieldName);
		Pair<Field, Field> configFields = new Pair<Field, Field>();
		configFields.setFirst(config.getFirst().getFieldByName(nodeFieldName));
		configFields.setSecond(config.getSecond().getFieldByName(nodeFieldName));
		
		if (configFields.isNull() == false) {
			String configFieldValuesA = configFields.getFirst().getValue();
			String configFieldValuesB = configFields.getSecond().getValue();
			
			List<Pair<String, Double>> couplesA = parseValueWeightCouples(configFieldValuesA);
			List<Pair<String, Double>> couplesB = parseValueWeightCouples(configFieldValuesB);
			
			double numerator = getValueWeight(nodeFieldValue, couplesA);
			double denominator = getValueWeight(nodeFieldValue, couplesB);						
			return ratio(numerator, denominator);
		} else {
			if (verbose) logger.handleNewField(nodeFieldName);
			return ratio(0, 0);
		}
	}
	
	protected double getValueWeight(String value, List<Pair<String, Double>> couples) {
		for (Pair<String, Double> couple: couples) {
			if (value.equals(couple.getFirst())) {
				return couple.getSecond();
			}
		}
		return 0;
	}
	
	protected double attributesLikelihood(List<Double> ratios) {
		double mult = 1;
		List<Double> exponents = exponents(ratios);
		for (int i = 0; i < ratios.size(); i++) {
			double ratio = ratios.get(i);
			double exp = exponents.get(i);
			mult = mult * pow(ratio, exp);
		}
		return mult;
	}
	
	protected void likelihood(double value) {
		likelihood = likelihood * value;
	}
	
	protected double ratio(double numerator, double denominator) {
		double ratio;
		if (denominator == 0 && numerator != 0) {
			ratio = round((numerator / (1 / ((double) this.numA + 1))), 4);
			if (verbose) logger.handleDebug(ratio, numerator, denominator, 1);
		} else if (numerator == 0 && denominator != 0) {
			ratio = round(((1 / ((double) this.numB + 1)) / denominator), 4);
			if (verbose) logger.handleDebug(ratio, numerator, denominator, 2);
		} else if (numerator == 0 && denominator == 0) {
			ratio = round((1 / ((double) this.numA + 1)), 4); //TODO 1?
			if (verbose) logger.handleDebug(ratio, numerator, denominator, 34);
		} else {
			ratio = round((numerator / denominator), 4);
			if (verbose) logger.handleDebug(ratio, numerator, denominator, 0);
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
	
	protected List<Double> exponents(List<Double> ratios) {
		List<Double> entropies = entropies(ratios);
		List<Double> exponents = new ArrayList<Double>();
		double n = ratios.size();
		for (double entropy: entropies) {
			exponents.add(((n - 1)*entropy + 1) / n);
		}
		return exponents;
	}
	
	protected List<Double> entropies(List<Double> list) {
		Map<Double, Long> occurrences =
			    list.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));		
		List<Double> entropies = new ArrayList<Double>();
		for (double element: list) {
			double occurrence = occurrences.get(element);
			double n = list.size();
			double x = occurrence / n;
			double norm = (n == 1) ? 1 : n / log(n);
			entropies.add(-1*(norm)*(x * log(x)));
		}
		return entropies;
	}
	
}