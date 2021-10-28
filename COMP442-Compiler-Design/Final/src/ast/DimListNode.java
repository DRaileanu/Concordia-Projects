package ast;
import visitors.Visitor;

public class DimListNode extends Node {
	public void accept(Visitor visitor){
        visitor.visit(this);
    }
	
}
