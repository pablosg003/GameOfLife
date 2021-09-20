package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

public class GUI extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private int xSize = 1000, ySize = 600;
	private JMenuBar menu;
	private MainPanel mainPanel;
	private JMenuItem clearButton, resizeButton, startButton, stopButton;

	public GUI() {
		
		//Centers the window
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension resolution = toolkit.getScreenSize();
		setBounds((resolution.width-xSize)/2, (resolution.height-ySize)/2, xSize, ySize);
		
		//Sets the title and config
		setTitle("The game of life");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//Menu configuration
		menu = new JMenuBar();
		clearButton = new JMenuItem("Clear");
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Clear");
			}
		});
		
		resizeButton = new JMenuItem("Set size");
		resizeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Set size");
				mainPanel.setTiles();
				setResizable(false);
			}
		});
		
		startButton = new JMenuItem("Start");
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Start");
			}
		});
		
		stopButton = new JMenuItem("Stop");
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Stop");
			}
		});
		
		menu.add(startButton);
		menu.add(stopButton);
		menu.add(clearButton);
		menu.add(resizeButton);
		setJMenuBar(menu);
		
		//Main panel configuration
		mainPanel = new MainPanel();
		mainPanel.setBackground(Color.BLACK);
		add(mainPanel);
		
	}
	
	private class MainPanel extends JPanel{
		
		private static final long serialVersionUID = 1L;
		private int pixelSize = 10, xTiles, yTiles;
		private Tile[][] tiles;

		public MainPanel() {
			super();
		}
		
		public void setTiles() {
						
			//Creates the layout with the required size
			Dimension panelSize = getSize();
			yTiles = panelSize.height/pixelSize;
			xTiles = panelSize.width/pixelSize;
			setLayout(new GridLayout(yTiles, xTiles));
			
			//Creates the tiles and adds them to the main panel
			tiles = new Tile[xTiles][yTiles];
			
			for (int j=0; j<yTiles; j++) {
				for (int i=0; i<xTiles; i++) {
					
					Tile tile = new Tile(i, j);
					tiles[i][j] = tile;
					if ((i+j)%2 == 0) tile.setBackground(Color.white);
					add(tile);
				}
			}	
		}
		
		private class Tile extends JPanel {
			
			private static final long serialVersionUID = 1L;
			@SuppressWarnings("unused")
			private int xCoord, yCoord;
			
			public Tile(int xCoord, int yCoord){
				
				super();
				setBackground(Color.black);
				this.xCoord = xCoord;
				this.yCoord = yCoord;

			}
		}
	}
}
