package node;

import java.io.IOException;

public class VarDefNode {
    //变量定义 VarDef → Ident [ '[' ConstExp ']' ] | Ident [ '[' ConstExp ']' ] '=' InitVal
    //改为 VarDef → Ident [ '[' ConstExp ']' ] [ '=' InitVal]
    private IdentNode identNode;
    private ConstExpNode constExpNode;
    private InitValNode initValNode;
    private int lineNum;

    public VarDefNode(IdentNode identNode, ConstExpNode constExpNode, InitValNode initValNode, int lineNum) {
        this.identNode = identNode;
        this.constExpNode = constExpNode;
        this.initValNode= initValNode;
        this.lineNum = lineNum;
    }

    public void print() throws IOException {
        identNode.print();
        if (constExpNode != null) {
            NodeOutput.parserString = NodeOutput.parserString.concat("LBRACK [\n");
            constExpNode.print();
            NodeOutput.parserString = NodeOutput.parserString.concat("RBRACK ]\n");
        }
        if (initValNode != null) {
            NodeOutput.parserString = NodeOutput.parserString.concat("ASSIGN =\n");
            initValNode.print();
        }
        NodeOutput.parserString = NodeOutput.parserString.concat("<VarDef>\n");
    }

    public String getIentName() {
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

    public InitValNode getInitValNode() {
        return initValNode;
    }
}
