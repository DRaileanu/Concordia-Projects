package ast;
import visitors.Visitor;

public class ClassDeclNode extends Node {
    public void accept(Visitor visitor){
        visitor.visit(this);
    }
}
