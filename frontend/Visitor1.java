package frontend;

import error.Error;
import error.ErrorType;
import frontend.symbolTableManagement.ArraySymbol;
import frontend.symbolTableManagement.FunctionSymbol;
import frontend.symbolTableManagement.Symbol;
import frontend.symbolTableManagement.SymbolTable;
import node.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Math.max;

public class Visitor1 {
    private CompUnitNode compUnitNode;
    private SymbolTable table = new SymbolTable(1, 0);
    private int id = 1;
    private int fatherId = 0;
    private int sum = 1;
    private HashMap<Integer, SymbolTable> tables = new HashMap<>();
    private HashMap<String, FunctionSymbol> functionSymbols = new HashMap<>();
    private int loopCount = 0;
    private boolean isCheckingFuncReturn = false;
    private boolean isCheckingVoidFunReturn = false;
    private String nowFunc;//用于检验当前函数实例的参数和行数
    private int nowFuncLineNum;
    private boolean isNowBlock = false;

    public Visitor1(CompUnitNode compUnitNode) {
        this.compUnitNode = compUnitNode;
        tables.put(1, table);
        visitCompUnitNode(this.compUnitNode);
    }

    private void visitCompUnitNode(CompUnitNode node) {
        for (DeclNode declNode : node.getDeclNodes()) {
            visitDeclNode(declNode);
        }
        for (FuncDefNode funcDefNode : node.getFuncDefNodes()) {
            visitFuncDefNode(funcDefNode);
        }
        visitMainFuncDefNode(node.getMainFuncDefNode());
    }

    private void visitMainFuncDefNode(MainFuncDefNode mainFuncDefNode) {
        visitBlockNode(mainFuncDefNode.getBlockNode());
        if (mainFuncDefNode.getBlockNode().getBlockItemNodes().size() == 0) {
            OutWrongInformation.addError(new Error(mainFuncDefNode.getBlockNode().getLineNum(), ErrorType.g));
        } else {
            BlockItemNode blockItemNode = mainFuncDefNode.getBlockNode().getBlockItemNodes().get(mainFuncDefNode.getBlockNode().getBlockItemNodes().size() - 1);
            if (blockItemNode.isDecl()) {
                ///****
                OutWrongInformation.addError(new Error(mainFuncDefNode.getBlockNode().getLineNum(), ErrorType.g));
            } else if (blockItemNode.getStmtNode().getType() != 4){
                OutWrongInformation.addError(new Error(mainFuncDefNode.getBlockNode().getLineNum(), ErrorType.g));
            }
        }
    }

    private void visitFuncDefNode(FuncDefNode funcDefNode) {
        if (functionSymbols.containsKey(funcDefNode.getIdentName())) {
            OutWrongInformation.addError(new Error(funcDefNode.getIdentLineNum(), ErrorType.b));
            /*deleteSymbol(funcDefNode.getIdentName());
            addFuncSymbol(funcDefNode.getIdentName(), 2, funcDefNode.getFuncType(), false, id, funcDefNode.getIdentLineNum(), funcDefNode.getFuncFParamNodes());
            /*fatherId = id;
            sum++;
            id = sum;
            tables.put(id, new SymbolTable(id, fatherId));
            functionSymbols.put(funcDefNode.getIdentName(), new FunctionSymbol(tables.get(fatherId).getNow(), fatherId, funcDefNode.getIdentName(), 2, funcDefNode.getFuncType(), false, id));
            */
            fatherId = id;
            sum++;
            id = sum;
            tables.put(id, new SymbolTable(id, fatherId));
//            functionSymbols.put(funcDefNode.getIdentName(), new FunctionSymbol(tables.get(fatherId).getNow(), fatherId, funcDefNode.getIdentName(), 2, funcDefNode.getFuncType(), false, id, funcDefNode.getFuncFParamNodes()));
            for (FuncFParamNode funcFParamNode : funcDefNode.getFuncFParamNodes()) {
                visitFuncFParamNode(funcFParamNode);
                System.out.print("@");
            }
            id = fatherId;
            sum--;
            fatherId = tables.get(id).getFatherId();
            isCheckingFuncReturn = (funcDefNode.getFuncType() != 2) ? true : false;
            if (isCheckingFuncReturn) {
                if (funcDefNode.getBlockNode().getBlockItemNodes().size() == 0) {
                    OutWrongInformation.addError(new Error(funcDefNode.getBlockNode().getLineNum(), ErrorType.g));
                } else {
                    BlockItemNode blockItemNode = funcDefNode.getBlockNode().getBlockItemNodes().get(funcDefNode.getBlockNode().getBlockItemNodes().size() - 1);
                    if (blockItemNode.isDecl()) {
                        ///****
                        OutWrongInformation.addError(new Error(funcDefNode.getBlockNode().getLineNum(), ErrorType.g));
                    } else if (blockItemNode.getStmtNode().getType() != 4){
                        OutWrongInformation.addError(new Error(funcDefNode.getBlockNode().getLineNum(), ErrorType.g));
                    }
                }
            }
            isCheckingVoidFunReturn = (funcDefNode.getFuncType() == 2) ? true : false;
            visitBlockNode(funcDefNode.getBlockNode());
            isCheckingFuncReturn = false;
            isCheckingVoidFunReturn = false;
        } else {
            addFuncSymbol(funcDefNode.getIdentName(), 2, funcDefNode.getFuncType(), false, id, funcDefNode.getIdentLineNum(), funcDefNode.getFuncFParamNodes());
            /*fatherId = id;
            sum++;
            id = sum;
            tables.put(id, new SymbolTable(id, fatherId));
            functionSymbols.put(funcDefNode.getIdentName(), new FunctionSymbol(tables.get(fatherId).getNow(), fatherId, funcDefNode.getIdentName(), 2, funcDefNode.getFuncType(), false, id));
            */
            fatherId = id;
            sum++;
            id = sum;
            tables.put(id, new SymbolTable(id, fatherId));
            functionSymbols.put(funcDefNode.getIdentName(), new FunctionSymbol(tables.get(fatherId).getNow(), fatherId, funcDefNode.getIdentName(), 2, funcDefNode.getFuncType(), false, id, funcDefNode.getFuncFParamNodes()));
            for (FuncFParamNode funcFParamNode : funcDefNode.getFuncFParamNodes()) {
                visitFuncFParamNode(funcFParamNode);
                System.out.print("@");
            }
            id = fatherId;
            sum--;
            fatherId = tables.get(id).getFatherId();
            isCheckingFuncReturn = (funcDefNode.getFuncType() != 2) ? true : false;
            if (isCheckingFuncReturn) {
                if (funcDefNode.getBlockNode().getBlockItemNodes().size() == 0) {
                    OutWrongInformation.addError(new Error(funcDefNode.getBlockNode().getLineNum(), ErrorType.g));
                } else {
                    BlockItemNode blockItemNode = funcDefNode.getBlockNode().getBlockItemNodes().get(funcDefNode.getBlockNode().getBlockItemNodes().size() - 1);
                    if (blockItemNode.isDecl()) {
                        ///****
                        OutWrongInformation.addError(new Error(funcDefNode.getBlockNode().getLineNum(), ErrorType.g));
                    } else if (blockItemNode.getStmtNode().getType() != 4){
                        OutWrongInformation.addError(new Error(funcDefNode.getBlockNode().getLineNum(), ErrorType.g));
                    }
                }
            }
            isCheckingVoidFunReturn = (funcDefNode.getFuncType() == 2) ? true : false;
            visitBlockNode(funcDefNode.getBlockNode());
            isCheckingFuncReturn = false;
            isCheckingVoidFunReturn = false;
        }
    }

    private void deleteSymbol(String identName) {
        SymbolTable tmpTable = tables.get(id);
        tmpTable.deleteSymbol(identName);
    }

    private void addFuncSymbol(String identName, int type, int bType, boolean isCon, Integer scopeId, int lineNum, ArrayList<FuncFParamNode> funcFParamNodes) {
        SymbolTable tmpTable = tables.get(id);
        tmpTable.addSymbol(new FunctionSymbol(tmpTable.getNow(), id, identName, type, bType, isCon, scopeId, funcFParamNodes), lineNum);
        return;
    }

    private void visitBlockNode(BlockNode blockNode) {
        System.out.println(1);
        if (blockNode == null) {
            System.out.println(2);
            return ;
        }
        fatherId = id;
        sum++;
        id = sum;
        if (!tables.containsKey(id)) {
            tables.put(id, new SymbolTable(id, fatherId));
        }
        for (BlockItemNode blockItemNode : blockNode.getBlockItemNodes()) {
            visitBlockItemNode(blockItemNode);
        }
        /*if (isCheckingFuncReturn && isNowBlock) {
            if (blockNode.getBlockItemNodes().size() == 0) {
                OutWrongInformation.addError(new Error(blockNode.getLineNum(), ErrorType.g));
            } else {
                BlockItemNode blockItemNode = blockNode.getBlockItemNodes().get(blockNode.getBlockItemNodes().size() - 1);
                if (blockItemNode.isDecl()) {
                    ///****
                    OutWrongInformation.addError(new Error(blockNode.getLineNum(), ErrorType.g));
                } else if (blockItemNode.getStmtNode().getType() != 4){
                    OutWrongInformation.addError(new Error(blockNode.getLineNum(), ErrorType.g));
                }
            }
        }*/
        id = tables.get(id).getFatherId();
        fatherId = tables.get(id).getFatherId();
    }

    private void visitBlockItemNode(BlockItemNode blockItemNode) {
        if (blockItemNode.isDecl()) {
            visitDeclNode(blockItemNode.getDeclNode());
        } else {
            visitStmtNode(blockItemNode.getStmtNode());
        }
    }

    private void visitStmtNode(StmtNode stmtNode) {
        switch (stmtNode.getType()) {
            case 1 :
                visitPrintf(stmtNode);
                break;
            case 2 :
                visitGetchar(stmtNode);
                break;
            case 3 :
                visitGetint(stmtNode);
                break;
            case 4 :
                visitReturn(stmtNode);
                break;
            case 5 :
                visitBreak(stmtNode);
                break;
            case 6 :
                visitContinue(stmtNode);
                break;
            case 7 :
                visitFor(stmtNode);
                break;
            case 8 :
                visitIf(stmtNode);
                break;
            case 9 :
                visitIfAndElse(stmtNode);
                break;
            case 10 :
                visitBlockNode(stmtNode.getBlockNode());
                break;
            case 11 :
                visitExpNode(stmtNode.getExpNode());
                break;
            case 12 :
                visitLval(stmtNode.getLValNode(), true);
                visitExpNode(stmtNode.getExpNode());
                break;
            default:
                break;
        }
    }

    private int visitExpNode(ExpNode expNode) {
        if (expNode == null) {
            return 0;
        }
        return visitAddExpNode(expNode.getAddExpNode());
    }

    private int visitAddExpNode(AddExpNode addExpNode) {
        int type = visitMulExpNode(addExpNode.getMulExpNode());
        if (addExpNode.getAddExpNode() != null) {
            type = max(visitAddExpNode(addExpNode.getAddExpNode()), type);
        }
        return type;
    }

    private int visitMulExpNode(MulExpNode mulExpNode) {
        int type = visitUnaryExpNode(mulExpNode.getUnaryExpNode());
        if (mulExpNode.getMulExpNode() != null) {
            TokenType op = mulExpNode.getOp();
            type = max(visitMulExpNode(mulExpNode.getMulExpNode()), type);
        }
        return type;
    }

    private int visitUnaryExpNode(UnaryExpNode unaryExpNode) {
        if (unaryExpNode.getPrimaryExpNode() != null) {
            return visitPrimaryExpNode(unaryExpNode.getPrimaryExpNode());
        } else if (unaryExpNode.getIdentNode( ) != null) {
            Symbol symbol = visitIdentNode(unaryExpNode.getIdentNode(), false);
            if (symbol == null) {
                return 0;
            }
            nowFunc = unaryExpNode.getIdentNode().getName();
            nowFuncLineNum = unaryExpNode.getIdentNode().getLineNum();
            if (unaryExpNode.getFuncRParamsNode() != null) {
                if (functionSymbols.get(unaryExpNode.getIdentNode().getName()).getSize()
                        != unaryExpNode.getFuncRParamsNode().getExpNodes().size()) {
                    //函数参数数量不匹配
                    OutWrongInformation.addError(new Error(unaryExpNode.getIdentNode().getLineNum(), ErrorType.d));
                }
                visitFuncRParamsNode(unaryExpNode.getFuncRParamsNode());
            } else {
                if (functionSymbols.get(unaryExpNode.getIdentNode().getName()).getSize() != 0) {
                    OutWrongInformation.addError(new Error(unaryExpNode.getIdentNode().getLineNum(), ErrorType.d));
                }
            }
            return (functionSymbols.get(unaryExpNode.getIdentNode().getName()).getbType() == 2) ? 0 : 1;
        } else if (unaryExpNode.getUnaryOpNode() != null) {
            return visitUnaryExpNode(unaryExpNode.getUnaryExpNode());
        }
        return 0;
    }

    private void visitFuncRParamsNode(FuncRParamsNode funcRParamsNode) {
        if (funcRParamsNode == null) {
            return ;
        }
        int i = 0;
        System.out.println("{" + nowFunc);
        String tmpFunc = nowFunc;
        for (ExpNode expNode : funcRParamsNode.getExpNodes()) {
            //对应参数类型
            int type = visitExpNode(expNode);
            System.out.println("type" + type);
            if (i > functionSymbols.get(tmpFunc).getFuncFParamNodes().size() - 1) {
                break;
            }
            switch (functionSymbols.get(tmpFunc).getFuncFParamNodes().get(i).getType()) {
                case 1 : //1:char
                    if (type != 1) {
                        OutWrongInformation.addError(new Error(nowFuncLineNum, ErrorType.e));
                        System.out.println(type + "!!!!!!!!!!!!!!!!!1");
                    }
                    break;
                case 2 : //2:int
                    if (type != 1) {
                        OutWrongInformation.addError(new Error(nowFuncLineNum, ErrorType.e));
                        System.out.println(type + "!!!!!!!!!!!!!!!!!2");
                    }
                    break;
                case 3 : //3:charArray
                    if (type != 3) {
                        OutWrongInformation.addError(new Error(nowFuncLineNum, ErrorType.e));
                        System.out.println(type + "!!!!!!!!!!!!!!!!!3" + "!" + type);
                    }
                    break;
                case 4 : //4:intArray
                    if (type != 2) {
                        OutWrongInformation.addError(new Error(nowFuncLineNum, ErrorType.e));
                        System.out.println(type + "!!!!!!!!!!!!!!!!!4");
                    }
                    break;
                default:
                    break;
            }
            i++;
        }
    }

    private int visitPrimaryExpNode(PrimaryExpNode primaryExpNode) {
        if (primaryExpNode.getExpNode() != null) {
            return visitExpNode(primaryExpNode.getExpNode());
        } else if (primaryExpNode.getLValNode() != null) {
            return visitLval(primaryExpNode.getLValNode(), false);
        } else if (primaryExpNode.getNumberNode() != null) {
            visitNumberNode(primaryExpNode.getNumberNode());
            return 1;
        } else if (primaryExpNode.getCharacterNode() != null) {
            visitCharacterNode(primaryExpNode.getCharacterNode());
            return 1;
        }
        return 0;
    }

    private void visitNumberNode(NumberNode numberNode) {
    }

    private void visitCharacterNode(CharacterNode characterNode) {
    }

    private void visitIf(StmtNode stmtNode) {
        visitCondNode(stmtNode.getCondNode());
        visitStmtNode(stmtNode.getIfStmtNode());
        //
    }

    private void visitCondNode(CondNode condNode) {
        visitLOrExpNode(condNode.getLOrExpNode());
    }

    private void visitLOrExpNode(LOrExpNode lOrExpNode) {
        visitLAndExpNode(lOrExpNode.getlAndExpNode());
        if (lOrExpNode.getlOrExpNode() != null) {
            visitLOrExpNode(lOrExpNode.getlOrExpNode());
        }
    }

    private void visitLAndExpNode(LAndExpNode lAndExpNode) {
        visitEqExpNode(lAndExpNode.getEqExpNode());
        if (lAndExpNode.getLAndExpNode() != null) {
            visitLAndExpNode(lAndExpNode.getLAndExpNode());
        }
    }

    private void visitEqExpNode(EqExpNode eqExpNode) {
        visitRelExpNode(eqExpNode.getRelExpNode());
        if (eqExpNode.getEqOrNot() != null) {
            visitEqExpNode(eqExpNode.getEqExpNode());
        }
    }

    private void visitRelExpNode(RelExpNode relExpNode) {
        visitAddExpNode(relExpNode.getAddExpNode());
        if (relExpNode.getRelType() != null) {
            visitRelExpNode(relExpNode.getRelExpNode());
        }
    }

    private void visitIfAndElse(StmtNode stmtNode) {
        visitCondNode(stmtNode.getCondNode());
        visitStmtNode(stmtNode.getIfStmtNode());
        visitStmtNode(stmtNode.getElseStmtNode());
    }

    private void visitFor(StmtNode stmtNode) {
        loopCount = loopCount + 1;
        //...
        visitForStmtNode(stmtNode.getForLeftStmtNode());
        if (stmtNode.getCondNode() != null) {
            visitCondNode(stmtNode.getCondNode());
        }
        visitForStmtNode(stmtNode.getForRightStmtNode());
        visitStmtNode(stmtNode.getForStmtNode());
        loopCount = loopCount - 1;
    }

    private void visitForStmtNode(ForStmtNode forLeftStmtNode) {
        if (forLeftStmtNode == null) {
            return;
        }
        visitLval(forLeftStmtNode.getlValNode(), true);
        visitExpNode(forLeftStmtNode.getExpNode());
    }

    private void visitBreak(StmtNode stmtNode) {
        if (loopCount == 0) {
            OutWrongInformation.addError(new Error(stmtNode.getLineNum(), ErrorType.m));
        }
        ///......
    }

    private void visitContinue(StmtNode stmtNode) {
        if (loopCount == 0) {
            OutWrongInformation.addError(new Error(stmtNode.getLineNum(), ErrorType.m));
        }
        ///......
    }

    private void visitReturn(StmtNode stmtNode) {
        if (isCheckingVoidFunReturn && stmtNode.getExpNode() != null) {
            OutWrongInformation.addError(new Error(stmtNode.getLineNum(), ErrorType.f));
        }
        ExpNode expNode = stmtNode.getExpNode();
        visitExpNode(expNode);
        //语义分析return语句
    }

    private void visitGetint(StmtNode stmtNode) {
        visitLval(stmtNode.getLValNode(), true);

    }

    private int visitLval(LValNode lValNode, boolean atLeftSide) {
        Symbol symbol = visitIdentNode(lValNode.getIdentNode(), atLeftSide);
        if (lValNode.getExpNode() != null) {
            visitExpNode(lValNode.getExpNode());
        }
        /*if (isCon && bType == 1 && type == 0) {
            return "ConstChar";
        } else if (isCon && bType == 0 && type == 0) {
            return "ConstInt";
        } else if (isCon && bType == 1 && type == 1) {
            return  "ConstCharArray";
        } else if (isCon && bType == 0 && type == 1) {
            return  "ConstIntArray";
        } else if (!isCon && bType == 1 && type == 0) {
            return "Char";
        } else if (!isCon && bType == 0 && type == 0) {
            return "Int";
        } else if (!isCon && bType == 1 && type == 1) {
            return "CharArray";
        } else if (!isCon && bType == 0 && type == 1) {
            return "IntArray";
        } else if (bType == 2 && type == 2) {
            return "VoidFunc";
        } else if (bType == 1 && type == 2) {
            return "CharFunc";
        } else if (bType == 0 && type == 2) {
            return "IntFunc";
        } else {
            return "WrongType";
        }*/
        if (symbol == null) {
            return 0;
        }
        System.out.println("{{{{{{" + symbol.typeToString() +symbol.getbType()+symbol.getType() + (lValNode.getExpNode() == null));
        if (symbol.getbType() == 0 && symbol.getType() == 1 && lValNode.getExpNode() == null) {//int[],a[],a
            return 2;
        } else if (symbol.getbType() == 1 && symbol.getType() == 1 && lValNode.getExpNode() == null) {
            return 3;
        } else if ((symbol.getbType() == 1 && symbol.getType() == 0)
                || (symbol.getbType() == 0 && symbol.getType() == 0)) {
            return 1;
        } else if (symbol.getbType() == 0 && symbol.getType() == 1 && lValNode.getExpNode() != null) {//a[1]
            return 1;
        } else if (symbol.getbType() == 1 && symbol.getType() == 1 && lValNode.getExpNode() != null) {
            return 1;
        }
        return 0;
    }

    private Symbol visitIdentNode(IdentNode identNode, boolean atLeftSide) {
        int tmp = id;
        do {
            //System.out.println(":::" + tmp);
            Symbol symbol = tables.get(tmp).findSymbol(identNode.getName());
            System.out.println(identNode.getName() + ":");
            if (symbol != null) {
                if (atLeftSide && symbol.isCon()) {
                    OutWrongInformation.addError(new Error(identNode.getLineNum(), ErrorType.h));
                }
                return symbol;
            }
            tmp = tables.get(tmp).getFatherId();
        } while (tmp != 0);
        //没找到
        OutWrongInformation.addError(new Error(identNode.getLineNum(), ErrorType.c));
        return null;
    }

    private void visitGetchar(StmtNode stmtNode) {
        //分析标识符是否在表中
        visitLval(stmtNode.getLValNode(), true);
        //OutWrongInformation.addError(new Error(stmtNode.getIdent().getLineNum(), ErrorType.c));
    }

    private void visitPrintf(StmtNode stmtNode) {
        //检查printf参数对应数量是否匹配
        int num = stmtNode.getPrintfExpNodes().size();
        if (stmtNode.getStringConstNode().getNum() != num) {
            OutWrongInformation.addError(new Error(stmtNode.getLineNum(), ErrorType.l));
        }
        for (ExpNode expNode : stmtNode.getPrintfExpNodes()) {
            visitExpNode(expNode);
        }
    }


    private void visitFuncFParamNode(FuncFParamNode funcFParamNode) {
        addSymbol(funcFParamNode.getName(), funcFParamNode.isArray() ? 1 : 0, funcFParamNode.getBType(), false, null, funcFParamNode.getIdentLineNum());
    }

    private void visitDeclNode(DeclNode declNode) {
        if (declNode.getType() == 1) {
            visitConstDeclNode(declNode.getConstDeclNode());
        } else {
            visitVarDeclNode(declNode.getVarDeclNode());
        }
    }

    private void visitVarDeclNode(VarDeclNode varDeclNode) {
        for (VarDefNode varDefNode : varDeclNode.getVarDefNodes()) {
            addSymbol(varDefNode.getIentName(), varDefNode.isArray() ? 1 : 0, varDeclNode.getType(), false, null, varDefNode.getIdentLineNum());
            visitVarDefNode(varDefNode);
        }
    }

    private void visitVarDefNode(VarDefNode varDefNode) {
        if (varDefNode.getConstExpNode() != null) {
            visitAddExpNode(varDefNode.getConstExpNode().getAddExpNode());
        }
        if (varDefNode.getInitValNode() != null) {
            visitInitValNode(varDefNode.getInitValNode());
        }
    }

    private void visitInitValNode(InitValNode initValNode) {
        if (initValNode.getExpNode() != null) {
            visitExpNode(initValNode.getExpNode());
        } else if (initValNode.getExpNodes() != null) {
            for (ExpNode expNode : initValNode.getExpNodes()) {
                visitExpNode(expNode);
            }
        } else {
            //String;
        }
    }

    private void visitConstDeclNode(ConstDeclNode constDeclNode) {
        for (ConstDefNode constDefNode : constDeclNode.getConstDefNodes()) {
            //判断是否为ARRAY;
            addSymbol(constDefNode.getIdentName(), constDefNode.isArray() ? 1 : 0, constDeclNode.getType(), true, null, constDefNode.getIdentLineNum());
            visitConstDefNode(constDefNode);
        }
    }

    private void visitConstDefNode(ConstDefNode constDefNode) {
        if (constDefNode.getConstExpNode() != null) {
            visitAddExpNode(constDefNode.getConstExpNode().getAddExpNode());
        }
        visitConstInitValNode(constDefNode.getConstInitValNode());
    }

    private void visitConstInitValNode(ConstInitValNode constInitValNode) {
        if (constInitValNode.getConstExpNode() != null) {
            visitAddExpNode(constInitValNode.getConstExpNode().getAddExpNode());
        } else if (constInitValNode.getConstExpNodes() != null) {
            for (ConstExpNode constExpNode : constInitValNode.getConstExpNodes()) {
                visitAddExpNode(constExpNode.getAddExpNode());
            }
        } else {
            //处理String串
        }
    }

    private void addSymbol(String identName, int type, int bType, boolean isCon, Integer scopeId, int lineNum) {
        if (type == 1) {
            SymbolTable tmpTable = tables.get(id);
            if (identName.equals("str")) {
                System.out.println("str" +"======" +new ArraySymbol(tmpTable.getNow(), id, identName, type, bType, isCon).typeToString());
            }
            tmpTable.addSymbol(new ArraySymbol(tmpTable.getNow(), id, identName, type, bType, isCon), lineNum);
            return;
        }

        tables.get(id).addSymbol(new Symbol(tables.get(id).getNow(), id, identName, type, bType, isCon), lineNum);
    }

    public void printSymbolTable(BufferedWriter symbolWriter) throws IOException {
        for (int i = 1; i <= sum; i++) {
            tables.get(i).print(i, symbolWriter);
        }
    }
}

