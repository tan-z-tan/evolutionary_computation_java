package mcmc;

import geneticProgramming.GpEnvironment;
import geneticProgramming.GpIndividual;
import geneticProgramming.GpNode;
import geneticProgramming.GpSymbolSet;
import geneticProgramming.GpTreeManager;
import geneticProgramming.symbols.DefaultSymbolType;
import geneticProgramming.symbols.SymbolType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import random.RandomManager;

public class TreeMCMC
{
	public static void main(String args[])
	{
		GpEnvironment<GpIndividual> environment = new GpEnvironment<GpIndividual>();
		GpSymbolSet symbolSet = new GpSymbolSet();
		symbolSet.addSymbol(new DefaultSymbolType("+", 2));
		//symbolSet.addSymbol(new DefaultSymbolType("if", 2));
		symbolSet.addSymbol(new DefaultSymbolType("x", 0));
		//symbolSet.addSymbol(new DefaultSymbolType("y", 0));
		//symbolSet.addSymbol(new DefaultSymbolType("1", 0));
		environment.setSymbolSet(symbolSet);
		environment.setNumberOfMaxDepth(5);
		environment.setNumberOfMaxInitialDepth(5);
		//environment.setNumberOfMinimumDepth(1);
		GpNode node = GpTreeManager.grow(environment);
		//System.out.println(GpTreeManager.getS_Expression( root ));
		//System.out.println(GpTreeManager.getS_Expression( markovChainStep(root, environment) ));
		
				
		int repetitionCount = 10000;
		int[] depthHistogram = new int[environment.getNumberOfMaxDepth()+1];
		// repetition
		System.out.println( 0 + ": " + GpTreeManager.getS_Expression(node) + " " + node.getDepth());
		for( int i = 0; i < repetitionCount; i++ )
		{
			node = markovChainStep(node, environment);
			//System.out.println( i + ": " + GpTreeManager.getS_Expression(node) + ", " + node.getDepthFromHere());
			depthHistogram[node.getDepthFromHere()] ++;
		}
		System.out.println( Arrays.toString(depthHistogram) );
	}
	
	public static GpNode markovChainStep(GpNode originalNode, GpEnvironment<GpIndividual> environment)
	{
		GpNode root = (GpNode)originalNode.clone();
		List<GpNode> bfs = GpTreeManager.breadthFirstSearch(root);
		List<GpNode> terminalNodes = new ArrayList<GpNode>();
		int nonterminalSize = bfs.size() -1;
		for( int i = bfs.size()-1; i >= 0; i-- )
		{
			if(bfs.get(i).isTerminal())
			{
				terminalNodes.add(bfs.get(i));
			}
		}
		GpNode randomNode = terminalNodes.get( (int)(RandomManager.getRandom() * terminalNodes.size()) );
		double randomValue = RandomManager.getRandom();		
		//System.out.println(randomNode + " is selected: depth=" + randomNode.getDepth());
		
		// remove
		if( randomValue < 1 /2.0 )
		{
			if( randomNode.getDepth() == 1 )
			{
				return originalNode;
			}
			else if (randomNode.getParent().getDepth() == 1 )
			{
				return new GpNode(environment.getSymbolSet().getTerminalSymbol(),1,1);
			}
			else
			{
				List<GpNode> children = randomNode.getParent().getParent().getChildren();
				children.set(children.indexOf(randomNode.getParent()), new GpNode(environment.getSymbolSet().getTerminalSymbol(), randomNode.getDepth(), 1));
			}
		}
		else if( randomValue < 0 /3.0 )// change
		{
			GpNode newNode = new GpNode(environment.getSymbolSet().getTerminalSymbol(), randomNode.getDepth(), 1);
			if (randomNode.getDepth() == 1) 
			{
				GpTreeManager.calculateDepth(newNode, 1);
				return newNode;
			}
			List<GpNode> children = randomNode.getParent().getChildren();
			children.set(children.indexOf(randomNode), newNode);
			
		}
		else // grow
		{
			//System.out.println("add");
			SymbolType randomType = environment.getSymbolSet().getFunctionSymbol();
			if( randomNode.getDepth() == environment.getNumberOfMaxDepth() )
			{
				return originalNode;
			}
			else
			{
				GpNode newNode = new GpNode(randomType, randomNode.getDepth(), 1);
				for( int i = 0; i < randomType.getArgumentSize(); i++ )
				{
					newNode.addChild(new GpNode(environment.getSymbolSet().getTerminalSymbol(), newNode.getDepth()+1, 1));
				}
				if (randomNode.getDepth() == 1) 
				{
					GpTreeManager.calculateDepth(newNode, 1);
					return newNode;
				}
				else
				{
					List<GpNode> children = randomNode.getParent().getChildren();
					children.set(children.indexOf(randomNode), newNode); 
				}
			}
		}
		
		GpTreeManager.calculateDepth(root, 1);
		return root;
	}
}
