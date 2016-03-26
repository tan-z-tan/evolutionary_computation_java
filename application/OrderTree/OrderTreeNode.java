package application.OrderTree;

import geneticProgramming.GpEnvironment;
import geneticProgramming.GpIndividual;
import geneticProgramming.GpNode;
import geneticProgramming.GpSymbolSet;
import geneticProgramming.GpTreeManager;
import geneticProgramming.symbols.SymbolType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderTreeNode extends SymbolType
{
	private static final double fullBonus = 2.0;
	private static final double partialBonus = 1.0;
	/** The original paper adopts 1.0 / 3.0 */
	private static final double penalty = 2. / 3.;
	private static final double completeBonusForX = 2.0;
	private static Map<String, Integer> royalTreeNode;
	private static final double value_X = 1.0;
	private static final double value_Y = 0.95;
	
	static
	{
		royalTreeNode = new HashMap<String, Integer>();
		royalTreeNode.put("x", 1);
		royalTreeNode.put("y", 1);
		royalTreeNode.put("A", 2);
		royalTreeNode.put("B", 3);
		royalTreeNode.put("C", 4);
		royalTreeNode.put("D", 5);
		royalTreeNode.put("E", 6);
		royalTreeNode.put("F", 7);
		royalTreeNode.put("G", 8);
		royalTreeNode.put("H", 9);
	}
	
	public OrderTreeNode(String name, int childSize)
	{
		this._symbolName = name;
		this._argumentSize = childSize;
	}
	
	@Override
	public Object evaluate(GpNode node, Object obj)
	{
		if( getArgumentSize() == 0 ) // terminal node
		{
			if( this.getSymbolName().equals("x") )
				return new RoyalTreeNodeEntity(this.getSymbolName(), value_X);
			else if( this.getSymbolName().equals("y") )
				return new RoyalTreeNodeEntity(this.getSymbolName(), value_Y);
			else
			{
				return null;
			}
		}
		
		// case function
		double nodeValue = 0;
		String completeType = "";
		for( int i = 0; i < getArgumentSize(); i++ )
		{			
			RoyalTreeNodeEntity childEntity = (RoyalTreeNodeEntity)node.getChild(i).evaluate(obj);
			if( i == 0 )
			{
				completeType = childEntity.getEntityType();
			}
			double childValue = childEntity.getNodeValue();
			boolean isCorrect = isCorrectChild(this._symbolName, node.getChild(i).getNodeType().getSymbolName());
			
			if( isCorrect )
			{
				if( !childEntity.getEntityType().equals("FALSE") )
				{
					childValue *= fullBonus;
					if( !childEntity.getEntityType().equals(completeType) )
					{
						completeType = "FALSE";
					}
				}
				else
				{
					childValue *= partialBonus;
					completeType = "FALSE";
				}
			}
			else
			{
				childValue *= penalty;
				completeType = "FALSE";
			}
			
			nodeValue += childValue;
		}
		
		if( !completeType.equals("FALSE") )// complete tree!
		{
			return new RoyalTreeNodeEntity(completeType, nodeValue * completeBonusForX);
		}
		else
		{
			return new RoyalTreeNodeEntity(completeType, nodeValue);
		}
	}
	
	public static boolean isCorrectChild(String parent, String child)
	{
		return royalTreeNode.get(parent) - royalTreeNode.get(child) == 1;
	}
	
	// inner class for calculating fitness
	class RoyalTreeNodeEntity
	{
		String entityRepresentation;
		double nodeValue;
		
		protected RoyalTreeNodeEntity(String entity, double value)
		{
			entityRepresentation = entity;
			nodeValue = value;
		}
		
		public String getEntityType()
		{
			return entityRepresentation;
		}
		public double getNodeValue()
		{
			return nodeValue;
		}
		
		@Override
		public String toString()
		{
			return entityRepresentation + " " + nodeValue; 
		}
	}
	
	public static int getIndex(SymbolType type)
	{
		String name = type.getSymbolName();
		return Integer.valueOf(name.substring(name.indexOf('_') +1));
	}
	
	@Override
	public int compareTo(SymbolType target)
	{
		if( this.getSymbolName().startsWith("JOIN") || target.getSymbolName().startsWith("JOIN") )
		{
			return super.compareTo(target);
		}
		else
		{
			return getIndex(this) - getIndex(target);
		}
	}
	
	public static void main(String argv[])
	{
		GpSymbolSet symbolSet = new GpSymbolSet();
		symbolSet.addSymbol( new OrderTreeNode("x", 0) );
		symbolSet.addSymbol( new OrderTreeNode("y", 0) );
		symbolSet.addSymbol( new OrderTreeNode("A", 1) );
		symbolSet.addSymbol( new OrderTreeNode("B", 2) );
		symbolSet.addSymbol( new OrderTreeNode("C", 3) );
		//symbolSet.addSymbol( new RoyalTreeNode("D", 4) );
		//symbolSet.addSymbol( new RoyalTreeNode("E", 5) );
		
		GpEnvironment<GpIndividual> environment = new GpEnvironment<GpIndividual>();
		environment.setSymbolSet( symbolSet );
		environment.setCrossoverRatio(0.9);
		environment.setEliteSize(1);
		environment.setMutationRatio(0.05);
		environment.setNumberOfMaxInitialDepth(3);
		environment.setNumberOfMaxDepth(3);
		environment.setNumberOfMinimumDepth(1);
		environment.setPopulationSize(1000);
		environment.setRepetitionNumber(20);
		//environment.setSelector(new TournamentSelector());
		environment.setPopulation( new ArrayList<GpIndividual>() );
		
		for (int i = 0; i < environment.getPopulationSize(); i++)
		{
			environment.getPopulation().add( new GpIndividual() );
			environment.getPopulation().get(i).setRootNode(GpTreeManager.grow(environment));
			GpIndividual ind = environment.getPopulation().get(i);
			RoyalTreeNodeEntity entity = (RoyalTreeNodeEntity)ind.evaluate();
			System.out.println( GpTreeManager.getS_Expression(ind.getRootNode()) + " " + entity.getNodeValue() );
			if( entity.getNodeValue() == 32 )
			{
				System.out.println(i);
				System.out.println( GpTreeManager.getS_Expression(ind.getRootNode()) );
				System.out.println( entity );
				System.exit(0);
			}
			//
			//System.out.println( ind.evaluate() );
		}
	}
}
