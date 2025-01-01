package node;

import java.io.IOException;

public class PrimaryExpNode {
    //'(' Exp ')' | LVal | Number | Character
    private ExpNode expNode;
    private LValNode lValNode;
    private NumberNode numberNode;
    private CharacterNode characterNode;

    public PrimaryExpNode(ExpNode expNode, LValNode lValNode, NumberNode numberNode, CharacterNode characterNode) {
        this.expNode = expNode;
        this.lValNode = lValNode;
        this.numberNode = numberNode;
        this.characterNode = characterNode;
    }

    public void print() throws IOException {
        if (expNode != null) {
            NodeOutput.parserString = NodeOutput.parserString.concat("LPARENT (\n");
            expNode.print();
            NodeOutput.parserString = NodeOutput.parserString.concat("RPARENT )\n");
        } else if (lValNode != null) {
            lValNode.print();
        } else if (numberNode != null) {
            numberNode.print();
        } else {
            characterNode.print();
        }
        NodeOutput.parserString = NodeOutput.parserString.concat("<PrimaryExp>\n");
    }

    public ExpNode getExpNode() {
        return expNode;
    }

    public LValNode getLValNode() {
        return lValNode;
    }

    public CharacterNode getCharacterNode() {
        return characterNode;
    }

    public NumberNode getNumberNode() {
        return numberNode;
    }
}
