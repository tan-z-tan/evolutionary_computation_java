package application.wallFollowing.symbols;


import geneticProgramming.GpNode;
import geneticProgramming.symbols.SymbolType;
import geometry.Vector3D;

/**
 * The gene symbol that represents ROTATE-45 operation
 * @author Makoto Tanji
 */
public class Symbol_ROTATE45INV extends SymbolType
{
	public Symbol_ROTATE45INV()
	{
		super("R-45", 1);
	}
	
	@Override
	public Object evaluate(GpNode node, Object obj)
	{
		return ((Vector3D)node.getChild(0).evaluate(obj)).rotate( -Math.PI / 4.0 );
	}
}
