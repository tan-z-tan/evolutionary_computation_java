package application.breastCancer.symbol;

import geneticProgramming.GpNode;
import geneticProgramming.symbols.SymbolType;
import application.breastCancer.BreastCancerIndividual;

public class Variable8 extends SymbolType
{
	@Override
	public Object evaluate(GpNode node, Object obj)
	{
		return ((BreastCancerIndividual)obj).getVariable(7);
	}
	
	public static void main(String args[])
	{
		Variable8 variable = new Variable8();
	}
}
