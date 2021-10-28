package ast;
import visitors.Visitor;

public class ClassDeclListNode extends Node {
    public void accept(Visitor visitor){
        visitor.visit(this);
    }
}
