package midend;

import error.Error;
import error.ErrorType;
import frontend.OutWrongInformation;
import frontend.Token;
import frontend.TokenType;
import frontend.symbolTableManagement.ArraySymbol;
import frontend.symbolTableManagement.FunctionSymbol;
import frontend.symbolTableManagement.Symbol;
import frontend.symbolTableManagement.SymbolTable;
import midend.type.*;
import midend.value.*;
import midend.value.instructions.*;
import node.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;

import static java.lang.Math.max;

public class Visitor {
    private CompUnitNode compUnitNode;
    private SymbolTable table = new SymbolTable(1, 0);
    private int id = 1;
    private int fatherId = 0;
    private int sum = 1;
    private HashMap<Integer, SymbolTable> tables = new HashMap<>();
    private HashMap<String, FunctionSymbol> functionSymbols = new HashMap<>();
    private HashMap<String, Type> funcNameMapReturnType = new HashMap<>();
    private int loopCount = 0;
    private boolean isCheckingFuncReturn = false;
    private boolean isCheckingVoidFunReturn = false;
    private String nowFunc;//用于检验当前函数实例的参数和行数
    private int nowFuncLineNum;
    private boolean isNowBlock = false;
    private String lastFunc;

    private boolean curForContinue = false;
    private HashMap<String, FunctionType> funcNameMapFuncType = new HashMap<>();
    private ArrayList<Value> curValuesForDefArray;
    private ConstArray curConstArray;//用于声明常量数组
    private boolean isChar = false;
    private Value curValue;//记录当前visit访问计算到的Value
    private Value curIdent;
    private int curInteger; //
    private BasicBlock curBasicBlock;
    private Function curFunc;
    private HashMap<Integer, HashMap<String, Value>> symbolTables = new HashMap<>();

    private boolean isVirtualBlock = false;
    private ArrayList<Value> curRealArguments = new ArrayList<>();//调用函数
    private ArrayList<Type> curArgumentsTypes = new ArrayList<>();//函数定义时使用
    private boolean isGlobal = false;
    private boolean isConst = false;
    private boolean isDecl= false;//表示在定义初值；

    private boolean existTerminate = false;
    private BasicBlock curElseBasicBlock;
    private BasicBlock curIfBasicBlock;
    private BasicBlock curBreakBasicBlock;
    private BasicBlock curContinueBasicBlock;
    private boolean curForBreak = false;
    private BasicBlock shouldGo;

    public Visitor(CompUnitNode compUnitNode) {
        this.compUnitNode = compUnitNode;
        tables.put(1, table);
        symbolTables.put(1, new HashMap<>());
        visitCompUnitNode(this.compUnitNode);
    }

    private void visitCompUnitNode(CompUnitNode node) {
        isGlobal = true;
        for (DeclNode declNode : node.getDeclNodes()) {
            visitDeclNode(declNode);
        }
        isGlobal = false;
        //引入库函数
        Function getint = new Function(new FunctionType(new IntegerType(32), new ArrayList<>()), "@getint", true);
        funcNameMapFuncType.put("getint", new FunctionType(new IntegerType(32), new ArrayList<>()));
        midend.value.Module.getInstance().addFunc(getint);

        Function getchar = new Function(new FunctionType(new IntegerType(8), new ArrayList<>()), "@getchar", true);
        funcNameMapFuncType.put("getchar", new FunctionType(new IntegerType(8), new ArrayList<>()));
        midend.value.Module.getInstance().addFunc(getchar);

        ArrayList<Type> putIntArgument = new ArrayList<>();
        putIntArgument.add(new IntegerType(32));
        Function putint = new Function(new FunctionType(new VoidType(), putIntArgument), "@putint", true);
        funcNameMapFuncType.put("putint", new FunctionType(new VoidType(), putIntArgument));
        midend.value.Module.getInstance().addFunc(putint);

        ArrayList<Type> putChArgument = new ArrayList<>();
        putChArgument.add(new IntegerType(32));
        Function putch = new Function(new FunctionType(new VoidType(), putChArgument), "@putch", true);
        funcNameMapFuncType.put("putch", new FunctionType(new VoidType(), putChArgument));
        midend.value.Module.getInstance().addFunc(putch);

        ArrayList<Type> putStrArgument = new ArrayList<>();
        putStrArgument.add(new PointerType(new IntegerType(8)));
        Function putstr = new Function(new FunctionType(new VoidType(), putStrArgument), "@putstr", true);
        funcNameMapFuncType.put("putstr", new FunctionType(new VoidType(), putStrArgument));
        midend.value.Module.getInstance().addFunc(putstr);

        for (FuncDefNode funcDefNode : node.getFuncDefNodes()) {
            visitFuncDefNode(funcDefNode);
        }
        visitMainFuncDefNode(node.getMainFuncDefNode());
    }

    private void visitMainFuncDefNode(MainFuncDefNode mainFuncDefNode) {
        curFunc = BuildValue.buildFunction("@main", new FunctionType(new IntegerType(32), new ArrayList<>()));
        curBasicBlock = BuildValue.buildBasicBlock(curFunc);
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
        curFunc = null;
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
            fatherId = id;
            sum++;
            id = sum;
            tables.put(id, new SymbolTable(id, fatherId));
            symbolTables.put(id, new HashMap<>());
            functionSymbols.put(funcDefNode.getIdentName(), new FunctionSymbol(tables.get(fatherId).getNow(), fatherId, funcDefNode.getIdentName(), 2, funcDefNode.getFuncType(), false, id, funcDefNode.getFuncFParamNodes()));
            curArgumentsTypes = new ArrayList<>();
            for (FuncFParamNode funcFParamNode : funcDefNode.getFuncFParamNodes()) {
                visitFuncFParamNode(funcFParamNode);
            }
            ArrayList<Type> argumentsTypes = new ArrayList<>();
            for (Type type : curArgumentsTypes) {
                argumentsTypes.add(type);
            }
            FunctionType functionType;
//            /*if (funType.equals("INTTK")) {
//            return 0;
//        } else if (funType.equals("CHARTK")) {
//            return 1;
//        } else {
//            return 2;
//        }*/
            if (funcDefNode.getFuncType() == 0) {
                functionType= new FunctionType(new IntegerType(32), argumentsTypes);
            } else if (funcDefNode.getFuncType() == 1) {
                functionType= new FunctionType(new IntegerType(8), argumentsTypes);
            } else {
                functionType= new FunctionType(new VoidType(), argumentsTypes);
            }
            funcNameMapReturnType.put(funcDefNode.getIdentName(), functionType.getReturnType());
            funcNameMapFuncType.put(funcDefNode.getIdentName(), functionType);
//            创造一个函数value
            Function function = BuildValue.buildFunciton("@" + funcDefNode.getIdentName(), functionType);
            curFunc = function;
            curBasicBlock = BuildValue.buildBasicBlock(curFunc);
//            创建函数参数，alloca，并store
            for (int i = 0; i < argumentsTypes.size(); i++) {
                if (argumentsTypes.get(i) instanceof IntegerType) {
                    Alloca alloca = BuildValue.buildAlloca(argumentsTypes.get(i), curBasicBlock);
                    BuildValue.buildStore(curBasicBlock, curFunc.getArguments().get(i), alloca);
                    symbolTables.get(id).put(funcDefNode.getFuncFParamNodes().get(i).getName(), alloca);
                } else {
                    Alloca alloca = BuildValue.buildAlloca(new PointerType(((PointerType)argumentsTypes.get(i)).getPointedType()), curBasicBlock);
                    BuildValue.buildStore(curBasicBlock, curFunc.getArguments().get(i), alloca);
                    symbolTables.get(id).put(funcDefNode.getFuncFParamNodes().get(i).getName(), alloca);
                }
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
            if (curFunc.getFunctionType().getReturnType() instanceof VoidType) {
                if (curBasicBlock.getInstructions().size() == 0) {
                    BuildValue.buildRetVoid(curBasicBlock);
                } else if (!(curBasicBlock.getInstructions().get(curBasicBlock.getInstructions().size() - 1) instanceof Ret)) {
                    BuildValue.buildRetVoid(curBasicBlock);
                }
            }
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
        if (blockNode == null) {
            return ;
        }
        fatherId = id;
        sum++;
        id = sum;
        if (!tables.containsKey(id)) {
            tables.put(id, new SymbolTable(id, fatherId));
            symbolTables.put(id, new HashMap<>());
        }
        for (BlockItemNode blockItemNode : blockNode.getBlockItemNodes()) {
            visitBlockItemNode(blockItemNode);
            if (curForContinue) {
                break;
            }
            if (curForBreak) {
                break;
            }
        }
        if (curForContinue) {
            curForContinue = false;
        }
        if (curForBreak) {
            curForBreak = false;
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
                if (!isVirtualBlock) {
                    curForBreak = true;
                }
                visitBreak(stmtNode);
                break;
            case 6 :
                if (!isVirtualBlock) {
                    curForContinue = true;
                }
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
                Value tmpVal = curValue;
                visitExpNode(stmtNode.getExpNode());
                BuildValue.buildStore(curBasicBlock, curValue, tmpVal);
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
        if (isConst || isGlobal) {
            int tmpInteger = curInteger;
            System.out.println("(/"+tmpInteger);
            AddExpNode tmpNode = addExpNode.getAddExpNode();
            TokenType op = addExpNode.getOp();
            while (tmpNode != null) {
                type = max(visitMulExpNode(tmpNode.getMulExpNode()), type);
                if (op.equals(TokenType.PLUS)) {
                    curInteger = tmpInteger + curInteger;
                } else {
                    curInteger = tmpInteger - curInteger;
                }
                op = tmpNode.getOp();
                tmpNode = tmpNode.getAddExpNode();
                tmpInteger = curInteger;
                System.out.println("(/"+tmpInteger);
            }
            curValue = new ConstantInt(isChar?8:32, curInteger);
        } else {
            AddExpNode tmpNode = addExpNode.getAddExpNode();
            TokenType op = addExpNode.getOp();
            while (tmpNode != null) {
                Value tmpValue= curValue;
                type = max(visitMulExpNode(tmpNode.getMulExpNode()), type);
                if (op.equals(TokenType.PLUS)) {
                    curValue = BuildValue.buildAdd(curBasicBlock, tmpValue, curValue);
                } else {
                    curValue = BuildValue.buildSub(curBasicBlock, tmpValue, curValue);
                }
                op = tmpNode.getOp();
                tmpNode = tmpNode.getAddExpNode();
            }
        }
        return type;
    }

    private int visitMulExpNode(MulExpNode mulExpNode) {
        int type = visitUnaryExpNode(mulExpNode.getUnaryExpNode());
        if (isConst || isGlobal) {
            int tmpInteger = curInteger;
            MulExpNode tmpNode = mulExpNode.getMulExpNode();
            TokenType op = mulExpNode.getOp();
            while (tmpNode!= null) {
                type = max(visitUnaryExpNode(tmpNode.getUnaryExpNode()), type);
                if (op.equals(TokenType.MULT)) {
                    curInteger = tmpInteger * curInteger;
                } else if (op.equals(TokenType.DIV)) {
                    System.out.println(curInteger);
                    curInteger = tmpInteger / curInteger;
                    System.out.println(curInteger);
                } else {
                    curInteger = tmpInteger % curInteger;
                }
                op = tmpNode.getOp();
                tmpNode = tmpNode.getMulExpNode();
                tmpInteger = curInteger;
            }
        } else {
            MulExpNode tmpNode = mulExpNode.getMulExpNode();
            TokenType op = mulExpNode.getOp();
            while (tmpNode != null) {
                Value tmpValue = curValue;
                type = max(visitUnaryExpNode(tmpNode.getUnaryExpNode()), type);
                if (op.equals(TokenType.MULT)) {
                    curValue = BuildValue.buildMul(curBasicBlock, tmpValue, curValue);
                } else if (op.equals(TokenType.DIV)) {
                    curValue = BuildValue.buildSDiv(curBasicBlock, tmpValue, curValue);
                } else {
                    curValue = BuildValue.buildSRem(curBasicBlock, tmpValue, curValue);
                }
                op = tmpNode.getOp();
                tmpNode = tmpNode.getMulExpNode();
            }
        }
        return type;
    }

    private int visitUnaryExpNode(UnaryExpNode unaryExpNode) {
        //一元表达式 UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp
        if (unaryExpNode.getPrimaryExpNode() != null) {
            return visitPrimaryExpNode(unaryExpNode.getPrimaryExpNode());
        } else if (unaryExpNode.getIdentNode( ) != null) {
            Symbol symbol = visitIdentNode(unaryExpNode.getIdentNode(), false);
            if (symbol == null) {
                return 0;
            }
            lastFunc = nowFunc;
            nowFunc = unaryExpNode.getIdentNode().getName();
            nowFuncLineNum = unaryExpNode.getIdentNode().getLineNum();
            if (unaryExpNode.getFuncRParamsNode() != null) {
                if (functionSymbols.get(unaryExpNode.getIdentNode().getName()).getSize()
                        != unaryExpNode.getFuncRParamsNode().getExpNodes().size()) {
                    //函数参数数量不匹配
                    OutWrongInformation.addError(new Error(unaryExpNode.getIdentNode().getLineNum(), ErrorType.d));
                }
                visitFuncRParamsNode(unaryExpNode.getFuncRParamsNode());
                Call call = BuildValue.buildCall(curBasicBlock, funcNameMapReturnType.get(nowFunc),"@" + nowFunc, curRealArguments, funcNameMapFuncType.get(nowFunc));
                curRealArguments = null;
                curValue = call;
            } else {
                Call call = BuildValue.buildCall(curBasicBlock, funcNameMapReturnType.get(nowFunc),"@" + nowFunc, new ArrayList<>(), funcNameMapFuncType.get(nowFunc));
                curValue = call;
                if (functionSymbols.get(unaryExpNode.getIdentNode().getName()).getSize() != 0) {
                    OutWrongInformation.addError(new Error(unaryExpNode.getIdentNode().getLineNum(), ErrorType.d));
                }
            }
            nowFunc = lastFunc;
            return (functionSymbols.get(unaryExpNode.getIdentNode().getName()).getbType() == 2) ? 0 : 1;
        } else if (unaryExpNode.getUnaryOpNode() != null) {
            int highType = visitUnaryExpNode(unaryExpNode.getUnaryExpNode());
            if (isConst || isGlobal) {
                if (unaryExpNode.getUnaryOpNode().getOp().equals(TokenType.PLUS)) {
                    curInteger = curInteger;
                } else if (unaryExpNode.getUnaryOpNode().getOp().equals(TokenType.MINU)) {
                    curInteger = -curInteger;
                } else {
                    curInteger = (curInteger == 0) ? 1 : 0;
                }
            } else {
                if (unaryExpNode.getUnaryOpNode().getOp().equals(TokenType.PLUS)) {
                    curValue = BuildValue.buildAdd(curBasicBlock, new ConstantInt(32, 0), curValue);
                } else if (unaryExpNode.getUnaryOpNode().getOp().equals(TokenType.MINU)) {
                    curValue = BuildValue.buildSub(curBasicBlock, new ConstantInt(32, 0), curValue);
                } else {
                    curValue = BuildValue.buildIcmp(curBasicBlock, IcmpType.EQ, curValue, new ConstantInt(1, 0));
                }
            }
            return highType;
        }
        return 0;
    }

    private void visitFuncRParamsNode(FuncRParamsNode funcRParamsNode) {
        ArrayList<Value> realArguments = new ArrayList<>();
        if (funcRParamsNode == null) {
            return ;
        }
        int i = 0;

        for (ExpNode expNode : funcRParamsNode.getExpNodes()) {
            //对应参数类型
            int type = visitExpNode(expNode);
            realArguments.add(curValue);
            if (i > functionSymbols.get(nowFunc).getFuncFParamNodes().size() - 1) {
                break;
            }
            switch (functionSymbols.get(nowFunc).getFuncFParamNodes().get(i).getType()) {
                case 1 : //1:char
                    if (type != 1) {
                        OutWrongInformation.addError(new Error(nowFuncLineNum, ErrorType.e));
                    }
                    break;
                case 2 : //2:int
                    if (type != 1) {
                        OutWrongInformation.addError(new Error(nowFuncLineNum, ErrorType.e));
                    }
                    break;
                case 3 : //3:charArray
                    if (type != 3) {
                        OutWrongInformation.addError(new Error(nowFuncLineNum, ErrorType.e));
                    }
                    break;
                case 4 : //4:intArray
                    if (type != 2) {
                        OutWrongInformation.addError(new Error(nowFuncLineNum, ErrorType.e));
                    }
                    break;
                default:
                    break;
            }
            i++;
        }
        curRealArguments = realArguments;
    }

    private int visitPrimaryExpNode(PrimaryExpNode primaryExpNode) {
        //'(' Exp ')' | LVal | Number | Character
        if (primaryExpNode.getExpNode() != null) {
            return visitExpNode(primaryExpNode.getExpNode());
        } else if (primaryExpNode.getLValNode() != null) {
            return visitLval(primaryExpNode.getLValNode(), false);
        } else if (primaryExpNode.getNumberNode() != null) {
            visitNumberNode(primaryExpNode.getNumberNode());
            curInteger = primaryExpNode.getNumberNode().getInteger();
            curValue = BuildValue.buildConstantInt(isChar? 8 : 32, curInteger);
            return 1;
        } else if (primaryExpNode.getCharacterNode() != null) {
            visitCharacterNode(primaryExpNode.getCharacterNode());
            curInteger = primaryExpNode.getCharacterNode().getInteger();
            curValue = BuildValue.buildConstantInt(8, curInteger);
            return 1;
        }
        return 0;
    }

    private void visitNumberNode(NumberNode numberNode) {
    }

    private void visitCharacterNode(CharacterNode characterNode) {
    }

    private void visitIf(StmtNode stmtNode) {
        BasicBlock ifBasicBlock = BuildValue.buildBasicBlock(curFunc);
        BasicBlock nextBasicBlock = BuildValue.buildBasicBlock(curFunc);
        curIfBasicBlock = ifBasicBlock;
        curElseBasicBlock = nextBasicBlock;
        visitCondNode(stmtNode.getCondNode());
        curBasicBlock = ifBasicBlock;
        if (stmtNode.getIfStmtNode().getType() != 10) {
            isVirtualBlock = true;
        }
        visitStmtNode(stmtNode.getIfStmtNode());
        isVirtualBlock = false;
        if (!curBasicBlock.getInstructions().isEmpty()) {
            if (curBasicBlock.getInstructions().get(curBasicBlock.getInstructions().size() - 1) instanceof Ret) {
                existTerminate = true;
            } else if (curBasicBlock.getInstructions().get(curBasicBlock.getInstructions().size() - 1) instanceof Br) {
                existTerminate = true;
            } else {
                BuildValue.buildBr(curBasicBlock, nextBasicBlock);
            }
        } else {
            BuildValue.buildBr(curBasicBlock, nextBasicBlock);
        }
        curBasicBlock = nextBasicBlock;
    }

    private void visitCondNode(CondNode condNode) {
        visitLOrExpNode(condNode.getLOrExpNode());
    }

    private void visitLOrExpNode(LOrExpNode lOrExpNode) {
        BasicBlock ifBasicBlock = curIfBasicBlock;
        BasicBlock elseBasicBlock = curElseBasicBlock;
        if (lOrExpNode.getlOrExpNode() != null) {
            LOrExpNode tmpNode = lOrExpNode;
            do {
                BasicBlock nextBasicBlock = BuildValue.buildBasicBlock(curFunc);
                curElseBasicBlock = nextBasicBlock;
                visitLAndExpNode(tmpNode.getlAndExpNode());
                curBasicBlock = nextBasicBlock;
                tmpNode = tmpNode.getlOrExpNode();
            } while (tmpNode.getlOrExpNode() != null);
            curElseBasicBlock = elseBasicBlock;
            visitLAndExpNode(tmpNode.getlAndExpNode());
            curBasicBlock = elseBasicBlock;
            /*BasicBlock nextBasicBlock1 = BuildValue.buildBasicBlock(curFunc);
            curElseBasicBlock = nextBasicBlock1;
            visitLAndExpNode(lOrExpNode.getlAndExpNode());
            curBasicBlock = nextBasicBlock1;
            LOrExpNode tmpNode = lOrExpNode.getlOrExpNode();
            while (tmpNode != null) {
                BasicBlock nextBasicBlock = BuildValue.buildBasicBlock(curFunc);
                curElseBasicBlock = nextBasicBlock;
                visitLAndExpNode(tmpNode.getlAndExpNode());
                curBasicBlock = nextBasicBlock;
                tmpNode = tmpNode.getlOrExpNode();
            }*/
        } else {
            visitLAndExpNode(lOrExpNode.getlAndExpNode());
        }
        curIfBasicBlock = ifBasicBlock;
        curElseBasicBlock = elseBasicBlock;
    }

    private void visitLAndExpNode(LAndExpNode lAndExpNode) {
        BasicBlock ifBasicBlock = curIfBasicBlock;
        BasicBlock elseBasicBlock = curElseBasicBlock;
        if (lAndExpNode.getLAndExpNode() != null) {
            LAndExpNode tmpNode = lAndExpNode;
            do {
                BasicBlock nextBasicBlock = BuildValue.buildBasicBlock(curFunc);
                visitEqExpNode(tmpNode.getEqExpNode());
                curValue = BuildValue.buildIcmp(curBasicBlock, IcmpType.NE, curValue, new ConstantInt(1, 0));
                BuildValue.buildBr(curBasicBlock, curValue, nextBasicBlock, elseBasicBlock);
                curBasicBlock = nextBasicBlock;
                tmpNode = tmpNode.getLAndExpNode();
            } while (tmpNode.getLAndExpNode() != null);
            curElseBasicBlock = elseBasicBlock;
            visitEqExpNode(tmpNode.getEqExpNode());
            curValue = BuildValue.buildIcmp(curBasicBlock, IcmpType.NE, curValue, new ConstantInt(1, 0));
            BuildValue.buildBr(curBasicBlock, curValue, ifBasicBlock, elseBasicBlock);
            curBasicBlock = elseBasicBlock;
            /*
            BasicBlock nextBasicBlock1 = BuildValue.buildBasicBlock(curFunc);
            visitEqExpNode(lAndExpNode.getEqExpNode());
            curValue = BuildValue.buildIcmp(curBasicBlock, IcmpType.NE, curValue, new ConstantInt(32, 0));
            BuildValue.buildBr(curBasicBlock, curValue, nextBasicBlock1, elseBasicBlock);
            curBasicBlock = nextBasicBlock1;
            LAndExpNode tmpNode = lAndExpNode.getLAndExpNode();
            while (tmpNode != null) {
                BasicBlock nextBasicBlock = BuildValue.buildBasicBlock(curFunc);
                visitEqExpNode(tmpNode.getEqExpNode());
                curValue = BuildValue.buildIcmp(curBasicBlock, IcmpType.NE, curValue, new ConstantInt(32, 0));
                BuildValue.buildBr(curBasicBlock, curValue, nextBasicBlock, elseBasicBlock);
                curBasicBlock = nextBasicBlock;
                tmpNode = tmpNode.getLAndExpNode();
            }*/
        } else {
            visitEqExpNode(lAndExpNode.getEqExpNode());
            curValue = BuildValue.buildIcmp(curBasicBlock, IcmpType.NE, curValue, new ConstantInt(1, 0));
            //如果为假，跳转到nextbb,否则跳转到ifbb
            BuildValue.buildBr(curBasicBlock, curValue, ifBasicBlock, elseBasicBlock);
        }
        curIfBasicBlock = ifBasicBlock;
        curElseBasicBlock = elseBasicBlock;
    }

    private void visitEqExpNode(EqExpNode eqExpNode) {
        visitRelExpNode(eqExpNode.getRelExpNode());
        if (eqExpNode.getEqOrNot() != null) {
            TokenType type = eqExpNode.getEqOrNot();
            EqExpNode tmpNode = eqExpNode.getEqExpNode();
            while (tmpNode != null) {
                Value tmpValue = curValue;
                visitRelExpNode(tmpNode.getRelExpNode());
                if (type.equals(TokenType.NEQ)) {
                    curValue = BuildValue.buildIcmp(curBasicBlock, IcmpType.NE, tmpValue, curValue);
                } else {
                    curValue = BuildValue.buildIcmp(curBasicBlock, IcmpType.EQ, tmpValue, curValue);
                }
                type = tmpNode.getEqOrNot();
                tmpNode = tmpNode.getEqExpNode();
            }
        }
    }

    private void visitRelExpNode(RelExpNode relExpNode) {
        visitAddExpNode(relExpNode.getAddExpNode());
        if (relExpNode.getRelType() != null) {
            TokenType type = relExpNode.getRelType();
            RelExpNode tmpNode = relExpNode.getRelExpNode();
            while (tmpNode != null) {
                Value tmpValue = curValue;
                visitAddExpNode(tmpNode.getAddExpNode());
                if (type.equals(TokenType.LSS)) {
                    curValue = BuildValue.buildIcmp(curBasicBlock, IcmpType.SLT, tmpValue, curValue);
                } else if (type.equals(TokenType.LEQ)) {
                    curValue = BuildValue.buildIcmp(curBasicBlock, IcmpType.SLE, tmpValue, curValue);
                } else if (type.equals(TokenType.GRE)) {
                    curValue = BuildValue.buildIcmp(curBasicBlock, IcmpType.SGT, tmpValue, curValue);
                } else if (type.equals(TokenType.GEQ)) {
                    curValue = BuildValue.buildIcmp(curBasicBlock, IcmpType.SGE, tmpValue, curValue);
                }
                type = tmpNode.getRelType();
                tmpNode = tmpNode.getRelExpNode();
            }
        }
    }

    private void visitIfAndElse(StmtNode stmtNode) {
        BasicBlock ifBasicBlock = BuildValue.buildBasicBlock(curFunc);
        BasicBlock elseBasicBlock = BuildValue.buildBasicBlock(curFunc);
        curIfBasicBlock = ifBasicBlock;
        curElseBasicBlock = elseBasicBlock;
        BasicBlock nextBasicBlock = BuildValue.buildBasicBlock(curFunc);
        visitCondNode(stmtNode.getCondNode());

        curBasicBlock = ifBasicBlock;
        if (stmtNode.getIfStmtNode().getType() != 10) {
            isVirtualBlock = true;
        }
        visitStmtNode(stmtNode.getIfStmtNode());
        isVirtualBlock = false;
        if (!curBasicBlock.getInstructions().isEmpty()) {
            if (curBasicBlock.getInstructions().get(curBasicBlock.getInstructions().size() - 1) instanceof Ret) {
                existTerminate = true;
            } else if (curBasicBlock.getInstructions().get(curBasicBlock.getInstructions().size() - 1) instanceof Br) {
                existTerminate = true;
            } else {
                BuildValue.buildBr(curBasicBlock, nextBasicBlock);
            }
        } else {
            BuildValue.buildBr(curBasicBlock, nextBasicBlock);
        }

        curBasicBlock = elseBasicBlock;
        if (stmtNode.getElseStmtNode().getType() != 10) {
            isVirtualBlock = true;
        }
        visitStmtNode(stmtNode.getElseStmtNode());
        isVirtualBlock = false;
        if (!curBasicBlock.getInstructions().isEmpty()) {
            if (curBasicBlock.getInstructions().get(curBasicBlock.getInstructions().size() - 1) instanceof Ret) {
                existTerminate = true;
            } else if (curBasicBlock.getInstructions().get(curBasicBlock.getInstructions().size() - 1) instanceof Br) {
                existTerminate = true;
            } else {
                BuildValue.buildBr(curBasicBlock, nextBasicBlock);
            }
        } else {
            BuildValue.buildBr(curBasicBlock, nextBasicBlock);
        }
        curBasicBlock = nextBasicBlock;
    }

    private void visitFor(StmtNode stmtNode) {
        if (stmtNode.getCondNode() != null) {
            if (stmtNode.getForRightStmtNode() == null) {
                loopCount = loopCount + 1;
                //...
                visitForStmtNode(stmtNode.getForLeftStmtNode());
                //三个基本块，条件，循环，循环后
                BasicBlock condBasicBlock = BuildValue.buildBasicBlock(curFunc);
                BasicBlock forBasicBlock = BuildValue.buildBasicBlock(curFunc);
                BasicBlock nextBasicBlock = BuildValue.buildBasicBlock(curFunc);
                BasicBlock breakBasicBlock = curBreakBasicBlock;//用于记录嵌套
                BasicBlock continueBasicBlock = curContinueBasicBlock;
                BuildValue.buildBr(curBasicBlock, condBasicBlock);
                curIfBasicBlock = forBasicBlock;//条件符合就进入
                curElseBasicBlock = nextBasicBlock;//类似于if-else

                curBasicBlock = condBasicBlock;
                visitCondNode(stmtNode.getCondNode());
                curBreakBasicBlock = nextBasicBlock;//退出
                curContinueBasicBlock = condBasicBlock;
                curBasicBlock = forBasicBlock;

                if (stmtNode.getForStmtNode().getType() != 10) {
                    isVirtualBlock = true;
                }
                visitStmtNode(stmtNode.getForStmtNode());
                isVirtualBlock = false;
                loopCount = loopCount - 1;
                if (curBasicBlock.getInstructions().isEmpty()) {
                    BuildValue.buildBr(curBasicBlock, condBasicBlock);
                } else if (!(curBasicBlock.getInstructions().get(curBasicBlock.getInstructions().size() - 1) instanceof Br)) {
                    BuildValue.buildBr(curBasicBlock, condBasicBlock);
                }
                /*if (curBasicBlock.getInstructions().isEmpty() || !(curBasicBlock.getInstructions().get(curBasicBlock.getInstructions().size() - 1) instanceof Br)) {
                    //visitForStmtNode(stmtNode.getForRightStmtNode());
                    loopCount = loopCount - 1;
                    if (curBasicBlock.getInstructions().isEmpty()) {
                        BuildValue.buildBr(curBasicBlock, condBasicBlock);
                    } else if (!(curBasicBlock.getInstructions().get(curBasicBlock.getInstructions().size() - 1) instanceof Br)) {
                        BuildValue.buildBr(curBasicBlock, condBasicBlock);
                    }
                }*/

                curBreakBasicBlock = breakBasicBlock;
                curContinueBasicBlock = continueBasicBlock;
                curBasicBlock = nextBasicBlock;
            } else {
                loopCount = loopCount + 1;
                //...
                visitForStmtNode(stmtNode.getForLeftStmtNode());
                //三个基本块，条件，循环，循环后
                BasicBlock condBasicBlock = BuildValue.buildBasicBlock(curFunc);
                BasicBlock updateBasicBlock = BuildValue.buildBasicBlock(curFunc);
                BasicBlock forBasicBlock = BuildValue.buildBasicBlock(curFunc);
                BasicBlock nextBasicBlock = BuildValue.buildBasicBlock(curFunc);
                BasicBlock breakBasicBlock = curBreakBasicBlock;//用于记录嵌套
                BasicBlock continueBasicBlock = curContinueBasicBlock;
                BuildValue.buildBr(curBasicBlock, condBasicBlock);
                curIfBasicBlock = forBasicBlock;//条件符合就进入
                curElseBasicBlock = nextBasicBlock;//类似于if-else

                curBasicBlock = condBasicBlock;
                visitCondNode(stmtNode.getCondNode());
                curBreakBasicBlock = nextBasicBlock;//退出
                curContinueBasicBlock = updateBasicBlock;
                curBasicBlock = forBasicBlock;

                if (stmtNode.getForStmtNode().getType() != 10) {
                    isVirtualBlock = true;
                }
                visitStmtNode(stmtNode.getForStmtNode());
                isVirtualBlock = false;/* private void visitContinue(StmtNode stmtNode) {
        if (loopCount == 0) {
            OutWrongInformation.addError(new Error(stmtNode.getLineNum(), ErrorType.m));
        }
        curForContinue = true;//同一个Block，continue后的语句不用生成
        //BasicBlock continueBasicBlock = BuildValue.buildBasicBlock(curFunc);

        BuildValue.buildBr(curBasicBlock, curContinueBasicBlock);
        curBasicBlock = curContinueBasicBlock;
        ///......
    }*/
             //   if (curBasicBlock.getInstructions().isEmpty() || !(curBasicBlock.getInstructions().get(curBasicBlock.getInstructions().size() - 1) instanceof Br)) {

                    loopCount = loopCount - 1;
                    if (curBasicBlock.getInstructions().isEmpty()) {
                        BuildValue.buildBr(curBasicBlock, updateBasicBlock);
                    } else if (!(curBasicBlock.getInstructions().get(curBasicBlock.getInstructions().size() - 1) instanceof Br)) {
                        BuildValue.buildBr(curBasicBlock, updateBasicBlock);
                    }
                    curBasicBlock = updateBasicBlock;
                    visitForStmtNode(stmtNode.getForRightStmtNode());
                    if (curBasicBlock.getInstructions().isEmpty()) {
                        BuildValue.buildBr(curBasicBlock, condBasicBlock);
                    } else if (!(curBasicBlock.getInstructions().get(curBasicBlock.getInstructions().size() - 1) instanceof Br)) {
                        BuildValue.buildBr(curBasicBlock, condBasicBlock);
                    }


                curBreakBasicBlock = breakBasicBlock;
                curContinueBasicBlock = continueBasicBlock;
                curBasicBlock = nextBasicBlock;
            }
        } else {
            if (stmtNode.getForRightStmtNode() == null) {
                loopCount = loopCount + 1;
                //...
                visitForStmtNode(stmtNode.getForLeftStmtNode());
                //两个基本块，条件，循环，循环后
                BasicBlock forBasicBlock = BuildValue.buildBasicBlock(curFunc);
                BasicBlock nextBasicBlock = BuildValue.buildBasicBlock(curFunc);
                BasicBlock breakBasicBlock = curBreakBasicBlock;//用于记录嵌套
                BasicBlock continueBasicBlock = curContinueBasicBlock;
                BuildValue.buildBr(curBasicBlock, forBasicBlock);
                curIfBasicBlock = forBasicBlock;//条件符合就进入
                curElseBasicBlock = nextBasicBlock;//类似于if-else

                curBreakBasicBlock = nextBasicBlock;//退出
                curContinueBasicBlock = forBasicBlock;
                curBasicBlock = forBasicBlock;

                if (stmtNode.getForStmtNode().getType() != 10) {
                    isVirtualBlock = true;
                }
                visitStmtNode(stmtNode.getForStmtNode());
                isVirtualBlock = false;

                loopCount = loopCount - 1;
                if (curBasicBlock.getInstructions().isEmpty()) {
                    BuildValue.buildBr(curBasicBlock, forBasicBlock);
                } else if (!(curBasicBlock.getInstructions().get(curBasicBlock.getInstructions().size() - 1) instanceof Br)) {
                    BuildValue.buildBr(curBasicBlock, forBasicBlock);
                }
                /*curBasicBlock = updateBasicBlock;
                visitForStmtNode(stmtNode.getForRightStmtNode());
                if (curBasicBlock.getInstructions().isEmpty()) {
                    BuildValue.buildBr(curBasicBlock, condBasicBlock);
                } else if (!(curBasicBlock.getInstructions().get(curBasicBlock.getInstructions().size() - 1) instanceof Br)) {
                    BuildValue.buildBr(curBasicBlock, condBasicBlock);
                }*/
                /*if (curBasicBlock.getInstructions().isEmpty() || !(curBasicBlock.getInstructions().get(curBasicBlock.getInstructions().size() - 1) instanceof Br)) {
                    //visitForStmtNode(stmtNode.getForRightStmtNode());
                    loopCount = loopCount - 1;
                    if (curBasicBlock.getInstructions().isEmpty()) {
                        BuildValue.buildBr(curBasicBlock, forBasicBlock);
                    } else if (!(curBasicBlock.getInstructions().get(curBasicBlock.getInstructions().size() - 1) instanceof Br)) {
                        BuildValue.buildBr(curBasicBlock, forBasicBlock);
                    }
                }*/

                curBreakBasicBlock = breakBasicBlock;
                curContinueBasicBlock = continueBasicBlock;
                curBasicBlock = nextBasicBlock;
            } else {
                loopCount = loopCount + 1;
                //...
                visitForStmtNode(stmtNode.getForLeftStmtNode());
                //两个基本块，条件，循环，循环后
                BasicBlock updateBasicBlock = BuildValue.buildBasicBlock(curFunc);
                BasicBlock forBasicBlock = BuildValue.buildBasicBlock(curFunc);
                BasicBlock nextBasicBlock = BuildValue.buildBasicBlock(curFunc);
                BasicBlock breakBasicBlock = curBreakBasicBlock;//用于记录嵌套
                BasicBlock continueBasicBlock = curContinueBasicBlock;
                BuildValue.buildBr(curBasicBlock, forBasicBlock);
                curIfBasicBlock = forBasicBlock;//条件符合就进入
                curElseBasicBlock = nextBasicBlock;//类似于if-else

                curBreakBasicBlock = nextBasicBlock;//退出
                curContinueBasicBlock = updateBasicBlock;
                curBasicBlock = forBasicBlock;

                if (stmtNode.getForStmtNode().getType() != 10) {
                    isVirtualBlock = true;
                }
                visitStmtNode(stmtNode.getForStmtNode());
                isVirtualBlock = false;
                loopCount = loopCount - 1;
                if (curBasicBlock.getInstructions().isEmpty()) {
                    BuildValue.buildBr(curBasicBlock, updateBasicBlock);
                } else if (!(curBasicBlock.getInstructions().get(curBasicBlock.getInstructions().size() - 1) instanceof Br)) {
                    BuildValue.buildBr(curBasicBlock, updateBasicBlock);
                }
                curBasicBlock = updateBasicBlock;
                visitForStmtNode(stmtNode.getForRightStmtNode());
                if (curBasicBlock.getInstructions().isEmpty()) {
                    BuildValue.buildBr(curBasicBlock, forBasicBlock);
                } else if (!(curBasicBlock.getInstructions().get(curBasicBlock.getInstructions().size() - 1) instanceof Br)) {
                    BuildValue.buildBr(curBasicBlock, forBasicBlock);
                }
                /*if (curBasicBlock.getInstructions().isEmpty() || !(curBasicBlock.getInstructions().get(curBasicBlock.getInstructions().size() - 1) instanceof Br)) {
                    curBasicBlock = updateBasicBlock;
                    visitForStmtNode(stmtNode.getForRightStmtNode());
                    loopCount = loopCount - 1;
                    if (curBasicBlock.getInstructions().isEmpty()) {
                        BuildValue.buildBr(curBasicBlock, forBasicBlock);
                    } else if (!(curBasicBlock.getInstructions().get(curBasicBlock.getInstructions().size() - 1) instanceof Br)) {
                        BuildValue.buildBr(curBasicBlock, forBasicBlock);
                    }
                }*/

                curBreakBasicBlock = breakBasicBlock;
                curContinueBasicBlock = continueBasicBlock;
                curBasicBlock = nextBasicBlock;
            }
        }
    }

    private void visitForStmtNode(ForStmtNode forLeftStmtNode) {
        if (forLeftStmtNode == null) {
            return;
        }
        visitLval(forLeftStmtNode.getlValNode(), true);
        Value tmpVal = curValue;
        visitExpNode(forLeftStmtNode.getExpNode());
        BuildValue.buildStore(curBasicBlock, curValue, tmpVal);
    }

    private void visitBreak(StmtNode stmtNode) {
        if (loopCount == 0) {
            OutWrongInformation.addError(new Error(stmtNode.getLineNum(), ErrorType.m));
        }
        BuildValue.buildBr(curBasicBlock, curBreakBasicBlock);
        shouldGo = curBreakBasicBlock;
        ///......
    }

    private void visitContinue(StmtNode stmtNode) {
        if (loopCount == 0) {
            OutWrongInformation.addError(new Error(stmtNode.getLineNum(), ErrorType.m));
        }
        //同一个Block，continue后的语句不用生成
        //BasicBlock continueBasicBlock = BuildValue.buildBasicBlock(curFunc);

        BuildValue.buildBr(curBasicBlock, curContinueBasicBlock);
        shouldGo = curContinueBasicBlock;
        ///......
    }

    private void visitReturn(StmtNode stmtNode) {
        if (isCheckingVoidFunReturn && stmtNode.getExpNode() != null) {
            OutWrongInformation.addError(new Error(stmtNode.getLineNum(), ErrorType.f));
        }
        ExpNode expNode = stmtNode.getExpNode();
        int type = visitExpNode(expNode);
        if (expNode == null) {
            BuildValue.buildRetVoid(curBasicBlock);
        } else {
            if (curFunc.getFunctionType().getReturnType().toString().equals("i32") && curValue.getType().toString().equals("i8")) {
                curValue = BuildValue.buildZeroExtend(curBasicBlock, curValue);
            } else if (curFunc.getFunctionType().getReturnType().toString().equals("i8") && curValue.getType().toString().equals("i32")) {
                curValue = BuildValue.buildTrunc(curBasicBlock, curValue);
            }
            curValue = BuildValue.buildRetWithReturn(curBasicBlock, curValue);
        }
        //语义分析return语句

    }

    private void visitGetint(StmtNode stmtNode) {
        /*
        %3 = call i32 @getint()
        store i32 %3, i32* %1
        */
        visitLval(stmtNode.getLValNode(), true);
        Value tmpIdentVal = curValue;
        curValue = BuildValue.buildCall(curBasicBlock, new IntegerType(32), "@getint", new ArrayList<>(), funcNameMapFuncType.get("getint"));
        BuildValue.buildStore(curBasicBlock, curValue, tmpIdentVal);
        /*赋值，？？？？？？可能需要类型转换*/

    }

    private int visitLval(LValNode lValNode, boolean atLeftSide) {
        //LVal → Ident ['[' Exp ']']
        Symbol symbol = visitIdentNode(lValNode.getIdentNode(), atLeftSide);
        Value tmpIdent = curIdent;
        System.out.println("!!@@"+curIdent.getName());
        if (lValNode.getExpNode() != null) {
            //ArrayType arrayType = ((ArrayType) curIdent.getType());
            visitExpNode(lValNode.getExpNode());
            //一维数组，指针偏移为0，数组偏移为curvalue
            //两种构造方式：指针，数组
            if (tmpIdent.getType() instanceof ArrayType) {
                //curIdent是const类型

                if (tmpIdent instanceof ConstantString) {
                    if(((ConstantInt) curValue).getVal() >= ((ConstantString)tmpIdent).getString().length()) {
                        curValue = new ConstantInt(isChar?8:32, 0);
                    } else {
                        curValue = new ConstantInt(isChar?8:32,((ConstantString)tmpIdent).getString().charAt(((ConstantInt) curValue).getVal()));
                    }
                } else if (((ConstArray)tmpIdent).getValues().get(0) instanceof ZeroInitializer){
                    curValue = new ConstantInt(isChar?8:32,0);
                } else if (curValue instanceof Load) {
                    curValue = new ConstantInt(isChar?8:32, ((ConstantInt)((ConstArray)tmpIdent).getValues().get(((ConstantInt) curValue).getVal())).getVal());
                } else {
                    curValue = new ConstantInt(isChar?8:32, ((ConstantInt)((ConstArray)tmpIdent).getValues().get(((ConstantInt) curValue).getVal())).getVal());
                }
            } else {
                //非const类型用指针存或获取
                if (curBasicBlock == null) {
                    //全局变量声明调用
                }
                if (((PointerType)tmpIdent.getType()).getPointedType() instanceof ArrayType) {
                    curValue = BuildValue.buildGetElementPtr(curBasicBlock, tmpIdent, BuildValue.buildConstantInt((symbol.getbType() == 0) ? 32 : 8, 0), curValue);
                } else if (((PointerType)tmpIdent.getType()).getPointedType() instanceof PointerType){
                    Load load = BuildValue.buildLoad(curBasicBlock, tmpIdent);
                    curValue = BuildValue.buildGetElementPtr(curBasicBlock, load, curValue);
                } else {
                    curValue = tmpIdent;//BuildValue.buildGetElementPtr(curBasicBlock, curIdent, curValue);
                }
                if (tmpIdent.getType() instanceof IntegerType) {
                    curInteger = ((ConstantInt) tmpIdent).getVal();
                    curValue = new ConstantInt(((IntegerType) tmpIdent.getType()).getBitSize(), curInteger);
                } else if (!atLeftSide) {
                    if (((PointerType)tmpIdent.getType()).getPointedType() instanceof ArrayType) {
                        curValue = BuildValue.buildLoad(curBasicBlock, curValue);
                    } else if (((PointerType)tmpIdent.getType()).getPointedType() instanceof PointerType){
                        curValue = BuildValue.buildLoad(curBasicBlock, curValue);
                    } else {
                        curValue = BuildValue.buildLoad(curBasicBlock, tmpIdent);//BuildValue.buildGetElementPtr(curBasicBlock, curIdent, curValue);
                    }
                }
            }
            //curValue = BuildValue.buildGetElementPtr(arrayType, curBasicBlock, curIdent, BuildValue.buildConstantInt((symbol.getbType() == 0) ? 32 : 8, 0), curValue);
            if (symbol == null) {
                return 0;
            }
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
        if (curIdent.getType() instanceof IntegerType) {
            curInteger = ((ConstantInt) curIdent).getVal();
            System.out.println("eee::::"+curIdent);
            curValue = new ConstantInt(((IntegerType) curIdent.getType()).getBitSize(), curInteger);
        } else {
            //如果是数组，那么没有偏移量
            if (curIdent.getType() instanceof ArrayType) {
                //curIdent是const类型
                if (curIdent instanceof ConstantString) {
                    if(((ConstantInt) curValue).getVal() >= ((ConstantString)curIdent).getString().length()) {
                        curValue = new ConstantInt(isChar?8:32, 0);
                    } else {
                        curValue = new ConstantInt(isChar?8:32,((ConstantString)curIdent).getString().charAt(((ConstantInt) curValue).getVal()));
                    }
                } else if (((ConstArray)curIdent).getValues().get(0) instanceof ZeroInitializer){
                    curValue = new ConstantInt(isChar?8:32,0);
                } else {
                    curValue = new ConstantInt(isChar?8:32, ((ConstantInt)((ConstArray)curIdent).getValues().get(((ConstantInt) curValue).getVal())).getVal());
                }
            } else {
                if (((PointerType)curIdent.getType()).getPointedType() instanceof ArrayType) {
                    curValue = BuildValue.buildGetElementPtr(curBasicBlock, curIdent, BuildValue.buildConstantInt((symbol.getbType() == 0) ? 32 : 8, 0), BuildValue.buildConstantInt((symbol.getbType() == 0) ? 32 : 8, 0));
                } else if (((PointerType)curIdent.getType()).getPointedType() instanceof PointerType){
                    Load load = BuildValue.buildLoad(curBasicBlock, curIdent);
                    curValue = BuildValue.buildGetElementPtr(curBasicBlock, load, BuildValue.buildConstantInt((symbol.getbType() == 0) ? 32 : 8, 0));
                } else {
                    curValue = curIdent;//BuildValue.buildGetElementPtr(curBasicBlock, curIdent, curValue);
                }
                if (!atLeftSide) {
                    if (((PointerType)curIdent.getType()).getPointedType() instanceof ArrayType) {
                        //数组名没有偏移量，curValue是首地址
                        //curValue = BuildValue.buildLoad(curBasicBlock, curValue);
                    } else if (((PointerType)curIdent.getType()).getPointedType() instanceof PointerType){
                        //curValue = BuildValue.buildLoad(curBasicBlock, curValue);
                    } else {
                        curValue = BuildValue.buildLoad(curBasicBlock, curIdent);//BuildValue.buildGetElementPtr(curBasicBlock, curIdent, curValue);
                    }
                }
            }
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
            Symbol symbol = tables.get(tmp).findSymbol(identNode.getName());
            if (symbol != null) {
                if (atLeftSide && symbol.isCon()) {
                    OutWrongInformation.addError(new Error(identNode.getLineNum(), ErrorType.h));
                }
                curIdent = symbolTables.get(tmp).get(identNode.getName());
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
        Value tmpIdentVal = curValue;
        curValue = BuildValue.buildCall(curBasicBlock, new IntegerType(8), "@getchar", new ArrayList<>(), funcNameMapFuncType.get("getchar"));
        BuildValue.buildStore(curBasicBlock, curValue, tmpIdentVal);
        /*赋值，？？？？？？可能需要类型转换*/
    }

    private void visitPrintf(StmtNode stmtNode) {
        //检查printf参数对应数量是否匹配
        int num = stmtNode.getPrintfExpNodes().size();
        if (stmtNode.getStringConstNode().getNum() != num) {
            OutWrongInformation.addError(new Error(stmtNode.getLineNum(), ErrorType.l));
        }
        ArrayList<Value> expValues = new ArrayList<>();
        for (ExpNode expNode : stmtNode.getPrintfExpNodes()) {
            visitExpNode(expNode);
            expValues.add(curValue);
        }
        //分析字符串
        String constString = stmtNode.getStringConstNode().toString().substring(1, stmtNode.getStringConstNode().toString().length() - 1);
        String nowStr = "";
        int formatNum = 0;
        for (int i = 0; i < constString.length(); i++) {
            if (i != constString.length() - 1
                    && constString.charAt(i) == '%'
                    && constString.charAt(i+1) == 'd') {
                //putint
                if (nowStr.length() != 0) {
                    ConstantString constantString = new ConstantString(deal(nowStr));
                    GlobalVar globalVar = BuildValue.buildPrintfStringGlobalVar(constantString);
                    GetElementPtr getElementPtr = BuildValue.buildGetElementPtr(curBasicBlock, globalVar, new ConstantInt(32, 0), new ConstantInt(32, 0));
                    ArrayList<Value> real = new ArrayList<>();
                    real.add(getElementPtr);
                    BuildValue.buildCall(curBasicBlock, new VoidType(), "@putstr", real, funcNameMapFuncType.get("putstr"));
                    nowStr = "";
                }
                ArrayList<Value> formatExp = new ArrayList<>();
                formatExp.add(expValues.get(formatNum++));
                BuildValue.buildCall(curBasicBlock, new VoidType(), "@putint", formatExp, funcNameMapFuncType.get("putint"));
                i++;
            } else if (i != constString.length() - 1
                    && constString.charAt(i) == '%'
                    && constString.charAt(i+1) == 'c') {
                //putchar
                if (nowStr.length() != 0) {
                    ConstantString constantString = new ConstantString(deal(nowStr));
                    GlobalVar globalVar = BuildValue.buildPrintfStringGlobalVar(constantString);
                    GetElementPtr getElementPtr = BuildValue.buildGetElementPtr(curBasicBlock, globalVar, new ConstantInt(32, 0), new ConstantInt(32, 0));
                    ArrayList<Value> real = new ArrayList<>();
                    real.add(getElementPtr);
                    BuildValue.buildCall(curBasicBlock, new VoidType(), "@putstr", real, funcNameMapFuncType.get("putstr"));
                    nowStr = "";
                }
                ArrayList<Value> formatExp = new ArrayList<>();
                formatExp.add(expValues.get(formatNum++));
                BuildValue.buildCall(curBasicBlock, new VoidType(), "@putch", formatExp, funcNameMapFuncType.get("putch"));
                i++;
            } else {
                //输出已经存的无转义、无格式化字符串
                nowStr += constString.charAt(i);
            }
        }
        if (nowStr.length() != 0) {
            ConstantString constantString = new ConstantString(deal(nowStr));
            GlobalVar globalVar = BuildValue.buildPrintfStringGlobalVar(constantString);
            GetElementPtr getElementPtr = BuildValue.buildGetElementPtr(curBasicBlock, globalVar, new ConstantInt(32, 0), new ConstantInt(32, 0));
            ArrayList<Value> real = new ArrayList<>();
            real.add(getElementPtr);
            BuildValue.buildCall(curBasicBlock, new VoidType(), "@putstr", real, funcNameMapFuncType.get("putstr"));
        }
    }

    private String deal(String replace) {
        return replace.replace("\\n", "\n")
                .replace("\\0","\0")
                .replace("\\\\","\\")
                .replace("\\a","" + (char)7)
                .replace("\\b","\b")
                .replace("\\t","\t")
                .replace("\\v","" + (char)11)
                .replace("\\\"","\"")
                .replace("\\\'","\'")
                .replace("\\f","\f");
    }


    private void visitFuncFParamNode(FuncFParamNode funcFParamNode) {
        addSymbol(funcFParamNode.getName(), funcFParamNode.isArray() ? 1 : 0, funcFParamNode.getBType(), false, null, funcFParamNode.getIdentLineNum());
        /*if (getBType() == 1 && !existBracket) {
            return 1;//"Char";
        } else if (getBType() == 0 && !existBracket) {
            return 2;//"Int";
        } else if (getBType() == 1 && existBracket) {
            return 3;//"CharArray";
        } else if (getBType()== 0 && existBracket) {
            return 4;//"IntArray";*/
        switch (funcFParamNode.getType()) {
            case 1:
                curArgumentsTypes.add(new IntegerType(8));
                break;
            case 2 :
                curArgumentsTypes.add(new IntegerType(32));
                break;
            case 3 :
                curArgumentsTypes.add(new PointerType(new IntegerType(8)));
                break;
            case 4 :
                curArgumentsTypes.add(new PointerType(new IntegerType(32)));
                break;
            default:
                break;
        }
    }

    private void visitDeclNode(DeclNode declNode) {
        isDecl = true;
        if (declNode.getType() == 1) {
            visitConstDeclNode(declNode.getConstDeclNode());
        } else {
            visitVarDeclNode(declNode.getVarDeclNode());
        }
        isDecl = false;
    }

    private void visitVarDeclNode(VarDeclNode varDeclNode) {
        for (VarDefNode varDefNode : varDeclNode.getVarDefNodes()) {
            addSymbol(varDefNode.getIentName(), varDefNode.isArray() ? 1 : 0, varDeclNode.getType(), false, null, varDefNode.getIdentLineNum());
            isChar = (varDeclNode.getType() == 1) ? true : false;
            visitVarDefNode(varDefNode);
        }
        isChar = false;
    }

    private void visitVarDefNode(VarDefNode varDefNode) {
        //VarDef → Ident [ '[' ConstExp ']' ] | Ident [ '[' ConstExp ']' ] '=' InitVal
        if (varDefNode.getConstExpNode() != null) {
            isConst = true;
            visitAddExpNode(varDefNode.getConstExpNode().getAddExpNode());
            isConst = false;
        }
        if (varDefNode.getInitValNode() != null) {
            ///>>>>>>>>>>>>>>>?????alloc分配数组情况未考虑
            int tmpInteger = curInteger;
            if (isGlobal) {
                visitInitValNode(varDefNode.getInitValNode());
                GlobalVar globalVar = new GlobalVar(varDefNode.getIentName(), false, curValue.getType(), curValue);
                symbolTables.get(id).put(varDefNode.getIentName(), globalVar);
                midend.value.Module.getInstance().addGlobalVar(globalVar);
            } else {
                Alloca alloca;
                if (varDefNode.getConstExpNode() != null) {
                    alloca = BuildValue.buildAlloca(new ArrayType(new IntegerType(isChar ? 8 : 32), tmpInteger), curBasicBlock);
                } else {
                    alloca = BuildValue.buildAlloca(new IntegerType(isChar ? 8 : 32), curBasicBlock);
                }
                visitInitValNode(varDefNode.getInitValNode());
                symbolTables.get(id).put(varDefNode.getIentName(), alloca);
                //store数组的每一个值
                if (varDefNode.getConstExpNode() != null) {
                    int index = 0;//偏移量
                    for (Value value: curValuesForDefArray) {
                        GetElementPtr getElementPtr = BuildValue.buildGetElementPtr(curBasicBlock, alloca, new ConstantInt(isChar? 8 : 32, 0), new ConstantInt(isChar? 8 : 32, index++));
                        BuildValue.buildStore(curBasicBlock, value, getElementPtr);
                    }
                } else {
                    BuildValue.buildStore(curBasicBlock, curValue, alloca);
                }
            }
        } else {
            if (varDefNode.getConstExpNode() != null) {
                curValue = new ZeroInitializer(new ArrayType(new IntegerType(isChar ? 8 : 32), curInteger));
            } else {
                curValue = new ConstantInt(isChar ? 8 : 32, 0);
            }
            if (isGlobal) {
                GlobalVar globalVar = new GlobalVar(varDefNode.getIentName(), false, curValue.getType(), curValue);
                symbolTables.get(id).put(varDefNode.getIentName(), globalVar);
                midend.value.Module.getInstance().addGlobalVar(globalVar);
            } else {
                if (varDefNode.getConstExpNode() != null) {
                    Alloca alloca = BuildValue.buildAlloca(curValue.getType(), curBasicBlock);
                    symbolTables.get(id).put(varDefNode.getIentName(), alloca);
                } else {
                    Alloca alloca = BuildValue.buildAlloca(new IntegerType(isChar ? 8 : 32), curBasicBlock);
                    symbolTables.get(id).put(varDefNode.getIentName(), alloca);
                    //我想着是因为不是全局变量的情况下，没必要store初值0到地址alloca
                    // BuildValue.buildStore(curBasicBlock, curValue, alloca);
                }
            }
        }
    }


    private void visitInitValNode(InitValNode initValNode) {
        if (initValNode.getExpNode() != null) {
            visitExpNode(initValNode.getExpNode());
            if (isGlobal) {
                curValue = BuildValue.buildConstantInt(isChar ? 8 : 32, curInteger);
            }
        } else if (initValNode.getExpNodes() != null || initValNode.getStringConstNode() == null) {
            if (isGlobal) {
                int length = curInteger;
                ArrayList<Value> values = new ArrayList<>();
                ArrayList<Integer> curIntegers = new ArrayList<>();
                int i = 0;
                for (ExpNode expNode : initValNode.getExpNodes()) {
                    visitExpNode(expNode);
                    curIntegers.add(curInteger);
                    values.add(new ConstantInt(isChar ? 8 : 32, curInteger));
                    i++;
                    if (i == length) {
                        break;
                    }
                }
                curConstArray = new ConstArray(new ArrayType(new IntegerType(isChar ? 8 : 32), /*curIntegers.size()*/length), values);
                curValue = curConstArray;
            } else {
                int i = 0;
                int length = curInteger;
                ArrayList<Value> values = new ArrayList<>();
                for (ExpNode expNode : initValNode.getExpNodes()) {
                    visitExpNode(expNode);
                    values.add(curValue);
                    i++;

                    if (i == length) {
                        break;
                    }
                }
                if (isChar) {
                    for (; i < length; i++) {
                        values.add(new ConstantInt(8, 0));
                    }
                }
                curValuesForDefArray = values;
                /**/
            }
        } else {
            if (isGlobal) {
                curValue = new ConstantString(initValNode.getStringConstNode().toString().substring(1, initValNode.getStringConstNode().toString().length() - 1).replace("\\\\","\\").replace("\\\'", "\'"), curInteger);//处理String串
            } else {
                int i = 0;
                ArrayList<Value> values = new ArrayList<>();
                int length = curInteger;
                for (char c : initValNode.getStringConstNode().toString().substring(1, initValNode.getStringConstNode().toString().length() - 1).replace("\\\\","\\").replace("\\\'", "\'").toCharArray()) {
                    values.add(new ConstantInt(8, c));
                    i++;
                    if (isChar? i == length - 1 : i == length) {
                        break;
                    }
                    if (c == 0) {
                        break;
                    }
                }
                for (; i < length; i++) {
                    values.add(new ConstantInt(8, 0));
                }
                curValuesForDefArray = values;
            }
            //String;
        }
    }

    private void visitConstDeclNode(ConstDeclNode constDeclNode) {
        for (ConstDefNode constDefNode : constDeclNode.getConstDefNodes()) {
            //判断是否为ARRAY;
            addSymbol(constDefNode.getIdentName(), constDefNode.isArray() ? 1 : 0, constDeclNode.getType(), true, null, constDefNode.getIdentLineNum());
            isChar = (constDeclNode.getType() == 1) ? true : false;
            visitConstDefNode(constDefNode);
        }
        isChar = false;
    }

    private void visitConstDefNode(ConstDefNode constDefNode) {
        // ConstDef -> Ident [ '[' ConstExp ']' ] '=' ConstInitVal
        if (constDefNode.getConstExpNode() != null) {
            isConst = true;
            visitAddExpNode(constDefNode.getConstExpNode().getAddExpNode());
            isConst = false;
        }
        if (constDefNode.getConstExpNode() == null) {
            if (isGlobal) {
                visitConstInitValNode(constDefNode.getConstInitValNode());
                System.out.println(curValue);
                //GlobalVar globalVar = new GlobalVar(constDefNode.getIdentName(), true, curValue.getType(), curValue);
                symbolTables.get(id).put(constDefNode.getIdentName(), curValue);
                //Module.getInstance().addGlobalVar(globalVar);
            } else {
                //Alloca alloca = BuildValue.buildAlloca(new IntegerType(isChar ? 8 : 32), curBasicBlock);
                visitConstInitValNode(constDefNode.getConstInitValNode());
                //symbolTables.get(id).put(constDefNode.getIdentName(), curValue);
                symbolTables.get(id).put(constDefNode.getIdentName(), curValue);
                //BuildValue.buildStore(curBasicBlock, curValue, alloca);
            }
        } else {
            int tmpInteger = curInteger;
            if (isGlobal) {
                visitConstInitValNode(constDefNode.getConstInitValNode());
                GlobalVar globalVar = new GlobalVar(constDefNode.getIdentName(), true, curValue.getType(), curValue);
                symbolTables.get(id).put(constDefNode.getIdentName(), globalVar);
                midend.value.Module.getInstance().addGlobalVar(globalVar);
            } else {
                Alloca alloca;
                if (constDefNode.getConstExpNode() != null) {
                    alloca = BuildValue.buildAlloca(new ArrayType(new IntegerType(isChar ? 8 : 32), tmpInteger), curBasicBlock);
                } else {
                    alloca = BuildValue.buildAlloca(new IntegerType(isChar ? 8 : 32), curBasicBlock);
                }
                visitConstInitValNode(constDefNode.getConstInitValNode());
                symbolTables.get(id).put(constDefNode.getIdentName(), alloca);
                System.out.println("const::"+constDefNode.getIdentName());
                //store数组的每一个值
                if (constDefNode.getConstExpNode() != null) {
                    int index = 0;//偏移量
                    for (Value value: curValuesForDefArray) {
                        GetElementPtr getElementPtr = BuildValue.buildGetElementPtr(curBasicBlock, alloca, new ConstantInt(isChar? 8 : 32, 0), new ConstantInt(isChar? 8 : 32, index++));
                        BuildValue.buildStore(curBasicBlock, value, getElementPtr);
                    }
                } else {
                    BuildValue.buildStore(curBasicBlock, curValue, alloca);
                }
            }
        }
    }

    //ConstInitVal → ConstExp | '{' [ ConstExp { ',' ConstExp } ] '}' | StringConst // 1.常表达式初值 2.一维数组初值
    private void visitConstInitValNode(ConstInitValNode constInitValNode) {
        isConst = true;
        if (constInitValNode.getConstExpNode() != null) {
            visitAddExpNode(constInitValNode.getConstExpNode().getAddExpNode());
            curValue = BuildValue.buildConstantInt(isChar ? 8 : 32, curInteger);
        } else if (constInitValNode.getConstExpNodes() != null) {
            //分为全局和局部两种定义urConstArray用于全局
            int length = curInteger;
            int i = 0;
            ArrayList<Value> values = new ArrayList<>();
            ArrayList<Value> values1 = new ArrayList<>();
            ArrayList<Integer> curIntegers = new ArrayList<>();
            for (ConstExpNode constExpNode : constInitValNode.getConstExpNodes()) {
                visitAddExpNode(constExpNode.getAddExpNode());
                curIntegers.add(curInteger);
                values.add(curValue);
                values1.add(curValue);
                i++;
                if (i == length) {
                    break;
                }
            }
            ///!!!!\0
            for (; i < length; i++) {
                values1.add(new ConstantInt(8, 0));
            }
            //TODO
            curConstArray = new ConstArray(new ArrayType(new IntegerType(isChar ? 8 : 32), /*curIntegers.size()*/length), values1);////////////////////////////////////
            curValue = curConstArray;
            //局部const定义
            curValuesForDefArray = values1;
            //...数组初始化
        } else {
            int length = curInteger;
            if (isGlobal) {
                curValue = new ConstantString(constInitValNode.getStringConstNode().toString().substring(1, constInitValNode.getStringConstNode().toString().length() - 1).replace("\\\\","\\").replace("\\\'", "\'"), curInteger);//处理String串
            } else {
                int i = 0;
                ArrayList<Value> values = new ArrayList<>();
                for (char c : constInitValNode.getStringConstNode().toString().substring(1, constInitValNode.getStringConstNode().toString().length() - 1).replace("\\\\","\\").replace("\\\'", "\'").toCharArray()) {
                    values.add(new ConstantInt(8, c));
                    i++;
                    if (c == 0) {
                        break;
                    }
                    if (i == length - 1) {
                        break;
                    }
                }
                for (; i < length; i++) {
                    values.add(new ConstantInt(8, 0));
                }
                curValuesForDefArray = values;
            }
        }
        System.out.println("const"+curValue);
        isConst = false;
    }

    private void addSymbol(String identName, int type, int bType, boolean isCon, Integer scopeId, int lineNum) {
        if (type == 1) {
            SymbolTable tmpTable = tables.get(id);
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
