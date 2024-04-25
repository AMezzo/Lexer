package lexer.daos;

public class Symbol {
  private String lexeme;
  private TokenKind kind;

  public Symbol(String lexeme, TokenKind kind) {
    this.lexeme = lexeme;
    this.kind = kind;
  }

  public String getLexeme() {
    return this.lexeme;
  }

  public TokenKind getTokenKind() {
    return this.kind;
  }
}
