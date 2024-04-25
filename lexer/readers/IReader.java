package lexer.readers;

public interface IReader extends AutoCloseable {
  public void close();

  public char read();

  public int getColumn();

  public int getLineNumber();
}
