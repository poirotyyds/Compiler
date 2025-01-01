package frontend.symbolTableManagement;

import node.FuncFParamNode;

import java.util.List;

public class FunctionSymbol extends Symbol {
    private int scopeId;//函数作用域id
    private List<FuncFParamNode> funcFParamNodes; // 函数参数表

    //tableId为所在的作用域
    public FunctionSymbol(int id, int tableId, String name, int type, int bType, boolean isCon, int scopeId, List<FuncFParamNode> funcFParamNodes) {
        super(id, tableId, name, type, bType, isCon);
        this.scopeId = scopeId;
        this.funcFParamNodes = funcFParamNodes;
    }

    public int getScopeId() {
        return scopeId;
    }

    public int getSize() {
        return funcFParamNodes.size();
    }

    public List<FuncFParamNode> getFuncFParamNodes() {
        return funcFParamNodes;
    }
}
