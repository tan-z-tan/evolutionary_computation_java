package application.breastCancer.symbol;

import geneticProgramming.GpNode;
import geneticProgramming.symbols.SymbolType;
import application.breastCancer.BreastCancerIndividual;

public class Variable2 extends SymbolType
{
	@Override
	public Object evaluate(GpNode node, Object obj)
	{
		return ((BreastCancerIndividual)obj).getVariable(1);
	}
	
	public static void main(String args[])
	{
		Variable2 variable = new Variable2();
	}
}
