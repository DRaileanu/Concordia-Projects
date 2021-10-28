
/*
    Implements DFA and a Push Down Automata when reaching state 22-23(block comment states)
    Although there are 61 total states, only 24 have outgoing transitions, so table contains 24 rows. If trying to transition from final state, throws error.
    31 columns to represent possible transitions
    States numbers coupled with Lexer, if states, make sure to also update Lexer or find way to decouple.
*/

public class TransitionTable {

    private int currentState;
    private int nestedComments;//used to track levels of depth of nested block comments

    private final static int[][] table = {
        /*
            The transitions follow the following order:
            letter(except e) , e , 0 , 1to9 , _ , space , " , \n , EOF , . , < , > , + , - , * , / , = , | , & , ! , ? , ( , ) , { , }[ , ] , ; , : , anyOtherChar
        */

        //0-> initial state
        {1, 1, 2, 3, 201, 0, 4, 0, 100, 128, 15, 16, 111, 112, 113, 19, 17, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 18, 201},
        //1 -> reading Id
        {1, 1, 1, 1, 1, 101, 101, 101, 101, 101, 101, 101, 101, 101, 101, 101, 101, 101, 101, 101, 101, 101, 101, 101, 101, 101, 101, 101, 101, 101, 101},
        //2 -> saw 0 ,reading intnum(0) or floatnum if seeing .
        {102, 102, 102, 102, 102, 102, 102, 102, 102, 5, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102},
        //3 -> reading intnum or floatnum that started with 1..9
        {102, 102, 3, 3, 102, 102, 102, 102, 102, 5, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102},
        //4 -> reading StringLit
        {4, 4, 4, 4, 4, 4, 104, 203, 203, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13},
        //5 -> saw .  ,reading floatnum
        {202, 202, 6, 6, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202},
        //6 -> reading floatnum w/o e. Waiting for e or nondigit
        {103, 8, 7, 6, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103},
        //7 -> saw 0 ,reading floatnum w/o e. Need 1..9 for valid floatnum
        {202, 202, 7, 6, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202},
        //8 -> saw e ,reading floatnum w/ e
        {202, 202, 9, 11, 202, 202, 202, 202, 202, 202, 202, 202, 10, 10, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202},
        //9 -> saw 0 after e ,tokenize as floatnum. State is used just to prevent having a separate final state w/o backup for floatnum.
        {103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103},
        //10-> saw + or - ,reading floatnum w/ e. Need to read an integer now
        {202, 202, 12, 11, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202},
        //11-> saw 1..9 after e ,reading floatnum. Waiting nondigit to tokenize
        {103, 103, 11, 11, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103},
        //12-> saw 0 after + or - ,will tokenize as floatnum. State used to avoid creating separate final state w/o backup for floatnum
        {103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103},
        //13-> saw at some point non-alphanum while reading StringLit. Waiting for ", \n, EOF
        {13, 13, 13, 13, 13, 13, 14, 203, 203, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13},
        //14-> saw matching " while reading invalidStringLit. Tokenize as invalidStr. State is used to avoid having final state w/o backup for invalidStringLit.
        {203, 203, 203, 203, 203, 203, 203, 203, 203, 203, 203, 203, 203, 203, 203, 203, 203, 203, 203, 203, 203, 203, 203, 203, 203, 203, 203, 203, 203, 203, 203},
        //15-> saw < ,could be < <> <=
        {107, 107, 107, 107, 107, 107, 107, 107, 107, 107, 107, 106, 107, 107, 107, 107, 109, 107, 107, 107, 107, 107, 107, 107, 107, 107, 107, 107, 107, 107, 107},
        //16-> saw > ,could be > >=
        {108, 108, 108, 108, 108, 108, 108, 108, 108, 108, 108, 108, 108, 108, 108, 108, 110, 108, 108, 108, 108, 108, 108, 108, 108, 108, 108, 108, 108, 108, 108},
        //17-> saw = ,could be = ==
        {115, 115, 115, 115, 115, 115, 115, 115, 115, 115, 115, 115, 115, 115, 115, 115, 105, 115, 115, 115, 115, 115, 115, 115, 115, 115, 115, 115, 115, 115, 115},
        //18-> saw : ,could be : ::
        {129, 129, 129, 129, 129, 129, 129, 129, 129, 129, 129, 129, 129, 129, 129, 129, 129, 129, 129, 129, 129, 129, 129, 129, 129, 129, 129, 129, 129, 130, 129},
        //19-> saw / ,could be just / // /*
        {114, 114, 114, 114, 114, 114, 114, 114, 114, 114, 114, 114, 114, 114, 21, 20, 114, 114, 114, 114, 114, 114, 114, 114, 114, 114, 114, 114, 114, 114, 114},
        //20-> reading inline comment. Waiting for \n or EOF
        {20, 20, 20, 20, 20, 20, 20, 131, 131, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20},
        
        //states 21 to 23 deal with block comments. Not DFA, nextState() handles these states as a pushdown automata
        //21-> reading block comment.
        {21, 21, 21, 21, 21, 21, 21, 21, 204, 21, 21, 21, 21, 21, 22, 23, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21},
        //22-> saw * in block comment. End it if see / ,-1 transition for / to detect error, as transition handled by nextState(), can be 21 or 132
        {21, 21, 21, 21, 21, 21, 21, 21, 204, 21, 21, 21, 21, 21, 22, -1, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21},
        //23->saw / in a block comment, start nested comment if see *
        {21, 21, 21, 21, 21, 21, 21, 21, 204, 21, 21, 21, 21, 21, 21, 23, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21}
    };

    public TransitionTable() {
        currentState = 0;
        nestedComments = 0;
    }

    public void reset(){
        currentState = 0;
    }

    public void nextState(int lookup){
        assert !isFinalState() : "Can't transition out of final state! You must have missed detecing final state!";
        int index;//index in table row where nextState is

        if((lookup>=65 && lookup<=90) || (lookup>=97 && lookup<=122)){//lookup is a letter
            if(lookup==101){index=1;}//lookup is 'e'
            else{index=0;}
        }
        else if(lookup>=48 && lookup<=57){//lookup is digit
            if(lookup==48){index=2;}//lookup is '0'
            else{index=3;}
        }
        else {
            switch((char)lookup){
                case '_': index=4; break;
                case ' ': case '\t': index=5; break;
                case '"': index=6; break;
                case '\n': index=7; break;
                case '.': index=9; break;
                case '<': index=10; break;
                case '>': index=11; break;
                case '+': index=12; break;
                case '-': index=13; break;
                case '*': index=14; break;
                case '/': index=15; break;
                case '=': index=16; break;
                case '|': index=17; break;
                case '&': index=18; break;
                case '!': index=19; break;
                case '?': index=20; break;
                case '(': index=21; break;
                case ')': index=22; break;
                case '{': index=23; break;
                case '}': index=24; break;
                case '[': index=25; break;
                case ']': index=26; break;
                case ';': index=27; break;
                case ',': index=28; break;
                case ':': index=29; break;
                default:{
                    if(lookup==-1){
                        index=8;
                    }
                    else{//any other character
                        index=30;
                    }
                }  
            }
        }

        //handle block comment transitions if needed, otherwise just transition
        switch(currentState){
            case 22: {
                if(index==15){
                    if(nestedComments>0){
                        nestedComments--;
                        currentState = 21;//continue reading block comment, but 1 level lower
                    }
                    else{currentState=132;}//finish block comment
                }
                else{currentState = table[currentState][index];}
            }
            break;
            case 23: {
                if(index==14){
                    nestedComments++;
                }
            }//don't break
            default: currentState = table[currentState][index];
        }
    }

    //states 0 to 99 are reserved for non-final states
    boolean isFinalState() {
        if(currentState>=0 && currentState<=99){//99 is more than enough, could reduce to 23, but I always forget about it when increasing # of states
            return false;
        }
        else{return true;}
    }

    public int getState(){
        return currentState;
    } 

    //all states needing backup must be specified here
    public boolean needBackup(){
        assert isFinalState() : "Can't backup if not in final state!";
        switch(currentState){
            case 100: case 101: case 102: case 103: case 107: 
            case 108: case 115: case 129: case 114: case 131:
            case 202: case 203: case 204:
                return true;
            default: return false;
        }
    }
}
