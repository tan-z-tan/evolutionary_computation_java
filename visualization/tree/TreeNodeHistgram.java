package visualization.tree;

import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

import visualization.S_ExpressionHandler;

public class TreeNodeHistgram
{
  public static void main(String args[])
  {
    TreeModel treeModel = S_ExpressionHandler.getTreeModelByS_Expression("( root" +
    		"( IF_IT ( ABS ( ADD PT_-1 ONE ) ) ( ADD ( IF_IT DU_1 ( DIV OC_-2 PT_-1 ) ) ( ABS ( SUB ( IF_IT DU_-2 PT_-2 ) ( ABS ( ADD PT_-3 ( ADD ( ADD ( ABS ( IF_IT PT_1 ( SUB ( ADD DU_2 ( ABS PT_-2 ) ) ZERO ) ) ) ( MUL ( SUB ( MUL ( DIV ( MUL OC_-3 DU_4 ) PT_3 ) PT_-1 ) ( MUL OC_2 ( ABS DU_2 ) ) ) ( ADD ( IF_IT DU_1 DU_-1 ) ( ABS ( SUB ( IF_IT DU_-2 OC_1 ) ( ABS ( ADD PT_-3 ( ADD ( ADD ( ABS PT_-1 ) ( ABS ( SUB PT_3 PT_-4 ) ) ) ( ADD PT_-1 PT_-3 ) ) ) ) ) ) ) ) ) ( ABS PT_1 ) ) ) ) ) ) ) )" +
    		"( ADD ( SUB ( ABS ( SUB ZERO ( MUL DU_2 ( ADD PT_3 ( IF_ET DU_-3 OC_1 ) ) ) ) ) ( SUB DU_-4 ( SUB ( ADD ( DIV ( SUB ( SUB ( IF_IT PT_-1 PT_1 ) ( ABS PT_1 ) ) ZERO ) ( IF_IT DU_4 ( DIV ( ABS ( ADD PT_-3 ( MUL PT_-4 ( ABS PT_-3 ) ) ) ) ( IF_IT ( ABS ( IF_IT PT_-4 PT_2 ) ) OC_-2 ) ) ) ) ( ABS ( ADD PT_-1 ( IF_IT ( ADD DU_4 PT_-1 ) ( MUL PT_1 ( ADD PT_2 ( DIV PT_3 PT_1 ) ) ) ) ) ) ) ( ABS ( IF_IT DU_-4 DU_-2 ) ) ) ) ) ( ABS ( IF_IT OC_1 DU_-2 ) ) )" +
    		"( ADD ( DIV ( ADD PT_-2 DU_-1 ) ( SUB PT_2 ( ADD ( IF_IT ( ABS ( ADD ( MUL ( ADD PT_-1 PT_-1 ) ( ADD ( ADD PT_-1 PT_-1 ) PT_-1 ) ) PT_-1 ) ) PT_-2 ) PT_-1 ) ) ) ( ADD ( ABS PT_-1 ) ( IF_IT PT_-1 ( MUL PT_-1 ( IF_IT PT_3 ( MUL PT_-4 ( IF_IT DU_2 PT_-2 ) ) ) ) ) ) )" +
    		"( ADD ( SUB OC_-3 ( ADD ( IF_IT DU_3 ( ADD PT_-1 PT_1 ) ) ( DIV ( MUL DU_3 ( DIV ( ABS ( DIV ( IF_IT PT_-1 PT_-3 ) DU_1 ) ) ( ADD PT_-1 PT_-1 ) ) ) ( SUB ( IF_IT PT_-2 OC_1 ) ( SUB OC_-1 ( IF_IT OC_2 ( DIV DU_-1 DU_2 ) ) ) ) ) ) ) ( ABS ( ADD ( ABS PT_-1 ) ( IF_IT ( SUB ( IF_IT PT_-3 OC_-2 ) ( IF_IT ZERO ( ADD PT_3 PT_-4 ) ) ) ( DIV DU_-1 ( SUB PT_2 PT_-2 ) ) ) ) ) )" +
    		"( IF_IT ( ADD PT_-1 OC_2 ) ( ADD PT_-2 ( ADD ( ADD ( ADD ( ABS PT_-1 ) ( ABS PT_-3 ) ) ( MUL ( MUL PT_-2 ( ADD ( ADD ( ADD DU_1 ( IF_IT PT_2 PT_-3 ) ) ( ADD PT_-1 ( IF_IT OC_1 OC_-4 ) ) ) ( IF_IT ( SUB OC_2 PT_-3 ) ( ADD PT_-2 DU_2 ) ) ) ) ( IF_IT ( DIV ( IF_IT ZERO PT_-1 ) DU_-4 ) PT_-3 ) ) ) ( ADD DU_-4 OC_3 ) ) ) )" +
    		" )");
    Object[] nodes = TreeGraph.depthFirstSearch( treeModel ).toArray();
    String[] nodeSymbols = new String[nodes.length];
    
    for( int i = 0; i < nodes.length; i++ )
    {
      nodeSymbols[i] = ((DefaultMutableTreeNode)nodes[i]).getUserObject().toString();
    }
    Arrays.sort( nodeSymbols );
    
    printHistgram( nodeSymbols );
  }
  
  private static void printHistgram(String[] nodeSymbols)
  {
    int currentSymbolNum = 0;
    String currentSymbol = "";
    
    for( int i = 0; i < nodeSymbols.length; i++ )
    {
      if( currentSymbol.equals( nodeSymbols[i] ) )
      {
	currentSymbolNum++;
      }
      else 
      {
	if( currentSymbolNum != 0 )
	  System.out.println( currentSymbol + ": "+ currentSymbolNum );
	currentSymbol = nodeSymbols[i];
	currentSymbolNum = 1;
      }
    }
    
    if( currentSymbolNum != 0 )
      System.out.println( currentSymbol + ": "+ currentSymbolNum );
    
    System.out.println("The Number of Symbols = " + nodeSymbols.length );
  }
}
