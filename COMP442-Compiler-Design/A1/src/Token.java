/*
    Simple data class to contain information about Token
    TokenType is declared outside Token since Lexer also needs to use. I just didn't want to create separate file for it.
*/

enum TokenType {
    Id, IntNum, FloatNum, StringLit,
    Equal, NotEqual, Less, Great, LessEq, GreatEq,
    Plus, Minus, Mult, Div, Assign,
    Or, And, Complement, Ternary, 
    OpenPar, ClosePar, OpenBra, CloseBra, OpenSq, CloseSq,
    SemiCol, Comma, Dot, Colon, DColon, 
    InlineCmt, BlockCmt,
    If, Then, Else, IntegerWord, FloatWord, StringWord, Void,
    Public, Private, Func, Var, Class, While,
    Read, Write, Return, Main, Inherits, Break, Continue,
    InvalidChar, InvalidNum, InvalidStringLit, InvalidCmt,
    EOF
}

public class Token {

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
        return String.format("Lexical error: %s: \"%s\": line %d", errorType, lexeme, location);
    }

    public String toString(){
        return String.format("[%s, %s, %d]", type.name(), lexeme, location);
    }
    
    public TokenType getType(){return type;}
    public int getLocation(){return location;}

}

