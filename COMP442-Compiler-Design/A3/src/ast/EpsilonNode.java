package ast;
import visitors.Visitor;

public class EpsilonNode extends Node {
    public EpsilonNode(){
        //data = "";
    }
    public void accept(Visitor visitor){
        visitor.visit(this);
    }
}
