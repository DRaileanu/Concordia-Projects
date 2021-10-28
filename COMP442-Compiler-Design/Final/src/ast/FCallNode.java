package ast;
import visitors.Visitor;

public class FCallNode extends Node{
    public void accept(Visitor visitor){
        visitor.visit(this);
    }
}
