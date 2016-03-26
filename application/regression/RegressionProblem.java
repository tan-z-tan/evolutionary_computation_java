package application.regression;

import geneticProgramming.GpEnvironment;
import geneticProgramming.GpSymbolSet;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class RegressionProblem// extends JApplet
{
  private RegressionEvolutionModel model;
  public static long time;
  
  public RegressionProblem(String[] argv)
  {
    int level = 6;

    Properties expParameter = new Properties();
    //expParameter.put("method", "SGP");
    expParameter.put("method", "PORTS");
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
    expParameter.put("maxDepth", "15");
    
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

    InputStream stream = RegressionProblem.class.getResourceAsStream("GP_Node_Type");

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
    RegressionProblem problem = new RegressionProblem(args);
    
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
