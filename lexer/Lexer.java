package lexer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import lexer.daos.Symbol;
import lexer.daos.Token;
import lexer.daos.TokenKind;
import lexer.readers.IReader;
import lexer.readers.SourceFileReader;

public class Lexer implements ILexer, AutoCloseable {
    private IReader reader;
    private char ch;
    private int startPosition, endPosition;
    private int lineNumber = 1;

    public Lexer(IReader reader) {
        this.reader = reader;
        this.ch = ' ';
        this.lineNumber = 1; 
    }

    public Lexer(String sourceFilePath) {
        this(new SourceFileReader(sourceFilePath));
    }

    @Override
    public void close() throws Exception {
        this.reader.close();
    }

    @Override
    public Token nextToken() throws Lexception {
        ignoreWhitespace();
        beginNewToken();

        if (Character.isJavaIdentifierStart(this.ch)) {
            return identifierOrKeyword();
        }

        if (Character.isDigit(this.ch)) {
            return integer();
        }

        return operatorOrSeparator();
    }

    private void ignoreWhitespace() {
        while (Character.isWhitespace(this.ch)) {
            advance();
        }
    }

    private void beginNewToken() {
        this.startPosition = this.reader.getColumn();
        this.endPosition = this.startPosition;
    }

    private Token identifierOrKeyword() {
        String lexeme = "";

        do {
            lexeme += this.ch;
            advance();
        } while (Character.isJavaIdentifierPart(this.ch) && !atEof());

        lexeme = lexeme.toLowerCase(); 

        TokenKind kind;
        try {
            kind = TokenKind.valueOf(lexeme); 
        } catch (IllegalArgumentException e) {
            kind = TokenKind.Identifier; 
        }

        return new Token(
            SymbolTable.recordSymbol(lexeme, kind),
            this.startPosition,
            this.endPosition - 1,
            reader.getLineNumber());
    }

    private Token integer() {
        String lexeme = "";
        TokenKind kind = TokenKind.IntLit; 
        
        lexeme += this.ch;
        advance();
        
        if (this.ch == 'b' || this.ch == 'B') {
            lexeme += this.ch;
            advance();
            kind = TokenKind.BinaryLit;
        } else {
            while ((this.ch == '0' || this.ch == '1') && !atEof()) {
                lexeme += this.ch;
                advance();
            }
            if (this.ch == 'b' || this.ch == 'B') {
                lexeme += this.ch;
                advance();
                kind = TokenKind.BinaryLit;
            } else {
                while (Character.isDigit(this.ch) && !atEof()) {
                    lexeme += this.ch;
                    advance();
                }
            }
        }
        
        return new Token(
            SymbolTable.recordSymbol(lexeme, kind),
            this.startPosition,
            this.endPosition - 1,
            this.reader.getLineNumber());
    }  
    
    private Token operatorOrSeparator() throws Lexception {
        String singleCharacter = "" + this.ch;

        if (atEof()) {
            return new Token(
                SymbolTable.recordSymbol(singleCharacter, TokenKind.EOF),
                this.startPosition,
                this.endPosition,
                this.lineNumber); 
        }

        advance();

        String doubleCharacter = singleCharacter + this.ch;
        Symbol symbol = SymbolTable.recordSymbol(doubleCharacter, TokenKind.BogusToken);

        if (symbol == null) {
            return singleCharacterOperatorOrSeparator(singleCharacter);
        } else if (symbol.getTokenKind() == TokenKind.Comment) {
            ignoreComment();
            return nextToken();
        } else {
            advance();

            return new Token(
                symbol,
                this.startPosition,
                this.endPosition - 1,
                this.lineNumber);
        }
    }

    private Token singleCharacterOperatorOrSeparator(String lexeme) throws Lexception {
        Symbol symbol = SymbolTable.recordSymbol(lexeme, TokenKind.BogusToken);

        if (symbol == null) {
            throw new Lexception(lexeme, this.reader.getLineNumber(), this.reader.getColumn());
        } else {
            return new Token(
                symbol,
                this.startPosition,
                this.endPosition - 1,
                this.lineNumber); 
        }
    }

    private void ignoreComment() {
        int currentLine = this.reader.getLineNumber();

        while (currentLine == this.reader.getLineNumber() && !atEof()) {
            advance();
        }
    }

    private void advance() {
        if (this.ch == '\n') {
            lineNumber++;
        }
        this.ch = this.reader.read();
        this.endPosition++;
    }

    private boolean atEof() {
        return this.ch == '\0';
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("usage: java lexer.Lexer <filename.x>");
            System.exit(1);
        }
    
        String sourceFilePath = args[0];
        File file = new File(sourceFilePath);
    
        if (!file.exists() || !file.isFile()) {
            String currentDir = System.getProperty("user.dir");
            sourceFilePath = currentDir + File.separator + "xsamples" + File.separator + args[0];
            file = new File(sourceFilePath);
        }
    
        if (!file.exists() || !file.isFile()) {
            System.err.println("Error: File " + sourceFilePath + " does not exist or is not a valid file.");
            System.exit(1);
        }
    
        try (Lexer lexer = new Lexer(sourceFilePath)) {
            Token token;
    
            while ((token = lexer.nextToken()).getTokenKind() != TokenKind.EOF) {
                System.out.println(token.testPrint());
            }
        } catch (Lexception lexception) {
            System.err.println(lexception.getMessage());
            System.exit(1);
        } catch (Exception exception) {
            System.err.println("Failed to close the Lexer");
            System.exit(1);
        }
    
        try (BufferedReader br = new BufferedReader(new FileReader(sourceFilePath))) {
            String line;
            int lineNumber = 1;
            while ((line = br.readLine()) != null) {
                System.out.printf("%3d: %s%n", lineNumber, line);
                lineNumber++;
            }
        } catch (IOException e) {
            System.err.println("Error reading the source file.");
            System.exit(1);
        }
    }    
}