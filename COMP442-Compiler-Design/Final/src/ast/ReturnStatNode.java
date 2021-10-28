package ast;
import visitors.Visitor;

public class ReturnStatNode extends Node {
    public void accept(Visitor visitor){
        visitor.visit(this);
    }
}
