package gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import utils.JDBC;

public class GUI {
	
	public static JFrame frame;

	private static void displayJFrame()
	  {
	    frame = new JFrame("File Origin Analysis");

	    JButton updateButton = new JButton("Update");
	    updateButton.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent ae) {
		        try {
					JDBC.updateDB("/home/saverio/Projects/FileOriginAnalysis/dataset/videos");
					System.out.println("Done.");
				} catch (Exception e) {
					e.printStackTrace();
				}
	    	}
	    });
	    
	    JButton initButton = new JButton("Init");
	    initButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent ae) {
		        try {
					JDBC.initializeDB();
					System.out.println("Done.");
				} catch (Exception e) {
					e.printStackTrace();
				}
		    }
	    });

	    frame.getContentPane().setLayout(new FlowLayout());
	    frame.add(updateButton);
	    frame.add(initButton);

	    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	    frame.setPreferredSize(new Dimension(300, 200));
	    frame.pack();
	    frame.setLocationRelativeTo(null);
	    frame.setVisible(true);
	  }
	
	public static void run() {
	    SwingUtilities.invokeLater(new Runnable() {
	    	public void run() {
	    		displayJFrame();
	    	}
	    });
	}
	
}
