package node;

import java.io.IOException;
import java.util.ArrayList;

public class CompUnitNode {
    //编译单元 CompUnit → {Decl} {FuncDef} MainFuncDef
    private ArrayList<DeclNode> declNodes;
    private ArrayList<FuncDefNode> funcDefNodes;
    private MainFuncDefNode mainFuncDefNode;

    public CompUnitNode(ArrayList<DeclNode> declNodes, ArrayList<FuncDefNode> funcDefNodes, MainFuncDefNode mainFuncDefNode) {
        this.declNodes = declNodes;
        this.funcDefNodes = funcDefNodes;
        this.mainFuncDefNode = mainFuncDefNode;
    }

    public void print() throws IOException {
        for (DeclNode declNode : this.declNodes) {
            declNode.print();
        }
        for (FuncDefNode funcDefNode : this.funcDefNodes) {
            funcDefNode.print();
        }
        mainFuncDefNode.print();
        NodeOutput.parserString = NodeOutput.parserString.concat("<CompUnit>\n");
    }

    public ArrayList<DeclNode> getDeclNodes() {
        return declNodes;
    }

    public ArrayList<FuncDefNode> getFuncDefNodes() {
        return funcDefNodes;
    }

    public MainFuncDefNode getMainFuncDefNode() {
        return mainFuncDefNode;
    }
}
