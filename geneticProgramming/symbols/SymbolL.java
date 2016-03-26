package geneticProgramming.symbols;

import geneticProgramming.GpNode;

public class SymbolL extends SymbolType
{
  public SymbolL(String name, int argumentSize)
  {
    _symbolName = name;
    _argumentSize = argumentSize;
  }
  
  @Override
  public Object evaluate(GpNode node, Object obj)
  {
    return node.getChild(0).evaluate(obj);
  }
}
