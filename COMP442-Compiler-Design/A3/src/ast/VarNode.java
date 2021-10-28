package ast;
import visitors.Visitor;

public class VarNode extends Node {
    public void accept(Visitor visitor){
        visitor.visit(this);
    }
}
