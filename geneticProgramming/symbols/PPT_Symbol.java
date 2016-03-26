package geneticProgramming.symbols;

import geneticProgramming.GpNode;

/**
 * PPT (Probabilistic Prototype Tree) 表現のノードを表すクラス．
 * 任意のSymbolTypeを任意のarityを持つPPTノードにラップする．evaluate, initialValueはコンストラクタに渡されたSymbolTypeのものを返す．
 * @author tanji
 */

public class PPT_Symbol extends SymbolType
{
    private SymbolType _originalSymbolType;
    
    public PPT_Symbol(SymbolType original, int arity)
    {
        _originalSymbolType = original;
        _symbolName = original.getSymbolName();
        if( original.getArgumentSize() != 0 )
        {
            _argumentSize = arity;
        }
        else
        {
            _argumentSize = original.getArgumentSize();
        }
    }
    
    public SymbolType getOriginalSymbol()
    {
    	return _originalSymbolType;
    }
    
    public Object initialValue()
    {
        return _originalSymbolType.initialValue();
    }
    
    @Override
    public Object evaluate(GpNode node, Object obj)
    {
        return _originalSymbolType.evaluate(node, obj);
    }
}
