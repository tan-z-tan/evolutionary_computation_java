package application.royalTreePPT;

import geneticProgramming.GpEnvironment;
import geneticProgramming.GpIndividual;
import geneticProgramming.GpSymbolSet;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Properties;

public class RoyalTreePPTProblem
{
    public static void main(String argv[])
    {
        int level = 4; // default
        Properties expParameter = new Properties();
        //expParameter.put("method", "SGP");
        //expParameter.put("method", "PORTS");
        //expParameter.put("method", "PPT_MN");
        //expParameter.put("method", "PIPE");
        //expParameter.put("method", "PPT_ECGP");
        expParameter.put("method", "PPT_PERCE");
        expParameter.put("PPT", "true");
        expParameter.put("PPTArity", String.valueOf(2));
        expParameter.put("PPTDepth", String.valueOf(level + 1));
        expParameter.put("PPTMaxCliqueSize", "9");
        expParameter.put("PPTSignificanceLevel", "0.1");
        expParameter.put("PPTSmoothingParameter", "0.03");
        expParameter.put("PPT_MN_order", "roulette");
        
        
        //expParameter.put("PPT_dependency", "chiSquare");
        expParameter.put("PPT_dependency", "mutualInformation");
        //expParameter.put("givenKnowledge", "parent-child,sibling");
        //expParameter.put("givenKnowledge", "sibling");
        
        //expParameter.put("peedSampling", "edgeBased");
        //expParameter.put("peedSampling", "cliqueBased");
        expParameter.put("peedSampling", "relatedCliqueBased");
        //expParameter.put("peedSampling", "dependencyBased");
        expParameter.put("peedCatProbability", "0.03");
        expParameter.put("printClique", "true");
        expParameter.put("isIndividualPrint", "true");
        
        //expParameter.put("method", "SIHC");
        expParameter.put("selector", "tournament");
        //expParameter.put("selector", "truncation");
        expParameter.put("truncationRate", "0.5");
        expParameter.put("selectionOrder", "normal");
        expParameter.put("tournamentSize", "10");
        //expParameter.put("initialization", "grow");
        expParameter.put("initialization", "full");
        expParameter.put("eliteSize", "1");
        expParameter.put("crossover", "90/10");
        expParameter.put("portsT", "0.01");
        expParameter.put("portsAlpha", "0.01");
        expParameter.put("portsMutation", "0.1");
        expParameter.put("portsStart", "root");
        // expParameter.put("portsDepthDependency", "0.0");
        // expParameter.put("portsUpdate", "none");
        // expParameter.put("portsUpdateParameter", "0.99");
        expParameter.put("autoUpdateT", "true");
        // expParameter.put("autoUpdateAlpha", "true");
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

        char[] levelSymbol = { 'A', 'B', 'C', 'D', 'E', 'F', 'G' };
        GpSymbolSet symbolSet = new GpSymbolSet();
        symbolSet.addSymbol(new RoyalTreePPTNode("x", 0));
        
        if( expParameter.containsKey("fitness") && expParameter.get("fitness").equals("deceptive") )
        {
        	symbolSet.addSymbol(new RoyalTreePPTNode("y", 0));
        }
        for (int i = 1; i <= level; i++)
        {
            symbolSet.addSymbol(new RoyalTreePPTNode(String.valueOf(levelSymbol[i - 1]), 2));
        }

        GpEnvironment<GpIndividual> environment = new GpEnvironment<GpIndividual>();
        environment.setSymbolSet(symbolSet);
        environment.setCrossoverRatio(0.9);
        // environment.setEliteSize(1);
        environment.setMutationRatio(0.1);
        environment.setNumberOfMaxInitialDepth(level + 1);
        environment.setNumberOfMaxDepth(level + 1);
        environment.setNumberOfMinimumDepth(level + 1);
        environment.setPopulationSize(1000);
        environment.setRepetitionNumber(20);
        environment.setPopulation(new ArrayList<GpIndividual>());

        environment.loadProperties(expParameter);

        // run
        RoyalTreePPTEvolutionModel model = new RoyalTreePPTEvolutionModel(environment);
        model.run();
    }
}
