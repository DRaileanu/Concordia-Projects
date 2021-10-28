package ast;
import visitors.Visitor;

public class FuncBodyNode extends Node {
    public void accept(Visitor visitor){
        visitor.visit(this);
    }
}
