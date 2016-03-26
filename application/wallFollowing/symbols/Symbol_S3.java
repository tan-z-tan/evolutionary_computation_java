package application.wallFollowing.symbols;


import geneticProgramming.GpNode;
import geneticProgramming.symbols.SymbolType;
import application.wallFollowing.Robot;

/**
 * The gene symbol that represents sensor 3
 * @author Makoto Tanji
 */
public class Symbol_S3 extends SymbolType
{
	public Symbol_S3()
	{
		super("S3", 0);
	}
	@Override
	public Object evaluate(GpNode node, Object obj)
	{
		return ((Robot)obj).getSensor(2);
	}	
}
