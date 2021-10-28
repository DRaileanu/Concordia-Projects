package ast;
import visitors.Visitor;

public class MembDeclListNode extends Node {
    public void accept(Visitor visitor){
        visitor.visit(this);
    }
}
