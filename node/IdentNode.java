package node;

import java.io.IOException;

public class IdentNode {
    private String name;
    private int lineNum;

    public IdentNode(String name, int lineNum) {
        this.name = name;
        this.lineNum = lineNum;
    }

    public String getName() {
        return this.name;
    }

    public void print() throws IOException {
        NodeOutput.parserString = NodeOutput.parserString.concat("IDENFR " + name + "\n");
    }

    public int getLineNum() {
        return lineNum;
    }
}
