package application.wallFollowing.symbols;


import geneticProgramming.GpNode;
import geneticProgramming.symbols.SymbolType;
import geometry.Vector3D;

/**
 * The gene symbol that represents IF LESS THAN operation
 * @author Makoto Tanji
 */
public class Symbol_IF_NT extends SymbolType
{
	public Symbol_IF_NT()
	{
		super("IF_NT", 3);
	}
	@Override
	public Object evaluate(GpNode node, Object obj)
	{
		if( ((Vector3D)node.getChild(0).evaluate(obj)).getY() < 0)
		{
			return (Vector3D)node.getChild(1).evaluate(obj);
		}
		else
		{
			return (Vector3D)node.getChild(2).evaluate(obj);
		}
	}
}
