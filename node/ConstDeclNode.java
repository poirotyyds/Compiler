package node;

import java.io.IOException;
import java.util.ArrayList;

public class ConstDeclNode {
    //常量声明 ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'
    private BTypeNode bTypeNode;
    private ArrayList<ConstDefNode>  constDefNodes;

    public ConstDeclNode(BTypeNode bTypeNode, ArrayList<ConstDefNode> constDefNodes) {
        this.bTypeNode = bTypeNode;
        this.constDefNodes = constDefNodes;
    }

    public void print() throws IOException {
        NodeOutput.parserString = NodeOutput.parserString.concat("CONSTTK const\n");
        bTypeNode.print();
        constDefNodes.get(0).print();
        for (int i = 1; i < constDefNodes.size(); i++) {
            NodeOutput.parserString = NodeOutput.parserString.concat("COMMA ,\n");
            constDefNodes.get(i).print();
        }
        NodeOutput.parserString = NodeOutput.parserString.concat("SEMICN ;\n");
        NodeOutput.parserString = NodeOutput.parserString.concat("<ConstDecl>\n");
    }

    public ArrayList<ConstDefNode> getConstDefNodes() {
        return constDefNodes;
    }

    public int getType() {
        return bTypeNode.getType();
    }
}
