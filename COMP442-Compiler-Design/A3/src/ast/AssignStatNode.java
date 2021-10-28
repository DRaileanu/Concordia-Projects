package ast;
import visitors.Visitor;

public class AssignStatNode extends Node {
    public void accept(Visitor visitor){
        visitor.visit(this);
    }
}
