package ast;
import visitors.Visitor;

public class FCallParamsNode extends Node {
    public void accept(Visitor visitor){
        visitor.visit(this);
    }
}
