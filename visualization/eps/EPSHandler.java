package visualization.eps;

import java.awt.Font;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.swing.JComponent;

import org.apache.xmlgraphics.java2d.GraphicContext;
import org.apache.xmlgraphics.java2d.ps.EPSDocumentGraphics2D;

public class EPSHandler
{
    private static int margin = 50;
    
	public static void printEPS(File outputFile, JComponent component)
	{
		//Set up the document size
		try {
		    EPSDocumentGraphics2D g2 = new EPSDocumentGraphics2D(false);
	        
	        AffineTransform transform = AffineTransform.getScaleInstance(1, 1);
	        transform.concatenate(AffineTransform.getTranslateInstance(margin/2, margin/2));
	        
	        //context.setFont();
	          
		    OutputStream out = new FileOutputStream(outputFile);
		    GraphicContext context = new org.apache.xmlgraphics.java2d.GraphicContext(transform);
		    
            //System.out.println(font);
	        g2.setGraphicContext(context);
	        g2.setClip(0, 0, (int)component.getPreferredSize().getWidth() + margin, (int)component.getPreferredSize().getHeight() + margin);
	        g2.setupDocument(out, (int)component.getPreferredSize().getWidth() + margin, (int)component.getHeight() + margin);
	        //g2.setFont(font);
	        component.printAll(g2);
			g2.finish(); //Wrap up and finalize the EPS file
		}
		catch(Exception e){ e.printStackTrace(); }
	}
}
