package math.combination;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * n×m マトリックスを考える．
 * 各行に重複しないように一つだけ１を割り当て，その行の他は全て0を割り当てる．
 * 列に関しても，各列に１は最大でも一つだけ(0個でもOK)．ただし最後の列は任意の数の1を持っても良い．
 * その組み合わせの数．
 */
public class AssignMatrixCombination
{
	public static void main(String[] args)
	{
//		System.out.println( beta(2, 3) );
//		System.out.println();
//		System.out.println( assignMatrix(2, 3) );
//		System.out.println( assignMatrix(2, 4) );
//		System.out.println();
		//System.out.println( assignMatrix(3, 2) );
		//System.out.println( assignMatrix(4, 2) );
//		System.out.println( assignMatrix(5, 2) );
		System.out.println();
//		System.out.println( assignMatrix(1, 1) );
//		System.out.println( assignMatrix(2, 2) );
//		System.out.println( assignMatrix(3, 3) );
//		System.out.println( assignMatrix(3, 2) );
//		System.out.println( assignMatrix(4, 4) );
//		System.out.println( assignMatrix(10, 4) );
//		System.out.println( assignMatrix(11, 4) );
//		System.out.println( assignMatrix(12, 4) );
//		System.out.println( assignMatrix(13, 4) );
		System.out.println( assignMatrix(10, 5) );
		System.out.println( assignMatrix(5, 10) );
		System.out.println( assignMatrix(3, 5) );
		
		System.out.println();
//		System.out.println( numberofAssociations(0, 1));
//		System.out.println( numberofAssociations(1, 2));
//		System.out.println( numberofAssociations(2, 3));
//		System.out.println( numberofAssociations(3, 4));
		System.out.println( numberofAssociations(10, 4) );
		System.out.println( numberofAssociations(5, 9) );
		System.out.println( numberofAssociations(3, 4) );
		
		System.out.println("-----------");
		DefaultMutableTreeNode node = enumerateUniqueAssociation(3, 3);
		List<Integer> rowIndices = new ArrayList<Integer>();
		rowIndices.add(1);
		rowIndices.add(2);
		rowIndices.add(4);
		List<int[]> list = enumerateFromTree(node, rowIndices, 7);
				
		System.out.println(list);
		
		System.out.println("----------");
		int rowSize = 3;
		int columnSize = 4;
		List<int[]> associationList = new ArrayList<int[]>();
		associationList = enumerateAssociationCombination(rowSize, columnSize);
		for( int[] association: associationList )
		{
			System.out.println(" " + Arrays.toString(association));
		}
		System.out.println(associationList.size());
	}
	
	/** rowSize * columnSize　の行列で割り当てのパターンを列挙する． */
	public static List<int[]> enumerateAssociationCombination(int rowSize, int columnSize)
	{
		List<int[]> resultList = new ArrayList<int[]>();
		
		for( int activeRowSize = 1; activeRowSize < 1 + Math.min(rowSize, columnSize); activeRowSize++ )
		{
			// create combination
			Integer[] indices = new Integer[rowSize];
			for( int i = 0; i < indices.length; i++ )
			{
				indices[i] = i;
			}
			CombinationEnumerator<Integer> comb = new CombinationEnumerator<Integer>(indices, activeRowSize);
			List<List<Integer>> combination = comb.getCombinations();
			
			for( List<Integer> activeRowIndices: combination )
			{
				DefaultMutableTreeNode node = enumerateUniqueAssociation(activeRowSize, columnSize);
				List<int[]> list = enumerateFromTree(node, activeRowIndices, rowSize);
				resultList.addAll(list);	
			}
		}
		
		return resultList;
	}
	
	/**
	 * 割り当ての組み合わせ木からリストを作る．
	 * rowIndicesは割り当られる行のインデックスリスト．
	 * 割り当てがないインデックスにはクラッター(-1)を入れる．
	 * @param root
	 * @param rowIndices
	 * @param rowSize
	 * @return
	 */
	public static List<int[]> enumerateFromTree(DefaultMutableTreeNode root, List<Integer> rowIndices, int rowSize)
	{
		List<Map<Integer, Integer>> resultMapList = new ArrayList<Map<Integer, Integer>>();
		Stack<DefaultMutableTreeNode> stack = new Stack<DefaultMutableTreeNode>();
		stack.add(root);
		Stack<Map<Integer, Integer>> stateStack = new Stack<Map<Integer, Integer>>();
		//List<Integer> initialIndices = new LinkedList<Integer>();
		Map<Integer, Integer> initialIndices = new HashMap<Integer, Integer>();
		for( int r = 0; r < rowSize; r++ )
		{
			if( rowIndices.contains(r) )
			{
				//initialIndices.put(r, 999);
			} else {
				//initialIndices.put(r, -1);
			}
		}
		stateStack.push(initialIndices);
		
		while( !stack.isEmpty() )
		{
			DefaultMutableTreeNode n = stack.pop();
			Map<Integer, Integer> indexMap = stateStack.pop();
			
			if( n.getChildCount() == 0 )
			{
				resultMapList.add(indexMap);
			}
			for( int i = 0; i < n.getChildCount(); i++ )
			{
				DefaultMutableTreeNode child = (DefaultMutableTreeNode)n.getChildAt(i);
				
				Map<Integer, Integer> childSeq = new HashMap<Integer, Integer>(indexMap);
				childSeq.put(rowIndices.get(childSeq.size()), (Integer)child.getUserObject());
				
				stack.push(child);
				stateStack.push(childSeq);
			}
		}
		
		List<int[]> result = new ArrayList<int[]>();
		for( Map<Integer, Integer> indicesMap: resultMapList )
		{
			int[] oneSort = new int[rowSize];
			for( int r = 0; r < rowSize; r++ )
			{
				if( !indicesMap.containsKey(r) )
				{
					oneSort[r] = -1;
					indicesMap.put(r, -1);
				} else {
					oneSort[r] = indicesMap.get(r);
				}
			}
			result.add(oneSort);
		}
				
		return result;
	}
	
	/** N * M matrix. no clutter. 割り当て木を生成する． */
	public static DefaultMutableTreeNode enumerateUniqueAssociation(int rowSize, int columnSize)
	{
		List<Integer> columnBit = new ArrayList<Integer>();
		for( int m = 0; m < columnSize; m++ )
		{
			columnBit.add(m);
		}
		
		Stack<List<Integer>> indicesStack = new Stack<List<Integer>>();
		indicesStack.push(columnBit);
		
		Stack<DefaultMutableTreeNode> treeStack = new Stack<DefaultMutableTreeNode>();
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(null);
		treeStack.push(root);
		
		while( !indicesStack.isEmpty() )
		{
			List<Integer> availableColumn = indicesStack.pop();
			DefaultMutableTreeNode node = treeStack.pop();
			
			int bitCount = availableColumn.size();
			if( columnSize - bitCount == rowSize )
			{
				
			} else {
				for( int i = 0; i < availableColumn.size(); i++ )
				{
					List<Integer> childState = new ArrayList<Integer>(availableColumn);
					childState.remove(i);
					DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(availableColumn.get(i));
					indicesStack.push(childState);
					node.add(childNode);
					treeStack.push(childNode);
				}
			}
		}
		
		return root;
	}
	
	/** see the paper "Extended Object Tracing using Particle Techniques", 2004.
	 * n*mの行列の割り当てに関する組み合わせ数を返す．論文バージョン．
	 * @return */
	public static long numberofAssociations(int n, int m)
	{
		long sum = 0;
		for( int i = 0; i <= Math.min(n, m); i++ )
		{
			sum += n_lambda(i, n, m);
		}
		return sum;
	}
	
	public static long n_lambda(int n_t, int n_p, int m)
	{
		return (fact(m) * fact(n_p)) / (fact(n_t) * fact(m - n_t) * fact(n_p - n_t));
	}
	
	/** n*mの行列の割り当てに関する組み合わせ数を返す．丹治バージョン．*/
	public static long assignMatrix(int n, int m)
	{
		long sum = 0;
		
		if( n < m ) {
			sum += beta(n, m-1);
			for( int i = 1; i <= n; i++ )
			{
				sum += comb(n, i) * beta(n-i, m-1);
			}
		} else {
			for( int i = (n-m+1); i <= n; i++ )
			{
				sum += comb(n, i) * beta(n-i, m-1);
			}
		}
			
		return sum;
	}
	
	public static long fact(int n)
	{
		if(n <= 1)
		{
			return 1;
		}
		return n * fact(n-1);
	}
	
	public static long comb(int n, int m)
	{
		return fact(n) / (fact(m) * fact(n-m));
	}
	
	/** n * m行で重複なく各行に一つ割り当てる数．n <= mでしか呼び出されないが一応チェックしている */
	public static long beta(int n, int m)
	{
		if( n > m )
		{
			int tmp = n;
			n = m;
			m = tmp;
		}
		return fact(m) / fact(m-n);
	}
}
