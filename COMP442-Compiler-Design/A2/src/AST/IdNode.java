package AST;
import lexer.Token;

public class IdNode extends ASTnode {
    public Token token;
    public IdNode(Token token){
        this.token = token;
        label += ":"+token.getLexeme();
    }

}
