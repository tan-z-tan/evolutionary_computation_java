package application.wallFollowing;

import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

import ports.Porte;
import ecCore.EvolutionModel;
import ecCore.selector.AbstractSelector;
import ecCore.selector.TournamentSelector;
import geneticProgramming.GpEnvironment;
import geneticProgramming.GpIndividual;
import geneticProgramming.GpNode;
import geneticProgramming.GpTreeManager;
import geometry.Vector3D;

public class WallFollowingPorteEvolutionModel extends EvolutionModel<Robot, GpEnvironment<Robot>>
{
  GpIndividual bestIndividual;
  int _maximumScore = 0;

  /** Field */
  private FieldPanel _field;
  /** Map objects in the experiment field */
  private List<Shape> _mapObjects;
  /** Data of map block objects */
  private int[][] _map;
  /** Number of vertical block of field */
  private int _mapBlockHeight;
  /** Number of horizontal block of field */
  private int _mapBlockWidth;
  /** Size of a Block */
  private int _blockSize;
  // for display in GUI
  private JLabel _bestFitness;
  private JLabel _averageFitness;
  private JLabel _bestTree;

  /** BLOCK depicts map block */
  private static final int BLOCK = 1;
  /** CHECK_BLOCK depicts neighbor block */
  private static final int CHECK_BLOCK = 2;
  /** BLANCK depicts blanc space in map */
  private static final int SPACE = 0;

  public WallFollowingPorteEvolutionModel(GpEnvironment<Robot> environment)
  {
    super(environment);
  }

  @Override
  public void evaluate()
  {
    for (Robot individual : _environment.getPopulation())
    {
      individual.renewalSensors();
    }
    for (int i = 0; i < 750; i++)
    {
      for (Robot individual : _environment.getPopulation())
      {
        // System.out.println(
        // GpTreeManager.getS_Expression(individual.getRootNode()) );
        Vector3D direction = (Vector3D) individual.evaluate();
        Vector3D destination = individual.getPosition().plus(direction);

        int positionX = (int) destination.divide(_blockSize).getY();
        int positionY = (int) destination.divide(_blockSize).getX();

        // if( positionY > _mapBlockHeight || positionX > _mapBlockWidth )
        // {
        // System.out.println("Over " + positionX + " " + positionY + " " +
        // destination + " " + _blockSize);
        // }
        if ((_map[positionY][positionX] & BLOCK) == BLOCK || direction.distance() == 0)
        {
          // nothing
        } else
        {
          // System.out.println( "Direction = " + direction + "\n Position = " +
          // destination + " position=(" + positionX + ", " + positionY+ ")");
          individual.move(direction);
          individual.renewalSensors();
          if ((_map[positionY][positionX] & CHECK_BLOCK) == CHECK_BLOCK
              && individual.getCheckMap()[positionY][positionX] == 0)
          {
            individual.increseFirnessValue();
            if (individual.getFitnessValue() == _maximumScore)
            {
              System.out.println("success!");
            }
            individual.getCheckMap()[positionY][positionX] = 1;
          }
        }
      }
      _field.repaint();
    }
    double sumOfFitness = 0;
    double sumOfDepth = 0;
    bestIndividual = _environment.getPopulation().get(0);
    for (Robot individual : _environment.getPopulation())
    {
      sumOfDepth += individual.getRootNode().getDepthFromHere();
      sumOfFitness += individual.getFitnessValue();
      if (bestIndividual.getFitnessValue() < individual.getFitnessValue())
      {
        bestIndividual = individual;
      }
    }
    _bestFitness.setText("Best Fitness: " + bestIndividual.getFitnessValue());
    _bestTree.setText("Best Tree: " + GpTreeManager.getS_Expression(bestIndividual.getRootNode()));
    _averageFitness.setText("Avarage Fitness: " + sumOfFitness / _environment.getPopulationSize());
    System.out.println("Generation " + _environment.getGenerationCount());
    System.out.println("Best Individual = " + bestIndividual.getFitnessValue());
    System.out.println(GpTreeManager.getS_Expression(bestIndividual.getRootNode()));
    System.out.println("Average Fitness = " + sumOfFitness / _environment.getPopulationSize());
    System.out.println("Average Depth   = " + sumOfDepth / _environment.getPopulationSize());
  }
  
  @Override
  public void evaluateIndividual(Robot individual)
  {
	  // TODO Auto-generated method stub
  	
  }
  
  @Override
  public void initialize()
  {
    List<Robot> robots = new ArrayList<Robot>();
    List<GpNode> nodeList = GpTreeManager.rampedHalfAndHalf(_environment);
    for (int i = 0; i < _environment.getPopulationSize(); i++)
    {
      Robot robot = createRobot();
      robot.setGene(nodeList.get(i));
      robots.add(robot);
    }
    _environment.setPopulation(robots);
    _field.setRobots(robots);
  }

  @Override
  public void updateGeneration()
  {
    // reproduction
    AbstractSelector<Robot> selector = new TournamentSelector<Robot>(_environment.getPopulation(), 5);
    // AbstractSelector<Robot> selector = new
    // TruncateSelector<Robot>(_environment.getPopulation());

    List<Robot> tmp = selector.getRandomPTypeList((int) (_environment.getPopulationSize() * 0.5));
    List<GpNode> parents = new ArrayList<GpNode>();
    List<Robot> nextPopulation = new ArrayList<Robot>(_environment.getPopulationSize());
    for (GpIndividual individual : tmp)
    {
      parents.add(individual.getRootNode());
    }

    Porte porte = new Porte(parents, (GpEnvironment<Robot>) _environment);
    long start = System.currentTimeMillis();
    for (int i = 0; i < _environment.getPopulationSize(); i++)
    {
      GpNode childNode = porte.getRandomSample();
      Robot child = createRobot();
      child.setRootNode(childNode);
      nextPopulation.add(child);
    }
    // System.out.println( "Time = " + (System.currentTimeMillis() - start) );
    _environment.setPopulation(nextPopulation);

    _field.setRobots(nextPopulation);
    _environment.setPopulation(nextPopulation);
  }

  /** makes robot random position */
  public Robot createRobot()
  {
    Robot robot;
    while (true)
    {
      // int x = (int) (Math.random() * _mapBlockWidth);
      // int y = (int) (Math.random() * _mapBlockHeight);
      int x = (int) (Math.random() * _mapBlockWidth * _blockSize);
      int y = (int) (Math.random() * _mapBlockHeight * _blockSize);
      if ((_map[y / _blockSize][x / _blockSize] & BLOCK) != 1)
      {
        robot = new Robot(new Vector3D(y, x, 0), _field);
        robot.setGene(GpTreeManager.grow(_environment));
        break;
      }
    }

    int[][] checkMap = new int[_mapBlockHeight][_mapBlockWidth];
    robot.setCheckMap(checkMap);
    // robot.renewalSensors();
    return robot;
  }

  /** Creates map block objects in the experiment field */
  public void createMap(int mapBlockWidth, int mapBlockHeight, int blockSize)
  {
    _mapBlockWidth = mapBlockWidth;
    _mapBlockHeight = mapBlockHeight;
    _blockSize = blockSize;
    _mapObjects = new ArrayList<Shape>();
    _map = new int[_mapBlockHeight][_mapBlockWidth];
    for (int i = 0; i < _mapBlockHeight; i++)
    {
      for (int j = 0; j < _mapBlockWidth; j++)
      {
        if (i == 0 || i == _mapBlockHeight - 1 || j == 0 || j == _mapBlockWidth - 1)
        {
          _map[i][j] = BLOCK;
        } else
        {
          _map[i][j] = SPACE;
        }
      }
    }

    _map[7][1] = BLOCK;
    // _map[6][1] = BLOCK;
    // _map[1][2] = BLOCK;
    _map[1][3] = BLOCK;
    // _map[2][3] = BLOCK;
    // _map[3][3] = BLOCK;
    // _map[7][3] = BLOCK;
    _map[1][4] = BLOCK;
    _map[2][4] = BLOCK;
    // _map[3][4] = BLOCK;
    _map[7][4] = BLOCK;
    _map[1][5] = BLOCK;
    _map[7][5] = BLOCK;
    _map[6][5] = BLOCK;
    _map[7][5] = BLOCK;
    _map[8][5] = BLOCK;
    _map[9][5] = BLOCK;
    _map[10][5] = BLOCK;
    _map[4][6] = BLOCK;
    _map[5][6] = BLOCK;
    _map[6][6] = BLOCK;
    _map[7][6] = BLOCK;
    _map[8][6] = BLOCK;
    _map[1][7] = BLOCK;
    _map[2][7] = BLOCK;
    _map[4][7] = BLOCK;
    _map[7][7] = BLOCK;
    _map[8][7] = BLOCK;
    _map[1][8] = BLOCK;
    _map[8][8] = BLOCK;

    _maximumScore = 0;
    for (int i = 0; i < _mapBlockHeight; i++)
    {
      for (int j = 0; j < _mapBlockWidth; j++)
      {
        if ((_map[i][j] & BLOCK) == BLOCK)
        {
          _mapObjects.add(new Rectangle(i * _blockSize, j * _blockSize, _blockSize, _blockSize));
          if (i != 0 && j != 0)
            _map[i - 1][j - 1] |= CHECK_BLOCK;
          if (i != 0)
            _map[i - 1][j] |= CHECK_BLOCK;
          if (i != 0 && j != _mapBlockWidth - 1)
            _map[i - 1][j + 1] |= CHECK_BLOCK;
          if (j != _mapBlockWidth - 1)
            _map[i][j + 1] |= CHECK_BLOCK;
          if (i != _mapBlockHeight - 1 && j != _mapBlockWidth - 1)
            _map[i + 1][j + 1] |= CHECK_BLOCK;
          if (i != _mapBlockHeight - 1)
            _map[i + 1][j] |= CHECK_BLOCK;
          if (i != _mapBlockHeight - 1 && j != 0)
            _map[i + 1][j - 1] |= CHECK_BLOCK;
          if (j != 0)
            _map[i][j - 1] |= CHECK_BLOCK;
        }
      }
    }
    for (int i = 0; i < _mapBlockHeight; i++)
    {
      for (int j = 0; j < _mapBlockWidth; j++)
      {
        if ((_map[i][j] & BLOCK) != BLOCK && (_map[i][j] & CHECK_BLOCK) == CHECK_BLOCK)
        {
          _maximumScore++;
        }
      }
    }
    System.out.println("The maximum score = " + _maximumScore);
  }

  public FieldPanel getField()
  {
    return _field;
  }

  public void setField(FieldPanel field)
  {
    _field = field;
  }

  public List<Shape> getMapObjects()
  {
    return _mapObjects;
  }

  public void setMapObjects(ArrayList<Shape> mapObjects)
  {
    _mapObjects = mapObjects;
  }

  public int[][] getMap()
  {
    return _map;
  }

  public void setMap(int[][] map)
  {
    _map = map;
  }

  public JLabel getBestFitness()
  {
    return _bestFitness;
  }

  public void setBestFitness(JLabel bestFitness)
  {
    _bestFitness = bestFitness;
  }

  public JLabel getAverageFitness()
  {
    return _averageFitness;
  }

  public void setAverageFitness(JLabel averageFitness)
  {
    _averageFitness = averageFitness;
  }

  public JLabel getBestTree()
  {
    return _bestTree;
  }

  public void setBestTree(JLabel bestTree)
  {
    _bestTree = bestTree;
  }
}
