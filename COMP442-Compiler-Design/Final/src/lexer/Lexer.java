package lexer;
import lexer.Token.TokenType;
import main.OutputHolder;
/*
    Reads stream of characters and extracts a token at a time using nextToken()

    Returned Token can be of error type, must handle outside Lexer
    When reading characters, will jump over \r, not a problem for current language, but can be fixed if needed.
*/

import java.io.IOException;
import java.io.PushbackReader;

public class Lexer {

    private PushbackReader input;
    private TransitionTable dfa;
    private int startLine;//starting line of the token we are reading. Can be different from current line for block comments
    private String lexeme;//could be local variable in nextToken, but then have to pass it through method call in obtainTokenType()
    private OutputHolder out = OutputHolder.getInstance();

    public Lexer(PushbackReader in) {
        input = in;
        dfa = new TransitionTable();
        startLine = 1;//start line for token we are reading
        lexeme = null;

    }

    // extract the next token in the program
    public Token nextToken() {
        lexeme = "";
        int linesCount = 0;//how many lines spans the current token we are reading, needed for multiline comments
        dfa.reset();
        Token token = null;
        try{
            do{
                int lookup = input.read();
                if(lookup==13){lookup=input.read();}//skip \r in new line on windows
                if(lookup==10){linesCount++;}//newline character

                dfa.nextState(lookup);
                if(dfa.isFinalState()) {
                    if(dfa.needBackup()){
                        if(lookup!=-1){
                            if(lookup==10){linesCount--;}//because we are pushing \n back
                            input.unread(lookup);
                        }
                    }
                    else{lexeme+=(char)lookup;}

                    int state = dfa.getState();
                    TokenType type = obtainTokenType(state);
                    token = new Token(type, lexeme, startLine);
                    
                    //add to output
                    out.addLexToken(token.getLocation(), token.toString());
                    if(token.isError()){
                        out.addLexError(token.getLocation(), token.getErrorMessage());
                    }
                }
                else{
                    if(dfa.getState()==0){
                        lexeme = "";
                        startLine+=linesCount;
                        linesCount=0;
                    }
                    else{
                        lexeme+=(char)lookup;
                    }
                }
            } while(token==null);

            startLine+=linesCount;
            //if(token.getType()==TokenType.EOF){return null;}
            //System.out.println(token.toString());
        }
        catch(IOException e){
            System.out.println("IO exception when reading file");
        }
        return token;
    }

    public TokenType obtainTokenType(int state) {
        TokenType type = null;
        switch(state){
            case 100: type=TokenType.EOF; break;
            case 101: {
                switch(lexeme){
                    case "if": type=TokenType.If; break;
                    case "then": type=TokenType.Then; break;
                    case "else": type=TokenType.Else; break;
                    case "integer": type=TokenType.IntegerWord; break;
                    case "float": type=TokenType.FloatWord; break;
                    case "string": type=TokenType.StringWord; break;
                    case "void": type=TokenType.Void; break;
                    case "public": type=TokenType.Public; break;
                    case "private": type=TokenType.Private; break;
                    case "func": type=TokenType.Func; break;
                    case "var": type=TokenType.Var; break;
                    case "class": type=TokenType.Class; break;
                    case "while": type=TokenType.While; break;
                    case "read": type=TokenType.Read; break;
                    case "write": type=TokenType.Write; break;
                    case "return": type=TokenType.Return; break;
                    case "main": type=TokenType.Main; break;
                    case "inherits": type=TokenType.Inherits; break;
                    case "break": type=TokenType.Break; break;
                    case "continue": type=TokenType.Continue; break;
                    default: type=TokenType.id;
                }
            }
            break;
            case 102: type=TokenType.intnum; break;
            case 103: type=TokenType.floatnum; break;
            case 104: type=TokenType.stringlit; break;
            case 105: type=TokenType.eq; break;
            case 106: type=TokenType.neq; break;
            case 107: type=TokenType.lt; break;
            case 108: type=TokenType.gt; break;
            case 109: type=TokenType.leq; break;
            case 110: type=TokenType.geq; break;
            case 111: type=TokenType.plus; break;
            case 112: type=TokenType.minus; break;
            case 113: type=TokenType.mult; break;
            case 114: type=TokenType.div; break;
            case 115: type=TokenType.assign; break;
            case 116: type=TokenType.or; break;
            case 117: type=TokenType.and; break;
            case 118: type=TokenType.not; break;
            case 119: type=TokenType.qm; break;
            case 120: type=TokenType.lpar; break;
            case 121: type=TokenType.rpar; break;
            case 122: type=TokenType.lcurbra; break;
            case 123: type=TokenType.rcurbra; break;
            case 124: type=TokenType.lsqbra; break;
            case 125: type=TokenType.rsqbra; break;
            case 126: type=TokenType.semicol; break;
            case 127: type=TokenType.comma; break;
            case 128: type=TokenType.dot; break;
            case 129: type=TokenType.colon; break;
            case 130: type=TokenType.dcolon; break;
            case 131: type=TokenType.inlinecmt; break;
            case 132: {
                type=TokenType.blockcmt;
                reformatBlockCmt();
            }
            break;
            case 201: type=TokenType.InvalidChar; break;
            case 202: type=TokenType.InvalidNum; break;
            case 203: type=TokenType.InvalidStringLit; break;
            case 204: type=TokenType.InvalidCmt; break;
            default: assert false : "cant obtainTokenType, no such state";
        }
        return type;
    }


    private void reformatBlockCmt(){
        lexeme=lexeme.replace("\n", "\\n");
    }

}
