package ast;
import lexer.Token;
import visitors.Visitor;

public class RelOpNode extends Node {
    public RelOpNode(Token token){
        data = token.getLexeme();
        linenum = String.valueOf(token.getLocation());
        label += ":"+data;
    }
    public void accept(Visitor visitor){
        visitor.visit(this);
    }
}
