package com.flatironschool.javacs;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class SearchWindow extends JFrame implements ActionListener {

	private JPanel contentPane;
	private String query;
	private ArrayList<Entry<String, Double>> results = new ArrayList<Entry<String, Double>>();
	public JTextArea outputArea;
	public JTextField inputBox;
	public JButton okButton, andButton, orButton;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SearchWindow frame = new SearchWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void actionPerformed(ActionEvent event)
	{
		//do userSearh from Search.java on the search term
		query = inputBox.getText();
		try{
		Search search = new Search();
		if (event.getSource() == okButton)
		{
			results = search.singleSearch(query);
		}
		else if (event.getSource() == andButton)
		{
			results = search.andSearch(query);
		}
		else if (event.getSource() == orButton)
		{
			results = search.orSearch(query);
		}
		System.out.println(results.toString());
		}
		catch(Exception e)
		{}
		
		outputArea.setText(null);
		for(int i = 0; i < results.size(); i++)
		{
			outputArea.append((results.get(i)).getKey() + "\n");
		}
	}

	/**
	 * Create the frame.
	 */
	public SearchWindow() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 20, 700, 700);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		setLayout(new FlowLayout());
		inputBox = new JTextField(30);
		add(inputBox);
		inputBox.setVisible(true);
		okButton = new JButton("Search");
		okButton.addActionListener(this);
		add(okButton);
		okButton.setVisible(true);
		
		andButton = new JButton("Search with and");
		andButton.addActionListener(this);
		add(andButton);
		andButton.setVisible(true);
		
		orButton = new JButton("Search with or");
		orButton.addActionListener(this);
		add(orButton);
		orButton.setVisible(true);
		
		outputArea = new JTextArea();
		add(outputArea);
		outputArea.setVisible(true);
		
	}

}