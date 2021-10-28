package ast;
import visitors.Visitor;

public class WhileStatNode extends Node {
    public void accept(Visitor visitor){
        visitor.visit(this);
    }
}
