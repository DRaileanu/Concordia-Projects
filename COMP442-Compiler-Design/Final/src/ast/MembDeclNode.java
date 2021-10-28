package ast;
import visitors.Visitor;

public class MembDeclNode extends Node {
    public void accept(Visitor visitor){
        visitor.visit(this);
    }
}
