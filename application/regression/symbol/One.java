package application.regression.symbol;

import geneticProgramming.GpNode;
import geneticProgramming.symbols.SymbolType;

public class One extends SymbolType
{
	@Override
	public Object evaluate(GpNode node, Object obj)
	{
		return (Double)1.0;
	}
}
