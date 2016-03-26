package application.max.symbols;

import geneticProgramming.GpNode;
import geneticProgramming.symbols.SymbolType;

public class Plus extends SymbolType
{
	public Plus()
	{
		super("+", 2);
	}
	
	@Override
	public Object evaluate(GpNode node, Object obj)
	{
		return (Double)((Double)node.getChild(0).evaluate(obj) + (Double)node.getChild(1).evaluate(obj));
	}
}
