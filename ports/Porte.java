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
public class Porte
{
	private List<GpNode> _promisingSolutions;
	private GpSymbolSet _symbolSet;
	private GpEnvironment<? extends GpIndividual> _environment;
	//private List<double[]> _frequencyIndex;
	//private List<double[]> _terminalFrequencyIndex;
	private List<List<GpNode>> _parentNodeMatric;
	private List<List<Double>> _parentNodeWeightMatric;
	//private List<List<GpNode>> _parentToTerminalNodeMatric;
	private List<List<Double>> _parentToTerminalNodeWeightMatric;
	
	// 2008 12 15
	private List<Set<SymbolType>> _transitionMatrices;
	
	private double[] _symbolFrequency;
	private int[] _symbolContainTreeSize;
	private double[] _terminalSymbolFrequency;
	private int[] _nodeSizeDistribution;
	private int _maximumNodeSize;
	private int _averageNodeSize;
	
	private double _probabilityAlpha;
	private double _probabilityBeta;
	
	private Map<Integer, Integer> _lastConstructionSizeMap;
	private List<Double> _offspringSizeList;
	
	/**
	 * constructor
	 * @param promisingSolutions
	 * @param environment
	 */
	public Porte(List<GpNode> promisingSolutions, GpEnvironment<? extends GpIndividual> environment)
	{
		_promisingSolutions = promisingSolutions;
		_symbolSet = environment.getSymbolSet();
		_environment = environment;
		calculateTransitionMatrices();
		//calculateNodeSizeDistribution();
		_probabilityAlpha = Double.valueOf(_environment.getAttribute("porteAlpha") );
		_probabilityBeta = Double.valueOf(_environment.getAttribute("porteBeta") );
		
		_lastConstructionSizeMap = new HashMap<Integer, Integer>();
		_offspringSizeList = new ArrayList<Double>();
	}
	
	private void calculateNodeSizeDistribution()
	{
		Map<Integer, Integer> sizeMap = new HashMap<Integer, Integer>();
		_maximumNodeSize = 0;
		for( GpNode node: _promisingSolutions )
		{
			int size = GpTreeManager.getNodeSize(node);
			if( sizeMap.containsKey(size) )
			{
				int currentCount = sizeMap.get(size);
				sizeMap.put(size, currentCount+1);
			}
			else
			{
				sizeMap.put(size, 1);
			}
			if( size > _maximumNodeSize )
			{
				_maximumNodeSize = size;
			}
		}
		//System.out.println(sizeMap);
		
		_averageNodeSize = 0;
		for( Integer size: sizeMap.keySet() )
		{
			_averageNodeSize += sizeMap.get(size) * size;
		}
		_averageNodeSize = _averageNodeSize / _promisingSolutions.size();
		System.out.println("Average Node Size = " + _averageNodeSize);
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
					int symbolIndex = _symbolSet.getIndex(node.getNodeType());// TODO
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
		
		boolean isTransition = false;
		
		// 木をランダムに追加
		
		while(leaf.size() != 0)
		{
			List<GpNode> nextLeaf = new ArrayList<GpNode>();
			List<Integer> nextFragmentList = new ArrayList<Integer>();
			//for( GpNode currentLeaf: leaf )
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
					int currentFractionSize = _lastConstructionSizeMap.get(fragmentList.get(i));
					//_probabilityBeta -= currentFractionSize * 0.00000001;
					//_probabilityAlpha += currentFractionSize * 0.00000001;
					//if( currentFractionSize > 100 )
					//{
					//randValue = 0;
					//}
					
					if( randValue < probabilityAlpha )
					{
						// alpha transition
						copyNode = selectAlphaTransitionNode(_promisingSolutions, currentLeaf, childIndex, isTerminalSearch);
						fragmentIndex = newIndex;
						_lastConstructionSizeMap.put(fragmentIndex , 0);
						newIndex++;
						isTransition = true;
					}
					else if ( randValue < probabilityAlpha + _probabilityBeta )
					{
						// beta transition
						copyNode = selectBetaTransitionNode(_promisingSolutions, isTerminalSearch);
						fragmentIndex = newIndex;
						_lastConstructionSizeMap.put(newIndex, 0);
						newIndex++;
						isTransition = true;
					}
					else // normal transition
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
								fragmentIndex = newIndex;
								_lastConstructionSizeMap.put(fragmentIndex , 0);
								newIndex++;
								isTransition = true;
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
		if( isTransition )
		{
			GpTreeManager.calculateDepth(child, 1);
			double averageSubsolutionSize = 0;
			for( Integer size: _lastConstructionSizeMap.values() )
			{
				averageSubsolutionSize += size;
			}
			_offspringSizeList.add( averageSubsolutionSize / _lastConstructionSizeMap.size() );
			//System.out.println( "Size = " + GpTreeManager.getNodeSize(child) );
			return child;
		}
		else
		{
			return getRandomSample( probabilityAlpha );
		}
	}
		
	// 集団の中から SymbolType が同じノードをランダムに探す
	private GpNode getRandomSameType2(GpNode currentLeaf)
	{
		int index = _environment.getSymbolSet().getIndex(currentLeaf.getNodeType());
		double randValue = Math.random() * _parentNodeWeightMatric.get(index).get( _parentNodeWeightMatric.get(index).size()-1 );
		//System.out.println( currentLeaf.getNodeType().getSymbolName() + " " + index + ": Rand Value " + _parentNodeWeightMatric.get(index).get(_parentNodeWeightMatric.get(index).size()-1) + " " + randValue );
		double currentValue = 0;
		for( int i = 0; i < _parentNodeWeightMatric.get(index).size(); i++ )
		{
			currentValue = _parentNodeWeightMatric.get(index).get(i);
			if( currentValue >= randValue )
			{
				return _parentNodeMatric.get( index ).get(i);
			}
		}
		return null;
	}
	
	// 集団の中から SymbolType が同じノードをランダムに探す
//	private GpNode getRandomSameType3(GpNode currentLeaf)
//	{
//		int symbolIndex = _symbolSet.getIndex(currentLeaf.getNodeType());
//		int randomTransitionIndex = (int)(Math.random() * _symbolContainTreeSize[symbolIndex]);
//		
//		int currentIndex = 0;
//		for( int geneIndex = 0; geneIndex < _frequencyIndex.size(); geneIndex ++)
//		{
//			double[] frequency = _frequencyIndex.get(geneIndex);
//			if( frequency[symbolIndex] != 0 )
//			{
//				currentIndex ++;
//			}
//			if( currentIndex >= randomTransitionIndex )
//			{
//				List<GpNode> candidateList = GpTreeManager.breathFirstSearch(_promisingSolutions.get(geneIndex));
//				List<GpNode> hitNodes = new ArrayList<GpNode>();
//				for( GpNode node: candidateList)
//				{
//					if( node.getNodeType().getSymbolName().equals(currentLeaf.getNodeType().getSymbolName()) )
//					{
//						hitNodes.add(node);
//					}
//				}
//				return hitNodes.get((int)Math.random() * hitNodes.size());
//			}
//		}
//		return null;
//	}
		
	/** 親世代 parents を引数にもらい、childrenSize個の子世代のゲノムを返す
	 * 使ってない．
	 */
	public List<GpNode> randomSampling(List<GpNode> parents, int childrenSize, int maximumSize)
	{
		List<GpNode> children = new ArrayList<GpNode>();
		
		//生成する個体分のループ
		for( int i = 0; i < childrenSize; i++ )
		{
			GpNode randomRoot = parents.get( (int) (Math.random() * parents.size()));			
			GpNode child = (GpNode)randomRoot.shallowClone();
			//child.getChildren().clear();
			int depth = 1;
			List<GpNode> leaf = new ArrayList<GpNode>();
			leaf.add(child);
			
			//木をランダムに追加
			while(leaf.size() != 0)
			{
				List<GpNode> nextLeaf = new ArrayList<GpNode>();
				for( GpNode currentLeaf: leaf )
				{
					// currentLeafと同じノードを集団から探す
					// 取り合えず、ランダムな個体を取り出して、そこから同じノードを探す(個体のサイズが大きく違うと偏りがでるので後で修正)
					List<GpNode> newChildren = new ArrayList<GpNode>();
					for(int childIndex = 0; childIndex < currentLeaf.getNodeType().getArgumentSize(); childIndex++)
					{
						GpNode copyNode;
						if( Math.random() > 0.0 )
						{
							copyNode = selectAlphaTransitionNode(parents, currentLeaf, childIndex, depth + 1 == maximumSize);
						}
						else
						{
							copyNode = (GpNode)currentLeaf.getChild(childIndex).shallowClone();
						}
						newChildren.add(copyNode);
						
						//copyNode.getChildren().clear();
						currentLeaf.getChildren().set(childIndex, copyNode);
						nextLeaf.add(copyNode);
					}
					//currentLeaf.setChildren(newChildren);
				}
				leaf = nextLeaf;
				depth++;
			}
			GpTreeManager.calculateDepth(child, 1);
			children.add(child);
		}
		
		return children;
	}
	
	/** ランダムにノードを選択する
	 * β transition
	 * @param parents
	 * @return 
	 */
	public GpNode selectBetaTransitionNode(List<GpNode> parents, boolean terminalSearch)
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
	
	/** ランダムにノードを選択する
	 * α transition
	 * @param parents
	 * @param currentLeaf
	 * @param isMax
	 * @return
	 */
	public GpNode selectAlphaTransitionNode(List<GpNode> parents, GpNode currentLeaf, int position, boolean isMax)
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

	public int getAverageNodeSize()
	{
		return _averageNodeSize;
	}

	public void setAverageNodeSize(int averageNodeSize)
	{
		_averageNodeSize = averageNodeSize;
	}
	
	public Map<Integer, Integer> getLastConstructionSizeMap()
	{
		return _lastConstructionSizeMap;
	}
	
	public void setLastConstructionSizeMap(Map<Integer, Integer> lastConstructionSizeMap)
	{
		_lastConstructionSizeMap = lastConstructionSizeMap;
	}

	public List<Double> getOffspringSizeList()
	{
		return _offspringSizeList;
	}

	public void setOffspringSizeList(List<Double> offspringSizeList)
	{
		_offspringSizeList = offspringSizeList;
	}
}
