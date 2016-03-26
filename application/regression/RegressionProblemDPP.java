package application.regression;

import geneticProgramming.GpEnvironment;
import geneticProgramming.GpSymbolSet;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

import mpi.MPI;

public class RegressionProblemDPP// extends JApplet
{
  private RegressionEvolutionModel model;

  public RegressionProblemDPP(String[] args)
  {
    // ***** initialisation step
    System.out.println(Arrays.toString(args));
    args = MPI.Init(args);
    int id = MPI.COMM_WORLD.Rank();
    int size = MPI.COMM_WORLD.Size();
    
    // ***** start
    int level = 4;
    
    Properties expParameter = new Properties();
    //expParameter.put("method", "SGP");
    expParameter.put("method", "PORTS");
    expParameter.put("level", String.valueOf(level));
    expParameter.put("selector", "tournament");
    expParameter.put("selectionOrder", "reverse");
    expParameter.put("tournamentSize", "4");
    expParameter.put("porteT", "0.4");
    expParameter.put("porteAlpha", "0.9");
    expParameter.put("maxDepth", "15");
    expParameter.put("sizePrint", "false");
    expParameter.put("parallel", "true");
    expParameter.put("np", String.valueOf(size));
    expParameter.put("id", String.valueOf(id));
    
    if (args.length > 0)
    {
      try
      {
        expParameter.load(new FileInputStream(new File(args[0])));
        if (expParameter.containsKey("level"))
        {
          level = Integer.valueOf(expParameter.getProperty("level"));
        }
      } catch (Exception e)
      {
        e.printStackTrace();
      }
    }

    InputStream stream = RegressionProblemDPP.class.getResourceAsStream("GP_Node_Type");

    // creates environment
    GpSymbolSet symbolSet = GpSymbolSet.getSymbolSet(stream);
    GpEnvironment<RegressionIndividual> environment = new GpEnvironment<RegressionIndividual>();
    environment.setRepetitionNumber(60);
    environment.setPopulationSize(128);
    environment.setCrossoverRatio(0.9);
    environment.setMutationRatio(0.1);
    environment.setEliteSize(0);
    environment.setNumberOfMaxInitialDepth(15);
    environment.setNumberOfMaxDepth(15);
    environment.setSymbolSet(symbolSet);

    // this method may override parameters above
    environment.loadProperties(expParameter);

    // prints experimental parameters
    System.out.println(environment.getAttributes());
    // constructs evolution model and run
    RegressionEvolutionModel model = new RegressionEvolutionModel(environment);
    model.run();
  }

  public static void main(String args[])
  {
    RegressionProblemDPP problem = new RegressionProblemDPP(args);
    
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
