package SIHC;

import geneticProgramming.GpEnvironment;
import geneticProgramming.GpIndividual;
import geneticProgramming.GpNode;
import geneticProgramming.GpSymbolSet;
import geneticProgramming.GpTreeManager;
import geneticProgramming.symbols.DefaultSymbolType;
import geneticProgramming.symbols.SymbolType;

import java.util.ArrayList;
import java.util.List;

public class StochasticIteratedHillClimbing
{
  public static void main(String args[])
  {
    GpEnvironment<GpIndividual> environment = new GpEnvironment<GpIndividual>();
    GpSymbolSet symbolSet = new GpSymbolSet();
    symbolSet.addSymbol(new DefaultSymbolType("X", 0));
    symbolSet.addSymbol(new DefaultSymbolType("X", 0));
    symbolSet.addSymbol(new DefaultSymbolType("X", 0));
    symbolSet.addSymbol(new DefaultSymbolType("A", 1));
    symbolSet.addSymbol(new DefaultSymbolType("B", 2));
    symbolSet.addSymbol(new DefaultSymbolType("C", 3));
    //symbolSet.addSymbol(new DefaultSymbolType("D", 4));
    environment.setSymbolSet(symbolSet);
    environment.setNumberOfMaxDepth(3);
    
    GpNode root = new GpNode(symbolSet.getSymbolByName("C"), 3);
    root.addChild(new GpNode(symbolSet.getSymbolByName("X"), 1));
    root.addChild(new GpNode(symbolSet.getSymbolByName("B"), 2));
    root.addChild(new GpNode(symbolSet.getSymbolByName("C"), 2));
    root.getChild(1).addChild(new GpNode(symbolSet.getSymbolByName("X"), 1));
    root.getChild(1).addChild(new GpNode(symbolSet.getSymbolByName("X"), 1));
    root.getChild(2).addChild(new GpNode(symbolSet.getSymbolByName("X"), 1));
    root.getChild(2).addChild(new GpNode(symbolSet.getSymbolByName("X"), 1));
    root.getChild(2).addChild(new GpNode(symbolSet.getSymbolByName("X"), 1));
    GpTreeManager.calculateDepth(root, 1);
    
    System.out.println( GpTreeManager.getS_Expression(root) );
    //System.out.println( GpTreeManager.getS_Expression(randomChange(root, environment)));
    System.out.println( GpTreeManager.getS_Expression(randomInsert(root, environment)));
    //System.out.println( GpTreeManager.getS_Expression(randomSubstitute(root, environment)) );
  }
  
  public static <T extends GpIndividual> GpNode randomChange(GpNode original, GpEnvironment<T> environment)
  {
    GpNode changedNode = null;
    while (changedNode == null)
    {
      if( Math.random() < 1/3.0 )
      {
        changedNode = randomSubstitute(original, environment);
      }
      else if( Math.random() < 1/2.0 )
      {
        changedNode = randomInsert(original, environment);
      }
      else
      {
        changedNode = randomDelete(original, environment);
      }
    }
    return changedNode;
  }
  
  public static <T extends GpIndividual> GpNode randomSubstitute(GpNode original, GpEnvironment<T> environment)
  {
    GpNode root = (GpNode)original.clone();
    environment.getSymbolSet().getTerminalSymbol();
    // it does not select root node
    GpNode randomNode = GpTreeManager.getNodeAt(root, (int)(Math.random() * (GpTreeManager.getNodeSize(root))));
    List<SymbolType> sameAritySymbolList = new ArrayList<SymbolType>();
    for( int i = 0; i < environment.getSymbolSet().getSymbolSize(); i++ ) 
    {
      if( randomNode.getNodeType().getArgumentSize() == environment.getSymbolSet().getSymbolType(i).getArgumentSize() )
      {
        sameAritySymbolList.add( environment.getSymbolSet().getSymbolType(i) );
      }
    }
    if( sameAritySymbolList.size() == 1 )
    {
      return null;
    }
    GpNode replaceNode = new GpNode(sameAritySymbolList.get((int)(Math.random() * sameAritySymbolList.size())), 1);
    if( randomNode == root )
    {
      //System.out.println("root");
      root = replaceNode;
    }
    else
    {
      randomNode.getParent().setChildAt(randomNode.getParent().getChildren().indexOf(randomNode), replaceNode);
    }
    
    for( int i = 0; i < randomNode.getChildren().size(); i++ )
    {
      replaceNode.addChild(randomNode.getChild(i));
    }
    
//    System.out.println(randomNode + " " + randomNode.getDepth());
//    System.out.println(replaceNode);
    
    GpTreeManager.calculateDepth(root, 1);
    
    return root;
  }
  
  public static <T extends GpIndividual> GpNode randomInsert(GpNode original, GpEnvironment<T> environment)
  {
    GpNode root = (GpNode)original.clone();
    environment.getSymbolSet().getTerminalSymbol();
    // it does not select root node
    GpNode randomNode = GpTreeManager.getNodeAt(root, (int)(Math.random() * (GpTreeManager.getNodeSize(root))));
    GpNode replaceNode = new GpNode(environment.getSymbolSet().getRandomType(), 1);
    //GpNode replaceNode = new GpNode(environment.getSymbolSet().getFunctionSymbol(), 1);
//    System.out.println(randomNode + " " + randomNode.getDepth());
//    System.out.println(replaceNode);
    
    if( randomNode.getDepth() + randomNode.getDepthFromHere() > environment.getNumberOfMaxDepth() )
    {
      return null;
    }
    
    if( randomNode == root )
    {
      //System.out.println("root");
      root = replaceNode;
    }
    else
    {
      randomNode.getParent().setChildAt(randomNode.getParent().getChildren().indexOf(randomNode), replaceNode);
    }
    
    if( replaceNode.getNodeType().getArgumentSize() >= 1 )
    {
      replaceNode.addChild(randomNode);
    }
    for( int i = 1; i < replaceNode.getNodeType().getArgumentSize(); i++ )
    {
      replaceNode.addChild(new GpNode(environment.getSymbolSet().getTerminalSymbol(), 1));
    }
    GpTreeManager.calculateDepth(root, 1);
    
    return root;
  }
  
  public static <T extends GpIndividual> GpNode randomDelete(GpNode original, GpEnvironment<T> environment)
  {
    GpNode root = (GpNode)original.clone();
    environment.getSymbolSet().getTerminalSymbol();
    // it does not select root node
    GpNode randomNode = GpTreeManager.getNodeAt(root, (int)(Math.random() * (GpTreeManager.getNodeSize(root))));
    GpNode replaceNode;
    //System.out.println(randomNode + " " + randomNode.getDepth());
    if( randomNode.isTerminal() )
    {
      replaceNode = new GpNode(environment.getSymbolSet().getTerminalSymbol(), randomNode.getDepth(), randomNode.getDepthFromHere());
    }
    else
    {
      replaceNode = randomNode.getChild(0);
      int maxSize = GpTreeManager.getNodeSize(replaceNode);
      for( GpNode child: randomNode.getChildren() )
      {
        int size = GpTreeManager.getNodeSize(child);
        if( GpTreeManager.getNodeSize(child) > maxSize )
        {
          replaceNode = child;
          maxSize = size;
        }
      }
    }
    if( randomNode == root )
    {
      //System.out.println("root");
      root = replaceNode;
    }
    else
    {
      randomNode.getParent().setChildAt(randomNode.getParent().getChildren().indexOf(randomNode), replaceNode);
    }
    
    GpTreeManager.calculateDepth(root, 1);
    return root;
  }
}
