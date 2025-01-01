package node;

import java.io.IOException;

public class NumberNode {
    private String value;

    public NumberNode(String value) {
        this.value = value;
    }

    public void print() throws IOException {
        NodeOutput.parserString = NodeOutput.parserString.concat("INTCON " + value + "\n");
        NodeOutput.parserString = NodeOutput.parserString.concat("<Number>\n");
    }

    public int getInteger() {
        return Integer.parseInt(value);
    }
}
