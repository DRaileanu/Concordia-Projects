package ast;
import visitors.Visitor;

public class VarDeclListNode extends Node {
    public void accept(Visitor visitor){
        visitor.visit(this);
    }
}
