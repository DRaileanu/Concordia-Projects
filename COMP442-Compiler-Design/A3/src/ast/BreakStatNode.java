package ast;
import visitors.Visitor;

public class BreakStatNode extends Node {
    public void accept(Visitor visitor){
        visitor.visit(this);
    }
}
