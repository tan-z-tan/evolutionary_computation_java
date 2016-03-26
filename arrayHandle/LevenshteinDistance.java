package arrayHandle;

import java.util.Arrays;
import java.util.List;

/** This class is to find Levenshtein distance that is defined by follows.
 *   
 * @author Makoto Tanji
 */
public class LevenshteinDistance
{
  /** returns mininum value of three values */
	private static int Minimum (int a, int b, int c)
	{
		int mi;
    mi = a;
    if (b < mi) mi = b;
    if (c < mi) mi = c;
    return mi;
  }

  /**
   * returns Levenshtein distance
   * @return distance between String s and t.
   */
  public static int getLevenshteinDistance (String s, String t)
  {
  	int d[][]; // matrix
  	int n; // length of s
  	int m; // length of t
  	int i; // iterates through s
  	int j; // iterates through t
  	char s_i; // ith character of s
  	char t_j; // jth character of t
  	int cost; // cost
  	
    // Step 1
    n = s.length ();
    m = t.length ();
    if (n == 0) return m;
    if (m == 0) return n;
    d = new int[n+1][m+1];
    
    // Step 2
    for (i = 0; i <= n; i++) d[i][0] = i;
    for (j = 0; j <= m; j++) d[0][j] = j;
    
    // Step 3
    for (i = 1; i <= n; i++)
    {
      s_i = s.charAt (i - 1);

      // Step 4
      for (j = 1; j <= m; j++)
      {
        t_j = t.charAt (j - 1);

        // Step 5
        if (s_i == t_j) cost = 0;
        else cost = 1;
        
        // Step 6
        d[i][j] = Minimum (d[i-1][j]+1, d[i][j-1]+1, d[i-1][j-1] + cost);
      }
    }
    
    // Step 7
    return d[n][m];
  }
  
  public static int getDistance(int[] s, int[] t)
  {
  	int distance = 0;
  	for( int i = 0; i < s.length; i++)
  	{
  		distance += Math.abs(s[i] - t[i]); 
  	}
  	return distance;
  }
  
  /**
   * returns Levenshtein distance with int[]
   * @return distance between int[] s and t.
   */
  public static int getLevenshteinDistance (int[] s, int[] t)
  {
  	int d[][]; // matrix
  	int n; // length of s
  	int m; // length of t
  	int i; // iterates through s
  	int j; // iterates through t
  	int s_i; // ith character of s
  	int t_j; // jth character of t
  	int cost; // cost
  	
    // Step 1
  	n = s.length;
    m = t.length;
    if (n == 0) return m;
    if (m == 0) return n;
    d = new int[n+1][m+1];
    
    // Step 2
    for (i = 0; i <= n; i++) d[i][0] = i;
    for (j = 0; j <= m; j++) d[0][j] = j;
    
    // Step 3
    for (i = 1; i <= n; i++)
    {
      s_i = s[i-1];

      // Step 4
      for (j = 1; j <= m; j++)
      {
        t_j = t[j-1];

        // Step 5
        if (s_i == t_j) cost = 0;
        else cost = 1;
        
        // Step 6
        d[i][j] = Minimum (d[i-1][j]+1, d[i][j-1]+1, d[i-1][j-1] + cost);
      }
    }
    
    // Step 7
    return d[n][m];
  }
  
  /**
   * returns Levenshtein distance with int[]
   * @return distance between int[] s and t.
   */
  public static int getLevenshteinDistance (List<?> s, List<?> t)
  {
  	int d[][]; // matrix
  	int n; // length of s
  	int m; // length of t
  	int i; // iterates through s
  	int j; // iterates through t
  	Object s_i; // ith character of s
  	Object t_j; // jth character of t
  	int cost; // cost
  	
    // Step 1
  	n = s.size();
    m = t.size();
    if (n == 0) return m;
    if (m == 0) return n;
    d = new int[n+1][m+1];
    
    // Step 2
    for (i = 0; i <= n; i++) d[i][0] = i;
    for (j = 0; j <= m; j++) d[0][j] = j;
    
    // Step 3
    for (i = 1; i <= n; i++)
    {
      s_i = s.get(i-1);

      // Step 4
      for (j = 1; j <= m; j++)
      {
      	t_j = t.get(j-1);

        // Step 5
        if (s_i.equals(t_j)) cost = 0;
        else cost = 1;
        
        // Step 6
        d[i][j] = Minimum (d[i-1][j]+1, d[i][j-1]+1, d[i-1][j-1] + cost);
      }
    }
    
    // Step 7
    return d[n][m];
  }
  
  public static void main(String args[])
  {
  	System.out.println( getLevenshteinDistance( new int[]{1, 2, 3, 3}, new int[]{1, 2, 3, 1, 2, 4} ) );
  	System.out.println( getLevenshteinDistance( Arrays.asList(new Integer[]{1, 2, 3, 3}), Arrays.asList(new Integer[]{1, 2, 3, 1, 2, 4}) ) );
  }
}
