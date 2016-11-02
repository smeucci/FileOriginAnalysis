package main;

import training.Train;
import videoclass.VideoClass;

public class Main {

	public static void main(String[] args) throws Exception{
				
		VideoClass classA = new VideoClass("A", "./dataset/classA.txt");
		VideoClass classB = new VideoClass("B", "./dataset/classB.txt");
		
		String outputPath = "./dataset/";
		boolean withAttributes = true;
		
		Train trainer = new Train(classA, classB, outputPath, withAttributes);
		
		trainer.train();
		
	}
	
}