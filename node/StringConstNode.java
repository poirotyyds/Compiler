package node;

import java.io.IOException;

public class StringConstNode {
    private String str;

    public StringConstNode(String str) {
        this.str = str;
    }

    public void print() throws IOException {
        NodeOutput.parserString = NodeOutput.parserString.concat("STRCON " + str + "\n");
    }

    public int getNum() {
        int num = 0;
        for (int i = 0; i < str.length() - 1; i++) {
            if (str.charAt(i) == '%' && (str.charAt(i + 1) == 'd' || str.charAt(i + 1) == 'c')) {
                i++;
                num++;
            }
        }
        return num;
    }

    @Override
    public String toString() {
        return str;
    }
}
