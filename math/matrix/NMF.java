package math.matrix;


import java.util.Arrays;

import javax.security.sasl.RealmCallback;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

/**
 * Non-Negative Matrix Factorizationを行うクラス．
 * 誤差関数はユークリッド距離が一般的（性能はわからない）．
 * 誤差関数がβダイバージェンスの場合は，Wを更新しない．前もって辞書ファイルで学習しておくこと．（ISMIR2011の方法より）
 * @author tanji
 */
public class NMF
{
	private RealMatrix V; // n * t
	private RealMatrix W; // n * r
	private RealMatrix H; // r * t
	private int n; // #{frequencies}
	private int r; // #{note templates}
	private int t; // time
	private double beta = 0.5;
	
	private int updateType = BETA_DIVERGENCE;
	
	public static final int EUCLIDEAN = 0;
	public static final int BETA_DIVERGENCE = 1;
	
	public NMF(RealMatrix V, int r)
	{
		this(V, r, EUCLIDEAN);
	}
	
	public NMF(RealMatrix V, int r, int type)
	{
		this.V = V;
		this.n = V.getRowDimension();
		this.t = V.getColumnDimension();
		this.r = r;
		this.updateType = type;
		
		W = MatrixUtils.createRealMatrix(n, r); // n * r
		H = MatrixUtils.createRealMatrix(r, t); // r * m
		MyMatrixUtil.randomFill( W, 0.1, 1 );
		MyMatrixUtil.randomFill( H, 0.1, 1 );
	}
	
	public double getError()
	{
		if( this.updateType == EUCLIDEAN ) {
			return MyMatrixUtil.euclidDistance( V, W.multiply(H) );
		} else if( this.updateType == BETA_DIVERGENCE ) {
			return MyMatrixUtil.betaDivergence( V, W.multiply(H), beta );
		}
		return MyMatrixUtil.euclidDistance( V, W.multiply(H) );
	}
	
	public void update()
	{
		H = update_H();
		W = update_W();
	}
	
	public RealMatrix update_H()
	{
		if( this.updateType == EUCLIDEAN ) {
			return update_H_euclidian();
		} else if( this.updateType == BETA_DIVERGENCE ) {
			return update_H_betaDivergence();
		} else {
			return null;
		}
	}
	
	public RealMatrix update_H_betaDivergence()
	{
		// H
		double[][] updateH = new double[this.r][this.t];
		
		RealMatrix W_t = W.transpose();
		RealMatrix WH = W.multiply(H);
		RealMatrix WH_beta_1 = MatrixUtils.createRealMatrix(n, t);
		RealMatrix WH_beta_2 = MatrixUtils.createRealMatrix(n, t);
		
		for(int r = 0; r < WH.getRowDimension(); r++ )
		{
			for(int c = 0; c < WH.getColumnDimension(); c++ )
			{
				WH_beta_1.setEntry(r, c, Math.pow(WH.getEntry(r, c), beta-1));
				WH_beta_2.setEntry(r, c, Math.pow(WH.getEntry(r, c), beta-2));
			}
		}
		
		for( int t = 0; t < this.t; t++ )
		{
			RealMatrix Wh_beta_1 = WH_beta_1.getColumnMatrix(t);
			RealMatrix Wh_beta_2 = WH_beta_2.getColumnMatrix(t);
			RealMatrix v = V.getColumnMatrix(t);
			
			//RealMatrix Wh_v = MatrixUtils.createRealMatrix(1, v.getColumnDimension());
			RealMatrix Wh_v = MatrixUtils.createRealMatrix(v.getRowDimension(), 1);
			for( int c = 0; c < v.getRowDimension(); c++ )
			{
				Wh_v.setEntry(c, 0, Wh_beta_2.getEntry(c, 0) * v.getEntry(c, 0));
			}
			RealMatrix numerator = W_t.multiply(Wh_v);
			RealMatrix denominator = W_t.multiply(Wh_beta_1);
			
			for( int row = 0; row < H.getRowDimension(); row++ )
			{
				if( !Double.isNaN(numerator.getEntry(row, 0)) && !Double.isNaN(denominator.getEntry(row, 0)) )
				{
					updateH[row][t] = H.getEntry(row, t) * numerator.getEntry(row, 0) / denominator.getEntry(row, 0);
				} else {
					updateH[row][t] = H.getEntry(row, t);
				}
			}
		}
		H.setSubMatrix(updateH, 0, 0);
		
		return H;
	}
	
	public RealMatrix update_H_euclidian()
	{
		// H
		RealMatrix denomH = W.transpose().multiply(W).multiply(H);
		RealMatrix numeratorH = W.transpose().multiply(V);
		
		RealMatrix newH = MyMatrixUtil.multiplyEntity(H, MyMatrixUtil.divideEntity(numeratorH, denomH));
		return newH;
	}
	
	public RealMatrix update_W()
	{
		if( this.updateType == EUCLIDEAN ) {
			return update_W_euclidian();
		} else if( this.updateType == BETA_DIVERGENCE ) {
			return W;
		} else {
			return null;
		}
	}
	
	public RealMatrix update_W_euclidian()
	{
		// W
		RealMatrix denomW = W.multiply(H).multiply(H.transpose());
		RealMatrix numeratorW = V.multiply(H.transpose());
		
		RealMatrix newH = MyMatrixUtil.multiplyEntity(W, MyMatrixUtil.divideEntity(numeratorW, denomW));
		return newH;
	}
	
	public RealMatrix getV()
	{
		return V;
	}

	public void setV(RealMatrix v)
	{
		V = v;
	}

	public RealMatrix getW()
	{
		return W;
	}

	public void setW(RealMatrix w)
	{
		W = w;
	}

	public RealMatrix getH()
	{
		return H;
	}

	public void setH(RealMatrix h)
	{
		H = h;
	}
	
	public double getBeta()
	{
		return beta;
	}

	public void setBeta(double beta)
	{
		this.beta = beta;
	}
	
	public static void main(String[] args)
	{
		double[][] dataV = new double[][]{
				{1,1,2,3,2},
				{0,2,2,2,0},
				{1,3,4,5,2},
				{0,4,4,4,0}
		};
		
		int r = 2;
		
		RealMatrix V = MatrixUtils.createRealMatrix(dataV); // n * m
		
//		/NMF nmf = new NMF(V, r, BETA_DIVERGENCE);
		NMF nmf = new NMF(V, r, EUCLIDEAN);
		nmf.setW( MatrixUtils.createRealMatrix(new double[][]{{1, 1}, {0, 2}, {1, 3}, {0, 4}}) );
		
		for( int i = 0; i < 200; i++ )
		{
			nmf.update();
			RealMatrix WH = nmf.getW().multiply(nmf.getH());
			nmf.setW( MatrixUtils.createRealMatrix(new double[][]{{1, 1}, {0, 2}, {1, 3}, {0, 4}}) );
			
			System.out.println( "--------------------- Update " + i + "  " + MyMatrixUtil.euclidDistance(V, WH) );
			System.out.println( "W =" );System.out.println( MyMatrixUtil.toString(nmf.getW()) );
			System.out.println( "H =" );System.out.println( MyMatrixUtil.toString(nmf.getH()) );
			System.out.println( "WH =" );System.out.println( MyMatrixUtil.toString(WH) );
		}
	}
}
