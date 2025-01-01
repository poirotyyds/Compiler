package node;

import java.io.IOException;

public class UnaryExpNode {
    //一元表达式 UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp
    private PrimaryExpNode primaryExpNode;
    private IdentNode identNode;
    private FuncRParamsNode funcRParamsNode;
    private UnaryOpNode unaryOpNode;
    private UnaryExpNode unaryExpNode;

    public UnaryExpNode(PrimaryExpNode primaryExpNode, IdentNode identNode
            , FuncRParamsNode funcRParamsNode, UnaryOpNode unaryOpNode, UnaryExpNode unaryExpNode) {
        this.primaryExpNode = primaryExpNode;
        this.identNode = identNode;
        this.funcRParamsNode = funcRParamsNode;
        this.unaryOpNode = unaryOpNode;
        this.unaryExpNode = unaryExpNode;
    }

    public void print() throws IOException {
        if (primaryExpNode != null) {
            primaryExpNode.print();
        } else if (identNode != null) {
            identNode.print();
            NodeOutput.parserString = NodeOutput.parserString.concat("LPARENT (\n");
            if (funcRParamsNode != null) {
                funcRParamsNode.print();
            }
            NodeOutput.parserString = NodeOutput.parserString.concat("RPARENT )\n");
        } else {
            unaryOpNode.print();
            unaryExpNode.print();
        }
        NodeOutput.parserString = NodeOutput.parserString.concat("<UnaryExp>\n");
    }

    public PrimaryExpNode getPrimaryExpNode() {
        return primaryExpNode;
    }

    public UnaryExpNode getUnaryExpNode() {
        return unaryExpNode;
    }

    public UnaryOpNode getUnaryOpNode() {
        return unaryOpNode;
    }

    public IdentNode getIdentNode() {
        return identNode;
    }

    public FuncRParamsNode getFuncRParamsNode() {
        return funcRParamsNode;
    }
}

