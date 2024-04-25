package lexer.tools;

public class ToolRunner {
  public static void main(String[] args) {
    TokenTool tokenKindGenerator = new TokenKindGenerator();
    tokenKindGenerator.regenerateSourceFile();

    TokenTool symbolTableGenerator = new SymbolTableGenerator();
    symbolTableGenerator.regenerateSourceFile();
  }
}
