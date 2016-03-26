package visualization;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * 
 * @author makoto tanji
 */
public class PlotPanel2D extends JPanel
{
    private static final long serialVersionUID = 1L;
    public static final int DEFAULT_POINT_SIZE = 6;
    public static final int PLOT_TYPE_LINE = 1;
    public static final int PLOT_TYPE_POINT = 2;
    public static final int PLOT_TYPE_RECTANGLE = 4;
    
    private int _pointSize;
    private List<List<Point2D>> _plotData;
    private List<Integer> _plotType;
    private double margin = 0.05;

    /** constructs empty PlotPanel2D */
    public PlotPanel2D()
    {
        _plotData = new ArrayList<List<Point2D>>();
        _plotType = new ArrayList<Integer>();
        _pointSize = DEFAULT_POINT_SIZE;
    }

    /**
     * constructs PlotPanel2D which display data.
     * 
     * @param plotData
     *            : ArrayList<Point2D> to be displayed
     */
    public PlotPanel2D(List<Point2D> plotData)
    {
        this();
        _plotData.add(plotData);
        _plotType.add(PLOT_TYPE_LINE);
        _pointSize = DEFAULT_POINT_SIZE;
    }

    /**
     * constructs PlotPanel2D which display data.
     * 
     * @param plotData
     *            : ArrayList<Point2D> to be displayed
     */
    public PlotPanel2D(List<List<Point2D>> plotData, int pointSize)
    {
        _plotData = plotData;
        _plotType = Arrays.asList(new Integer[_plotData.size()]);
        Collections.fill(_plotType, PLOT_TYPE_LINE);
        _pointSize = pointSize;
    }

    public void paintComponent(Graphics g)
    {
        // clear
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (_plotData.size() == 0)
            return;
        
        // plot
        g2.setColor(Color.RED);

        // gets maximum and minimum values for X and Y
        double maxX = Double.MIN_VALUE;
        double minX = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE;
        for (int i = 0; i < _plotData.size(); i++)
        {
            for (int j = 0; j < _plotData.get(i).size(); j++)
            {
                Point2D dataPoint = _plotData.get(i).get(j);
                if (dataPoint.getX() > maxX)
                    maxX = dataPoint.getX();
                if (dataPoint.getX() < minX)
                    minX = dataPoint.getX();
                if (dataPoint.getY() > maxY)
                    maxY = dataPoint.getY();
                if (dataPoint.getY() < minY)
                    minY = dataPoint.getY();
            }
        }
        double width = (maxX - minX);
        double height = (maxY - minY);
        maxX += width * margin;
        minX -= width * margin;
        maxY += height * margin;
        minY -= height * margin;

        double widthRatio = getWidth() / (maxX - minX);
        double heightRatio = getHeight() / (maxY - minY);

        for (int index = 0; index < _plotData.size(); index++)
        {
        	if (index % 8 == 0)
            {
                g2.setColor(Color.RED);
            } else if (index % 8 == 1)
            {
                g2.setColor(Color.BLUE);
            } else if (index % 8 == 2)
            {
                g2.setColor(Color.GREEN);
            } else if (index % 8 == 3)
            {
                g2.setColor(Color.CYAN);   
            } else if (index % 8== 4)
            {
                g2.setColor(Color.MAGENTA);
            } else if (index % 8 == 5)
            {
                g2.setColor(Color.orange);
            } else if (index % 8 == 6)
            {
                g2.setColor(Color.GRAY);
            } else if (index % 8 == 7)
            {
                g2.setColor(Color.YELLOW);
            }
        	
            List<Point2D> oneData = _plotData.get(index);
            if (oneData.size() == 0)
            {
                continue;
            }
            //if( _plotType.get(index) == PLOT_TYPE_POINT )
            if( (_plotType.get(index) & PLOT_TYPE_POINT) == PLOT_TYPE_POINT )
            {
                Point2D dataPoint = oneData.get(0);
                g2.fillOval((int)((dataPoint.getX() - minX) * widthRatio - _pointSize / 2.0), getHeight() - (int)((dataPoint.getY() - minY) * heightRatio + _pointSize / 2.0), _pointSize, _pointSize);
            }
            Point2D lastPoint = oneData.get(0);
            for (int i = 1; i < oneData.size(); i++)
            {
                Point2D dataPoint = oneData.get(i);
                //if( _plotType.get(index) == PLOT_TYPE_LINE )
                if( (_plotType.get(index) & PLOT_TYPE_LINE) == PLOT_TYPE_LINE )
                {
                    g2.drawLine((int) ((lastPoint.getX() - minX) * widthRatio), getHeight() - (int) ((lastPoint.getY() - minY) * heightRatio),
                            (int) ((dataPoint.getX() - minX) * widthRatio), getHeight() - (int) ((dataPoint.getY() - minY) * heightRatio));    
                }
                //else if( _plotType.get(index) == PLOT_TYPE_POINT )
                if( (_plotType.get(index) & PLOT_TYPE_POINT) == PLOT_TYPE_POINT )
                {
                    //System.out.println("i = " + i + " " + dataPoint);
                	g2.fillOval((int)((dataPoint.getX() - minX) * widthRatio - _pointSize / 2.0), getHeight() - (int)((dataPoint.getY() - minY) * heightRatio + _pointSize / 2.0), _pointSize, _pointSize);
                }
                lastPoint = dataPoint;
            }
        }
        g2.setColor(Color.BLACK);
        g2.drawString(String.valueOf(minX), 20, getHeight() - 10);
        g2.drawString(String.valueOf(minY), 10, getHeight() - 40);
        g2.drawString(String.valueOf(maxX), getWidth() - 30, getHeight() - 10);
        g2.drawString(String.valueOf(maxY), 10, 10);
    }

    public int getPointSize()
    {
        return _pointSize;
    }

    public void setPointSize(int pointSize)
    {
        _pointSize = pointSize;
    }

    /** returns plot Data */
    public List<List<Point2D>> getPlotData()
    {
        return _plotData;
    }

    /** sets plot Data */
    public void setPlotData(int index, double[] xData, double[] yData)
    {
        if (xData.length != yData.length)
        {
            return;
        }
        List<Point2D> plotData = new ArrayList<Point2D>(xData.length);
        for (int i = 0; i < xData.length; i++)
        {
            plotData.add(new Point2D.Double((Double) xData[i], (Double) yData[i]));
        }
        _plotData.set(index, plotData);
    }
    
    /** sets plot Data */
    public void addPlotData(List<Double> xData, List<Double> yData, int plotType)
    {
        if (xData.size()!= yData.size())
        {
            return;
        }
        List<Point2D> plotData = new ArrayList<Point2D>(xData.size());
        for (int i = 0; i < xData.size(); i++)
        {
            plotData.add(new Point2D.Double((Double) xData.get(i), (Double) yData.get(i)));
        }
        _plotData.add(plotData);
        _plotType.add(plotType);
    }
    
    /** sets plot Data */
    public void addPlotData(Double[] xData, Double[] yData, int plotType)
    {
        if (xData.length != yData.length)
        {
            return;
        }
        List<Point2D> plotData = new ArrayList<Point2D>(xData.length);
        for (int i = 0; i < xData.length; i++)
        {
            plotData.add(new Point2D.Double((Double) xData[i], (Double) yData[i]));
        }
        _plotData.add(plotData);
        _plotType.add(plotType);
    }
    
    /** sets plot Data */
    public void addPlotData(double[] xData, double[] yData, int plotType)
    {
        if (xData.length != yData.length)
        {
            return;
        }
        List<Point2D> plotData = new ArrayList<Point2D>(xData.length);
        for (int i = 0; i < xData.length; i++)
        {
            plotData.add(new Point2D.Double((Double) xData[i], (Double) yData[i]));
        }
        _plotData.add(plotData);
        _plotType.add(plotType);
    }

    /** sets plot Data */
    public void setPlotData(List<List<Point2D>> plotData, List<Integer> plotType)
    {
        _plotData = plotData;
        _plotType = plotType;
    }

    /** sets plot Data */
    public void addPlotData(List<Point2D> plotData)
    {
        addPlotData(plotData, PLOT_TYPE_LINE);
    }

    /** sets plot Data */
    public void addPlotData(List<Point2D> plotData, int plotType)
    {
        _plotData.add(plotData);
        _plotType.add(plotType);
    }

    public static void main(String[] args)
    {
        List<Point2D> testData1 = new ArrayList<Point2D>();
        List<Point2D> testData2 = new ArrayList<Point2D>();
        List<List<Point2D>> data = new ArrayList<List<Point2D>>();
        data.add(testData1);
        data.add(testData2);
        testData1.add(new Point2D.Double(30, 20));
        testData1.add(new Point2D.Double(73, 20));
        testData1.add(new Point2D.Double(22, 40));
        testData1.add(new Point2D.Double(63, 57));
        testData1.add(new Point2D.Double(82, 26));
        testData1.add(new Point2D.Double(23, 56));
        testData1.add(new Point2D.Double(-100, 105));
        testData2.add(new Point2D.Double(100, 450));
        testData2.add(new Point2D.Double(-73, 202));
        testData2.add(new Point2D.Double(-22, 402));
        testData2.add(new Point2D.Double(63, 257));
        testData2.add(new Point2D.Double(-82, 26));
        testData2.add(new Point2D.Double(23, 56));
        testData2.add(new Point2D.Double(-50, 5));

        JFrame frame = new JFrame();
        PlotPanel2D panel = new PlotPanel2D();
        //panel.setPlotData(data, Arrays.asList(PLOT_TYPE_LINE, PLOT_TYPE_LINE) );
        //panel.setPlotData(data, Arrays.asList(PLOT_TYPE_LINE, PLOT_TYPE_POINT) );
        panel.addPlotData(testData1, PLOT_TYPE_LINE);
        panel.addPlotData(testData1, PLOT_TYPE_POINT);
        frame.setSize(500, 500);
        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
