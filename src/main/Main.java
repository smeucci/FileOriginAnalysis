package main;

import testing.Test;
import training.Train;
import videoclass.VideoClass;

public class Main {

	public static void main(String[] args) throws Exception{
				
		VideoClass classA = new VideoClass("A", "./dataset/classA.txt");
		VideoClass classB = new VideoClass("B", "./dataset/classB.txt");
		
		String outputPath = "./dataset/";
		boolean withAttributes = true;
		
		//new Train(classA, classB, outputPath, withAttributes).train();
		
		String video1 = "/media/saverio/DATA/dataset-righini/xmls/galaxys3_dasara/flat/sky_move_1.mp4.xml";
		String video2 = "/media/saverio/DATA/dataset-righini/xmls/huaweig6_rossana/indoor/garage_move_1.mp4.xml";
		String video3 = "/media/saverio/DATA/dataset-righini/xmls/ipadmini_marco/indoor/uni_move_1.MOV.xml";
		new Test(video1, "./dataset/configA-w.xml", "./dataset/configB-w.xml", 20, 20).test();
	}
	
}