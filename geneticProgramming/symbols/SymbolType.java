 package geneticProgramming.symbols;

import geneticProgramming.GpNode;

import java.io.Serializable;

public abstract class SymbolType implements Comparable<SymbolType>, Serializable
{
  private static final long serialVersionUID = -7228540527582667957L;
  protected int _argumentSize;
  protected String _symbolName;

  public SymbolType()
  {
    _argumentSize = 0;
    _symbolName = "";
  }

  public SymbolType(String name, int childSize)
  {
    _argumentSize = childSize;
    _symbolName = name;
  }

  public String getSymbolName()
  {
    return _symbolName;
  }

  public void setSymbolName(String name)
  {
    _symbolName = name;
  }

  /**
   * evaluates and returns value of this node. All subclasses must implements
   * this method.
   */
  public abstract Object evaluate(GpNode node, Object obj);

  public int getArgumentSize()
  {
    return _argumentSize;
  }

  /**
   * returns initial value for this symbol type. This method is used for
   * initialize extra value such as random value. Override in subclass.
   */
  public Object initialValue()
  {
    return null;
  }

  public void setArgumentSize(int argumentSize)
  {
    _argumentSize = argumentSize;
  }

  public int compareTo(SymbolType target)
  {
    return _argumentSize - target.getArgumentSize();
  }

  @Override
  public String toString()
  {
    StringBuilder str = new StringBuilder();
    str.append("[").append(_symbolName).append(", ").append(_argumentSize).append("]");
    return str.toString();
  }
}
