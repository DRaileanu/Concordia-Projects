package AST;
import lexer.Token;

public class SignNode extends ASTnode {
    public Token token;
    public SignNode(Token token){
        this.token = token;
        label += ":"+token.getLexeme();
    }
}
