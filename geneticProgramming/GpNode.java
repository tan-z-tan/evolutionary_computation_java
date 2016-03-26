package geneticProgramming;

import geneticProgramming.symbols.SymbolType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author tanji
 */
public final class GpNode implements Cloneable, Serializable
{
  private static final long serialVersionUID = -266893198320983107L;
  private List<GpNode> _children;
  //private GpNode[] _children;
  private GpNode _parent;
  private SymbolType _nodeType;
  private Object _extraValue;
  /** the depth of this node in tree */
  private int _depth;
  /** the depth of tree under this node */
  private int _depthFromHere;

  /** constructor */
  public GpNode(SymbolType type, int depth)
  {
      this(type, depth, 0);
  }

  /** constructor */
  public GpNode(SymbolType type, int depth, int depthFromHere)
  {
    if (type == null)
    {
      throw new IllegalArgumentException();
    }
    _children = new ArrayList<GpNode>(type.getArgumentSize());
    //_children = new GpNode[type.getArgumentSize()];
    _nodeType = type;
    _extraValue = type.initialValue(); // default null
    _depth = depth;
    _depthFromHere = depthFromHere;
  }

  /** evaluates and returns value of this node. */
  public Object evaluate(Object obj)
  {
    return _nodeType.evaluate(this, obj);
  }

  public List<GpNode> getChildren()
  {
    return _children;
  }

  /** returns i'th child, or returns null if child does not exist. */
  public GpNode getChild(int i)
  {
    return _children.get(i);
  }

  public void addChild(GpNode node)
  {
    if (_children.size() < _nodeType.getArgumentSize())
    {
      _children.add(node);
      node.setParent(this);
    }
  }

  /** return true if this is terminal node */
  public boolean isTerminal()
  {
      return this._nodeType.getArgumentSize() == 0;
  }

  /** return true if this is nonterminal node */
  public boolean isNonterminal()
  {
      return !isTerminal();
  }

  public void setChildren(List<GpNode> children)
  {
    _children = children;
  }

  public void setChildAt(int index, GpNode child)
  {
    _children.set(index, child);
  }

  public Object getExtraValue()
  {
    return _extraValue;
  }

  public void setExtraValue(Object value)
  {
    _extraValue = value;
  }

  public GpNode getParent()
  {
    return _parent;
  }

  public void setParent(GpNode _parent)
  {
    this._parent = _parent;
  }

  public void setNodeType(SymbolType nodeType)
  {
    _nodeType = nodeType;
  }

  public SymbolType getNodeType()
  {
    return _nodeType;
  }

  public int getDepth()
  {
    return _depth;
  }

  public void setDepth(int depth)
  {
    _depth = depth;
  }

  public int getDepthFromHere()
  {
    return _depthFromHere;
  }

  public void setDepthFromHere(int depthFromHere)
  {
    _depthFromHere = depthFromHere;
  }

  @Override
  public String toString()
  {
    StringBuilder str = new StringBuilder(_nodeType.getSymbolName());
    if (_extraValue != null)
    {
      str.append("[").append(_extraValue).append("]");
    }
    //str.append("[").append(_depth).append("]");
    return str.toString();
  }

  @Override
  public Object clone()
  {
    try
    {
      GpNode node = (GpNode) super.clone();

      // node.setChildren( new ArrayList<GpNode>(_children) );
      node.setChildren( new ArrayList<GpNode>(_nodeType.getArgumentSize()) );
      for (int i = 0; i < _children.size(); i++)
      {
          node.addChild((GpNode) _children.get(i).clone());
        // node.addChild( (GpNode)_children.get(i) );
      }
      return node;
    } catch (CloneNotSupportedException e)
    {
      e.printStackTrace();
    }
    return null;
  }

  public GpNode shallowClone()
  {
      GpNode node = new GpNode(_nodeType, _depth, _depthFromHere);
      node.setExtraValue(_extraValue);
      //for( int i = 0; i < _children.size(); i++ )
      // {
      // node.addChild( (GpNode)_children.get(i) );
      // }
      node._children.addAll(_children);
      //node._children = new ArrayList<GpNode>(_children);
      return node;
  }

  public static void main(String args[])
  {
    //DefaultSymbolType type = new DefaultSymbolType("Name", 2);
    SymbolType type = new SymbolType("Name", 10)
    {
      @Override
      public Object evaluate(GpNode node, Object obj)
      {
        return 0.0;
      }
      
      public Object initialValue()
      {
        List<String> value = new ArrayList<String>();
        value.add("tanji");
        value.add("makoto");
        return value;
      }
    };
    GpNode testNode = new GpNode(type, 1);
    System.out.println(testNode);
    GpNode cloneNode = (GpNode) testNode.clone();
    System.out.println(cloneNode);
    ((List<String>) testNode.getExtraValue()).add("afo");
    testNode.addChild(new GpNode(type, 0));
    testNode.addChild(new GpNode(type, 0));
    testNode.addChild(new GpNode(type, 0));
    testNode.addChild(new GpNode(type, 0));
    testNode.addChild(new GpNode(type, 0));
    testNode.addChild(new GpNode(type, 0));
    testNode.addChild(new GpNode(type, 0));
    testNode.addChild(new GpNode(type, 0));
    testNode.addChild(new GpNode(type, 0));
    testNode.addChild(new GpNode(new SymbolType(){
      @Override
      public Object evaluate(GpNode node, Object obj)
      {
        // TODO Auto-generated method stub
        return null;
      }}, 0));
    System.out.println(testNode);
    System.out.println(cloneNode);
    try{
      ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("/home/tanji/afo2")));
      oos.writeObject(testNode);
      
      ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File("/home/tanji/afo2")));
      GpNode obj = (GpNode)ois.readObject();
    }catch(Exception e) { e.printStackTrace(); }
  }
}
