package node;


import java.io.IOException;

public class DeclNode {
    //声明 Decl → ConstDecl | VarDecl
    private ConstDeclNode constDeclNode;
    private VarDeclNode varDeclNode;
    private int lineNum;

    public DeclNode(ConstDeclNode constDeclNode, VarDeclNode varDeclNode, int lineNum) {
        this.constDeclNode = constDeclNode;
        this.varDeclNode = varDeclNode;
        this.lineNum = lineNum;
    }

    public void print() throws IOException {
        if (constDeclNode == null) {
            varDeclNode.print();
        } else {
            constDeclNode.print();
        }
        //NodeOutput.writeToParser("<Decl>");
    }

    public int getType() {
        if (constDeclNode == null) {
            return 2;
        } else {
            return 1;
        }
    }

    public ConstDeclNode getConstDeclNode() {
        return constDeclNode;
    }

    public VarDeclNode getVarDeclNode() {
        return varDeclNode;
    }

    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }
}
