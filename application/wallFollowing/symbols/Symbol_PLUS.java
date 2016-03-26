package application.wallFollowing.symbols;


import geneticProgramming.GpNode;
import geneticProgramming.symbols.SymbolType;
import geometry.Vector3D;

/**
 * The gene symbol that represents PLUS operation
 * @author Makoto Tanji
 */
public class Symbol_PLUS extends SymbolType
{
	public Symbol_PLUS()
	{
		super("PLUS", 2);
	}
	
	@Override
	public Object evaluate(GpNode node, Object obj)
	{
		return ((Vector3D)node.getChild(0).evaluate(obj)).plus((Vector3D)node.getChild(1).evaluate(obj));
	}
}
