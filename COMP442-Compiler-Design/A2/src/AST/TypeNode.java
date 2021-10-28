package AST;
import lexer.Token;

public class TypeNode extends ASTnode {
    public Token token;
    public TypeNode(Token token){
        this.token = token;
        label += ":"+token.getLexeme();
    }

}
