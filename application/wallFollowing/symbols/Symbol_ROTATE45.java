package application.wallFollowing.symbols;


import geneticProgramming.GpNode;
import geneticProgramming.symbols.SymbolType;
import geometry.Vector3D;

/**
 * The gene symbol that represents ROTATE45 operation
 * @author Makoto Tanji
 */
public class Symbol_ROTATE45 extends SymbolType
{
	public Symbol_ROTATE45()
	{
		super("R45", 1);
	}
	
	@Override
	public Object evaluate(GpNode node, Object obj)
	{
		return ((Vector3D)node.getChild(0).evaluate(obj)).rotate( Math.PI / 4.0 );
	}
}
