package ast;
import visitors.Visitor;

public class IndexListNode extends Node {
    public void accept(Visitor visitor){
        visitor.visit(this);
    }
}
