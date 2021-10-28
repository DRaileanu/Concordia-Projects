package ast;
import visitors.Visitor;

public class ProgNode extends Node {
    public void accept(Visitor visitor){
        visitor.visit(this);
    }
}
