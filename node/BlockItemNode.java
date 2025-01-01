package node;

import java.io.IOException;

public class BlockItemNode {
    //语句块项 BlockItem → Decl | Stmt
    private DeclNode declNode;
    private StmtNode stmtNode;

    public BlockItemNode(DeclNode declNode, StmtNode stmtNode) {
        this.declNode = declNode;
        this.stmtNode = stmtNode;
    }

    public void print() throws IOException {
        if (declNode != null) {
            declNode.print();
        } else {
            stmtNode.print();
        }
    }

    public boolean isDecl() {
        return stmtNode == null;
    }

    public DeclNode getDeclNode() {
        return declNode;
    }

    public StmtNode getStmtNode() {
        return stmtNode;
    }
}
