package frontend;


import error.Error;
import error.ErrorType;
import node.*;

import java.util.ArrayList;

public class Parser {
    private ArrayList<Token> tokens;
    private int pos = 0;
    private CompUnitNode compUnitNode;
    private boolean isParserWrong = false;

    public Parser(ArrayList<Token> tokens) {
        this.tokens = tokens;
        compUnitNode = parseCompUnit();
    }

    public CompUnitNode getCompUnitNode() {
        return this.compUnitNode;
    }

    private CompUnitNode parseCompUnit() {
        //编译单元 CompUnit → {Decl} {FuncDef} MainFuncDef
        ArrayList<DeclNode> declNodes = new ArrayList<>();
        ArrayList<FuncDefNode> funcDefNodes = new ArrayList<>();
        while (tokens.get(pos).getType().equals(TokenType.CONSTTK)
                || ((tokens.get(pos).getType().equals(TokenType.INTTK)
                || tokens.get(pos).getType().equals(TokenType.CHARTK))
                && tokens.get(pos + 1).getType().equals(TokenType.IDENFR)
                && !tokens.get(pos + 2).getType().equals(TokenType.LPARENT))) {
            declNodes.add(parseDecl());
        }
        while ((tokens.get(pos).getType().equals(TokenType.INTTK)
                || tokens.get(pos).getType().equals(TokenType.CHARTK)
                || tokens.get(pos).getType().equals(TokenType.VOIDTK))
                && tokens.get(pos + 1).getType().equals(TokenType.IDENFR)
                && tokens.get(pos + 2).getType().equals(TokenType.LPARENT)) {
            funcDefNodes.add(parseFuncDef());
        }
        MainFuncDefNode mainFuncDefNode = parseMainFuncDef();
        return new CompUnitNode(declNodes, funcDefNodes, mainFuncDefNode);
    }

    private MainFuncDefNode parseMainFuncDef() {
        //主函数定义 MainFuncDef → 'int' 'main' '(' ')' Block
        if (tokens.get(pos).getType().equals(TokenType.INTTK)
                && tokens.get(pos + 1).getType().equals(TokenType.MAINTK)
                && tokens.get(pos + 2).getType().equals(TokenType.LPARENT)
                && tokens.get(pos + 3).getType().equals(TokenType.RPARENT)) {
            pos += 4;
            BlockNode blockNode = parseBlock();
            return new MainFuncDefNode(blockNode);
        } else {
            if (tokens.get(pos).getType().equals(TokenType.INTTK)
                    && tokens.get(pos + 1).getType().equals(TokenType.MAINTK)
                    && tokens.get(pos + 2).getType().equals(TokenType.LPARENT)
                    && !tokens.get(pos + 3).getType().equals(TokenType.RPARENT)) {
                isParserWrong = true;
                OutWrongInformation.addError(new Error(tokens.get(pos).getLineNum(), ErrorType.j));
                pos += 3;
                BlockNode blockNode = parseBlock();
                return new MainFuncDefNode(blockNode);
            }
            throw new RuntimeException("ss");
        }
    }

    private FuncDefNode parseFuncDef() {
        //函数定义 FuncDef → FuncType Ident '(' [FuncFParams] ')' Block
        if (tokens.get(pos).getType().equals(TokenType.VOIDTK)
                || tokens.get(pos).getType().equals(TokenType.INTTK)
                || tokens.get(pos).getType().equals(TokenType.CHARTK)) {
            int position1 = pos;
            FuncTypeNode funcTypeNode = new FuncTypeNode(tokens.get(pos).getTypeString());
            pos++;
            IdentNode identNode = parseIdent();
            if (tokens.get(pos).getType().equals(TokenType.LPARENT)) {
                pos++;
                if (tokens.get(pos).getType().equals(TokenType.RPARENT)) {
                    pos++;
                    BlockNode blockNode = parseBlock();
                    return new FuncDefNode(funcTypeNode, identNode, null, blockNode);
                } else if (tokens.get(pos).getType().equals(TokenType.LBRACE)) {
                    isParserWrong = true;
                    OutWrongInformation.addError(new Error(tokens.get(position1).getLineNum(), ErrorType.j));//.wrongInformation.put(position1, tokens.get(position1).getLineNum() + " j");
                    BlockNode blockNode = parseBlock();
                    return new FuncDefNode(funcTypeNode, identNode, null, blockNode);
                } else {
                    int position = pos;
                    FuncFParamsNode funcFParamsNode = parseFuncFParams();
                    if (tokens.get(pos).getType().equals(TokenType.RPARENT)) {
                        pos++;
                        BlockNode blockNode = parseBlock();
                        return new FuncDefNode(funcTypeNode, identNode, funcFParamsNode, blockNode);
                    } else {
                        isParserWrong = true;
                        OutWrongInformation.addError(new Error(tokens.get(position).getLineNum(), ErrorType.j));//.wrongInformation.put(position, tokens.get(position).getLineNum() + " j");
                        BlockNode blockNode = parseBlock();
                        return new FuncDefNode(funcTypeNode, identNode, funcFParamsNode, blockNode);
                    }
                }
            } else {
                throw new RuntimeException("error");
            }
        } else {
            throw new RuntimeException("error");
        }
    }

    private BlockNode parseBlock() {
        //语句块 Block → '{' { BlockItem } '}'
        if (tokens.get(pos).getType().equals(TokenType.LBRACE)) {
            pos++;
            if (tokens.get(pos).getType().equals(TokenType.RBRACE)) {
                pos++;
                return new BlockNode(new ArrayList<BlockItemNode>(), tokens.get(pos - 1).getLineNum());
            }
            ArrayList<BlockItemNode> blockItemNodes = new ArrayList<>();
            blockItemNodes.add(parseBlockItem());
            while (!tokens.get(pos).getType().equals(TokenType.RBRACE)) {
                blockItemNodes.add(parseBlockItem());
            }
            pos++;
            return new BlockNode(blockItemNodes, tokens.get(pos - 1).getLineNum());
        } else {
            System.out.printf(tokens.get(pos).getValue());
            throw new RuntimeException("error");
        }
    }

    private BlockItemNode parseBlockItem() {
        //语句块项 BlockItem → Decl | Stmt
        if (tokens.get(pos).getType().equals(TokenType.CONSTTK)
                || tokens.get(pos).getType().equals(TokenType.INTTK)
                || tokens.get(pos).getType().equals(TokenType.CHARTK)) {
            DeclNode declNode = parseDecl();
            return new BlockItemNode(declNode, null);
        } else {
            StmtNode stmtNode = parseStmt();
            return new BlockItemNode(null, stmtNode);
        }
    }

    private StmtNode parseStmt() {
        if (tokens.get(pos).getType().equals(TokenType.PRINTFTK)) {
            int position1 = pos;
            int position = pos;
            pos++;
            if (tokens.get(pos).getType().equals(TokenType.LPARENT)) {
                pos++;
                StringConstNode stringConstNode = new StringConstNode(tokens.get(pos).getValue());
                pos++;
                ArrayList<ExpNode> printfExpNodes = new ArrayList<>();
                while(tokens.get(pos).getType().equals(TokenType.COMMA)) {
                    pos++;
                    position = pos;
                    printfExpNodes.add(parseExp());
                }
                if (tokens.get(pos).getType().equals(TokenType.RPARENT)) {
                    pos++;
                    if (tokens.get(pos).getType().equals(TokenType.SEMICN)) {
                        pos++;
                        StmtNode stmtNode = new StmtNode(1, stringConstNode, printfExpNodes, null, null, null, null, null, null, null, null, null);
                        stmtNode.setLineNum(tokens.get(position1).getLineNum());
                        return stmtNode;
                    } else {
                        isParserWrong = true;
                        OutWrongInformation.addError(new Error(tokens.get(position).getLineNum(), ErrorType.i));//.wrongInformation.put(position, tokens.get(position).getLineNum() + " i");//*
                        StmtNode stmtNode = new StmtNode(1, stringConstNode, printfExpNodes, null, null, null, null, null, null, null, null, null);
                        stmtNode.setLineNum(tokens.get(position1).getLineNum());
                        return stmtNode;
                    }
                } else {
                    isParserWrong = true;
                    OutWrongInformation.addError(new Error(tokens.get(position).getLineNum(), ErrorType.j));//wrongInformation.put(position, tokens.get(position).getLineNum() + " j");
                    if (tokens.get(pos).getType().equals(TokenType.SEMICN)) {
                        pos++;
                        StmtNode stmtNode = new StmtNode(1, stringConstNode, printfExpNodes, null, null, null, null, null, null, null, null, null);
                        stmtNode.setLineNum(tokens.get(position1).getLineNum());
                        return stmtNode;
                    } else {
                        isParserWrong = true;
                        OutWrongInformation.addError(new Error(tokens.get(position).getLineNum(), ErrorType.i));//wrongInformation.put(position, tokens.get(position).getLineNum() + " i");
                        StmtNode stmtNode = new StmtNode(1, stringConstNode, printfExpNodes, null, null, null, null, null, null, null, null, null);
                        stmtNode.setLineNum(tokens.get(position1).getLineNum());
                        return stmtNode;
                    }
                }
            } else {
                throw new RuntimeException("error");
            }
        } else if (tokens.get(pos).getType().equals(TokenType.RETURNTK)) {
            int positionLine = tokens.get(pos).getLineNum();
            pos++;
            if (tokens.get(pos).getType().equals(TokenType.SEMICN)) {
                pos++;
                StmtNode stmtNode = new StmtNode(4, null, null, null, null, null, null, null, null, null, null, null);
                stmtNode.setLineNum(positionLine);
                return stmtNode;
            } else {
                ExpNode expNode = parseExp();
                if (tokens.get(pos).getType().equals(TokenType.SEMICN) && expNode != null) {
                    pos++;
                    StmtNode stmtNode = new StmtNode(4, null, null, null, expNode, null, null, null, null, null, null, null);
                    stmtNode.setLineNum(positionLine);
                    return stmtNode;
                } else {
                    isParserWrong = true;
                    OutWrongInformation.addError(new Error(tokens.get(pos - 1).getLineNum(), ErrorType.i));//.wrongInformation.put(pos - 1, tokens.get(pos - 1).getLineNum() + " i");
                    StmtNode stmtNode = new StmtNode(4, null, null, null, expNode, null, null, null, null, null, null, null);
                    stmtNode.setLineNum(positionLine);
                    return stmtNode;
                }
            }
        } else if (tokens.get(pos).getType().equals(TokenType.BREAKTK)) {
            pos++;
            if (tokens.get(pos).getType().equals(TokenType.SEMICN)) {
                pos++;
                StmtNode stmtNode = new StmtNode(5, null, null, null, null, null, null, null, null, null, null, null);
                stmtNode.setLineNum(tokens.get(pos - 2).getLineNum());
                return stmtNode;
            } else {
                isParserWrong = true;
                OutWrongInformation.addError(new Error(tokens.get(pos - 1).getLineNum(), ErrorType.i));//.wrongInformation.put(pos - 1, tokens.get(pos - 1).getLineNum() + " i");
                StmtNode stmtNode = new StmtNode(5, null, null, null, null, null, null, null, null, null, null, null);
                stmtNode.setLineNum(tokens.get(pos - 1).getLineNum());
                return stmtNode;
            }
        } else if (tokens.get(pos).getType().equals(TokenType.CONTINUETK)) {
            pos++;
            if (tokens.get(pos).getType().equals(TokenType.SEMICN)) {
                pos++;
                StmtNode stmtNode = new StmtNode(6, null, null, null, null, null, null, null, null, null, null, null);
                stmtNode.setLineNum(tokens.get(pos - 2).getLineNum());
                return stmtNode;
            } else {
                isParserWrong = true;
                OutWrongInformation.addError(new Error(tokens.get(pos - 1).getLineNum(), ErrorType.i));//.wrongInformation.put(pos - 1, tokens.get(pos - 1).getLineNum() + " i");
                StmtNode stmtNode =  new StmtNode(6, null, null, null, null, null, null, null, null, null, null, null);
                stmtNode.setLineNum(tokens.get(pos - 1).getLineNum());
                return stmtNode;
            }
        } else if (tokens.get(pos).getType().equals(TokenType.IFTK)) {
            int position = pos + 2;
            pos++;
            if (tokens.get(pos).getType().equals(TokenType.LPARENT)) {
                pos++;
                CondNode condNode = parseCond();
                if (tokens.get(pos).getType().equals(TokenType.RPARENT)) {
                    pos++;
                    StmtNode ifStmtNode = parseStmt();
                    if (tokens.get(pos).getType().equals(TokenType.ELSETK)) {
                        pos++;
                        StmtNode elseStmtNode = parseStmt();
                        return new StmtNode(9, null, null, null, null, null, condNode, null, null, null, ifStmtNode, elseStmtNode);
                    } else {
                        return new StmtNode(8, null, null, null, null, null, condNode, null, null, null, ifStmtNode, null);
                    }
                } else {
                    isParserWrong = true;
                    OutWrongInformation.addError(new Error(tokens.get(position).getLineNum(), ErrorType.j));//.wrongInformation.put(position, tokens.get(position).getLineNum() + " j");
                    StmtNode ifStmtNode = parseStmt();
                    if (tokens.get(pos).getType().equals(TokenType.ELSETK)) {
                        pos++;
                        StmtNode elseStmtNode = parseStmt();
                        return new StmtNode(9, null, null, null, null, null, condNode, null, null, null, ifStmtNode, elseStmtNode);
                    } else {
                        return new StmtNode(8, null, null, null, null, null, condNode, null, null, null, ifStmtNode, null);
                    }
                }
            } else {
                throw  new RuntimeException("error");
            }
        } else if (tokens.get(pos).getType().equals(TokenType.LBRACE)) {
            BlockNode blockNode = parseBlock();
            return new StmtNode(10, null, null, null, null, null, null, null, null, blockNode, null, null);
        } else if (tokens.get(pos).getType().equals(TokenType.FORTK)) {
            pos++;
            if (tokens.get(pos).getType().equals(TokenType.LPARENT)) {
                pos++;
                if (tokens.get(pos).getType().equals(TokenType.SEMICN)) {//leftStmt为空
                    pos++;
                    if (tokens.get(pos).getType().equals(TokenType.SEMICN)) {
                        pos++;
                        if (tokens.get(pos).getType().equals(TokenType.RPARENT)) {
                            pos++;
                            StmtNode forStmtNode = parseStmt();
                            return new StmtNode(7, null, null, null, null, null, null, null, forStmtNode, null, null, null);
                        } else {
                            ForStmtNode rightForStmtNode = parseForStmt();
                            if (tokens.get(pos).getType().equals(TokenType.RPARENT)) {
                                pos++;
                                StmtNode forStmtNode = parseStmt();
                                return new StmtNode(7, null, null, null, null, null, null, rightForStmtNode, forStmtNode, null, null, null);
                            } else {
                                throw new RuntimeException("error");
                            }
                        }
                    } else {
                        CondNode condNode = parseCond();
                        if (tokens.get(pos).getType().equals(TokenType.SEMICN)) {
                            pos++;
                            if (tokens.get(pos).getType().equals(TokenType.RPARENT)) {
                                pos++;
                                StmtNode forStmtNode = parseStmt();
                                return new StmtNode(7, null, null, null, null, null, condNode, null, forStmtNode, null, null, null);
                            } else {
                                ForStmtNode rightForStmtNode = parseForStmt();
                                if (tokens.get(pos).getType().equals(TokenType.RPARENT)) {
                                    pos++;
                                    StmtNode forStmtNode = parseStmt();
                                    return new StmtNode(7, null, null, null, null, null, condNode, rightForStmtNode, forStmtNode, null, null, null);
                                } else {
                                    throw new RuntimeException("error");
                                }
                            }
                        } else {
                            throw new RuntimeException("error");
                        }
                    }
                } else {//leftStmt不空
                    ForStmtNode leftForStmtNode = parseForStmt();
                    if (tokens.get(pos).getType().equals(TokenType.SEMICN)) {
                        pos++;
                        if (tokens.get(pos).getType().equals(TokenType.SEMICN)) {//cond为空
                            pos++;
                            if (tokens.get(pos).getType().equals(TokenType.RPARENT)) {
                                pos++;
                                StmtNode forStmtNode = parseStmt();
                                return new StmtNode(7, null, null, null, null, leftForStmtNode, null, null, forStmtNode, null, null, null);
                            } else {
                                ForStmtNode rightForStmtNode = parseForStmt();
                                if (tokens.get(pos).getType().equals(TokenType.RPARENT)) {
                                    pos++;
                                    StmtNode forStmtNode = parseStmt();
                                    return new StmtNode(7, null, null, null, null, leftForStmtNode, null, rightForStmtNode, forStmtNode, null, null, null);
                                } else {
                                    throw new RuntimeException("error");
                                }
                            }
                        } else {
                            CondNode condNode = parseCond();
                            if (tokens.get(pos).getType().equals(TokenType.SEMICN)) {
                                pos++;
                                if (tokens.get(pos).getType().equals(TokenType.RPARENT)) {
                                    pos++;
                                    StmtNode forStmtNode = parseStmt();
                                    return new StmtNode(7, null, null, null, null, leftForStmtNode, condNode, null, forStmtNode, null, null, null);
                                } else {
                                    ForStmtNode rightForStmtNode = parseForStmt();
                                    if (tokens.get(pos).getType().equals(TokenType.RPARENT)) {
                                        pos++;
                                        StmtNode forStmtNode = parseStmt();
                                        return new StmtNode(7, null, null, null, null, leftForStmtNode, condNode, rightForStmtNode, forStmtNode, null, null, null);
                                    } else {
                                        throw new RuntimeException("error");
                                    }
                                }
                            } else {
                                throw new RuntimeException("error");
                            }
                        }
                    } else {
                        throw new RuntimeException("error");
                    }
                }
            } else {
                throw new RuntimeException("error");
            }
        } else {
            int i = pos;
            int flag = 0;
            int lineNum = tokens.get(i).getLineNum();
            while (lineNum == tokens.get(i).getLineNum()) {
                if (tokens.get(i).getType().equals(TokenType.ASSIGN)) {
                    flag = 1;System.out.print(tokens.get(i).getValue() + tokens.get(i - 1).getValue());
                    break;
                }
                i++;
            }
            if (flag == 1) {
                //| LVal '=' 'getint''('')'';'------3
                //| LVal '=' 'getchar''('')'';'-----2
                //LVal '=' Exp ';'
                int position = pos;
                LValNode lValNode = parseLVal();
                if (tokens.get(pos).getType().equals(TokenType.ASSIGN)) {
                    pos++;
                    if (tokens.get(pos).getType().equals(TokenType.GETINTTK)) {
                        pos++;
                        if (tokens.get(pos).getType().equals(TokenType.LPARENT)
                                && tokens.get(pos + 1).getType().equals(TokenType.RPARENT)
                                && tokens.get(pos + 2).getType().equals(TokenType.SEMICN)) {
                            pos += 3;
                            return new StmtNode(3, null, null, lValNode, null, null, null, null, null, null, null, null);
                        } else {
                            if (tokens.get(pos).getType().equals(TokenType.LPARENT)
                                    && !tokens.get(pos + 1).getType().equals(TokenType.RPARENT)) {
                                isParserWrong = true;
                                OutWrongInformation.addError(new Error(tokens.get(position).getLineNum(), ErrorType.j));//.wrongInformation.put(position, tokens.get(position).getLineNum() + " j");
                                pos += 2;
                                return new StmtNode(3, null, null, lValNode, null, null, null, null, null, null, null, null);
                            }
                            if (tokens.get(pos).getType().equals(TokenType.LPARENT)
                                    && tokens.get(pos + 1).getType().equals(TokenType.RPARENT)
                                    && !tokens.get(pos + 2).getType().equals(TokenType.SEMICN)) {
                                isParserWrong = true;
                                OutWrongInformation.addError(new Error(tokens.get(position).getLineNum(), ErrorType.i));//.wrongInformation.put(position, tokens.get(position).getLineNum() + " i");
                                pos += 2;
                                return new StmtNode(3, null, null, lValNode, null, null, null, null, null, null, null, null);
                            }
                            throw new RuntimeException("error");
                        }
                    } else if (tokens.get(pos).getType().equals(TokenType.GETCHARTK)) {
                        pos++;
                        if (tokens.get(pos).getType().equals(TokenType.LPARENT)
                                && tokens.get(pos + 1).getType().equals(TokenType.RPARENT)
                                && tokens.get(pos + 2).getType().equals(TokenType.SEMICN)) {
                            pos += 3;
                            return new StmtNode(2, null, null, lValNode, null, null, null, null, null, null, null, null);
                        } else {
                            if (tokens.get(pos).getType().equals(TokenType.LPARENT)
                                    && !tokens.get(pos + 1).getType().equals(TokenType.RPARENT)) {
                                isParserWrong = true;
                                OutWrongInformation.addError(new Error(tokens.get(position).getLineNum(), ErrorType.j));//.wrongInformation.put(position, tokens.get(position).getLineNum() + " j");//*
                                pos += 2;
                                return new StmtNode(2, null, null, lValNode, null, null, null, null, null, null, null, null);
                            }
                            if (tokens.get(pos).getType().equals(TokenType.LPARENT)
                                    && tokens.get(pos + 1).getType().equals(TokenType.RPARENT)
                                    && !tokens.get(pos + 2).getType().equals(TokenType.SEMICN)) {
                                isParserWrong = true;
                                OutWrongInformation.addError(new Error(tokens.get(position).getLineNum(), ErrorType.i));//.wrongInformation.put(position, tokens.get(position).getLineNum() + " i");
                                pos += 2;
                                return new StmtNode(2, null, null, lValNode, null, null, null, null, null, null, null, null);
                            }
                            throw new RuntimeException("error");
                        }
                    } else {
                        int position1 = pos;
                        ExpNode expNode = parseExp();
                        if (tokens.get(pos).getType().equals(TokenType.SEMICN)) {
                            pos++;
                            return new StmtNode(12, null, null, lValNode, expNode, null, null, null, null, null, null, null);
                        } else {
                            isParserWrong = true;
                            OutWrongInformation.addError(new Error(tokens.get(position1).getLineNum(), ErrorType.i));//.wrongInformation.put(position1, tokens.get(position1).getLineNum() + " i");
                            return new StmtNode(12, null, null, lValNode, expNode, null, null, null, null, null, null, null);
                        }
                    }
                } else {
                    throw new RuntimeException("ji");
                }
            } else {//Stmt -> [Exp] ';'----11
                if (tokens.get(pos).getType().equals(TokenType.SEMICN)) {
                    pos++;
                    return new StmtNode(11, null, null, null, null, null, null, null, null, null, null, null);
                } else {
                    int position = pos;
                    ExpNode expNode = parseExp();
                    if (tokens.get(pos).getType().equals(TokenType.SEMICN)) {
                        pos++;
                        return new StmtNode(11, null, null, null, expNode, null, null, null, null, null, null, null);
                    } else {
                        isParserWrong = true;
                        OutWrongInformation.addError(new Error(tokens.get(position).getLineNum(), ErrorType.i));//.wrongInformation.put(position, tokens.get(position).getLineNum() + " i");
                        return new StmtNode(11, null, null, null, expNode, null, null, null, null, null, null, null);
                    }
                }
            }
        }
    }

    private ForStmtNode parseForStmt() {
        //语句 ForStmt → LVal '=' Exp
        LValNode lValNode = parseLVal();
        if (tokens.get(pos).getType().equals(TokenType.ASSIGN)) {
            pos++;
            ExpNode expNode = parseExp();
            return new ForStmtNode(lValNode, expNode);
        } else {
            throw new RuntimeException("SSS");
        }
    }

    private CondNode parseCond() {
        //Cond -> LOrExp
        LOrExpNode lOrExpNode = parseLOrExp();
        return new CondNode(lOrExpNode);
    }

    private LOrExpNode parseLOrExp() {
        //LOrExp → LAndExp ['||' LOrExp]
        LAndExpNode lAndExpNode = parseLAndExp();
        if (tokens.get(pos).getType().equals(TokenType.OR)) {
            pos++;
            LOrExpNode lOrExpNode = parseLOrExp();
            return new LOrExpNode(lAndExpNode, lOrExpNode);
        }
        return new LOrExpNode(lAndExpNode, null);
    }

    private LAndExpNode parseLAndExp() {
        //LAndExp → EqExp ['&&' LAndExp]
        EqExpNode eqExpNode = parseEqExp();
        if (tokens.get(pos).getType().equals(TokenType.AND)) {
            pos++;
            LAndExpNode lAndExpNode = parseLAndExp();
            return new LAndExpNode(eqExpNode, lAndExpNode);
        }
        return new LAndExpNode(eqExpNode, null);
    }

    private EqExpNode parseEqExp() {
        //相等性表达式 EqExp → RelExp | EqExp ('==' | '!=') RelExp
        //改为EqExp -> RelExp [ ('==' | '!=') EqExp]
        RelExpNode relExpNode = parseRelExp();
        if (tokens.get(pos).getType().equals(TokenType.EQL)) {
            pos++;
            EqExpNode eqExpNode = parseEqExp();
            return new EqExpNode(relExpNode, TokenType.EQL, eqExpNode);
        } else if (tokens.get(pos).getType().equals(TokenType.NEQ)) {
            pos++;
            EqExpNode eqExpNode = parseEqExp();
            return new EqExpNode(relExpNode, TokenType.NEQ, eqExpNode);
        }
        return new EqExpNode(relExpNode, null, null);
    }

    private RelExpNode parseRelExp() {
        //RelExp → AddExp [('<' | '>' | '<=' | '>=') RelExp]
        AddExpNode addExpNode = parseAddExp();
        if (tokens.get(pos).getType().equals(TokenType.LSS)) {
            pos++;
            RelExpNode relExpNode = parseRelExp();
            return new RelExpNode(addExpNode, TokenType.LSS, relExpNode);
        } else if (tokens.get(pos).getType().equals(TokenType.GRE)) {
            pos++;
            RelExpNode relExpNode = parseRelExp();
            return new RelExpNode(addExpNode, TokenType.GRE, relExpNode);
        } else if (tokens.get(pos).getType().equals(TokenType.LEQ)) {
            pos++;
            RelExpNode relExpNode = parseRelExp();
            return new RelExpNode(addExpNode, TokenType.LEQ, relExpNode);
        } else if (tokens.get(pos).getType().equals(TokenType.GEQ)) {
            pos++;
            RelExpNode relExpNode = parseRelExp();
            return new RelExpNode(addExpNode, TokenType.GEQ, relExpNode);
        }
        return new RelExpNode(addExpNode, null, null);
    }

    private FuncFParamsNode parseFuncFParams() {
        //函数形参表 FuncFParams → FuncFParam { ',' FuncFParam }
        ArrayList<FuncFParamNode> funcFParamNodes = new ArrayList<>();
        funcFParamNodes.add(parseFuncFParam());
        while(tokens.get(pos).getType().equals(TokenType.COMMA)) {
            pos++;
            funcFParamNodes.add(parseFuncFParam());
        }
        return new FuncFParamsNode(funcFParamNodes);
    }

    private FuncFParamNode parseFuncFParam() {
        if (tokens.get(pos).getType().equals(TokenType.INTTK)
                || tokens.get(pos).getType().equals(TokenType.CHARTK)) {
            BTypeNode bTypeNode = parseBType();
            IdentNode identNode = parseIdent();
            if (tokens.get(pos).getType().equals(TokenType.LBRACK)) {
                pos++;
                if (tokens.get(pos).getType().equals(TokenType.RBRACK)) {
                    pos++;
                    return new FuncFParamNode(bTypeNode, identNode, true);
                } else {
                    isParserWrong = true;
                    OutWrongInformation.addError(new Error(tokens.get(pos - 2).getLineNum(), ErrorType.k));//.wrongInformation.put(pos - 2, tokens.get(pos - 2).getLineNum() + " k");
                    return new FuncFParamNode(bTypeNode, identNode, true);
                }
            } else {
                return new FuncFParamNode(bTypeNode, identNode,  false);
            }
        } else {
            throw new RuntimeException("error");
        }
    }


    private IdentNode parseIdent() {
        if (tokens.get(pos).getType().equals(TokenType.IDENFR)) {
            return new IdentNode(tokens.get(pos++).getValue(), tokens.get(pos - 1).getLineNum());
        } else {
            throw new RuntimeException("error");
        }
    }

    private DeclNode parseDecl() {
        //Decl → ConstDecl | VarDecl
        ConstDeclNode constDeclNode;
        VarDeclNode varDeclNode;
        int lineNum = tokens.get(pos).getLineNum();
        if (tokens.get(pos).getType().equals(TokenType.CONSTTK)) {
            constDeclNode = parseConstDecl();
            return new DeclNode(constDeclNode, null, lineNum);
        } else {
            varDeclNode = parseVarDecl();
            return new DeclNode(null, varDeclNode, lineNum);
        }
    }

    private VarDeclNode parseVarDecl() {
        // VarDecl → BType VarDef { ',' VarDef } ';'
        if (tokens.get(pos).getType().equals(TokenType.INTTK)
                || tokens.get(pos).getType().equals(TokenType.CHARTK)) {
            BTypeNode bTypeNode = parseBType();
            ArrayList<VarDefNode> varDefNodes = new ArrayList<>();
            int position = pos;
            varDefNodes.add(parseVarDef());
            while (tokens.get(pos).getType().equals(TokenType.COMMA)) {
                pos++;
                position = pos;
                varDefNodes.add(parseVarDef());
            }
            if(tokens.get(pos).getType().equals(TokenType.SEMICN)) {
                pos++;
                return new VarDeclNode(bTypeNode, varDefNodes);
            } else {
                isParserWrong = true;
                OutWrongInformation.addError(new Error(tokens.get(position).getLineNum(), ErrorType.i));//.wrongInformation.put(position, tokens.get(position).getLineNum() + " i");
                return new VarDeclNode(bTypeNode, varDefNodes);
            }
        } else {
            throw new RuntimeException("error");//error
        }
    }

    private VarDefNode parseVarDef() {
        //变量定义 VarDef → Ident [ '[' ConstExp ']' ] | Ident [ '[' ConstExp ']' ] '=' InitVal
        //改为 VarDef → Ident [ '[' ConstExp ']' ] [ '=' InitVal]
        if (tokens.get(pos).getType().equals(TokenType.IDENFR)) {
            IdentNode identNode = parseIdent();
            if (tokens.get(pos).getType().equals(TokenType.LBRACK)) {
                pos++;
                int position = pos;
                ConstExpNode constExpNode = parseConstExp();
                if (tokens.get(pos).getType().equals(TokenType.RBRACK)) {
                    pos++;
                    if (tokens.get(pos).getType().equals(TokenType.ASSIGN)) {
                        pos++;
                        InitValNode initValNode = parseInitVal();
                        return new VarDefNode(identNode, constExpNode, initValNode, identNode.getLineNum());
                    } else {
                        return new VarDefNode(identNode, constExpNode, null, identNode.getLineNum());
                    }
                } else {
                    isParserWrong = true;
                    OutWrongInformation.addError(new Error(tokens.get(position).getLineNum(), ErrorType.k));//.wrongInformation.put(position, tokens.get(position).getLineNum() + " k");
                    if (tokens.get(pos).getType().equals(TokenType.ASSIGN)) {
                        pos++;
                        InitValNode initValNode = parseInitVal();
                        return new VarDefNode(identNode, constExpNode, initValNode, identNode.getLineNum());
                    } else {
                        return new VarDefNode(identNode, constExpNode, null, identNode.getLineNum());
                    }
                }
            } else if (tokens.get(pos).getType().equals(TokenType.ASSIGN)) {
                    pos++;
                    InitValNode initValNode = parseInitVal();
                return new VarDefNode(identNode, null, initValNode, identNode.getLineNum());
            } else {
                return new VarDefNode(identNode, null, null, identNode.getLineNum());
            }
        } else {
            System.out.printf(tokens.get(pos).getValue() + tokens.get(pos).getLineNum());
            throw new RuntimeException("error");
        }
    }

    private InitValNode parseInitVal() {
        //变量初值 InitVal → Exp | '{' [ Exp { ',' Exp } ] '}' | StringConst
        if (tokens.get(pos).getType().equals(TokenType.STRCON)) {
            return new InitValNode(null, null, new StringConstNode(tokens.get(pos++).getValue()));
        } else if (tokens.get(pos).getType().equals(TokenType.LBRACE)) {
            ArrayList<ExpNode> expNodes = new ArrayList<>();
            pos++;
            if (tokens.get(pos).getType().equals(TokenType.RBRACE)) {
                pos++;
                return new InitValNode(null, null, null);
            } else {
                expNodes.add(parseExp());
                while (tokens.get(pos).getType().equals(TokenType.COMMA)) {
                    pos++;
                    expNodes.add(parseExp());
                }
                if (tokens.get(pos).getType().equals(TokenType.RBRACE)) {
                    pos++;
                    return new InitValNode(null, expNodes, null);
                } else {
                    throw new RuntimeException("error");
                }
            }
        } else {
            return new InitValNode(parseExp(), null, null);
        }
    }

    private ConstDeclNode parseConstDecl() {
        //ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'
        if (tokens.get(pos).getType().equals(TokenType.CONSTTK)) {
            pos++;
            BTypeNode bTypeNode = parseBType();
            ArrayList<ConstDefNode> constDefNodes = new ArrayList<>();
            int position = pos;
            constDefNodes.add(parseConstDef());
            while (tokens.get(pos).getType().equals(TokenType.COMMA)) {
                pos++;
                position = pos;
                constDefNodes.add(parseConstDef());
            }
            if(tokens.get(pos).getType().equals(TokenType.SEMICN)) {
                pos++;
                return new ConstDeclNode(bTypeNode, constDefNodes);
            } else {
                isParserWrong = true;
                OutWrongInformation.addError(new Error(tokens.get(position).getLineNum(), ErrorType.i));//.wrongInformation.put(position, tokens.get(position).getLineNum() + " i");
                return new ConstDeclNode(bTypeNode, constDefNodes);
            }
        } else {
            throw new RuntimeException("error");//error
        }
    }

    private ConstDefNode parseConstDef() {
        //ConstDef → Ident [ '[' ConstExp ']' ] '=' ConstInitVal
        if (tokens.get(pos).getType().equals(TokenType.IDENFR)) {
            String identName = tokens.get(pos).getValue();
            int identPos = pos;
            pos++;
            if (tokens.get(pos).getType().equals(TokenType.LBRACK)) {
                pos++;
                int position = pos;
                ConstExpNode constExpNode = parseConstExp();
                if (tokens.get(pos).getType().equals(TokenType.RBRACK)) {
                    pos++;
                    if (tokens.get(pos).getType().equals(TokenType.ASSIGN)) {
                        pos++;
                        ConstInitValNode constInitValNode = parseConstInitVal();
                        return new ConstDefNode(new IdentNode(identName, tokens.get(position - 2).getLineNum()), constExpNode, constInitValNode);
                    } else {
                        throw new RuntimeException("error");
                    }
                } else {
                    isParserWrong = true;
                    OutWrongInformation.addError(new Error(tokens.get(position).getLineNum(), ErrorType.k));//.wrongInformation.put(position, tokens.get(position).getLineNum() + " k");
                    if (tokens.get(pos).getType().equals(TokenType.ASSIGN)) {
                        pos++;
                        ConstInitValNode constInitValNode = parseConstInitVal();
                        return new ConstDefNode(new IdentNode(identName, tokens.get(position - 2).getLineNum()), constExpNode, constInitValNode);
                    } else {
                        throw new RuntimeException("error");
                    }
                }
            } else if (tokens.get(pos).getType().equals(TokenType.ASSIGN)){
                pos++;
                ConstInitValNode constInitValNode = parseConstInitVal();
                return new ConstDefNode(new IdentNode(identName, tokens.get(identPos).getLineNum()), null, constInitValNode);
            } else {
                throw new RuntimeException("error");
            }
        } else {
            System.out.printf(tokens.get(pos).getValue());
            throw new RuntimeException("error");
        }
    }

    private ConstInitValNode parseConstInitVal() {
        //常量初值 ConstInitVal → ConstExp | '{' [ ConstExp { ',' ConstExp } ] '}' | StringConst
        if (tokens.get(pos).getType().equals(TokenType.STRCON)) {
            return new ConstInitValNode(null, null, new StringConstNode(tokens.get(pos++).getValue()));
        } else if (tokens.get(pos).getType().equals(TokenType.LBRACE)) {
            ArrayList<ConstExpNode> constExpNodes = new ArrayList<>();
            pos++;
            if (tokens.get(pos).getType().equals(TokenType.RBRACE)) {
                pos++;
                return new ConstInitValNode(null, new ArrayList<>(), null);
            } else {
                constExpNodes.add(parseConstExp());
                while (tokens.get(pos).getType().equals(TokenType.COMMA)) {
                    pos++;
                    constExpNodes.add(parseConstExp());
                }
                if (tokens.get(pos).getType().equals(TokenType.RBRACE)) {
                    pos++;
                    return new ConstInitValNode(null, constExpNodes, null);
                } else {
                    throw new RuntimeException("error");
                }
            }
        } else {
            return new ConstInitValNode(parseConstExp(), null, null);
        }
    }

    private ConstExpNode parseConstExp() {
        return new ConstExpNode(parseAddExp());
    }

    private AddExpNode parseAddExp() {
        //AddExp → MulExp ['+'/'-'AddExp]
        MulExpNode mulExpNode = parseMulExp();
        AddExpNode addExpNode;
        if (tokens.get(pos).getType().equals(TokenType.PLUS)
                || tokens.get(pos).getType().equals(TokenType.MINU)) {
            TokenType op = tokens.get(pos).getType();
            pos++;
            addExpNode = parseAddExp();
            return new AddExpNode(mulExpNode, op, addExpNode);
        }
        return new AddExpNode(mulExpNode, null, null);
    }

    private MulExpNode parseMulExp() {
        //MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
        //改为MulExp → UnaryExp [ ('*' | '/' | '%') MulExp]
        UnaryExpNode unaryExpNode = parseUnaryExp();
        MulExpNode mulExpNode;
        if (tokens.get(pos).getType().equals(TokenType.MULT)
                || tokens.get(pos).getType().equals(TokenType.DIV)
                || tokens.get(pos).getType().equals(TokenType.MOD)) {
            TokenType op = tokens.get(pos).getType();
            pos++;
            mulExpNode = parseMulExp();
            return new MulExpNode(unaryExpNode, op, mulExpNode);
        }
        return new MulExpNode(unaryExpNode, null, null);
    }

    private UnaryExpNode parseUnaryExp() {
        //一元表达式 UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp
        if (tokens.get(pos).getType().equals(TokenType.PLUS)
                || tokens.get(pos).getType().equals(TokenType.MINU)
                || tokens.get(pos).getType().equals(TokenType.NOT)) {
            UnaryOpNode unaryOpNode = new UnaryOpNode(tokens.get(pos).getType());
            pos++;
            UnaryExpNode unaryExpNode = parseUnaryExp();
            return new UnaryExpNode(null, null, null, unaryOpNode, unaryExpNode);
        } else if (tokens.get(pos).getType().equals(TokenType.IDENFR)
                && tokens.get(pos + 1).getType().equals(TokenType.LPARENT)) {
            IdentNode identNode = new IdentNode(tokens.get(pos).getValue(), tokens.get(pos).getLineNum());
            pos += 2;
            if (tokens.get(pos).getType().equals(TokenType.RPARENT)) {
                pos++;
                return new UnaryExpNode(null, identNode,  null, null, null);
            }
            int position = pos;
            FuncRParamsNode funcRParamsNode = parseFuncRParams();
            //pos++;
            if (tokens.get(pos).getType().equals(TokenType.RPARENT)) {
                pos++;
                return new UnaryExpNode(null, identNode, funcRParamsNode, null, null);
            } else {
                isParserWrong = true;
                OutWrongInformation.addError(new Error(tokens.get(position).getLineNum(), ErrorType.j));//.wrongInformation.put(position, tokens.get(position).getLineNum() + " j");
                return new UnaryExpNode(null, identNode, funcRParamsNode, null, null);
            }
        } else{
            return new UnaryExpNode(parsePrimaryExp(), null, null, null, null);
        }
    }

    private PrimaryExpNode parsePrimaryExp() {
        // PrimaryExp →'(' Exp ')' | LVal | Number | Character
        //LVal → Ident ['[' Exp ']']
        if (tokens.get(pos).getType().equals(TokenType.LPARENT)) {
            pos++;
            int position = pos;
            ExpNode expNode = parseExp();
            if (tokens.get(pos).getType().equals(TokenType.RPARENT)) {
                pos++;
                return new PrimaryExpNode(expNode, null, null, null);
            } else {
                isParserWrong = true;
                OutWrongInformation.addError(new Error(tokens.get(position).getLineNum(), ErrorType.j));//.wrongInformation.put(position, tokens.get(position).getLineNum() + " j");
                return new PrimaryExpNode(expNode, null, null, null);
            }
        } else if (tokens.get(pos).getType().equals(TokenType.IDENFR)) {
            return new PrimaryExpNode(null, parseLVal(), null, null);
        } else if (tokens.get(pos).getType().equals(TokenType.CHRCON)) {
            CharacterNode characterNode = new CharacterNode(tokens.get(pos).getValue());
            pos++;
            return new PrimaryExpNode(null, null, null, characterNode);
        } else if (tokens.get(pos).getType().equals(TokenType.INTCON)) {
            NumberNode numberNode = new NumberNode(tokens.get(pos).getValue());
            pos++;
            return new PrimaryExpNode(null, null, numberNode, null);
        } else {
            return null;
            /*System.out.printf(tokens.get(pos-2).getValue() + tokens.get(pos-2).getLineNum());
            System.out.printf(tokens.get(pos-1).getValue() + tokens.get(pos-1).getLineNum());
            System.out.printf(tokens.get(pos).getValue() + tokens.get(pos).getLineNum());
            throw new RuntimeException("error");*/
        }
    }

    private LValNode parseLVal() {
        //LVal → Ident ['[' Exp ']']
        if (tokens.get(pos).getType().equals(TokenType.IDENFR)) {
            IdentNode identNode = new IdentNode(tokens.get(pos).getValue(), tokens.get(pos).getLineNum());
            pos++;
            if (tokens.get(pos).getType().equals(TokenType.LBRACK)) {
                pos++;
                int position = pos;
                ExpNode expNode = parseExp();
                if (tokens.get(pos).getType().equals(TokenType.RBRACK)) {
                    pos++;
                    return new LValNode(identNode, expNode);
                } else {
                    isParserWrong = true;
                    OutWrongInformation.addError(new Error(tokens.get(position).getLineNum(), ErrorType.k));//.wrongInformation.put(position, tokens.get(position).getLineNum() + " k");
                    return new LValNode(identNode, expNode);
                }
            } else {
                return new LValNode(identNode, null);
            }
        } else {
            throw new RuntimeException("error");
        }
    }

    private FuncRParamsNode parseFuncRParams() {
        //FuncRParams → Exp { ',' Exp }
        ArrayList<ExpNode> expNodes = new ArrayList<>();
        expNodes.add(parseExp());
        while (tokens.get(pos).getType().equals(TokenType.COMMA)) {
            pos++;
            expNodes.add(parseExp());
        }
        return new FuncRParamsNode(expNodes);
    }

    private ExpNode parseExp() {
        return new ExpNode(parseAddExp());
    }

    private BTypeNode parseBType() {
        //BType → 'int' | 'char'
        if (tokens.get(pos).getType().equals(TokenType.INTTK)) {
            pos++;
            return new BTypeNode(TokenType.INTTK);
        } else if (tokens.get(pos).getType().equals(TokenType.CHARTK)) {
            pos++;
            return new BTypeNode(TokenType.CHARTK);
        } else {
            throw new RuntimeException("error");//error
        }
    }

    public boolean getIsParserWrong() {
        return isParserWrong;
    }
}
