package application.max.symbols;

import geneticProgramming.GpNode;
import geneticProgramming.symbols.SymbolType;

public class Variable extends SymbolType
{
	public Variable()
	{
		super("0.5", 0);
	}
	
	@Override
	public Object evaluate(GpNode node, Object obj)
	{
		return Double.valueOf(0.5);
	}
}
