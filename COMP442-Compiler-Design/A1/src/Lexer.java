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

    public Lexer(PushbackReader in) {
        input = in;
        dfa = new TransitionTable();
        startLine = 1;
        lexeme = null;

    }

    // extract the next token in the program
    public Token nextToken() throws IOException {
        lexeme = "";
        int linesCount = 0;
        dfa.reset();
        Token token = null;
        do{
            int lookup = input.read();
            if(lookup==13){lookup=input.read();}//skip \r in new line on windows
            if(lookup==10){linesCount++;}

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
        if(token.getType()==TokenType.EOF){return null;}
        //System.out.println(token.toString());
        return token;
    }

    private TokenType obtainTokenType(int state) {
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
                    default: type=TokenType.Id;
                }
            }
            break;
            case 102: type=TokenType.IntNum; break;
            case 103: type=TokenType.FloatNum; break;
            case 104: type=TokenType.StringLit; break;
            case 105: type=TokenType.Equal; break;
            case 106: type=TokenType.NotEqual; break;
            case 107: type=TokenType.Less; break;
            case 108: type=TokenType.Great; break;
            case 109: type=TokenType.LessEq; break;
            case 110: type=TokenType.GreatEq; break;
            case 111: type=TokenType.Plus; break;
            case 112: type=TokenType.Minus; break;
            case 113: type=TokenType.Mult; break;
            case 114: type=TokenType.Div; break;
            case 115: type=TokenType.Assign; break;
            case 116: type=TokenType.Or; break;
            case 117: type=TokenType.And; break;
            case 118: type=TokenType.Complement; break;
            case 119: type=TokenType.Ternary; break;
            case 120: type=TokenType.OpenPar; break;
            case 121: type=TokenType.ClosePar; break;
            case 122: type=TokenType.OpenBra; break;
            case 123: type=TokenType.CloseBra; break;
            case 124: type=TokenType.OpenSq; break;
            case 125: type=TokenType.CloseSq; break;
            case 126: type=TokenType.SemiCol; break;
            case 127: type=TokenType.Comma; break;
            case 128: type=TokenType.Dot; break;
            case 129: type=TokenType.Colon; break;
            case 130: type=TokenType.DColon; break;
            case 131: type=TokenType.InlineCmt; break;
            case 132: {
                type=TokenType.BlockCmt;
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
