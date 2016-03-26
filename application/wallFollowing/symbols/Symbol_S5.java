package application.wallFollowing.symbols;


import geneticProgramming.GpNode;
import geneticProgramming.symbols.SymbolType;
import application.wallFollowing.Robot;

/**
 * The gene symbol that represents sensor 5
 * @author Makoto Tanji
 */
public class Symbol_S5 extends SymbolType
{
	public Symbol_S5()
	{
		super("S5", 0);
	}
	@Override
	public Object evaluate(GpNode node, Object obj)
	{
		return ((Robot)obj).getSensor(4);
	}
}
