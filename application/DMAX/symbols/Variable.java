package application.DMAX.symbols;

import geneticProgramming.GpNode;
import geneticProgramming.symbols.SymbolType;

import org.apache.commons.math3.complex.Complex;

public class Variable extends SymbolType
{
    static Complex value = new Complex( 0.95, 0 );
    
	public Variable()
	{
		super("0.95", 0);
	}
	
	@Override
	public Object evaluate(GpNode node, Object obj)
	{
		return value;
	}
}
