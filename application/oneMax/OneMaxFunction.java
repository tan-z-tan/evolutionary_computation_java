package application.oneMax;

import java.util.List;

public class OneMaxFunction
{
	public static double evaluate(Integer[] gene)
	{
		double value = 0;
		for( int i = 0; i < gene.length; i++ )
		{
			value += gene[i];
		}
		return value;
	}
	
	public static double evaluate(List<Integer> gene)
	{
		double value = 0;
		for( int i = 0; i < gene.size(); i++ )
		{
			value += gene.get(i);
		}
		return value;
	}
}
