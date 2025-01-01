package node;

import java.io.IOException;
import java.util.ArrayList;

public class BlockNode {
    //语句块 Block → '{' { BlockItem } '}
    private ArrayList<BlockItemNode> blockItemNodes;
    private int lastLineNum;

    public BlockNode(ArrayList<BlockItemNode> blockItemNodes, int lastLineNum) {
        this.blockItemNodes = blockItemNodes;
        this.lastLineNum = lastLineNum;
    }

    public void print() throws IOException {
        NodeOutput.parserString = NodeOutput.parserString.concat("LBRACE {\n");
        for (int i = 0; blockItemNodes != null && i < blockItemNodes.size(); i++) {
            blockItemNodes.get(i).print();
        }
        NodeOutput.parserString = NodeOutput.parserString.concat("RBRACE }\n");
        NodeOutput.parserString = NodeOutput.parserString.concat("<Block>\n");
    }

    public ArrayList<BlockItemNode> getBlockItemNodes() {
        return blockItemNodes;
    }

    public int getLineNum() {
        return this.lastLineNum;
    }
}
