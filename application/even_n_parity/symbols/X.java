package application.even_n_parity.symbols;

import geneticProgramming.GpNode;
import geneticProgramming.symbols.SymbolType;
import application.even_n_parity.Even_n_parityIndividual;
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
    return ((Even_n_parityIndividual)obj).getX(index);
  }
}
