package application.DMAX.symbols;

import geneticProgramming.GpNode;
import geneticProgramming.symbols.SymbolType;

import org.apache.commons.math3.complex.Complex;

public class Multiply extends SymbolType
{
    public Multiply(int r)
    {
        super("*", r);
    }

    @Override
    public Object evaluate(GpNode node, Object obj)
    {
        Complex value = new Complex(1, 0);
        for (int i = 0; i < this._argumentSize; i++)
        {
            value = value.multiply((Complex) node.getChild(i).evaluate(obj));
        }
        return value;
    }
}
