package node;

import frontend.TokenType;

import java.io.IOException;

public class BTypeNode {
    //基本类型 BType → 'int' | 'char'
    private TokenType type;

    public BTypeNode(TokenType type) {
        this.type = type;
    }

    public void print() throws IOException {
        if (this.type.equals(TokenType.CHARTK)) {
            NodeOutput.parserString = NodeOutput.parserString.concat("CHARTK char\n");
        } else {
            NodeOutput.parserString = NodeOutput.parserString.concat("INTTK int\n");
        }
    }

    public int getType() {
        if (this.type.equals(TokenType.CHARTK)) {
            return 1;
        } else {
            return 0;
        }
    }
}
