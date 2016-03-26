package xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class DTD_DependencyAnalysis
{
	public static void main(String args[])
	{
		File dtdDirectory = new File("/home/tanji/research/MusicXML/musicxml/");
		if( !dtdDirectory.exists() ) // if directory is not exist -> exit
		{
				System.exit(0);
		}
		
		DTD_DependencyAnalysis.printDTD( new File(dtdDirectory, "partwise.dtd") );
		DTD_DependencyAnalysis.printDTD( new File(dtdDirectory, "attributes.dtd"));
		DTD_DependencyAnalysis.printDTD( new File(dtdDirectory, "barline.dtd"));
		DTD_DependencyAnalysis.printDTD( new File(dtdDirectory, "common.dtd"));
		DTD_DependencyAnalysis.printDTD( new File(dtdDirectory, "direction.dtd"));
		DTD_DependencyAnalysis.printDTD( new File(dtdDirectory, "identity.dtd"));
		DTD_DependencyAnalysis.printDTD( new File(dtdDirectory, "layout.dtd"));
		DTD_DependencyAnalysis.printDTD( new File(dtdDirectory, "link.dtd"));
		DTD_DependencyAnalysis.printDTD( new File(dtdDirectory, "note.dtd"));
		DTD_DependencyAnalysis.printDTD( new File(dtdDirectory, "score.dtd"));
	}
	
	public static void printDTD(File dtdFile)
	{
		try
		{
			BufferedReader reader = new BufferedReader( new FileReader( dtdFile ) );
			while( reader.ready() )
			{
				String line = reader.readLine().trim();
				if( line.startsWith("<!--") )
				{
					// comment
				}
				else if( line.startsWith("<!ENTITY") )
				{
					ReadENTITY( reader, line );
				}
				else if( line.startsWith("<!ELEMENT") )
				{
					ReadELEMENT( reader, line );
				}
			}
		}
		catch( Exception e ) { e.printStackTrace(); }
	}
	
	private static void  ReadENTITY(BufferedReader reader, String line)
	{
		String newLine = line;
		System.out.println( newLine );
		
		try{
			while( !newLine.contains(">") )
			{
				newLine = reader.readLine();
				System.out.println( newLine );
			}
		} catch(Exception e) { e.printStackTrace(); }
	}
	
	private static void ReadELEMENT(BufferedReader reader, String line)
	{
		String newLine = line;
		System.out.println( newLine );
		
		try{
			while( !newLine.contains(">") )
			{
				newLine = reader.readLine();
				System.out.println( newLine );
			}
		} catch(Exception e) { e.printStackTrace(); }
	}
}
