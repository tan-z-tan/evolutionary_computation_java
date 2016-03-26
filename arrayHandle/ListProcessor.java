package arrayHandle;

import java.util.ArrayList;
import java.util.List;

public class ListProcessor
{
	/** returns a newly created list that contains (listA + listB) */
	public static List<Double> plus(List<Double> listA, List<Double> listB)
	{
		if( listA.size() != listB.size() )
		{
			throw new IllegalArgumentException();
		}
		
		List<Double> list = new ArrayList<Double>();
		for( int i = 0; i < listA.size(); i++ )
		{
			list.add( listA.get(i) + listB.get(i) );
		}
		
		return list;
	}

	/** argument list is modified. */
	public static void divide(List<Double> list, double z)
	{
		for( int i = 0; i < list.size(); i++ )
		{
			list.set(i, list.get(i)/z);
		}
	}
	
	public static double Σ(List<Double> list)
	{
		double sum = 0;
		for( int i = 0; i < list.size(); i++ )
		{
			sum += list.get(i);
		}
		return sum;
	}
}
