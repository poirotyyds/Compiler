package node;

import java.io.IOException;
import java.util.ArrayList;

public class FuncFParamsNode {
    //函数形参表 FuncFParams → FuncFParam { ',' FuncFParam }
    private ArrayList<FuncFParamNode> funcFParamNodes;

    public FuncFParamsNode(ArrayList<FuncFParamNode> funcFParamNodes) {
        this.funcFParamNodes = funcFParamNodes;
    }

    public void print() throws IOException {
        funcFParamNodes.get(0).print();
        for (int i = 1; i < funcFParamNodes.size(); i++) {
            NodeOutput.parserString = NodeOutput.parserString.concat("COMMA ,\n");
            funcFParamNodes.get(i).print();
        }
        NodeOutput.parserString = NodeOutput.parserString.concat("<FuncFParams>\n");
    }

    public ArrayList<FuncFParamNode> getFuncFParamNodes() {
        return funcFParamNodes;
    }
}
