package node;

import java.io.IOException;

public class ConstDefNode {
    //ConstDef → Ident [ '[' ConstExp ']' ] '=' ConstInitVal
    private IdentNode identNode;
    private ConstExpNode constExpNode;
    private ConstInitValNode constInitValNode;

    public ConstDefNode(IdentNode identNode, ConstExpNode constExpNode, ConstInitValNode constInitValNode) {
        this.identNode = identNode;
        this.constExpNode = constExpNode;
        this.constInitValNode = constInitValNode;
    }

    public void print() throws IOException {
        identNode.print();
        if (constExpNode != null) {
            NodeOutput.parserString = NodeOutput.parserString.concat("LBRACK [\n");
            constExpNode.print();
            NodeOutput.parserString = NodeOutput.parserString.concat("RBRACK ]\n");
        }
        NodeOutput.parserString = NodeOutput.parserString.concat("ASSIGN =\n");
        constInitValNode.print();
        NodeOutput.parserString = NodeOutput.parserString.concat("<ConstDef>\n");
    }

    public String getIdentName() {
        return identNode.getName();
    }

    public boolean isArray() {
        return constExpNode != null;
    }

    public int getIdentLineNum() {
        return identNode.getLineNum();
    }

    public ConstExpNode getConstExpNode() {
        return constExpNode;
    }

    public ConstInitValNode getConstInitValNode() {
        return constInitValNode;
    }
}
