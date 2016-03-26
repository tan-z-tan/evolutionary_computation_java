package application.DMAX;

import geneticProgramming.GpEnvironment;
import geneticProgramming.GpIndividual;
import geneticProgramming.GpSymbolSet;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Properties;

import application.DMAX.symbols.Lambda;
import application.DMAX.symbols.Multiply;
import application.DMAX.symbols.Plus;
import application.DMAX.symbols.Variable;

public class DmaxProblem
{
    public static void main(String argv[])
    {
        int level = 3; // default
        int r = 3; // default
        Properties expParameter = new Properties();
        // expParameter.put("method", "SGP");
        // expParameter.put("method", "PORTS");
        //expParameter.put("method", "PPT_MN");
        //expParameter.put("method", "PIPE");
        //expParameter.put("method", "ECGP");
        expParameter.put("method", "PERCE");
        //expParameter.put("method", "PIPE");
        expParameter.put("PPT", "true");
        expParameter.put("PPTArity", String.valueOf(r));
        expParameter.put("PPTDepth", String.valueOf(level + 1));
        expParameter.put("PPTMaxCliqueSize", "7");
        expParameter.put("PPTSignificanceLevel", "0.05");
        expParameter.put("PPT_dependency", "mutualInformation");
        //expParameter.put("PPT_dependency", "chiSquare");
        expParameter.put("PPTSmoothingParameter", "0.07");
        expParameter.put("PPT_MN_order", "roulette");
        expParameter.put("peedSampling", "cliqueBased");
        expParameter.put("peedCatProbability", "0.05");
        
        // expParameter.put("method", "SIHC");
        expParameter.put("selector", "tournament");
        //expParameter.put("selector", "truncation");
        expParameter.put("truncationRate", "0.5");
        expParameter.put("selectionOrder", "normal");
        expParameter.put("tournamentSize", "30");
        //expParameter.put("initialization", "grow");
        expParameter.put("initialization", "full");
        expParameter.put("eliteSize", "1");
        expParameter.put("crossover", "90/10");
        expParameter.put("portsT", "0.02");
        expParameter.put("portsAlpha", "0.01");
        expParameter.put("portsMutation", "0.05");
        expParameter.put("portsStart", "root");
        // expParameter.put("portsDepthDependency", "0.0");
        // expParameter.put("portsUpdate", "none");
        // expParameter.put("portsUpdateParameter", "0.99");
        //expParameter.put("autoUpdateT", "true");
        expParameter.put("autoUpdateAlpha", "true");
        // expParameter.put("sizePrint", "false");
        // expParameter.put("isUniqueStructureRecorded", "false");
        expParameter.put("level", String.valueOf(level));
        expParameter.put("fitness", "deceptive");
        // expParameter.put("maxSizeForAbandoning", "1000");
        // expParameter.put("crossover", "depth-dependent");

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

        GpSymbolSet symbolSet = new GpSymbolSet();
        symbolSet.addSymbol(new Lambda(r));
        //symbolSet.addSymbol(new One());
        symbolSet.addSymbol(new Variable());
        symbolSet.addSymbol(new Plus(r));
        symbolSet.addSymbol(new Multiply(r));
        
        GpEnvironment<GpIndividual> environment = new GpEnvironment<GpIndividual>();
        environment.setSymbolSet(symbolSet);
        environment.setCrossoverRatio(0.9);
        // environment.setEliteSize(1);
        environment.setMutationRatio(0.1);
        environment.setNumberOfMaxInitialDepth(level + 1);
        environment.setNumberOfMaxDepth(level + 1);
        environment.setNumberOfMinimumDepth(level + 1);
        environment.setPopulationSize(2000);
        environment.setRepetitionNumber(50);
        environment.setPopulation(new ArrayList<GpIndividual>());

        environment.loadProperties(expParameter);

        // run
        DMaxEvolutionModel model = new DMaxEvolutionModel(environment);
        model.run();
    }
}
