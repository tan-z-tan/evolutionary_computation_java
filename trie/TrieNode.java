package trie;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import random.RandomManager;

public class TrieNode<E extends Comparable<?>> implements Comparable<TrieNode<E>>
{
	private E element;
	private double value;
	private TrieNode<E> parent;
	private List<TrieNode<E>> children;
	private double smoothingProbability = 0.0;
	
	public static void main(String[] args)
	{
		TrieNode<String> tree = new TrieNode<String>(null);
		tree.count( Arrays.asList("This", "is", "a", "pen") );
		tree.count( Arrays.asList("This", "is", "a", "cup") );
		tree.count( Arrays.asList("This", "is", "a", "milk") );
		tree.count( Arrays.asList("This", "is", "an", "apple") );
		tree.count( Arrays.asList("This", "is", "my", "hand") );
		tree.count( Arrays.asList("A", "to", "tea", "ted", "ten", "i", "in", "inn") );
		
		//tree.normalize(tree.getValue());
		System.out.println(tree);
		
		System.out.println( tree.getNode(Arrays.asList("This", "is", "a")));
		System.out.println( tree.getRandomNext(Arrays.asList("This", "is", "a")));
		System.out.println( tree.getRandomNext(Arrays.asList("This", "is", "a")));
		System.out.println( tree.getRandomNext(Arrays.asList("This", "is", "a")));
		
		
		//TrieNode<Integer> a = TrieNode.constructIntegerTrie("( null[234.0] ( 1[37.0] ( 0[16.0] ( 0[5.0] ( -1[3.0] ( 0[2.0] ( 0[2.0] 0[2.0] ) ) ) ( 0[2.0] ( 0[1.0] ( 0[1.0] -1[1.0] ) ) ( -1[1.0] ( 0[1.0] 1[1.0] ) ) ) ) ( 2[6.0] ( 0[6.0] ( -5[1.0] ( 0[1.0] 5[1.0] ) ) ( 2[2.0] ( 0[2.0] -5[2.0] ) ) ( 0[3.0] ( 0[3.0] 0[3.0] ) ) ) ) ( -5[4.0] ( 0[4.0] ( 7[2.0] ( 0[2.0] 5[2.0] ) ) ( 5[2.0] ( 0[2.0] -9[2.0] ) ) ) ) ) ( -2[5.0] ( 0[5.0] ( 2[5.0] ( 0[4.0] ( 1[3.0] 0[1.0] ) ) ) ) ) ( 3[1.0] ( 0[1.0] ( 5[1.0] ( 0[1.0] ( 0[1.0] 0[1.0] ) ) ) ) ) ( 2[5.0] ( 0[5.0] ( 2[1.0] ( 0[1.0] ( 1[1.0] 0[1.0] ) ) ) ( 1[3.0] ( 0[3.0] ( -5[3.0] 0[3.0] ) ) ) ( 0[1.0] ( 0[1.0] ( 0[1.0] 0[1.0] ) ) ) ) ) ( -1[7.0] ( 0[1.0] ( 1[1.0] ( 0[1.0] ( 2[1.0] 0[1.0] ) ) ) ) ( 1[6.0] ( -1[3.0] ( 1[3.0] ( 2[3.0] 0[3.0] ) ) ) ( 5[2.0] ( 0[2.0] ( -7[2.0] 0[2.0] ) ) ) ( 2[1.0] ( 0[1.0] ( 1[1.0] 0[1.0] ) ) ) ) ) ) ( 0[125.0] ( 0[70.0] ( 0[52.0] ( 1[7.0] ( 0[6.0] 0[1.0] ( 1[2.0] 0[2.0] ) ( -1[1.0] 0[1.0] ) ) ) ( 0[30.0] ( 0[25.0] ( 0[13.0] 0[9.0] 1[2.0] -12[2.0] ) ( 1[5.0] -1[3.0] 0[2.0] ) ( 2[3.0] -2[3.0] ) ( -12[3.0] 0[3.0] ) ( -2[1.0] -1[1.0] ) ) ( -1[2.0] ( -2[2.0] 2[2.0] ) ) ( 1[2.0] ( -1[1.0] 0[1.0] ) ( 0[1.0] 1[1.0] ) ) ( -12[1.0] ( 0[1.0] 0[1.0] ) ) ) ( -1[6.0] ( -2[1.0] ( 2[1.0] 0[1.0] ) ) ( 0[4.0] ( 1[2.0] 0[2.0] ) ( -2[2.0] 0[2.0] ) ) ( 1[1.0] ( -1[1.0] 1[1.0] ) ) ) ( 2[2.0] ( -2[2.0] ( -2[2.0] 0[2.0] ) ) ) ( -12[3.0] ( 0[3.0] ( 0[3.0] 0[3.0] ) ) ) ( -2[1.0] ( -1[1.0] ( 1[1.0] 0[1.0] ) ) ) ) ( -1[9.0] ( 0[4.0] ( 0[2.0] ( 0[2.0] 0[2.0] ) ) ( 1[2.0] ( 0[2.0] -5[2.0] ) ) ) ( -2[2.0] ( 2[2.0] ( 0[2.0] -4[1.0] -12[1.0] ) ) ) ( 1[3.0] ( -1[3.0] ( 1[3.0] -1[3.0] ) ) ) ) ( 1[3.0] ( -1[1.0] ( 0[1.0] ( 1[1.0] 0[1.0] ) ) ) ( 0[2.0] ( 1[1.0] ( 0[1.0] 0[1.0] ) ) ( -1[1.0] ( 0[1.0] 1[1.0] ) ) ) ) ( 2[1.0] ( -2[1.0] ( -2[1.0] ( 0[1.0] 0[1.0] ) ) ) ) ( -12[2.0] ( 0[2.0] ( 0[2.0] ( 0[2.0] -1[2.0] ) ) ) ) ( -2[1.0] ( -1[1.0] ( 1[1.0] ( 0[1.0] -5[1.0] ) ) ) ) ) ( -1[8.0] ( 0[3.0] ( 0[3.0] ( 0[3.0] ( 0[3.0] 0[3.0] ) ) ) ) ( -2[1.0] ( 2[1.0] ( 0[1.0] ( -4[1.0] 0[1.0] ) ) ) ) ( 1[4.0] ( 2[1.0] ( 0[1.0] ( 2[1.0] 0[1.0] ) ) ) ( 3[1.0] ( 0[1.0] ( 5[1.0] 0[1.0] ) ) ) ( -1[2.0] ( 1[2.0] ( -1[2.0] 1[2.0] ) ) ) ) ) ( 1[10.0] ( 0[9.0] ( 2[2.0] ( 0[2.0] ( -5[1.0] 0[1.0] ) ( 2[1.0] 0[1.0] ) ) ) ( 1[1.0] ( 0[1.0] ( 0[1.0] 0[1.0] ) ) ) ( -1[1.0] ( 0[1.0] ( 1[1.0] -3[1.0] ) ) ) ( 0[1.0] ( 0[1.0] ( -1[1.0] 0[1.0] ) ) ) ( -5[4.0] ( 0[4.0] ( 7[1.0] 0[1.0] ) ( 5[3.0] 0[3.0] ) ) ) ) ( -1[1.0] ( 0[1.0] ( 1[1.0] ( 0[1.0] 2[1.0] ) ) ) ) ) ( 2[13.0] ( 0[9.0] ( -5[4.0] ( 0[4.0] ( 5[4.0] 0[4.0] ) ) ) ( 2[2.0] ( 0[2.0] ( -5[2.0] 0[2.0] ) ) ) ( 0[1.0] ( 0[1.0] ( 0[1.0] 0[1.0] ) ) ) ( 1[2.0] ( 0[2.0] ( -5[2.0] 0[2.0] ) ) ) ) ( -2[2.0] ( -2[2.0] ( 0[2.0] ( 0[2.0] 0[2.0] ) ) ) ) ( 2[1.0] ( -1[1.0] ( 1[1.0] ( -1[1.0] 1[1.0] ) ) ) ) ( 1[1.0] ( 2[1.0] ( 0[1.0] ( 0[1.0] 0[1.0] ) ) ) ) ) ( -5[5.0] ( 0[5.0] ( 5[4.0] ( 0[4.0] ( -2[3.0] 0[3.0] ) ( -8[1.0] 7[1.0] ) ) ) ( 7[1.0] ( 0[1.0] ( 5[1.0] 0[1.0] ) ) ) ) ) ( -4[2.0] ( 0[2.0] ( -1[2.0] ( 1[2.0] ( 2[2.0] 0[2.0] ) ) ) ) ) ( 5[9.0] ( 0[8.0] ( 0[4.0] ( 0[4.0] ( 0[4.0] 0[4.0] ) ) ) ( 2[1.0] ( 2[1.0] ( -1[1.0] 1[1.0] ) ) ) ( -2[2.0] ( 0[2.0] ( -1[2.0] 1[2.0] ) ) ) ( -8[1.0] ( 7[1.0] ( -2[1.0] 0[1.0] ) ) ) ) ( 3[1.0] ( -3[1.0] ( 0[1.0] ( 0[1.0] 0[1.0] ) ) ) ) ) ( -2[3.0] ( 0[3.0] ( -2[1.0] ( 0[1.0] ( 4[1.0] 0[1.0] ) ) ) ( 4[1.0] ( 0[1.0] ( 5[1.0] 0[1.0] ) ) ) ( -1[1.0] ( 1[1.0] ( -1[1.0] 1[1.0] ) ) ) ) ) ( 4[1.0] ( 0[1.0] ( 5[1.0] ( 0[1.0] ( 0[1.0] 0[1.0] ) ) ) ) ) ( -7[2.0] ( 0[2.0] ( 0[2.0] ( 0[2.0] ( 0[2.0] 0[2.0] ) ) ) ) ) ( -12[2.0] ( 0[2.0] ( 0[2.0] ( 0[2.0] ( -1[2.0] 0[2.0] ) ) ) ) ) ) ( -2[11.0] ( 0[8.0] ( 2[1.0] ( 0[1.0] ( 1[1.0] ( 0[1.0] 2[1.0] ) ) ) ) ( -1[2.0] ( 1[2.0] ( 3[2.0] ( 0[2.0] 5[2.0] ) ) ) ) ( -2[2.0] ( 0[2.0] ( 4[2.0] ( 0[2.0] 5[2.0] ) ) ) ) ( 4[2.0] ( 0[2.0] ( 5[2.0] ( 0[1.0] 0[1.0] ) ( 3[1.0] -3[1.0] ) ) ) ) ( 0[1.0] ( 0[1.0] ( -1[1.0] ( 1[1.0] -1[1.0] ) ) ) ) ) ( 2[2.0] ( 0[2.0] ( -4[1.0] ( 0[1.0] ( -1[1.0] 1[1.0] ) ) ) ( -5[1.0] ( -7[1.0] ( 2[1.0] 0[1.0] ) ) ) ) ) ( -1[1.0] ( 0[1.0] ( 0[1.0] ( 0[1.0] ( 1[1.0] 0[1.0] ) ) ) ) ) ) ( -1[22.0] ( 0[7.0] ( 0[2.0] ( 0[2.0] ( 0[2.0] ( 0[2.0] 0[2.0] ) ) ) ) ( 1[3.0] ( 0[3.0] ( 2[2.0] ( 0[2.0] 2[2.0] ) ) ( -5[1.0] ( 0[1.0] 7[1.0] ) ) ) ) ( -2[2.0] ( 0[2.0] ( 12[2.0] ( -1[2.0] 1[2.0] ) ) ) ) ) ( -2[4.0] ( 2[4.0] ( 0[4.0] ( -4[3.0] ( 0[3.0] -1[3.0] ) ) ( -12[1.0] ( 0[1.0] 2[1.0] ) ) ) ) ) ( 1[11.0] ( 3[1.0] ( 0[1.0] ( 5[1.0] ( 0[1.0] 0[1.0] ) ) ) ) ( 2[3.0] ( 0[3.0] ( 2[1.0] ( 0[1.0] 1[1.0] ) ) ( 1[2.0] ( 0[2.0] -5[2.0] ) ) ) ) ( -1[6.0] ( 1[6.0] ( -1[3.0] ( 1[3.0] 2[2.0] 0[1.0] ) ) ( 0[3.0] ( 0[3.0] 0[3.0] ) ) ) ) ( 5[1.0] ( 0[1.0] ( -7[1.0] ( 0[1.0] 0[1.0] ) ) ) ) ) ) ( -4[1.0] ( 0[1.0] ( -1[1.0] ( 1[1.0] ( 2[1.0] ( 0[1.0] 2[1.0] ) ) ) ) ) ) ( 2[17.0] ( 0[13.0] ( 2[4.0] ( 0[4.0] ( 1[3.0] ( 0[3.0] 2[1.0] -5[2.0] ) ) ( -5[1.0] ( 0[1.0] 5[1.0] ) ) ) ) ( 1[4.0] ( 0[4.0] ( 2[2.0] ( 0[2.0] 0[2.0] ) ) ( -5[2.0] ( 0[2.0] 5[2.0] ) ) ) ) ( 0[2.0] ( 0[2.0] ( 0[2.0] ( 0[2.0] 1[1.0] -2[1.0] ) ) ) ) ( -5[2.0] ( 0[2.0] ( 5[2.0] ( 0[2.0] -2[2.0] ) ) ) ) ( -2[1.0] ( 0[1.0] ( 2[1.0] ( 0[1.0] 2[1.0] ) ) ) ) ) ( -2[3.0] ( -1[2.0] ( 0[2.0] ( 0[2.0] ( 0[2.0] 1[2.0] ) ) ) ) ( -2[1.0] ( 0[1.0] ( 0[1.0] ( 0[1.0] -1[1.0] ) ) ) ) ) ( -1[1.0] ( 1[1.0] ( -1[1.0] ( 1[1.0] ( -1[1.0] 1[1.0] ) ) ) ) ) ) ( 5[7.0] ( 0[7.0] ( -2[4.0] ( 0[4.0] ( -2[2.0] ( 0[2.0] 4[2.0] ) ) ( -1[2.0] ( 1[2.0] -1[2.0] ) ) ) ) ( 0[1.0] ( 0[1.0] ( 0[1.0] ( 0[1.0] 2[1.0] ) ) ) ) ( -7[1.0] ( 0[1.0] ( 0[1.0] ( 0[1.0] 0[1.0] ) ) ) ) ( -8[1.0] ( 7[1.0] ( -2[1.0] ( 0[1.0] 2[1.0] ) ) ) ) ) ) ( 4[1.0] ( 0[1.0] ( 5[1.0] ( 0[1.0] ( 0[1.0] ( 0[1.0] 0[1.0] ) ) ) ) ) ) ( -5[7.0] ( 0[5.0] ( 7[2.0] ( 0[2.0] ( 5[2.0] ( 0[2.0] 2[2.0] ) ) ) ) ( 5[3.0] ( 0[3.0] ( -2[1.0] ( 0[1.0] -1[1.0] ) ) ( -1[1.0] ( 0[1.0] -2[1.0] ) ) ( -8[1.0] ( 7[1.0] -2[1.0] ) ) ) ) ) ( -7[2.0] ( 2[2.0] ( 0[2.0] ( -2[2.0] ( 0[2.0] 2[2.0] ) ) ) ) ) ) ( 7[1.0] ( 0[1.0] ( 5[1.0] ( 0[1.0] ( 2[1.0] ( 2[1.0] -1[1.0] ) ) ) ) ) ) ( -7[2.0] ( 0[2.0] ( 0[2.0] ( 0[2.0] ( 0[2.0] ( 0[2.0] 0[2.0] ) ) ) ) ) ) ( -12[3.0] ( 0[3.0] ( 0[3.0] ( 0[3.0] ( -1[3.0] ( 0[3.0] -2[3.0] ) ) ) ) ) ) ) ");
		//System.out.println("-----------");
		//System.out.println(a);
	}
	
	public TrieNode()
	{
		this(null);
	}
	
	public TrieNode(E e)
	{
		this.element = e;
		this.children = new ArrayList<TrieNode<E>>();
		this.parent = null;
		this.value = 0;
	}
	
	public static TrieNode<Integer> constructIntegerTrie(File file)
	{
		try{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine();
			return constructIntegerTrie(line);
		} catch(Exception e) {e.printStackTrace();}
		
		return null;
	}
	
	public static TrieNode<Integer> constructIntegerTrie(String str)
	{
		str = str.replace("(", " ( ");
		str = str.replace(")", " ) ");
		str = str.trim();
		String[] tokens = str.split("\\s+");
		
		TrieNode<Integer> root = new TrieNode<Integer>();
		
		Stack<TrieNode<Integer>> stack = new Stack<TrieNode<Integer>>();
		stack.push(root);
		TrieNode<Integer> node = root;
		
		boolean isParentPosition = true;
		for( String token: tokens )
		{
			if( token.equals("(") )
			{
				TrieNode<Integer> newParent = new TrieNode<Integer>();
				node.addChild(newParent);
				stack.push( node );
				node = newParent;
				isParentPosition = true;
			} else if( token.equals(")") )
			{
				node = stack.pop();
			} else {
				String elementStr = token.substring(0, token.indexOf("["));
				Integer element = null;
				if( !elementStr.equals("null") )
				{
					element = Integer.valueOf( elementStr );
				}
				Double value = Double.valueOf( token.substring(token.indexOf("[") + 1, token.indexOf("]")) );
				if( isParentPosition )
				{
					node.setElement(element);
					node.setValue(value);
					isParentPosition = false;
				} else {
					TrieNode<Integer> child = new TrieNode<Integer>();
					child.setElement(element);
					child.setValue(value);
					node.addChild( child );
				}
			}
		}
		
		return root.getChild(0);
	}
	
	
	/** データの追加 */
	public void count(List<E> sequence)
	{
		if( sequence ==null || sequence.size() <= 0 )
		{
			return;
		}
		
		setValue(getValue() + 1);
		TrieNode<E> node = this;
		for( int i = 0; i < sequence.size(); i++ )
		{
			E e = sequence.get(i);
						
			int index = node.getChildIndex(e);
			if( index == -1 )
			{
				node = node.addChild(e); // value -> 1
				node.setValue(1);
			} else {
				node = node.getChild(index);
				node.setValue(node.getValue() + 1);
			}
		}
	}
	
	/** データの追加 */
	public void count(E[] sequence)
	{
		count(Arrays.asList(sequence));
	}
	
	/** 合致するノードを返す */
	public TrieNode<E> getNode(List<E> sequence)
	{
		if( sequence ==null || sequence.size() <= 0 )
		{
			return null;
		}
		
		TrieNode<E> node = this;
		for( int i = 0; i < sequence.size(); i++ )
		{
			E e = sequence.get(i);
						
			int index = node.getChildIndex(e);
			if( index == -1 )
			{
				return null;
			} else {
				node = node.getChild(index);
			}
		}
		return node;
	}
	
	public TrieNode<E> addChild(E e)
	{
		TrieNode<E> child = new TrieNode<E>(e);
		children.add(child);
		return child;
	}
	
	public TrieNode<E> addChild(TrieNode<E> child)
	{
		children.add(child);
		return child;
	}
	
	/** 次に値がnextをとる確率を返す． */
	public double getProbability(E next)
	{
		double sum = 0;
		double nextCount = 0;
		for( TrieNode<E> c: this.getChildren() )
		{
			sum += c.getValue();
			if( c.getElement().equals(next) )
			{
				nextCount = c.getValue();
			}
		}
		
		if( nextCount == 0 )
		{
			return smoothingProbability;
		}
		
		return nextCount / sum;
	}
	
	/** 系列contextまで進んだ状態で次に，値がnextをとる確率を返す． */
	public double getProbability(List<E> context, E next)
	{
		double p = 1;
		
		TrieNode<E> node = this;
		for( int i = 0; i < context.size(); i++ )
		{
			E e = context.get(i);
			int index = node.getChildIndex( e );
			if( index != -1 )
			{
				node = node.getChild(index);
			} else {
				node = null;
				break;
			}
		}
		
		if( node != null )
		{
			return node.getProbability(next);
		}
		
		return smoothingProbability;
	}
	
	/** 子ノードを確率値に応じてランダムに返す． */
	public TrieNode<E> getRandomNext()
	{
		TrieNode<E> last = this;
		if( last == null || last.isTerminal() )
		{
			return null;
		}
		
		double rand = RandomManager.getRandom() * last.getValue();
		double sum = 0;
		for( TrieNode<E> c: last.getChildren() )
		{
			sum += c.getValue();
			if( sum >= rand )
			{
				return c;
			}
		}
		
		return null;
	}
	
	/** 子ノードを確率値に応じてランダムに返す． */
	public TrieNode<E> getRandomNext(List<E> sequence)
	{
		TrieNode<E> last = getNode(sequence);
		if( last == null || last.isTerminal() )
		{
			return null;
		}
		
		double rand = RandomManager.getRandom() * last.getValue();
		double sum = 0;
		for( TrieNode<E> c: last.getChildren() )
		{
			sum += c.getValue();
			if( sum >= rand )
			{
				return c;
			}
		}
		
		return null;
	}
	
	public TrieNode<E> getChild(int index)
	{
		return children.get(index);
	}
	
	/** その値のインデックスを返す．見つからなければ-1を返す． */
	public int getChildIndex(E e)
	{
		for( int i = 0; i < children.size(); i++ )
		{
			if( children.get(i).element.equals(e) )
			{
				return i;
			}
		}
		return -1;
	}
	
	public boolean isTerminal()
	{
		return children == null || children.isEmpty();
	}
	
	public void normalize(double normalizeFactor)
	{
		this.setValue( this.getValue() / normalizeFactor );
		for( TrieNode<E> c: children )
		{
			c.normalize(normalizeFactor);
		}
	}
	
	@Override
	public String toString()
	{
		if( isTerminal() )
		{
			if( element != null )
			{
				return element.toString() + "[" + value +"]";
			}
		}
		
		StringBuilder str = new StringBuilder("( ");
		str.append(element).append("[" + value +"]").append(" ");
		
		for( TrieNode<E> c: children )
		{
			str.append(c).append(" ");
		}
		str.append(")");
		return str.toString();
	}
	
	@Override
	public int compareTo(TrieNode<E> o)
	{
		return 0;
		//return o.compareTo(this.element);
	}
	
	// --- getter and setter ---
	public E getElement()
	{
		return element;
	}

	public void setElement(E element)
	{
		this.element = element;
	}

	public double getValue()
	{
		return value;
	}

	public void setValue(double value)
	{
		this.value = value;
	}

	public TrieNode<E> getParent()
	{
		return parent;
	}

	public void setParent(TrieNode<E> parent)
	{
		this.parent = parent;
	}

	public List<TrieNode<E>> getChildren()
	{
		return children;
	}
}
