package ast;
import visitors.Visitor;

public class VarDeclNode extends Node {
    public void accept(Visitor visitor){
        visitor.visit(this);
    }
}
