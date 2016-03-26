package application.multiplexer;

public class Test
{
  public static void main(String[] args)
  {
    for( int i = 0; i < Math.pow(2, 6); i++ )
    {
      String binaryStr = Integer.toBinaryString(i);
      StringBuilder str = new StringBuilder();
      while( str.length() != 6 - binaryStr.length() )
      {
        str.append("0");
      }
      str.append(binaryStr);
      System.out.println(str.toString());
    }
  }
}
