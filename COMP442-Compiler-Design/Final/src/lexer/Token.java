package lexer;
/*
    Simple data class to contain information about Token
    TokenType is declared outside Token since Lexer also needs to use. I just didn't want to create separate file for it.
*/


public class Token {
    public static enum TokenType {
        id, intnum, floatnum, stringlit,
        eq, neq, lt, gt, leq, geq,
        plus, minus, mult, div, assign,
        or, and, not, qm, 
        lpar, rpar, lcurbra, rcurbra, lsqbra, rsqbra,
        semicol, comma, dot, colon, dcolon, 
        inlinecmt, blockcmt,
    
        If, Then, Else, IntegerWord, FloatWord, StringWord, Void,
        Public, Private, Func, Var, Class, While,
        Read, Write, Return, Main, Inherits, Break, Continue,
        
        InvalidChar, InvalidNum, InvalidStringLit, InvalidCmt,
        EOF, EPSILON
    }

    private TokenType type;
    private String lexeme;
    private int location;

    public Token(TokenType type, String lexeme, int location){
        this.type = type;
        this.lexeme = lexeme;
        this.location = location;
    }

    //all error TokenType must be specified here
    public boolean isError(){
        boolean result;
        switch(type){
            case InvalidChar: case InvalidNum: case InvalidStringLit: case InvalidCmt:
                result=true;
            break;
            default: result = false;
        }
        return result;
    }

    //Error message for .outlexerrors
    public String getErrorMessage(){
        assert isError() : "No message, as not Error Token!";
        String errorType;
        switch(type){
            case InvalidChar:       errorType="Invalid character"; break;
            case InvalidNum:        errorType="Invalid number"; break;
            case InvalidStringLit:  errorType="Invalid string literal"; break;
            case InvalidCmt:        errorType="Invalid comment"; break;
            default: errorType="";
        }
        assert !errorType.equals("") : "Error Token has no message! You forgot to add it!";
        return String.format("%s: \"%s\"", errorType, lexeme);
    }

    public String toString(){
        return String.format("[%s, %s, %d]", type.name(), lexeme, location);
    }

    
    public TokenType getType(){return type;}
    public int getLocation(){return location;}
    public String getLexeme(){return lexeme;}


}

