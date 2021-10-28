package visitors;
import ast.*;
import symboltable.*;
import symboltable.SymTabEntry.Kind;
import main.OutputHolder;

import java.util.ArrayList;

public class TypeCheckVisitor implements Visitor{
    OutputHolder out = OutputHolder.getInstance();
    SymTab globaltable;

    @Override
    public void visit(Node node) {
        System.err.println(String.format("\n\nSHOULDN'T BE VISITING abstract Node! Means missing method definition: visit(%s)\n\n", node.getClass().getSimpleName()));
        
    }

    @Override
    public void visit(AddOpNode node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(AssignStatNode node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(BreakStatNode node) {
        
        
    }

    @Override
    public void visit(ClassDeclListNode node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(ClassDeclNode node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(ContinueStatNode node) {
                
    }

    @Override
    public void visit(DataMemberNode node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(DimListNode node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(EpsilonNode node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(FCallNode node) {
        SymTab localtable = node.symtab;

        //visit FCallParamsNode to get all params(ArrayList<VarEntry>)
        node.getChildren().get(1).accept(this);
        
    }

    @Override
    public void visit(FCallParamsNode node) {
        
        
    }

    @Override
    public void visit(FParamListNode node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(FParamNode node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(FuncBodyNode node) {
        //pass local functable and funcentry to statListNode and visit it
        Node statListNode = node.getChildren().get(1);
        statListNode.symtab = node.symtab;
        statListNode.symtabentry = node.symtabentry;
        statListNode.accept(this);
        
    }

    @Override
    public void visit(FuncDeclNode node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(FuncDefListNode node) {
        //visit all funcDefNode childs
        for(Node child : node.getChildren()){
            child.accept(this);
        }
        
    }

    @Override
    public void visit(FuncDefNode node) {
        //visit funcBodyNode
        node.getChildren().get(4).accept(this);
        
    }

    @Override
    public void visit(IdNode node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(IfStatNode node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(IndexListNode node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(InherListNode node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(MembDeclListNode node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(MembDeclNode node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(MultOpNode node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(NotNode node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(NumNode node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(ProgNode node) {
        //set globaltable reference
        this.globaltable = node.symtab;
        //no need to visit classDeclListNode
        //visit all functions in funcDefListNode
        for(Node child : node.getChildren().get(1).getChildren()){
            child.accept(this);
        }
        
    }

    @Override
    public void visit(ReadStatNode node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(RelExprNode node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(RelOpNode node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(ReturnStatNode node) {
        SymTab localtable = node.symtab;
        
        //TODO getting function return type from name of functable. Alternative is to pass funcentry down as well and get it from there, consider it maybe?
        String type = localtable.name.substring(localtable.name.lastIndexOf(":")+1);
        //visit <expr> child to get type and compare with func return type
        node.getChildren().get(0).accept(this);
        String exprType = node.getChildren().get(0).type;
        if(!type.equals(exprType)){
            out.addSemanticError(Integer.valueOf(node.linenum), String.format("Line %s : Return type mismatch. Expected %s, got %s", node.linenum, type, exprType));
        }
        

    }

    @Override
    public void visit(SignNode node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(StatListNode node) {
        SymTab localtable = node.symtab;
        //pass localtable to all childs and visit them. Childs are some variation of Statement() from Parser
        for(Node child : node.getChildren()){
            child.symtab = localtable;
            child.accept(this);
        }
        
    }

    @Override
    public void visit(StringlitNode node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(TernaryNode node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(TypeNode node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(VarDeclListNode node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(VarDeclNode node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(VarNode node) {
        SymTab localtable = node.symtab;
        ArrayList<Node> dataMemberNodes = node.getChildren();
        //check if VarNode represents a funcCall
        boolean isFCall;
        if((FCallNode) dataMemberNodes.get(dataMemberNodes.size()-1)!=null){
            isFCall=true;
        }
        else{isFCall=false;}
        

        //CASE 1: FuncCall
        if(isFCall){
            FCallNode fcall = (FCallNode)dataMemberNodes.get(dataMemberNodes.size()-1);
            //Case 1.1: call to free func
            if(dataMemberNodes.size()==1){
                //pass down localtable and visit fcallnode to get type and typecheck parameters
                fcall.symtab=localtable;
                fcall.accept(this);
                //String fcallname = fcall.getChildren().get(0).data;
                
                
                

            }
            
        }


    }

    @Override
    public void visit(VisibilityNode node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(WhileStatNode node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(WriteStatNode node) {
        //TODO
        
    }
    
}
