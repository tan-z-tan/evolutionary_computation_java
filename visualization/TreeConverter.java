package visualization;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.tree.TreeModel;

public class TreeConverter
{
  private static File file = new File(System.getenv("HOME") + "/research/experiment/PCFG/data/expData/data.txt");
  private static String startLine = "Analysis on";

  public static void main(String args[])
  {
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    ArrayList<String> resultData = new ArrayList<String>();

    try
    {
      reader = new BufferedReader(new FileReader(file));
      while (reader.ready())
      {
        String line = reader.readLine();
        int number = 0;

        if (line.startsWith(startLine))
        {
          line = line.substring(startLine.length()).trim();
          // number = Integer.parseInt( line.substring( line.indexOf("_") + 1,
          // line.indexOf(".txt")) );
          number = Integer.parseInt(line);
        } else
        {
          continue;
        }
        line = reader.readLine();
        if (line.contains("can not derive:"))
        {
          reader.readLine();
          continue;
        }
        // read S expression
        line = reader.readLine();
        TreeModel treeModel = S_ExpressionHandler.getTreeModelByS_Expression(line);
        Object root = treeModel.getRoot();
        StringBuilder str = new StringBuilder(number);
        str.append(" ");
        for (int i = 0; i < treeModel.getChildCount(root); i++)
        {
          str.append(printLowestS_Expression(root, treeModel.getChild(root, i), treeModel));
        }
        resultData.add(number + str.toString());
      }
    } catch (Exception e)
    {
      e.printStackTrace();
    }

    String[] data = resultData.toArray(new String[0]);
    Arrays.sort(data, new Comparator<String>()
    {
      public int compare(String strA, String strB)
      {
        int numberA = Integer.parseInt(strA.substring(0, strA.indexOf(" (")).trim());
        int numberB = Integer.parseInt(strB.substring(0, strB.indexOf(" (")).trim());
        return numberA - numberB;
      }
    });
    for (String str : data)
    {
      System.out.println(str);
    }
  }

  private static StringBuilder printLowestS_Expression(Object parent, Object node, TreeModel treeModel)
  {
    StringBuilder str = new StringBuilder();

    if ((parent.toString().startsWith("S") || parent.toString().startsWith("Beat"))
        && !node.toString().startsWith("Beat"))
    {
      str.append("( ");
    }
    if (treeModel.isLeaf(node))
    {
      str.append(node.toString());
      str.append(" ");
    } else
    {
      for (int i = 0; i < treeModel.getChildCount(node); i++)
      {
        str.append(printLowestS_Expression(node, treeModel.getChild(node, i), treeModel));
      }
    }

    if ((parent.toString().startsWith("S") || parent.toString().startsWith("Beat"))
        && !node.toString().startsWith("Beat"))
    {
      str.append(") ");
    }
    return str;
  }
}
