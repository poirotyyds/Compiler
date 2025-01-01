package node;

import java.io.IOException;

public class FuncFParamNode {
    //函数形参 FuncFParam → BType Ident ['[' ']']
    private BTypeNode bTypeNode;
    private IdentNode identNode;
    private boolean existBracket;//是数组

    public FuncFParamNode(BTypeNode bTypeNode, IdentNode identNode, boolean existBracket) {
        this.bTypeNode = bTypeNode;
        this.identNode = identNode;
        this.existBracket = existBracket;
    }

    public void print() throws IOException {
        bTypeNode.print();
        identNode.print();
        if (existBracket) {
            NodeOutput.parserString = NodeOutput.parserString.concat("LBRACK [\n");
            NodeOutput.parserString = NodeOutput.parserString.concat("RBRACK ]\n");
        }
        NodeOutput.parserString = NodeOutput.parserString.concat("<FuncFParam>\n");
    }

    public int getBType() {
        return bTypeNode.getType();
    }

    public String getName() {
        return identNode.getName();
    }

    public boolean isArray() {
        return existBracket;
    }

    public int getIdentLineNum() {
        return identNode.getLineNum();
    }

    public int getType() {
        if (getBType() == 1 && !existBracket) {
            return 1;//"Char";
        } else if (getBType() == 0 && !existBracket) {
            return 2;//"Int";
        } else if (getBType() == 1 && existBracket) {
            return 3;//"CharArray";
        } else if (getBType()== 0 && existBracket) {
            return 4;//"IntArray";
        } else {
            return 404;//"WrongType";
        }
    }
}
