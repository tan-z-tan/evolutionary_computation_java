package application.regression.symbol;

import geneticProgramming.GpNode;
import geneticProgramming.symbols.SymbolType;

public class Plus extends SymbolType
{
	@Override
	public Object evaluate(GpNode node, Object obj)
	{
		return (Double)node.getChild(0).evaluate(obj) + (Double)node.getChild(1).evaluate(obj);
	}
}
