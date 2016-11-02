package training;

import java.util.Iterator;

import com.vftlite.tree.Field;
import com.vftlite.tree.Tree;

import videoclass.VideoClass;
import static com.vftlite.core.VFT.*;
import static utils.Utils.*;
import static com.vftlite.util.Util.*;

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
		
		Tree configA = createClassConfig(this.classA, this.outputPath, this.withAttributes);
		Tree configB = createClassConfig(this.classB, this.outputPath, this.withAttributes);
		
		saveTree(configA, this.classA.getName() + "-config", this.outputPath);
		saveTree(configB, this.classB.getName() + "-config", this.outputPath);
		
		Tree config = mergeClassConfig(configA, configB);
		saveTree(config, "configAB", this.outputPath);
		saveTree(config, "configBA", this.outputPath);
		
		for (String file: this.classA.getXmlFiles()) {
			computeWeights(config, buildTreeFromXMLFile(file), this.classA.getXmlFiles().size());
		}
		
		saveTree(config, "configAB-w", this.outputPath);
		
		Tree config2 = buildTreeFromXMLFile(this.outputPath + "configBA.xml");
		for (String file: this.classB.getXmlFiles()) {
			computeWeights(config2, buildTreeFromXMLFile(file), this.classB.getXmlFiles().size());
		}
		saveTree(config2, "configBA-w", this.outputPath);
		
		System.out.println("DONE");
		
	}
	
	protected Tree createClassConfig(VideoClass _class, String outputPath, boolean withAttributes) throws Exception {
		Tree config = createRootTree();
		for (String file: _class.getXmlFiles()) {
			Tree tree = buildTreeFromXMLFile(file);
			mergeTree(config, tree, withAttributes);	
		}	
		return config;
	}
	
	protected Tree mergeClassConfig(Tree a, Tree b) throws Exception {
		mergeTree(a, b, this.withAttributes);
		return a;
	}
	
	protected void computeWeights(Tree config, Tree tree, int numOfVideos) {
		if (config.getNumChildren() > 0) {
			Iterator<Tree> configIterator = config.iterator();
			while(configIterator.hasNext()) {
				boolean isPresent = false;
				Tree toCheck = null;
				Tree configChild = configIterator.next();
				
				Iterator<Tree> treeIterator = tree.iterator();
				if (treeIterator != null) {
					while(treeIterator.hasNext() && !isPresent) {
						Tree treeChild = treeIterator.next();
						if (configChild.getName().equals(treeChild.getName()) && !treeChild.getName().equals("trak")) {
							isPresent = true;
							toCheck = treeChild;
						} else if (contains(checkTrakType(treeChild), "vide") && contains(checkTrakType(configChild), "vide")) {	
							isPresent = true;
							toCheck = treeChild;	
						} else if (contains(checkTrakType(treeChild), "soun") && contains(checkTrakType(configChild), "soun")) {
							isPresent = true;
							toCheck = treeChild;
						}						
					}
				}
				
				if (isPresent) {
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
			
			if (!nodeFieldName.equals("stuff")) {
				
				Field configField = config.getFieldByName(nodeFieldName);
				String configFieldValue = configField.getValue();
				
				String[] splits = configFieldValue.split("\\;");
				String[] values = splits[0].replaceAll("\\[|\\]", "").split("\\,");
				double[] weights = parseWeights(splits[1]);
				
				for (int i = 0; i < values.length; i++) {
					if (values[i].equals(nodeFieldValue)) {
						weights[i] = round((weights[i]) + (1 / (double) numOfVideos), 4);
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