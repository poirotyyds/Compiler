package node;

import java.io.IOException;

public class ExpNode {
    private AddExpNode addExpNode;

    public ExpNode(AddExpNode addExpNode) {
        this.addExpNode = addExpNode;
    }

    public void print() throws IOException {
        addExpNode.print();
        NodeOutput.parserString = NodeOutput.parserString.concat("<Exp>\n");
    }

    public AddExpNode getAddExpNode() {
        return addExpNode;
    }
}
