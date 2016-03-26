package math.distance;

public class SpearmanRankCorrelation
{
	/** returns correlation value -1.0 to 1.0 */
	public static double correlation(int[] rankA, int[] rankB)
	{
		int n = rankA.length;
		double error = 0;
		for( int i = 0; i < n; i++ )
		{
			error += Math.pow(rankA[i] - rankB[i], 2);
		}
		return 1 - (6 * error) / (n * (n * n - 1));
	}
	
	public static void main(String[] args)
	{
		System.out.println( correlation(new int[]{0,1,2}, new int[]{0,1,2}) );
		System.out.println( correlation(new int[]{0,1,2}, new int[]{2,1,0}) );
		System.out.println( correlation(new int[]{1,2,0}, new int[]{1,0,2}) );
		System.out.println( correlation(new int[]{0,1,2}, new int[]{1,0,2}) );
		System.out.println( correlation(new int[]{1,2,0}, new int[]{0,2,1}) );
	}
}
