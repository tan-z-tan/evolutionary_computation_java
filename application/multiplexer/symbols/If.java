package application.multiplexer.symbols;

import geneticProgramming.GpNode;
import geneticProgramming.symbols.SymbolType;

public class If extends SymbolType
{
  @Override
  public Object evaluate(GpNode node, Object obj)
  {
    if( (Boolean) node.getChild(0).evaluate(obj) )
    {
      return (Boolean) node.getChild(1).evaluate(obj);
    }
    else
    {
      return (Boolean) node.getChild(2).evaluate(obj);
    }
  }
}
