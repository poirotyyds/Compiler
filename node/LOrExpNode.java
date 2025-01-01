package node;

import java.io.IOException;

public class LOrExpNode {
    //逻辑或表达式 LOrExp → LAndExp | LOrExp '||' LAndExp
    //LOrExp → LAndExp ['||' LOrExp]
    private LAndExpNode lAndExpNode;
    private LOrExpNode lOrExpNode;

    public LOrExpNode(LAndExpNode lAndExpNode, LOrExpNode lOrExpNode) {
        this.lAndExpNode = lAndExpNode;
        this.lOrExpNode = lOrExpNode;
    }

    public void print() throws IOException {
        lAndExpNode.print();
        NodeOutput.parserString = NodeOutput.parserString.concat("<LOrExp>\n");
        if (lOrExpNode != null) {
            NodeOutput.parserString = NodeOutput.parserString.concat("OR ||\n");
            lOrExpNode.print();
        }
    }

    public LOrExpNode getlOrExpNode() {
        return lOrExpNode;
    }

    public LAndExpNode getlAndExpNode() {
        return lAndExpNode;
    }
}
