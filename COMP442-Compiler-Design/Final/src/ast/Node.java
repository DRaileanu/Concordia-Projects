package ast;
import symboltable.*;
import visitors.*;

import java.util.ArrayList;

public abstract class Node {
    
    public String data              = null; //store lexeme for certain nodes
    public String linenum           = null; //store lineNum for error/warning reporting

    //introduced for symbtabcreator visitor
    public SymTab symtab            = null;
    public SymTabEntry symtabentry  = null;

    //introduced for typecheck visitor
    public String type;

    //for .outast
    protected int idNum;                    //for unique id in .outast
    protected String label;                 //label for .outast

    private static int idCounter = 0;
    private Node parent;
    private Node rightSib;
    private Node leftmostSib;
    private Node leftmostChild;



    public Node(){
        leftmostSib = this;
        idNum = idCounter++;
        String className = this.getClass().getSimpleName();
        label = className.substring(0, className.indexOf("Node"));
    }

    public void accept(Visitor visitor){
        visitor.visit(this);
    }
    

    //inserts a new sibling node y in the list of siblings of this node
    public void makeSiblings(Node y){
        //find rightmost node in the list of siblings
        Node xsibs = this;
        while(xsibs.rightSib!=null){
            xsibs = xsibs.rightSib;
        }
        //join the sibling lists
        Node ysibs = y.leftmostSib;
        xsibs.rightSib = ysibs;
        //update pointers to leftmost sibling and parent
        ysibs.leftmostSib = xsibs.leftmostSib;
        ysibs.parent = xsibs.parent;
        while(ysibs.rightSib!=null){
            ysibs = ysibs.rightSib;
            ysibs.leftmostSib = xsibs.leftmostSib;
            ysibs.parent = xsibs.parent;
        }
    }

    //adopts node y and all its siblings under this
    public void adoptChildren(Node y){
        if(this.leftmostChild!=null){
            this.leftmostChild.makeSiblings(y);
        }
        else{
            Node ysibs = y.leftmostSib;
            this.leftmostChild = ysibs;
            while(ysibs!=null){
                ysibs.parent=this;
                ysibs = ysibs.rightSib;
            }
        }
    }

    //adopts node y and all its siblings under this, but to the left of current leftmost child
    public void adoptChildrenLeftmost(Node y){
        if(this.leftmostChild!=null){
            Node oldLeftmostChild = this.leftmostChild;
            this.leftmostChild = y;
            y.parent = this;
            y.makeSiblings(oldLeftmostChild);
        }
        else{
            Node ysibs = y.leftmostSib;
            this.leftmostChild = ysibs;
            while(ysibs!=null){
                ysibs.parent=this;
                ysibs = ysibs.rightSib;
            }
        }
    }

    public ArrayList<Node> getChildren(){
        ArrayList<Node> children = new ArrayList<Node>();
        Node child = leftmostChild;
        while(child!=null){
            children.add(child);
            child=child.rightSib;
        }
        return children;
    }

    public void appendOutAST(ArrayList<String> outast){
        outast.add(String.format("%d[label=\"%s\"]", idNum, label));
        ArrayList<Node> children = getChildren();
        for(Node child : children){
            outast.add(String.format("%d->%d", idNum, child.idNum));
        }
        for(Node child : children){
            child.appendOutAST(outast);
        }
    }

}