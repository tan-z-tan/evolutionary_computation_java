package application.breastCancer.symbol;

import geneticProgramming.GpNode;
import geneticProgramming.symbols.SymbolType;
import application.breastCancer.BreastCancerIndividual;

public class Variable1 extends SymbolType
{
	@Override
	public Object evaluate(GpNode node, Object obj)
	{
		return ((BreastCancerIndividual)obj).getVariable(0);
	}
	
	public static void main(String args[])
	{
		Variable1 variable = new Variable1();
	}
}
