package node;

import frontend.Token;
import frontend.TokenType;

import java.io.IOException;

public class MulExpNode {
    //乘除模表达式 MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
    private UnaryExpNode unaryExpNode;
    private TokenType op;
    private MulExpNode mulExpNode;

    public MulExpNode(UnaryExpNode unaryExpNode, TokenType op, MulExpNode mulExpNode) {
        this.mulExpNode = mulExpNode;
        this.op = op;
        this.unaryExpNode = unaryExpNode;
    }

    public MulExpNode getMulExpNode() {
        return mulExpNode;
    }

    public UnaryExpNode getUnaryExpNode() {
        return unaryExpNode;
    }

    public TokenType getOp() {
        return op;
    }

    public void print() throws IOException {
        unaryExpNode.print();
        NodeOutput.parserString = NodeOutput.parserString.concat("<MulExp>\n");
        if (op != null) {
            switch (op) {
                case MULT:
                    NodeOutput.parserString = NodeOutput.parserString.concat("MULT *\n");
                    break;
                case DIV:
                    NodeOutput.parserString = NodeOutput.parserString.concat("DIV /\n");
                    break;
                case MOD:
                    NodeOutput.parserString = NodeOutput.parserString.concat("MOD %\n");
                    break;
                default:
                    break;
            }
            mulExpNode.print();
        }
    }
}
