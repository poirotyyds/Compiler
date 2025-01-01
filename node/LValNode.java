package node;

import java.io.IOException;

public class LValNode {
    //LVal → Ident ['[' Exp ']']
    private IdentNode identNode;
    private ExpNode expNode;

    public LValNode(IdentNode identNode, ExpNode expNode) {
        this.identNode = identNode;
        this.expNode =expNode;
    }

    public void print() throws IOException {
        identNode.print();
        if (expNode != null) {
            NodeOutput.parserString = NodeOutput.parserString.concat("LBRACK [\n");
            expNode.print();
            NodeOutput.parserString = NodeOutput.parserString.concat("RBRACK ]\n");
        }
        NodeOutput.parserString = NodeOutput.parserString.concat("<LVal>\n");
    }

    public String getIdentName() {
        return identNode.getName();
    }

    public IdentNode getIdentNode() {
        return identNode;
    }

    public ExpNode getExpNode() {
        return expNode;
    }
}
