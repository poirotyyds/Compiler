package node;

import java.io.IOException;

public class MainFuncDefNode {
    // MainFuncDef → 'int' 'main' '(' ')' Block
    private BlockNode blockNode;

    public MainFuncDefNode(BlockNode blockNode) {
        this.blockNode = blockNode;
    }

    public void print() throws IOException {
        NodeOutput.parserString = NodeOutput.parserString.concat("INTTK int\n");
        NodeOutput.parserString = NodeOutput.parserString.concat("MAINTK main\n");
        NodeOutput.parserString = NodeOutput.parserString.concat("LPARENT (\n");
        NodeOutput.parserString = NodeOutput.parserString.concat("RPARENT )\n");
        blockNode.print();
        NodeOutput.parserString = NodeOutput.parserString.concat("<MainFuncDef>\n");
    }

    public BlockNode getBlockNode() {
        return blockNode;
    }
}
