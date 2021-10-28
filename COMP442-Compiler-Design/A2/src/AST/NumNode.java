package AST;
import lexer.Token;

public class NumNode extends ASTnode {
    public Token token;
    public NumNode(Token token){
        this.token = token;
        label += ":"+token.getLexeme();
    }
}
