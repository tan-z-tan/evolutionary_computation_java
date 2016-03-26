package arrayHandle;

public class KMP
{
	private static int[] calculateNextTable(int[] key)
	{
		int[] nextTable = new int[key.length];
		int i = 0;
		int j = -1;
		nextTable[0] = -1;
		
		while( i < key.length - 1 )
		{
			if( j < 0 || key[i] == key[j] )	{
				nextTable[++i] = ++j;
			}
			else j = nextTable[j];
		}
		return nextTable;
	}
	
	public static int getKMP_MaxLength(int[] targetA, int[] targetB)
	{
		int maxLength = 0;
		if( targetA.length >= targetB.length ) // targetB is shorter than targetA
		{
			if( targetB.length == 0 || targetA.length == 0 )return 0;
			
			int[] nextTabel = KMP.calculateNextTable( targetB );
			// start
			int i = 0;
			int j = 0;
			int k;
			
			while( i < targetA.length && j < targetB.length )
			{
				if( j < 0 || targetA[i] == targetB[j] )
				{
					i++;
					j++;
					if( j > maxLength )
					{
						maxLength = j;
					}
				}
				else j = nextTabel[j];
			}
			return maxLength;
		}
		else
		{
			return getKMP_MaxLength( targetB, targetA );
		}
	}
	
	public static void main(String args[])
	{
		int[] testText = {1, 2, 3, 4, 5, 3, 4, 1, 2, 3, 5, 8};
		int[] testPattern = {1, 2, 3, 5, 8};
		getKMP_MaxLength( testText, testPattern );
	}
}
