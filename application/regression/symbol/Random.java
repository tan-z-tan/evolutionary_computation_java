package application.regression.symbol;

import geneticProgramming.GpNode;
import geneticProgramming.symbols.SymbolType;

public class Random extends SymbolType
{
	@Override
	public Object evaluate(GpNode node, Object obj)
	{
		return node.getExtraValue();
	}
	
	@Override
	public Object initialValue()
	{
		return (Double)(Math.random() * 1.0);
	}
}
