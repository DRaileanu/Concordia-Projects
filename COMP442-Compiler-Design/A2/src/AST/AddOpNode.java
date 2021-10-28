package AST;
import lexer.Token;

public class AddOpNode extends ASTnode {
    public Token token;
    public AddOpNode(Token token){
        this.token = token;
        label += ":"+token.getLexeme();
    }
}
