package geometry;

import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

/**
 *
 *	@author Makoto Tanji
 *	LineShape is line represented by as follows.
 *  Line(x, y) = vectorP + s * vectorDirection.
 */
public class LineShape
{
	/** start vectorP */
	private Vector3D _start;
	/** direction vector */
	private Vector3D _vector;
	/** for use of line segment */
	private Vector3D _end;
	/**  line type */
	private int _lineType;
	
	/** LINE_STRAIGHT is infinity length line */
	public static final int STRAIGHT = 1;
	/** LINE_HALF is half line from start Vector3D A to infinity */
	public static final int HALF = 2;
	/** LINE_SEGMENT is line from start Vector3D A to end Vector3D */
	public static final int SEGMENT = 3;
	
	/** Creates new Line3D. Itis empty line */
	public LineShape()
	{
		this( new Vector3D(), new Vector3D(), STRAIGHT );
	}
	
	/** Creates new Line3D. Point = (pointX, pointY, pointZ), direction = (vectorX-pointX, vectorY-pointY, vectorZ-pointZ) */
	public LineShape(double pointX, double pointY, double vectorX, double vectorY, int type)
	{
		this( new Vector3D(pointX, pointY, 0), new Vector3D(vectorX -pointX, vectorY -pointY, 0), type );
	}
	
	/** Creates new Line3D by specified point and vector */
	public LineShape(Vector3D point, Vector3D vector, int type)
	{
		setStart(point);
		setVector(vector);
		setEnd( point.plus(vector) );
		_lineType = type;
	}
	
	/** returns intersect point in position vector. If two lines are parallel, returns null */
	public Vector3D getIntersect(LineShape line)
	{
		double vwValue = _vector.vectorProduct( line.getVector() ).norm();
		if( vwValue == 0 ) return null;
		
		Vector3D pq = _start.minus( line.getStart() );
		double coefficient = -line.getVector().vectorProduct( pq ).norm() / vwValue;
		return _start.plus( _vector.multiple(coefficient) ).reverse();
	}
	
	/** returns intersect point in position vector. If two lines are parallel, returns null */
	public Vector<Vector3D> getIntersect(Rectangle2D rect)
	{
		Vector<Vector3D> intersectPoints = new Vector<Vector3D>();
		if( getY(rect.getMinX()) >= rect.getMinY() && getY(rect.getMinX()) <= rect.getMaxY() )
			intersectPoints.add( new Vector3D(rect.getMinX(), getY(rect.getMinX()), 0) );
		if( getY(rect.getMaxX()) >= rect.getMinY() && getY(rect.getMaxX()) <= rect.getMaxY() )
			intersectPoints.add( new Vector3D(rect.getMaxX(), getY(rect.getMaxX()), 0) );
		if( getX(rect.getMinY()) >= rect.getMinX() && getX(rect.getMinY()) <= rect.getMaxX() )
			intersectPoints.add( new Vector3D(getX(rect.getMinY()), rect.getMinY(), 0) );
		if( getX(rect.getMaxY()) >= rect.getMinX() && getX(rect.getMaxY()) <= rect.getMaxX() )
			intersectPoints.add( new Vector3D(getX(rect.getMaxY()), rect.getMaxY(), 0) );
		return intersectPoints;
	}
	
	/** Returns x value on line by specified y */
	public double getX(double y)
	{
		if( _lineType == HALF || _lineType == SEGMENT )
			if( _vector.getY() * (y - _start.getY()) < 0 )return Double.NaN;
		if( _lineType == SEGMENT )
		{
			
		}
		double ratio = _vector.getX() / _vector.getY();
		return (_start.getX() -ratio * _start.getY() + ratio * y);
	}
	
	/** Returns y value on line by specified x */
	public double getY(double x)
	{
		if( _lineType == HALF || _lineType == SEGMENT )
			if( _vector.getX() * (x - _start.getX()) < 0 )return Double.NaN;
		if( _lineType == SEGMENT ) {}
		double ratio = _vector.getY() / _vector.getX();
		return (_start.getY() -ratio * _start.getX() + ratio * x);
	}
	
	// getter and setter
	public Vector3D getStart()
	{
		return _start;
	}
	
	public void setStart(Vector3D point)
	{
		_start = point;
	}

	public Vector3D getVector()
	{
		return _vector;
	}

	public void setVector(Vector3D vector)
	{
		_vector = vector;
	}

	public Vector3D getEnd()
	{
		return _end;
	}

	public void setEnd(Vector3D endPoint)
	{
		_end = endPoint;
	}

	public int getLineType()
	{
		return _lineType;
	}

	public void setLineType(int lineType)
	{
		_lineType = lineType;
	}
	
	/** returns Vector3D representing this line */
	public Vector3D toVector()
	{
		return _end.minus(_start);
	}
	
	/** creates and returns Line2D object */
	public Shape getShape()
	{
		//System.out.println(_start.getX() + " " +_start.getY() + " " +_end.getX() + " " + _end.getY());
		return new Line2D.Double(_start.getX(), _start.getY(), _end.getX(), _end.getY());
	}
	
	public String toString()
	{
		return "line [point=" + _start.toString() + " vector=" + _vector.toString() + "]";
	}
}