package config;

import java.nio.file.Path;
import java.nio.file.Paths;

public class LexerConfiguration {
  public static final String LEXER_PACKAGE = "lexer";
  public static final String DAO_PACKAGE = "daos";
  public static final String TOOLS_PACKAGE = "tools";

  public static final String TOKEN_FILE_NAME = "tokens.txt";
  public static final String TOKEN_KIND_CLASS_NAME = "TokenKind";
  public static final String TOKEN_KIND_FILE_NAME = String.format("%s.java", TOKEN_KIND_CLASS_NAME);
  public static final String UNIQUE_SYMBOL_MAP_CLASS_NAME = "SymbolTable";
  public static final String UNIQUE_SYMBOL_MAP_FILE_NAME = String.format("%s.java", UNIQUE_SYMBOL_MAP_CLASS_NAME);

  public static final Path TOKEN_FILE_PATH = Paths.get(LEXER_PACKAGE, TOOLS_PACKAGE, TOKEN_FILE_NAME);
  public static final Path TOKEN_KIND_FILE_PATH = Paths.get(LEXER_PACKAGE, DAO_PACKAGE, TOKEN_KIND_FILE_NAME);
  public static final Path UNIQUE_SYMBOL_MAP_FILE_PATH = Paths.get(LEXER_PACKAGE, UNIQUE_SYMBOL_MAP_FILE_NAME);
}
