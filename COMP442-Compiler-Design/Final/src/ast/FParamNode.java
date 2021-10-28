package ast;
import visitors.Visitor;

public class FParamNode extends Node {
    public void accept(Visitor visitor){
        visitor.visit(this);
    }
}
