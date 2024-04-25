package lexer;

public class Lexception extends Exception {
  public static final String MESSAGE = "Invalid character encountered: %s (code: %d) on line %d, column %d";

  public Lexception(String lexeme, int line, int column) {
    super(String.format(MESSAGE, lexeme, (int) lexeme.charAt(0), line, column));
  }
}
