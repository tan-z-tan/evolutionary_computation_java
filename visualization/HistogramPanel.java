package visualization;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class HistogramPanel extends JPanel
{
	private int[] _histgramData;
	private double _maxValue;
	
	public HistogramPanel()
	{
		_histgramData = new int[0];
	}
	
	public void paintComponent(Graphics g)
	{
		int height = getHeight();
		// clear
		g.setColor( Color.WHITE );
		g.fillRect(0, 0, getWidth(), getHeight() );
		
		// write histogram
		g.setColor( Color.BLACK );
		for( int i = 0; i < _histgramData.length; i++ )
		{
			g.drawLine( i + 1, height, i + 1, height - (int)(height * _histgramData[i] / _maxValue) );
		}
		setPreferredSize( new Dimension(_histgramData.length + 50, 50) );
	}
	
	/** returns histgram data as int[] */
	public int[] getHistgramData()
	{
		return _histgramData;
	}
	/** sets histgram data as int[] */
	public void setHistgramData(int[] histgramData)
	{
		_histgramData = histgramData;
		_maxValue = 1;
		for( int i = 0; i < _histgramData.length; i++ )
		{
			if( _histgramData[i] > _maxValue )
				_maxValue = _histgramData[i];
		}
	}

	public static void main(String[] args)
	{
		int[] testData = new int[5];
		testData[0] = 50;
		testData[1] = 20;
		testData[2] = 70;
		testData[3] = 139;
		testData[4] = 89;
		
		JFrame frame = new JFrame();
		HistogramPanel histPanel = new HistogramPanel();
		histPanel.setHistgramData( testData );
		
		frame.setSize( 500, 500 );
		frame.add(histPanel);
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setVisible( true );
	}
}
