package geometry;

import java.awt.geom.Point2D;
import java.io.Serializable;

public class Vector3D implements Cloneable, Serializable
{
  private static final long serialVersionUID = -1472193979923013054L;
  private double _x;
  private double _y;
  private double _z;

  /** Creates new Vector3D (0, 0, 0) */
  public Vector3D()
  {
    this(0, 0, 0);
  }

  /** Creates new Vector3D (point.x, point.y, 0) from Point2D.Double */
  public Vector3D(Point2D.Double point)
  {
    this(point.getX(), point.getY(), 0);
  }

  /** Creates new Vector3D (x, y, z) */
  public Vector3D(double x, double y, double z)
  {
    _x = x;
    _y = y;
    _z = z;
  }

  /** returns result of this + vector */
  public Vector3D plus(Vector3D vector)
  {
    return new Vector3D(_x + vector.getX(), _y + vector.getY(), _z + vector.getZ());
  }

  /** returns result of this - vector */
  public Vector3D minus(Vector3D vector)
  {
    return new Vector3D(_x - vector.getX(), _y - vector.getY(), _z - vector.getZ());
  }

  /** returns results of (c * this) */
  public Vector3D multiple(double c)
  {
    return new Vector3D(c * _x, c * _y, c * _z);
  }

  /** returns results of (this / c) */
  public Vector3D divide(double c)
  {
    return new Vector3D(_x / c, _y / c, _z / c);
  }

  /** returns inverse of this */
  public Vector3D reverse()
  {
    return new Vector3D(-_x, -_y, -_z);
  }

  /** returns result of schalar product of this and vector */
  public double schalarProduct(Vector3D vector)
  {
    return _x * vector.getX() + _y * vector.getY() + _z * vector.getZ();
  }

  /** returns result of vector product of this and vector */
  public Vector3D vectorProduct(Vector3D vector)
  {
    return new Vector3D(_y * vector.getZ() - _z * vector.getY(), _z * vector.getX() - _x * vector.getZ(), _x
        * vector.getY() - _y * vector.getX());
  }

  /** returns absolute value |vector| in double */
  public double norm()
  {
    return (Math.sqrt(_x * _x + _y * _y + _z * _z));
  }

  // --- getter and setter ---
  public double getX()
  {
    return _x;
  }

  public void setX(double x)
  {
    _x = x;
  }

  public double getY()
  {
    return _y;
  }

  public void setY(double y)
  {
    _y = y;
  }

  public double getZ()
  {
    return _z;
  }

  public void setZ(double z)
  {
    _z = z;
  }

  // toString method
  public String toString()
  {
    return "(" + String.valueOf(_x) + ", " + String.valueOf(_y) + ", " + String.valueOf(_z) + ")";
  }

  public Point2D getPoint2D()
  {
    return new Point2D.Double(_x, _y);
  }

  /** returns Euclidean distance from original point (0, 0, 0) */
  public double distance()
  {
    return Math.sqrt(_x * _x + _y * _y + _z * _z);
  }

  /** returns Euclidean distance from specified point */
  public double distance(Vector3D point)
  {
    return Math.sqrt((_x - point.getX()) * (_x - point.getX()) + (_y - point.getY()) * (_y - point.getY())
        + (_z - point.getZ()) * (_z - point.getZ()));

  }

  /**
   * Rotates Vector3D from original point
   * @param theta
   *          rotate angle (Radian)
   * @return
   */
  public Vector3D rotate(double theta)
  {
    Vector3D resultPoint = new Vector3D();
    double x = resultPoint.getX();
    double y = resultPoint.getY();
    resultPoint.setCoordinates(_x * Math.cos(theta) - _y * Math.sin(theta),
        _x * Math.sin(theta) + _y * Math.cos(theta), 0);
    return resultPoint;
  }

  /**
   * sets three coordinates (x, y, z)
   * 
   * @param x
   * @param y
   * @param z
   */
  private void setCoordinates(double x, double y, double z)
  {
    _x = x;
    _y = y;
    _z = z;
  }

  public Vector3D clone()
  {
    return new Vector3D(_x, _y, _z);
  }
}
