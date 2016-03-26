package application.multiplexer.symbols;

import geneticProgramming.GpNode;
import geneticProgramming.symbols.SymbolType;

public class And extends SymbolType
{
  @Override
  public Object evaluate(GpNode node, Object obj)
  {
    return (Boolean) node.getChild(0).evaluate(obj) && (Boolean) node.getChild(1).evaluate(obj);
  }
}
