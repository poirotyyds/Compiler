package node;

import java.io.IOException;
import java.util.ArrayList;

public class FuncRParamsNode {
    //FuncRParams → Exp { ',' Exp }
    private ArrayList<ExpNode> expNodes;

    public FuncRParamsNode(ArrayList<ExpNode> expNodes) {
        this.expNodes = expNodes;
    }

    public void print() throws IOException {
        expNodes.get(0).print();
        for (int i = 1; i < expNodes.size(); i++) {
            NodeOutput.parserString = NodeOutput.parserString.concat("COMMA ,\n");
            expNodes.get(i).print();
        }
        NodeOutput.parserString = NodeOutput.parserString.concat("<FuncRParams>\n");
    }

    public ArrayList<ExpNode> getExpNodes() {
        return expNodes;
    }
}
