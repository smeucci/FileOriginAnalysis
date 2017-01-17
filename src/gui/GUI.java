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
	    frame = new JFrame("Our JButton listener example");

	    // create our jbutton
	    JButton UpdateButton = new JButton("Update");
	    
	    // add the listener to the jbutton to handle the "pressed" event
	    UpdateButton.addActionListener(new ActionListener()
	    {
	      public void actionPerformed(ActionEvent e)
	      {
	        try {
				JDBC.updateDB("/home/saverio/Projects/FileOriginAnalysis/dataset/videos");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				System.out.println(e1.getMessage());
			}
	      }
	    });
	    
	 // create our jbutton
	    JButton initButton = new JButton("Init");
	    
	    // add the listener to the jbutton to handle the "pressed" event
	    initButton.addActionListener(new ActionListener()
	    {
	      public void actionPerformed(ActionEvent e)
	      {
	        try {
				JDBC.initializeDB();;
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				System.out.println(e1.getMessage());
			}
	      }
	    });

	    // put the button on the frame
	    frame.getContentPane().setLayout(new FlowLayout());
	    frame.add(UpdateButton);
	    frame.add(initButton);

	    // set up the jframe, then display it
	    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	    frame.setPreferredSize(new Dimension(300, 200));
	    frame.pack();
	    frame.setLocationRelativeTo(null);
	    frame.setVisible(true);
	  }
	
	public static void run() {
		// schedule this for the event dispatch thread (edt)
	    SwingUtilities.invokeLater(new Runnable()
	    {
	      public void run()
	      {
	        displayJFrame();
	      }
	    });
	}
	
}
