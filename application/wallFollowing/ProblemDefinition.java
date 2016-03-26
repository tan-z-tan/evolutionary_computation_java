package application.wallFollowing;

import geneticProgramming.GpEnvironment;
import geneticProgramming.GpSymbolSet;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Shape;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import application.wallFollowing.symbols.Symbol_DOUBLE;
import application.wallFollowing.symbols.Symbol_HALF;
import application.wallFollowing.symbols.Symbol_IF_LT;
import application.wallFollowing.symbols.Symbol_INVERSE;
import application.wallFollowing.symbols.Symbol_LAST;
import application.wallFollowing.symbols.Symbol_PLUS;
import application.wallFollowing.symbols.Symbol_ROTATE45;
import application.wallFollowing.symbols.Symbol_ROTATE45INV;
import application.wallFollowing.symbols.Symbol_S1;
import application.wallFollowing.symbols.Symbol_S2;
import application.wallFollowing.symbols.Symbol_S3;
import application.wallFollowing.symbols.Symbol_S4;
import application.wallFollowing.symbols.Symbol_S5;
import application.wallFollowing.symbols.Symbol_S6;

public class ProblemDefinition
{
  // field parameter
  /** Field */
  private FieldPanel _field;
  /** Map objects in the experiment field */
  private List<Shape> _mapObjects;
  /** Number of vertical block of field */
  private int _mapBlockHeight;
  /** Number of horizontal block of field */
  private int _mapBlockWidth;
  /** Size of a Block */
  private int _blockSize;

  // ---------- GP parameters ----------
  GpEnvironment<Robot> _environment;
  
  // --- GUI parts ---
  private JPanel _topPanel;
  private JLabel _maxFitness;
  private JLabel _bestTree;
  private JLabel _averageFitness;

  /** Constructor */
  public ProblemDefinition(String[] argv)
  {
    Properties expParameter = new Properties();
    expParameter.put("method", "SGP");
    //expParameter.put("method", "PORTS");
    expParameter.put("selector", "tournament");
    expParameter.put("tournamentSize", "4");
    expParameter.put("crossoverRatio", "0.8");
    expParameter.put("mutationRatio", "0.1");
    expParameter.put("porteAlpha", "0.4");
    expParameter.put("porteBeta", "0.1");
    expParameter.put("maxDepth", "8");
    expParameter.put("maxInitialDepth", "8");
    expParameter.put("gui", "true");
    //expParameter.put("gui", "false");
    
    // PERCE setting
    
    //expParameter.put("method", "PPT_PEED");
    //expParameter.put("PPT", "true");
    expParameter.put("PPTArity", "4");
    expParameter.put("PPTDepth", "4");
    expParameter.put("PPTMaxCliqueSize", "7");
    expParameter.put("PPTSignificanceLevel", "0.75");
    expParameter.put("PPTSmoothingParameter", "0.03");
    expParameter.put("PPT_MN_order", "roulette");
    //expParameter.put("peedSampling", "edgeBased");
    expParameter.put("peedSampling", "cliqueBased");
    expParameter.put("initialization", "full");
    
    
    if (argv.length > 0)
    {
      try
      {
        expParameter.load(new FileInputStream(new File(argv[0])));
      } catch (Exception e)
      {
        e.printStackTrace();
      }
    }

    // MAP
    _blockSize = 32;
    _mapBlockHeight = 15;
    _mapBlockWidth = 10;

    // GP environment
    _environment = new GpEnvironment<Robot>();
    _environment.setPopulationSize(256);
    _environment.setCrossoverRatio(0.8);
    _environment.setMutationRatio(0.1);
    //_environment.setNumberOfMaxInitialDepth(10);
    //_environment.setNumberOfMaxDepth(10);
    _environment.setNumberOfMaxInitialDepth(3);
    _environment.setNumberOfMaxDepth(5);
    _environment.setEliteSize(1);
    _environment.setRepetitionNumber(30);
    // _environment.putAttribute("porteAlpha", "0.2");
    // _environment.putAttribute("porteBeta", "0.005");

    // this method may override parameters above
    _environment.loadProperties(expParameter);

    // sets gp symbol
    GpSymbolSet symbolSet = _environment.getSymbolSet();
    // Robots
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
    symbolSet.addSymbol(new Symbol_LAST());

    initGUI(Boolean.valueOf((String) expParameter.get("gui")));

    // prints experimental parameters
    expParameter.list(System.out);
    System.out.println(_environment.getAttributes());

    // ---------- evolution model ----------
    if (expParameter.getProperty("method").equals("SGP"))
    {
      WallFollowingEvolutionModel2 _model;
      _model = new WallFollowingEvolutionModel2(_environment);
      _model.createMap(_mapBlockWidth, _mapBlockHeight, _blockSize);
      _field.setObjects(_model.getMapObjects());
      _model.setField(_field);
      _model.setBestFitness(_maxFitness);
      _model.setBestTree(_bestTree);
      _model.setAverageFitness(_averageFitness);
      _model.setEnvironment(_environment);
      _model.initialize();
      start(_model, Boolean.valueOf((String) expParameter.get("gui")));
    } else if (expParameter.getProperty("method").equals("PORTS"))
    {
    	WallFollowingPorteEvolutionModel _model;
      _model = new WallFollowingPorteEvolutionModel(_environment);
      _model.createMap(_mapBlockWidth, _mapBlockHeight, _blockSize);
      _field.setObjects(_model.getMapObjects());
      _model.setField(_field);
      _model.setBestFitness(_maxFitness);
      _model.setBestTree(_bestTree);
      _model.setAverageFitness(_averageFitness);
      _model.setEnvironment(_environment);
      _model.initialize();
      start(_model, Boolean.valueOf((String) expParameter.get("gui")));
    }
    else
    {
        WallFollowingEvolutionModel2 _model;	
        _model = new WallFollowingEvolutionModel2(_environment);
        _model.createMap(_mapBlockWidth, _mapBlockHeight, _blockSize);
        _field.setObjects(_model.getMapObjects());
        _model.setField(_field);
        _model.setBestFitness(_maxFitness);
        _model.setBestTree(_bestTree);
        _model.setAverageFitness(_averageFitness);
        _model.setEnvironment(_environment);
        _model.initialize();
        start(_model, Boolean.valueOf((String) expParameter.get("gui")));
    }
  }

  private void initGUI(boolean gui)
  {
    _field = new FieldPanel(_mapObjects);
    _maxFitness = new JLabel("Max Fitness: ");
    _bestTree = new JLabel("Avarage Fitness: ");
    _averageFitness = new JLabel("Average Tree Size: ");
    JPanel parameterPanel = new JPanel();
    parameterPanel.setLayout(new GridLayout(3, 1));
    parameterPanel.add(_maxFitness);
    parameterPanel.add(_bestTree);
    parameterPanel.add(_averageFitness);
    _topPanel = new JPanel(new BorderLayout());
    _topPanel.add(_field, BorderLayout.CENTER);
    _topPanel.add(parameterPanel, BorderLayout.SOUTH);
  }

  public void start(WallFollowingEvolutionModel2 model, boolean gui)
  {
    if (gui)
    {
      JFrame frame = new JFrame("WallFollowingDemo");
      frame.setSize(1000, 750);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.add(_topPanel);
      frame.setVisible(true);
    }
    Thread gpThread = new Thread(model);
    gpThread.run();
  }

  public void start(WallFollowingPorteEvolutionModel model, boolean gui)
  {
    if (gui)
    {
      JFrame frame = new JFrame("WallFollowingDemo");
      frame.setSize(1000, 750);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.add(_topPanel);
      frame.setVisible(true);
    }
    Thread gpThread = new Thread(model);
    gpThread.run();
  }

  public static void main(String args[])
  {
    ProblemDefinition problem = new ProblemDefinition(args);
    // problem.init();
  }
}
