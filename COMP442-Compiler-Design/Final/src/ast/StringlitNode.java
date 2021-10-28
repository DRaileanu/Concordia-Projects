package ast;
import lexer.Token;
import visitors.Visitor;

public class StringlitNode extends Node {
    public StringlitNode(Token token){
        data = token.getLexeme();
        linenum = String.valueOf(token.getLocation());
        label += ":"+data;
    }
    public void accept(Visitor visitor){
        visitor.visit(this);
    }
}
