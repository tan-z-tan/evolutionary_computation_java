package application.regression.symbol;

import geneticProgramming.GpNode;
import geneticProgramming.symbols.SymbolType;

public class IfLessThen extends SymbolType
{
	@Override
	public Object evaluate(GpNode node, Object obj)
	{
		if( (Double)node.getChild(0).evaluate(obj) < (Double)node.getChild(1).evaluate(obj) )
		{
			return node.getChild(2).evaluate(obj);
		}
		else return node.getChild(3).evaluate(obj);
	}
}
