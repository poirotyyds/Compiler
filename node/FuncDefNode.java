package node;

import frontend.TokenType;

import java.io.IOException;
import java.util.ArrayList;

public class FuncDefNode {
    //函数定义 FuncDef → FuncType Ident '(' [FuncFParams] ')' Block
    private FuncTypeNode funcTypeNode;
    private IdentNode identNode;
    private FuncFParamsNode funcFParamsNode;
    private  BlockNode blockNode;

    public FuncDefNode(FuncTypeNode funcTypeNode, IdentNode identNode, FuncFParamsNode funcFParamsNode, BlockNode blockNode) {
        this.funcTypeNode = funcTypeNode;
        this.identNode = identNode;
        this.funcFParamsNode = funcFParamsNode;
        this.blockNode = blockNode;
    }

    public void print() throws IOException {
        funcTypeNode.print();
        identNode.print();
        NodeOutput.parserString = NodeOutput.parserString.concat("LPARENT (\n");
        if (funcFParamsNode != null) {
            funcFParamsNode.print();
        }
        NodeOutput.parserString = NodeOutput.parserString.concat("RPARENT )\n");
        blockNode.print();
        NodeOutput.parserString = NodeOutput.parserString.concat("<FuncDef>\n");
    }

    public int getFuncType() {
        return funcTypeNode.getType();
    }

    public String getIdentName() {
        return identNode.getName();
    }

    public ArrayList<FuncFParamNode> getFuncFParamNodes() {
        if (funcFParamsNode == null) {
            return new ArrayList<FuncFParamNode>();
        }
        return funcFParamsNode.getFuncFParamNodes();
    }

    public BlockNode getBlockNode() {
        return blockNode;
    }

    public int getIdentLineNum() {
        return identNode.getLineNum();
    }
}
