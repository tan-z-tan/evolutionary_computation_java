package application.multiplexer.symbols;

import geneticProgramming.GpNode;
import geneticProgramming.symbols.SymbolType;

public class Not extends SymbolType
{
  @Override
  public Object evaluate(GpNode node, Object obj)
  {
    return !(Boolean) node.getChild(0).evaluate(obj);
  }
}
