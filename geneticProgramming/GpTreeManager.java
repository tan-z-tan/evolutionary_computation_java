package geneticProgramming;

import geneticProgramming.symbols.DefaultSymbolType;
import geneticProgramming.symbols.PPT_Symbol;
import geneticProgramming.symbols.SymbolL;
import geneticProgramming.symbols.SymbolType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

import random.RandomManager;
import visualization.S_ExpressionHandler;

public class GpTreeManager
{
    public static final int RETRIEVAL_BFS = 0;
    public static final int RETRIEVAL_DFS = 1;
    private static List<Integer> _sizeOfLastTimeCrossover;
    private static List<Integer> _sizeOfLastTimeMutation;
    static
    {
        _sizeOfLastTimeCrossover = new ArrayList<Integer>();
        _sizeOfLastTimeMutation = new ArrayList<Integer>();
    }

    /**
     * creates random tree.
     * 
     * @param environment
     *            it has the parameters of GP.
     * @return newly created root node of tree.
     */
    public static GpNode grow(GpEnvironment<? extends GpIndividual> environment)
    {
        return grow(environment, environment.getNumberOfMaxInitialDepth());
    }
    
    /**
     * creates random tree.
     * 
     * @param environment
     *            it has the parameters of GP.
     * @return newly created root node of tree.
     */
    public static GpNode grow(GpEnvironment<? extends GpIndividual> environment, int maxDepth)
    {
        if (maxDepth == 1)
        {
            return new GpNode(environment.getSymbolSet().getTerminalSymbol(), 1, 1);
        }

        GpNode root = new GpNode(environment.getSymbolSet().getFunctionSymbol(), 1, 1);

        List<GpNode> leafList = new ArrayList<GpNode>();
        List<GpNode> nextLeaf = new ArrayList<GpNode>();

        leafList.add(root);

        // depth 1 to max -1
        for (int depth = 2; depth <= maxDepth; depth++)
        {
            for (int i = 0; i < leafList.size(); i++)
            {
                int childrenSize = leafList.get(i).getNodeType().getArgumentSize();
                for (int j = 0; j < childrenSize; j++)
                {
                    GpNode child;
                    if (depth == maxDepth)
                    {
                        child = new GpNode(environment.getSymbolSet().getTerminalSymbol(), depth);
                    } else
                    {
                        child = new GpNode(environment.getSymbolSet().getRandomType(), depth);
                    }
                    leafList.get(i).addChild(child);
                    nextLeaf.add(child);
                }
            }

            leafList = nextLeaf;
            nextLeaf = new ArrayList<GpNode>();
        }

        calculateDepth(root, 1);
        return root;
    }

    /**
     * creates random tree.
     * 
     * @param environment which has the parameters of GP.
     * @return newly created root node of tree.
     */
    public static GpNode grow_PPT(GpEnvironment<? extends GpIndividual> environment)
    {
        return grow_PPT(environment, environment.getNumberOfMaxInitialDepth(), environment.getNumberOfMaxInitialDepth());
    }

    /**
     * creates random tree.
     * 
     * @param environment which has the parameters of GP.
     * @return newly created root node of tree.
     */
    public static GpNode grow_PPT(GpEnvironment<? extends GpIndividual> environment, int growDepth, int maxDepth)
    {
        if (maxDepth == 1)
        {
            return new GpNode(environment.getSymbolSet().getTerminalSymbol(), 1, 1);
        }

        GpNode root = new GpNode(environment.getSymbolSet().getFunctionSymbol(), 1, 1);

        List<GpNode> leafList = new ArrayList<GpNode>();
        List<GpNode> nextLeaf = new ArrayList<GpNode>();

        leafList.add(root);

        // depth 1 to max -1
        for (int depth = 2; depth <= maxDepth; depth++)
        {
            for (int i = 0; i < leafList.size(); i++)
            {
                int childrenSize = leafList.get(i).getNodeType().getArgumentSize();
                for (int j = 0; j < childrenSize; j++)
                {
                    boolean isL = false;
                    GpNode child;
                    if (depth >= growDepth)
                    {
                        child = new GpNode(environment.getSymbolSet().getTerminalSymbol(), depth);
                    } else
                    {
                        child = new GpNode(environment.getSymbolSet().getRandomType(), depth);
                    }
                    if( child.isTerminal() )
                    {
                        //System.out.println("infill " + depth + " / " + maxDepth);
                        child = infill(environment, maxDepth - depth + 1, new SymbolL("L", Integer.valueOf(environment.getAttribute("PPTArity"))));
                        //child = infill(environment, maxDepth - depth + 1, environment.getSymbolSet().getSymbolByName("L"));
                        isL = true;
                    }
                    leafList.get(i).addChild(child);
                    if( !isL )
                    {
                        nextLeaf.add(child);
                    }
                }
            }

            leafList = nextLeaf;
            nextLeaf = new ArrayList<GpNode>();
        }

        calculateDepth(root, 1);
        return root;
    }
    
    /**
     * creates random tree.
     * @param environment
     *            it has the parameters of GP.
     * @return newly created root node of tree.
     */
    public static GpNode infill(GpEnvironment<? extends GpIndividual> environment, int targetDepth, SymbolType symbolL)
    {
        if( targetDepth == 1 )
        {
            return new GpNode(environment.getSymbolSet().getTerminalSymbol(), 1, 1);
        }
        
        GpNode root = new GpNode(symbolL, 1, 1);

        List<GpNode> leafList = new ArrayList<GpNode>();
        List<GpNode> nextLeaf = new ArrayList<GpNode>();

        leafList.add(root);

        // depth 1 to max -1
        for (int depth = 2; depth <= targetDepth; depth++)
        {
            //System.out.println("depth " + depth + "/" + targetDepth);
            
            for (int i = 0; i < leafList.size(); i++)
            {
                int childrenSize = leafList.get(i).getNodeType().getArgumentSize();
                
                GpNode child;
                for (int j = 0; j < childrenSize; j++)
                {
                    if (depth == targetDepth)
                    {
                        child = new GpNode(environment.getSymbolSet().getTerminalSymbol(), depth);
                    } else
                    {
                        //System.out.println(i + " " + j);
                        if( i == 0 && j == 0 )
                        {
                            //System.out.println("Symbol L");
                            child = new GpNode(symbolL, depth);
                        }
                        else
                        {
                            child = new GpNode(environment.getSymbolSet().getFunctionSymbol(), depth);
                        }
                    }
                    leafList.get(i).addChild(child);
                    nextLeaf.add(child);
                }
            }

            leafList = nextLeaf;
            nextLeaf = new ArrayList<GpNode>();
        }

        calculateDepth(root, 1);
        return root;
    }
    
    /**
     * creates random tree.
     * 
     * @param environment
     *            it has the parameters of GP.
     * @return newly created root node of tree.
     */
    public static GpNode full(GpEnvironment<? extends GpIndividual> environment)
    {
        return full(environment, environment.getNumberOfMaxInitialDepth());
    }

    /**
     * creates random tree.
     * @param environment
     *            it has the parameters of GP.
     * @return newly created root node of tree.
     */
    public static GpNode full(GpEnvironment<? extends GpIndividual> environment, int maxDepth)
    {
        GpNode root = new GpNode(environment.getSymbolSet().getFunctionSymbol(), 1, 1);

        List<GpNode> leafList = new ArrayList<GpNode>();
        List<GpNode> nextLeaf = new ArrayList<GpNode>();

        leafList.add(root);

        // depth 1 to max -1
        for (int depth = 2; depth <= maxDepth; depth++)
        {
            for (int i = 0; i < leafList.size(); i++)
            {
                int childrenSize = leafList.get(i).getNodeType().getArgumentSize();
                for (int j = 0; j < childrenSize; j++)
                {
                    GpNode child;
                    if (depth == maxDepth)
                    {
                        child = new GpNode(environment.getSymbolSet().getTerminalSymbol(), depth);
                    } else
                    {
                        child = new GpNode(environment.getSymbolSet().getFunctionSymbol(), depth);
                    }
                    leafList.get(i).addChild(child);
                    nextLeaf.add(child);
                }
            }

            leafList = nextLeaf;
            nextLeaf = new ArrayList<GpNode>();
        }

        calculateDepth(root, 1);
        return root;
    }
    
    /**
     * creates random tree.
     * 
     * @param environment which has the parameters of GP.
     * @return newly created root node of tree.
     */
    public static GpNode full_PPT(GpEnvironment<? extends GpIndividual> environment, int fullDepth, int maxDepth)
    {
        if (maxDepth == 1)
        {
            return new GpNode(environment.getSymbolSet().getTerminalSymbol(), 1, 1);
        }

        GpNode root = new GpNode(environment.getSymbolSet().getFunctionSymbol(), 1, 1);

        List<GpNode> leafList = new ArrayList<GpNode>();
        List<GpNode> nextLeaf = new ArrayList<GpNode>();

        leafList.add(root);

        // depth 1 to max -1
        for (int depth = 2; depth <= fullDepth; depth++)
        {
            for (int i = 0; i < leafList.size(); i++)
            {
                int childrenSize = leafList.get(i).getNodeType().getArgumentSize();
                for (int j = 0; j < childrenSize; j++)
                {
                    boolean isL = false;
                    GpNode child;
                    if (depth == fullDepth)
                    {
                        child = infill(environment, maxDepth - fullDepth + 1, new SymbolL("L", Integer.valueOf(environment.getAttribute("PPTArity"))));
                    } else
                    {
                        child = new GpNode(environment.getSymbolSet().getFunctionSymbol(), depth);
                    }
                    leafList.get(i).addChild(child);
                    if( !isL )
                    {
                        nextLeaf.add(child);
                    }
                }
            }

            leafList = nextLeaf;
            nextLeaf = new ArrayList<GpNode>();
        }

        calculateDepth(root, 1);
        return root;
    }
    /**
     * remove all symbol "L".
     * Then, remove unnecessary children.
     * @param root
     * @return
     */
    public static GpNode trimAll(GpNode root)
    {
        GpNode newRoot = root;
        while(newRoot.getNodeType().getSymbolName().equals("L"))
        {
            newRoot = newRoot.getChild(0);
        }
        GpNode newNode = (GpNode)newRoot.clone();

        trim(newNode);
        return newNode;
    }
    
    /**
     * remove all symbol "L".
     * E.g. (A (B (L x y)) C) -> (A (B x) C).
     * @param root
     * @return
     */
    public static GpNode trimL(GpNode root)
    {
        GpNode newRoot = root;
        while(newRoot.getNodeType().getSymbolName().equals("L"))
        {
            newRoot = newRoot.getChild(0);
        }
        GpNode newNode = (GpNode)newRoot.clone();

        trim(newNode);
        return newNode;
    }
    
    public static void trim(GpNode node)
    {
    	if( node.getNodeType() instanceof PPT_Symbol )
    	{
    		while( node.getChildren().size() > ((PPT_Symbol)node.getNodeType()).getOriginalSymbol().getArgumentSize() )
        	{
        		node.getChildren().remove(node.getChildren().size()-1);
        	}
    	}
    	if( node.getNodeType().getSymbolName().equals("L") )
        {
            node.getParent().setChildAt(node.getParent().getChildren().indexOf(node), node.getChild(0));
            node.getChild(0).setParent(node.getParent());
            trim(node.getChild(0));
        }
        else
        {
        	while( node.getChildren().size() > node.getNodeType().getArgumentSize() )
        	{
        		node.getChildren().remove(node.getChildren().size()-1);
        	}
        	for( int i = 0; i < node.getChildren().size(); i++ )
            {
                trim(node.getChild(i));
            }
        }
    }
    
    public static List<GpNode> rampedHalfAndHalf(GpEnvironment<?> environment)
    {
        List<GpNode> trees = new ArrayList<GpNode>();
        //double stepSize = environment.getNumberOfMaxInitialDepth() / 2;
        double stepSize = environment.getNumberOfMaxInitialDepth();
        stepSize = (int)stepSize;
        
        int index = 0;
        for (int i = 2; i <= stepSize; i++)
        {
            for (;index < i* (environment.getPopulationSize() / stepSize) - (environment.getPopulationSize() / stepSize) / 2; index++)
            {
                if( environment.getAttributes().containsKey("PPT") && Boolean.valueOf(environment.getAttribute("PPT")) )
                {
                    trees.add(grow_PPT(environment, i, environment.getNumberOfMaxDepth()));
                    //System.out.println( "GROW " + GpTreeManager.getS_Expression(trees.get(trees.size()-1)) );
                }
                else
                {
                    trees.add(grow(environment, i));
                }
            }
            for (; index < i * environment.getPopulationSize() / stepSize; index++)
            {
                if( environment.getAttributes().containsKey("PPT") && Boolean.valueOf(environment.getAttribute("PPT")) )
                {
                    trees.add(full_PPT(environment, i, environment.getNumberOfMaxDepth()));
                    //System.out.println( "FULL " + GpTreeManager.getS_Expression(trees.get(trees.size()-1)) );
                }
                else
                {
                    trees.add(full(environment, i));
                }
            }
        }

        return trees;
    }

    public static int calculateDepth(GpNode node, int depth)
    {
        node.setDepth(depth);
        int depthFromHere = 1;
        if (node.getChildren().size() == 0)
        {
            node.setDepthFromHere(1);
            return 1;
        }

        for (GpNode childNode : node.getChildren())
        {
            int depthFromChild = calculateDepth(childNode, depth + 1);
            if (depthFromHere < depthFromChild)
            {
                depthFromHere = depthFromChild;
            }
        }
        node.setDepthFromHere(depthFromHere + 1);
        return depthFromHere + 1;
    }

    /**
     * crossover operation. it returns two children as results of subtree exchanging.
     * This is the implementation of uniform crossover.
     * @param environment
     * @return two children
     */
    public static GpNode[] crossover(GpNode treeA, GpNode treeB, GpEnvironment<? extends GpIndividual> environment)
    {
        treeA = (GpNode) treeA.clone();
        treeB = (GpNode) treeB.clone();
        //treeA = (GpNode) treeA.shallowClone();
        //treeB = (GpNode) treeB.shallowClone();
        List<GpNode> allNodesA = breadthFirstSearch(treeA);
        List<GpNode> allNodesB = breadthFirstSearch(treeB);

        // selects
        GpNode randomNodeA = allNodesA.get((int) (RandomManager.getRandom() * (allNodesA.size())));
        GpNode randomNodeB = allNodesB.get((int) (RandomManager.getRandom() * (allNodesB.size())));
        while (randomNodeB.getDepthFromHere() + randomNodeA.getDepth() - 1 > environment.getNumberOfMaxDepth()
                || randomNodeA.getDepthFromHere() + randomNodeB.getDepth() - 1 > environment.getNumberOfMaxDepth())
        {
            randomNodeB = allNodesB.get((int) (RandomManager.getRandom() * allNodesB.size()));
        }

        return crossover(treeA, treeB, randomNodeA, randomNodeB, environment);
    }
    
    /**
     * crossover operation. it returns two children as results of subtree exchanging.
     * 
     * @param environment
     * @return two children
     */
    public static GpNode[] crossover90_10(GpNode treeA, GpNode treeB, GpEnvironment<? extends GpIndividual> environment)
    {
        treeA = (GpNode) treeA.clone();
        treeB = (GpNode) treeB.clone();
        int terminalSizeA = getTerminalNodeSize(treeA);
        int terminalSizeB = getTerminalNodeSize(treeB);
        int internalSizeA = getNodeSize(treeA) - terminalSizeA;
        int internalSizeB = getNodeSize(treeB) - terminalSizeB;
        
        GpNode randomNodeA = null;
        GpNode randomNodeB = null;
        
        if( RandomManager.getRandom() > 0.1 ) // function
        {
            int randIndex = (int)(internalSizeA * RandomManager.getRandom());
            randomNodeA = getNonterminalNodeAt(treeA, randIndex);
            if( randomNodeA == null )
            {
                randomNodeA = treeA;
            }
        }
        else
        {
            int randIndex = (int)(terminalSizeA * RandomManager.getRandom());
            randomNodeA = getTerminalNodeAt(treeA, randIndex);
        }
        
        if( RandomManager.getRandom() > 0.1 ) // function
        {
            int randIndex = (int)(internalSizeB * RandomManager.getRandom());
            randomNodeB = getNonterminalNodeAt(treeB, randIndex);
            if( randomNodeB == null )
            {
                randomNodeB = treeB;
            }
        }
        else
        {
            int randIndex = (int)(terminalSizeB * RandomManager.getRandom());
            randomNodeB = getTerminalNodeAt(treeB, randIndex);
        }
        
        while (randomNodeB.getDepthFromHere() + randomNodeA.getDepth() - 1 > environment.getNumberOfMaxDepth()
                || randomNodeA.getDepthFromHere() + randomNodeB.getDepth() - 1 > environment.getNumberOfMaxDepth())
        {
            if( RandomManager.getRandom() > 0.1 ) // function
            {
                int randIndex = (int)(internalSizeA * RandomManager.getRandom());
                randomNodeA = getNonterminalNodeAt(treeA, randIndex);
            }
            else
            {
                int randIndex = (int)(terminalSizeA * RandomManager.getRandom());
                randomNodeA = getTerminalNodeAt(treeA, randIndex);
            }
            
            if( RandomManager.getRandom() > 0.1 ) // function
            {
                int randIndex = (int)(internalSizeB * RandomManager.getRandom());
                randomNodeB = getNonterminalNodeAt(treeB, randIndex);
            }
            else
            {
                int randIndex = (int)(terminalSizeB * RandomManager.getRandom());
                randomNodeB = getTerminalNodeAt(treeB, randIndex);
            }
        }

        return crossover(treeA, treeB, randomNodeA, randomNodeB, environment);
    }
    
    /**
     * crossover operation. it returns two children as results of subtree exchanging.
     * Depth Dependent crossover operator from Ito and Iba.
     * @param environment
     * @return two children
     */
    public static GpNode[] crossoverDepthDependent(GpNode treeA, GpNode treeB, GpEnvironment<? extends GpIndividual> environment)
    {
        treeA = (GpNode) treeA.clone();
        treeB = (GpNode) treeB.clone();
        int depthA = selectRandomDepth(treeA);
        List<GpNode> targetNodesA = getNodeByDepth(treeA, depthA);
        GpNode crossoverNodeA = targetNodesA.get((int)(RandomManager.getRandom()*targetNodesA.size()));
        
        int depthB = selectRandomDepth(treeB);
        List<GpNode> targetNodesB = getNodeByDepth(treeB, depthB);
        GpNode crossoverNodeB = targetNodesB.get((int)(RandomManager.getRandom()*targetNodesB.size()));
        while (crossoverNodeB.getDepthFromHere() + crossoverNodeA.getDepth() - 1 > environment.getNumberOfMaxDepth()
                || crossoverNodeA.getDepthFromHere() + crossoverNodeB.getDepth() - 1 > environment.getNumberOfMaxDepth())
        {
            depthB = selectRandomDepth(treeB);
            targetNodesB = getNodeByDepth(treeB, depthB);
            crossoverNodeB = targetNodesB.get((int)(RandomManager.getRandom()*targetNodesB.size()));
        }
        
        GpNode[] children = crossover(treeA, treeB, crossoverNodeA, crossoverNodeB, environment);
        //System.out.println("afo 2");
        //System.out.println(getS_Expression(children[0]));
        //System.out.println(getS_Expression(children[1]));
        
//        if( getS_Expression(children[0]).startsWith("( C ( B ( C x x x ) ( A x ) ) ( D ( D x x x ( B ( C x ( B x x ) ( B x x ) ) ( D A ( A x ) ( C x x x ) ( D x x x x ) ) ) ) ( C x x x ) ( D x x x x ) ( C x x x ) ) ( C ( B x x ) ( A x ) ( C x x x ) ) ) ") )
//        {
//            System.out.println("find!");
//            System.out.println( depthA );
//            System.out.println( depthB );
//            //getDepth(children[0]);
//            System.exit(0);
//        }
//        if( getS_Expression(children[1]).startsWith("( C ( B ( C x x x ) ( A x ) ) ( D ( D x x x ( B ( C x ( B x x ) ( B x x ) ) ( D A ( A x ) ( C x x x ) ( D x x x x ) ) ) ) ( C x x x ) ( D x x x x ) ( C x x x ) ) ( C ( B x x ) ( A x ) ( C x x x ) ) ) ") )
//        {
//            System.out.println("find!");
//            System.out.println( depthA );
//            System.out.println( depthB );
//            //getDepth(children[1]);
//            System.exit(0);
//        }
        return children;
    }

    protected static int selectRandomDepth(GpNode tree)
    {
        calculateDepth(tree, 1);
        int maxDepth = tree.getDepthFromHere();
        double cumulativeValue = 0;
        for( int i = 0; i < maxDepth; i++ )
        {
            cumulativeValue += Math.pow(2, -i);
        }
        //System.out.println(cumulativeValue);
        // determines random depth
        int depth = 0;
        double randValue = RandomManager.getRandom() * cumulativeValue;
        cumulativeValue = 0;
        for( int i = 0; i < maxDepth; i++ )
        {
            cumulativeValue += Math.pow(2, -i);
            if( cumulativeValue >= randValue )
            {
                depth = i + 1;
                break;
            }
        }
        return depth;
    }
    
    /**
     * exchanging method
     * @param environment
     * @return two children
     */
    public static GpNode[] crossover(GpNode treeA, GpNode treeB, GpNode randomNodeA, GpNode randomNodeB, GpEnvironment<? extends GpIndividual> environment)
    {
        GpNode[] children = new GpNode[2];

        int sizeRandomNodeA = getNodeSize(randomNodeA);
        int sizeRandomNodeB = getNodeSize(randomNodeB);
        _sizeOfLastTimeCrossover.clear();
        _sizeOfLastTimeCrossover.add(sizeRandomNodeA);
        _sizeOfLastTimeCrossover.add(sizeRandomNodeB);
        _sizeOfLastTimeCrossover.add(GpTreeManager.getNodeSize(treeA) - sizeRandomNodeA);
        _sizeOfLastTimeCrossover.add(GpTreeManager.getNodeSize(treeB) - sizeRandomNodeB);

        if (randomNodeA == treeA)
        {
            treeA = randomNodeB;
        } else
        {
            randomNodeA.getParent().getChildren().set(randomNodeA.getParent().getChildren().indexOf(randomNodeA), randomNodeB);
        }
        if (randomNodeB == treeB)
        {
            treeB = randomNodeA;
        } else
        {
            randomNodeB.getParent().getChildren().set(randomNodeB.getParent().getChildren().indexOf(randomNodeB), randomNodeA);
        }
        
        // System.out.println( getS_Expression(childA) );
        // System.out.println( getS_Expression(childB) );
        calculateDepth(treeA, 1);
        calculateDepth(treeB, 1);
        children[0] = treeA;
        children[1] = treeB;

        return children;
    }

    /**
     * crossover operation. it returns two children as results of subtree exchanging.
     * 
     * @param environment
     * @return two children
     */
    public static GpNode mutation(GpNode tree, GpEnvironment<? extends GpIndividual> environment)
    {
        GpNode child = (GpNode) tree.clone();
        
        int nodeSize = getNodeSize(child);
        
        // selects
        //GpNode randomNode = allNodes.get((int) (RandomManager.getRandom() * nodeSize));
        GpNode randomNode = getNodeAt(child, (int) (RandomManager.getRandom() * nodeSize));
        int allowableDepth = environment.getNumberOfMaxDepth() - randomNode.getDepth();
        GpNode randomTree = grow(environment, Math.min(allowableDepth + 1, environment.getNumberOfMaxInitialDepth()));

        // if(
        // GpTreeManager.getS_Expression(tree).equals("( If X4 ( And X4 ( If ( Or X6 X6 ) ( Or X6 X6 ) X5 ) ) ( And ( If X2 ( If ( And ( Or ( Not ( Not ( Not ( Or ( Or X1 X4 ) X3 ) ) ) ) ( Not X5 ) ) ( Or X1 ( If X3 X6 ( Not X2 ) ) ) ) X1 X5 ) X4 ) X2 ) )")
        // )
        // {
        // System.out.println("Mutation test ");
        // System.out.println("allowable depth = " + allowableDepth);
        // System.out.println("random node " + GpTreeManager.getS_Expression(randomNode));
        // System.out.println("random tree " + GpTreeManager.getS_Expression(randomTree));
        // }

        int sizeRandomTree = GpTreeManager.getNodeSize(randomTree);
        _sizeOfLastTimeMutation.clear();
        _sizeOfLastTimeMutation.add(nodeSize - GpTreeManager.getNodeSize(randomNode));
        _sizeOfLastTimeMutation.add(sizeRandomTree);
        if (RandomManager.getRandom() > 0.5)
        {
            randomTree = new GpNode(environment.getSymbolSet().getTerminalSymbol(), randomNode.getDepth());
        }

        if (randomNode == child) // if root node is selected
        {
            child = randomTree;
        } else
        {
            randomNode.getParent().getChildren().set(randomNode.getParent().getChildren().indexOf(randomNode), randomTree);
        }

        calculateDepth(child, 1);
        return child;
    }
    
    /**
     * retrieves tree and returns result. BFS.
     * 
     * @param node
     * @return result of the retrieval.
     */
    public static Object[] breadthFirstSearchObject(GpNode node)
    {
        List<Object> result = new ArrayList<Object>();
        List<GpNode> leaves = new ArrayList<GpNode>();
        List<GpNode> nextLeaves = new ArrayList<GpNode>();

        leaves.add(node);
        result.add(node);

        while (leaves.size() != 0)
        {
            for (int i = 0; i < leaves.size(); i++)
            {
                int childrenSize = leaves.get(i).getNodeType().getArgumentSize();
                for (int j = 0; j < childrenSize; j++)
                {
                    nextLeaves.add(leaves.get(i).getChild(j));
                }
            }
            leaves = nextLeaves;
            for( int j = 0; j < nextLeaves.size(); j++ )
            {
            	result.add(nextLeaves.get(j).getExtraValue());
            }
            nextLeaves = new ArrayList<GpNode>();
        }
        return result.toArray();
    }
    
    /**
     * retrieves tree and returns result. BFS.
     * 
     * @param node
     * @return result of the retrieval.
     */
    public static List<GpNode> breadthFirstSearch(GpNode node)
    {
        List<GpNode> result = new ArrayList<GpNode>();
        List<GpNode> leaves = new ArrayList<GpNode>();
        List<GpNode> nextLeaves = new ArrayList<GpNode>();

        leaves.add(node);
        result.add(node);

        while (leaves.size() != 0)
        {
            for (int i = 0; i < leaves.size(); i++)
            {
                int childrenSize = leaves.get(i).getNodeType().getArgumentSize();
                for (int j = 0; j < childrenSize; j++)
                {
                    nextLeaves.add(leaves.get(i).getChild(j));
                }
            }
            leaves = nextLeaves;
            result.addAll(nextLeaves);
            nextLeaves = new ArrayList<GpNode>();
        }
        return result;
    }

    /**
     * retrieves tree and returns the size of tree. BFS order.
     * 
     * @param node
     * @return size of the tree
     */
    public static int getNodeSize(GpNode rootNode)
    {
        int size = 1;
        Stack<GpNode> nodeStack = new Stack<GpNode>();
        nodeStack.push(rootNode);

        while (nodeStack.size() != 0)
        {
            GpNode node = nodeStack.pop();
            for (GpNode child : node.getChildren())
            {
                size++;
                if (child.isNonterminal())
                {
                    nodeStack.push(child);
                }
            }
        }
        return size;
    }
    
    /**
     * retrieves tree and returns the size of tree. BFS order.
     * Restriction: same depth
     * @param node
     * @return size of the tree
     */
    public static int getNodeSizeWithDepth(GpNode rootNode, int depth)
    {
        if( depth == rootNode.getDepth() )
        {
            return 1;
        }
        int size = 0;
        Stack<GpNode> nodeStack = new Stack<GpNode>();
        nodeStack.push(rootNode);

        while (nodeStack.size() != 0)
        {
            GpNode node = nodeStack.pop();
            for (GpNode child : node.getChildren())
            {
                if( child.getDepth() == depth )
                {
                    size++;
                }
                if (child.isNonterminal())
                {
                    nodeStack.push(child);
                }
            }
        }
        return size;
    }
    
    /**
     * retrieves tree and returns the size of terminal nodes. BFS order.
     * 
     * @param node
     * @return size of the tree
     */
    public static int getTerminalNodeSize(GpNode rootNode)
    {
        if( rootNode.isTerminal() )
        {
            return 1;
        }
        int size = 0;
        Stack<GpNode> nodeStack = new Stack<GpNode>();
        nodeStack.push(rootNode);

        while (nodeStack.size() != 0)
        {
            GpNode node = nodeStack.pop();
            for (GpNode child : node.getChildren())
            {
                if (child.isNonterminal())
                {
                    nodeStack.push(child);
                } else
                {
                    size++;
                }
            }
        }
        return size;
    }
    
    /** 指定された深さのノードのリストを返す */
    private static List<GpNode> getNodeByDepth(GpNode root, int depth)
    {
        List<GpNode> resultList = new ArrayList<GpNode>();
        if( depth == 1 )
        {
            resultList.add(root);
            return resultList;
        }
        
        Stack<GpNode> nodeStack = new Stack<GpNode>();
        nodeStack.push(root);

        while (nodeStack.size() != 0)
        {
            GpNode node = nodeStack.pop();
            for (GpNode child : node.getChildren())
            {
                //System.out.println(child + " " + child.getDepth());
                if (child.getDepth() == depth)
                {
                    resultList.add(child);
                }
                if (child.isNonterminal())
                {
                    nodeStack.push(child);
                }
            }
        }
        return resultList;
    }
    
    /**
     * retrieves tree and returns a node at specified index.
     * 
     * @param node
     * @return node at specified index
     */
    public static GpNode getNodeAt(GpNode rootNode, int index)
    {
        int currentIndex = 0;
        if (index == currentIndex)
        {
            return rootNode;
        }
        Queue<GpNode> nodeStack = new LinkedList<GpNode>();
        nodeStack.offer(rootNode);

        while (nodeStack.size() != 0)
        {
            GpNode node = nodeStack.poll();
            for (GpNode child : node.getChildren())
            {
                currentIndex++;
                if (index == currentIndex)
                {
                    return child;
                }
                if (child.isNonterminal())
                {
                    nodeStack.offer(child);
                }
            }
        }
        return null;
    }
    
    /**
     * retrieves tree and returns a node at specified index.
     * Restriction: same depth
     * @param node
     * @return node at specified index
     */
    public static GpNode getNodeAtWithDepth(GpNode rootNode, int depth, int index)
    {
        if( rootNode.getDepth() == depth )
        {
            return rootNode;
        }
        
        int currentIndex = 0;
        if (index == currentIndex)
        {
            return rootNode;
        }
        Stack<GpNode> nodeStack = new Stack<GpNode>();
        nodeStack.push(rootNode);
        
        while (nodeStack.size() != 0)
        {
            GpNode node = nodeStack.pop();
            for (GpNode child : node.getChildren())
            {
                if( child.getDepth() == depth )
                {
                    currentIndex++;
                }
                if (index == currentIndex)
                {
                    return child;
                }
                if (child.isNonterminal())
                {
                    nodeStack.push(child);
                }
            }
        }
        return null;
    }
    
    /**
     * retrieves tree and returns a nonterminal node at specified index.
     * 
     * @param node
     * @return node at specified index
     */
    public static GpNode getNonterminalNodeAt(GpNode rootNode, int index)
    {
        int currentIndex = 0;
        Stack<GpNode> nodeStack = new Stack<GpNode>();
        nodeStack.push(rootNode);

        while (nodeStack.size() != 0)
        {
            GpNode node = nodeStack.pop();
            if( node.isNonterminal() )
            {
                if( index == currentIndex )
                {
                    return node;
                }
                currentIndex ++;
            }
            
            for (GpNode child : node.getChildren())
            {
                if (child.isNonterminal())
                {
                    nodeStack.push(child);
                }
            }
        }
        return null;
    }
    
    /**
     * retrieves tree and returns a terminal node at specified index.
     * 
     * @param node
     * @return node at specified index
     */
    public static GpNode getTerminalNodeAt(GpNode rootNode, int index)
    {
        if( rootNode.isTerminal() && index == 0 )
        {
            return rootNode;
        }
        
        int currentIndex = 0;
        Stack<GpNode> nodeStack = new Stack<GpNode>();
        nodeStack.push(rootNode);

        while (nodeStack.size() != 0)
        {
            GpNode node = nodeStack.pop();
            for (GpNode child : node.getChildren())
            {
                if( child.isTerminal() )
                {
                    if (index == currentIndex)
                    {
                        return child;
                    }
                    currentIndex++;
                }
                if (child.isNonterminal())
                {
                    nodeStack.push(child);
                }
            }
        }
        return null;
    }

    public static GpNode copyTree(GpNode root)
    {
        List<GpNode> leafList = new ArrayList<GpNode>();
        List<GpNode> nextLeaf = new ArrayList<GpNode>();

        leafList.add((GpNode) root.clone());

        while (true)
        {
            for (int i = 0; i < leafList.size(); i++)
            {
                GpNode node = leafList.get(i);
                int childrenSize = node.getNodeType().getArgumentSize();
                for (int j = 0; j < childrenSize; j++)
                {
                    GpNode child = leafList.get(i).getChild(j);

                    leafList.get(i).addChild(child);
                    nextLeaf.add(child);
                }
            }
            leafList = nextLeaf;
            nextLeaf = new ArrayList<GpNode>();
            break;
        }
        return null;
    }

    public static int getDepth(GpNode rootNode)
    {
        int depth = 1;
        List<GpNode> leafChildren = new ArrayList<GpNode>();
        leafChildren.add(rootNode);
        while (true)
        {
            // System.out.println(leafChildren);
            List<GpNode> nextLeafChildren = new ArrayList<GpNode>();
            for (GpNode node : leafChildren)
            {
                for (int i = 0; i < node.getNodeType().getArgumentSize(); i++)
                {
                    nextLeafChildren.add(node.getChild(i));
                }
            }
            if (nextLeafChildren.size() == 0)
            {
                return depth;
            }
            leafChildren = nextLeafChildren;
            depth++;
        }
    }

    public static List<Integer> getSizeOfLastTimeCrossover()
    {
        return _sizeOfLastTimeCrossover;
    }

    public static List<Integer> getSizeOfLastTimeMutation()
    {
        return _sizeOfLastTimeMutation;
    }
    
    /** X[P] -> X.
     * @param str
     * @return
     */
    private static String trimParameter(String str)
    {
    	if( str.indexOf("[") != -1 )
    	{
    		return str.substring(0, str.indexOf("["));
    	}
    	return str;
    }
    
    /**
     * returns tree string for S expression form.
     * 
     * @param node
     * @return
     */
    public static GpNode constructGpNodeFromString(String str, GpSymbolSet set)
    {
        TreeModel model = S_ExpressionHandler.getTreeModelByS_Expression(str);

        DefaultMutableTreeNode rootSymbolNode = (DefaultMutableTreeNode) model.getRoot();
        List<DefaultMutableTreeNode> currentLine = new ArrayList<DefaultMutableTreeNode>();
        currentLine.add(rootSymbolNode);
        
        Stack<DefaultMutableTreeNode> stack = new Stack<DefaultMutableTreeNode>();
        Stack<GpNode> gpNodeStack = new Stack<GpNode>();
        GpNode rootGpNode = new GpNode(set.getSymbolByName(trimParameter( (String) rootSymbolNode.getUserObject()) ), 1);
        String extraValue = set.getExtraValueByName((String) rootSymbolNode.getUserObject());
        if( extraValue != null )
        {
            rootGpNode.setExtraValue( Double.valueOf(extraValue) );
        }
        for (int i = 0; i < rootSymbolNode.getChildCount(); i++)
        {
            stack.add((DefaultMutableTreeNode) rootSymbolNode.getChildAt(i));
            GpNode node = new GpNode(set.getSymbolByName((String) ((DefaultMutableTreeNode) rootSymbolNode.getChildAt(i)).getUserObject()), 1);
            extraValue = set.getExtraValueByName( (String)((DefaultMutableTreeNode) rootSymbolNode.getChildAt(i)).getUserObject() );
            if( extraValue != null )
            {
                node.setExtraValue( Double.valueOf(extraValue) );
            }
            rootGpNode.addChild(node);
            gpNodeStack.add(rootGpNode.getChild(i));
        }
        // gpNodeStack.addAll(rootGpNode.getChildren());

        while (!stack.isEmpty())
        {
            DefaultMutableTreeNode symbolNode = stack.pop();
            GpNode gpNode = gpNodeStack.pop();
            for (int i = 0; i < symbolNode.getChildCount(); i++)
            {
                DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) symbolNode.getChildAt(i);
                stack.add(childNode);
                //System.out.println( childNode.getUserObject() );
                GpNode node = new GpNode(set.getSymbolByName((String) childNode.getUserObject()), 1);
                extraValue = set.getExtraValueByName( (String)((DefaultMutableTreeNode) childNode).getUserObject() );
                if( extraValue != null )
                {
                    node.setExtraValue( Double.valueOf(extraValue) );
                }
                gpNode.addChild(node);
                gpNodeStack.add(node);
            }
        }

        return rootGpNode;
    }
    
    
    
    /**
     * returns tree string for S expression form. It returns "" if specified node is null.
     * 
     * @param node
     * @return
     */
    public static String getS_Expression(GpNode node)
    {
        if( node == null )
        {
            return "";
        }
        
        if (node.isTerminal())
        {
            return node.toString();
        }

        StringBuilder str = new StringBuilder("( ");
        str.append(node.getNodeType().getSymbolName());
        //str.append("[").append(node.getDepth()).append("]");
        str.append(" ");

        for (GpNode child : node.getChildren())
        {
            str.append(getS_Expression(child));
            str.append(" ");
        }
        str.append(")");
        return str.toString();
    }
    
    public static void testGrow_PPT()
    {
        GpSymbolSet set = new GpSymbolSet();
        set.addSymbol(new DefaultSymbolType("A", 3));
        set.addSymbol(new DefaultSymbolType("B", 3));
        set.addSymbol(new DefaultSymbolType("E", 0));
        set.addSymbol(new DefaultSymbolType("F", 0));
        
        GpEnvironment<GpIndividual> env = new GpEnvironment<GpIndividual>();
        env.putAttribute("PPTArity", "3");
        env.setSymbolSet(set);
        env.setNumberOfMaxDepth(4);
        env.setNumberOfMaxInitialDepth(4);
        
        GpNode root = grow_PPT(env, 3, 4);
        System.out.println(getS_Expression(root));
        System.out.println(getS_Expression(trimL(root)));
        
        
        //root = full_PPT(env, 2, 4);
        //root = infill(env, 3, new SymbolL("L", 3));
        //System.out.println(getS_Expression(root));
    }
    
    public static void main(String args[])
    {
        testGrow_PPT();
        System.exit(0);
        
        GpSymbolSet set = new GpSymbolSet();
        set.addSymbol(new DefaultSymbolType("A", 2));
        set.addSymbol(new DefaultSymbolType("B", 2));
        set.addSymbol(new DefaultSymbolType("C", 3));
        set.addSymbol(new DefaultSymbolType("D", 2));
        set.addSymbol(new DefaultSymbolType("E", 0));
        set.addSymbol(new DefaultSymbolType("F", 0));
        set.addSymbol(new DefaultSymbolType("G", 1));
        set.addSymbol(new DefaultSymbolType("H", 0));
        set.addSymbol(new DefaultSymbolType("L", 3));
        
        //GpNode rootNode = constructGpNodeFromString("(B (B (C E F F) (D F E)) (G H))", set);
        GpNode rootNode = constructGpNodeFromString("(B (L (L E F F) (D F E) F) (G H))", set);
        System.out.println( getS_Expression(rootNode) );
        System.out.println( getS_Expression(trimL(rootNode)) );
        System.exit(0);
        System.out.println("size = " + getTerminalNodeSize(rootNode));
        for (int i = 0; i < getTerminalNodeSize(rootNode); i++)
        {
            System.out.println(i + ", size = " + getTerminalNodeAt(rootNode, i));
        }
        System.out.println(GpTreeManager.getS_Expression(rootNode));
        //System.exit(0);
        
        GpNode root = new GpNode(new DefaultSymbolType("A", 5), 1);
        GpNode node1 = new GpNode(new DefaultSymbolType("B", 2), 1);
        GpNode node2 = new GpNode(new DefaultSymbolType("C1", 0), 1);
        GpNode node3 = new GpNode(new DefaultSymbolType("C2", 0), 1);
        GpNode node4 = new GpNode(new DefaultSymbolType("C3", 0), 1);
        GpNode node5 = new GpNode(new DefaultSymbolType("B", 1), 1);
        root.addChild(node1);
        root.addChild(node2);
        root.addChild(node3);
        root.addChild(node4);
        root.addChild(node5);
        GpNode node11 = new GpNode(new DefaultSymbolType("D1", 0), 1);
        GpNode node12 = new GpNode(new DefaultSymbolType("D2", 0), 1);
        GpNode node21 = new GpNode(new DefaultSymbolType("D3", 0), 1);
        // GpNode node22 = new GpNode(new DefaultSymbolType("D4", 0), 1);
        node1.addChild(node11);
        node1.addChild(node12);
        node5.addChild(node21);
        // node5.addChild(node22);

        System.out.println(breadthFirstSearch(root));

        System.out.println(getNodeSize(root));
        System.out.println(getTerminalNodeSize(root));
        System.out.println(getTerminalNodeAt(root, (int) (RandomManager.getRandom() * getTerminalNodeSize(root))));
        
        // crossover test
        GpEnvironment<GpIndividual> env = new GpEnvironment<GpIndividual>();
        System.out.println("crossover 90/10");
        System.out.println( "Parent A = " + getS_Expression(rootNode) );
        System.out.println( "Parent B = " + getS_Expression(root) );
        
        //crossoverDepthDependent(root, rootNode, env);
        GpNode[] children = crossoverDepthDependent(rootNode, root, env);
        children = crossover90_10(rootNode, root, env);
        System.out.println( getS_Expression(children[0]) );
        System.out.println( getS_Expression(children[1]) );
        
        System.out.println( "test " + getTerminalNodeAt(rootNode, 6) );
        
        System.out.println( getTerminalNodeSize( constructGpNodeFromString("(A H H)", set)));
    }
}
