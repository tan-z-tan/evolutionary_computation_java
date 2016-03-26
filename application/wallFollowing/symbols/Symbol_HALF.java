package application.wallFollowing.symbols;


import geneticProgramming.GpNode;
import geneticProgramming.symbols.SymbolType;
import geometry.Vector3D;

/**
 * The gene symbol that represents HALF operation
 * @author Makoto Tanji
 */
public class Symbol_HALF extends SymbolType
{
	public Symbol_HALF()
	{
		super("HALF", 1);
	}
	
	@Override
	public Object evaluate(GpNode node, Object obj)
	{
		Vector3D child1Vector = (Vector3D)node.getChild(0).evaluate(obj);
		return ((Vector3D)child1Vector.multiple(0.5));
	}
}
