package application.regression;

import geneticProgramming.GpEnvironment;
import geneticProgramming.GpSymbolSet;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class RegressionProblemPPT// extends JApplet
{
  private RegressionEvolutionModel model;
  public static long time;
  
  public RegressionProblemPPT(String[] argv)
  {
    int level = 6;

    Properties expParameter = new Properties();
    expParameter.put("method", "SGP");
    //expParameter.put("method", "PORTS");
    if( false ){
    	expParameter.put("method", "PPT_PERCE");
    	expParameter.put("PPT", "true");
    	expParameter.put("PPTArity", "4");
    	expParameter.put("PPTDepth", "4");
    	expParameter.put("PPTMaxCliqueSize", "13");
    	expParameter.put("PPTSignificanceLevel", "0.75");
    	expParameter.put("PPTSmoothingParameter", "0.02");
    	expParameter.put("PPT_MN_order", "roulette");
    	expParameter.put("PPT_dependency", "chiSquare");
    	expParameter.put("initialization", "full");
    	expParameter.put("peedSampling", "relatedCliqueBased");
    	expParameter.put("truncationRate", "1.0");
    	expParameter.put("portsT", "0.01");
    	expParameter.put("peedCatProbability", "0.2");
    }
    
    expParameter.put("level", String.valueOf(level));
    expParameter.put("selector", "tournament");
    expParameter.put("selectionOrder", "reverse");
    expParameter.put("tournamentSize", "4");
    expParameter.put("portsT", "0.5");
    expParameter.put("portsAlpha", "0.9");
    expParameter.put("autoUpdateT", "true");
    expParameter.put("autoUpdateAlpha", "false");
    expParameter.put("portsMutation", "0.00");
    expParameter.put("sizePrint", "false");
    expParameter.put("isUniqueStructureRecorded", "false");
    //expParameter.put("maxDepth", "15");
    expParameter.put("maxDepth", "4");
    
    if (argv.length > 0)
    {
      try
      {
        expParameter.load(new FileInputStream(new File(argv[0])));
        if (expParameter.containsKey("level"))
        {
          level = Integer.valueOf(expParameter.getProperty("level"));
        }
      } catch (Exception e)
      {
        e.printStackTrace();
      }
    }

    InputStream stream = RegressionProblemPPT.class.getResourceAsStream("GP_Node_Type");

    // creates environment
    GpSymbolSet symbolSet = GpSymbolSet.getSymbolSet(stream);
    GpEnvironment<RegressionIndividual> environment = new GpEnvironment<RegressionIndividual>();
    environment.setRepetitionNumber(50);
    environment.setPopulationSize(500);
    environment.setCrossoverRatio(0.9);
    environment.setMutationRatio(0.1);
    environment.setEliteSize(0);
    environment.setNumberOfMaxInitialDepth(4);
    environment.setNumberOfMaxDepth(15);
    environment.setSymbolSet(symbolSet);

    // this method may override parameters above
    environment.loadProperties(expParameter);

    // prints experimental parameters
    //System.out.println(expParameter);
    System.out.println(environment.getAttributes());

    // constructs evolution model and run
    //RegressionEvolutionModel model = new RegressionEvolutionModel(environment);
    RegressionEvolutionModel model = new RegressionEvolutionModel(environment);
    model.run();
  }

  public static void main(String args[])
  {
    RegressionProblemPPT problem = new RegressionProblemPPT(args);
    
    // GUI
    // JFrame frame = new JFrame("Function Approximation");
    // frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    // frame.setSize(400, 400);
    // frame.add(problem.model.getPlotPanel());
    // frame.setVisible( true );

    // run
    // problem.model.run();
    // System.out.println("fail");
    // System.exit(0);
  }
}
