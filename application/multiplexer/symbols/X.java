package application.multiplexer.symbols;

import geneticProgramming.GpNode;
import geneticProgramming.symbols.SymbolType;
import application.multiplexer.MultiplexerIndividual;

public class X extends SymbolType
{
  int index;
  
  public X(int index)
  {
    this.index = index;
    this._symbolName = "X" + String.valueOf(index);
  }
  
  @Override
  public Object evaluate(GpNode node, Object obj)
  {
    return ((MultiplexerIndividual)obj).getX(index);
  }
}
