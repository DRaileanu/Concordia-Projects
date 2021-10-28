package ast;
import visitors.Visitor;

public class TernaryNode extends Node {
    public void accept(Visitor visitor){
        visitor.visit(this);
    }
}
