package application.wallFollowing;

import geneticProgramming.GPEvolutionModel;
import geneticProgramming.GpEnvironment;
import geneticProgramming.GpTreeManager;
import geometry.Vector3D;

import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

public class WallFollowingEvolutionModel2 extends GPEvolutionModel<Robot, GpEnvironment<Robot>>
{
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

	public WallFollowingEvolutionModel2(GpEnvironment<Robot> environment)
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
		for (int i = 0; i < 1000; i++)
		{
			try{
				//Thread.sleep(5);
			}catch(Exception e){e.printStackTrace();}
			
			for (Robot individual : _environment.getPopulation())
			{
				Vector3D direction = (Vector3D) individual.evaluate();
				Vector3D destination = individual.getPosition().plus(direction);

				int positionX = (int) destination.divide(_blockSize).getY();
				int positionY = (int) destination.divide(_blockSize).getX();
				
				int currentX = (int)individual.getPosition().divide(_blockSize).getY();
				int currentY = (int)individual.getPosition().divide(_blockSize).getX();
				
				if( _isIndividualPrint )
				{
					System.out.println( GpTreeManager.getS_Expression( GpTreeManager.trimL(individual.getRootNode()) ) );
				}
				
				// collision
				if ((_map[positionY][positionX] & BLOCK) == BLOCK && direction.distance() != 0)
				{
//					List<Shape> objects = _field.getObjects();
//					LineShape moveLine = new LineShape(individual.getPosition(), direction, LineShape.SEGMENT);
//					//System.out.println(moveLine);
//					Vector3D collisionPoint = new Vector3D(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
//					
//					for (Shape shape : objects)
//					{
//						if (shape instanceof Rectangle)
//						{
//							Vector<Vector3D> intersectPoints = moveLine.getIntersect((Rectangle) shape);
//							for (int j = 0; j < intersectPoints.size(); j++)
//							{
//								if ( collisionPoint.distance(individual.getPosition()) > intersectPoints.get(j).distance(individual.getPosition()) )
//								{
//									collisionPoint = intersectPoints.get(j);
//								}
//							}
//						}
//					}
//					direction = collisionPoint.minus(individual.getPosition());
//					destination = collisionPoint;
				}
				else if( direction.distance() == 0 )
				{
					// nothing
				} else
				{
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
	}

	public void oneEvaluation(Robot individual)
	{
		Vector3D direction = (Vector3D) individual.evaluate();
		Vector3D destination = individual.getPosition().plus(direction);

		int positionX = (int) destination.divide(_blockSize).getY();
		int positionY = (int) destination.divide(_blockSize).getX();
		
		int currentX = (int)individual.getPosition().divide(_blockSize).getY();
		int currentY = (int)individual.getPosition().divide(_blockSize).getX();
		
		// collision
		if ((_map[positionY][positionX] & BLOCK) == BLOCK && direction.distance() != 0)
		{
//			List<Shape> objects = _field.getObjects();
//			LineShape moveLine = new LineShape(individual.getPosition(), direction, LineShape.SEGMENT);
//			//System.out.println(moveLine);
//			Vector3D collisionPoint = new Vector3D(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
//			
//			for (Shape shape : objects)
//			{
//				if (shape instanceof Rectangle)
//				{
//					Vector<Vector3D> intersectPoints = moveLine.getIntersect((Rectangle) shape);
//					for (int j = 0; j < intersectPoints.size(); j++)
//					{
//						if ( collisionPoint.distance(individual.getPosition()) > intersectPoints.get(j).distance(individual.getPosition()) )
//						{
//							collisionPoint = intersectPoints.get(j);
//						}
//					}
//				}
//			}
//			direction = collisionPoint.minus(individual.getPosition());
//			destination = collisionPoint;
		}
		else if( direction.distance() == 0 )
		{
			// nothing
		} else
		{
			individual.move(direction);
			individual.renewalSensors();
			if ((_map[positionY][positionX] & CHECK_BLOCK) == CHECK_BLOCK
					&& individual.getCheckMap()[positionY][positionX] == 0)
			{
				individual.increseFirnessValue();
				//individual.getMovedPanel().add(new Rectangle2D.Double(positionY * _blockSize, positionX * _blockSize, _blockSize, _blockSize));
				if (individual.getFitnessValue() == _maximumScore)
				{
					System.out.println("success!");
				}
				individual.getCheckMap()[positionY][positionX] = 1;
			}
		}
	}
	
	@Override
	public void initialize()
	{
		super.initialize();
		_field.setRobots(_environment.getPopulation());
	}

	@Override
	public void updateGeneration()
	{
		super.updateGeneration();
		_field.setRobots(_environment.getPopulation());
	}

	/** creates robot at random position */
	@Override
	public Robot createNewIndividual()
	{
		Robot robot;
		while (true)
		{
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
		return robot;
	}


	/** Creates map block objects in the experiment field */
	public void createMap(int mapBlockWidth, int mapBlockHeight, int blockSize)
	{
		if( _environment.getAttribute("map") != null && _environment.getAttribute("map").equals("complex") )
		{
			createMap_complex(mapBlockWidth, mapBlockHeight, blockSize);
		}
		else
		{
			createMap_simple(mapBlockWidth, mapBlockHeight, blockSize);
		}
	}
	
	/** Creates map block objects in the experiment field */
	public void createMap_simple(int mapBlockWidth, int mapBlockHeight, int blockSize)
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
		_map[7][2] = BLOCK;
		_map[7][3] = BLOCK;
		_map[8][1] = BLOCK;
		_map[8][2] = BLOCK;
		_map[9][1] = BLOCK;
		
		//_map[7][1] = BLOCK;
		// _map[6][1] = BLOCK;
		// _map[1][2] = BLOCK;
		//_map[1][3] = BLOCK;
		// _map[2][3] = BLOCK;
		// _map[3][3] = BLOCK;
		// _map[7][3] = BLOCK;
		//_map[1][4] = BLOCK;
		//_map[2][4] = BLOCK;
		// _map[3][4] = BLOCK;
		//_map[7][4] = BLOCK;
		//_map[1][5] = BLOCK;
		//_map[7][5] = BLOCK;
		//_map[6][5] = BLOCK;
//		_map[7][5] = BLOCK;
//		_map[8][5] = BLOCK;
//		_map[9][5] = BLOCK;
//		_map[10][5] = BLOCK;
//		_map[4][6] = BLOCK;
//		_map[5][6] = BLOCK;
//		_map[6][6] = BLOCK;
//		_map[7][6] = BLOCK;
//		_map[8][6] = BLOCK;
		//_map[1][7] = BLOCK;
		//_map[2][7] = BLOCK;
//		_map[4][7] = BLOCK;
//		_map[7][7] = BLOCK;
//		_map[8][7] = BLOCK;
		//_map[1][8] = BLOCK;
//		_map[8][8] = BLOCK;

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
	
	/** Creates map block objects in the experiment field */
	public void createMap_complex(int mapBlockWidth, int mapBlockHeight, int blockSize)
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
		//_map[2][4] = BLOCK;
		// _map[3][4] = BLOCK;
		_map[7][4] = BLOCK;
		//_map[1][5] = BLOCK;
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
		//_map[1][7] = BLOCK;
		//_map[2][7] = BLOCK;
		_map[4][7] = BLOCK;
		_map[7][7] = BLOCK;
		_map[8][7] = BLOCK;
		//_map[1][8] = BLOCK;
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