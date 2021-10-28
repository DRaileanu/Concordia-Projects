package ast;
import visitors.Visitor;

public class WriteStatNode extends Node {
    public void accept(Visitor visitor){
        visitor.visit(this);
    }
}
