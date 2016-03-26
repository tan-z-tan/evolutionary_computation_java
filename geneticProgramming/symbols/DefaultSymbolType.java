package geneticProgramming.symbols;

import geneticProgramming.GpNode;

public class DefaultSymbolType extends SymbolType
{
  public DefaultSymbolType(String name, int argumentSize)
  {
    _symbolName = name;
    _argumentSize = argumentSize;
  }
  
  @Override
  public Object evaluate(GpNode node, Object obj)
  {
    return null;
  }
}
