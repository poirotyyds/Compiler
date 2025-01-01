package node;

import frontend.TokenType;

import java.io.IOException;

public class UnaryOpNode {
    //UnaryOp → '+' | '−' | '!'
    private TokenType op;

    public UnaryOpNode(TokenType op) {
        this.op = op;
    }

    public void print() throws IOException {
        if (op.equals(TokenType.PLUS)) {
            NodeOutput.parserString = NodeOutput.parserString.concat("PLUS +\n");
        } else if (op.equals(TokenType.MINU)) {
            NodeOutput.parserString = NodeOutput.parserString.concat("MINU -\n");
        } else if (op.equals(TokenType.NOT)) {
            NodeOutput.parserString = NodeOutput.parserString.concat("NOT !\n");
        }
        NodeOutput.parserString = NodeOutput.parserString.concat("<UnaryOp>\n");
    }

    public TokenType getOp() {
        return op;
    }
}
