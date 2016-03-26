package application.wallFollowing;
import geneticProgramming.GpIndividual;
import geneticProgramming.GpNode;
import geometry.LineShape;
import geometry.Vector3D;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Robot extends GpIndividual
{
	/** width of robot shape */
	public static int WIDTH = 1;
	/** height of robot shape */
	public static int HEIGHT = 1;
	/** length of robot move */
	public static final int MOVE_LENGTH = 2;
	/** Position in X-Y coodinate */
	private Vector3D _position;
	/** Direction vector, length is always 1.0 */
	private Vector3D _direction;
	/** sensor lines from position to infinite distance */
	private Vector3D[] _sensors;
	/** Field Panel to get sensor values */
	private FieldPanel _field;
	/** Shape of robot */
	private GeneralPath _shape;
	/** check map*/
	private int[][] _checkMap;
	/** For evaluation of gene tree */
	private int[] _subTreeSize;
	/** The trajectory of this robot */
	//private List<LineShape> _moveTrajectory;
	/** panels that this robot moved */
	//private List<Shape> _movedPanel;
	//private GeneralPath _trajectory;
	
	/** Creates new FollowingRobot */
	public Robot(Vector3D position, FieldPanel field)
	{
		setField(field);
		
		_shape = new GeneralPath(new Arc2D.Double(position.getX() - (WIDTH / 2), position.getY() - (HEIGHT / 2), WIDTH, 	HEIGHT, 0, 360, Arc2D.OPEN));
		double angle = Math.random() * Math.PI * 2;
		//angle = Math.PI * (-3 / 4.0);
		_direction = new Vector3D( Math.cos(angle), Math.sin(angle), 0);
		_sensors = new Vector3D[6];
		_sensors[0] = _direction.rotate( Math.PI / 2.0 );
		_sensors[1] = _direction.rotate( Math.PI / 4.0 );
		_sensors[2] = _direction.rotate( 0 );
		_sensors[3] = _direction.rotate( -Math.PI / 4.0 );
		_sensors[4] = _direction.rotate( -Math.PI / 2.0 );
		_sensors[5] = _direction.rotate( -Math.PI );
		
		setPosition(position);
//		_moveTrajectory = new ArrayList<LineShape>();
//		_movedPanel = new ArrayList<Shape>();
//		_trajectory = new GeneralPath();
//		_trajectory.moveTo(position.getX(), position.getY());
	}
	
	/** Renewal sensor values */
	public void renewalSensors()
	{
		List<Shape> objects = _field.getObjects();
		LineShape[] sensorLines = new LineShape[_sensors.length]; // 6 lines
		Vector3D[] points = new Vector3D[_sensors.length]; // 6 points
		for (int i = 0; i < _sensors.length; i++)
		{
			points[i] = new Vector3D(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
			sensorLines[i] = new LineShape(_position, _sensors[i], LineShape.HALF);
		}
		
		for (Shape shape : objects)
		{
			if (shape instanceof Rectangle)
			{
				for (int i = 0; i < _sensors.length; i++)
				{
					Vector<Vector3D> intersectPoints = sensorLines[i].getIntersect((Rectangle) shape);
					for (int j = 0; j < intersectPoints.size(); j++)
					{
						if ( points[i].distance(_position) > intersectPoints.get(j).distance(_position) )
							points[i] = intersectPoints.get(j);
					}
				}
			}
		}
		double arcRadius = 3 * Math.sqrt(_fitnessValue);
		GeneralPath shape = new GeneralPath(new Arc2D.Double(
				_position.getX() - (WIDTH / 2),
				_position.getY() - (HEIGHT / 2), WIDTH, HEIGHT, 0, 360, Arc2D.CHORD));
		
		shape.append( new Arc2D.Double(_position.getX() - (arcRadius / 2),
				_position.getY() - (arcRadius / 2),	arcRadius, arcRadius, 0, 360, Arc2D.CHORD), false );
		shape.append(new Line2D.Double(_position.plus(_sensors[5].multiple(arcRadius/5)).getPoint2D(), _position.getPoint2D()), false);
		
		_shape = shape;
		for (int i = 0; i < _sensors.length; i++)
		{
			points[i] = points[i].minus( _position );
			_sensors[i] = points[i];
			//_shape.append(new Line2D.Double(_position.getX(), _position.getY(), _position.getX() + _sensors[i].getX(),_position.getY() + _sensors[i].getY()), false);
		}
//		_trajectory = new GeneralPath();
//		if( _moveTrajectory.size() > 0 )
//		{
//			_trajectory.moveTo( _moveTrajectory.get(0).getStart().getX(), _moveTrajectory.get(0).getStart().getY() );
//		}
//		else
//		{
//			_trajectory.moveTo(_position.getX(), _position.getY());
//		}
//		for( int i = 0; i < _moveTrajectory.size(); i++ )
//		{
//			_trajectory.lineTo(_moveTrajectory.get(i).getEnd().getX(), _moveTrajectory.get(i).getEnd().getY());
//		}
	}
	
	/** returns checkMap */
	public int[][] getCheckMap()
	{
		return _checkMap;
	}
	
	/** returns checkMap */
	public void setCheckMap(int[][] checkMap)
	{
		_checkMap = checkMap;
	}
	
	/** evaluate method */
	public Object evaluate()
	{
		_direction = (Vector3D)_rootNode.evaluate(this);
		if( _direction.distance() > MOVE_LENGTH )
			_direction = _direction.divide( _direction.distance() / MOVE_LENGTH );
		return _direction;
	}
	
	// -------- getter and setter --------
	/** returns last direction */
	public Vector3D getLastDirection()
	{
		return _direction;
	}
	
	/** returns sensor at i */
	public Vector3D getSensor(int i)
	{
		return _sensors[i];
	}
	
	/** returns position Vector3D */
	public Vector3D getPosition()
	{
		return _position;
	}

	/** sets position Point2D.Double */
	public void setPosition(Vector3D position)
	{
		_position = position;
	}

	/** returns FieldPanel */
	public FieldPanel getField()
	{
		return _field;
	}

	/** returns robot shape */
	public Shape getShape()
	{
		return _shape;
	}

	/** sets robot shape */
	public void setShape(Shape shape)
	{
		_shape = (GeneralPath) shape;
	}

	/** sets FieldPanel */
	public void setField(FieldPanel field)
	{
		_field = field;
	}
	
	/** sets TreeGType*/
	public void setGene(GpNode gType)
	{
		_rootNode = gType;
	}
	
	/** increase adaptation value */
	public void increseFirnessValue()
	{
		_fitnessValue++;
	}
	
//	public GeneralPath getTrajectory()
//	{
//		return _trajectory;
//	}
//	
//	public List<Shape> getMovedPanel()
//	{
//		return _movedPanel;
//	}
	
	/** add specified Vector3D to robot position */
	public void move(Vector3D direction)
	{
//		_moveTrajectory.add(new LineShape(_position, direction, LineShape.SEGMENT));
		
		_direction = direction;
		//_sensors[0].setStart(destination);
		_sensors[0] = direction.rotate(Math.PI/2.0);
		
		//_sensors[1].setStart(destination);
		_sensors[1] = direction.rotate(Math.PI/4.0);
		
		//_sensors[2].setStart(destination);
		_sensors[2] = direction;
		
		//_sensors[3].setStart(destination);
		_sensors[3] = direction.rotate(-Math.PI/4.0);
		
		//_sensors[4].setStart(destination);
		_sensors[4] = direction.rotate(-Math.PI/2.0);
		
		//_sensors[5].setStart(destination);
		_sensors[5] = direction.rotate(-Math.PI);
		
		_position = _position.plus( direction );
	}
}
