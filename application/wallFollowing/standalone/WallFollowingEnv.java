package application.wallFollowing.standalone;

import geneticProgramming.GpEnvironment;
import geneticProgramming.GpNode;
import geneticProgramming.GpSymbolSet;
import geneticProgramming.GpTreeManager;
import geometry.Vector3D;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import application.wallFollowing.FieldPanel;
import application.wallFollowing.Robot;
import application.wallFollowing.WallFollowingEvolutionModel2;
import application.wallFollowing.symbols.Symbol_DOUBLE;
import application.wallFollowing.symbols.Symbol_HALF;
import application.wallFollowing.symbols.Symbol_IF_LT;
import application.wallFollowing.symbols.Symbol_INVERSE;
import application.wallFollowing.symbols.Symbol_PLUS;
import application.wallFollowing.symbols.Symbol_ROTATE45;
import application.wallFollowing.symbols.Symbol_ROTATE45INV;
import application.wallFollowing.symbols.Symbol_S1;
import application.wallFollowing.symbols.Symbol_S2;
import application.wallFollowing.symbols.Symbol_S3;
import application.wallFollowing.symbols.Symbol_S4;
import application.wallFollowing.symbols.Symbol_S5;
import application.wallFollowing.symbols.Symbol_S6;

public class WallFollowingEnv
{
	private static WallFollowingEvolutionModel2 model;
	private static Thread 	runThread = new Thread()
	{
		@Override
		public void run()
		{
			super.run();
			while(true)
			{
				try{
					Thread.sleep(20);
				}catch(Exception e){e.printStackTrace();}
				step();
			}	
		}
	};
	static
	{
		runThread.start();
		runThread.suspend();
	}
	
	private static JButton buttonStart;
	private static JButton buttonPause;
	private static boolean isPause = true;
	
	public static void main(String[] args)
	{
		GpEnvironment<Robot> environment = new GpEnvironment<Robot>();
		environment.setPopulationSize(1);
		environment.setPopulation(new ArrayList<Robot>());
		environment.setNumberOfMaxDepth(10);
		environment.setNumberOfMaxInitialDepth(10);
		
	    final GpSymbolSet symbolSet = environment.getSymbolSet();
	    symbolSet.addSymbol(new Symbol_S1());
	    symbolSet.addSymbol(new Symbol_S2());
	    symbolSet.addSymbol(new Symbol_S3());
	    symbolSet.addSymbol(new Symbol_S4());
	    symbolSet.addSymbol(new Symbol_S5());
	    symbolSet.addSymbol(new Symbol_S6());
	    symbolSet.addSymbol(new Symbol_PLUS());
	    symbolSet.addSymbol(new Symbol_DOUBLE());
	    symbolSet.addSymbol(new Symbol_INVERSE());
	    symbolSet.addSymbol(new Symbol_HALF());
	    symbolSet.addSymbol(new Symbol_ROTATE45());
	    symbolSet.addSymbol(new Symbol_ROTATE45INV());
	    symbolSet.addSymbol(new Symbol_IF_LT());
		
	    // MAP
	    int blockSize = 32;
	    int mapBlockHeight = 15;
	    int mapBlockWidth = 10;
	    	    
	    model = new WallFollowingEvolutionModel2(environment);
	    model.createMap(mapBlockWidth, mapBlockHeight, blockSize);
	    FieldPanel field = new FieldPanel(model.getMapObjects());
	    	    
	    model.setField(field);
	    model.setBestFitness(new JLabel("0"));
	    model.setBestTree(new JLabel(""));
	    model.setAverageFitness(new JLabel(""));
	    model.setEnvironment(environment);
	    model.initialize();
	    
	    
	    
	    // GUI part
		JFrame frame = new JFrame("WallFollowing Environment");
		
		// buttons
		JPanel bottomPanel = new JPanel();
		/**
		 buttonStart = new JButton("Run");
		 
		buttonStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				start();
			}
		});
		*/
		buttonPause = new JButton("start");
		buttonPause.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				pause();
			}
		});
		JButton buttonStep = new JButton("Step");
		buttonStep.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				step();
			}
		});
		
		JButton buttonRandomSet = new JButton("RandomPosition");
		buttonRandomSet.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				randomSet();
			}
		});
		bottomPanel.add(buttonPause);
		bottomPanel.add(buttonStep);
		bottomPanel.add(buttonRandomSet);
		
		// top panel
		final JTextField genomeField = new JTextField("( PLUS ( R45 ( Double S3 ) ) ( PLUS ( PLUS S2 S5 ) S2 ) )");
		genomeField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				GpNode newGenom = GpTreeManager.constructGpNodeFromString(genomeField.getText(), symbolSet);
				model.getPopulation().get(0).setRootNode(newGenom);
				model.getPopulation().get(0).setFitnessValue(0);
				if( !isPause )
				{
					pause();
				}	
			}
		});
		JButton buttonSetGene = new JButton("Set");
		buttonSetGene.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				GpNode newGenom = GpTreeManager.constructGpNodeFromString(genomeField.getText(), symbolSet);
				model.getPopulation().get(0).setRootNode(newGenom);
				model.getPopulation().get(0).setFitnessValue(0);
				if( !isPause )
				{
					pause();
				}
			}
		});
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.add(genomeField, BorderLayout.CENTER);
		topPanel.add(buttonSetGene, BorderLayout.EAST);
		
		frame.setLayout(new BorderLayout());
		frame.add(field, BorderLayout.CENTER);
		frame.add(topPanel, BorderLayout.NORTH);
		frame.add(bottomPanel, BorderLayout.SOUTH);
		frame.setSize(740, 580);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	private static void start()
	{
		if( isPause )
		{
			pause();
		}
		else
		{
			runThread.start();
			buttonStart.setEnabled(false);
		}
	}
	
	private static void pause()
	{
		if( isPause )
		{
			try{
				runThread.resume();
			}catch(Exception e){e.printStackTrace();}
			buttonPause.setText("pause");
			isPause = false;
		}
		else
		{
			try{
				runThread.suspend();
			}catch(Exception e){e.printStackTrace();}
			buttonPause.setText("start");
			isPause = true;
		}
	}
	
	private static void step()
	{
		model.oneEvaluation(model.getPopulation().get(0));
		model.getField().repaint();
	}
	
	private static void randomSet()
	{
		if( !isPause )
		{
			pause();
		}
		Robot tmp = model.createNewIndividual();
		Robot robot = model.getPopulation().get(0);
		robot.setPosition(tmp.getPosition());
		robot.setCheckMap(tmp.getCheckMap());
		robot.setFitnessValue(0);
		model.oneEvaluation(model.getPopulation().get(0));
		model.getField().repaint();
	}
}
