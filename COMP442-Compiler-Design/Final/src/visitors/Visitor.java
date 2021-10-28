package visitors;
import ast.*;

public interface Visitor {
    
    public abstract void visit(Node node);
    public abstract void visit(AddOpNode node);
    public abstract void visit(AssignStatNode node);
    public abstract void visit(BreakStatNode node);
    public abstract void visit(ClassDeclListNode node);
    public abstract void visit(ClassDeclNode node);
    public abstract void visit(ContinueStatNode node);
    public abstract void visit(DataMemberNode node);
    public abstract void visit(DimListNode node);
    public abstract void visit(EpsilonNode node);
    public abstract void visit(FCallNode node);
    public abstract void visit(FCallParamsNode node);
    public abstract void visit(FParamListNode node);
    public abstract void visit(FParamNode node);
    public abstract void visit(FuncBodyNode node);
    public abstract void visit(FuncDeclNode node);
    public abstract void visit(FuncDefListNode node);
    public abstract void visit(FuncDefNode node);
    public abstract void visit(IdNode node);
    public abstract void visit(IfStatNode node);
    public abstract void visit(IndexListNode node);
    public abstract void visit(InherListNode node);
    public abstract void visit(MembDeclListNode node);
    public abstract void visit(MembDeclNode node);
    public abstract void visit(MultOpNode node);
    public abstract void visit(NotNode node);
    public abstract void visit(NumNode node);
    public abstract void visit(ProgNode node);
    public abstract void visit(ReadStatNode node);
    public abstract void visit(RelExprNode node);
    public abstract void visit(RelOpNode node);
    public abstract void visit(ReturnStatNode node);
    public abstract void visit(SignNode node);
    public abstract void visit(StatListNode node);
    public abstract void visit(StringlitNode node);
    public abstract void visit(TernaryNode node);
    public abstract void visit(TypeNode node);
    public abstract void visit(VarDeclListNode node);
    public abstract void visit(VarDeclNode node);
    public abstract void visit(VarNode node);
    public abstract void visit(VisibilityNode node);
    public abstract void visit(WhileStatNode node);
    public abstract void visit(WriteStatNode node);
}
