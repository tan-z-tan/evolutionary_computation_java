package perce;

import geneticProgramming.DefaultGpIndividual;
import geneticProgramming.GpEnvironment;
import geneticProgramming.GpIndividual;
import geneticProgramming.GpNode;
import geneticProgramming.GpSymbolSet;
import geneticProgramming.GpTreeManager;
import geneticProgramming.symbols.DefaultSymbolType;
import geneticProgramming.symbols.SymbolType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import markovNet.Clique;
import markovNet.MarkovNetManager;
import markovNet.MarkovNetManager.Edge;
import random.RandomManager;

/**
 * An implementation of PEED (Program Evolution with Estimation of Dependency).
 * June 25, 2010
 * 
 * @author tanji
 */
public class PERCE
{
	public static final int EDGE_BASED = 0;
	public static final int CLIQUE_BASED = 1;
	public static final int DEPENDENCY_BASED = 2;
	public static final int RELATED_CLIQUE_BASED = 3;

	public static final int CHI_SQUARE = 0;
	public static final int MUTUAL_INFORMATION = 1;

	int sampling = CLIQUE_BASED;
	int dependency = CHI_SQUARE;

	// private List<GpNode> _promisingSolutions;
	// private List<? extends GpIndividual> _promisingIndividuals;
	private GpSymbolSet _symbolSet;
	private GpEnvironment<? extends GpIndividual> _environment;
	private List<List<Integer>> permutationList; // 各ノードの順列
	private List<List<Integer>> permutationBoundaryList; // 各ノードの境界のインデックス
	private Map<String, List<Integer>> andListMap;

	private MarkovNetManager mn;
	private double[][] cliqueRelation;

	// 2008 12 15
	// _transitionMatricesは各関数ノードから終端記号へのリンクがあるかどうかを記録する．
	private List<Set<SymbolType>> _transitionMatrices;

	private int _sumOfTreeSize;
	private double _sumOfTransitionCount;

	private double _probabilityT; // 　遷移確率
	private double _probabilityC = 0; // 　結合確率
	private double _smoothigParameter = 0.0;
	private int samplingOrder = 0;

	protected double _averageFragmentSize;
	protected double _averageTreeSize = 0;
	protected double _averageTransitionCount;
	protected double _averageBranchSize = 0;
	protected double _cutCount = 0;

	/**
	 * constructor
	 * 
	 * @param promisingSolutions
	 * @param environment
	 */
	public PERCE(List<? extends GpIndividual> promisingIndividual, GpEnvironment<? extends GpIndividual> environment)
	{
		_symbolSet = environment.getSymbolSet();
		_environment = environment;
		_probabilityT = Double.valueOf(_environment.getAttribute("portsT"));

		if (_environment.getAttribute("peedCatProbability") != null)
		{
			_probabilityC = Double.valueOf(_environment.getAttribute("peedCatProbability"));
		}
		if (_environment.getAttribute("peedSampling") != null)
		{
			if (_environment.getAttribute("peedSampling").equals("cliqueBased"))
			{
				sampling = CLIQUE_BASED;
			} else if (_environment.getAttribute("peedSampling").equals("edgeBased"))
			{
				sampling = EDGE_BASED;
			} else if (_environment.getAttribute("peedSampling").equals("dependencyBased"))
			{
				sampling = DEPENDENCY_BASED;
			} else if (_environment.getAttribute("peedSampling").equals("relatedCliqueBased"))
			{
				sampling = RELATED_CLIQUE_BASED;
			}
		}
		if (_environment.getAttribute("PPT_dependency") != null)
		{
			if (_environment.getAttribute("PPT_dependency").equals("chiSquare"))
			{
				dependency = CHI_SQUARE;
			} else if (_environment.getAttribute("PPT_dependency").equals("mutualInformation"))
			{
				dependency = MUTUAL_INFORMATION;
			}
		}
		if (environment.getAttribute("PPT_MN_order").equals("root"))
		{
			this.samplingOrder = MarkovNetManager.ORDER_ROOT;
		} else if (environment.getAttribute("PPT_MN_order").equals("random"))
		{
			this.samplingOrder = MarkovNetManager.ORDER_RANDOM;
		} else if (environment.getAttribute("PPT_MN_order").equals("roulette"))
		{
			this.samplingOrder = MarkovNetManager.ORDER_ROULETTE;
		} else if (environment.getAttribute("PPT_MN_order").equals("max"))
		{
			this.samplingOrder = MarkovNetManager.ORDER_MAX;
		} else
		{
			System.out.println("PPT_MN_{root, random, max, roulette} must be specified.");
			System.exit(0);
		}

		update(promisingIndividual);
	}
	
	/**
	 * returns random GpNode by method of PORTS
	 * 
	 * @return
	 */
	public GpNode getRandomSample()
	{
		if (sampling == CLIQUE_BASED)
		{
			GpNode childNode = sampling_CliqueBased();

			while (childNode == null)
			{
				childNode = sampling_CliqueBased();
			}
			return childNode;
		} else if (sampling == EDGE_BASED)
		{
			GpNode childNode = sampling_EdgeBased();

			while (childNode == null)
			{
				childNode = sampling_EdgeBased();
			}
			return childNode;
		} else if (sampling == DEPENDENCY_BASED)
		{
			GpNode childNode = sampling_DepencencyBased();

			while (childNode == null)
			{
				childNode = sampling_DepencencyBased();
			}
			return childNode;
		} else if (sampling == RELATED_CLIQUE_BASED)
		{
			GpNode childNode = sampling_relatedCliqueBased();
						
			while (childNode == null)
			{
				childNode = sampling_relatedCliqueBased();
			}
			return childNode;
		} else
		{
			return null;
		}
	}

	/**
	 * エッジベースのサンプリング
	 * 
	 * @return
	 */
	private GpNode sampling_EdgeBased()
    {
    	//System.out.println("edge based");
        // sampling
    	int transitionCount = 0;
    	int[] sampledData = new int[mn.getNodeSize()]; // ここにノードの値を入れていく．
    	Object[] sampledObjectData = new Object[mn.getNodeSize()]; // ここにノードの値を入れていく．
    	int sampledSize = 0;
    	Arrays.fill(sampledData, -1);
    	//GpNode currentTree = getRandomTree();
    	int randIndex = (int)(RandomManager.getRandom() * mn.getDataList().size());
    	int[] currentTree = getTree(randIndex);
    	Object[] currentObjectList = getObjectList(randIndex);
        
    	List<Integer> frontLine = new ArrayList<Integer>();
        
        while(sampledSize < mn.getNodeSize())
        {
        	int firstTarget = getRandomUnsampledIndex(sampledData, sampledSize);
        	
        	if( RandomManager.getRandom() < _probabilityT )
        	{
        		transitionCount++;
        		randIndex = (int)(RandomManager.getRandom() * mn.getDataList().size());
        		currentTree = getTree(randIndex);
        		currentObjectList = getObjectList(randIndex);
        	}
        	
        	// set
        	setData( currentTree, currentObjectList, sampledData, sampledObjectData, firstTarget, mn );
    		frontLine.add(firstTarget);
    		sampledSize ++;
    		
        	//System.out.println("sampled size = " + sampledSize);
        	while(!frontLine.isEmpty())
        	{
            	//System.out.println("sampled data = " + Arrays.toString(sampledData) + "  Front line = " + frontLine);
        		List<List<Integer>> neighborListList = new ArrayList<List<Integer>>();
        		
        		for( int i = 0; i < frontLine.size(); i++ )
        		{
        			List<Integer> neighborList = new ArrayList<Integer>();
                    
        			if( RandomManager.getRandom() <= 1.0 ) // dependency edge
                    {
        				//neighborList = mn.getDependentEdgeList(frontLine.get(i));
        			    List<Edge> edgeList = mn.getDependentEdgeList(frontLine.get(i));
        			    neighborList = new ArrayList<Integer>();
        			    for( int v = 0; v < edgeList.size(); v++ )
        			    {
        			        int pairIndex = edgeList.get(v).getStart();
        			        if( pairIndex == frontLine.get(i) )
        			        {
        			            pairIndex = edgeList.get(v).getEnd();
        			        }
        			        neighborList.add( pairIndex );
        			    }
                        cutSampledIndexes(neighborList, sampledData);
                    }
                    else
                    {
        				neighborList = getRandomNeighbor(frontLine.get(i));
                        cutSampledIndexes(neighborList, sampledData);
                    }
        			if( neighborList.isEmpty() )
        			{
        				frontLine.remove(i--);
        				continue;
        			}
        			neighborListList.add(neighborList);
        		}
        		if( neighborListList.isEmpty() )
        		{
        			break;
        		}
        		
        		//System.out.println("neighbor list list = " + neighborListList);
        		List<Integer> targetNeighbor = neighborListList.get( (int)(RandomManager.getRandom() * neighborListList.size()) );
        		int target = targetNeighbor.get( (int)(targetNeighbor.size() * RandomManager.getRandom()) );
        		
        		if( RandomManager.getRandom() < _probabilityT )
            	{
        			transitionCount++;
            		randIndex = (int)(RandomManager.getRandom() * mn.getDataList().size());
            		currentTree = getTree(randIndex);
            		currentObjectList = getObjectList(randIndex);
        			//currentTree = getRandomTree();
            	}
            	
        		//System.out.println("new sample at " + target);
        		setData(currentTree, currentObjectList, sampledData, sampledObjectData, target, mn);
        		sampledSize++;
        		frontLine.add(target);
        	}
        }
        //System.out.println("sampled data = " + Arrays.toString(sampledData));
        if( transitionCount == 0 )
        {
        	return null;
        }
        return mn.constructGPTree(sampledData, sampledObjectData);
    }

	/**
	 * 依存関係ベースのサンプリング
	 * 
	 * @return
	 */
	private GpNode sampling_DepencencyBased()
	{
		// System.out.println("dependency based");
		// sampling
		int transitionCount = 0;
		int[] sampledData = new int[mn.getNodeSize()]; // ここにノードの値を入れていく．
		Object[] sampledObjectData = new Object[mn.getNodeSize()]; // ここにノードの値を入れていく．
		int sampledSize = 0;
		Arrays.fill(sampledData, -1);
		// GpNode currentTree = getRandomTree();
		// int[] currentTree = getRandomTree();
		int randIndex = (int) (RandomManager.getRandom() * mn.getDataList().size());
		int[] currentTree = getTree(randIndex);
		Object[] currentObjectList = getObjectList(randIndex);

		List<Integer> frontLine = new ArrayList<Integer>();

		while (sampledSize < mn.getNodeSize())
		{
			int firstTarget = getRandomUnsampledIndex(sampledData, sampledSize);

			if (RandomManager.getRandom() < _probabilityT)
			{
				transitionCount++;
				// currentTree = getRandomTree();
				randIndex = (int) (RandomManager.getRandom() * mn.getDataList().size());
				currentTree = getTree(randIndex);
				currentObjectList = getObjectList(randIndex);
			}

			// set
			setData(currentTree, currentObjectList, sampledData, sampledObjectData, firstTarget, mn);
			frontLine.add(firstTarget);
			sampledSize++;

			// System.out.println("sampled size = " + sampledSize);
			while (!frontLine.isEmpty())
			{
				// System.out.println( "sampled data = " +
				// Arrays.toString(sampledData) );
				// System.out.println("sampled data = " +
				// Arrays.toString(sampledData) + "  Front line = " +
				// frontLine);
				List<List<Integer>> neighborListList = new ArrayList<List<Integer>>();
				List<List<Double>> neighborWeightListList = new ArrayList<List<Double>>();

				for (int i = 0; i < frontLine.size(); i++)
				{
					List<Integer> neighborList = new ArrayList<Integer>();
					List<Double> neighborWeightList = new ArrayList<Double>();

					if (RandomManager.getRandom() <= 1.0) // dependency edge
					{
						// neighborList =
						// mn.getDependentEdgeList(frontLine.get(i));
						// neighborList = mn.getEdgeMap().get( frontLine.get(i)
						// );

						List<Edge> edgeList = mn.getDependentEdgeList(frontLine.get(i));
						neighborList = new ArrayList<Integer>();
						neighborWeightList = new ArrayList<Double>();

						for (int v = 0; v < edgeList.size(); v++)
						{
							int pairIndex = edgeList.get(v).getStart();
							if (pairIndex == frontLine.get(i))
							{
								pairIndex = edgeList.get(v).getEnd();
							}
							neighborList.add(pairIndex);
							neighborWeightList.add(edgeList.get(v).getWeight());
						}

						// cutSampledIndexes(neighborList, sampledData);
						cutSampledIndexes(neighborList, neighborWeightList, sampledData);
					}

					if (neighborList.isEmpty())
					{
						frontLine.remove(i--);
						continue;
					}
					neighborListList.add(neighborList);
					neighborWeightListList.add(neighborWeightList);
				}
				if (neighborListList.isEmpty())
				{
					break;
				}

				if (RandomManager.getRandom() < _probabilityT)
				{
					transitionCount++;
					// currentTree = getRandomTree();
					randIndex = (int) (RandomManager.getRandom() * mn.getDataList().size());
					currentTree = getTree(randIndex);
					currentObjectList = getObjectList(randIndex);
				}

				// System.out.println( neighborListList );
				// System.out.println( neighborWeightListList );
				// choose one index
				int target = -1;
				double weightSum = 0;
				for (int s = 0; s < neighborListList.size(); s++)
				{
					for (int t = 0; t < neighborListList.get(s).size(); t++)
					{
						weightSum += neighborWeightListList.get(s).get(t);
					}
				}
				double randomValue = weightSum * RandomManager.getRandom();
				double current = 0;
				for (int s = 0; s < neighborListList.size(); s++)
				{
					for (int t = 0; t < neighborListList.get(s).size(); t++)
					{
						current += neighborWeightListList.get(s).get(t);
						if (current > randomValue)
						{
							target = neighborListList.get(s).get(t);
							break;
						}
					}
				}

				if (RandomManager.getRandom() < _probabilityT)
				{
					transitionCount++;
					// currentTree = getRandomTree();
					randIndex = (int) (RandomManager.getRandom() * mn.getDataList().size());
					currentTree = getTree(randIndex);
					currentObjectList = getObjectList(randIndex);
				}

				// System.out.println("new sample at " + target);
				if (RandomManager.getRandom() < _smoothigParameter)
				{
					// sampledData[i] = targetTree[i];
					randIndex = (int) (RandomManager.getRandom() * mn.getDataList().size());
					currentTree = getTree(randIndex);
					currentObjectList = getObjectList(randIndex);
					sampledData[target] = currentTree[target];
				} else
				{
					sampledData[target] = currentTree[target];
				}
				// setData(currentTree, sampledData, target, mn);
				sampledSize++;
				frontLine.add(target);
			}
		}
		// System.out.println("sampled data = " + Arrays.toString(sampledData));
		if (transitionCount == 0) { return null; }
		return mn.constructGPTree(sampledData, sampledObjectData);
	}

	/**
	 * MNFDP．結合，遷移は行わない． クリークベースのサンプリング
	 * 
	 * @return
	 */
	private GpNode sampling_CliqueBased()
	{
		int[] sampledData = new int[mn.getNodeSize()]; // ここにノードの値を入れていく．
		Object[] sampledObjectData = new Object[mn.getNodeSize()]; // ここにノードの値を入れていく．
		Arrays.fill(sampledData, -1);

		List<Clique<SymbolType>> cliqueList = mn.getCliqueList();
		if (samplingOrder == MarkovNetManager.ORDER_RANDOM)
		{
			cliqueList = mn.orderCliqueList_randomRoot(cliqueList);
		} else if (samplingOrder == MarkovNetManager.ORDER_MAX)
		{
			cliqueList = mn.orderCliqueList_Max(cliqueList);
		} else if (samplingOrder == MarkovNetManager.ORDER_ROULETTE)
		{
			cliqueList = mn.orderCliqueList_Roulette(cliqueList);
		} else if (samplingOrder == MarkovNetManager.ORDER_ROOT)
		{
			// nothing to do
		} else
		{
			System.out.println("PPT_MN_{root, random, max, roulette} must be specified.");
			System.exit(0);
		}

		// int[] currentTree = getRandomTree();
		// List<Object> currentTree = getRandomTree();
		int randIndex = (int) (RandomManager.getRandom() * mn.getDataList().size());
		int[] currentTree = getTree(randIndex);
		Object[] currentObjectList = getObjectList(randIndex);

		for (Clique<SymbolType> clique : cliqueList)
		{
			// System.out.println("clique " + clique);
			int restrictionCount = 0;
			int[] restriction = new int[mn.getNodeSize()];
			int[] condition = new int[mn.getNodeSize()];
			Arrays.fill(restriction, -2);
			Arrays.fill(condition, -1);

			for (int i = 0; i < clique.getCode().bitLength(); i++)
			{
				if (sampledData[i] != -1) // データが既にあったら
				{
					restriction[i] = sampledData[i];
					condition[i] = sampledData[i];
					restrictionCount++;
				} else if (clique.getCode().testBit(i))
				{
					restriction[i] = -1;
				}
			}
			// System.out.println("restriction = " +
			// Arrays.toString(restriction));
			// System.out.println("clique = " + clique.getCode().toString(2));

			List<Integer> candidateSet = null;
			String stateString = Arrays.toString(condition);

			if (restrictionCount == 0)
			{

			} else if (andListMap.containsKey(stateString))
			{
				candidateSet = andListMap.get(stateString);
			} else
			{
				List<Integer> swapList = null;
				int[] currentCondition = new int[mn.getNodeSize()];
				Arrays.fill(currentCondition, -1);
				
				for (int c = 0; c < clique.getCode().bitLength(); c++)
				{
					if (clique.getCode().testBit(c) && restriction[c] >= 0)
					{
						currentCondition[c] = restriction[c];

						int[] tmpCondition = new int[mn.getNodeSize()];
						Arrays.fill(tmpCondition, -1);
						tmpCondition[c] = restriction[c];

						String currentConditionString = Arrays.toString(currentCondition);
						// System.out.println("condition = " +
						// currentConditionString);
						List<Integer> matchList = null;
						if (andListMap.containsKey(Arrays.toString(currentCondition)))
						{
							matchList = andListMap.get(Arrays.toString(currentCondition));
						} else
						{
							int start = 0;
							int end = 0;
							start = permutationBoundaryList.get(c).get(restriction[c]);
							end = permutationBoundaryList.get(c).get(restriction[c] + 1);
							matchList = permutationList.get(c).subList(start, end);
						}
						
						if (candidateSet == null)
						{
							candidateSet = new ArrayList<Integer>(matchList);
							swapList = new ArrayList<Integer>(matchList.size());
						} else	// candidateSet && matchList
						{
							// candidateSet.retainAll(matchList);
							int indexA = 0;
							int indexB = 0;
							// System.out.println( matchList );
							// System.out.println( candidateSet );
							swapList.clear(); // next candidate list
							
							while (indexA < matchList.size() && indexB < candidateSet.size())
							{
								// System.out.println(indexA + "  " +
								// indexB);
								// System.out.println( " data = " +
								// matchList.get(indexA) + "  " +
								// candidateSet.get(indexB) );
								
								if (matchList.get(indexA) < candidateSet.get(indexB))
								{
									indexA++;
								} else if (matchList.get(indexA) > candidateSet.get(indexB))
								{
									// candidateSet.remove(indexB);
									indexB++;
								} else if (matchList.get(indexA).equals(candidateSet.get(indexB)))
								{
									swapList.add(matchList.get(indexA));
									indexA++;
									indexB++;
								}
							}
							List<Integer> tmp = candidateSet;
							candidateSet = swapList;
							swapList = tmp;
						}
						andListMap.put(currentConditionString, new ArrayList<Integer>(candidateSet));
					}
				}
				if (candidateSet != null)
				{
					andListMap.put(stateString, new ArrayList<Integer>(candidateSet));
				}
			}

			// int[] targetTree = null;
			if (candidateSet == null || candidateSet.isEmpty())
			{
				if (RandomManager.getRandom() >= _probabilityC)
				{
					// currentTree = getRandomTree();
					randIndex = (int) (RandomManager.getRandom() * mn.getDataList().size());
					currentTree = getTree(randIndex);
					currentObjectList = getObjectList(randIndex);
				}
				// System.out.println("afoafo");
			} else
			{
				int targetSize = (int) (RandomManager.getRandom() * candidateSet.size());
				int targetIndex = candidateSet.get(targetSize);
				// targetTree = mn.getDataList().get(targetIndex);
				// if( RandomManager.getRandom() > 0.1 )
				// randIndex = (int)(RandomManager.getRandom() *
				// mn.getDataList().size());
				currentTree = getTree(targetIndex);
				currentObjectList = getObjectList(targetIndex);
				// currentTree = mn.getDataList().get(targetIndex);
				// currentObject = mn.getObjeList().get(targetIndex);

			}

			// System.out.println( "data sampling start " +
			// Arrays.toString(sampledData));
			for (int i = 0; i < restriction.length; i++)
			{
				if (restriction[i] == -1)
				{
					// sampledData[i] = mn.getIndex(
					// MarkovNetManager.getNodeSymbol(targetTree,
					// i).getNodeType() );
					if (RandomManager.getRandom() < _smoothigParameter)
					{
						randIndex = (int) (RandomManager.getRandom() * mn.getDataList().size());
						currentTree = getTree(randIndex);
						currentObjectList = getObjectList(randIndex);
						sampledData[i] = currentTree[i];

					} else
					{
						sampledData[i] = currentTree[i];
					}
				}
			}
		}

		// System.out.println( "Sampled Data = " + Arrays.toString(sampledData)
		// );
		return mn.constructGPTree(sampledData, sampledObjectData);
	}

	/**
	 * relatedクリークベースのサンプリング
	 * 
	 * @return
	 */
	private GpNode sampling_relatedCliqueBased()
	{
		int[] sampledData = new int[mn.getNodeSize()]; // ここにノードの値を入れていく．
		Object[] sampledObjectData = new Object[mn.getNodeSize()]; // ここにノードの値を入れていく．
		Arrays.fill(sampledData, -1);
		
		List<Clique<SymbolType>> cliqueList = mn.getCliqueList();
		int cliqueSize = cliqueList.size();
		int cliqueIndex = (int) (RandomManager.getRandom() * cliqueSize);

		// select a root clique by roulette selection
		double weightSum = 0;
		for (int i = 0; i < cliqueList.size(); i++)
		{
			weightSum += cliqueList.get(i).getWeight();
		}
		double randValue = RandomManager.getRandom() * weightSum;
		weightSum = 0;
		for (int i = 0; i < cliqueList.size(); i++)
		{
			weightSum += cliqueList.get(i).getWeight();
			if (randValue < weightSum)
			{
				cliqueIndex = i;
				break;
			}
		}
		
		// System.out.println("c " + cliqueIndex);
		//System.out.println("clique " + cliqueIndex);
		boolean[] usedFlag = new boolean[mn.getCliqueList().size()];
		Arrays.fill(usedFlag, false);
		usedFlag[cliqueIndex] = true;
		// int[] currentTree = getRandomTree();
		int randIndex = (int) (RandomManager.getRandom() * mn.getDataList().size());
		int[] currentTree = getTree(randIndex);
		Object[] currentObjectList = getObjectList(randIndex);
		
		//currentTree = inputData(cliqueList.get(cliqueIndex), currentTree, sampledData, sampledObjectData, currentObjectList);
		Object[] current = inputData(cliqueList.get(cliqueIndex), currentTree, sampledData, sampledObjectData, currentObjectList);
		currentTree = (int[])current[0];
		currentObjectList= (Object[])current[1];
		
		for (int i = 0; i < cliqueSize-1; i++)
		{
			double sum = 0;
			for (int j = 0; j < usedFlag.length; j++)
			{
				if (!usedFlag[j])
				{
					for (int k = 0; k < usedFlag.length; k++)
					{
						if( usedFlag[k] )
						{
							sum += mn.getRelationValue(k, j);
						}
					}
				}
			}
			randValue = RandomManager.getRandom() * sum;
			// System.out.println("sum = " + sum);
			sum = 0;

			for (int j = 0; j < usedFlag.length; j++)
			{
				if (!usedFlag[j])
				{
					for (int k = 0; k < usedFlag.length; k++)
					{
						if( usedFlag[k] )
						{
							sum += mn.getRelationValue(k, j);
						}
					}
					if (randValue <= sum)
					{
						cliqueIndex = j;
						usedFlag[j] = true;
						//System.out.println("clique " + j);
						break;
					}
				}
			}
			// System.out.println("clique " + cliqueIndex);
			//currentTree = inputData(cliqueList.get(cliqueIndex), currentTree, sampledData, sampledObjectData, currentObjectList);
			current = inputData(cliqueList.get(cliqueIndex), currentTree, sampledData, sampledObjectData, currentObjectList);
			currentTree = (int[])current[0];
			currentObjectList = (Object[])current[1];
		}
		
		//System.out.println("sampled symbol data = " + Arrays.toString(sampledData));
		//System.out.println("sampled object data = " + Arrays.toString(sampledObjectData));
		return mn.constructGPTree(sampledData, sampledObjectData);
	}

	/**
	 * 対象のクリークのデータをsampledDataに入力する． 返り値は現在の木．
	 */
	private Object[] inputData(Clique<SymbolType> clique, int[] currentTree, int[] sampledData, Object[] sampledObject,
			Object[] currentObjectList)
	{
		//System.out.println(" input " + clique + " data = " + Arrays.toString(sampledData) + " current = " + Arrays.toString(currentTree) );
		int restrictionCount = 0;
		int[] restriction = new int[mn.getNodeSize()];
		int[] condition = new int[mn.getNodeSize()];
		Arrays.fill(restriction, -2);
		Arrays.fill(condition, -1);

		for (int i = 0; i < clique.getCode().bitLength(); i++)
		{
			if( clique.getCode().testBit(i) )
			{
				if (sampledData[i] != -1) // データが既にあったら
				{
					restriction[i] = sampledData[i];
					condition[i] = sampledData[i];
					restrictionCount++;
				}
				else
				{
					restriction[i] = -1;
				}
			}else if( clique.getCode().testBit(i) )
			{
				//restriction[i] = -1;
			}
		}

		List<Integer> candidateSet = null;
		String stateString = Arrays.toString(condition);
		
		//System.out.println(stateString);
		//System.out.println(andListMap);
		
		if (restrictionCount == 0)
		{
			// nothing to do
		} else if (andListMap.containsKey(stateString))
		{
			candidateSet = andListMap.get(stateString);
		} else
		{
			List<Integer> swapList = null;
			int[] currentCondition = new int[mn.getNodeSize()];
			Arrays.fill(currentCondition, -1);

			for (int c = 0; c < clique.getCode().bitLength(); c++)
			{
				if (clique.getCode().testBit(c) && restriction[c] >= 0)
				{
					currentCondition[c] = restriction[c];
					
					int[] tmpCondition = new int[mn.getNodeSize()];
					Arrays.fill(tmpCondition, -1);
					tmpCondition[c] = restriction[c];

					String currentConditionString = Arrays.toString(currentCondition);
					// System.out.println("condition = " + currentConditionString);
					List<Integer> matchList = null;
					if (andListMap.containsKey(Arrays.toString(currentCondition)))
					{
						matchList = andListMap.get(Arrays.toString(currentCondition));
					} else
					{
						int start = 0;
						int end = 0;
						// c 番目が restriction[c]である個体のpermutationList上でのstartIndex, endIndex．多少複雑．
						start = permutationBoundaryList.get(c).get(restriction[c]);
						end = permutationBoundaryList.get(c).get(restriction[c] + 1);
						matchList = permutationList.get(c).subList(start, end);

						if (candidateSet == null)
						{
							candidateSet = new ArrayList<Integer>(matchList);
							swapList = new ArrayList<Integer>(matchList.size());
						} else
						// candidateSet && matchList
						{
							int indexA = 0;
							int indexB = 0;
							swapList.clear();

							while (indexA < matchList.size() && indexB < candidateSet.size())
							{
								if (matchList.get(indexA) < candidateSet.get(indexB))
								{
									indexA++;
								} else if (matchList.get(indexA) > candidateSet.get(indexB))
								{
									indexB++;
								} else if (matchList.get(indexA).equals(candidateSet.get(indexB)))
								{
									swapList.add(matchList.get(indexA));
									indexA++;
									indexB++;
								}
							}
							List<Integer> tmp = candidateSet;
							candidateSet = swapList;
							swapList = tmp;
						}
						andListMap.put(currentConditionString, new ArrayList<Integer>(candidateSet));
					}
				}
			}
			if (candidateSet != null)
			{
				andListMap.put(stateString, candidateSet);
			}
		}

		if (restrictionCount == 0 || candidateSet == null || candidateSet.isEmpty())
		{
			//System.out.println("null");
			if (RandomManager.getRandom() <= _probabilityC)
			{
				// nothing to do
			} else
			{
				int randIndex = (int) (RandomManager.getRandom() * mn.getDataList().size());
				//System.out.println("mn size = " + mn.getDataList().size() + " rand = " + randIndex);
				currentTree = getTree(randIndex);
				currentObjectList = getObjectList(randIndex);
			}
		} else
		{
			if (RandomManager.getRandom() <= _probabilityC)
			{
				// nothing to do
			} else
			{
				int targetIndex = candidateSet.get((int) (RandomManager.getRandom() * candidateSet.size()));
				currentTree = getTree(targetIndex);
				currentObjectList = getObjectList(targetIndex);
				//int[] change = getTree(targetIndex);
				//Object[] changeObject = getObjectList(targetIndex);
				//System.arraycopy(change, 0, currentTree, 0, change.length);
				//System.arraycopy(changeObject, 0, currentObjectList, 0, changeObject.length);
				
			}
		}

		// System.out.println( "data sampling start " +
		// Arrays.toString(sampledData));
		for (int i = 0; i < restriction.length; i++)
		{
			if (restriction[i] == -1) // to be filled
			{
				// sampledData[i] = mn.getIndex(
				// MarkovNetManager.getNodeSymbol(targetTree, i).getNodeType()
				// );
				if (RandomManager.getRandom() < _smoothigParameter)
				{
					int randIndex = (int) (RandomManager.getRandom() * mn.getDataList().size());
					int[] change = getTree(randIndex);
					Object[] changeObject = getObjectList(randIndex);
					sampledData[i] = change[i];
					sampledObject[i] = changeObject[i];
				} else
				{
					if (RandomManager.getRandom() < _probabilityT)
					{
						// currentTree = getRandomTree();
						int randIndex = (int) (RandomManager.getRandom() * mn.getDataList().size());
						currentTree = getTree(randIndex);
						currentObjectList = getObjectList(randIndex);
						
						//int[] change = getTree(randIndex);
						//Object[] changeObject = getObjectList(randIndex);
						//System.arraycopy(change, 0, currentTree, 0, change.length);
						//System.arraycopy(changeObject, 0, currentObjectList, 0, changeObject.length);
					}
					sampledData[i] = currentTree[i];
					sampledObject[i] = currentObjectList[i];
				}
			}
		}

		//return currentTree;
		return new Object[]{currentTree, currentObjectList};
	}

	private double calculateCliqueRelation(final int i, int j, MarkovNetManager mn)
	{
		double relation = 0;

		Clique<SymbolType> c_i = mn.getCliqueList().get(i);
		Clique<SymbolType> c_j = mn.getCliqueList().get(i);

		for (int n = 0; n < c_i.getCode().bitLength(); n++)
		{
			if (c_i.getCode().testBit(n))
			{
				for (int m = 0; m < c_j.getCode().bitLength(); m++)
				{
					if (c_j.getCode().testBit(m))
					{
						Edge edge = mn.getEdgeStringMap().get(String.valueOf(n) + "_" + String.valueOf(m));
						if (edge != null)
						{
							relation += edge.getWeight();
						}
					}
				}
			}
		}

		return relation;
	}

	/**
	 * まだサンプリングされていないインデックスをランダムに返す
	 */
	private static int getRandomUnsampledIndex(int[] sampledData, int sampledSize)
	{
		int firstRandom = (int) (RandomManager.getRandom() * (sampledData.length - sampledSize));
		for (int i = 0; i < sampledData.length; i++)
		{
			if (sampledData[i] == -1)
			{
				if (firstRandom == 0) { return i; }
				firstRandom--;
			}
		}
		return -1;
	}

	/**
	 * インデックスのデータをサンプリングする
	 */
	// private static void setData(GpNode tree, int[] sampledData, int index,
	// MarkovNetManager mn)
	private static void setData(int[] tree, Object[] objectData, int[] sampledData, Object[] sampledObjectData, int index, MarkovNetManager mn)
	{
		// sampledData[index] = mn.getIndex(
		// MarkovNetManager.getNodeSymbol(tree, index).getNodeType() );
		sampledData[index] = tree[index];
		sampledObjectData[index] = objectData[index];
	}

	/**
	 * サンプルされているインデックスを除く
	 * 
	 * @param list
	 * @param sampledData
	 */
	private static void cutSampledIndexes(List<Integer> list, int[] sampledData)
	{
		for (int i = 0; i < list.size(); i++)
		{
			if (sampledData[list.get(i)] != -1)
			{
				list.remove(i);
				i--;
			}
		}
	}

	/**
	 * サンプルされているインデックスを除く
	 * 
	 * @param list
	 * @param sampledData
	 */
	private static void cutSampledIndexes(List<Integer> list, List<Double> weightList, int[] sampledData)
	{
		for (int i = 0; i < list.size(); i++)
		{
			if (sampledData[list.get(i)] != -1)
			{
				list.remove(i);
				weightList.remove(i);
				i--;
			}
		}
	}

	/**
	 * 木構造上での近傍を返す
	 * 
	 * @param i
	 * @return
	 */
	private List<Integer> getRandomNeighbor(int i)
	{
		// neighbors -> parent, child_1, child_2,...,child_n
		List<Integer> neighbor = new ArrayList<Integer>();
		if (i != 0)
		{
			neighbor.add(mn.getParent(i));
		}
		if (!mn.isTerminal(i))
		{
			neighbor.addAll(mn.getChildren(i));
		}
		return neighbor;
	}

	/**
	 * 優良個体から一つ木構造を選ぶ
	 * 
	 * @param i
	 * @return
	 */
	private int[] getTree(int i)
	{
		// int rand = (int)(_promisingSolutions.size() *
		// RandomManager.getRandom());
		// return _promisingSolutions.get(rand);
		return mn.getDataList().get(i);
	}

	/**
	 * 優良個体から一つ木構造を選ぶ
	 * @param i
	 * @return
	 */
	private Object[] getObjectList(int i)
	{
		// int rand = (int)(_promisingSolutions.size() *
		// RandomManager.getRandom());
		// return _promisingSolutions.get(rand);
		return mn.getObjeList().get(i);
	}

	// /** 優良個体から一つ木構造を選ぶ
	// * @param i
	// * @return
	// */
	// private int[] getRandomTree()
	// {
	// //int rand = (int)(_promisingSolutions.size() *
	// RandomManager.getRandom());
	// //return _promisingSolutions.get(rand);
	// int rand = (int)(mn.getDataList().size() * RandomManager.getRandom());
	//
	// return mn.getDataList().get(rand);
	// }

	public void update(List<? extends GpIndividual> promisingIndividuals)
	{
		mn = new MarkovNetManager(promisingIndividuals, _environment);

		if (dependency == CHI_SQUARE)
		{
			mn.extractDependencyEdge_chiSquare();
		} else if (dependency == MUTUAL_INFORMATION)
		{
			mn.extractDependencyEdge_mutualInformation();
		}
		if (_environment.getAttribute("givenKnowledge") != null )
		{
			if(	_environment.getAttribute("givenKnowledge").contains("parent-child") )
			{
				mn.addParentChildRelation();
			}
			if(	_environment.getAttribute("givenKnowledge").contains("sibling") )
			{
				mn.addSiblingRelation();
			}
		}

		if (_environment.getAttribute("PERCE_clique") != null
				&& _environment.getAttribute("PERCE_clique").equals("full_reduce"))
		{
			System.out.println("full and reduce");
			CliqueDivisor.divideCliqueN_times(mn, Integer.valueOf(_environment.getAttribute("divideNumber")));

			System.out.println("clique size = " + mn.getCliqueList().size());
			List<Clique<SymbolType>> cliqueList = mn.getCliqueList();
			// cliqueRelation = new
			// double[cliqueList.size()][cliqueList.size()];
			// for( int i = 0; i < cliqueList.size(); i++ )
			// {
			// cliqueRelation[i] = new double[cliqueList.size()];
			// cliqueRelation[i][i] = 0;
			// for( int j = i; j < cliqueList.size(); j++ )
			// {
			// //cliqueRelation[i][j-i] = calculateCliqueRelation(i, j, mn);
			// cliqueRelation[i][j] = calculateCliqueRelation(i, j, mn);
			// cliqueRelation[j][i] = cliqueRelation[i][j];
			// }
			// }
			andListMap = new HashMap<String, List<Integer>>();
			permutationList = new ArrayList<List<Integer>>(mn.getNodeSize());
			permutationBoundaryList = new ArrayList<List<Integer>>(mn.getNodeSize());
			for (int i = 0; i < mn.getNodeSize(); i++)
			{
				int radix = mn.isTerminal(i) ? _symbolSet.getTerminalSize() : _symbolSet.getFunctionList().size();
				List<Integer> permutation = RadixSort.sort_permutation(mn.getDataList(), radix, i);
				List<Integer> boundary = new ArrayList<Integer>();
				int previous = -1;
				for (int n = 0; n < permutation.size(); n++) // individual
																// permutation
																// for this node
				{
					int nodeData = mn.getDataList().get(permutation.get(n))[i];

					if (nodeData == previous)
					{
						continue;
					}
					int increment = 0;
					while (nodeData > previous + increment)
					{
						increment++;
						boundary.add(n);
					}
					previous = nodeData;
				}
				// padding
				int boundarySize = boundary.size();
				// System.out.println("boundary " + boundarySize);
				// System.out.println("candidate size = " +
				// getCandidateSize(i));
				for (int n = 0; n < getCandidateSize(i) - boundarySize + 1; n++) // 最後に必ずpermutation.size()のインデックスを追加する．番兵のようなもの
				{
					boundary.add(permutation.size());
				}
				// System.out.println("mn " + _symbolSet.getTerminalList());
				// System.out.println("mn " + _symbolSet.getFunctionList());
				permutationList.add(permutation);
				permutationBoundaryList.add(boundary);
			}

			return;
		}

		// System.out.println("PEED 3");
		if (sampling == CLIQUE_BASED || sampling == RELATED_CLIQUE_BASED)
		{
			mn.extractCliqueList();

			List<Clique<SymbolType>> cliqueList = mn.getCliqueList();
			cliqueRelation = new double[cliqueList.size()][cliqueList.size()];
			for (int i = 0; i < cliqueList.size(); i++)
			{
				if( _environment.getAttribute("printClique") != null && Boolean.valueOf(_environment.getAttribute("printClique")) )
				{
					System.out.println("Clique " + i + ":" + cliqueList.get(i).toS_Expression(mn.getDepth(), mn.getArity()) + ":" + cliqueList.get(i));
				}
				cliqueRelation[i] = new double[cliqueList.size()];
				cliqueRelation[i][i] = 0;
				for (int j = i; j < cliqueList.size(); j++)
				{
					// cliqueRelation[i][j-i] = calculateCliqueRelation(i, j,
					// mn);
					cliqueRelation[i][j] = calculateCliqueRelation(i, j, mn);
					cliqueRelation[j][i] = cliqueRelation[i][j];
				}
			}
		}

		andListMap = new HashMap<String, List<Integer>>();
		permutationList = new ArrayList<List<Integer>>(mn.getNodeSize());
		permutationBoundaryList = new ArrayList<List<Integer>>(mn.getNodeSize());
		for (int i = 0; i < mn.getNodeSize(); i++)
		{
			int radix = mn.isTerminal(i) ? _symbolSet.getTerminalSize() : _symbolSet.getFunctionList().size();
			List<Integer> permutation = RadixSort.sort_permutation(mn.getDataList(), radix, i);
			List<Integer> boundary = new ArrayList<Integer>();
			int previous = -1;
			for (int n = 0; n < permutation.size(); n++) // individual
															// permutation for
															// this node
			{
				int nodeData = mn.getDataList().get(permutation.get(n))[i];

				if (nodeData == previous)
				{
					continue;
				}
				int increment = 0;
				while (nodeData > previous + increment)
				{
					increment++;
					boundary.add(n);
				}
				previous = nodeData;
			}
			// padding
			int boundarySize = boundary.size();
			// System.out.println("boundary " + boundarySize);
			// System.out.println("candidate size = " + getCandidateSize(i));
			for (int n = 0; n < getCandidateSize(i) - boundarySize + 1; n++) // 最後に必ずpermutation.size()のインデックスを追加する．番兵のようなもの
			{
				boundary.add(permutation.size());
			}
			// System.out.println("mn " + _symbolSet.getTerminalList());
			// System.out.println("mn " + _symbolSet.getFunctionList());
			permutationList.add(permutation);
			permutationBoundaryList.add(boundary);
		}
	}

	public int getCandidateSize(int i)
	{
		return mn.isTerminal(i) ? _symbolSet.getTerminalSize() : _symbolSet.getFunctionList().size();
	}

	public int getSumOfTreeSize()
	{
		return _sumOfTreeSize;
	}

	public double getSumOfTransitionCount()
	{
		return _sumOfTransitionCount;
	}

	public double getAverageBranchSize()
	{
		return _averageBranchSize;
	}

	public double getCutCount()
	{
		return _cutCount;
	}

	public static void main(String[] args)
	{
		GpEnvironment<DefaultGpIndividual> env = new GpEnvironment<DefaultGpIndividual>();
		GpSymbolSet symbolSet = new GpSymbolSet();
		SymbolType X = new DefaultSymbolType("X", 0);
		SymbolType Y = new DefaultSymbolType("Y", 0);
		SymbolType Z = new DefaultSymbolType("Z", 0);
		SymbolType A = new DefaultSymbolType("A", 2);
		SymbolType B = new DefaultSymbolType("B", 2);
		SymbolType C = new DefaultSymbolType("C", 2);
		SymbolType L = new DefaultSymbolType("L", 2);
		symbolSet.addSymbol(X);
		symbolSet.addSymbol(Y);
		symbolSet.addSymbol(Z);
		symbolSet.addSymbol(A);
		symbolSet.addSymbol(B);
		symbolSet.addSymbol(C);
		symbolSet.addSymbol(L);
		env.setSymbolSet(symbolSet);
		env.setNumberOfMaxDepth(3);
		env.setNumberOfMaxInitialDepth(3);

		env.putAttribute("portsT", "0.0");
		env.putAttribute("portsAlpha", "0.0");
		env.putAttribute("PPTArity", "2");
		env.putAttribute("PPTDepth", "3");
		env.putAttribute("PPTMaxCliqueSize", "5");
		env.putAttribute("PPTSignificanceLevel", "0.1");
		env.putAttribute("PPT_dependency", "chiSquare");
		env.putAttribute("PPTSmoothingParameter", "0");
		// env.putAttribute("peedSampling", "cliqueBased");
		env.putAttribute("peedSampling", "relatedCliqueBased");
		env.putAttribute("PPT_MN_order", "roulette");

		// env.putAttribute("peedSampling", "dependencyBased");

		env.setPopulationSize(4);

		// create sample tree
		GpNode tree1 = GpTreeManager.constructGpNodeFromString("(A (B X X) (C Y Y))", symbolSet);
		GpNode tree2 = GpTreeManager.constructGpNodeFromString("(A (B Y Y) (B Y X))", symbolSet);
		GpNode tree3 = GpTreeManager.constructGpNodeFromString("(B (A X X) (A X Y))", symbolSet);
		GpNode tree4 = GpTreeManager.constructGpNodeFromString("(B (C X Y) (C Y Y))", symbolSet);
		System.out.println(GpTreeManager.getS_Expression(tree1));
		System.out.println(GpTreeManager.getS_Expression(tree2));
		System.out.println(GpTreeManager.getS_Expression(tree3));
		System.out.println(GpTreeManager.getS_Expression(tree4));
		// GpNode tree5 =
		// GpTreeManager.constructGpNodeFromString("(C (C Y Y) (B Y X))",
		// symbolSet);
		
		List<GpIndividual> population = new ArrayList<GpIndividual>();
		population.add(new GpIndividual(tree1));
		population.add(new GpIndividual(tree2));
		population.add(new GpIndividual(tree3));
		population.add(new GpIndividual(tree4));
		// population.add( new GpIndividual(tree5) );

		PERCE peed = new PERCE(population, env);
		System.out.println("edge list   = " + peed.mn.getEdgeList());
		System.out.println("clique list = " + peed.mn.getCliqueList());
		System.out.println("transition matrix");
		double[][] matrix = peed.mn.getRelationMatrix();
		for( int i = 0; i < peed.mn.getCliqueList().size(); i++ )
		{
			for( int j = 0; j < peed.mn.getCliqueList().size(); j++ )
			{
				//System.out.print(matrix[i][j] + " ");
				System.out.printf( "%5f " , peed.mn.getRelationValue(i, j));
			}
			System.out.println();
		}
		System.out.println();
		
		for (int i = 0; i < peed.mn.getEdgeList().size(); i++)
		{
			System.out.println(" edge " + peed.mn.getEdgeList().get(i));
		}
		
		List<String> result = new ArrayList<String>();
		for( int i = 0; i < 10; i++ )
		{
			System.out.println();
			result.add( GpTreeManager.getS_Expression(peed.getRandomSample()) );
		}
		for( String s_exp: result )
		{
			System.out.println(s_exp);
		}
		
		System.out.println(peed.permutationList);
		System.out.println(peed.permutationBoundaryList);
		int node = 3;
		int symbol = 2;
		int start = peed.permutationBoundaryList.get(node).get(symbol);
		int end   = peed.permutationBoundaryList.get(node).get(symbol+1);
		System.out.println( peed.permutationList.get(node).subList(start, end) );
		// int[] a = new int[]{1,2,3};
		// int[] b = new int[]{1,2,3};
		// int[] c = new int[]{1,3,3};
		// System.out.println(a == b);
		// System.out.println(a.equals(b));
		// //Arrays.equals(a, a2)
		// Map<String, String> afo = new HashMap<String, String>();
		// afo.put(Arrays.toString(a), "a");
		// afo.put(Arrays.toString(b), "b");
		// afo.put(Arrays.toString(c), "c");
		// System.out.println(afo);
	}
}
