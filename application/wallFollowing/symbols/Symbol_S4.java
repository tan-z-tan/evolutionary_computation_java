package application.wallFollowing.symbols;


import geneticProgramming.GpNode;
import geneticProgramming.symbols.SymbolType;
import application.wallFollowing.Robot;

/**
 * The gene symbol that represents sensor 4
 * @author Makoto Tanji
 */
public class Symbol_S4 extends SymbolType
{
	public Symbol_S4()
	{
		super("S4", 0);
	}
	@Override
	public Object evaluate(GpNode node, Object obj)
	{
		return ((Robot)obj).getSensor(3);
	}
}
