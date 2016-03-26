package ports;

import geneticProgramming.GpEnvironment;
import geneticProgramming.GpIndividual;
import geneticProgramming.GpNode;
import geneticProgramming.GpSymbolSet;
import geneticProgramming.GpTreeManager;
import geneticProgramming.symbols.SymbolType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import random.RandomManager;

/** An implementation of POMS (Program Optimization by Monte carlo Search). 
 * @author tanji
 */
public class PORTE_Core
{
	private List<GpNode> _promisingSolutions;
	private GpSymbolSet _symbolSet;
	private GpEnvironment<? extends GpIndividual> _environment;
	
	// 2008 12 15
	private List<Set<SymbolType>> _transitionMatrices;
	
	private int _maximumNodeSize;
	private int _sumOfTreeSize;
	
	private double _probabilityT;
	private double _probabilityAlpha;
	
	private Map<Integer, Integer> _lastConstructionSizeMap;
	private Map<Integer, Integer> _allConstructionSizeMap;
	private List<Double> _offspringSizeList;
	
	/**
	 * constructor
	 * @param promisingSolutions
	 * @param environment
	 */
	public PORTE_Core(List<GpNode> promisingSolutions, GpEnvironment<? extends GpIndividual> environment)
	{
		_promisingSolutions = promisingSolutions;
		_symbolSet = environment.getSymbolSet();
		_environment = environment;
		_probabilityT = Double.valueOf(_environment.getAttribute("porteT") );
		_probabilityAlpha = Double.valueOf(_environment.getAttribute("porteAlpha") );
		
		calculateTransitionMatrices();
		_lastConstructionSizeMap = new HashMap<Integer, Integer>();
		_allConstructionSizeMap = new HashMap<Integer, Integer>();
		_offspringSizeList = new ArrayList<Double>();
	}
	
	/**
	 * 終端記号への遷移を記録する
	 * 終端記号を持たない関数ノードがある場合があるため．
	 */
	private void calculateTransitionMatrices()
	{
		_transitionMatrices = new ArrayList<Set<SymbolType>>();
		for( int i = 0; i < _environment.getSymbolSet().getSymbolSize(); i++ )
		{
			_transitionMatrices.add(new HashSet<SymbolType>());
		}
		for(GpNode root: _promisingSolutions)
		{
			List<GpNode> bfs = GpTreeManager.breadthFirstSearch(root);
			for( GpNode node: bfs )
			{
				if( node.isNonterminal() )
				{
					int symbolIndex = _symbolSet.getIndex(node.getNodeType());
					for( GpNode child: node.getChildren() )
					{
						if( child.isTerminal() )
						{
							_transitionMatrices.get(symbolIndex).add(node.getNodeType());		
						}
					}
				}
			}
		}
	}
	
	/**
	 * returns random GpNode by method of Monte Carlo Tree 
	 * @return
	 */
	public GpNode getRandomSample()
	{
		return getRandomSample(_probabilityAlpha);
	}
	
	/**
	 * returns random GpNode by method of Monte Carlo Tree 
	 * @return
	 */
	public GpNode getRandomSample(double probabilityAlpha)
	{
		GpNode child = (GpNode)_promisingSolutions.get( (int) (Math.random() * _promisingSolutions.size()) ).shallowClone();
		List<Integer> fragmentList = new ArrayList<Integer>();
		_lastConstructionSizeMap.clear();
		List<GpNode> leaf = new ArrayList<GpNode>();
		leaf.add(child);
		fragmentList.add(1);
		_lastConstructionSizeMap.put(1, 1);
		int depth = 1; // 生成する木の深さ
		int newIndex = 2;
		int nodeSize = 1;
		
		boolean atLeastOneTransition = false;
		
		// 木をランダムに追加
		while(leaf.size() != 0)
		{
			List<GpNode> nextLeaf = new ArrayList<GpNode>();
			List<Integer> nextFragmentList = new ArrayList<Integer>();
			for( int i = 0; i < leaf.size(); i++ )
			{
				GpNode currentLeaf = leaf.get(i);
				for(int childIndex = 0; childIndex < currentLeaf.getNodeType().getArgumentSize(); childIndex++)
				{
					// currentLeafと同じノードを集団から探す
					GpNode copyNode = null;
					// 木の最大深さに達した場合
					boolean isTerminalSearch = (depth + 1 == _environment.getNumberOfMaxDepth());					
					double randValue = RandomManager.getRandom();
					int fragmentIndex = fragmentList.get(i);
					//int currentFractionSize = _lastConstructionSizeMap.get(fragmentList.get(i));
					nodeSize++;
					
					if( randValue <= _probabilityT )
					{
						double r = RandomManager.getRandom();
						if( r < _probabilityAlpha )
						{
							// alpha transition
							copyNode = selectAlphaTransitionNode(_promisingSolutions, isTerminalSearch);
						}
						else
						{
							// beta transition
							copyNode = selectBetaTransitionNode(_promisingSolutions, currentLeaf, childIndex, isTerminalSearch);
						}
						fragmentIndex = newIndex;
						_lastConstructionSizeMap.put(newIndex, 1);
						newIndex++;
						atLeastOneTransition = true;
					}
					else // normal traverse
					{
						// gamma transition
						if( !isTerminalSearch )
						{
							copyNode = (GpNode)currentLeaf.getChild( childIndex );
						}
						else
						{
							if( currentLeaf.getChild(childIndex).isTerminal() )
							{
								copyNode = (GpNode)currentLeaf.getChild( childIndex );
							}
							else
							{
								copyNode = new GpNode(_environment.getSymbolSet().getTerminalSymbol(), depth+1);
								//fragmentIndex = newIndex;
								//_lastConstructionSizeMap.put(fragmentIndex , 0);
								//newIndex++;
								//atLeastOneTransition = true;
							}
						}
					}
					copyNode = copyNode.shallowClone();
					currentLeaf.getChildren().set(childIndex, copyNode);
					nextLeaf.add(copyNode);
					nextFragmentList.add(fragmentIndex);
					_lastConstructionSizeMap.put(fragmentIndex, _lastConstructionSizeMap.get(fragmentIndex) + 1);
				}
			}
			leaf = nextLeaf;
			fragmentList = nextFragmentList;
			depth++;
		}
		if( atLeastOneTransition ) // sampling finished
		{
			GpTreeManager.calculateDepth(child, 1);
			double averageSubsolutionSize = 0;
			for( Integer size: _lastConstructionSizeMap.values() )
			{
				if( _allConstructionSizeMap.containsKey(size) )
				{
					_allConstructionSizeMap.put(size, _allConstructionSizeMap.get(size) + 1);
				}
				else
				{
					_allConstructionSizeMap.put(size, 1);
				}
				averageSubsolutionSize += size;
			}
			_offspringSizeList.add( averageSubsolutionSize / _lastConstructionSizeMap.size() );
			//System.out.println( "Size = " + GpTreeManager.getNodeSize(child) );
			_sumOfTreeSize += nodeSize;
			return child;
		}
		else
		{
			return null;
		}
	}
		
	/** ラベルに関係なくランダムにノードを選択する
	 * β transition
	 * @param parents
	 * @return 
	 */
	public GpNode selectAlphaTransitionNode(List<GpNode> parents, boolean terminalSearch)
	{
		if( !terminalSearch )
		{
			GpNode nextRandomTree = parents.get((int) (Math.random() * parents.size()));
			int size = GpTreeManager.getNodeSize(nextRandomTree);
			return GpTreeManager.getNodeAt(nextRandomTree, ((int)(Math.random() * size)));
		}
		else
		{
			GpNode nextRandomTree = parents.get((int) (Math.random() * parents.size()));
			int size = GpTreeManager.getTerminalNodeSize(nextRandomTree);
			return GpTreeManager.getTerminalNodeAt(nextRandomTree, ((int)(Math.random() * size)));
		}
	}
	
	/** 同じラベルを持つノードをランダムに選択する
	 * α transition
	 * @param parents
	 * @param currentLeaf
	 * @param isMax
	 * @return
	 */
	public GpNode selectBetaTransitionNode(List<GpNode> parents, GpNode currentLeaf, int position, boolean isMax)
	{
		int loop = 0;
		
		// if there are no such transitions (currentLeaf -> terminal symbol). 
		if( isMax && _transitionMatrices.get( _environment.getSymbolSet().getIndex(currentLeaf.getNodeType()) ).size() == 0 )
		{
			System.out.println("there is no terminal transition from " + currentLeaf.getNodeType() + " to terminal symbol.");
			return new GpNode(_environment.getSymbolSet().getTerminalSymbol(), currentLeaf.getDepth() + 1);
		}
		
		List<GpNode> candidateList = new ArrayList<GpNode>();
		while(true)
		{
			GpNode nextRandomTree = parents.get((int) (Math.random() * parents.size()));
			candidateList = GpTreeManager.breadthFirstSearch(nextRandomTree);
			
			List<GpNode> hitNodes = new ArrayList<GpNode>();
			// gets nodes which have same label
			for( GpNode node: candidateList)
			{
				//if( node.getNodeType().getSymbolName().equals(currentLeaf.getNodeType().getSymbolName()) )
				if( node.getNodeType() == currentLeaf.getNodeType() )
				{
					hitNodes.add(node);
				}
			}
			if( hitNodes.size() != 0 )
			{
				GpNode candidate = hitNodes.get( (int)(Math.random() * hitNodes.size()) );
				if( !isMax )
				{
					return (GpNode)candidate.getChild( position );
				}
				else
				{
					List<GpNode> leafNodes = new ArrayList<GpNode>();
					for( int c = 0; c < candidate.getChildren().size(); c++ )
					{
						if( candidate.getChild(c).isTerminal() ) {
							leafNodes.add(candidate.getChild(c));
						}
					}
					if (leafNodes.size() != 0) {
						return	(GpNode)leafNodes.get( (int)(Math.random() * leafNodes.size()) );
					}
					else{	// do nothing
					}
				}
			}
			if( loop > 1000 )
			{
				System.out.println("over " + loop + " " + hitNodes + " " + isMax);
			}
			loop++;
		}
	}

	public int getMaximumNodeSize()
	{
		return _maximumNodeSize;
	}

	public void setMaximumNodeSize(int maximumNodeSize)
	{
		_maximumNodeSize = maximumNodeSize;
	}

	public int getSumOfTreeSize()
	{
		return _sumOfTreeSize;
	}
	
	public Map<Integer, Integer> getLastConstructionSizeMap()
	{
		return _lastConstructionSizeMap;
	}
	
	public Map<Integer, Integer> getAllConstructionSizeMap()
	{
		return _allConstructionSizeMap;
	}
	
	public List<Double> getOffspringSizeList()
	{
		return _offspringSizeList;
	}
}
