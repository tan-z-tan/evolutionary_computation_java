package application.regression.symbol;

import application.regression.RegressionIndividual;
import geneticProgramming.GpNode;
import geneticProgramming.symbols.SymbolType;

public class Variable extends SymbolType
{
	@Override
	public Object evaluate(GpNode node, Object obj)
	{
		return ((RegressionIndividual)obj).getVariable();
	}
	
	public static void main(String args[])
	{
		Variable variable = new Variable();
	}
}
