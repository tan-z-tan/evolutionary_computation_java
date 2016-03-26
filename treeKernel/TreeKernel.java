package treeKernel;

import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import visualization.S_ExpressionHandler;

public class TreeKernel
{
	/** return kernel value for two trees. */
	public static double treeKernel(TreeModel treeA, TreeModel treeB)
	{
		if( treeA.getRoot() instanceof DefaultMutableTreeNode && treeB.getRoot() instanceof DefaultMutableTreeNode )
		{
			List<TreeNode> listOfA = depthFirstSearch( (TreeNode)treeA.getRoot() );
			List<TreeNode> listOfB = depthFirstSearch( (TreeNode)treeB.getRoot() );
			double kernelValue = 0;
			for( TreeNode nodeA: listOfA )
			{
				for( TreeNode nodeB: listOfB )
				{
					kernelValue += calculateKernel((DefaultMutableTreeNode)nodeA, (DefaultMutableTreeNode)nodeB, 1);
				}
			}
			return kernelValue;
		}
		return 0;
	}
	
	/** return kernel value for two trees. */
	public static double treeSameRatio(TreeModel treeA, TreeModel treeB)
	{
		if( treeA.getRoot() instanceof DefaultMutableTreeNode && treeB.getRoot() instanceof DefaultMutableTreeNode )
		{
			List<TreeNode> listOfA = depthFirstSearch( (TreeNode)treeA.getRoot() );
			List<TreeNode> listOfB = depthFirstSearch( (TreeNode)treeB.getRoot() );
			double kernelValue = 1;
			for( TreeNode nodeA: listOfA )
			{
				for( TreeNode nodeB: listOfB )
				{
					kernelValue *= calculateKernel((DefaultMutableTreeNode)nodeA, (DefaultMutableTreeNode)nodeB, 0);
				}
			}
			return kernelValue;
		}
		return 0;
	}
	
	/** return result List of DFS(depth first search).
	 * @param node to be started
	 * @return the result list of DFS
	 */
	public static List<TreeNode> depthFirstSearch(TreeNode node)
	{
		List<TreeNode> nodeList = new ArrayList<TreeNode>();
		nodeList.add(node);
		for( int i = 0; i < node.getChildCount(); i++)
		{
			nodeList.addAll( depthFirstSearch(node.getChildAt(i)) );
		}
		return nodeList;
	}
	
	/** this method is used in calculating kernel value.
	 * @param nodeA
	 * @param nodeB
	 * @return 
	 */
	private static double calculateKernel(DefaultMutableTreeNode nodeA, DefaultMutableTreeNode nodeB, double addition)
	{
		//System.out.println( nodeA.getUserObject() + " " + nodeB.getUserObject() );
		if( !nodeA.getUserObject().equals(nodeB.getUserObject()) )
		{
			return 0;
		}
		// now, two labels is same
		if( nodeA.getChildCount() == 0 || nodeB.getChildCount() == 0 )
		{
			return 1;
		}
		double value = 1;
		int size = Math.min(nodeA.getChildCount(), nodeB.getChildCount());
		for( int i = 0; i < size; i++ )
		{
			value *= addition + calculateKernel((DefaultMutableTreeNode)nodeA.getChildAt(i), (DefaultMutableTreeNode)nodeB.getChildAt(i), addition);
		}
		return value;
	}
	
	// main method for test
	public static void main(String args[])
	{
		TreeModel treeA = S_ExpressionHandler.getTreeModelByS_Expression("(+ (* 1 (/ 2 0)) 3)");
		TreeModel treeB = S_ExpressionHandler.getTreeModelByS_Expression("(+ (* 1 (/ 2 0)) 3)");
		TreeModel treeC = S_ExpressionHandler.getTreeModelByS_Expression("(+ (* 1 (/ 2 (/ 5 8))) 3)");
		TreeModel treeD = S_ExpressionHandler.getTreeModelByS_Expression("(+ 1 (/ 2 0))");
		TreeModel treeE = S_ExpressionHandler.getTreeModelByS_Expression("(3 (2 1))");
		TreeModel treeF = S_ExpressionHandler.getTreeModelByS_Expression("(4 (2 1) 3)");
		TreeModel treeS = S_ExpressionHandler.getTreeModelByS_Expression("(4 (3 1 2))");
		
		double treeKernelValue = treeKernel(treeA, treeC);
		System.out.println( "K(A, B) " + treeKernel(treeA, treeB) );
		System.out.println( "K(A, C) " + treeKernel(treeA, treeC) );
		System.out.println( "K(A, D) " + treeKernel(treeA, treeD) );
		System.out.println( "K(C, D) " + treeKernel(treeC, treeD) );
		System.out.println( "K(A, E) " + treeKernel(treeA, treeE) );
		System.out.println( "K(E, F) " + treeKernel(treeE, treeF) );
		System.out.println( "K(S, S) " + treeKernel(treeS, treeS) );
		System.out.println( "K(S, S) " + treeKernel(treeE, treeS) );
	}
}
