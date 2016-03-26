package math.stchastics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.commons.math3.distribution.GammaDistribution;
import org.apache.commons.math3.special.Gamma;

public class DirichletDistribution
{
	public static void main(String[] args)
	{
		double[] alpha = new double[]{1, 5, 10};
		final List<double[]> sampleList = new ArrayList<double[]>();
		final int gridSize = 10;
		final double[][] probabilityList = new double[gridSize][];
		
		for( int i = 0; i < 1000; i++ )
		{
			double[] sample = sample(alpha);
			//System.out.println( Arrays.toString(sample) );
			sampleList.add(sample);
		}
		
		double margin = 0.00001;
		for( int i = 0; i < gridSize; i++ )
		{
			double x_1 = margin + i / (gridSize - margin*2);
			probabilityList[i] = new double[11-i];
			for( int j = 0; j < gridSize-i; j++ )
			{
				double x_2 = margin + j / (gridSize - margin*2);
				//double x_2 = j / 10.0  + Double.MIN_NORMAL;
				double x_3 = 1 - x_1 - x_2;
				double p = Math.exp( pDirichletLN(new double[]{x_1, x_2, x_3}, alpha) );
				probabilityList[i][j] = p;
			}
		}
		
		JPanel panel = new JPanel(){
			public void paintComponent(Graphics g)
			{
				((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
				g.setColor(Color.WHITE);
				g.fillRect(0, 0, getWidth(), getHeight());
				
				g.setColor( Color.RED );
				for(double[] sample: sampleList)
				{
					g.drawArc((int)(sample[0] * getWidth()), (int)(sample[1] * getHeight()), 2, 2, 1, 360);
				}
				
				g.setColor( new Color(0.0f, 0.0f, 1.0f, 0.5f) );
				for( int i = 0; i < probabilityList.length; i++ )
				{
					double x_1 = i / (double)gridSize;
					for( int j = 0; j < probabilityList[i].length; j++ )
					{
						double x_2 = j / (double)gridSize;
						int circle_w = (int)(5000 * Math.sqrt(probabilityList[i][j])) / getWidth() + 2;
						int circle_h = (int)(5000 * Math.sqrt(probabilityList[i][j])) / getHeight() + 2;
						g.fillArc((int)(x_1 * getWidth() - circle_w /2), (int)(x_2 * getHeight() - circle_h /2), circle_w, circle_h, 0, 360);
					}
				}
			}
		};
		
		JFrame frame = new JFrame();
		frame.add(panel);
		frame.setVisible(true);
		frame.setSize(500 ,500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	/** パラメータがalphaに従うディリクレ分布からサンプリングを行う */
	public static double[] sample(double[] alpha)
	{
		double[] sample = new double[alpha.length];
		
		double sum = 0;
		for( int i = 0; i < alpha.length; i++ )
		{
			GammaDistribution gamma_i = new GammaDistribution(alpha[i], 1);
			double x_i = gamma_i.sample();
			sample[i] = x_i;
			sum += x_i;
		}
		for( int i = 0; i < alpha.length; i++ )
		{
			sample[i] = sample[i] / sum;
		}
		
		return sample;
	}
	
	/**
	 * パラメータalphaのディリクレ分布の対数確率を返す．
	 * 未チェック
	 * @param x
	 * @param alpha
	 * @return
	 */
	public static double pDirichletLN(double[] x, double[] alpha)
	{
		double p = 0;
		double b = 0;
		
		for(int i=0;i<alpha.length;i++){
			b += Gamma.logGamma(alpha[i]);
		}
		double c=0;
		for(int i=0;i<alpha.length;i++){
			c += alpha[i];
		}
		double d = Gamma.logGamma(c);
		
		double g=0;
		for(int i=0;i<alpha.length;i++){
			g += Math.log(x[i]) * (alpha[i]-1);
		}
		p = g - b + d;
		if( Double.isNaN(p) )
		{
			p = 0;
		}
		
		return p;
	}
}