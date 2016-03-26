package application.regression.symbol;

import geneticProgramming.GpNode;
import geneticProgramming.symbols.SymbolType;

/**
 * exponential function
 * @author tanji
 */
public class Exp extends SymbolType
{
	@Override
	public Object evaluate(GpNode node, Object obj)
	{
		return Math.exp((Double)node.getChild(0).evaluate(obj));
	}
}
