package AST;
import lexer.Token;

public class ASTnodeFactory {
    public enum NodeType{
        EPSILON, prog, classDeclList, classDecl, funcDefList, funcDef,
        inherList, membDeclList, membDecl, varDecl, funcDecl, 
        dimList, fParamList, fParam, statList, funcBody, varDeclList,
        ifStat, relExpr, whileStat, readStat, writeStat, returnStat,
        breakStat, continueStat, not, ternary, assignStat, dataMember,
        var, indexList, fCallParams, fCall,


        id, visibility, type, num, relOp, stringlit, sign, multOp, addOp
    }

    public static ASTnode makeNode(NodeType type){
        ASTnode node = null;
        switch(type){
            case EPSILON: node = new EpsilonNode();
            break;
            case prog: node = new ProgNode();
            break;
            case classDeclList: node = new ClassDeclListNode();
            break;
            case classDecl: node = new ClassDeclNode();
            break;
            case funcDefList: node = new FuncDefListNode();
            break;
            case funcDef: node = new FuncDefNode();
            break;
            case inherList: node = new InherListNode();
            break;
            case membDeclList: node = new MembDeclListNode();
            break;
            case membDecl: node = new MembDeclNode();
            break;
            case varDecl: node = new VarDeclNode();
            break;
            case funcDecl: node = new FuncDeclNode();
            break;
            case dimList: node = new DimListNode();
            break;
            case fParamList: node = new FParamListNode();
            break;
            case fParam: node = new FParamNode();
            break;
            case statList: node = new StatListNode();
            break;
            case ifStat: node = new IfStatNode();
            break;
            case relExpr: node = new RelExprNode();
            break;
            case whileStat: node = new WhileStatNode();
            break;
            case readStat: node = new ReadStatNode();
            break;
            case writeStat: node = new WriteStatNode();
            break;
            case returnStat: node = new ReturnStatNode();
            break;
            case breakStat: node = new BreakStatNode();
            break;
            case continueStat: node = new ContinueStatNode();
            break;
            case not: node = new NotNode();
            break;
            case ternary: node = new TernaryNode();
            break;
            case assignStat: node = new AssignStatNode();
            break;
            case dataMember: node = new DataMemberNode();
            break;
            case var: node = new VarNode();
            break;
            case indexList: node = new IndexListNode();
            break;
            case fCallParams: node = new FCallParamsNode();
            break;
            case fCall: node = new FCallNode();
            break;
            case funcBody: node = new FuncBodyNode();
            break;
            case varDeclList: node = new VarDeclListNode();
            break;
            


            default: return null;
        }
        return node;
    }

    public static ASTnode makeNode(NodeType type, Token token){
        ASTnode node = null;
        switch(type){
            case id: node = new IdNode(token);
            break;
            case visibility: node = new VisibilityNode(token);
            break;
            case type: node = new TypeNode(token);
            break;
            case num: node = new NumNode(token);
            break;
            case relOp: node = new RelOpNode(token);
            break;
            case stringlit: node = new StringlitNode(token);
            break;
            case sign: node = new SignNode(token);
            break;
            case multOp: node = new MultOpNode(token);
            break;
            case addOp: node = new AddOpNode(token);
            break;

            default: return null;
        }
        return node;
    }

    public static ASTnode makeFamily(NodeType type, ASTnode... childs){
        for(int i=0; i<childs.length-1; i++){
            childs[i].makeSiblings(childs[i+1]);
        }
        ASTnode parent = makeNode(type);
        parent.adoptChildren(childs[0]);
        return parent;
    }
    


}