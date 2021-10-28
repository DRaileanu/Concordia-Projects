package ast;
import visitors.Visitor;

public class ReadStatNode extends Node {
    public void accept(Visitor visitor){
        visitor.visit(this);
    }
}
