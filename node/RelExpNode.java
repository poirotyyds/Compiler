package node;

import frontend.TokenType;

import java.io.IOException;

public class RelExpNode {
    //关系表达式 RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
    //RelExp → AddExp [('<' | '>' | '<=' | '>=') RelExp]
    private AddExpNode addExpNode;
    private TokenType relType;
    private RelExpNode relExpNode;

    public RelExpNode(AddExpNode addExpNode, TokenType relType, RelExpNode relExpNode) {
        this.addExpNode = addExpNode;
        this.relType = relType;
        this.relExpNode = relExpNode;
    }

    public void print() throws IOException {
        addExpNode.print();
        NodeOutput.parserString = NodeOutput.parserString.concat("<RelExp>\n");
        if (relType != null) {
            if (relType.equals(TokenType.LSS)) {
                NodeOutput.parserString = NodeOutput.parserString.concat("LSS <\n");
                relExpNode.print();
            } else if (relType.equals(TokenType.GRE)){
                NodeOutput.parserString = NodeOutput.parserString.concat("GRE >\n");
                relExpNode.print();
            } else if (relType.equals(TokenType.LEQ)){
                NodeOutput.parserString = NodeOutput.parserString.concat("LEQ <=\n");
                relExpNode.print();
            } else if (relType.equals(TokenType.GEQ)){
                NodeOutput.parserString = NodeOutput.parserString.concat("GEQ >=\n");
                relExpNode.print();
            }
        }
    }

    public RelExpNode getRelExpNode() {
        return relExpNode;
    }

    public AddExpNode getAddExpNode() {
        return addExpNode;
    }

    public TokenType getRelType() {
        return relType;
    }
}
