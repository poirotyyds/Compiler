package node;

import frontend.TokenType;

import java.io.IOException;

public class EqExpNode {
    //相等性表达式 EqExp → RelExp | EqExp ('==' | '!=') RelExp
    //EqExp → RelExp [ ('==' | '!=') EqExp]

    private RelExpNode relExpNode;
    private TokenType eqOrNot;
    private EqExpNode eqExpNode;

    public EqExpNode(RelExpNode relExpNode, TokenType eqOrNot, EqExpNode eqExpNode) {
        this.relExpNode = relExpNode;
        this.eqOrNot = eqOrNot;
        this.eqExpNode = eqExpNode;
    }

    public void print() throws IOException {
        relExpNode.print();
        NodeOutput.parserString = NodeOutput.parserString.concat("<EqExp>\n");
        if (eqOrNot != null) {
            if (eqOrNot.equals(TokenType.EQL)) {
                NodeOutput.parserString = NodeOutput.parserString.concat("EQL ==\n");
                eqExpNode.print();
            } else if (eqOrNot.equals(TokenType.NEQ)) {
                NodeOutput.parserString = NodeOutput.parserString.concat("NEQ !=\n");
                eqExpNode.print();
            }
        }
    }

    public EqExpNode getEqExpNode() {
        return eqExpNode;
    }

    public RelExpNode getRelExpNode() {
        return relExpNode;
    }

    public TokenType getEqOrNot() {
        return eqOrNot;
    }
}
