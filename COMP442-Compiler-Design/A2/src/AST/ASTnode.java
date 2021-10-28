package AST;

import java.util.ArrayList;

public class ASTnode {
    
    private ASTnode parent;
    private ASTnode rightSib;
    private ASTnode leftmostSib;
    private ASTnode leftmostChild;
    protected int idNum;
    protected String label;
    private static int idCounter = 0;

    public ASTnode(){
        leftmostSib = this;
        idNum = idCounter++;
        String className = this.getClass().getSimpleName();
        label = className.substring(0, className.indexOf("Node"));
    }
    

    //inserts a new sibling node y in the list of siblings of this node
    public void makeSiblings(ASTnode y){
        //find rightmost node in the list of siblings
        ASTnode xsibs = this;
        while(xsibs.rightSib!=null){
            xsibs = xsibs.rightSib;
        }
        //join the sibling lists
        ASTnode ysibs = y.leftmostSib;
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
    public void adoptChildren(ASTnode y){
        if(this.leftmostChild!=null){
            this.leftmostChild.makeSiblings(y);
        }
        else{
            ASTnode ysibs = y.leftmostSib;
            this.leftmostChild = ysibs;
            while(ysibs!=null){
                ysibs.parent=this;
                ysibs = ysibs.rightSib;
            }
        }
    }

    //adopts node y and all its siblings under this, but to the left of current leftmost child
    public void adoptChildrenLeftmost(ASTnode y){
        if(this.leftmostChild!=null){
            ASTnode oldLeftmostChild = this.leftmostChild;
            this.leftmostChild = y;
            y.parent = this;
            y.makeSiblings(oldLeftmostChild);
        }
        else{
            ASTnode ysibs = y.leftmostSib;
            this.leftmostChild = ysibs;
            while(ysibs!=null){
                ysibs.parent=this;
                ysibs = ysibs.rightSib;
            }
        }
    }

    public ArrayList<ASTnode> getChildren(){
        ArrayList<ASTnode> children = new ArrayList<ASTnode>();
        ASTnode child = leftmostChild;
        while(child!=null){
            children.add(child);
            child=child.rightSib;
        }
        return children;
    }

    public void appendOutAST(ArrayList<String> outast){
        outast.add(String.format("%d[label=\"%s\"]", idNum, label));
        ArrayList<ASTnode> children = getChildren();
        for(ASTnode child : children){
            outast.add(String.format("%d->%d", idNum, child.idNum));
        }
        for(ASTnode child : children){
            child.appendOutAST(outast);
        }
    }

}