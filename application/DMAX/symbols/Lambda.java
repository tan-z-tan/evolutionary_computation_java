package application.DMAX.symbols;

import geneticProgramming.GpNode;
import geneticProgramming.symbols.SymbolType;

import org.apache.commons.math3.complex.Complex;

public class Lambda extends SymbolType
{
    //static Complex value;
    static Complex value = new Complex( -1, 0 );
    
	public Lambda(int r)
	{
	    super("r", 0);
		//init(r);
	}
	
	private static void init(int r)
	{
	    //value = new Complex( Math.cos((2 * Math.PI) / r), Math.sin((2 * Math.PI) / r) );
	}
	
	@Override
	public Object evaluate(GpNode node, Object obj)
	{
	    return value;
	}
}
