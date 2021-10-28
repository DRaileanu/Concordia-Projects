package ast;
import visitors.Visitor;

public class NotNode extends Node {
    public void accept(Visitor visitor){
        visitor.visit(this);
    }
}
