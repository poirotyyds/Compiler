package node;

import frontend.TokenType;
import midend.value.Value;

import java.io.IOException;

public class AddExpNode {
    // AddExp → MulExp | AddExp ('+' | '−') MulExp
    //改为AddExp → MulExp ['+'/'-'AddExp]
    private MulExpNode mulExpNode;
    private AddExpNode addExpNode;
    private TokenType op;

    public AddExpNode(MulExpNode mulExpNode, TokenType op, AddExpNode addExpNode) {
        this.mulExpNode = mulExpNode;
        this.addExpNode = addExpNode;
        this.op = op;
    }

    public void print() throws IOException {
        mulExpNode.print();
        NodeOutput.parserString = NodeOutput.parserString.concat("<AddExp>\n");
        if (op != null) {
            if (op.equals(TokenType.PLUS)) {
                NodeOutput.parserString = NodeOutput.parserString.concat("PLUS +\n");
            } else {
                NodeOutput.parserString = NodeOutput.parserString.concat("MINU -\n");
            }
            addExpNode.print();
        }
    }

    public MulExpNode getMulExpNode() {
        return mulExpNode;
    }

    public AddExpNode getAddExpNode() {
        return addExpNode;
    }

    public Value genIR() {
        return null;
    }

    public TokenType getOp() {
        return op;
    }
}
