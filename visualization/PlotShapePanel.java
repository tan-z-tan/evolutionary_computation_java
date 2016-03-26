package visualization;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * 
 * @author makoto tanji
 */
public class PlotShapePanel extends JPanel
{
  private static final long serialVersionUID = 1L;
  public static final int DEFAULT_POINT_SIZE = 10;

  private int _pointSize;
  private List<ColorShape> _plotData;

  /** constructs empty PlotPanel2D */
  public PlotShapePanel()
  {
    _plotData = new ArrayList<ColorShape>();
    _pointSize = DEFAULT_POINT_SIZE;
  }

  /**
   * constructs PlotPanel2D which display data.
   * 
   * @param plotData
   *          : ArrayList<Point2D> to be displayed
   */
  public PlotShapePanel(List<ColorShape> plotData)
  {
    _plotData = plotData;
    _pointSize = DEFAULT_POINT_SIZE;
  }

  public void paintComponent(Graphics g)
  {
    Graphics2D g2 = (Graphics2D)g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
    // clear
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, getWidth(), getHeight());

    if (_plotData.size() == 0)
      return;
    // plot
    g.setColor(Color.BLACK);

    // gets maximum and minimum values for X and Y
    double maxX = Double.MIN_VALUE;
    double minX = Double.MAX_VALUE;
    double maxY = Double.MIN_VALUE;
    double minY = Double.MAX_VALUE;
    for( ColorShape shape: _plotData )
    {
      if( shape.getShape().getBounds().x > maxX )
      {
        maxX = shape.getShape().getBounds().x;
      }
      else if( shape.getShape().getBounds().x < minX )
      {
        minX = shape.getShape().getBounds().x;
      }
      if( shape.getShape().getBounds().y > maxY )
      {
        maxY = shape.getShape().getBounds().y;
      }
      else if( shape.getShape().getBounds().y < minY )
      {
        minY = shape.getShape().getBounds().y;
      }
    }
    double widthRatio = getWidth() / (maxX - minX);
    double heightRatio = getHeight() / (maxY - minY);

    for (ColorShape shape: _plotData)
    {
      g2.setColor(shape.getColor());
      g2.fill(shape.getShape());
    }
    g.drawString(String.valueOf(minX), 20, getHeight() - 10);
    g.drawString(String.valueOf(minY), 10, getHeight() - 40);
    g.drawString(String.valueOf(maxX), getWidth() - 30, getHeight() -10);
    g.drawString(String.valueOf(maxY), 10, 10);
  }

  /** returns plot Data */
  public List<ColorShape> getPlotData()
  {
    return _plotData;
  }
  
  /** sets plot Data */
  public void setPlotData(List<ColorShape> plotData)
  {
    _plotData = plotData;
  }

  /** sets plot Data */
  public void addPlotData(ColorShape shape)
  {
    _plotData.add(shape);
  }

  public static void main(String[] args)
  {
    List<ColorShape> testData = new ArrayList<ColorShape>();
    testData.add(new ColorShape(new Rectangle2D.Double(40, 50, 100, 20), Color.RED));
    testData.add(new ColorShape(new Arc2D.Double(200, 100, 30, 30, 0, 300, Arc2D.PIE), Color.BLACK));
    
    JFrame frame = new JFrame();
    PlotShapePanel panel = new PlotShapePanel();
    panel.setPlotData(testData);
    frame.setSize(500, 500);
    frame.add(panel);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
  }
}
