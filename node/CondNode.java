package node;

import java.io.IOException;

public class CondNode {
    //条件表达式 Cond → LOrExp // 存在即可
    private LOrExpNode lOrExpNode;

    public CondNode(LOrExpNode lOrExpNode) {
        this.lOrExpNode = lOrExpNode;
    }

    public void print() throws IOException {
        lOrExpNode.print();
        NodeOutput.parserString = NodeOutput.parserString.concat("<Cond>\n");
    }

    public LOrExpNode getLOrExpNode() {
        return lOrExpNode;
    }
}
