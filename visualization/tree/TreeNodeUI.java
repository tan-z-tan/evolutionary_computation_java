package visualization.tree;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import javax.swing.tree.DefaultMutableTreeNode;

public class TreeNodeUI
{
	private Object _treeNode;
	private TreeNodeUI _parentNodeUI;
	private ArrayList<TreeNodeUI> _childrenNodeUI;
	
	// shape elements
	private Shape _shape;
	private Arc2D _nodeArc;
	private Line2D _nodeLink;
	private String _nodeName;
	private Line2D _closedPointLine;
	
	private TreeGraph _visualTree;
	private double _x;
	private double _y;
	private boolean _closed;
	private boolean _closedPoint;
	
	public TreeNodeUI()
	{
		
	}
	
	public TreeNodeUI(TreeGraph tree, Object node)
	{
		_childrenNodeUI = new ArrayList<TreeNodeUI>();
		_visualTree = tree;
		_treeNode = node;
		if( node instanceof DefaultMutableTreeNode )
		{
			if( node instanceof DefaultMutableTreeNode )
			{
				if( ((DefaultMutableTreeNode)node).getUserObject() != null )
					_nodeName = ((DefaultMutableTreeNode)node).getUserObject().toString();
				else _nodeName = "";
			}
		}
		// constructs shape elements
		_shape = new GeneralPath();
		_nodeArc = new Arc2D.Double(0, 0, _visualTree.getNodeArcRadius() * 2, _visualTree.getNodeArcRadius() * 2, 0, 360, Arc2D.OPEN);
		_nodeArc.setFrameFromCenter( _x, _y, _x + _visualTree.getNodeArcRadius(), _y + _visualTree.getNodeArcRadius() );
		_nodeLink = null; // no link in default
		_closedPointLine = null; // opened
		
		constructShape();
		
		_closed = false;
		_closedPoint = false;
	}
	
	public void draw(Graphics2D g, Color color, Color textColor)
	{
		if( _closed ) return; // closed
		
		g.setColor( color );
		g.draw( _shape );
		g.setColor( textColor );
		g.drawString( _nodeName, (float)(_x - _visualTree.getNodeArcRadius() + _nodeArc.getWidth() * 0.1), (float)(_y + _nodeArc.getHeight() * 0.3));
		g.setColor( Color.BLACK );
	}
	
	private void constructShape()
	{
		((GeneralPath)_shape).reset();
		((GeneralPath)_shape).append( _nodeArc, false );
		if( _nodeLink != null ) ((GeneralPath)_shape).append( _nodeLink, false );
		if( _closedPointLine != null ) ((GeneralPath)_shape).append( _closedPointLine, false);
	}
	
	protected void setPosition(double x, double y)
	{
		_x = x;
		_y = y;
		_nodeArc.setFrameFromCenter( x, y, x + _visualTree.getNodeArcRadius(), y + _visualTree.getNodeArcRadius() );
		if( _nodeLink != null )
		{
			_nodeLink.setLine( x, y, _nodeLink.getX2(), _nodeLink.getY2() );
		}
		constructShape();
	}
	
	protected void setLine(double x, double y)
	{
		double distance = Math.sqrt( Math.pow((x - _x), 2) + Math.pow(y - _y,2) );
	  double angle = Math.acos( (x - _x) / distance );
	  if( (y - _y) < 0 ) angle *= -1;
	  
	  _nodeLink = new Line2D.Double(
	  		_x + _visualTree.getNodeArcRadius() * Math.cos(angle),
	      _y + _visualTree.getNodeArcRadius() * Math.sin(angle),
	      x - (_visualTree.getNodeArcRadius()) * Math.cos(angle),
	      y - (_visualTree.getNodeArcRadius()) * Math.sin(angle) );
	  constructShape();
	}
	
	protected Shape getShape()
	{
		return _shape;
	}

	protected double getCenterX()
	{
		return _x;
	}
	protected double getCenterY()
	{
		return _y;
	}

	protected Object getTreeNode()
	{
		return _treeNode;
	}
	protected void setTreeNode(Object treeNode)
	{
		_treeNode = treeNode;
	}
	
	/** returns parent TreeNodeUI */
	public TreeNodeUI getParentNodeUI()
	{
		return _parentNodeUI;
	}
	/** sets parent TreeNodeUI */
	public void setParentNodeUI(TreeNodeUI parent)
	{
		parent.addChildNodeUI(this);
		_parentNodeUI = parent;
	}
	/** returns TreeNodeUI children */
	public ArrayList<TreeNodeUI> getChildrenNodeUI()
	{
		return _childrenNodeUI;
	}
	/** returns TreeNodeUI child on specified index */
	public TreeNodeUI getChildNodeUI(int index)
	{
		return _childrenNodeUI.get(index);
	}
	/** sets TreeNodeUI children */
	public void addChildNodeUI(TreeNodeUI childNodeUI)
	{
		_childrenNodeUI.add( childNodeUI );
	}
	/** returns the number of chidlren */
	protected int getChildCount()
	{
		return _childrenNodeUI.size();
	}
	/** returns if this node is closed */
	protected boolean isClosed()
	{
		return _closed;
	}
	/** sets close boolean value. Default value is false. */
	protected void setClosed(boolean closed)
	{
		_closed = closed;
	}
	
	/** returns if this node is closedPoint */
	protected boolean isClosedPoint()
	{
		return _closedPoint;
	}
	/** sets closed point boolean value. Default value is false. */
	protected void setClosedPoint(boolean closedPoint)
	{
		if( closedPoint == true && _closedPoint == false )
		{
			_closedPointLine = new Line2D.Double(_nodeArc.getCenterX(), _nodeArc.getMaxY(), _nodeArc.getCenterX(), _nodeArc.getMaxY() + _visualTree.getNodeArcRadius() /2);
			constructShape();
		}
		else if( closedPoint == false && _closedPoint == true )
		{
			_closedPointLine = null;
			constructShape();
		}
		_closedPoint = closedPoint;
	}
}
