package node;

import java.io.IOException;

public class LAndExpNode {
    //逻辑与表达式 LAndExp → EqExp | LAndExp '&&' EqExp
    //改为右递归LAndExp → EqExp ['&&' LAndExp]
    private EqExpNode eqExpNode;
    private LAndExpNode lAndExpNode;

    public LAndExpNode(EqExpNode eqExpNode, LAndExpNode lAndExpNode) {
        this.eqExpNode = eqExpNode;
        this.lAndExpNode = lAndExpNode;
    }

    public void print() throws IOException {
        eqExpNode.print();
        NodeOutput.parserString = NodeOutput.parserString.concat("<LAndExp>\n");
        if (lAndExpNode != null) {
            NodeOutput.parserString = NodeOutput.parserString.concat("AND &&\n");
            lAndExpNode.print();
        }
    }

    public LAndExpNode getLAndExpNode() {
        return lAndExpNode;
    }

    public EqExpNode getEqExpNode() {
        return eqExpNode;
    }
}
