package application.DMAX.symbols;

import geneticProgramming.GpNode;
import geneticProgramming.symbols.SymbolType;

import org.apache.commons.math3.complex.Complex;

public class One extends SymbolType
{
    static Complex value = new Complex( 1, 0 );
    
	public One()
	{
		super("1", 0);
	}
	
	@Override
	public Object evaluate(GpNode node, Object obj)
	{
		return value;
	}
}
