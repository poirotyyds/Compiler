package node;

import java.io.IOException;
import java.util.ArrayList;

public class VarDeclNode {
    //变量声明 VarDecl → BType VarDef { ',' VarDef } ';'
    private BTypeNode bTypeNode;
    private ArrayList<VarDefNode> varDefNodes;

    public VarDeclNode(BTypeNode bTypeNode, ArrayList<VarDefNode> varDefNodes) {
        this.bTypeNode = bTypeNode;
        this.varDefNodes = varDefNodes;
    }

    public void print() throws IOException {
        bTypeNode.print();
        varDefNodes.get(0).print();
        for (int i = 1; i < varDefNodes.size(); i++) {
            NodeOutput.parserString = NodeOutput.parserString.concat("COMMA ,\n");
            varDefNodes.get(i).print();
        }
        NodeOutput.parserString = NodeOutput.parserString.concat("SEMICN ;\n");
        NodeOutput.parserString = NodeOutput.parserString.concat("<VarDecl>\n");
    }

    public ArrayList<VarDefNode> getVarDefNodes() {
        return varDefNodes;
    }

    public int getType() {
        return bTypeNode.getType();
    }
}
