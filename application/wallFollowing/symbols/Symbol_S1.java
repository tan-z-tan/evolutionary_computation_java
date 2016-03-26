package application.wallFollowing.symbols;


import geneticProgramming.GpNode;
import geneticProgramming.symbols.SymbolType;
import application.wallFollowing.Robot;

/**
 * The gene symbol that represents sensor 1
 * @author Makoto Tanji
 */
public class Symbol_S1 extends SymbolType
{
	public Symbol_S1()
	{
		super("S1", 0);
	}
	
	@Override
	public Object evaluate(GpNode node, Object obj)
	{
		return ((Robot)obj).getSensor(0);
	}
}
