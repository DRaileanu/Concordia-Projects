package AST;
import lexer.Token;

public class StringlitNode extends ASTnode {
    public Token token;
    public StringlitNode(Token token){
        this.token = token;
    }
}
