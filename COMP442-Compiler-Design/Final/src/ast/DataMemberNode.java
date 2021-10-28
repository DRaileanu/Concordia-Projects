package ast;
import visitors.Visitor;

public class DataMemberNode extends Node {
    public void accept(Visitor visitor){
        visitor.visit(this);
    }
}
