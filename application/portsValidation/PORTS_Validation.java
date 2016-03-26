package application.portsValidation;

import geneticProgramming.GPEvolutionModel;
import geneticProgramming.GpEnvironment;
import geneticProgramming.GpIndividual;
import geneticProgramming.GpSymbolSet;
import geneticProgramming.symbols.DefaultSymbolType;
import geneticProgramming.symbols.SymbolType;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import util.PopulationAnalyzer;
import application.multiplexer.symbols.And;
import application.multiplexer.symbols.If;
import application.multiplexer.symbols.Not;
import application.multiplexer.symbols.Or;
import application.multiplexer.symbols.X;

public class PORTS_Validation
{
  public static void main(String argv[])
  {
    //RandomManager.setSeed(39);
    int level = 6; // default
    Properties expParameter = new Properties();
    //expParameter.put("method", "SGP");
    expParameter.put("method", "PORTS");
    expParameter.put("selector", "tournament");
    expParameter.put("selectionOrder", "normal");
    expParameter.put("tournamentSize", "6");
    expParameter.put("porteT", "0.5");
    expParameter.put("porteAlpha", "1");
    expParameter.put("level", String.valueOf(level));
    expParameter.put("initialization", "grow");
    
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
    symbolSet.addSymbol(new DefaultSymbolType("N", 1));
    symbolSet.addSymbol(new DefaultSymbolType("N", 1));
    symbolSet.addSymbol(new DefaultSymbolType("N", 1));
    symbolSet.addSymbol(new DefaultSymbolType("N", 1));
    symbolSet.addSymbol(new DefaultSymbolType("N", 1));
    symbolSet.addSymbol(new DefaultSymbolType("N", 1));
    symbolSet.addSymbol(new DefaultSymbolType("N", 1));
    symbolSet.addSymbol(new DefaultSymbolType("N", 1));
    symbolSet.addSymbol(new DefaultSymbolType("N", 1));
    symbolSet.addSymbol(new DefaultSymbolType("N", 1));
    symbolSet.addSymbol(new DefaultSymbolType("N", 1));
    symbolSet.addSymbol(new DefaultSymbolType("N", 1));
    symbolSet.addSymbol(new DefaultSymbolType("N", 1));
    symbolSet.addSymbol(new DefaultSymbolType("N", 1));
    symbolSet.addSymbol(new DefaultSymbolType("N", 1));
    symbolSet.addSymbol(new DefaultSymbolType("N", 1));
    symbolSet.addSymbol(new DefaultSymbolType("N", 1));
    symbolSet.addSymbol(new DefaultSymbolType("N", 1));
    symbolSet.addSymbol(new DefaultSymbolType("N", 1));
    symbolSet.addSymbol(new DefaultSymbolType("N", 1));
    symbolSet.addSymbol(new DefaultSymbolType("N", 1));
    symbolSet.addSymbol(new DefaultSymbolType("N", 1));
    symbolSet.addSymbol(new DefaultSymbolType("N", 1));
    symbolSet.addSymbol(new DefaultSymbolType("N", 1));
    symbolSet.addSymbol(new DefaultSymbolType("N", 1));
    symbolSet.addSymbol(new DefaultSymbolType("N", 1));
    symbolSet.addSymbol(new DefaultSymbolType("N", 1));
    symbolSet.addSymbol(new DefaultSymbolType("T", 0));
    
    //GpEnvironment<MultiplexerIndividual> environment = new GpEnvironment<MultiplexerIndividual>();
    GpEnvironment<GpIndividual> environment = new GpEnvironment<GpIndividual>();
    environment.setSymbolSet(symbolSet);
    environment.setCrossoverRatio(0.9);
    environment.setEliteSize(0);
    environment.setMutationRatio(0.1);
    environment.setNumberOfMaxInitialDepth(1000);
    environment.setNumberOfMaxDepth(1000);
    //environment.setNumberOfMinimumDepth(10);
    environment.setPopulationSize(1000);
    environment.setRepetitionNumber(50);
    //environment.setPopulation(new ArrayList<MultiplexerIndividual>());
    
    environment.loadProperties(expParameter);
    
    // print experimental parameters
    System.out.println(expParameter);
    System.out.println(environment.getAttributes());
    
    //MultiplexerEvolutionModel model = new MultiplexerEvolutionModel(environment);
    GPEvolutionModel<GpIndividual, GpEnvironment<GpIndividual>> model = new GPEvolutionModel<GpIndividual, GpEnvironment<GpIndividual>>(environment);
    PopulationAnalyzer analyzer = new PopulationAnalyzer(environment);
    //model.run();
    model.initialize();
//    {
//        for( int i = 1; i < environment.getPopulationSize(); i++ )
//        {
//            environment.getPopulation().set(i, environment.getPopulation().get(0));
//        }
//    }
    for( int i = 0; i < 3; i++ )
    {
        System.out.println("---------- Generation Count = " + environment.getGenerationCount() );
        System.out.println(" Average Size = " + analyzer.calculateAverageTreeSize());
        model.updateGeneration();
    }
  }
}
