package node;

import java.io.IOException;

public class ConstExpNode {
    private AddExpNode addExpNode;

    public ConstExpNode(AddExpNode addExpNode) {
        this.addExpNode = addExpNode;
    }

    public void print() throws IOException {
        addExpNode.print();
        NodeOutput.parserString = NodeOutput.parserString.concat("<ConstExp>\n");
    }

    public AddExpNode getAddExpNode() {
        return addExpNode;
    }
}
