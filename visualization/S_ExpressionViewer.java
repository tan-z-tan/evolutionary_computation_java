package visualization;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.TreeModel;

import visualization.eps.EPSHandler;
import visualization.tree.TreeGraph;

public class S_ExpressionViewer implements ActionListener, ChangeListener
{
    private String _S_Expression;
    private JPanel _frame;
    //private JButton _btnCreate;
    private JTextField _textField;
    private TreeGraph _treeGraph;
    private JMenuItem _menuItemOpen;
    private JMenuItem _menuItemSave;
    private JSlider _scaleSlider;
    private JMenuBar menubar;
    
    // for test
    public static void main(String args[])
    {
        S_ExpressionViewer s_tree = new S_ExpressionViewer();
        if( args.length >= 1 )
        {
            s_tree.setS_Expression(args[0]);
        }
        
        if( args.length == 2 )
        {
            s_tree.saveEPS(new File(args[1]));
            System.exit(0);
        }
        
        JFrame frame = new JFrame("S-expression Viewer");
        frame.setJMenuBar(s_tree.menubar);
        frame.add(s_tree._frame);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);
        frame.setVisible(true);
    }
    
    public S_ExpressionViewer()
    {
        // data
        _S_Expression = "";
        
        // JFrame, top window
        _frame = new JPanel(new BorderLayout());
        //_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //_frame.setSize(800, 500);
        
        // JSplitPane that includes TreeGraph and TextArea
        _treeGraph = new TreeGraph(S_ExpressionHandler.getTreeModelByS_Expression(_S_Expression));
        JScrollPane scrollPane = new JScrollPane( _treeGraph );
        
        _textField = new JTextField("");
        _textField.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e)
            {
                setS_Expression( _textField.getText() );
                _treeGraph.updateModel();
                _treeGraph.repaint();                  
            }
        });
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(_textField, BorderLayout.CENTER);
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e)
            {
                _textField.setText("");
            }
        });
        //topPanel.add(clearButton, BorderLayout.WEST);
        JPanel topWest = new JPanel();
        topWest.add( clearButton );
        JButton wider = new JButton("→←");
        JButton closer = new JButton("←→");
        wider.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				_treeGraph.setNodeArcRadius( _treeGraph.getNodeArcRadius() + 2 );
				_treeGraph.updateModel();
				_treeGraph.repaint();
			}
		});
        closer.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				_treeGraph.setNodeArcRadius( _treeGraph.getNodeArcRadius() - 2 );
				_treeGraph.updateModel();
				_treeGraph.repaint();
			}
		});
        topWest.add( wider );;
        topWest.add( closer );;
        topPanel.add(topWest, BorderLayout.WEST);
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false, topPanel, scrollPane);
        
        // button and slider
        //_btnCreate = new JButton("Create S_Expression tree");
        //_btnCreate.addActionListener(this);
        _scaleSlider = new JSlider(JSlider.VERTICAL, 1, 500, 100);
        _scaleSlider.addChangeListener(this);
        //_scaleSlider.setEnabled( false );
        
        // menu
        menubar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        _menuItemOpen = new JMenuItem("Open File");
        _menuItemSave = new JMenuItem("Save EPS");
        _menuItemOpen.addActionListener(this);
        _menuItemSave.addActionListener(this);
        fileMenu.add(_menuItemOpen);
        fileMenu.add(_menuItemSave);
        menubar.add(fileMenu);
        
        _frame.add(splitPane, BorderLayout.CENTER);
        //_frame.add(_btnCreate, BorderLayout.NORTH);
        _frame.add(_scaleSlider, BorderLayout.EAST);
        _frame.setVisible(true);
    }
    
    public String getS_Expression()
    {
        return _S_Expression;
    }
    
    public void setS_Expression(String expression)
    {
        if( !_S_Expression.equals(expression) )
        {
            _S_Expression = expression;
            _textField.setText( _S_Expression );
            TreeModel treeModel = S_ExpressionHandler.getTreeModelByS_Expression( _S_Expression );
            _treeGraph.setTreeModel(treeModel);
        }
    }
    
    // ------------------------ listener methods ------------------------
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == _menuItemOpen)
        {
            JFileChooser chooser = new JFileChooser();
            int chooserValue = chooser.showOpenDialog(_frame);
            if (chooserValue == JFileChooser.APPROVE_OPTION)
            {
                File openFile = chooser.getSelectedFile();
                TreeModel treeModel = S_ExpressionHandler.getTreeModelByS_Expression(openFile);
                _treeGraph.setTreeModel(treeModel);
                _treeGraph.repaint();
                _frame.repaint();
            }
        } else if (e.getSource() == _menuItemSave)
        {
            JFileChooser chooser = new JFileChooser();
            int chooserValue = chooser.showSaveDialog(_frame);          
            if (chooserValue == JFileChooser.APPROVE_OPTION)
            {
                File openFile = chooser.getSelectedFile();
                saveEPS(openFile);
            }
        }
    }
    
    private void saveEPS(File file)
    {
        _treeGraph.setVisible(true);
        if( _treeGraph.getSize().getWidth() == 0 || _treeGraph.getSize().getHeight() == 0 )
        {
            _treeGraph.setSize(600, 400);
        }
        EPSHandler.printEPS(file, _treeGraph);
    }
    
    public void stateChanged(ChangeEvent e)
    {
        if (e.getSource() == this._scaleSlider)
        {
            _treeGraph.setScale(_scaleSlider.getValue() / 100.0);
            //_treeGraph.updateModel();
            _treeGraph.repaint();
        }
    }
}
