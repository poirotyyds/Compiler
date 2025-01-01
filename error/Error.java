package error;

public class Error {
    private ErrorType errorType;
    private int lineNum;

    public Error(int lineNum, ErrorType type) {
        errorType = type;
        this.lineNum = lineNum;
    }

    public int getLineNum() {
        return lineNum;
    }

    public ErrorType getType() {
        return errorType;
    }
}
