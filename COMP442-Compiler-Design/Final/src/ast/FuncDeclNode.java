package ast;
import visitors.Visitor;

public class FuncDeclNode extends Node {
    public void accept(Visitor visitor){
        visitor.visit(this);
    }
}
