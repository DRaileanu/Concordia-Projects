package ast;
import visitors.Visitor;

public class ContinueStatNode extends Node {
    public void accept(Visitor visitor){
        visitor.visit(this);
    }
}
