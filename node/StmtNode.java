package node;

import frontend.symbolTableManagement.Symbol;

import java.io.IOException;
import java.util.ArrayList;

public class StmtNode {
    //语句 Stmt → LVal '=' Exp ';' // 每种类型的语句都要覆盖-12
    //| [Exp] ';' //有无Exp两种情况-11
    //| Block---------------10
    //| 'if' '(' Cond ')' Stmt [ 'else' Stmt ] // 1.有else 2.无else-----9,8
    //| 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt -----7
    //| 'break' ';' | 'continue' ';'-----5,6
    //| 'return' [Exp] ';' // 1.有Exp 2.无Exp-----4
    //| LVal '=' 'getint''('')'';'------3
    //| LVal '=' 'getchar''('')'';'-----2
    //| 'printf''('StringConst {','Exp}')'';'-----1
    private int type;
    private StringConstNode stringConstNode;
    private ArrayList<ExpNode> printfExpNodes;
    private LValNode lValNode;
    private ExpNode expNode;
    private ForStmtNode forLeftStmtNode;
    private CondNode condNode;
    private ForStmtNode forRightStmtNode;
    private StmtNode forStmtNode;
    private BlockNode blockNode;
    private StmtNode ifStmtNode;
    private StmtNode elseStmtNode;
    private int lineNum;

    public StmtNode(int type, StringConstNode stringConstNode, ArrayList<ExpNode> printfExpNodes
            , LValNode lValNode, ExpNode expNode, ForStmtNode forLeftStmtNode, CondNode condNode, ForStmtNode forRightStmtNode
            , StmtNode forStmtNode, BlockNode blockNode, StmtNode ifStmtNode, StmtNode elseStmtNode) {
        this.type = type;
        this.stringConstNode = stringConstNode;
        this.printfExpNodes = printfExpNodes;
        this.lValNode = lValNode;
        this.expNode = expNode;
        this.forLeftStmtNode = forLeftStmtNode;
        this.condNode = condNode;
        this.forRightStmtNode = forRightStmtNode;
        this.forStmtNode = forStmtNode;
        this.blockNode = blockNode;
        this.ifStmtNode = ifStmtNode;
        this.elseStmtNode = elseStmtNode;
    }

    public String getIdentName() {
        return lValNode.getIdentName();
    }

    public void print() throws IOException {
        if (type == 1) {
            NodeOutput.parserString = NodeOutput.parserString.concat("PRINTFTK printf\n");
            NodeOutput.parserString = NodeOutput.parserString.concat("LPARENT (\n");
            stringConstNode.print();
            for (ExpNode printfExpNode : printfExpNodes) {
                NodeOutput.parserString = NodeOutput.parserString.concat("COMMA ,\n");
                printfExpNode.print();
            }
            NodeOutput.parserString = NodeOutput.parserString.concat("RPARENT )\n");
            NodeOutput.parserString = NodeOutput.parserString.concat("SEMICN ;\n");
        } else if (type == 2) {
            lValNode.print();
            NodeOutput.parserString = NodeOutput.parserString.concat("ASSIGN =\n");
            NodeOutput.parserString = NodeOutput.parserString.concat("GETCHARTK getchar\n");
            NodeOutput.parserString = NodeOutput.parserString.concat("LPARENT (\n");
            NodeOutput.parserString = NodeOutput.parserString.concat("RPARENT )\n");
            NodeOutput.parserString = NodeOutput.parserString.concat("SEMICN ;\n");
        } else if (type == 3) {
            lValNode.print();
            NodeOutput.parserString = NodeOutput.parserString.concat("ASSIGN =\n");
            NodeOutput.parserString = NodeOutput.parserString.concat("GETINTTK getint\n");
            NodeOutput.parserString = NodeOutput.parserString.concat("LPARENT (\n");
            NodeOutput.parserString = NodeOutput.parserString.concat("RPARENT )\n");
            NodeOutput.parserString = NodeOutput.parserString.concat("SEMICN ;\n");
        } else if (type == 4) {
            NodeOutput.parserString = NodeOutput.parserString.concat("RETURNTK return\n");
            if (expNode != null) {
                expNode.print();
            }
            NodeOutput.parserString = NodeOutput.parserString.concat("SEMICN ;\n");
        } else if (type == 5) {
            NodeOutput.parserString = NodeOutput.parserString.concat("BREAKTK break\n");
            NodeOutput.parserString = NodeOutput.parserString.concat("SEMICN ;\n");
        } else if (type == 6) {
            NodeOutput.parserString = NodeOutput.parserString.concat("CONTINUETK continue\n");
            NodeOutput.parserString = NodeOutput.parserString.concat("SEMICN ;\n");
        } else if (type == 7) {
            NodeOutput.parserString = NodeOutput.parserString.concat("FORTK for\n");
            NodeOutput.parserString = NodeOutput.parserString.concat("LPARENT (\n");
            if (forLeftStmtNode != null) {
                forLeftStmtNode.print();
            }
            NodeOutput.parserString = NodeOutput.parserString.concat("SEMICN ;\n");
            if (condNode != null) {
                condNode.print();
            }
            NodeOutput.parserString = NodeOutput.parserString.concat("SEMICN ;\n");
            if (forRightStmtNode != null) {
                forRightStmtNode.print();
            }
            NodeOutput.parserString = NodeOutput.parserString.concat("RPARENT )\n");
            forStmtNode.print();
        } else if (type == 8) {
            NodeOutput.parserString = NodeOutput.parserString.concat("IFTK if\n");
            NodeOutput.parserString = NodeOutput.parserString.concat("LPARENT (\n");
            condNode.print();
            NodeOutput.parserString = NodeOutput.parserString.concat("RPARENT )\n");
            ifStmtNode.print();
        } else if (type == 9) {
            NodeOutput.parserString = NodeOutput.parserString.concat("IFTK if\n");
            NodeOutput.parserString = NodeOutput.parserString.concat("LPARENT (\n");
            condNode.print();
            NodeOutput.parserString = NodeOutput.parserString.concat("RPARENT )\n");
            ifStmtNode.print();
            NodeOutput.parserString = NodeOutput.parserString.concat("ELSETK else\n");
            elseStmtNode.print();
        } else if (type == 10) {
            blockNode.print();
        } else if (type == 11) {
            if (expNode != null) {
                expNode.print();
            }
            NodeOutput.parserString = NodeOutput.parserString.concat("SEMICN ;\n");
        } else if (type == 12) {
            lValNode.print();
            NodeOutput.parserString = NodeOutput.parserString.concat("ASSIGN =\n");
            expNode.print();
            NodeOutput.parserString = NodeOutput.parserString.concat("SEMICN ;\n");
        }
        NodeOutput.parserString = NodeOutput.parserString.concat("<Stmt>\n");
    }

    public int getType() {
        return type;
    }

    public ArrayList<ExpNode> getPrintfExpNodes() {
        return printfExpNodes;
    }

    public StringConstNode getStringConstNode() {
        return stringConstNode;
    }

    public StmtNode getIfStmtNode() {
        return ifStmtNode;
    }

    public StmtNode getElseStmtNode() {
        return elseStmtNode;
    }

    public BlockNode getBlockNode() {
        return blockNode;
    }

    public StmtNode getForStmtNode() {
        return forStmtNode;
    }

    public LValNode getLValNode() {
        return lValNode;
    }

    public ExpNode getExpNode() {
        return expNode;
    }

    public ForStmtNode getForLeftStmtNode() {
        return forLeftStmtNode;
    }

    public ForStmtNode getForRightStmtNode() {
        return forRightStmtNode;
    }

    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }

    public int getLineNum() {
        return lineNum;
    }

    public CondNode getCondNode() {
        return condNode;
    }
}
