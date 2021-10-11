package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSlider;

public class GUI extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private int xSize = 1500, ySize = 800;
	private JMenuBar menu;
	private MainPanel mainPanel;
	private JMenuItem clearButton, resizeButton, startButton, stopButton;
	private boolean isStarted = false;
	private GenLabel genLabel;
	private JSlider waitTime;

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
		clearButton.setEnabled(false);
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Clear");
				mainPanel.clear();
				genLabel.resetGen();
				genLabel.setText("Generation: 0");
			}
		});
		
		resizeButton = new JMenuItem("Set size");
		resizeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Set size");
				mainPanel.setTiles();
				setResizable(false);
				
				//Resets the enabled buttons
				resizeButton.setEnabled(false);
				clearButton.setEnabled(true);
				startButton.setEnabled(true);
			}
		});
		
		startButton = new JMenuItem("Start");
		startButton.setEnabled(false);
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Start");
				
				//Resets the enabled buttons
				isStarted = true;
				clearButton.setEnabled(false);
				stopButton.setEnabled(true);
				startButton.setEnabled(false);
				
				mainPanel.startGame();
			}
		});
		
		stopButton = new JMenuItem("Stop");
		stopButton.setEnabled(false);
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Stop");
				
				//Resets the enabled buttons
				isStarted = false;
				clearButton.setEnabled(true);
				stopButton.setEnabled(false);
				startButton.setEnabled(true);
				
				mainPanel.stopGame();
			}
		});
		
		menu.add(startButton);
		menu.add(stopButton);
		menu.add(clearButton);
		
		menu.add(resizeButton);
		setJMenuBar(menu);
		
		//Main panel configuration
		setLayout(new BorderLayout());
		mainPanel = new MainPanel();
		add(mainPanel, BorderLayout.CENTER);
		
		//Generation label updating
		genLabel = new GenLabel();
		
		//JSlider with wait time between generations
		JLabel timeL = new JLabel("Time between generations:");
		waitTime = new JSlider(JSlider.HORIZONTAL, 50, 5000, 500);
		
		//South panel configuration
		JPanel southPanel = new JPanel();
		southPanel.add(genLabel);
		southPanel.add(new JPanel());
		southPanel.add(new JPanel());
		southPanel.add(new JPanel());
		southPanel.add(new JPanel());
		southPanel.add(timeL);
		southPanel.add(waitTime);
		add(southPanel, BorderLayout.SOUTH);
		
	}
	
	private class GenLabel extends JLabel implements GenListener {
		
		private static final long serialVersionUID = 1L;
		private int generation = 0;

		public GenLabel() {
			setText("Generation: 0");
		}
		
		public void genChanged() {
			generation++;
			setText("Generation: " + generation);
		}
		
		public void resetGen() {
			generation = 0;
		}
	}
	
	private class MainPanel extends JPanel{
		
		private static final long serialVersionUID = 1L;
		private int pixelSize = 15, xTiles, yTiles;
		private Tile[][] tiles;
		private GameOfLife gameOfLife;
		private Thread mainGOL;
		private boolean keepRunning = true;

		public MainPanel() {
			super();
		}
		
		public void startGame() {
			
			gameOfLife = new GameOfLife();
			gameOfLife.addListener(genLabel);
			mainGOL = new Thread(gameOfLife);
			keepRunning = true;
			mainGOL.start();
		}
		
		public void stopGame() {
			keepRunning = false;
		}
		
		public void setTiles() {
						
			//Creates the layout with the required size
			Dimension panelSize = getSize();
			yTiles = panelSize.height/pixelSize;
			xTiles = panelSize.width/pixelSize;
			setLayout(new GridLayout(yTiles, xTiles));
			
			//Creates the listener
			TileListener listener = new TileListener();
			
			//Creates the tiles and adds them to the main panel
			tiles = new Tile[xTiles][yTiles];
			
			for (int j=0; j<yTiles; j++) {
				for (int i=0; i<xTiles; i++) {
					
					Tile tile = new Tile(i, j);
					tiles[i][j] = tile;
					add(tile);
					tile.addMouseListener(listener);
				}
			}	
		}
		
		//Sets all tiles off
		public void clear() {
			for(Tile[] t2: tiles) {
				for (Tile t: t2) {
					t.setOff();
				}
			}
		}
		
		private class TileListener extends MouseAdapter {
			
			public void mouseClicked(MouseEvent e) {
				Object source = e.getSource();
				
				if (source instanceof Tile && isStarted == false) {
					((Tile) source).switchColor();
				}
			}
		}
		
		private class GameOfLife implements Runnable {
			
			private int maxI = tiles.length-1, maxJ = tiles[0].length-1;
			private GenListener genListener;
			
			synchronized public void run() {
				
				while(keepRunning) {
					//Registers which tiles change next generation in a matrix
					boolean[][] nextGenChanges = new boolean[maxI+1][maxJ+1];
					for (int i = 0; i<maxI+1; i++) {
						for (int j = 0; j<maxJ+1; j++) {
							nextGenChanges[i][j] = hasToChange(i, j);
						}
					}
					
					//Enacts the changes 
					for (int i = 0; i<maxI+1; i++) {
						for (int j = 0; j<maxJ+1; j++) {
							if (nextGenChanges[i][j]) tiles[i][j].switchColor();
						}
					}
					
					//Listener and event stuff
					genListener.genChanged();
					//Wait time
					try {
						wait(waitTime.getValue());
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
			public void addListener(GenListener toAdd) {
				genListener = toAdd;
			}
			
			private boolean hasToChange(int i, int j) {
				
				Tile tile = tiles[i][j];
				int adjacentCells = 0;
				
				//Checks the number of adjacent alive tiles if inside the bounds
				if (i<maxI) {
					if (tiles[i+1][j].isAlive()) adjacentCells++;
					if (j<maxJ) if (tiles[i+1][j+1].isAlive()) adjacentCells++;
					if (j>0) if (tiles[i+1][j-1].isAlive()) adjacentCells++;
				}
				if (i>0) {
					if (tiles[i-1][j].isAlive()) adjacentCells++;
					if (j<maxJ) if (tiles[i-1][j+1].isAlive()) adjacentCells++;
					if (j>0) if (tiles[i-1][j-1].isAlive()) adjacentCells++;
				}
				if (j<maxJ) if (tiles[i][j+1].isAlive()) adjacentCells++;
				if (j>0) if (tiles[i][j-1].isAlive()) adjacentCells++;
				
				//Returns according to the game of life rules
				if (tile.isAlive() && !(adjacentCells == 2 || adjacentCells == 3)) return true;
				else if ((!tile.isAlive()) && adjacentCells == 3) return true;
				else return false;
			}
		}
		
		private class Tile extends JPanel {
			
			private static final long serialVersionUID = 1L;
			
			@SuppressWarnings("unused")
			private int xCoord, yCoord;
			private Color onColor = Color.WHITE, offColor = Color.BLACK, currentColor;
			private boolean alive = false;
			
			public Tile(int xCoord, int yCoord){
				
				super();
				setBackground(Color.black);
				this.xCoord = xCoord;
				this.yCoord = yCoord;
			}
			
			public void setOn() {
				currentColor = onColor;
				setBackground(currentColor);
				alive = true;
			}
			
			public void setOff() {
				currentColor = offColor;
				setBackground(currentColor);
				alive = false;
			}
			
			public void switchColor() {
				if (currentColor == onColor) {
					setOff();
				} else {
					setOn();
				}
			}
			public boolean isAlive() {
				return alive;
			}
		}
	}
}

interface GenListener {
	void genChanged();
}
