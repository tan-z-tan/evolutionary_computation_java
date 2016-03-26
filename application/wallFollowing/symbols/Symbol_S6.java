package application.wallFollowing.symbols;


import geneticProgramming.GpNode;
import geneticProgramming.symbols.SymbolType;
import application.wallFollowing.Robot;

/**
 * The gene symbol that represents sensor 6
 * @author Makoto Tanji
 */
public class Symbol_S6 extends SymbolType
{
	public Symbol_S6()
	{
		super("S6", 0);
	}
	@Override
	public Object evaluate(GpNode node, Object obj)
	{
		return ((Robot)obj).getSensor(5);
	}
}
