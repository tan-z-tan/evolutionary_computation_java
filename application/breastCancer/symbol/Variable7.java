package application.breastCancer.symbol;

import geneticProgramming.GpNode;
import geneticProgramming.symbols.SymbolType;
import application.breastCancer.BreastCancerIndividual;

public class Variable7 extends SymbolType
{
	@Override
	public Object evaluate(GpNode node, Object obj)
	{
		return ((BreastCancerIndividual)obj).getVariable(6);
	}
	
	public static void main(String args[])
	{
		Variable7 variable = new Variable7();
	}
}
