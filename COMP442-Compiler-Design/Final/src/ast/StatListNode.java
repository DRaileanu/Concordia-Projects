package ast;
import visitors.Visitor;

public class StatListNode extends Node {
    public void accept(Visitor visitor){
        visitor.visit(this);
    }
}
