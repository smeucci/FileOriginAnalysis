package training;

import java.util.Iterator;

import com.vftlite.tree.Field;
import com.vftlite.tree.Tree;

import utils.VideoClass;
import static com.vftlite.core.VFT.*;
import static utils.Utils.*;

public class Train {

	private VideoClass classA;
	private VideoClass classB;
	private String outputPath;
	private boolean withAttributes;
	
	public Train(VideoClass classA, VideoClass classB, String outputPath, boolean withAttributes) {
		this.classA = classA;
		this.classB = classB;
		this.outputPath = outputPath;
		this.withAttributes = withAttributes;
	}
	
	public void train() throws Exception {
		
		Tree mergeA = createClassConfig(this.classA, this.withAttributes);
		Tree mergeB = createClassConfig(this.classB, this.withAttributes);
		
		saveTree(mergeA, "merge" + this.classA.getName(), this.outputPath);
		saveTree(mergeB, "merge" + this.classB.getName(), this.outputPath);
		
		Tree configA = mergeClassConfig(mergeA, mergeB);
		Tree configB = configA.cloneAll(); //TODO why
		saveTree(configA, "config", this.outputPath);
		
		int i = 0;
		for (String file: this.classA.getXmlFiles()) {
			computeWeights(configA, buildTreeFromXMLFile(file), this.classA.getNumXmlFiles());
			i++;
		}
		configA.addField("numOfVideos", String.valueOf(i));
		saveTree(configA, "configA-w", this.outputPath);
		
		int j = 0;
		for (String file: this.classB.getXmlFiles()) {
			computeWeights(configB, buildTreeFromXMLFile(file), this.classB.getNumXmlFiles());
			j++;
		}
		configB.addField("numOfVideos", String.valueOf(j));
		saveTree(configB, "configB-w", this.outputPath);
		
	}
	
	protected Tree createClassConfig(VideoClass _class, boolean withAttributes) throws Exception {
		Tree config = createRootTree();
		for (String file: _class.getXmlFiles()) {
			Tree tree = buildTreeFromXMLFile(file);
			mergeTree(config, tree, withAttributes);	
		}	
		return config;
	}
	
	protected Tree mergeClassConfig(Tree a, Tree b) throws Exception {
		Tree config = createRootTree();
		mergeTree(config, a, this.withAttributes);
		mergeTree(config, b, this.withAttributes);
		return config;
	}
	
	protected void computeWeights(Tree config, Tree tree, int numOfVideos) {
		if (config.isLeaf() == false) {
			Iterator<Tree> configIterator = config.iterator();
			while (configIterator.hasNext()) {
				Tree configChild = configIterator.next();				
				Tree toCheck = getCorrespondingChildTree(configChild, tree);
				
				if (toCheck != null) {
					updateWeights(configChild, toCheck, numOfVideos);
					computeWeights(configChild, toCheck, numOfVideos);
				}
			}
		}
	}
	
	protected void updateWeights(Tree config, Tree node, int numOfVideos) {
		for (Field nodeField: node.getFieldsList()) {
			
			String nodeFieldName = nodeField.getName();
			String nodeFieldValue = nodeField.getValue();
			
			if (!nodeFieldName.equals("stuff")) { //TODO change to unusedField (also in vft-lite)
				
				Field configField = config.getFieldByName(nodeFieldName);
				String configFieldValue = configField.getValue();
				
				String[] splits = configFieldValue.split("\\;");
				String[] values = splits[0].replaceAll("\\[|\\]", "").split("\\,");
				double[] weights = parseWeights(splits[1]);
				
				for (int i = 0; i < values.length; i++) {
					if (values[i].equals(nodeFieldValue)) {
						weights[i] = round((weights[i]) + (1 / (double) numOfVideos), 4); //TODO precision issue, es. 1/33
					}
				}
				
				String result = updateFieldValue(values, weights);
				configField.setvalue(result);
			}
		}
	}
	
	protected String updateFieldValue(String[] values, double[] weights) {
		StringBuffer result = new StringBuffer();
		result.append("[");
		for (int i = 0; i < values.length; i++) {
			result.append(values[i]);
			String comma = (i == values.length - 1) ? "" : ",";
			result.append(comma);
		}
		result.append("];[");
		for (int i = 0; i < weights.length; i++) {
			result.append(weights[i]);
			String comma = (i == weights.length - 1) ? "" : ",";
			result.append(comma);		
		}
		result.append("]");
		return result.toString();
	}
	
}