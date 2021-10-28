package ast;
import visitors.Visitor;

public class IfStatNode extends Node {
    public void accept(Visitor visitor){
        visitor.visit(this);
    }
}
