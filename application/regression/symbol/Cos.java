package application.regression.symbol;

import geneticProgramming.GpNode;
import geneticProgramming.symbols.SymbolType;

public class Cos extends SymbolType
{
	@Override
	public Object evaluate(GpNode node, Object obj)
	{
		return Math.cos((Double)node.getChild(0).evaluate(obj));
	}
}
