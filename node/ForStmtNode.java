package node;

import java.io.IOException;

public class ForStmtNode {
    //语句 ForStmt → LVal '=' Exp // 存在即可
    private LValNode lValNode;
    private ExpNode expNode;

    public ForStmtNode(LValNode lValNode, ExpNode expNode) {
        this.expNode = expNode;
        this.lValNode = lValNode;
    }

    public void print() throws IOException {
        lValNode.print();
        NodeOutput.parserString = NodeOutput.parserString.concat("ASSIGN =\n");
        expNode.print();
        NodeOutput.parserString = NodeOutput.parserString.concat("<ForStmt>\n");
    }

    public LValNode getlValNode() {
        return lValNode;
    }

    public ExpNode getExpNode() {
        return expNode;
    }
}
