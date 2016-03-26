package ports;

import ecCore.selector.AbstractSelector;
import ecCore.selector.TournamentSelector;
import ecCore.selector.TruncateSelector;
import geneticProgramming.DefaultGpIndividual;
import geneticProgramming.GpEnvironment;
import geneticProgramming.GpIndividual;
import geneticProgramming.GpNode;
import geneticProgramming.GpSymbolSet;
import geneticProgramming.GpTreeManager;
import geneticProgramming.symbols.DefaultSymbolType;
import geneticProgramming.symbols.SymbolType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;

import random.RandomManager;

/**
 * An implementation of PORTS (Program Optimization by Random Tree Sampling).
 * 
 * @author tanji
 */
public class PORTS_Cut
{
    private List<GpNode> _promisingSolutions;
    private GpSymbolSet _symbolSet;
    private GpEnvironment<? extends GpIndividual> _environment;
    
    // 2008 12 15
    // _transitionMatricesは各関数ノードから終端記号へのリンクがあるかどうかを記録する．
    private List<Set<SymbolType>> _transitionMatrices;

    private int _sumOfTreeSize;
    private double _sumOfTransitionCount;
    
    private double _probabilityT;
    private double _probabilityAlpha;
    private double _leastProbability = 0;
    private double _minimumAlpha = 0.05;
    private boolean _randomStart = false;
    private double _depthDependency = 0.0;
    
    private Map<Integer, Integer> _lastConstructionSizeMap;
    private Map<Integer, Integer> _allConstructionSizeMap;
    private List<Double> _offspringSizeList;
    private List<List<Integer>> _lastFragmentsSize;//各個体に関するフラグメントのリスト
    private List<Double> _lastAlphaRatio;//アルファ遷移の割合
    private boolean _isAutoUpdateT = false;
    private boolean _isAutoUpdateAlpha = false;
    private boolean _sameTreeCut = true;
    
    protected double _averageFragmentSize;
    protected double _averageTreeSize = 0;
    protected double _averageTransitionCount;
    protected double _averageBranchSize = 0;
    protected double _cutCount = 0;
    
    // 木の子供への情報を保持するClass，ポインタの用に使う．
    class TreePointer
    {
        GpNode parent;
        int index;

        /** 親とインデックスを指定して，ノードへのポインタを構築する */
        public TreePointer(GpNode parent, int index)
        {
            this.parent = parent;
            this.index = index;
        }

        /** ポインタの指し示すノードを返す */
        public GpNode getOwn()
        {
            return parent.getChild(index);
        }

        public String toString()
        {
            return getOwn().toString();
        }
    }

    /**
     * constructor
     * 
     * @param promisingSolutions
     * @param environment
     */
    public PORTS_Cut(List<? extends GpIndividual> promisingIndividual, GpEnvironment<? extends GpIndividual> environment)
    {
        _symbolSet = environment.getSymbolSet();
        _environment = environment;
        _probabilityT = Double.valueOf(_environment.getAttribute("portsT"));
        _probabilityAlpha = Double.valueOf(_environment.getAttribute("portsAlpha"));
        
        _lastConstructionSizeMap = new HashMap<Integer, Integer>();
        //_allConstructionSizeMap = new HashMap<Integer, Integer>();
        _allConstructionSizeMap = new TreeMap<Integer, Integer>();
        _offspringSizeList = new ArrayList<Double>();
        _lastFragmentsSize = new ArrayList<List<Integer>>();
        _lastAlphaRatio = new ArrayList<Double>();
        
        if( _environment.getAttribute("autoUpdateT") != null )
        {
            _isAutoUpdateT = Boolean.valueOf(_environment.getAttribute("autoUpdateT"));
        }
        if( _environment.getAttribute("autoUpdateAlpha") != null )
        {
            _isAutoUpdateAlpha = Boolean.valueOf(_environment.getAttribute("autoUpdateAlpha"));
        }
        if( _environment.getAttribute("minimumAlpha") != null )
        {
            _minimumAlpha = Double.valueOf(_environment.getAttribute("minimumAlpha"));
        }
        if( _environment.getAttribute("portsStart") != null )
        {
            _randomStart = _environment.getAttribute("portsStart").equals("random");
        }
        if( _environment.getAttribute("portsSameTreeCut") != null )
        {
            _sameTreeCut = Boolean.valueOf( _environment.getAttribute("sameTreeCut") );
        }
        if( _environment.getAttribute("portsLeastProbability") != null )
        {
            _leastProbability = Double.valueOf( _environment.getAttribute("portsLeastProbability") );
        }
        if( _environment.getAttribute("portsDepthDependency") != null )
        {
            _depthDependency = Double.valueOf( _environment.getAttribute("portsDepthDependency") );
        }
        update(promisingIndividual);
    }

    /**
     * 終端記号への遷移を記録する 終端記号を持たない関数ノードがある場合があるため． _transitionMatricesは各関数ノードから終端記号へのリンクがあるかどうかを記録する．
     */
    private void calculateTransitionMatrices()
    {
        _transitionMatrices = new ArrayList<Set<SymbolType>>();
        for (int i = 0; i < _environment.getSymbolSet().getSymbolSize(); i++)
        {
            _transitionMatrices.add(new HashSet<SymbolType>());
        }
        for (GpNode root : _promisingSolutions)
        {
            List<GpNode> bfs = GpTreeManager.breadthFirstSearch(root);
            for (GpNode node : bfs)
            {
                if (node.isNonterminal())
                {
                    int symbolIndex = _symbolSet.getIndex(node.getNodeType());
                    Set<SymbolType> functionSet = _transitionMatrices.get(symbolIndex);
                    for (GpNode child : node.getChildren())
                    {
                        if (child.isTerminal())
                        {
                            functionSet.add(node.getNodeType());
                        }
                    }
                }
            }
        }
    }

    /**
     * returns random GpNode by method of PORTS
     * 
     * @return
     */
    public GpNode getRandomSample()
    {
        GpNode childNode = getRandomSamplingTopDown();
        while (childNode == null)
        {
            childNode = getRandomSamplingTopDown();
        }
        return childNode;
    }

    /**
     * Jan. 21, 2009 returns random GpNode by method of PORTS
     * 
     * @return
     */
    public GpNode getRandomSamplingTopDown()
    {
        GpNode child = _promisingSolutions.get((int) (RandomManager.getRandom() * _promisingSolutions.size())).shallowClone();
        if( _randomStart )
        {
            child = GpTreeManager.getNodeAt(child, (int)(RandomManager.getRandom() * GpTreeManager.getNodeSize(child))).shallowClone();
        }
        child.setDepth(1);
        
        int sumOfTransitionCount = 0;
        
        // log
        List<Integer> fragmentSizeList = new ArrayList<Integer>();
        double alphaProbability = 0;
        int transitionCount = 0;
        
        Stack<TreePointer> agendaStack = new Stack<TreePointer>();
        // 最初のノードは親がいないので(かわいそう)ダミーのポインタを登録しておく．
        GpNode dammyNode = new GpNode(new DefaultSymbolType("dammy", 1), 0); // ダミーなので深さ0
        dammyNode.addChild(child);
        agendaStack.push(new TreePointer(dammyNode, 0));
        
        _lastConstructionSizeMap.clear();
        int fragmentIndex = 1;
        boolean atLeastOneTransition = false;
        
        // agendaが空になる(過不足無い木が生成される)まで繰り返す
        while (!agendaStack.isEmpty())
        {
            // System.out.println("Agenda List = " + agendaStack );
            // まず，候補ノードポインタを登録する
            GpNode fragmentRoot = agendaStack.pop().getOwn();
            List<TreePointer> growthFront = new ArrayList<TreePointer>();
            for (int i = 0; i < fragmentRoot.getNodeType().getArgumentSize(); i++)
            {
                growthFront.add(new TreePointer(fragmentRoot, i));
            }
            int fragmentSize = 1;
            boolean leafAppending = false;
            if (fragmentRoot.isTerminal())
            {
                leafAppending = true;
                // continue;
            }
            
            // 以下のループで一つのFragmentがサンプリングされる，ランダム遷移が起こったらBreak!
            while (true)
            {
                if (growthFront.size() == 0)
                {
                    _lastConstructionSizeMap.put(fragmentIndex, fragmentSize);
                    fragmentSizeList.add(fragmentSize);
                    fragmentIndex++;
                    break;
                }

                // 成長するポインタを決める
                GpNode copyNode = null;
                TreePointer growthPoint = growthFront.get((int) (RandomManager.getRandom() * growthFront.size()));
                boolean isTerminal = _environment.getNumberOfMaxDepth() <= (growthPoint.parent.getDepth() + 1);
                // if( isTerminal ) System.out.println("Depth = " +
                // (growthPoint.parent.getDepth() + 1));
                // System.out.println("Depth limit ? = " + isTerminal);

                double randValue = RandomManager.getRandom();
                if (randValue <= _probabilityT) // ランダム遷移するか条件判定
                {
                    _averageBranchSize += growthFront.size();
                    _cutCount++;
                    // 全ての候補点をランダム遷移させる
                    for (TreePointer newAgendaPointer : growthFront)
                    {
                        sumOfTransitionCount++;
                        //_sumOfTransitionCount ++;
                        transitionCount ++;
                        isTerminal = _environment.getNumberOfMaxDepth() <= (newAgendaPointer.parent.getDepth() + 1);
                        double r = RandomManager.getRandom();
                        if (r < _probabilityAlpha)// alpha transition
                        {
                            copyNode = selectAlphaTransitionNode(_promisingSolutions, newAgendaPointer.getOwn().getDepth(), isTerminal);
                            alphaProbability++;
                        } else
                        // beta transition
                        {
                            if (isTerminal && newAgendaPointer.getOwn().isTerminal())
                            {
                                copyNode = newAgendaPointer.getOwn();
                                if (copyNode == null)
                                    System.err.println("beta 1");
                            } else if (isTerminal)
                            {
                                // TODO バグがあるかもしれない
                                // copyNode = selectAlphaTransitionNode(_promisingSolutions, isTerminal);
                                copyNode = selectBetaTransitionNode(_promisingSolutions, newAgendaPointer.parent, newAgendaPointer.index, isTerminal);
                                if (copyNode == null)
                                    System.err.println("beta 2");
                            } else
                            {
                                copyNode = selectBetaTransitionNode(_promisingSolutions, newAgendaPointer.parent, newAgendaPointer.index, isTerminal);
                                if (copyNode == null)
                                    System.err.println("beta 3");
                            }
                            // System.out.println( "(β): " + agendaPointer.parent + " -> " + copyNode );
                        }
                        copyNode = copyNode.shallowClone();
                        copyNode.setDepth(newAgendaPointer.parent.getDepth() + 1);
                        newAgendaPointer.parent.setChildAt(newAgendaPointer.index, copyNode);
                        agendaStack.push(newAgendaPointer);
                    }
                    _lastConstructionSizeMap.put(fragmentIndex, fragmentSize);
                    fragmentSizeList.add(fragmentSize);
                    fragmentIndex++;
                    atLeastOneTransition = true;
                    break;
                } else
                {
                    // 　ランダム遷移しない場合．一番簡単．
                    // gamma transition
                    if (!isTerminal)
                    {
                        copyNode = growthPoint.getOwn();
                    } else
                    // 深さ制限のため終端記号へ強制遷移
                    {
                        if (growthPoint.getOwn().isTerminal())
                        {
                            copyNode = growthPoint.getOwn();
                        } else
                        {
                            copyNode = new GpNode(_environment.getSymbolSet().getTerminalSymbol(), 1);
                        }
                    }
                }
                // System.out.println("(γ): " + growthPoint.parent + " -> " + copyNode);
                fragmentSize++;
                copyNode = copyNode.shallowClone();
                copyNode.setDepth(growthPoint.parent.getDepth() + 1);
                growthPoint.parent.setChildAt(growthPoint.index, copyNode);
                growthFront.remove(growthPoint);
                for (int i = 0; i < copyNode.getNodeType().getArgumentSize(); i++)
                {
                    growthFront.add(new TreePointer(copyNode, i)); // 前線を進める
                }
            }
        }
        // サンプリング終了
        if (!_sameTreeCut || atLeastOneTransition)
        {
            // fragmentの平均サイズを記録する
            child.setParent(null);
            GpTreeManager.calculateDepth(child, 1);
            double averageSubsolutionSize = 0;
            // System.out.println("afo " + _lastConstructionSizeMap);
            for (Integer size : _lastConstructionSizeMap.values())
            {
                if (_allConstructionSizeMap.containsKey(size))
                {
                    _allConstructionSizeMap.put(size, _allConstructionSizeMap.get(size) + 1);
                } else
                {
                    _allConstructionSizeMap.put(size, 1);
                }
                averageSubsolutionSize += size;
            }
            _offspringSizeList.add(averageSubsolutionSize / _lastConstructionSizeMap.size());
            _lastFragmentsSize.add(fragmentSizeList);
            _lastAlphaRatio.add( alphaProbability / transitionCount );
            _sumOfTreeSize += GpTreeManager.getNodeSize(child);
            _sumOfTransitionCount += sumOfTransitionCount;
            
            return child;
        } else
        {
            return null;
        }
    }

    /**
     * jan. 21, 2009 returns random GpNode by PORTS
     * 
     * @return
     */
    public GpNode getRandomSamplingBottomUp()
    {
        return null;
    }

    /**
     * ラベルに関係なくランダムにノードを選択する α transition
     * 
     * @param parents
     * @return
     */
    public GpNode selectAlphaTransitionNode(List<GpNode> parents, int depth, boolean terminalSearch)
    {
        if (!terminalSearch)
        {
            GpNode nextRandomTree = parents.get((int) (RandomManager.getRandom() * parents.size()));
            
            if( _depthDependency > RandomManager.getRandom() )
            {
                //System.out.println("The target depth = " + depth);
                int size = GpTreeManager.getNodeSizeWithDepth(nextRandomTree, depth);
                while( size == 0 )
                {
                    nextRandomTree = parents.get((int) (RandomManager.getRandom() * parents.size()));
                    size = GpTreeManager.getNodeSizeWithDepth(nextRandomTree, depth);
                }
                return GpTreeManager.getNodeAtWithDepth(nextRandomTree, depth, ((int) (RandomManager.getRandom() * size)));
            }
            int size = GpTreeManager.getNodeSize(nextRandomTree);
            return GpTreeManager.getNodeAt(nextRandomTree, ((int) (RandomManager.getRandom() * size)));
        } else
        {
            GpNode nextRandomTree = parents.get((int) (RandomManager.getRandom() * parents.size()));
            int size = GpTreeManager.getTerminalNodeSize(nextRandomTree);
            if (size == 0)
            {
                System.err.println("Size == 0!! " + GpTreeManager.getS_Expression(nextRandomTree));
            }
            int randomIndex = (int) (RandomManager.getRandom() * size);
            GpNode copyNode = GpTreeManager.getTerminalNodeAt(nextRandomTree, randomIndex);
            if (copyNode == null)
            {
                System.err.println("alpha 1");
                System.out.println("size and randomIndex = " + size + " " + randomIndex);
                return new GpNode(_environment.getSymbolSet().getTerminalSymbol(), 1);
            }
            return copyNode;
        }
    }

    /**
     * 同じラベルを持つノードをランダムに選択する β transition
     * 
     * @param parents
     * @param currentLeaf
     * @param isMax
     * @return
     */
    public GpNode selectBetaTransitionNode(List<GpNode> parents, GpNode currentLeaf, int position, boolean isMax)
    {
        int loop = 0;

        // if there are no such transitions (currentLeaf -> terminal symbol).
        if (isMax && _transitionMatrices.get(_environment.getSymbolSet().getIndex(currentLeaf.getNodeType())).size() == 0)
        {
            System.out.println("there is no terminal transition from " + currentLeaf.getNodeType() + " to terminal symbol.");
            return new GpNode(_environment.getSymbolSet().getTerminalSymbol(), currentLeaf.getDepth() + 1);
        }

        List<GpNode> candidateList = new ArrayList<GpNode>();
        while (true)
        {
            GpNode nextRandomTree = parents.get((int) (RandomManager.getRandom() * parents.size()));
            candidateList = GpTreeManager.breadthFirstSearch(nextRandomTree);

            List<GpNode> hitNodes = new ArrayList<GpNode>();
            // gets nodes which have same label
            for (GpNode node : candidateList)
            {
                // if(
                // node.getNodeType().getSymbolName().equals(currentLeaf.getNodeType().getSymbolName())
                // )
                if (node.getNodeType() == currentLeaf.getNodeType())
                {
                    hitNodes.add(node);
                }
            }
            if (hitNodes.size() != 0)
            {
                GpNode candidate = hitNodes.get((int) (RandomManager.getRandom() * hitNodes.size()));
                if (!isMax)
                {
                    return (GpNode) candidate.getChild(position);
                } else
                {
                    List<GpNode> leafNodes = new ArrayList<GpNode>();
                    for (int c = 0; c < candidate.getChildren().size(); c++)
                    {
                        if (candidate.getChild(c).isTerminal())
                        {
                            leafNodes.add(candidate.getChild(c));
                        }
                    }
                    if (leafNodes.size() != 0)
                    {
                        return (GpNode) leafNodes.get((int) (RandomManager.getRandom() * leafNodes.size()));
                    } else
                    { // do nothing
                    }
                }
            }
            if (loop > 1000)
            {
                System.out.println("over " + loop + " " + hitNodes + " " + isMax);
            }
            loop++;
        }
    }

    /**
     * 同じラベルを持つノードをランダムに選択する β transition
     * 
     * @param parents
     * @param currentLeaf
     * @param isMax
     * @return
     */
    public GpNode selectBetaTransitionNodeCut(List<GpNode> parents, GpNode currentLeaf, boolean isMax)
    {
        int loop = 0;

        // if there are no such transitions (currentLeaf -> terminal symbol).
        if (isMax && _transitionMatrices.get(_environment.getSymbolSet().getIndex(currentLeaf.getNodeType())).size() == 0)
        {
            System.out.println("there is no terminal transition from " + currentLeaf.getNodeType() + " to terminal symbol.");
            return new GpNode(_environment.getSymbolSet().getTerminalSymbol(), currentLeaf.getDepth() + 1);
        }

        List<GpNode> candidateList = new ArrayList<GpNode>();
        while (true)
        {
            GpNode nextRandomTree = parents.get((int) (RandomManager.getRandom() * parents.size()));
            candidateList = GpTreeManager.breadthFirstSearch(nextRandomTree);

            List<GpNode> hitNodes = new ArrayList<GpNode>();
            // gets nodes which have same label
            for (GpNode node : candidateList)
            {
                // if(
                // node.getNodeType().getSymbolName().equals(currentLeaf.getNodeType().getSymbolName())
                // )
                if (node.getNodeType() == currentLeaf.getNodeType())
                {
                    hitNodes.add(node);
                }
            }
            if (hitNodes.size() != 0)
            {
                GpNode candidate = hitNodes.get((int) (RandomManager.getRandom() * hitNodes.size()));
                if (!isMax)
                {
                    return candidate;
                } else
                {
                    List<GpNode> leafNodes = new ArrayList<GpNode>();
                    for (int c = 0; c < candidate.getChildren().size(); c++)
                    {
                        if (candidate.getChild(c).isTerminal())
                        {
                            leafNodes.add(candidate.getChild(c));
                        }
                    }
                    if (leafNodes.size() != 0)
                    {
                        return (GpNode) leafNodes.get((int) (RandomManager.getRandom() * leafNodes.size()));
                    } else
                    { // do nothing
                    }
                }
            }
            if (loop > 1000)
            {
                System.out.println("over " + loop + " " + hitNodes + " " + isMax);
            }
            loop++;
        }
    }
    
    public void update(List<? extends GpIndividual> promisingIndividuals)
    {
        // parameter update
        // TODO
        _promisingSolutions = new ArrayList<GpNode>();
        for (GpIndividual individual : promisingIndividuals)
        {
            _promisingSolutions.add(individual.getRootNode());
        }
        if( _lastFragmentsSize.size() != 0 )
        {
            _averageFragmentSize = 0;
            double averageAlphaProbability = 0;
            int selectedCount = 0;
            //System.out.println( "The size of last fragment list = " + _lastFragmentsSize.size() );
            for (int i = 0; i < _environment.getPopulationSize(); i++ )
            {
                GpIndividual individual = promisingIndividuals.get(i);
                int index = _environment.getPopulation().indexOf(individual) - _environment.getEliteSize();
                if( index < _environment.getEliteSize() ) // elite
                {
                    continue;
                }
                //System.out.println("Index = " + index);
                for( int size: _lastFragmentsSize.get(index) )
                {
                    _averageFragmentSize += size;
                    selectedCount ++;
                }
                //_averageFragmentSize += _offspringSizeList.get(_environment.getPopulation().indexOf(individual));
                averageAlphaProbability += _lastAlphaRatio.get(index);
            }
            //_averageFragmentSize = _averageFragmentSize / promisingIndividuals.size();
            _averageFragmentSize = _averageFragmentSize / selectedCount;
            //System.out.println("Average Fragment Size = " + _averageFragmentSize);
            double averageAllFragmentSize = 0;
            int allCount = 0;
            for( List<Integer> fragmentSizeList: _lastFragmentsSize )
            {
                for( int index = 0; index < fragmentSizeList.size(); index++ )
                {
                    averageAllFragmentSize += fragmentSizeList.get(index);
                    allCount++;
                }
            }
            //System.out.println("All count = " + allCount + ", sum of fragment size =" + averageAllFragmentSize);
            averageAllFragmentSize = averageAllFragmentSize / allCount;
            
            double leastProbability = 1 / ((double)_sumOfTreeSize / _lastFragmentsSize.size());
            double ratio = ((1 / (_averageFragmentSize)) - leastProbability) / ((1 / (averageAllFragmentSize)) - leastProbability);
            //double ratio = ((1 / (_averageFragmentSize)) ) / ((1 / (averageAllFragmentSize)) );
            
            if( _isAutoUpdateT )
            {
                System.out.println("Auto Update");
                if( !_environment.getAttributes().containsKey("portsUpdate") )
                {
                    _environment.getAttributes().put("portsUpdate", "adaptive");
                }
                
                if( _environment.getAttributes().containsKey("portsUpdate") && _environment.getAttribute("portsUpdate").equals("none") )
                {
                    System.out.println("none");
                    // nothing to do
                }
                else if( _environment.getAttributes().containsKey("portsUpdate") && _environment.getAttribute("portsUpdate").equals("adaptive") )
                {
                    System.out.println("adaptive");
                    _probabilityT = leastProbability + (_probabilityT -leastProbability) * ratio;
                }
                else if( _environment.getAttributes().containsKey("portsUpdate") && _environment.getAttribute("portsUpdate").equals("liner") )
                {
                    System.out.println("liner");
                    _probabilityT = Math.max( leastProbability, _probabilityT + Double.valueOf(_environment.getAttribute("portsUpdateParameter")) );
                }                
                else if( _environment.getAttributes().containsKey("portsUpdate") && _environment.getAttribute("portsUpdate").equals("exponential") )
                {
                    System.out.println("exponential");
                    _probabilityT = Math.max( leastProbability, _probabilityT * Double.valueOf(_environment.getAttribute("portsUpdateParameter")) );
                }
                else if( _environment.getAttributes().containsKey("portsUpdate") && _environment.getAttribute("portsUpdate").equals("pl") )
                {
                    System.out.println("pl");
                    _probabilityT = 1 * leastProbability;
                }
            }
            if( _isAutoUpdateAlpha )
            {
                //averageAlphaProbability = Math.min(1, 1.01 * averageAlphaProbability / promisingIndividuals.size());
                averageAlphaProbability = Math.max(_minimumAlpha, averageAlphaProbability / promisingIndividuals.size());
                //averageAlphaProbability = averageAlphaProbability / promisingIndividuals.size();
                //averageAlphaProbability = 0.05 + 0.01 * (_probabilityAlpha - 0.05) * (averageAlphaProbability / promisingIndividuals.size());
                //averageAlphaProbability = 0.1 + (_probabilityAlpha - 0.1) * (averageAlphaProbability / promisingIndividuals.size()) / (_probabilityAlpha);
                
                _probabilityAlpha = averageAlphaProbability;
            }
            System.out.println("Least Probability = " + leastProbability);
            System.out.println("fragmentSize_all = " + averageAllFragmentSize);
            System.out.println("fragmentSize_selected= " + _averageFragmentSize);
            System.out.println("portsT = " + _probabilityT);
            System.out.println("portsAlpha = " + _probabilityAlpha);
        }
        
        calculateTransitionMatrices();
        
        _lastConstructionSizeMap.clear();
        _allConstructionSizeMap.clear();
        _offspringSizeList.clear();
        _lastFragmentsSize.clear();
        _lastAlphaRatio.clear();
        
        _averageBranchSize = 0;
        _cutCount = 0;
        _averageTransitionCount = 0;
        _averageFragmentSize = 0;
        _sumOfTreeSize = 0;
        _sumOfTransitionCount = 0;
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

//    public static void oneDimensionChainTest()
//    {
//        GpEnvironment<DefaultGpIndividual> env = new GpEnvironment<DefaultGpIndividual>();
//        GpSymbolSet symbolSet = new GpSymbolSet();
//        SymbolType T = new DefaultSymbolType("T", 0);
//        SymbolType N = new DefaultSymbolType("N", 1);
//        symbolSet.addSymbol(T);
//        symbolSet.addSymbol(N);
//        
//        env.putAttribute("porteT", "0.5");
//        env.putAttribute("porteAlpha", "1");
//        env.setSymbolSet(symbolSet);
//        env.setNumberOfMaxDepth(100);
//        env.setNumberOfMaxInitialDepth(100);
//        
//        GpNode root = new GpNode(N, 1);
//        GpNode chain = root;
//        for(int i = 0; i < 10 -2; i++)
//        {
//            GpNode child = new GpNode(N, i + 1);
//            chain.addChild(child);
//            chain = child;
//        }
//        chain.addChild(new GpNode(T, 11));
//        
//        System.out.println(GpTreeManager.getS_Expression(root));
//        
//        List<GpNode> treeList = new ArrayList<GpNode>();
//        treeList.add(root);
//        
//        PORTS_Cut porte = new PORTS_Cut(treeList, env);
//        double sizeSum = 0;
//        double repeatNum = 1000;
//        for( int i = 0; i < repeatNum; i++ )
//        {
//            GpNode sample = porte.getRandomSamplingTopDown();
//            while(sample == null)
//            {
//                sample = porte.getRandomSamplingTopDown();
//            }
//            //System.out.println(sample);
//            //System.out.println(GpTreeManager.getS_Expression(sample));
//            sizeSum += GpTreeManager.getNodeSize(sample);
//        }
//        System.out.println(sizeSum / repeatNum);
//        //System.out.println(GpTreeManager.getS_Expression(sample) + ", " + sample.getDepthFromHere());
//    }
//    
//    public static void main(String[] args)
//    {
//        oneDimensionChainTest();
//        System.exit(0);
//        GpEnvironment<DefaultGpIndividual> env = new GpEnvironment<DefaultGpIndividual>();
//        GpSymbolSet symbolSet = new GpSymbolSet();
//        SymbolType T = new DefaultSymbolType("t", 0);
//        SymbolType N1 = new DefaultSymbolType("n1", 1);
//        SymbolType N2 = new DefaultSymbolType("n2", 2);
//        SymbolType N3 = new DefaultSymbolType("n3", 3);
//        symbolSet.addSymbol(T);
//        symbolSet.addSymbol(N1);
//        symbolSet.addSymbol(N2);
//        symbolSet.addSymbol(N3);
//        env.setSymbolSet(symbolSet);
//        env.setNumberOfMaxDepth(12);
//        env.setNumberOfMaxInitialDepth(12);
//
//        env.putAttribute("porteT", "1.0");
//        env.putAttribute("porteAlpha", "0.0");
//        env.setPopulationSize(4);
//
//        // create sample tree
//        GpNode n0 = new GpNode(N3, 1, 4);
//
//        GpNode n11 = new GpNode(N2, 2, 2);
//        GpNode n12 = new GpNode(N2, 2, 3);
//        GpNode n13 = new GpNode(N1, 2, 2);
//
//        GpNode n21 = new GpNode(T, 3, 1);
//        GpNode n22 = new GpNode(T, 3, 1);
//        GpNode n23 = new GpNode(N1, 3, 2);
//        GpNode n24 = new GpNode(N3, 3, 2);
//        GpNode n25 = new GpNode(T, 3, 1);
//
//        GpNode n31 = new GpNode(T, 4, 1);
//        GpNode n32 = new GpNode(T, 4, 1);
//        GpNode n33 = new GpNode(T, 4, 1);
//        GpNode n34 = new GpNode(T, 4, 1);
//
//        n0.addChild(n11);
//        n0.addChild(n12);
//        n0.addChild(n13);
//
//        n11.addChild(n21);
//        n11.addChild(n22);
//
//        n12.addChild(n23);
//        n12.addChild(n24);
//
//        n13.addChild(n25);
//
//        n23.addChild(n31);
//
//        n24.addChild(n32);
//        n24.addChild(n33);
//        n24.addChild(n34);
//
//        // run]
//        // RandomManager.setSeed(13);
//
//        // GpNode node = GpTreeManager.full(env);
//        // System.out.println(GpTreeManager.getS_Expression(node));
//        // System.out.println( GpTreeManager.getNodeSize(node) );
//
//        List<GpNode> treeList = new ArrayList<GpNode>();
//        treeList.add(n0);
//        // treeList.add(node);
//        PORTS_Cut porte = new PORTS_Cut(treeList, env);
//        GpNode sample = porte.getRandomSamplingTopDown();
//        //		
//        // double averageAllSize = 0;
//        // int sum = 0;
//        // for( int i = 0; i < 50; i++ )
//        // {
//        // //GpNode sample = porte.getRandomSamplingTopDown();
//        // GpNode sample = porte.getRandomSamplingTopDown();
//        // //System.out.println( GpTreeManager.getS_Expression(sample) );
//        // Map<Integer,Integer> sizeMap = porte.getLastConstructionSizeMap();
//        // double averageSize = 0;
//        // for(Entry<Integer, Integer> entry: sizeMap.entrySet())
//        // {
//        // averageSize += entry.getValue();
//        // //sum += entry.getValue();
//        // sum ++;
//        // }
//        // averageAllSize += (averageSize);
//        // }
//        // System.out.println("total average = " + averageAllSize / (sum));
//        //
//        System.out.println("Depth = " + GpTreeManager.getDepth(n0));
//        System.out.println("Depth = " + GpTreeManager.getDepth(sample));
//        System.out.println(GpTreeManager.getS_Expression(n0));
//        System.out.println(GpTreeManager.getS_Expression(sample));
//    }
}
