package visualization;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

public class S_ExpressionHandler
{
  /**
   * parses S expression and returns corresponding DefaultMutableTreeModel by
   * given File
   */
  public static DefaultTreeModel getTreeModelByS_Expression(File file)
  {
    try
    {
      FileInputStream in = new FileInputStream(file);
      BufferedReader reader = new BufferedReader(new InputStreamReader(in));

      String s_expression = reader.readLine();
      while (reader.ready())
      {
        s_expression += " " + reader.readLine().trim();
      }

      if (s_expression == null)
        return null;
      DefaultTreeModel treeModel = getTree(s_expression);
      return treeModel;
    } catch (Exception e)
    {
      e.printStackTrace();
    }
    return null;
  }

  public static TreeModel getTreeModelByS_Expression(String s_expression)
  {
    if (s_expression == null)
      return null;
    TreeModel treeModel = getTree(s_expression);
    return treeModel;
  }

  public static String getS_Expression(TreeModel model)
  {
    return getS_Expression((TreeNode) model.getRoot());
  }

  private static String getS_Expression(TreeNode node)
  {
    if (node.getChildCount() == 0)
    {
      return node.toString();
    }
    StringBuilder str = new StringBuilder("( ").append(node.toString());
    for (int i = 0; i < node.getChildCount(); i++)
    {
      str.append(" ").append(getS_Expression(node.getChildAt(i)));
    }
    return str.append(" )").toString();
  }

  private static DefaultTreeModel getTree(String s_string)
  {
    DefaultMutableTreeNode root = new DefaultMutableTreeNode("ROOT");
    DefaultMutableTreeNode currentNode = root;

    // to be exist at least one blank space on front and back of parenthesis
    s_string = s_string.replace("(", " ( ");
    s_string = s_string.replace(")", " ) ");
    s_string = s_string.trim();
    String[] words = s_string.split("\\s+");
    ArrayList<String> elements = new ArrayList<String>(Arrays.asList(words));

    for (int i = 0; i < elements.size(); i++)
    {
      if (elements.get(i).startsWith("(") && elements.get(i).length() > 1)
      {
        String separateElement = elements.get(i).substring(1);
        elements.remove(i);
        elements.add(i, separateElement);
        elements.add(i, "(");
        i = i - 1;
        continue;
      }
      if (elements.get(i).endsWith(")") && elements.get(i).length() > 1)
      {
        String separateElement = elements.get(i).substring(0, elements.get(i).length() - 1);
        elements.remove(i);
        elements.add(i, ")");
        elements.add(i, separateElement);
        i = i - 1;
        continue;
      }
    }
    Stack<DefaultMutableTreeNode> rootStack = new Stack<DefaultMutableTreeNode>();

    // It is assumed that the first word is '('
    for (int i = 0; i < elements.size(); i++)
    {
      if (elements.get(i).equals("("))
      {
        i++;
        DefaultMutableTreeNode newRootNode = new DefaultMutableTreeNode(elements.get(i));
        currentNode.add(newRootNode);
        rootStack.push(currentNode);
        currentNode = newRootNode;
      } else if (elements.get(i).equals(")"))
      {
        currentNode = rootStack.pop();
      } else
        currentNode.add(new DefaultMutableTreeNode(elements.get(i)));
    }

    if (root.getChildCount() > 0)
      return new DefaultTreeModel(root.getChildAt(0));
    else
      return new DefaultTreeModel(root);
  }

  // for test
  public static void main(String args[])
  {
    TreeModel model = S_ExpressionHandler.getTreeModelByS_Expression("(+ 3 (/ 3 ( * 2 8 )))");
    System.out.println(getS_Expression(model));
  }
}
