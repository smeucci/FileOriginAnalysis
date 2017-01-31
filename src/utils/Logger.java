package utils;

import static java.lang.Math.log;
import static utils.Utils.round;

import org.json.JSONObject;

public class Logger {

	public void handleException(Exception e) {
		e.printStackTrace();
		System.out.println(e.getMessage());
	}
	
	public void handleDebug(double ratio, double numerator, double denominator, int type) {
		System.out.println("ratio: " + ratio + " - wA: " + numerator + " - wB: " + denominator 
				+ " - case: " + type);
	}
	
	public void handleSaveTree(String filename) {
		System.out.println("Saved '" + filename + "'");
	}
	
	public void handleTestResult(String filename, double likelihood) {
		//System.out.println("# Is video '" + filename + "' of class A?");
		//System.out.println("# Likelihood: " + likelihood + ", Loglikelihood: " + log(likelihood));
		JSONObject res = new JSONObject();
		res.put("filename", filename);
		res.put("likelihood", likelihood);
		res.put("loglikelihood", log(likelihood));
		System.out.print(res.toString() + "\n");
	}
	
	public void handleTestResult(String filename, String label, String predictedLabel, double likelihood) {
		System.out.println("# Test video '" + filename + "':");
		System.out.println(" - predicted class: " + predictedLabel + "\n"
						  + " - true class: " + label + "\n"
						  + " - likelihood: " + likelihood + "\n"
						  + " - Loglikelihood: " + log(likelihood));
	}
	
	public void handleAccuracy(int correctlyPredicted, int tot) {
		System.out.println("Accuracy: " + correctlyPredicted + "/" + tot+ ", " 
				+ round(100 * (double) correctlyPredicted / (double) tot, 2) + " %");
	}
	
	public void handleBeginTag(String name) {
		System.out.println("## BEGIN " + name.toUpperCase() + " ##");
	}
	
	public void handleEndTag(String name, double entropy, double factor, double likelihood) {
		System.out.println("## END " + name.toUpperCase() + " - entropy: " + entropy 
				+ ", factor: " + factor + ", likelihood: " + likelihood + " ##");
	}
	
	public void handleField(String name) {
		System.out.println("- " + name);
	}
	
	public void handleNewField(String name) {
		System.out.println("- NEW " + name);
	}
	
	public void handleNewTag(String name) {
		System.out.println("## NEW: " + name.toUpperCase() + " ##");
	}
}
