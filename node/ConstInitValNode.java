package node;

import java.io.IOException;
import java.util.ArrayList;

public class ConstInitValNode {
    //ConstInitVal → ConstExp | '{' [ ConstExp { ',' ConstExp } ] '}' | StringConst
    private ConstExpNode constExpNode;
    private ArrayList<ConstExpNode> constExpNodes;
    private StringConstNode stringConstNode;

    public ConstInitValNode(ConstExpNode constExpNode, ArrayList<ConstExpNode> constExpNodes, StringConstNode stringConstNode) {
        this.constExpNode = constExpNode;
        this.constExpNodes = constExpNodes;
        this.stringConstNode = stringConstNode;
    }

    public void print() throws IOException {
        if (constExpNode != null) {
            constExpNode.print();
        } else if (stringConstNode != null) {
            stringConstNode.print();
        } else {
            NodeOutput.parserString = NodeOutput.parserString.concat("LBRACE {\n");
            if (constExpNodes != null && constExpNodes.size() != 0) {
                constExpNodes.get(0).print();
                for (int i = 1; i < constExpNodes.size(); i++) {
                    NodeOutput.parserString = NodeOutput.parserString.concat("COMMA ,\n");
                    constExpNodes.get(i).print();
                }
            }
            NodeOutput.parserString = NodeOutput.parserString.concat("RBRACE }\n");
        }
        NodeOutput.parserString = NodeOutput.parserString.concat("<ConstInitVal>\n");
    }

    public ConstExpNode getConstExpNode() {
        return constExpNode;
    }

    public StringConstNode getStringConstNode() {
        return stringConstNode;
    }

    public ArrayList<ConstExpNode> getConstExpNodes() {
        return constExpNodes;
    }
}
