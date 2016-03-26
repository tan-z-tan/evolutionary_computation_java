package application.wallFollowing;

import geneticProgramming.GpNode;
import geneticProgramming.GpSymbolSet;
import geneticProgramming.GpTreeManager;
import geometry.Vector3D;

import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;

import application.wallFollowing.symbols.Symbol_DOUBLE;
import application.wallFollowing.symbols.Symbol_HALF;
import application.wallFollowing.symbols.Symbol_IF_LT;
import application.wallFollowing.symbols.Symbol_INVERSE;
import application.wallFollowing.symbols.Symbol_PLUS;
import application.wallFollowing.symbols.Symbol_ROTATE45;
import application.wallFollowing.symbols.Symbol_ROTATE45INV;
import application.wallFollowing.symbols.Symbol_S1;
import application.wallFollowing.symbols.Symbol_S2;
import application.wallFollowing.symbols.Symbol_S3;
import application.wallFollowing.symbols.Symbol_S4;
import application.wallFollowing.symbols.Symbol_S5;
import application.wallFollowing.symbols.Symbol_S6;

public class TreeEvaluationExample
{
  /** BLOCK depicts map block */
  private static final int BLOCK = 1;
  /** CHECK_BLOCK depicts neighbor block */
  private static final int CHECK_BLOCK = 2;
  /** BLANCK depicts blank space in map */
  private static final int SPACE = 0;
  
  public static void main(String args[])
  {
    // sets GP symbol
    GpSymbolSet symbolSet = new GpSymbolSet();
    
    // Robots
    symbolSet.addSymbol(new Symbol_S1());
    symbolSet.addSymbol(new Symbol_S2());
    symbolSet.addSymbol(new Symbol_S3());
    symbolSet.addSymbol(new Symbol_S4());
    symbolSet.addSymbol(new Symbol_S5());
    symbolSet.addSymbol(new Symbol_S6());
    symbolSet.addSymbol(new Symbol_PLUS());
    symbolSet.addSymbol(new Symbol_DOUBLE());
    symbolSet.addSymbol(new Symbol_INVERSE());
    symbolSet.addSymbol(new Symbol_HALF());
    symbolSet.addSymbol(new Symbol_ROTATE45());
    symbolSet.addSymbol(new Symbol_ROTATE45INV());
    symbolSet.addSymbol(new Symbol_IF_LT());
    
    GpNode rootNode = GpTreeManager.constructGpNodeFromString("(PLUS S1 S2)", symbolSet);
    System.out.println( GpTreeManager.getS_Expression(rootNode) );
    
    Robot robot = new Robot(new Vector3D(240, 500, 0), createField());
    robot.setGene(rootNode);
    
    
    Object result = robot.evaluate();
    System.out.println( result );
  }
  
  /** Creates a field object */
  private static FieldPanel createField()
  {
    FieldPanel field = new FieldPanel(createMap(10, 15, 32));
    return field;
  }
  
  /** Creates map block objects in the experiment field */
  public static List<Shape> createMap(int mapBlockWidth, int mapBlockHeight, int blockSize)
  {
    List<Shape> _mapObjects = new ArrayList<Shape>();
    int[][] _map = new int[mapBlockHeight][mapBlockWidth];
    for (int i = 0; i < mapBlockHeight; i++)
    {
      for (int j = 0; j < mapBlockWidth; j++)
      {
        if (i == 0 || i == mapBlockHeight - 1 || j == 0 || j == mapBlockWidth - 1)
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

    for (int i = 0; i < mapBlockHeight; i++)
    {
      for (int j = 0; j < mapBlockWidth; j++)
      {
        if ((_map[i][j] & BLOCK) == BLOCK)
        {
          _mapObjects.add(new Rectangle(i * blockSize, j * blockSize, blockSize, blockSize));
          if (i != 0 && j != 0)
            _map[i - 1][j - 1] |= CHECK_BLOCK;
          if (i != 0)
            _map[i - 1][j] |= CHECK_BLOCK;
          if (i != 0 && j != mapBlockWidth - 1)
            _map[i - 1][j + 1] |= CHECK_BLOCK;
          if (j != mapBlockWidth - 1)
            _map[i][j + 1] |= CHECK_BLOCK;
          if (i != mapBlockHeight - 1 && j != mapBlockWidth - 1)
            _map[i + 1][j + 1] |= CHECK_BLOCK;
          if (i != mapBlockHeight - 1)
            _map[i + 1][j] |= CHECK_BLOCK;
          if (i != mapBlockHeight - 1 && j != 0)
            _map[i + 1][j - 1] |= CHECK_BLOCK;
          if (j != 0)
            _map[i][j - 1] |= CHECK_BLOCK;
        }
      }
    }
    
    return _mapObjects;
  }
}
