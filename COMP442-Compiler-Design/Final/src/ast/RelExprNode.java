package ast;
import visitors.Visitor;

public class RelExprNode extends Node {
    public void accept(Visitor visitor){
        visitor.visit(this);
    }
}
