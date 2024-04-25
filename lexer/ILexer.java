package lexer;

import lexer.daos.Token;

public interface ILexer {
  public Token nextToken() throws Lexception;
}
