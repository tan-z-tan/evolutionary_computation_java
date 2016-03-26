package geneticProgramming;

import geneticProgramming.symbols.DefaultSymbolType;
import geneticProgramming.symbols.SymbolType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import random.RandomManager;

/**
 * GP で使われるシンボルを管理する class
 * 
 * @author tanji
 */
public class GpSymbolSet
{
    private List<SymbolType> _symbolList;
    private List<SymbolType> _functionList;
    private List<SymbolType> _terminalList;
    private int _terminalSize;
    
    /** default constructor */
    public GpSymbolSet()
    {
        _symbolList = new ArrayList<SymbolType>();
    }

    public GpSymbolSet(List<SymbolType> types)
    {
        _symbolList = types;
        sort();
    }

    /** returns list of function symbols */
    public List<SymbolType> getFunctionList()
    {
        return _functionList;
    }
    
    /** returns list of terminal symbols */
    public List<SymbolType> getTerminalList()
    {
        return _terminalList;
    }
    
    /** returns random type */
    public SymbolType getRandomType()
    {
        return _symbolList.get((int) (RandomManager.getRandom() * _symbolList.size()));
    }

    /**
     * returns randomly selected symbol type in function type.
     * 
     * @return random function symbol.
     */
    public SymbolType getFunctionSymbol()
    {
        return _symbolList.get(_terminalSize + (int) (RandomManager.getRandom() * (_symbolList.size() - _terminalSize)));
    }

    /**
     * returns randomly selected symbol type in terminal type.
     * 
     * @return random terminal symbol.
     */
    public SymbolType getTerminalSymbol()
    {
        return _symbolList.get((int) (RandomManager.getRandom() * _terminalSize));
    }

    /** adds symbol type */
    public void addSymbol(SymbolType type)
    {
        _symbolList.add(type);
        sort();
    }

    /** adds symbol type */
    public void addSymbol(SymbolType type, int argSize)
    {
        type.setArgumentSize(argSize);
        addSymbol(type);
    }

    /** adds symbol type */
    public void addSymbol(SymbolType type, String name, int argSize)
    {
        type.setArgumentSize(argSize);
        type.setSymbolName(name);
        addSymbol(type);
    }

    private void sort()
    {
        Collections.sort(_symbolList);
        for (int i = 0; i < _symbolList.size(); i++)
        {
            if (_symbolList.get(i).getArgumentSize() != 0)
            {
                _terminalSize = i;
                _terminalList = _symbolList.subList(0, _terminalSize);
                _functionList = _symbolList.subList(_terminalSize, _symbolList.size());
                
                return;
            }
        }
    }

    /** returns symbol type at specified index */
    public SymbolType getSymbolType(int index)
    {
        return _symbolList.get(index);
    }

    /** returns the number of symbols */
    public int getSymbolSize()
    {
        return _symbolList.size();
    }

    /** returns the number of terminal symbols */
    public int getTerminalSize()
    {
        return _terminalSize;
    }
    
    /**
     * returns all the symbols in the SymbolSet.
     * @return
     */
    public List<SymbolType> getSymbolList()
    {
        return _symbolList;
    }

    /**
     * returns GP Symbol Set from specified File.
     * 
     * @param file
     * @return GP symbol set
     */
    public static GpSymbolSet getSymbolSet(File file)
    {
        GpSymbolSet symbolSet = new GpSymbolSet();
        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            while (reader.ready())
            {
                String line = reader.readLine();
                if (line.startsWith("//"))
                {
                    continue;
                } else if (line.startsWith("}"))
                {
                    return symbolSet;
                } else if (line.startsWith("GP Nodes"))
                {
                    readSymbols(symbolSet, reader);
                }
            }
            return symbolSet;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return symbolSet;
    }

    /**
     * returns GP Symbol Set from specified File.
     * 
     * @param file
     * @return GP symbol set
     */
    public static GpSymbolSet getSymbolSet(InputStream file)
    {
        GpSymbolSet symbolSet = new GpSymbolSet();
        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(file));
            while (reader.ready())
            {
                String line = reader.readLine();
                if (line.startsWith("//"))
                {
                    continue;
                } else if (line.startsWith("}"))
                {
                    return symbolSet;
                } else if (line.startsWith("GP Nodes"))
                {
                    readSymbols(symbolSet, reader);
                }
            }
            return symbolSet;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return symbolSet;
    }

    /** reads symbols. This method uses reflection techniques. */
    private static void readSymbols(GpSymbolSet symbolSet, BufferedReader reader) throws Exception
    {
        try
        {
            while (reader.ready())
            {
                String line = reader.readLine();
                if (line.startsWith("//"))
                {
                    continue;
                } else if (line.startsWith("}"))
                {
                    return;
                }
                String[] tokens = line.split("\\s*,\\s*");
                Class<? extends SymbolType> nodeTypeClass = Class.forName(tokens[1]).asSubclass(SymbolType.class);
                SymbolType nodeType = nodeTypeClass.newInstance();
                nodeType.setSymbolName(tokens[0]);
                nodeType.setArgumentSize(Integer.valueOf(tokens[2]));
                symbolSet.addSymbol(nodeType);
            }
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    public SymbolType getSymbolByName(String name)
    {
        if( name.indexOf("[") != -1 )
        {
            name = name.substring(0, name.indexOf("["));
        }
        for (int i = 0; i < _symbolList.size(); i++)
        {
            if (_symbolList.get(i).getSymbolName().equals(name))
            {
                return _symbolList.get(i);
            }
        }
        return null;
    }

    /**
     * returns the index of specified symbolType, or -1 if there is no such type.
     * 
     * @param symbolType
     */
    public int getIndex(SymbolType type)
    {
        return _symbolList.indexOf(type);
    }

    @Override
    public String toString()
    {
        StringBuilder str = new StringBuilder("SymbolSet [");
        str.append(System.getProperty("line.separator"));
        for (SymbolType symbolType : _symbolList)
        {
            str.append(symbolType);
            str.append(System.getProperty("line.separator"));
        }
        str.append("]");
        return str.toString();
    }

    public String getExtraValueByName(String userObject)
    {
        if( userObject.indexOf("[") != -1 )
        {
            return userObject.substring(userObject.indexOf("[") + 1, userObject.length() -1);
        }
        else
        {
            return null;
        }
    }
    
    public static void main(String[] argv)
    {
        GpSymbolSet set = new GpSymbolSet();
        set.addSymbol(new DefaultSymbolType("A", 3));
        set.addSymbol(new DefaultSymbolType("B", 2));
        set.addSymbol(new DefaultSymbolType("C", 1));
        set.addSymbol(new DefaultSymbolType("x", 0));
        set.addSymbol(new DefaultSymbolType("y", 0));
        
        System.out.println(set._symbolList);
        System.out.println(set._terminalList);
        System.out.println(set._functionList);
    }
}
