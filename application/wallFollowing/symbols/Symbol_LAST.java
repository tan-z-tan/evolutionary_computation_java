package application.wallFollowing.symbols;


import geneticProgramming.GpNode;
import geneticProgramming.symbols.SymbolType;
import application.wallFollowing.Robot;

/**
 * The gene symbol that represents LAST peration
 * @author Makoto Tanji
 */
public class Symbol_LAST extends SymbolType
{
	public Symbol_LAST()
	{
		super("LAST", 0);
	}
	
	@Override
	public Object evaluate(GpNode node, Object obj)
	{
		return ((Robot)obj).getLastDirection();
	}
}
