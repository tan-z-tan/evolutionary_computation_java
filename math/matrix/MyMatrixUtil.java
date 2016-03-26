package math.matrix;

import java.util.List;

import org.apache.commons.math3.geometry.partitioning.Side;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import random.RandomManager;

public class MyMatrixUtil
{
    /** convert List"Double" to a RealMatrix.
     * returns n * 1 matrix (vector). */
    public static RealMatrix convertToRealMatrix(List<Double> data)
    {
        double[][] dataArray = new double[data.size()][1];
        for( int i = 0; i < data.size(); i++ )
        {
            dataArray[i][0] = data.get(i);
        }
        return MatrixUtils.createRealMatrix(dataArray);
    }
    
    public static RealMatrix convertToRealMatrix(double[] data)
    {
        double[][] dataArray = new double[data.length][1];
        for( int i = 0; i < data.length; i++ )
        {
            dataArray[i][0] = data[i];
        }
        return MatrixUtils.createRealMatrix(dataArray);
    }
	
    /** 要素ごとの積を計算し，返す */
	public static RealMatrix multiplyEntity(RealMatrix A, RealMatrix B)
	{
		RealMatrix R = MatrixUtils.createRealMatrix( A.getRowDimension(), A.getColumnDimension() );
		
		for( int i = 0; i < A.getRowDimension() ; i++ )
		{
			for( int j = 0; j < A.getColumnDimension() ; j++ )
			{
				R.setEntry(i, j, A.getEntry(i, j) * B.getEntry(i, j));
			}
		}
		
		return R;
	}
	
    /** 要素ごとの割り算を計算し，返す */
	public static RealMatrix divideEntity(RealMatrix A, RealMatrix B)
	{
		RealMatrix R = MatrixUtils.createRealMatrix( A.getRowDimension(), A.getColumnDimension() );
		
		for( int i = 0; i < A.getRowDimension() ; i++ )
		{
			for( int j = 0; j < A.getColumnDimension() ; j++ )
			{
				R.setEntry(i, j, A.getEntry(i, j) / B.getEntry(i, j));
			}
		}
		
		return R;
	}
	
	/** クロネッカー積を計算して返す． */
	public static RealMatrix kroneckerProduct(RealMatrix A, RealMatrix B)
	{
		RealMatrix R = MatrixUtils.createRealMatrix( A.getRowDimension() * B.getRowDimension(), A.getColumnDimension() * B.getColumnDimension() );
		
		for( int i = 0; i < A.getRowDimension() ; i++ )
		{
			for( int j = 0; j < B.getRowDimension() ; j++ )
			{
				for( int s = 0; s < A.getColumnDimension() ; s++ )
				{
					for( int k = 0; k < B.getColumnDimension() ; k++ )
					{
						int row = i * B.getRowDimension() + j;
						int column = s * B.getColumnDimension() + k;
						R.setEntry(row, column, A.getEntry(i, s) * B.getEntry(j, k));
					}
				}
			}
		}
		
		return R;
	}
	
	/** matrixAとmatrixBのbeta-divergenceを求める． */
	public static double betaDivergence(RealMatrix matrixA, RealMatrix matrixB, double beta)
	{
		double sum = 0;
		for( int r = 0; r < matrixA.getRowDimension(); r++ )
		{
			for( int c = 0; c < matrixB.getColumnDimension(); c++ )
			{
				double x = matrixA.getEntry(r, c);
				double y = matrixB.getEntry(r, c);
				double betaValue = 1.0 / (beta * (beta -1)) * (Math.pow(x, beta) + (beta -1) * Math.pow(y, beta) - beta * x * Math.pow(y, beta-1));
				if( !Double.isNaN(betaValue) )
				{
					sum += betaValue;
				}
			}
		}
		return sum;
	}
	
	/** matrixAとmatrixBのユークリッド距離を求める． */
	public static double euclidDistance(RealMatrix matrixA, RealMatrix matrixB)
	{
		return euclidDistance( matrixA.subtract(matrixB) );
	}
	
	/** matrixの0行列からのユークリッド距離を求める． */
	public static double euclidDistance(RealMatrix matrix)
	{
		double sum = 0;
		for( int r = 0; r < matrix.getRowDimension(); r++ )
		{
			for( int c = 0; c < matrix.getColumnDimension(); c++ )
			{
				sum += matrix.getEntry(r, c) * matrix.getEntry(r, c);
			}
		}
		return 	Math.sqrt( sum );
	}
	
	/** min - maxの間の乱数でマトリックスを埋める */
	public static void randomFill(RealMatrix matrix, double min, double max)
	{
		for( int i = 0; i < matrix.getRowDimension() ; i++ )
		{
			for( int j = 0; j < matrix.getColumnDimension(); j++ )
			{
				matrix.setEntry( i, j, RandomManager.getRandom(min, max) );
			}
		}
	}
	
	/**
	 * マトリックスの文字列表現を返す.１要素につき，8.3fで表示する．
	 * @param matrix
	 * @return
	 */
	public static String toString(RealMatrix matrix)
	{
		return toString( matrix, 8, 3 );
	}
	/**
	 * マトリックスの文字列表現を返す.elementLen (e), floatingLen (f)で１要素の長さを指定する．
	 * 例えばe=8, f=3だと printf("%8.3f", f)のように一つの要素が表示される．
	 * @param matrix
	 * @return
	 */
	public static String toString(RealMatrix matrix, int elementLen, int floatingLen)
    {
    	StringBuilder str = new StringBuilder();
    	for( int i = 0; i < matrix.getRowDimension(); i++ )
    	{
    		str.append("| ");
    		for( int j = 0; j < matrix.getColumnDimension(); j++ )
    		{
    			str.append( String.format("%" + elementLen + "." + floatingLen + "f", matrix.getEntry(i, j)) ).append(" ");
    		}
    		str.append("|").append("\n");
    	}
    	return str.toString();
    }

	public static RealMatrix createRealMatrix(List data)
	{
		double[][] elements = new double[data.size()][];
		
		for( int r = 0; r < data.size(); r++ )
		{
			Object row = data.get(r);
			double[] rowData = null;
			if( row instanceof double[] )
			{
				rowData = new double[((double[])row).length];
				for( int c = 0; c < rowData.length; c++ )
				{
					rowData[c] = ((double[])row)[c];
				}
			} else if (row instanceof List )
			{
				rowData = new double[((List)row).size()];
				for( int c = 0; c < rowData.length; c++ )
				{
					rowData[c] = (Double)((List)row).get(c);
				}
			}
			elements[r] = rowData;
		}
		
		RealMatrix matrix = MatrixUtils.createRealMatrix( elements );
		
		return matrix;
	}
}
