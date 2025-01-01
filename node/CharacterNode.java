package node;

import java.io.IOException;

public class CharacterNode {
    private String value;

    public CharacterNode(String value) {
        this.value = value;
    }

    public void print() throws IOException {
        NodeOutput.parserString = NodeOutput.parserString.concat("CHRCON " + value + "\n");
        NodeOutput.parserString = NodeOutput.parserString.concat("<Character>\n");
    }

    public int getInteger() {
        if (value.length() == 3) { // 非转义字符
            return (int) value.charAt(1);
        } else { //转义字符
            switch (value.charAt(2)) {
                case 'a' :
                    return 7;
                case 'b' :
                    return 8;
                case 't' :
                    return 9;
                case 'n' :
                    return 10;
                case 'v' :
                    return 11;
                case 'f' :
                    return 12;
                case '\"' :
                    return 34;
                case '\'' :
                    return 39;
                case '\\' :
                    return 92;
                case '0' :
                    return 0;
                default:
                    throw new RuntimeException("未定义转义符");
            }
        }
    }
}
