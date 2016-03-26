package application.wallFollowing.symbols;


import geneticProgramming.GpNode;
import geneticProgramming.symbols.SymbolType;
import application.wallFollowing.Robot;

/**
 * The gene symbol that represents sensor 2
 * @author Makoto Tanji
 */
public class Symbol_S2 extends SymbolType
{
	public Symbol_S2()
	{
		super("S2", 0);
	}
	
	@Override
	public Object evaluate(GpNode node, Object obj)
	{
		return ((Robot)obj).getSensor(1);
	}	
}
