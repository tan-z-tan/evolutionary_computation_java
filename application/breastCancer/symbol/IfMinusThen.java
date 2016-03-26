package application.breastCancer.symbol;

import geneticProgramming.GpNode;
import geneticProgramming.symbols.SymbolType;

public class IfMinusThen extends SymbolType
{
	@Override
	public Object evaluate(GpNode node, Object obj)
	{
		if( (Double)node.getChild(0).evaluate(obj) < 0 )
		{
			return node.getChild(1).evaluate(obj);
		}
		else return node.getChild(2).evaluate(obj);
	}
}
