package AST;
import lexer.Token;

public class VisibilityNode extends ASTnode {
    public Token token;
    public VisibilityNode(Token token){
        this.token = token;
        label += ":"+token.getLexeme();
    }
}
