package ast;
import visitors.Visitor;

public class InherListNode extends Node {
    public void accept(Visitor visitor){
        visitor.visit(this);
    }
}
