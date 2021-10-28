package ast;
import lexer.Token;
import visitors.Visitor;

public class VisibilityNode extends Node {
    public VisibilityNode(Token token){
        data = token.getLexeme();
        linenum = String.valueOf(token.getLocation());
        label += ":"+data;
    }
    public void accept(Visitor visitor){
        visitor.visit(this);
    }
}
