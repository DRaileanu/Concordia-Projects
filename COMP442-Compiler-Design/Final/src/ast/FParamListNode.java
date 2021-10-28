package ast;
import visitors.Visitor;

public class FParamListNode extends Node {
    public void accept(Visitor visitor){
        visitor.visit(this);
    }
}
