package application.regression.symbol;

import geneticProgramming.GpNode;
import geneticProgramming.symbols.SymbolType;

/**
 * (defun rlog (argument)
 * 	(if (= 0 argument) 0 (log (abs argument))))
 * from book J. Koza 1992.
 * @author tanji
 */
public class Log extends SymbolType
{
	@Override
	public Object evaluate(GpNode node, Object obj)
	{
		Double value = Math.abs((Double)node.getChild(0).evaluate(obj));
		if( value == 0 ) return new Double(0);
		else return Math.log(Math.abs(value));
	}
}
