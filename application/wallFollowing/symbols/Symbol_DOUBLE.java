package application.wallFollowing.symbols;


import geneticProgramming.GpNode;
import geneticProgramming.symbols.SymbolType;
import geometry.Vector3D;

/**
 * The gene symbol that represents DOUBLE operation
 * @author Makoto Tanji
 */
public class Symbol_DOUBLE extends SymbolType
{
	public Symbol_DOUBLE()
	{
		super("Double", 1);
	}
	
	@Override
	public Object evaluate(GpNode node, Object obj)
	{
		Vector3D child1Vector = (Vector3D)node.getChild(0).evaluate(obj);
		return ((Vector3D)child1Vector.multiple(2));
	}
}
