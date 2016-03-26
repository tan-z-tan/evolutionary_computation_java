package application.Order;

import geneticProgramming.GpEnvironment;
import geneticProgramming.GpIndividual;
import geneticProgramming.GpSymbolSet;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Properties;

public class OrderProblem
{
	public static void main(String argv[])
	{
		int level = 9; // default
		Properties expParameter = new Properties();
		//expParameter.put("method", "SGP");
		//expParameter.put("method", "PORTS");
        //expParameter.put("method", "PPT_PEED");
		expParameter.put("method", "PPT_MN");
		//expParameter.put("method", "PIPE");
		//expParameter.put("method", "PPT_PERCE");
        expParameter.put("PPT", "true");
        expParameter.put("PPTArity", "2");
        //expParameter.put("PPTDepth", String.valueOf(level + 1));
        expParameter.put("PPTDepth", "5");
        expParameter.put("PPTMaxCliqueSize", "5");
        expParameter.put("PPTSignificanceLevel", "0.75");
        expParameter.put("PPTSmoothingParameter", "0.05");
        expParameter.put("PPT_MN_order", "roulette");
        //expParameter.put("peedSampling", "edgeBased");
        expParameter.put("peedSampling", "cliqueBased");
        //expParameter.put("peedSampling", "dependencyBased");
        expParameter.put("peedCatProbability", "0.05");
        expParameter.put("initialization", "rampedHalfAndHalf");
        
		expParameter.put("selector", "tournament");
		expParameter.put("tournamentSize", "2");
		expParameter.put("truncationRate", "0.5");
		expParameter.put("populationSize", "2000");
		expParameter.put("repetitionNumber", "200");
		expParameter.put("portsT", "0.01");
		expParameter.put("portsAlpha", "0.0");
		expParameter.put("autoUpdateT", "true");
		expParameter.put("portsUpdateT", "adaptive");
		//expParameter.put("porteBeta", "0.1");
		expParameter.put("level", String.valueOf(level));
		expParameter.put("fitness", "deceptiveOrder");
		//expParameter.put("fitness", "order");
		expParameter.put("k", String.valueOf(3));
		expParameter.put("delta", String.valueOf(0.25));
		
		if( argv.length > 0 )
		{
			try{
			    expParameter.load(new FileInputStream(new File(argv[0])));
				if( expParameter.containsKey("level") )
				{
				    level = Integer.valueOf(expParameter.getProperty("level"));
				}
			} catch (Exception e) { e.printStackTrace(); }
		}
		
		GpSymbolSet symbolSet = new GpSymbolSet();
		for( int i = 1; i <= level; i++)
		{
			symbolSet.addSymbol( new OrderNode("x_" + String.valueOf(i), 0) );
			symbolSet.addSymbol( new OrderNode("*x_" + String.valueOf(i), 0) );
		}
		symbolSet.addSymbol( new OrderNode("J", 2) );
		
		GpEnvironment<GpIndividual> environment = new GpEnvironment<GpIndividual>();
		environment.setSymbolSet( symbolSet );
		environment.setCrossoverRatio(0.9);
		environment.setEliteSize(0);
		environment.setMutationRatio(0.1);
		environment.setNumberOfMaxInitialDepth( Integer.valueOf((String)expParameter.get("PPTDepth")) );
		environment.setNumberOfMaxDepth( Integer.valueOf((String)expParameter.get("PPTDepth")) );
		environment.setNumberOfMinimumDepth(1);
		environment.setRepetitionNumber(100);
		environment.setPopulation( new ArrayList<GpIndividual>() );
		
		environment.loadProperties(expParameter);
		
		// print experimental parameters
		System.out.println( expParameter );
		System.out.println( environment.getAttributes() );
		
		// run
		OrderEvolutionModel model = new OrderEvolutionModel(environment);
		model.run();
		model.finish();
	}
}
