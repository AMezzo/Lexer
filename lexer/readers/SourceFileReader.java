package lexer.readers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class SourceFileReader implements IReader {

  private String filePath;
  private BufferedReader reader;

  private int lineNumber;
  private int column;
  private char lastChar;

  public SourceFileReader(String filePath) {
    this.filePath = filePath;
    this.lineNumber = 1;
    this.column = -1;

    try {
      this.reader = new BufferedReader(new FileReader(filePath));
    } catch (FileNotFoundException e) {
      System.err.println(String.format("Failed to find source file [%s].", filePath));
      System.exit(1);
    }
  }

  @Override
  public void close() {
    try {
      this.reader.close();
    } catch (IOException e) {
      System.err.println(String.format("Failed to close source file [%s].", this.filePath));
    }
  }

  @Override
  public char read() {
    try {
        if (this.lastChar == '\n' || (this.lastChar == '\r' && peek() != '\n')) {
            this.lineNumber++;
            this.column = -1;
        }

        this.lastChar = advance();

        if (this.lastChar == '\0') {
            return '\0';
        }

        return this.lastChar;
    } catch (IOException e) {
      System.err.println(
          String.format(
              "Failed to read from source file [%s] on line [%d], column [%d]",
              this.filePath,
              this.lineNumber,
              this.column));
      System.exit(1);

      return '\0';
    }
  }
  
  private char peek() throws IOException {
    this.reader.mark(1);
    char nextChar = (char) this.reader.read();
    this.reader.reset();
    return nextChar;
}

  private char advance() throws IOException {
    this.column++;

    int i = this.reader.read();

    if (i == -1) {
      return '\0';
    }

    return (char) i;
  }

  @Override
  public int getColumn() {
    return this.column;
  }

  @Override
  public int getLineNumber() {
    return this.lineNumber;
  }

}
