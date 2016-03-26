package application.breastCancer.symbol;

import geneticProgramming.GpNode;
import geneticProgramming.symbols.SymbolType;

public class Sin extends SymbolType
{
	@Override
	public Object evaluate(GpNode node, Object obj)
	{
		return Math.sin((Double)node.getChild(0).evaluate(obj));
	}
}
