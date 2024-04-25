package lexer.daos;

public class Token {
    private int leftPosition, rightPosition, lineNumber; 
    private Symbol symbol;

    public Token(Symbol symbol, int leftPosition, int rightPosition) {
        this(symbol, leftPosition, rightPosition, -1);
    }

    public Token(Symbol symbol, int leftPosition, int rightPosition, int lineNumber) {
        this.symbol = symbol;
        this.leftPosition = leftPosition;
        this.rightPosition = rightPosition;
        this.lineNumber = lineNumber; 
    }

    public int getRightPosition() {
        return rightPosition;
    }

    public int getLeftPosition() {
        return leftPosition;
    }

    public String getLexeme() {
        return this.symbol.getLexeme();
    }

    public TokenKind getTokenKind() {
        return this.symbol.getTokenKind();
    }

    public int getLineNumber() {
        return this.lineNumber;
    }

    public String testPrint() {
        return String.format(
            "%-20s left: %-8d right: %-8d line: %-8d %s", 
            this.getLexeme(),
            this.getLeftPosition(), 
            this.getRightPosition(), 
            this.getLineNumber(),
            this.getTokenKind());
    }
    
    @Override
    public String toString() {
    return testPrint();
}
}
