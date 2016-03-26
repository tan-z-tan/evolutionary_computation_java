package math.gaussianProcess;

import javax.swing.tree.TreeModel;

public class TreeKernel implements Kernel<TreeModel>
{
    public TreeKernel()
    {
        
    }
    
    @Override
    public double k(TreeModel x1, TreeModel x2)
    {
        return treeKernel.TreeKernel.treeKernel(x1, x2);
    }
}
