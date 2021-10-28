package ast;
import visitors.Visitor;

public class FuncDefNode extends Node {
    public void accept(Visitor visitor){
        visitor.visit(this);
    }
}
