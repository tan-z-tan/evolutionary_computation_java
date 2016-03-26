package markovNet;

import geneticProgramming.GpEnvironment;
import geneticProgramming.GpIndividual;
import geneticProgramming.GpNode;
import geneticProgramming.GpSymbolSet;
import geneticProgramming.GpTreeManager;
import geneticProgramming.symbols.DefaultSymbolType;
import geneticProgramming.symbols.SymbolType;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import perce.CliqueDivisor;
import random.RandomManager;

/**
 * 最大深さ = depthの分岐数 = arityの完全木のプロトタイプ木上でマルコフネットを構築する．
 * 
 * @author tanji
 */
public class MarkovNetManager
{
	public static final int TYPE_MUTUAL_INFORMATION = 0;
	public static final int TYPE_CHI_SQUARE = 1;
	public static final int ORDER_ROOT = 0;
	public static final int ORDER_RANDOM = 1;
	public static final int ORDER_ROULETTE = 2;
	public static final int ORDER_MAX = 3;

	private int arity;
	private int depth;
	private int nodeSize;
	private List<Edge> edgeList;
	private List<Clique<SymbolType>> cliqueList;
	private int maxCliqueSize;
	private double significanceLevel;
	private double smoothingParameter;
	private GpEnvironment<? extends GpIndividual> environment;
	private List<MarkovNetNode<SymbolType>> nodeList;
	private int samplingOrder = 0;
	// private Map<Integer, double[]> merginalProbabilityMap;
	// private Map<Integer, List<Integer>> edgeMap;
	private Map<Integer, List<Edge>> edgeMap; // "4" -> List[edge_to_2, edge_to_5]
	private Map<String, Edge> edgeStringMap; // "1_2 -> edge" (edge from 1 to 2)
	private List<int[]> dataList;
	//private List<List<Object>> objectList;
	private List<Object[]> objectList;
	private double[][] relationMatrix;
	private int testType = TYPE_MUTUAL_INFORMATION;
	private List<Integer> terminalSymbolList;
	private List<Integer> functionSymbolList;

	/**
	 * マルコフネットのエッジを表現するクラス．
	 * 
	 * @author tanji
	 */
	public class Edge implements Comparable<Edge>
	{
		private int start;
		private int end;
		private double weight;

		public Edge(int i, int j, double probability)
		{
			start = i;
			end = j;
			weight = probability;
		}

		public int getStart()
		{
			return start;
		}

		public void setStart(int start)
		{
			this.start = start;
		}

		public int getEnd()
		{
			return end;
		}

		public void setEnd(int end)
		{
			this.end = end;
		}

		public double getWeight()
		{
			return weight;
		}

		public void setWeight(double weight)
		{
			this.weight = weight;
		}

		public String toString()
		{
			return new StringBuilder("(").append(start).append(", ").append(end).append(")[").append(weight)
					.append("]").toString();
		}

		@Override
		public int compareTo(Edge e)
		{
			if (start == e.start && end == e.end && weight == e.weight) { return 0; }

			if (weight - e.weight < 0)
				return -1;
			else if (weight - e.weight > 0)
				return 1;
			else
			{
				if (start < e.start)
					return -1;
				else if (start > e.start)
					return 1;
				else
				{
					if (end < e.end)
						return -1;
					else if (end > e.end)
						return 1;
					else
						return 0;
				}
			}
		}
	}

	/**
	 * コンストラクタ
	 * 
	 * @param environment
	 * @param significanceLevel
	 */
	public MarkovNetManager(List<? extends GpIndividual> promisingSolutions,
			GpEnvironment<? extends GpIndividual> environment)
	{
		this(promisingSolutions, environment, Integer.valueOf(environment.getAttribute("PPTArity")), Integer
				.valueOf(environment.getAttribute("PPTDepth")), Integer.valueOf(environment
				.getAttribute("PPTMaxCliqueSize")), Double.valueOf(environment.getAttribute("PPTSignificanceLevel")),
				Double.valueOf(environment.getAttribute("PPTSmoothingParameter")));
	}

	/**
	 * コンストラクタ
	 * 
	 * @param environment
	 * @param significanceLevel
	 */
	public MarkovNetManager(List<? extends GpIndividual> promisingSolutions,
			GpEnvironment<? extends GpIndividual> environment, int arity, int depth, int maxCliqueSize,
			double significanceLevel, double smoothingParameter)
	{
		this.environment = environment;
		this.arity = arity;
		this.depth = depth;
		this.maxCliqueSize = maxCliqueSize;
		this.significanceLevel = significanceLevel;
		this.smoothingParameter = smoothingParameter;

		initialize(promisingSolutions);
	}

	/**
	 * 初期化．ノードサイズの決定 nodesize = \sum_{i=0}^{depth-1}Math.pow(arity,i)．
	 * MarkovNetNodeのリストの作成． 依存関係エッジの検出(χ二乗検定)． クリークの検出（BronKerboschアルゴリズム）．
	 * クリークの確率の付加．
	 */
	private void initialize(List<? extends GpIndividual> promisingSolutions)
	{
		if (environment.getAttribute("PPT_MN_order").equals("root"))
		{
			this.samplingOrder = ORDER_ROOT;
		} else if (environment.getAttribute("PPT_MN_order").equals("random"))
		{
			this.samplingOrder = ORDER_RANDOM;
		} else if (environment.getAttribute("PPT_MN_order").equals("roulette"))
		{
			this.samplingOrder = ORDER_ROULETTE;
		} else if (environment.getAttribute("PPT_MN_order").equals("max"))
		{
			this.samplingOrder = ORDER_MAX;
		} else
		{
			System.out.println("PPT_MN_{root, random, max, roulette} must be specified.");
			System.exit(0);
		}

		if (environment.getAttribute("PPT_dependency").equals("chiSquare"))
		{
			this.testType = TYPE_CHI_SQUARE;
		} else if (environment.getAttribute("PPT_dependency").equals("mutualInformation"))
		{
			this.testType = TYPE_MUTUAL_INFORMATION;
		}

		int size = 0;
		for (int i = 0; i < depth; i++)
		{
			size += (int) Math.pow(arity, i);
		}
		nodeSize = size;

		// creates list of nodes.
		nodeList = new ArrayList<MarkovNetNode<SymbolType>>();
		for (int i = 0; i < nodeSize; i++)
		{
			MarkovNetNode<SymbolType> node;
			if (isTerminal(i))
			{
				node = new MarkovNetNode<SymbolType>(environment.getSymbolSet().getTerminalList());
			} else
			{
				node = new MarkovNetNode<SymbolType>(environment.getSymbolSet().getFunctionList());
			}
			nodeList.add(node);
		}

		dataList = new ArrayList<int[]>(promisingSolutions.size());
		//objectList = new ArrayList<List<Object>>(promisingSolutions.size());
		objectList = new ArrayList<Object[]>(promisingSolutions.size());
		for (GpIndividual ind : promisingSolutions)
		{
			List<GpNode> bfs = GpTreeManager.breadthFirstSearch(ind.getRootNode());
			//List<Object> objList = GpTreeManager.breathFirstSearchObject(ind.getRootNode());
			Object[] objList = new Object[bfs.size()];
			for( int i = 0; i < bfs.size(); i++ )
			{
				objList[i] = bfs.get(i).getExtraValue();
			}
			
			int[] data = new int[nodeSize];
			for (int n = 0; n < nodeSize; n++)
			{
				SymbolType type = bfs.get(n).getNodeType();
				if (type.getArgumentSize() == 0)
				{
					data[n] = environment.getSymbolSet().getTerminalList().indexOf(type);
				} else
				{
					data[n] = environment.getSymbolSet().getFunctionList().indexOf(type);
				}
			}
			dataList.add(data);
			objectList.add(objList);
		}

		terminalSymbolList = new ArrayList<Integer>();
		functionSymbolList = new ArrayList<Integer>();
		for (SymbolType symbol : environment.getSymbolSet().getSymbolList())
		{
			if (symbol.getArgumentSize() == 0)
			{
				terminalSymbolList.add(environment.getSymbolSet().getIndex(symbol));
			} else
			{
				functionSymbolList.add(environment.getSymbolSet().getIndex(symbol));
			}
		}
	}

	public void extractCliqueList()
	{
		System.out.println("The number of Edge = " + edgeList.size());

		System.out.println("  Clique Extracting...");
		List<BigInteger> resultCliques = BronKerboschAlgorithm.bronKerboschAlgorithm(nodeList);
		cliqueList = new ArrayList<Clique<SymbolType>>();
		for (int i = 0; i < resultCliques.size(); i++)
		{
			BigInteger cliqueCode = resultCliques.get(i);

			List<List<SymbolType>> candidateListList = new ArrayList<List<SymbolType>>();
			int candidateSize = 1;
			for (int index = 0; index < cliqueCode.bitLength(); index++)
			{
				if (cliqueCode.testBit(index))
				{
					candidateSize *= nodeList.get(index).getCandidateList().size();
					candidateListList.add(nodeList.get(index).getCandidateList());
				}
			}

			Clique<SymbolType> clique = new Clique<SymbolType>(cliqueCode, candidateListList, candidateSize);
			calculateCliqueWeight(clique);

			cliqueList.add(clique);
		}

		cliqueList = orderCliqueList(cliqueList);

		// remove overlapped cliques
		BigInteger mask = new BigInteger("0");
		for (int i = 0; i < cliqueList.size(); i++)
		{
			if (cliqueList.get(i).getCode().andNot(mask).bitCount() == 0)
			{
				cliqueList.remove(cliqueList.get(i--));
			} else
			{
				mask = mask.or(cliqueList.get(i).getCode());
			}
		}
		for (int i = 0; i < cliqueList.size(); i++)
		{
			// if( cliqueList.get(i).size() != 1 )
			// {
			// System.out.println("  clique code = (" + i + "/" +
			// cliqueList.size() + ") " + cliqueList.get(i));
			// }
		}

		// calculate relationship of pairs of cliques
		relationMatrix = new double[cliqueList.size()][];
		for (int i = 0; i < cliqueList.size(); i++)
		{
			relationMatrix[i] = new double[cliqueList.size() - i - 1];
			for (int j = i + 1; j < cliqueList.size(); j++)
			{
				// System.out.println("calculating " + i + " " + j);
				relationMatrix[i][j - i - 1] = calculateRelationShip(i, j);
				// System.out.println("Relation " + i + " " + j + " " +
				// relationMatrix[i][j-i-1]);
			}
		}

	}

	private double calculateRelationShip(int i, int j)
	{
		Clique<SymbolType> c_i = cliqueList.get(i);
		Clique<SymbolType> c_j = cliqueList.get(j);
		double dependency = 0;

		for (int n = 0; n < c_i.getCode().bitLength(); n++)
		{
			if (c_i.getCode().testBit(n))
			{
				for (int m = 0; m < c_j.getCode().bitLength(); m++)
				{
					if (c_j.getCode().testBit(m))
					{
						if (n == m) // overlapped node
						{
							dependency += 1;
						} else
						{
							String str = String.valueOf(n) + "_" + String.valueOf(m);
							if (edgeStringMap.containsKey(str))
							{
								dependency += edgeStringMap.get(str).getWeight();
							}
						}
					}
				}
			}
		}

		return dependency;
	}

	public double getRelationValue(int i, int j)
	{
		if (i == j)
		{
			return 1;
		} else if (i > j)
		{
			return getRelationValue(j, i);
		} else
		{
			return relationMatrix[i][j - i - 1];
		}
	}

	public void calculateProbabilityTable()
	{
		// System.out.println("Calculating probability start");
		for (int i = 0; i < this.cliqueList.size(); i++)
		{
			// System.out.println("clique " + i);
			Clique<SymbolType> clique = cliqueList.get(i);

			double[] probabilityArray = new double[clique.getCandidateSize()];
			int count = 0;
			// System.out.println(candidateListList);

			for (int[] ind : dataList)
			{
				// System.out.println(Arrays.toString(ind));
				countDataOnClique(ind, clique, probabilityArray);
				count++;
			}
			for (int probabilityIndex = 0; probabilityIndex < probabilityArray.length; probabilityIndex++)
			{
				probabilityArray[probabilityIndex] = probabilityArray[probabilityIndex] / count;
			}

			clique.setProbabilityList(probabilityArray);
		}
		// System.out.println("Calculating probability end");
	}

	/**
	 * サンプリングする．
	 * 
	 * @return
	 */
	public GpNode sampleNewTree()
	{
		List<Clique<SymbolType>> reorderedList = null;
		if (samplingOrder == ORDER_RANDOM)
		{
			reorderedList = orderCliqueList_randomRoot(cliqueList);
		} else if (samplingOrder == ORDER_MAX)
		{
			reorderedList = orderCliqueList_Max(cliqueList);
		} else if (samplingOrder == ORDER_ROULETTE)
		{
			reorderedList = orderCliqueList_Roulette(cliqueList);
		} else if (samplingOrder == ORDER_ROOT)
		{
			reorderedList = cliqueList;
		} else
		{
			System.out.println("PPT_MN_{root, random, max, roulette} must be specified.");
			System.exit(0);
		}
		return sampleNewTree(reorderedList, nodeSize);
	}

	/**
	 * サンプリングする．
	 * 
	 * @return
	 */
	private GpNode sampleNewTree(List<Clique<SymbolType>> cliqueList, int nodeSize)
	{
		// System.out.println("sampling from " + cliqueList);
		int[] sampleData = new int[nodeSize]; // ここにノードの値を入れていく．
		Object[] objectArray = new Object[nodeSize];
		
		Arrays.fill(sampleData, -1);
		
		for (Clique<SymbolType> targetClique : cliqueList)
		{
			// System.out.println();
			// System.out.println("Sample clique " + targetClique);
			List<Integer> sampleTargetList = new ArrayList<Integer>();
			for (int i = 0; i < targetClique.getCode().bitLength(); i++)
			{
				if (targetClique.getCode().testBit(i))
				{
					if (sampleData[i] == -1)
					{
						sampleTargetList.add(i);
					}
				}
			}
			if (sampleTargetList.size() == 0)
			{
				continue;
			}
			sampleMerginalProbability(sampleTargetList, sampleData, targetClique, smoothingParameter);
		}

		return constructGPTree(sampleData, objectArray);
	}

	/**
	 * クリークの周辺分布からクリーク中のノードの値をサンプリングする．サンプリングの対象ノードは，sampleTargetListのノードのみ．
	 * sampleDataに代入する．
	 * 
	 * @param sampleTargetList
	 * @param sampleData
	 * @param targetClique
	 */
	private static void sampleMerginalProbability(List<Integer> sampleTargetList, int[] sampleData,
			Clique<SymbolType> clique, double smoothingParameter)
	{
		int[] originalState = new int[clique.size()];
		int[] searchState = new int[clique.size()];

		int stateIndex = 0;
		int remainderSize = 0;
		BigInteger targetMask = new BigInteger("0");
		for (int i = 0; i < clique.getCode().bitLength(); i++)
		{
			if (clique.getCode().testBit(i))
			{
				if (sampleData[i] != -1)
				{
					searchState[stateIndex] = sampleData[i];
				} else
				{
					targetMask = targetMask.setBit(stateIndex);
					remainderSize++;
				}
				stateIndex++;
			}
		}
		if (remainderSize == 0) { return; }

		System.arraycopy(searchState, 0, originalState, 0, searchState.length);
		// System.out.println("Search State 1 " +
		// Arrays.toString(searchState));;

		double probabilitySum = 0;
		while (true)
		{
			probabilitySum += clique.getProbability(searchState);
			if (!incrementWithMask(searchState, 0, clique.getCandidateListList(), targetMask))
			{
				break;
			}
		}

		double threshold = probabilitySum * RandomManager.getRandom();
		double randValue = 0;
		System.arraycopy(originalState, 0, searchState, 0, searchState.length);
		// System.out.println("search state " + Arrays.toString(searchState));
		while (true)
		{
			randValue += clique.getProbability(searchState);
			// System.out.println("Search State " + Arrays.toString(searchState)
			// + ", " + randValue + " / " + threshold + " max = " +
			// probabilitySum);
			if (randValue >= threshold)
			{
				// set data
				stateIndex = 0;
				for (int sampleIndex = 0; sampleIndex < clique.getCode().bitLength(); sampleIndex++)
				{
					if (clique.getCode().testBit(sampleIndex))
					{
						if (sampleData[sampleIndex] == -1)
						{
							if (RandomManager.getRandom() > smoothingParameter)
							{
								sampleData[sampleIndex] = searchState[stateIndex];
							} else
							{
								sampleData[sampleIndex] = clique.getRandomState(sampleIndex);
							}
						}
						stateIndex++;
					}
				}
				return;
			}
			if (!incrementWithMask(searchState, 0, clique.getCandidateListList(), targetMask))
			{
				break;
			}
		}
		System.out.println("okashi--!");
		System.exit(0);
	}

	public int getIndex(SymbolType type)
	{
		if (type.getArgumentSize() == 0)
		{
			return environment.getSymbolSet().getTerminalList().indexOf(type);
		} else
		{
			return environment.getSymbolSet().getFunctionList().indexOf(type);
		}
	}

	/** 与えられたデータからGPの木構造を構築する． */
	public GpNode constructGPTree(int[] sampleData, Object[] objectList)
	{
		if (sampleData.length == 0) { return null; }

		GpNode root = new GpNode(nodeList.get(0).getCandidateList().get(sampleData[0]), 1);
		root.setExtraValue(objectList[0]);
		List<GpNode> frontLine = new ArrayList<GpNode>();
		List<GpNode> newLine = new ArrayList<GpNode>();
		frontLine.add(root);

		int nodeIndex = 1;
		for (int i = 1; i < depth; i++)
		{
			// System.out.println("depth = " + i);
			int size = (int) Math.pow(arity, i);
			for (int j = 0; j < size; j++)
			{
				int parentIndex = (int) (j / arity);
				// System.out.println("parent index = " + parentIndex);

				MarkovNetNode<SymbolType> node = nodeList.get(nodeIndex);
				GpNode newNode = new GpNode(node.getCandidateList().get(sampleData[nodeIndex]), i);
				newNode.setExtraValue(objectList[nodeIndex]);
				frontLine.get(parentIndex).addChild(newNode);
				newLine.add(newNode);
				nodeIndex++;
			}
			frontLine = newLine;
			newLine = new ArrayList<GpNode>();
		}

		return root;
	}

	public static boolean incrementWithMask(int[] state, int i, List<List<SymbolType>> candidateListList,
			BigInteger mask)
	{
		if (i >= state.length) { return false; }

		if (!mask.testBit(i)) { return incrementWithMask(state, i + 1, candidateListList, mask); }

		if (state[i] + 1 == candidateListList.get(i).size())
		{
			return incrementWithMask(state, i + 1, candidateListList, mask);
		} else
		{
			state[i] = state[i] + 1;
			for (int j = 0; j < i; j++)
			{
				if (mask.testBit(j))
				{
					state[j] = 0;
				}
			}
			return true;
		}
	}

	/**
	 * 指定したindividualが持っているクリーク上のノードのデータを返す．同時に確率表の対応する点をインクリメントする．
	 * 例えばクリーク=[A,B,C]でA={0,1},B={0,1,2},C={0,1}だとすると，[1,2,0]を返す．
	 * 
	 * @param ind
	 * @param clique
	 * @return
	 */
	private static List<Integer> countDataOnClique(GpIndividual ind, Clique<SymbolType> clique,
			double[] probabilityArray)
	{
		List<Integer> data = new ArrayList<Integer>(clique.getCode().bitCount());
		int startIndex = 0;
		int range = clique.getCandidateSize();
		for (int index = 0; index < clique.getCode().bitLength(); index++)
		{
			if (clique.getCode().testBit(index))
			{
				int state = clique.getCandidateListList().get(data.size())
						.indexOf(getNodeSymbol(ind, index).getNodeType());
				startIndex = startIndex + state * (range / clique.getCandidateListList().get(data.size()).size());
				range = range / clique.getCandidateListList().get(data.size()).size();
				data.add(state);
			}
		}
		// System.out.println(data + " Start Index = " + startIndex);
		probabilityArray[startIndex]++;

		return data;
	}

	/**
	 * 指定したindividualが持っているクリーク上のノードのデータを返す．同時に確率表の対応する点をインクリメントする．
	 * 例えばクリーク=[A,B,C]でA={0,1},B={0,1,2},C={0,1}だとすると，[1,2,0]を返す．
	 * 
	 * @param ind
	 * @param clique
	 * @return
	 */
	public static List<Integer> countDataOnClique(int[] ind, Clique<SymbolType> clique, double[] probabilityArray)
	{
		// System.out.println(" clique " + clique.getCode().toString(2));
		List<Integer> data = new ArrayList<Integer>(clique.getCode().bitCount());
		int startIndex = 0;
		int range = clique.getCandidateSize();
		for (int index = 0; index < clique.getCode().bitLength(); index++)
		{
			if (clique.getCode().testBit(index))
			{
				// int state =
				// clique.getCandidateListList().get(data.size()).indexOf(getNodeSymbol(ind,
				// index).getNodeType());
				int state = ind[index];
				startIndex = startIndex + state * (range / clique.getCandidateListList().get(data.size()).size());
				range = range / clique.getCandidateListList().get(data.size()).size();
				data.add(state);
			}
		}
		// System.out.println(data + " Start Index = " + startIndex);
		probabilityArray[startIndex]++;

		return data;
	}

	/**
	 * クリークを並び替える．非破壊的. クリークリストから重みを元にした並び替えをおこなう．
	 * 
	 * @param cliqueList
	 */
	public List<Clique<SymbolType>> orderCliqueList_Max(List<Clique<SymbolType>> cliqueList)
	{
		List<Clique<SymbolType>> copyList = new ArrayList<Clique<SymbolType>>(cliqueList);
		Collections.sort(copyList);

		return copyList;
	}

	/**
	 * クリークを並び替える．非破壊的. クリークリストから重みを元にルーレット選択を行い，追加する．それをリストの個数個分繰り返す．
	 * 
	 * @param cliqueList
	 */
	public List<Clique<SymbolType>> orderCliqueList_Roulette(List<Clique<SymbolType>> cliqueList)
	{
		List<Clique<SymbolType>> copyList = new ArrayList<Clique<SymbolType>>(cliqueList);
		List<Clique<SymbolType>> resultList = new ArrayList<Clique<SymbolType>>();
		double sum = 0;

		for (int i = 0; i < copyList.size(); i++)
		{
			sum += copyList.get(i).getWeight();
		}

		while (!copyList.isEmpty())
		{
			double randomValue = RandomManager.getRandom() * sum;
			double x = 0;
			Clique<SymbolType> clique = copyList.get(0);
			for (int i = 0; i < copyList.size(); i++)
			{
				x += copyList.get(i).getWeight();
				if (x > randomValue)
				{
					clique = copyList.get(i);
					break;
				}
			}
			resultList.add(clique);
			copyList.remove(clique);
			sum -= clique.getWeight();
		}

		return resultList;
	}

	/**
	 * クリークを並び替える．非破壊的.
	 * 
	 * @param cliqueList
	 */
	public List<Clique<SymbolType>> orderCliqueList_randomRoot(List<Clique<SymbolType>> cliqueList)
	{
		List<Clique<SymbolType>> copyList = new ArrayList<Clique<SymbolType>>(cliqueList);
		List<Clique<SymbolType>> resultList = new ArrayList<Clique<SymbolType>>();
		Collections.sort(copyList);

		double sum = 0;
		for (int i = 0; i < copyList.size(); i++)
		{
			sum += copyList.get(i).getWeight();
		}
		double randomValue = RandomManager.getRandom() * sum;
		double x = 0;
		Clique<SymbolType> root = copyList.get(0);
		for (int i = 0; i < copyList.size(); i++)
		{
			x += copyList.get(i).getWeight();
			if (x > randomValue)
			{
				root = copyList.get(i);
				break;
			}
		}

		copyList.remove(root);
		copyList.add(0, root); // set root clique to be at the first position

		while (!copyList.isEmpty())
		{
			BigInteger frontLine = copyList.get(0).getCode();
			resultList.add(copyList.get(0));
			// System.out.println("front line = " + frontLine.toString(2));
			copyList.remove(0);

			while (!copyList.isEmpty())
			{
				Clique<SymbolType> nextClique = null;
				int maxDuplicationCount = -1;
				for (int i = 0; i < copyList.size(); i++)
				{
					Clique<SymbolType> targetClique = copyList.get(i);
					int duplicationCount = targetClique.getCode().and(frontLine).bitCount();
					if (duplicationCount > maxDuplicationCount)
					{
						nextClique = targetClique;
						maxDuplicationCount = duplicationCount;
					}
				}

				if (maxDuplicationCount == -1) // if there is no duplicating
												// clique
				{
					break;
				}
				copyList.remove(nextClique);
				resultList.add(nextClique);
				frontLine = frontLine.or(nextClique.getCode());
			}
		}

		return resultList;
	}

	/**
	 * クリークを並び替える．
	 * 
	 * @param cliqueList
	 */
	private List<Clique<SymbolType>> orderCliqueList(List<Clique<SymbolType>> cliqueList)
	{
		List<Clique<SymbolType>> copyList = cliqueList;
		List<Clique<SymbolType>> resultList = new ArrayList<Clique<SymbolType>>();

		Collections.sort(copyList);
		// Collections.sort(copyList, );

		while (!copyList.isEmpty())
		{
			BigInteger frontLine = copyList.get(0).getCode();
			resultList.add(copyList.get(0));
			// System.out.println("front line = " + frontLine.toString(2));
			copyList.remove(0);

			while (!copyList.isEmpty())
			{
				Clique<SymbolType> nextClique = null;
				int maxDuplicationCount = -1;
				for (int i = 0; i < copyList.size(); i++)
				{
					Clique<SymbolType> targetClique = copyList.get(i);
					int duplicationCount = targetClique.getCode().and(frontLine).bitCount();
					if (duplicationCount > maxDuplicationCount)
					{
						nextClique = targetClique;
						maxDuplicationCount = duplicationCount;
					}
				}

				if (maxDuplicationCount == -1) // if there is no duplicating
												// clique
				{
					break;
				}
				copyList.remove(nextClique);
				resultList.add(nextClique);
				frontLine = frontLine.or(nextClique.getCode());
			}
		}

		return resultList;
	}

	/**
	 * クリークの重要度を計算してセットする．
	 * 
	 * @param clique
	 */
	private void calculateCliqueWeight(Clique<SymbolType> clique)
	{
		double weight = 0;
		// System.out.println(" Clique + " + clique);

		for (Edge edge : edgeList)
		{
			if (clique.getCode().testBit(edge.start) && clique.getCode().testBit(edge.end))
			{
				weight += edge.weight;
				// System.out.println(edge.start + " " + edge.end + " " + edge);
			}
		}
		clique.setWeight(weight);
	}

	/**
	 * 全結合のグラフからテストを行い，クリークを抽出する．
	 */
	public void extractDependencyCliqueFromFullTree(int cutNumber)
	{
		CliqueDivisor.divideCliqueN_times(this, 10);
	}

	/**
	 * 依存関係のエッジを検出する．χ二乗検定で確率がsignificanceLevel以下のエッジのみを残す．
	 */
	public void extractDependencyEdge_chiSquare()
	{
		edgeList = new ArrayList<Edge>();

		Map<Integer, Set<Edge>> edgeOnNodes = new HashMap<Integer, Set<Edge>>();
		for (int i = 0; i < nodeSize; i++)
		{
			edgeOnNodes.put(i, new TreeSet<Edge>());
		}

		List<Integer> terminalSymbolList = new ArrayList<Integer>();
		List<Integer> functionSymbolList = new ArrayList<Integer>();
		for (SymbolType symbol : environment.getSymbolSet().getSymbolList())
		{
			if (symbol.getArgumentSize() == 0)
			{
				terminalSymbolList.add(environment.getSymbolSet().getIndex(symbol));
			} else
			{
				functionSymbolList.add(environment.getSymbolSet().getIndex(symbol));
			}
		}

		for (int i = 0; i < nodeSize; i++)
		{
			// /System.out.println("i = " + i);
			for (int j = i + 1; j < nodeSize; j++)
			{
				// System.out.print("  j = " + j);
				List<Integer> candidateListA = isTerminal(i) ? terminalSymbolList : functionSymbolList;
				List<Integer> candidateListB = isTerminal(j) ? terminalSymbolList : functionSymbolList;
				double chiSquareValue = 0;
				chiSquareValue = 1 - ChiSquareTest.calculateChiSquareProbability(dataList, i, j, candidateListA,
						candidateListB);
				// System.out.println(" | " + chiSquareValue);
				if (chiSquareValue > significanceLevel)
				{
					Edge newEdge = new Edge(i, j, chiSquareValue);
					// System.out.println("new edge " + newEdge);
					if (edgeOnNodes.get(i).size() < maxCliqueSize && edgeOnNodes.get(j).size() < maxCliqueSize)
					{
						// System.out.println("add smart " + i + " " + j);
						edgeOnNodes.get(i).add(newEdge);
						edgeOnNodes.get(j).add(newEdge);
						edgeList.add(newEdge);
						nodeList.get(i).setNeighbor(j);
						nodeList.get(j).setNeighbor(i);
					} else if (edgeOnNodes.get(i).size() >= maxCliqueSize && edgeOnNodes.get(j).size() < maxCliqueSize)
					{
						Edge weakestEdge = edgeOnNodes.get(i).iterator().next();
						if (weakestEdge.weight < chiSquareValue
								|| (weakestEdge.weight == chiSquareValue && RandomManager.getRandom() < 0.5))
						{
							// System.out.println("add with i remove " + i + " "
							// + weakestEdge);
							edgeOnNodes.get(weakestEdge.start).remove(weakestEdge);
							edgeOnNodes.get(weakestEdge.end).remove(weakestEdge);
							nodeList.get(weakestEdge.start).removeNeighbor(weakestEdge.end);
							nodeList.get(weakestEdge.end).removeNeighbor(weakestEdge.start);
							edgeList.remove(weakestEdge);

							edgeOnNodes.get(i).add(newEdge);
							edgeOnNodes.get(j).add(newEdge);
							edgeList.add(newEdge);
							nodeList.get(i).setNeighbor(j);
							nodeList.get(j).setNeighbor(i);
						}
					} else if (edgeOnNodes.get(i).size() < maxCliqueSize && edgeOnNodes.get(j).size() >= maxCliqueSize)
					{
						Edge weakestEdge = edgeOnNodes.get(j).iterator().next();
						if (weakestEdge.weight < chiSquareValue
								|| (weakestEdge.weight == chiSquareValue && RandomManager.getRandom() < 0.5))
						{
							// System.out.println("add with j remove " +
							// weakestEdge);
							edgeOnNodes.get(weakestEdge.start).remove(weakestEdge);
							edgeOnNodes.get(weakestEdge.end).remove(weakestEdge);
							nodeList.get(weakestEdge.start).removeNeighbor(weakestEdge.end);
							nodeList.get(weakestEdge.end).removeNeighbor(weakestEdge.start);
							edgeList.remove(weakestEdge);

							edgeOnNodes.get(i).add(newEdge);
							edgeOnNodes.get(j).add(newEdge);
							edgeList.add(newEdge);
							nodeList.get(i).setNeighbor(j);
							nodeList.get(j).setNeighbor(i);
						}
					} else
					{
						Edge weakestEdge_i = edgeOnNodes.get(i).iterator().next();
						Edge weakestEdge_j = edgeOnNodes.get(j).iterator().next();
						if ((weakestEdge_i.weight < chiSquareValue && weakestEdge_j.weight < chiSquareValue)
								|| (weakestEdge_i.weight == chiSquareValue && RandomManager.getRandom() < 0.25)
								|| (weakestEdge_j.weight == chiSquareValue && RandomManager.getRandom() < 0.25))
						{
							// System.out.println("add with i j remove " +
							// weakestEdge_i + " " + weakestEdge_j);
							edgeOnNodes.get(i).remove(weakestEdge_i);
							edgeOnNodes.get(j).remove(weakestEdge_j);
							nodeList.get(weakestEdge_i.start).removeNeighbor(weakestEdge_i.end);
							nodeList.get(weakestEdge_i.end).removeNeighbor(weakestEdge_i.start);
							nodeList.get(weakestEdge_j.start).removeNeighbor(weakestEdge_j.end);
							nodeList.get(weakestEdge_j.end).removeNeighbor(weakestEdge_j.start);
							edgeList.remove(weakestEdge_i);
							edgeList.remove(weakestEdge_j);

							edgeOnNodes.get(i).add(newEdge);
							edgeOnNodes.get(j).add(newEdge);
							edgeList.add(newEdge);
							nodeList.get(i).setNeighbor(j);
							nodeList.get(j).setNeighbor(i);
						}

					}
				}
			}
		}

		// edgeMap = new HashMap<Integer, List<Integer>>();
		edgeMap = new HashMap<Integer, List<Edge>>();
		edgeStringMap = new HashMap<String, Edge>();
		for (Edge edge : edgeList)
		{
			int s = edge.start;
			int e = edge.end;
			if (edgeMap.get(s) == null)
			{
				// edgeMap.put(s, new ArrayList<Integer>());
				edgeMap.put(s, new ArrayList<Edge>());
			}
			if (edgeMap.get(e) == null)
			{
				edgeMap.put(e, new ArrayList<Edge>());
				// edgeMap.put(e, edge);
			}
			// edgeMap.get(s).add(e);
			// edgeMap.get(e).add(s);
			edgeMap.get(s).add(edge);
			edgeMap.get(e).add(edge);

			if (s <= e)
			{
				edgeStringMap.put(String.valueOf(s) + "_" + String.valueOf(e), edge);
			} else
			{
				edgeStringMap.put(String.valueOf(e) + "_" + String.valueOf(s), edge);
			}
		}
	}

	/**
	 * 依存関係のエッジを検出する．正規化された相互情報量(redundancy / Z)がsignificanceLevel以上のエッジのみを残す．
	 */
	public void extractDependencyEdge_mutualInformation()
	{
		edgeList = new ArrayList<Edge>();

		Map<Integer, Set<Edge>> edgeOnNodes = new HashMap<Integer, Set<Edge>>();
		for (int i = 0; i < nodeSize; i++)
		{
			edgeOnNodes.put(i, new TreeSet<Edge>());
		}

		List<Integer> terminalSymbolList = new ArrayList<Integer>();
		List<Integer> functionSymbolList = new ArrayList<Integer>();
		for (SymbolType symbol : environment.getSymbolSet().getSymbolList())
		{
			if (symbol.getArgumentSize() == 0)
			{
				terminalSymbolList.add(environment.getSymbolSet().getIndex(symbol));
			} else
			{
				functionSymbolList.add(environment.getSymbolSet().getIndex(symbol));
			}
		}

		for (int i = 0; i < nodeSize; i++)
		{
			for (int j = i + 1; j < nodeSize; j++)
			{
				// System.out.print("(i, j) = (" + i + "," + j + ")");
				List<Integer> candidateListA = isTerminal(i) ? terminalSymbolList : functionSymbolList;
				List<Integer> candidateListB = isTerminal(j) ? terminalSymbolList : functionSymbolList;
				double chiSquareValue = 0;

				chiSquareValue = MutualInformationTest.calculateNormalizedRedundancy(dataList, i, j, candidateListA,
						candidateListB);
//				System.out.println( "(i, j) = " + i + " " + j + " = " + chiSquareValue );

				// System.out.println(" | " + chiSquareValue);
				if (chiSquareValue > significanceLevel)
				{
					Edge newEdge = new Edge(i, j, chiSquareValue);
					// System.out.println("new edge " + newEdge);
					if (edgeOnNodes.get(i).size() < maxCliqueSize && edgeOnNodes.get(j).size() < maxCliqueSize)
					{
//						System.out.println("add with removing");
//						for( Iterator iter = edgeOnNodes.get(i).iterator(); iter.hasNext();  )
//						{
//							System.out.println(" Next = " + iter.next() );
//						}
						// System.out.println("add smart " + i + " " + j);
						edgeOnNodes.get(i).add(newEdge);
						edgeOnNodes.get(j).add(newEdge);
						edgeList.add(newEdge);
						nodeList.get(i).setNeighbor(j);
						nodeList.get(j).setNeighbor(i);
					} else if (edgeOnNodes.get(i).size() >= maxCliqueSize && edgeOnNodes.get(j).size() < maxCliqueSize)
					{
//						System.out.println("add with removing i >= MC, j < MC");
//						for( Iterator iter = edgeOnNodes.get(i).iterator(); iter.hasNext();  )
//						{
//							System.out.println(" Next = " + iter.next() );
//						}
						Edge weakestEdge = edgeOnNodes.get(i).iterator().next();
						if (weakestEdge.weight < chiSquareValue	|| (weakestEdge.weight == chiSquareValue && RandomManager.getRandom() < 0.5))
						{
							// System.out.println("add with i remove " + i + " "
							// + weakestEdge);
							edgeOnNodes.get(weakestEdge.start).remove(weakestEdge);
							edgeOnNodes.get(weakestEdge.end).remove(weakestEdge);
							nodeList.get(weakestEdge.start).removeNeighbor(weakestEdge.end);
							nodeList.get(weakestEdge.end).removeNeighbor(weakestEdge.start);
							edgeList.remove(weakestEdge);

							edgeOnNodes.get(i).add(newEdge);
							edgeOnNodes.get(j).add(newEdge);
							edgeList.add(newEdge);
							nodeList.get(i).setNeighbor(j);
							nodeList.get(j).setNeighbor(i);
						}
					} else if (edgeOnNodes.get(i).size() < maxCliqueSize && edgeOnNodes.get(j).size() >= maxCliqueSize)
					{
//						System.out.println("add with removing i < MC, j >= MC");
//						for( Iterator iter = edgeOnNodes.get(i).iterator(); iter.hasNext();  )
//						{
//							System.out.println(" Next = " + iter.next() );
//						}
						Edge weakestEdge = edgeOnNodes.get(j).iterator().next();
						if (weakestEdge.weight < chiSquareValue || (weakestEdge.weight == chiSquareValue && RandomManager.getRandom() < 0.5))
						{
							// System.out.println("add with j remove " +
							// weakestEdge);
							edgeOnNodes.get(weakestEdge.start).remove(weakestEdge);
							edgeOnNodes.get(weakestEdge.end).remove(weakestEdge);
							nodeList.get(weakestEdge.start).removeNeighbor(weakestEdge.end);
							nodeList.get(weakestEdge.end).removeNeighbor(weakestEdge.start);
							edgeList.remove(weakestEdge);

							edgeOnNodes.get(i).add(newEdge);
							edgeOnNodes.get(j).add(newEdge);
							edgeList.add(newEdge);
							nodeList.get(i).setNeighbor(j);
							nodeList.get(j).setNeighbor(i);
						}
					} else
					{
//						System.out.println("add with removing ? ");
//						for( Iterator iter = edgeOnNodes.get(i).iterator(); iter.hasNext();  )
//						{
//							System.out.println(" Next = " + iter.next() );
//						}
						Edge weakestEdge_i = edgeOnNodes.get(i).iterator().next();
						Edge weakestEdge_j = edgeOnNodes.get(j).iterator().next();
						if ((weakestEdge_i.weight < chiSquareValue && weakestEdge_j.weight < chiSquareValue)
								|| (weakestEdge_i.weight == chiSquareValue && RandomManager.getRandom() < 0.25)
								|| (weakestEdge_j.weight == chiSquareValue && RandomManager.getRandom() < 0.25))
						{
							// System.out.println("add with i j remove " +
							// weakestEdge_i + " " + weakestEdge_j);
							edgeOnNodes.get(i).remove(weakestEdge_i);
							edgeOnNodes.get(j).remove(weakestEdge_j);
							nodeList.get(weakestEdge_i.start).removeNeighbor(weakestEdge_i.end);
							nodeList.get(weakestEdge_i.end).removeNeighbor(weakestEdge_i.start);
							nodeList.get(weakestEdge_j.start).removeNeighbor(weakestEdge_j.end);
							nodeList.get(weakestEdge_j.end).removeNeighbor(weakestEdge_j.start);
							edgeList.remove(weakestEdge_i);
							edgeList.remove(weakestEdge_j);

							edgeOnNodes.get(i).add(newEdge);
							edgeOnNodes.get(j).add(newEdge);
							edgeList.add(newEdge);
							nodeList.get(i).setNeighbor(j);
							nodeList.get(j).setNeighbor(i);
						}

					}
				}
			}
		}

		// edgeMap = new HashMap<Integer, List<Integer>>();
		edgeMap = new HashMap<Integer, List<Edge>>();
		edgeStringMap = new HashMap<String, Edge>();
		for (Edge edge : edgeList)
		{
			int s = edge.start;
			int e = edge.end;
			if (edgeMap.get(s) == null)
			{
				// edgeMap.put(s, new ArrayList<Integer>());
				edgeMap.put(s, new ArrayList<Edge>());
			}
			if (edgeMap.get(e) == null)
			{
				edgeMap.put(e, new ArrayList<Edge>());
				// edgeMap.put(e, edge);
			}
			// edgeMap.get(s).add(e);
			// edgeMap.get(e).add(s);
			edgeMap.get(s).add(edge);
			edgeMap.get(e).add(edge);

			if (s <= e)
			{
				edgeStringMap.put(String.valueOf(s) + "_" + String.valueOf(e), edge);
			} else
			{
				edgeStringMap.put(String.valueOf(e) + "_" + String.valueOf(s), edge);
			}
		}
	}
	
	/**
	 * 親子関係のエッジを強制的に追加する．
	 */
	public void addParentChildRelation()
	{
		for(int i = 0; i < this.nodeSize; i++)
		{
			int parent = getParent(i);
			if(parent < 0)
			{
				continue;
			}
			String edgeStr = new StringBuilder(String.valueOf(parent)).append("_").append(String.valueOf(i)).toString();
			if( edgeStringMap.get(edgeStr) == null )
			{
				Edge newEdge = new Edge(parent, i, 1);
				addEdge(newEdge, edgeStr, parent, i);
			}
		}
	}
	
	/**
	 * 兄弟関係のエッジを強制的に追加する．
	 */
	public void addSiblingRelation()
	{
		for(int i = 0; i < this.nodeSize; i++)
		{
			List<Integer> siblings = getChildren(i);
			for(int c = 0; c < siblings.size(); c++ )
			{
				for(int target = c+1; target < siblings.size(); target++ )
				{	
					String edgeStr = new StringBuilder(siblings.get(c)).append("_").append(siblings.get(target)).toString();
					if( ! edgeStringMap.containsKey(edgeStr) )
					{
						Edge newEdge = new Edge(siblings.get(c), siblings.get(target), 1);
						addEdge(newEdge, edgeStr, siblings.get(c), siblings.get(target));
					}
				}
			}
				
		}
	}
	
	private void addEdge(Edge newEdge, String edgeStr, int i, int j) // i<=j
	{
		edgeList.add(newEdge);
		if( edgeMap.containsKey(i) )
		{
			edgeMap.get(i).add(newEdge);
		}
		else
		{
			List<Edge> list = new ArrayList<Edge>();
			list.add(newEdge);
			edgeMap.put(i, list);
		}
		if( edgeMap.containsKey(j) )
		{
			edgeMap.get(j).add(newEdge);
		}
		else
		{
			List<Edge> list = new ArrayList<Edge>();
			list.add(newEdge);
			edgeMap.put(j, list);
		}	
		edgeStringMap.put(edgeStr, newEdge);
	}
	
	/**
	 * 依存関係のエッジを検出する．testTypeによって判別の方法が変わる．
	 * 依存関係があると判定されたらその度合いを返す．この値はtestTypeによって違い
	 * ，例えばchi-squareテストで有意度5%以上の条件（significanceLevel=0.95）なら0.95以上の値が帰ってくる．
	 * 依存関係が無い場合は-1を返す．
	 */
	public double dependencyTest(int i, int j)
	{
		edgeList = new ArrayList<Edge>();

		List<Integer> candidateListA = isTerminal(i) ? terminalSymbolList : functionSymbolList;
		List<Integer> candidateListB = isTerminal(j) ? terminalSymbolList : functionSymbolList;
		double dependencyValue = 0;

		if (testType == TYPE_MUTUAL_INFORMATION)
		{
			dependencyValue = MutualInformationTest.calculateNormalizedRedundancy(dataList, i, j, candidateListA,
					candidateListB);
		} else if (testType == TYPE_CHI_SQUARE)
		{
			dependencyValue = ChiSquareTest.calculateChiSquareProbability(dataList, i, j, candidateListA,
					candidateListB);
		} else
		{
			System.out.println("okashii yo. Please specify test type \"mutualInformation\" or \"chiSquare\".");
		}

		if (dependencyValue > significanceLevel)
		{
			return dependencyValue;
		} else
		{
			return -1;
		}
	}

	public Map<String, Edge> getEdgeStringMap()
	{
		return edgeStringMap;
	}

	public void setEdge(int i, int j)
	{
		nodeList.get(i).setNeighbor(j);
		nodeList.get(j).setNeighbor(i);
	}

	/**
	 * return true if and only if the specified index is terminal index.
	 * 
	 * @param i
	 * @return
	 */
	public boolean isTerminal(int i)
	{
		if (i >= (nodeSize - (int) Math.pow(arity, depth - 1))) { return true; }
		return false;
	}

	/**
	 * return index of parent node if there exist, otherwise return -1;
	 * 
	 * @param i
	 * @return
	 */
	public int getParent(int i)
	{
		if (i == 0) { return -1; }
		return (int) ((i - 1) / arity);
	}

	// public List<Integer> getDependentEdgeList(int i)
	public List<Edge> getDependentEdgeList(int i)
	{
		if (edgeMap.containsKey(i))
		{
			return edgeMap.get(i);
		} else
		{
			return new ArrayList<Edge>(0);
		}
	}

	/**
	 * return index list of children nodes if there exist, otherwise return
	 * empty list;
	 * 
	 * @param i
	 * @return
	 */
	public List<Integer> getChildren(int i)
	{
		List<Integer> childIndexes = new ArrayList<Integer>();
		if (isTerminal(i)) { return childIndexes; }
		for (int c = 0; c < arity; c++)
		{
			childIndexes.add(i * arity + c + 1);
		}
		return childIndexes;
	}

	/**
	 * returns node at i-th
	 * 
	 * @param ind
	 * @param i
	 * @return
	 */
	private static GpNode getNodeSymbol(GpIndividual ind, int i)
	{
		return getNodeSymbol(ind.getRootNode(), i);
	}

	/**
	 * returns node at i-th
	 * 
	 * @param ind
	 * @param i
	 * @return
	 */
	public static GpNode getNodeSymbol(GpNode root, int i)
	{
		return GpTreeManager.getNodeAt(root, i);
	}

	public List<MarkovNetNode<SymbolType>> getNodeList()
	{
		return nodeList;
	}

	public List<Edge> getEdgeList()
	{
		return edgeList;
	}

	public void setEdgeList(List<Edge> edgeList)
	{
		this.edgeList = edgeList;
	}

	public List<Clique<SymbolType>> getCliqueList()
	{
		return cliqueList;
	}

	public void setCliqueList(List<Clique<SymbolType>> cliqueList)
	{
		this.cliqueList = cliqueList;
	}

	public int getArity()
	{
		return arity;
	}

	public void setArity(int arity)
	{
		this.arity = arity;
	}

	public int getDepth()
	{
		return depth;
	}

	public void setDepth(int depth)
	{
		this.depth = depth;
	}

	public int getNodeSize()
	{
		return nodeSize;
	}

	public void setNodeSize(int nodeSize)
	{
		this.nodeSize = nodeSize;
	}

	public Map<Integer, List<Edge>> getEdgeMap()
	{
		return edgeMap;
	}

	public void setEdgeMap(Map<Integer, List<Edge>> edgeMap)
	{
		this.edgeMap = edgeMap;
	}

	public List<int[]> getDataList()
	{
		return dataList;
	}

	public List<Object[]> getObjeList()
	{
		return objectList;
	}

	public void setDataList(List<int[]> dataList)
	{
		this.dataList = dataList;
	}

	public double[][] getRelationMatrix()
	{
		return relationMatrix;
	}

	// main method for test
	public static void main(String[] args)
	{

		GpEnvironment<GpIndividual> env = new GpEnvironment<GpIndividual>();
		GpSymbolSet set = new GpSymbolSet();
		set.addSymbol(new DefaultSymbolType("A", 2));
		set.addSymbol(new DefaultSymbolType("B", 2));
		set.addSymbol(new DefaultSymbolType("C", 2));
		set.addSymbol(new DefaultSymbolType("X", 0));
		set.addSymbol(new DefaultSymbolType("Y", 0));
		set.addSymbol(new DefaultSymbolType("Z", 0));

		List<GpIndividual> population = new ArrayList<GpIndividual>();
		population.add(new GpIndividual(GpTreeManager.constructGpNodeFromString("(A (B Y Z) (C X Y))", set)));
		population.add(new GpIndividual(GpTreeManager.constructGpNodeFromString("(B (A X Y) (B X Z))", set)));
		population.add(new GpIndividual(GpTreeManager.constructGpNodeFromString("(C (C X Y) (C X X))", set)));
		// population.add( new
		// GpIndividual(GpTreeManager.constructGpNodeFromString("(B (A Y Y) (A Y X))",
		// set)) );
		population.add(new GpIndividual(GpTreeManager.constructGpNodeFromString("(B (C Y X) (B Y Y))", set)));
		// population.add( new
		// GpIndividual(GpTreeManager.constructGpNodeFromString("(C (C X Y) (B Y Y))",
		// set)) );
		// population.add( new
		// GpIndividual(GpTreeManager.constructGpNodeFromString("(C (C X Y) (B Y Y))",
		// set)) );
		// population.add( new
		// GpIndividual(GpTreeManager.constructGpNodeFromString("(A (B X Y) (A Y X))",
		// set)) );
		env.setPopulation(population);
		env.setPopulationSize(3);
		env.putAttribute("PPTArity", "2");
		env.putAttribute("PPTDepth", "3");
		env.putAttribute("PPTMaxCliqueSize", "3");
		env.putAttribute("PPTSignificanceLevel", "0.5");
		env.putAttribute("PPTSmoothingParameter", "0.0");
		env.putAttribute("PPT_MN_order", "root");
		env.setSymbolSet(set);

		MarkovNetManager net = new MarkovNetManager(population, env);
		net.extractDependencyEdge_chiSquare();
		net.extractCliqueList();
		net.calculateProbabilityTable();
		System.out.println("Clique List = " + net.cliqueList);
		GpNode node = net.sampleNewTree();

		List<Map<String, Integer>> samples = new ArrayList<Map<String, Integer>>();
		for (int s = 0; s < 7; s++)
		{
			Map<String, Integer> afo = new HashMap<String, Integer>();
			samples.add(afo);
		}
		for (int i = 0; i < 10000; i++)
		{
			node = net.sampleNewTree();
			for (int s = 0; s < 7; s++)
			{
				String name = getNodeSymbol(node, s).getNodeType().getSymbolName();
				if (samples.get(s).containsKey(name))
				{
					samples.get(s).put(name, samples.get(s).get(name) + 1);
				} else
				{
					samples.get(s).put(name, 1);
				}
			}
		}
		for (int s = 0; s < 7; s++)
		{
			System.out.println(samples.get(s));
		}

		// System.out.println(GpTreeManager.getS_Expression(node));
	}

}