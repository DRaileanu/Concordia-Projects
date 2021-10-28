package AST;
import lexer.Token;

public class MultOpNode extends ASTnode {
    public Token token;
    public MultOpNode(Token token){
        this.token = token;
        label += ":"+token.getLexeme();
    }
}
