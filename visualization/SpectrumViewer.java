package visualization;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;

public class SpectrumViewer extends JComponent
{
  private List<double[]> _plotData;
  private BufferedImage _image;
  private double xRatio = 1;
  private double yRatio = 1;
  private String[] indexNameMapX;
  private String[] indexNameMapY;
  
  /** data is column based like that (() () () ()) */
  public SpectrumViewer(double[][] data, double xRatio, double yRatio)
  {
	  int w = data.length;
	  int h = data[0].length;
	  
	  _plotData = new ArrayList<double[]>();
	  for( int i = 0; i < w; i++ )
	  {
		  double[] line = new double[h];
		  for( int j = 0; j < h; j++ )
		  {
			  line[j] = data[i][j];
		  }
		  _plotData.add( line );
	  }
	  this.xRatio = xRatio;
	  this.yRatio = yRatio;
	  initialize();
  }
  
  /** data is column based like that (() () () ()) */
  public SpectrumViewer(double[][] data)
  {
	  this(data, 1, 1);
  }
  
  /** data is column based like that (() () () ()) */
  public SpectrumViewer(List<double[]> data)
  {
	  this( data, 1, 1, null, null );
  }
  
  /** data is column based like that (() () () ()) */
  public SpectrumViewer(List<double[]> data, double xRatio, double yRatio, String[] indexNameMapX, String[] indexNameMapY )
  {
    _plotData = data;
    this.xRatio = xRatio;
    this.yRatio = yRatio;
    this.indexNameMapX = indexNameMapX;
    this.indexNameMapY = indexNameMapY;
    
    initialize();
  }
  
  
  private void initialize()
  {
    // create image
    int maxY = 0;
    double maxValue = 0;
    
    for (int i = 0; i < _plotData.size(); i++)
    {
      for (int j = 0; j < _plotData.get(i).length; j++)
      {
        if( maxValue < _plotData.get(i)[j])
        {
          maxValue = _plotData.get(i)[j];
        }
      }
      if (_plotData.get(i).length > maxY)
      {
        maxY = _plotData.get(i).length;
      }
    }
    System.out.println("maxY = " + maxY);
    System.out.println("maxValue = " + maxValue);
    System.out.println("size = " + _plotData.size());
    _image = new BufferedImage(_plotData.size(), maxY, BufferedImage.TYPE_INT_ARGB);
    System.out.println("Image (w, h) = " + _image.getWidth() + " " + _image.getHeight());
    WritableRaster raster = _image.getRaster();
    
    for (int i = 0; i < _plotData.size(); i++)
    {
      for (int j = 0; j < _plotData.get(i).length; j++)
      {
        Color color;
        double value = _plotData.get(i)[j] / maxValue;
        //value = (float) Math.sqrt(value);
        value *= 3;
        value = Math.min(3, value);
        int intValue = (int) (value * 255);
        int[] colorArray;
        if (value <= 1)
        {
          colorArray = new int[] { 0, 0, intValue, 255 };
        } else if (value <= 2)
        {
          colorArray = new int[] { 0, intValue - 255, 255, 255 };
        } else
        {
          colorArray = new int[] { intValue - 255 * 2, 255, 255, 255 };
        }
        colorArray = new int[] {
            (int)Math.max(0, Math.min(255, (value - 0) * 255)),
            (int)Math.max(0, Math.min(255, (value - 1) * 255)),
            (int)Math.max(0, Math.min(255, (value - 2) * 255)), 255 };
        
        raster.setPixel(i, _image.getHeight() - j - 1, colorArray);
      }
    }
    
    // mouse listener
    addMouseListener(new MouseListener(){
      @Override
      public void mouseClicked(MouseEvent e)
      {
        if( _plotData != null )
        {
          int xPos = (int)(e.getX() / xRatio);
          int yPos = _image.getHeight() - (int)(e.getY() / yRatio) -1;
          if( xPos < _plotData.size() && yPos < _plotData.get(0).length )
          {
        	  String xName = String.valueOf(xPos);
        	  String yName = String.valueOf(yPos);
        	  if( indexNameMapX != null && indexNameMapX.length > xPos ) {
        		  xName = indexNameMapX[xPos];
        	  }
        	  if( indexNameMapY != null && indexNameMapY.length > yPos ) {
        		  yName = indexNameMapY[yPos];
        	  }
            System.out.println( "(" + xName + "," + yName + ") = " + _plotData.get(xPos)[yPos] );
          }
        }
      }
      @Override
      public void mouseEntered(MouseEvent e)
      { }
      @Override
      public void mouseExited(MouseEvent e)
      { }
      @Override
      public void mousePressed(MouseEvent e)
      { }
      @Override
      public void mouseReleased(MouseEvent e)
      { }
    });
    addMouseMotionListener(new MouseMotionListener(){
      @Override
      public void mouseDragged(MouseEvent e)
      { }
      @Override
      public void mouseMoved(MouseEvent e)
      {
       
      }
    });
  }

  public void paintComponent(Graphics g)
  {
    // clear
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, getWidth(), getHeight());

    if (_image == null)
    {
      return;
    }

    g.drawImage(_image, 0, 0, (int) (_image.getWidth() * xRatio), (int) (_image.getHeight() * yRatio), this);
    this.setPreferredSize(new Dimension((int) (_image.getWidth() * xRatio), (int) (_image.getHeight() * yRatio)));
  }

  public double getXRatio()
  {
    return xRatio;
  }

  public void setXRatio(double ratio)
  {
    xRatio = ratio;
  }

  public double getYRatio()
  {
    return yRatio;
  }

  public void setYRatio(double ratio)
  {
    yRatio = ratio;
  }

  public List<double[]> getPlotData()
  {
    return _plotData;
  }

  public void setPlotData(List<double[]> plotData)
  {
    _plotData = plotData;
  }
  
  public static void showSpectrogram(List<double[]> data, double xRatio, double yRatio, String[] xName, String[] yName) {
	  SpectrumViewer viewer = new SpectrumViewer(data, xRatio, yRatio, xName, yName);
	  
	  JFrame frame = new JFrame();
	  frame.add(viewer);
	  frame.setPreferredSize(new Dimension((int)(data.get(0).length * xRatio), (int)(data.size() * yRatio)));
	  frame.setVisible(true);
  }
  
  public static void showSpectrogram(double[][] data, double xRatio, double yRatio, String[] xName, String[] yName) {
	  SpectrumViewer viewer = new SpectrumViewer(data, xRatio, yRatio);
	  viewer.indexNameMapX = xName;
	  viewer.indexNameMapY = yName;
	  
	  JFrame frame = new JFrame();
	  frame.add(viewer);
	  frame.setPreferredSize(new Dimension((int)(data[0].length * xRatio), (int)(data.length * yRatio)));
	  frame.setVisible(true);
  }
}
