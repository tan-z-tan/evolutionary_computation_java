package application.wallFollowing.symbols;


import geneticProgramming.GpNode;
import geneticProgramming.symbols.SymbolType;
import geometry.Vector3D;

/**
 * The gene symbol that represents IF LESS THAN operation
 * @author Makoto Tanji
 */
public class Symbol_IF_LT extends SymbolType
{
	public Symbol_IF_LT()
	{
		super("IF_LT", 4);
	}
	@Override
	public Object evaluate(GpNode node, Object obj)
	{
		if( ((Vector3D)node.getChild(0).evaluate(obj)).distance() > ((Vector3D)node.getChild(1).evaluate(obj)).distance() )
			return (Vector3D)node.getChild(2).evaluate(obj);
		else return (Vector3D)node.getChild(3).evaluate(obj);
	}
}
