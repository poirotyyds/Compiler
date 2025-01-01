package node;

import java.io.IOException;
import java.util.ArrayList;

public class InitValNode {
    //变量初值 InitVal → Exp | '{' [ Exp { ',' Exp } ] '}' | StringConst
    private ExpNode expNode;
    private ArrayList<ExpNode> expNodes;
    private StringConstNode stringConstNode;

    public InitValNode(ExpNode expNode, ArrayList<ExpNode> expNodes, StringConstNode stringConstNode) {
        this.expNode = expNode;
        this.expNodes = expNodes;
        this.stringConstNode = stringConstNode;
    }

    public void print() throws IOException {
        if (stringConstNode != null) {
            stringConstNode.print();
        } else if (expNode != null) {
            expNode.print();
        } else {
            NodeOutput.parserString = NodeOutput.parserString.concat("LBRACE {\n");
            if (expNodes.size() != 0) {
                expNodes.get(0).print();
                for (int i = 1; i < expNodes.size(); i++) {
                    NodeOutput.parserString = NodeOutput.parserString.concat("COMMA ,\n");
                    expNodes.get(i).print();
                }
            }
            NodeOutput.parserString = NodeOutput.parserString.concat("RBRACE }\n");
        }
        NodeOutput.parserString = NodeOutput.parserString.concat("<InitVal>\n");
    }

    public StringConstNode getStringConstNode() {
        return stringConstNode;
    }

    public ExpNode getExpNode() {
        return expNode;
    }

    public ArrayList<ExpNode> getExpNodes() {
        return expNodes;
    }
}
