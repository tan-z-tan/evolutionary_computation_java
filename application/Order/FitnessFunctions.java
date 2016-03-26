package application.Order;

import geneticProgramming.GpNode;
import geneticProgramming.symbols.SymbolType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FitnessFunctions
{
	/**
	 * ORDER tree function
	 */
	public static double fitnessFunction_OrderTree(GpNode rootNode, int level)
	{
		List<GpNode> list = Arrays.asList(new GpNode[level]);
		Collections.fill(list, null);		
		inorderTraverse(rootNode, list);
		
		double fitness = 0;
		
		for( int i = 0; i < list.size(); i++ )
		{
			if( list.get(i) == null ) continue;
			
			String name = list.get(i).getNodeType().getSymbolName();
			if( name.charAt(0) == '*' )
			{
				//nodeOutput[index-1] = -1;
			}
			if( name.charAt(0) == 'x' )
			{
				fitness++;
				//nodeOutput[index-1] = 1;
			}
		}
		
		return fitness;
	}
	
	/**
	 * ORDER tree function
	 */
	public static double fitnessFunction_DeceptiveOrderTree(GpNode rootNode, int level, int k, double delta)
	{
		List<GpNode> list = Arrays.asList(new GpNode[level]);
		Collections.fill(list, null);
		inorderTraverse(rootNode, list);
		
		double fitness = 0;
		
		for( int i = 0; i < level / k; i++ )
		{
			double u = 0;
			for( int j = k * i; j < k * i + k; j++ )
			{
				if( list.get(j) == null )
				{
					continue;
				}
				if( list.get(j).getNodeType().getSymbolName().startsWith("x") )
				{
					u++;
				}
			}
			if( u== k )
			{
				fitness += 1;
			}
			else
			{
			    fitness += (1.0 - delta) * (1 - u / (k - 1));
			}
		}
		
//		for( int i = 0; i < list.size(); i++ )
//		{
//			if( list.get(i) == null ) continue;
//			
//			String name = list.get(i).getNodeType().getSymbolName();
//			
//			if( name.charAt(0) == 'x' )
//			{
//				fitness++;
//			}
//		}
		
		return fitness;
	}
	
	/** Inorder Traverse.
	 * returns only terminal nodes
	 */
	public static void inorderTraverse(GpNode node, List<GpNode> list)
	{
		if( node.isTerminal() )
		{
			int index = OrderNode.getIndex(node.getNodeType());
			if( list.get(index -1) == null )
			{
				list.set(index -1, node);
			}
			//list.add(node);
			return;
		}
		else
		{
			inorderTraverse(node.getChild(0), list);
			//list.add(node);
			inorderTraverse(node.getChild(1), list);
		}
	}
}
