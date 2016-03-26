package application.breastCancer.symbol;

import geneticProgramming.GpNode;
import geneticProgramming.symbols.SymbolType;
import application.breastCancer.BreastCancerIndividual;

public class Variable6 extends SymbolType
{
	@Override
	public Object evaluate(GpNode node, Object obj)
	{
		return ((BreastCancerIndividual)obj).getVariable(5);
	}
	
	public static void main(String args[])
	{
		Variable6 variable = new Variable6();
	}
}
