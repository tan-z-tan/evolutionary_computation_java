package application.OrderTree;

import geneticProgramming.GpNode;
import geneticProgramming.symbols.DefaultSymbolType;

public class FitnessFunctions
{
	static class Fitness
	{
		int fitness = 0;
	}
	
	/**
	 * ORDER tree function
	 */
	public static double fitnessFunction_OrderTree(GpNode node)
	{
		Fitness fitness = new Fitness();
		orderTreeTraverse(node, fitness);
		return fitness.fitness;
	}
	
	/** fitness calculation of ORDER TREE */
	private static void orderTreeTraverse(GpNode p, Fitness fitness)
	{
		if( p.isTerminal() )
		{
			return;
		}
		
		// left
		GpNode l = p.getChild(0);		
		if( getValue(p) < getValue(l) )
		{
			fitness.fitness++;
			orderTreeTraverse(l, fitness);
		}
		else if( getValue(p) == getValue(l) && l.isNonterminal() )
		{
			leftNeutralWalk(l, fitness);
		}
		
		// right
		GpNode r = p.getChild(1);
		if( getValue(p) < getValue(r) )
		{
			fitness.fitness++;
			orderTreeTraverse(r, fitness);
		}
		else if( getValue(p) == getValue(r) && r.isNonterminal() )
		{
			leftNeutralWalk(r, fitness);
		}
	}
	
	private static void leftNeutralWalk(GpNode p, Fitness fitness)
	{
		while(p.isNonterminal())
		{
			if( p.isNonterminal() && getValue(p) == getValue(p.getChild(0)) )
			{
				p = p.getChild(0);
				continue;
			}
			else if( getValue(p) < getValue(p.getChild(0)) )
			{
				orderTreeTraverse(p.getChild(0), fitness);
				fitness.fitness++;
				break;	
			}
			break;
		}
	}
	
	private static int getValue(GpNode n)
	{
		return Integer.valueOf( n.getNodeType().getSymbolName() );
	}
	
	public static void main(String args[])
	{
		GpNode root = new GpNode(new DefaultSymbolType("0", 2), 1);
		GpNode n1 = new GpNode(new DefaultSymbolType("1", 2), 1);
		GpNode n2 = new GpNode(new DefaultSymbolType("0", 2), 1);
		root.addChild(n1);
		root.addChild(n2);
		GpNode n11 = new GpNode(new DefaultSymbolType("2", 2), 1);
		GpNode n12 = new GpNode(new DefaultSymbolType("1", 2), 1);
		n1.addChild(n11);
		n1.addChild(n12);
		GpNode n21 = new GpNode(new DefaultSymbolType("2", 2), 1);
		GpNode n22 = new GpNode(new DefaultSymbolType("2", 2), 1);
		n2.addChild(n21);
		n2.addChild(n22);
		GpNode n121 = new GpNode(new DefaultSymbolType("3", 2), 1);
		GpNode n122 = new GpNode(new DefaultSymbolType("3", 2), 1);
		n12.addChild(n121);
		n12.addChild(n122);
		
		System.out.println( "Fitness = " + fitnessFunction_OrderTree(root) );
	}
}
