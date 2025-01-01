package frontend;
import error.Error;
import error.ErrorType;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Lexer {
    private ArrayList<Token> tokens = new ArrayList<Token>();
    private HashMap<String, TokenType> keywords = new HashMap<String, TokenType>();
    private boolean isLexerWrong = false;
    private ArrayList<String> lexerWrongInformation = new ArrayList<String>();

    public Lexer(String testfile) throws IOException {
        initKeyWords();
        String source = read(testfile);
        int pos = 0;
        int lineNum = 1;
        char ch;
        while (pos < source.length()) {
            ch = source.charAt(pos);
            if (ch == '\n') {
                lineNum++;
                pos++;
                continue;
            }
            String s = "";
            if (Character.isLetter(ch) || ch == '_') {
                s += ch;
                pos++;
                while(Character.isLetter(ch = source.charAt(pos)) || Character.isDigit(ch) || ch == '_') {
                    s += ch;
                    pos++;
                }
                if (keywords.containsKey(s)) {
                    tokens.add(new Token(keywords.get(s), s, lineNum));
                } else {
                    tokens.add(new Token(TokenType.IDENFR, s, lineNum));
                }
                pos--;
            } else if (Character.isDigit(ch)) {
                s += ch;
                pos++;
                while(Character.isDigit(ch = source.charAt(pos))) {
                    s += ch;
                    pos++;
                }
                tokens.add(new Token(TokenType.INTCON, s, lineNum));
                pos--;
            } else if (ch == '\'') {
                boolean escape = false;
                s += ch;
                pos++;
                ch = source.charAt(pos);
                if (ch == '\\') {
                    escape = true;
                }
                s += ch;
                pos++;
                if (escape == false) {
                    if ((ch = source.charAt(pos)) != '\'') {
                        //error
                        return;
                    } else {
                        s += ch;
                        tokens.add(new Token(TokenType.CHRCON, s, lineNum));
                    }
                } else {
                    ch = source.charAt(pos);
                    //需判断转义字符是否合法
                    s += ch;
                    pos++;
                    if ((ch = source.charAt(pos)) != '\'') {
                        //error
                        return;
                    } else {
                        s += ch;
                        tokens.add(new Token(TokenType.CHRCON, s, lineNum));
                    }
                }
            } else if (ch == '"') {
                s += ch;
                pos++;
                while ((ch = source.charAt(pos)) != '"') {
                    s += ch;
                    pos++;
                }
                s += ch;
                tokens.add(new Token(TokenType.STRCON, s, lineNum));
            } else if (ch == '&') {
                s += ch;
                pos++;
                if ((ch = source.charAt(pos)) == '&') {
                    s += ch;
                    tokens.add((new Token(TokenType.AND, s, lineNum)));
                } else {
                    isLexerWrong = true;
                    lexerWrongInformation.add(lineNum + " " + 'a');
                    OutWrongInformation.addError(new Error(lineNum, ErrorType.a));
                    tokens.add((new Token(TokenType.AND, "&&", lineNum)));
                    //ERROR;
                }
            } else if (ch == '|') {
                s += ch;
                pos++;
                if ((ch = source.charAt(pos)) == '|') {
                    s += ch;
                    tokens.add(new Token(TokenType.OR, s, lineNum));
                } else {
                    isLexerWrong = true;
                    lexerWrongInformation.add(lineNum + " " + 'a');
                    OutWrongInformation.addError(new Error(lineNum, ErrorType.a));
                    tokens.add(new Token(TokenType.OR, "||", lineNum));
                    //ERROR;
                }
            } else if (ch == '!' || ch == '<' || ch == '>' || ch == '=') {
                s += ch;
                pos++;
                if ((ch = source.charAt(pos)) == '=') {
                    s += ch;
                    tokens.add(new Token(keywords.get(s), s, lineNum));
                } else {
                    tokens.add(new Token(keywords.get(s), s, lineNum));
                    pos--;
                }
            } else if (ch == '/') {
                s += ch;
                pos++;
                ch = source.charAt(pos);
                if (ch == '/') {
                    s += ch;
                    pos++;
                    while (pos < source.length() && (ch = source.charAt(pos)) != '\n') {
                        pos++;
                    }
                    lineNum += 1;
                } else if (ch == '*') {
                    s += ch;
                    pos++;
                    while ((ch = source.charAt(pos)) != '*' || source.charAt(pos + 1) != '/') {
                        pos++;
                        if (ch == '\n') {
                            lineNum += 1;
                        }
                    }
                    pos += 1;
                } else {
                    tokens.add(new Token(TokenType.DIV, s, lineNum));
                    pos--;
                }
            } else if (keywords.containsKey(s + ch)) {
                tokens.add(new Token(keywords.get(s + ch), s + ch, lineNum));
            }
            pos++;
        }
    }

    private void initKeyWords() {
        this.keywords.put("main", TokenType.MAINTK);
        this.keywords.put("const", TokenType.CONSTTK);
        this.keywords.put("int", TokenType.INTTK);
        this.keywords.put("char", TokenType.CHARTK);
        this.keywords.put("break", TokenType.BREAKTK);
        this.keywords.put("continue", TokenType.CONTINUETK);
        this.keywords.put("if", TokenType.IFTK);
        this.keywords.put("else", TokenType.ELSETK);
        this.keywords.put("!", TokenType.NOT);
        this.keywords.put("+", TokenType.PLUS);
        this.keywords.put("-", TokenType.MINU);
        this.keywords.put("*", TokenType.MULT);
        this.keywords.put("/", TokenType.DIV);
        this.keywords.put("%", TokenType.MOD);
        this.keywords.put("<", TokenType.LSS);
        this.keywords.put(">", TokenType.GRE);
        this.keywords.put("=", TokenType.ASSIGN);
        this.keywords.put(";", TokenType.SEMICN);
        this.keywords.put(",", TokenType.COMMA);
        this.keywords.put("(", TokenType.LPARENT);
        this.keywords.put(")", TokenType.RPARENT);
        this.keywords.put("[", TokenType.LBRACK);
        this.keywords.put("]", TokenType.RBRACK);
        this.keywords.put("{", TokenType.LBRACE);
        this.keywords.put("}", TokenType.RBRACE);
        this.keywords.put("!=", TokenType.NEQ);
        this.keywords.put("<=", TokenType.LEQ);
        this.keywords.put(">=", TokenType.GEQ);
        this.keywords.put("==", TokenType.EQL);
        this.keywords.put("printf", TokenType.PRINTFTK);
        this.keywords.put("for", TokenType.FORTK);
        this.keywords.put("void", TokenType.VOIDTK);
        this.keywords.put("return", TokenType.RETURNTK);
        this.keywords.put("getint", TokenType.GETINTTK);
        this.keywords.put("getchar", TokenType.GETCHARTK);
    }

    private String read(String testfile) throws IOException {
        InputStream in = new BufferedInputStream(Files.newInputStream(Paths.get(testfile)));
        Scanner scanner = new Scanner(in);
        StringJoiner stringJoiner = new StringJoiner("\n");
        while (scanner.hasNextLine()) {
            stringJoiner.add(scanner.nextLine());
        }
        scanner.close();
        in.close();
        return stringJoiner.toString();
    }

    public ArrayList<Token> getTokens() {
        return this.tokens;
    }

    public boolean getIsLexerWrong() {
        return isLexerWrong;
    }

    public ArrayList<String> getLexerWrongInformation() {
        return lexerWrongInformation;
    }
}
