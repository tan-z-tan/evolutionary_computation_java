package application.wallFollowing;
import geometry.LineShape;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.util.List;

import javax.swing.JPanel;

public class FieldPanel extends JPanel
{
	/** FieldPanel draw all _objects in paintComponent method */
	private List<Shape> _objects;
	/** Robots */
	private List<Robot> _robots;
	static final long serialVersionUID = 1;
	
	/** Creates new FieldPanel object.*/
	public FieldPanel()
	{
		this(null);
	}
	/** Creates new FieldPanel object and sets _objects */
	public FieldPanel( List<Shape> objects )
	{
		super(true);
		setObjects( objects );
	}
	
	/** paintComponent override from super class */
	@Override
	public void paintComponent(Graphics graphics)
	{
		Graphics2D g = (Graphics2D)graphics;
		g.setBackground(Color.WHITE);
		g.clearRect(0, 0, getWidth(), getHeight());
		g.scale(1.5, 1.5);
		
		g.setColor( Color.BLACK );
		
		for(Shape obj: _objects)
		{
			g.draw( obj );
		}
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Color robotColor = new Color(0, 100, 150, 100);
		Color movedBlockColor = new Color(100, 100, 100, 100);
		g.setStroke(new BasicStroke(0.5f));
		
		if( _robots != null )
		{
			for(int i = 0; i < _robots.size(); i++)
			{
				Robot robot = _robots.get(i);
				g.setColor( robotColor );
				g.fill( robot.getShape() );
				g.setColor( Color.DARK_GRAY);
				g.draw( robot.getShape() );
				if( true )
				{
					//g.draw( robot.getTrajectory() );
					//g.setColor(movedBlockColor);
					//List<Shape> movedPanel = robot.getMovedPanel();
					//for( Shape panel: movedPanel )
					//{
						//g.fill(panel);
					//}
				}
			}
		}
	}
	
	//--- getter setter ---
	/** Returns map objects Vector<RectangularShape> */
	public List<Shape> getObjects()
	{
		return _objects;
	}
	/** Setter method of Vector<RectangularShape> map object Vector */
	public void setObjects(List<Shape> objects)
	{
		_objects = objects;
	}
	public List<Robot> getRobots()
	{
		return _robots;
	}
	
	public void setRobots(List<Robot> robots)
	{
		_robots = robots;
	}
}