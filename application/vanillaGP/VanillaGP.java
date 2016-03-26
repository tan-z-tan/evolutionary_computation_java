package application.vanillaGP;

import geneticProgramming.GPEvolutionModel;
import geneticProgramming.GpEnvironment;
import geneticProgramming.GpIndividual;
import geneticProgramming.GpSymbolSet;
import geneticProgramming.GpTreeManager;
import geneticProgramming.symbols.DefaultSymbolType;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class VanillaGP
{
//    private static JFrame frame;
//    private static XYSeriesCollection dataCollection = new XYSeriesCollection();
//    static{
//        frame = new JFrame();
//        frame.setLayout(new BorderLayout());
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//    }
    
    public static void main(String args[])
    {
        Properties expParameter = new Properties();
        //expParameter.put("method", "SGP");
        expParameter.put("method", "PORTS");
        expParameter.put("selector", "tournament");
        expParameter.put("tournamentSize", "1");
        expParameter.put("selectionOrder", "normal");
        expParameter.put("crossover", "normal");
        expParameter.put("portsT", "0.2");
        expParameter.put("portsAlpha", "1");
        //expParameter.put("portsStart", "random");
        expParameter.put("initialization", "full");
        //expParameter.put("portsSameTreeCut", "false");
        //expParameter.put("autoUpdateT", "true");
        expParameter.put("autoUpdateAlpha", "false");
        expParameter.put("portsMutation", "0.00");
        //expParameter.put("sizePrint", "true");
        //expParameter.put("isUniqueStructureRecorded", "true");
        //expParameter.put("maxDepth", "15");
        
        if (args.length > 0)
        {
            try
            {
                expParameter.load(new FileInputStream(new File(args[0])));
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        
        // creates environment
        GpSymbolSet symbolSet = new GpSymbolSet();
        symbolSet.addSymbol(new DefaultSymbolType("N", 2));
        symbolSet.addSymbol(new DefaultSymbolType("t", 0));
        GpEnvironment<GpIndividual> environment = new GpEnvironment<GpIndividual>();
        environment.setRepetitionNumber(30);
        environment.setPopulationSize(100);
        environment.setCrossoverRatio(1);
        environment.setMutationRatio(0.0);
        environment.setEliteSize(0);
        environment.setNumberOfMaxInitialDepth(5);
        environment.setNumberOfMaxDepth(10000);
        environment.setSymbolSet(symbolSet);
        
        // this method may override parameters above
        environment.loadProperties(expParameter);

        // prints experimental parameters
        System.out.println(environment.getAttributes());

        // constructs evolution model and run
        GPEvolutionModel<GpIndividual, GpEnvironment<GpIndividual>> model = new GPEvolutionModel<GpIndividual, GpEnvironment<GpIndividual>>(environment);
        model.initialize();
        for( GpIndividual ind: model.getEnvironment().getPopulation() )
        {
            //System.out.println(GpTreeManager.getS_Expression(ind.getRootNode()));
        }
        //printSizeDistribution(environment);
        model.run();
        
        for( GpIndividual ind: model.getEnvironment().getPopulation() )
        {
            //System.out.println(GpTreeManager.getS_Expression(ind.getRootNode()));
        }
        //printSizeDistribution(environment);
    }
    
    private static void printSizeDistribution(GpEnvironment<? extends GpIndividual> environment)
    {
        XYSeries data = new XYSeries("Generation " + String.valueOf(environment.getGenerationCount()));
        
        // make up size distribution
        int[] sizeDistribution = new int[500];
        for( int i = 0; i < environment.getPopulation().size(); i++ )
        {
            GpIndividual ind = environment.getPopulation().get(i);
            int size = GpTreeManager.getNodeSize(ind.getRootNode());
            if( size < sizeDistribution.length )
            {
                sizeDistribution[size] ++;
            }
        }
        for( int i = 0; i < sizeDistribution.length; i++ )
        {
            if( sizeDistribution[i] != 0 )
            {
                System.out.println(i + " " + (double)sizeDistribution[i] / environment.getPopulationSize());
                data.add(i, (double)sizeDistribution[i] / environment.getPopulationSize());
            }
        }
        
//        dataCollection.addSeries(data);
//        JFreeChart chart = ChartFactory.createXYLineChart("Size Distribution", "Number of Nodes", "Frequency", dataCollection, PlotOrientation.VERTICAL, false, true, false);
//        XYPlot plot = chart.getXYPlot();
//        //plot.getRangeAxis().setRange(0, 1);
//        //plot.getRangeAxis().setMinorTickCount(1);
//        plot.setRangeAxis(new LogarithmicAxis(""));
//        JPanel panel = new ChartPanel(chart);
//        frame.add(panel, BorderLayout.CENTER);
//        frame.setSize(700, 500);
//        frame.setVisible(true);
    }
}
