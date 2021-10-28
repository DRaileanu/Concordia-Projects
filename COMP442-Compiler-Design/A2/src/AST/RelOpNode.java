package AST;
import lexer.Token;

public class RelOpNode extends ASTnode {
    public Token token;
    public RelOpNode(Token token){
        this.token = token;
        label += ":"+token.getLexeme();
    }
}
