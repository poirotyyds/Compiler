package frontend;

public class Token {

    private TokenType type;
    private String value;
    private int lineNum;

    public Token(TokenType type, String value, int lineNum) {
        this.type = type;
        this.value = value;
        this.lineNum = lineNum;
    }

    public String getValue () {
        return this.value;
    }

    public int getLineNum() {
        return this.lineNum;
    }

    public String getTypeString() {
        return type.toString();
    }

    public TokenType getType() {
        return type;
    }
}