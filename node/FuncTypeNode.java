package node;

import java.io.IOException;

public class FuncTypeNode {
    //函数类型 FuncType → 'void' | 'int' | 'char'
    private String funType;

    public FuncTypeNode(String funType) {
        this.funType = funType;
    }

    public void print() throws IOException {
        if (funType.equals("VOIDTK")) {
            NodeOutput.parserString = NodeOutput.parserString.concat("VOIDTK void\n");
        } else if (funType.equals("INTTK")) {
            NodeOutput.parserString = NodeOutput.parserString.concat("INTTK int\n");
        } else {
            NodeOutput.parserString = NodeOutput.parserString.concat("CHARTK char\n");
        }
        NodeOutput.parserString = NodeOutput.parserString.concat("<FuncType>\n");
    }

    public int getType() {
        if (funType.equals("INTTK")) {
            return 0;
        } else if (funType.equals("CHARTK")) {
            return 1;
        } else {
            return 2;
        }
    }
}
