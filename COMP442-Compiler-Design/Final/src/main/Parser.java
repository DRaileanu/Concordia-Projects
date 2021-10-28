package main;
import java.util.EnumSet;
import java.util.Stack;

import ast.*;
import ast.ASTnodeFactory.NodeType;

import java.util.ArrayList;

import lexer.*;
import lexer.Token.TokenType;

public class Parser {

    enum Symbol{
        Start, 
        AParams, AParamsTail, AddOp, 
        ArithExpr, ArithExprTail, ArraySizeRept,
        AssignOp, AssignStatTail,
        ClassDecl, ClassDeclBody, ClassMethod, 
        Expr, ExprTail, 
        FParams, FParamsTail,
        Factor, 
        FuncBody, FuncDecl, FuncDeclTail, FuncDef, FuncHead,
        FuncOrAssignStat, FuncOrAssignStatIdnest, FuncOrAssignStatIdnestFuncTail,
        FuncStatTail, FuncStatTailIdnest, FuncOrAssignStatIdnestVarTail, 
        FuncOrVar, FuncOrVarIdnest, FuncOrVarIdnestTail,
        Function, 
        IndiceRep, Inherit, IntNum,
        MemberDecl, MethodBodyVar, MultOp, 
        NestedId, Prog, RelOp,
        Sign, StatBlock, Statement, StatementList,
        Term, TermTail, Type, 
        VarDecl, VarDeclRep, Variable, VariableIdnest, VariableIdnestTail,
        Visibility
    }

    public boolean debug;
    private Lexer lex;
    private Token lookahead;
    private boolean success;
    public Stack<Pointer<Node>> semStack;
    public Node ast;
    public ArrayList<String> derivations;
    private ArrayList<String> derivTemp;
    private int derivIndex;
    private OutputHolder out = OutputHolder.getInstance();

    public Parser(Lexer lex){
        this.lex = lex;
        lookahead=null;
        debug = false;
        success = true;
        semStack = new Stack<Pointer<Node>>();
        derivations = new ArrayList<String>();//complete list of derivations from <START> to terminals only. Each String is a derivation line
        derivTemp = new ArrayList<String>();//current derivation representation, gets modified and placed in derivations at every change
        derivIndex = 0;//index of derivTemp that we will expand next. Essentially leftmost non-terminal
    }

    
    public boolean parse(){
        //create pointer to empty node and push on stack, if parsing succesful, will point to ProgNode
        Pointer<Node> astP = new Pointer<Node>();
        semStack.push(astP);

        lookahead = nextToken();

        //start building the parsing derivation
        derivTemp.add(derivIndex, "<START>");
        derivations.add(String.join(" ", derivTemp));

        if(START() & match(TokenType.EOF)){
            this.ast = astP.ref;
            derivations.add(String.join(" ", derivTemp));
            return true;
        }
        else{
            derivations.add(String.join(" ", derivTemp));
            return false;
        }
    }


    private boolean match(TokenType tokenType){
        derivIndex++;
        if(lookahead.getType().equals(tokenType)){
            if(debug){
                System.out.println("Matched "+tokenType);
            }
            lookahead = nextToken();
            return true;
        }
        else{
            success = false;
            out.addSyntaxError(lookahead.getLocation(), String.format("Unexpected Token %s '%s'; Expected %s", lookahead.getType(), lookahead.getLexeme(), tokenType.toString()));
            if(debug){
                System.out.println("Unexpected Token "+lookahead.getType()+" at line: "+(lookahead.getLocation())+"; Expected "+tokenType.toString());
            }
            lookahead = nextToken();
            return false;
        }
    }
    private boolean match(TokenType tokenType, Pointer<Token> tokenP){
        tokenP.ref = lookahead;
        return match(tokenType);
    }


    private boolean skipErrors(EnumSet<TokenType> first, EnumSet<TokenType> follow){
        TokenType lookaheadType = lookahead.getType();
        if(
            first.contains(lookaheadType)
            ||
            (first.contains(TokenType.EPSILON) && follow.contains(lookaheadType))
        ){return true;}
        else{
            success = false;
            // if(follow.contains(lookaheadType)){
            //     out.addSyntaxError(lookahead.getLocation(), String.format("Unexpected Token %s '%s'; Remove this token", lookahead.getType(), lookahead.getLexeme()));
            // }
            out.addSyntaxError(lookahead.getLocation(), String.format("Unexpected Token %s '%s'", lookahead.getType(), lookahead.getLexeme()));
            if(debug){
                System.out.println("Unexpected Token "+lookaheadType+" at line: "+(lookahead.getLocation()));
            }
            while(!(first.contains(lookaheadType) || follow.contains(lookaheadType))){
                //out.addSyntaxError(lookahead.getLocation(), String.format("Unexpected Token %s '%s'", lookahead.getType(), lookahead.getLexeme()));
                lookahead = nextToken();
                lookaheadType = lookahead.getType();
                if(first.contains(TokenType.EPSILON) && follow.contains(lookaheadType)){
                    //System.out.println("returning false in skipErrors");
                    return true;
                }
                if(lookaheadType.equals(TokenType.EOF)){
                    //System.out.println("returning false in skipErrors (EOF)");
                    return false;
                }
            }
            //System.out.println("returning true in skipErrors");
            return true;
        } 
    }

    private Token nextToken(){
        Token token = lex.nextToken();
        TokenType tokenType = token.getType();
        while(tokenType.equals(TokenType.inlinecmt) || tokenType.equals(TokenType.blockcmt)){
            token = lex.nextToken();
            tokenType = token.getType();
        }
        return token;
    }

    private boolean START(){
        if(debug){
            System.out.println("DEBUG: Parsing <Start>");
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> result = semStack.pop();
        if(!skipErrors(first(Symbol.Start), follow(Symbol.Start))){return false;}
        if(first(Symbol.Prog).contains(lookahead.getType())){
            Pointer<Node> prog = new Pointer<Node>();
            semStack.push(prog);

            derivTemp.add(derivIndex, "<Prog>");
            derivations.add(String.join(" ", derivTemp));
            if(Prog()){
                if(debug) System.out.println("<START> ::= <Prog> ");
                result.ref = prog.ref;
                return true;
            }
            else return false;
        }
        else return false;
    }

    private boolean AParams(){
        //*result is null, becomes fCallParams with 0+ <Expr> childs
        if(debug){
            System.out.println("DEBUG: Parsing <AParams> at line "+lookahead.getLocation());
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> result = semStack.pop();
        result.ref = ASTnodeFactory.makeNode(NodeType.fCallParams);
        if(!skipErrors(first(Symbol.AParams), follow(Symbol.AParams))){return false;}
        if(first(Symbol.Expr).contains(lookahead.getType())){
            Pointer<Node> exprP = new Pointer<Node>();
            Pointer<Node> nestedExprP = new Pointer<Node>();
            semStack.push(nestedExprP);
            semStack.push(exprP);

            derivTemp.add(derivIndex, "<Expr>"); derivTemp.add(derivIndex+1, "<AParamsTail>");
            derivations.add(String.join(" ", derivTemp));
            if(Expr() & AParamsTail()){
                if(debug) System.out.println("<AParams> ::= <Expr> <AParamsTail> at line "+lookahead.getLocation());
                if(nestedExprP.ref!=null){
                    exprP.ref.makeSiblings(nestedExprP.ref);
                }
                result.ref.adoptChildren(exprP.ref);
                return true;
            }
            else return false;
        }
        else if(follow(Symbol.AParams).contains(lookahead.getType())){
            if(debug) System.out.println("<AParams> ::= EPSILON ");
            derivations.add(String.join(" ", derivTemp));
            return true;
        }
        else return false;
    }

    private boolean AParamsTail(){
        //*result is null, remains so or becomes <Expr> with potential <Expr> siblings
        if(debug){
            System.out.println("DEBUG: Parsing <AParamsTail> at line "+lookahead.getLocation());
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> result = semStack.pop();
        if(!skipErrors(first(Symbol.AParamsTail), follow(Symbol.AParamsTail))){return false;}
        if(lookahead.getType().equals(TokenType.comma)){
            Pointer<Node> nestedExprP = new Pointer<Node>();
            semStack.push(nestedExprP);
            semStack.push(result);

            derivTemp.add(derivIndex, "','"); derivTemp.add(derivIndex+1, "<Expr>"); derivTemp.add(derivIndex+2, "<AParamsTail>");
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.comma) & Expr() & AParamsTail()){
                if(debug) System.out.println("<AParamsTail> ::= ',' <Expr> <AParamsTail> ");
                if(nestedExprP.ref!=null){
                    result.ref.makeSiblings(nestedExprP.ref);
                }
                return true;
            }
            else return false;
        }
        else if(follow(Symbol.AParamsTail).contains(lookahead.getType())){
            if(debug) System.out.println("<AParamsTail> ::= EPSILON ");
            derivations.add(String.join(" ", derivTemp));
            return true;
        }
        else return false;
    }

    private boolean AddOp(){
        //*result is null, becomes addOp
        if(debug){
            System.out.println("DEBUG: Parsing <AddOp> at line "+lookahead.getLocation());
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> result = semStack.pop();
        Pointer<Token> addOpToken = new Pointer<Token>();
        if(!skipErrors(first(Symbol.AddOp), follow(Symbol.AddOp))){return false;}
        if(lookahead.getType().equals(TokenType.plus)){
            derivTemp.add(derivIndex, "'+'");
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.plus, addOpToken)){
                if(debug) System.out.println("<AddOp> ::= '+' ");
                result.ref = ASTnodeFactory.makeNode(NodeType.addOp, addOpToken.ref);
                return true;
            }
            else return false;
        }
        else if(lookahead.getType().equals(TokenType.minus)){
            derivTemp.add(derivIndex, "'-'");
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.minus, addOpToken)){
                if(debug) System.out.println("<AddOp> ::= '-' ");
                result.ref = ASTnodeFactory.makeNode(NodeType.addOp, addOpToken.ref);
                return true;
            }
            else return false;
        }
        else if(lookahead.getType().equals(TokenType.or)){
            derivTemp.add(derivIndex, "'or'");
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.or, addOpToken)){
                if(debug) System.out.println("<AddOp> ::= 'or' ");
                result.ref = ASTnodeFactory.makeNode(NodeType.addOp, addOpToken.ref);
                return true;
            }
            else return false;
        }
        else return false;
    }


    private boolean ArithExpr(){
        //*result is null, becomes <term> or addOp with childs <arithExpr>,<term>
        if(debug){
            System.out.println("DEBUG: Parsing <ArithExpr> at line "+lookahead.getLocation());
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> result = semStack.pop();
        if(!skipErrors(first(Symbol.ArithExpr), follow(Symbol.ArithExpr))){return false;}
        if(first(Symbol.Term).contains(lookahead.getType())){
            Pointer<Node> leftTermP = new Pointer<Node>();
            Pointer<Node> addOpP = new Pointer<Node>();
            Pointer<Node> arithExprP = new Pointer<Node>();
            semStack.push(arithExprP);
            semStack.push(addOpP);
            semStack.push(leftTermP);

            derivTemp.add(derivIndex, "'<Term>'"); derivTemp.add(derivIndex+1, "<ArithExprTail>");
            derivations.add(String.join(" ", derivTemp));
            if(Term() & ArithExprTail()){
                if(debug) System.out.println("<ArithExpr> ::= <Term> <ArithExprTail> ");
                if(addOpP.ref==null){
                    result.ref = leftTermP.ref;
                }
                else{
                    result.ref = addOpP.ref;
                    result.ref.adoptChildren(leftTermP.ref);
                    result.ref.adoptChildren(arithExprP.ref);
                }
                return true;
            }
            else return false;
        }
        else return false;
    }



    private boolean ArithExprTail(){
        //*addOpP and *arithExprP are null. Both remain null, or *addOpP->addOpP and *arithExprP->term/addOp(since recursive)
        //note that arithExprP is rightTermP from ArithExpr()
        if(debug){
            System.out.println("DEBUG: Parsing <ArithExprTail> at line "+lookahead.getLocation());
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> addOpP = semStack.pop();
        Pointer<Node> arithExprP = semStack.pop();
        if(!skipErrors(first(Symbol.ArithExprTail), follow(Symbol.ArithExprTail))){return false;}
        if(first(Symbol.AddOp).contains(lookahead.getType())){
            Pointer<Node> termP = new Pointer<Node>();
            Pointer<Node> nestedAddOpP = new Pointer<Node>();
            Pointer<Node> nestedArithExprP = new Pointer<Node>();
            semStack.push(nestedArithExprP);
            semStack.push(nestedAddOpP);
            semStack.push(termP);
            semStack.push(addOpP);

            derivTemp.add(derivIndex, "<AddOp>"); derivTemp.add(derivIndex+1, "<Term>"); derivTemp.add(derivIndex+2, "<ArithExprTail>");
            derivations.add(String.join(" ", derivTemp));
            if(AddOp() & Term() & ArithExprTail()){
                if(debug) System.out.println("<ArithExprTail> ::= <AddOp> <Term> <ArithExprTail> ");
                if(nestedAddOpP.ref==null){
                    arithExprP.ref = termP.ref;
                }
                else{
                    arithExprP.ref = nestedAddOpP.ref;
                    arithExprP.ref.adoptChildren(termP.ref);
                    arithExprP.ref.adoptChildren(nestedArithExprP.ref);
                }
                return true;
            }
            else return false;
        }

        else if(follow(Symbol.ArithExprTail).contains(lookahead.getType())){
            if(debug) System.out.println("<ArithExprTail> ::= EPSILON ");
            derivations.add(String.join(" ", derivTemp));
            return true;
        }
        else return false;
    }



    private boolean ArraySizeRept(){
        //*result is null at first, remains so if follow condition or becomes EPSILON/num after IntNum()
        if(debug){
            System.out.println("DEBUG: Parsing <ArraySizeRept> at line "+lookahead.getLocation());
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> result = semStack.pop();
        if(!skipErrors(first(Symbol.ArraySizeRept), follow(Symbol.ArraySizeRept))){return false;}
        if(lookahead.getType().equals(TokenType.lsqbra)){
            Pointer<Node> nestedIntNumP = new Pointer<Node>();
            semStack.push(nestedIntNumP);
            semStack.push(result);

            derivTemp.add(derivIndex, "'['"); derivTemp.add(derivIndex+1, "<IntNum>"); derivTemp.add(derivIndex+2, "']'"); derivTemp.add(derivIndex+3, "<ArraySizeRept>");
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.lsqbra) & IntNum() & match(TokenType.rsqbra) & ArraySizeRept()){
                if(debug) System.out.println("<ArraySizeRept> ::= '[' <IntNum> ']' <ArraySizeRept> ");
                if(nestedIntNumP.ref!=null){
                    result.ref.makeSiblings(nestedIntNumP.ref);
                }
                return true;
            }
            else return false;
        }
        
        else if(follow(Symbol.ArraySizeRept).contains(lookahead.getType())){
            if(debug) System.out.println("<ArraySizeRept> ::= EPSILON ");
            derivations.add(String.join(" ", derivTemp));
            return true;
        }
        else return false;
    }


    private boolean AssignOp(){
        //*result is null, becomes assignStat
        if(debug){
            System.out.println("DEBUG: Parsing <AssignOp> at line "+lookahead.getLocation());
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> result = semStack.pop();
        if(!skipErrors(first(Symbol.AssignOp), follow(Symbol.AssignOp))){return false;}
        if(lookahead.getType().equals(TokenType.assign)){

            derivTemp.add(derivIndex, "=");
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.assign)){
                if(debug) System.out.println("<AssignOp> ::= 'assign' ");
                result.ref = ASTnodeFactory.makeNode(NodeType.assignStat);
                return true;
            }
            else return false;
        }
        else return false;
    }


    private boolean AssignStatTail(){
        //*result is null, becomes assignStat with 1 <expr> child
        if(debug){
            System.out.println("DEBUG: Parsing <AssignStatTail> at line "+lookahead.getLocation());
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> result = semStack.pop();
        if(!skipErrors(first(Symbol.AssignStatTail), follow(Symbol.AssignStatTail))){return false;}
        if(first(Symbol.AssignOp).contains(lookahead.getType())){
            Pointer<Node> assignP = new Pointer<Node>();
            Pointer<Node> exprP = new Pointer<Node>();
            semStack.push(exprP);
            semStack.push(assignP);

            derivTemp.add(derivIndex, "<AssignOp>"); derivTemp.add(derivIndex+1, "<Expr>");
            derivations.add(String.join(" ", derivTemp));
            if(AssignOp() & Expr()){
                if(debug) System.out.println("<AssignStatTail> ::= <AssignOp> <Expr> ");
                result.ref = assignP.ref;
                result.ref.adoptChildren(exprP.ref);
                return true;
            }
            else return false;
        }
        else return false;
    }


    private boolean ClassDecl(){
        //*result is classDeclList. Every call adds a new child until follow condition
        if(debug){
            System.out.println("DEBUG: Parsing <ClassDecl> at line "+lookahead.getLocation());
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> result = semStack.pop();
        if(!skipErrors(first(Symbol.ClassDecl), follow(Symbol.ClassDecl))){return false;}
        if(lookahead.getType().equals(TokenType.Class)){
            Node classDecl = ASTnodeFactory.makeNode(NodeType.classDecl);
            result.ref.adoptChildren(classDecl);
            Pointer<Token> idToken = new Pointer<Token>();
            Pointer<Node> inherListP = new Pointer<Node>();
            inherListP.ref = ASTnodeFactory.makeNode(NodeType.inherList);
            Pointer<Node> membDeclListP = new Pointer<Node>();
            membDeclListP.ref = ASTnodeFactory.makeNode(NodeType.membDeclList);
            semStack.push(result);
            semStack.push(membDeclListP);
            semStack.push(inherListP);

            derivTemp.add(derivIndex, "'class'"); derivTemp.add(derivIndex+1, "'id'"); derivTemp.add(derivIndex+2, "<Inherit>"); derivTemp.add(derivIndex+3, "'{'"); derivTemp.add(derivIndex+4, "<ClassDeclBody>"); derivTemp.add(derivIndex+5, "'}'"); derivTemp.add(derivIndex+6, "';'"); derivTemp.add(derivIndex+7, "<ClassDecl>");
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.Class) & match(TokenType.id, idToken) & Inherit() & match(TokenType.lcurbra) & ClassDeclBody() & match(TokenType.rcurbra) & match(TokenType.semicol) & ClassDecl()){
                if(debug) System.out.println("<ClassDecl> ::= 'class' 'id' <Inherit> '{' <ClassDeclBody> '}' ';' <ClassDecl>");
                Node id = ASTnodeFactory.makeNode(NodeType.id, idToken.ref);
                classDecl.adoptChildren(id);
                classDecl.adoptChildren(inherListP.ref);
                classDecl.adoptChildren(membDeclListP.ref);
                return true;
            }
            else return false;
        }

        else if(follow(Symbol.ClassDecl).contains(lookahead.getType())){
            if(debug) System.out.println("<ClassDecl> ::= EPSILON ");
            derivations.add(String.join(" ", derivTemp));
            return true;
        }
        else return false;
    }


    private boolean ClassDeclBody(){
        //*result is a membDeclList. Every loop we add a membDecl under it
        if(debug){
            System.out.println("DEBUG: Parsing <ClassDeclBody> at line "+lookahead.getLocation());
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> result = semStack.pop();
        if(!skipErrors(first(Symbol.ClassDeclBody), follow(Symbol.ClassDeclBody))){return false;}
        if(first(Symbol.Visibility).contains(lookahead.getType()) || first(Symbol.MemberDecl).contains(lookahead.getType())){
            Node membDecl = ASTnodeFactory.makeNode(NodeType.membDecl);
            result.ref.adoptChildren(membDecl);      
            Pointer<Node> visibilityP = new Pointer<Node>();
            Pointer<Node> varDeclORfuncDeclP = new Pointer<Node>();
            semStack.push(result);
            semStack.push(varDeclORfuncDeclP);
            semStack.push(visibilityP);

            derivTemp.add(derivIndex, "<Visibility>"); derivTemp.add(derivIndex+1, "<MemberDecl>"); derivTemp.add(derivIndex+2, "<ClassDeclBody");
            derivations.add(String.join(" ", derivTemp));
            if(Visibility() & MemberDecl() & ClassDeclBody()){
                if(debug) System.out.println("<ClassDeclBody> ::= <Visibility> <MemberDecl> <ClassDeclBody> ");
                membDecl.adoptChildren(visibilityP.ref);
                membDecl.adoptChildren(varDeclORfuncDeclP.ref);
                return true;
            }
            else return false;
        }

        else if(follow(Symbol.ClassDeclBody).contains(lookahead.getType())){
            if(debug) System.out.println("<ClassDeclBody> ::= EPSILON ");
            derivations.add(String.join(" ", derivTemp));
            return true;
        }
        else return false;
    }


    private boolean ClassMethod(){
        //*result is null, remains so or becomes id
        if(debug){
            System.out.println("DEBUG: Parsing <ClassMethod> at line "+lookahead.getLocation());
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> result = semStack.pop();
        if(!skipErrors(first(Symbol.ClassMethod), follow(Symbol.ClassMethod))){return false;}
        if(lookahead.getType().equals(TokenType.dcolon)){
            Pointer<Token> idToken = new Pointer<Token>();

            derivTemp.add(derivIndex, "'::'"); derivTemp.add(derivIndex+1, "'id'");
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.dcolon) & match(TokenType.id, idToken)){
                if(debug) System.out.println("<ClassMethod> ::= 'sr' 'id' ");
                result.ref = ASTnodeFactory.makeNode(NodeType.id, idToken.ref);
                return true;
            }
            else return false;
        }

        else if(follow(Symbol.ClassMethod).contains(lookahead.getType())){
            if(debug) System.out.println("<ClassMethod> ::= EPSILON ");
            derivations.add(String.join(" ", derivTemp));
            return true;
        }
        else return false;
    }


    private boolean Expr(){
        //*result is null. Becomes <arithExpr> or relExpr with <arithExpr>, relOp, <arithExpr> childs
        if(debug){
            System.out.println("DEBUG: Parsing <Expr> at line "+lookahead.getLocation());
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> result = semStack.pop();
        if(!skipErrors(first(Symbol.Expr), follow(Symbol.Expr))){return false;}
        if(first(Symbol.ArithExpr).contains(lookahead.getType())){
            Pointer<Node> leftArithExprP = new Pointer<Node>();
            Pointer<Node> relOpP = new Pointer<Node>();
            Pointer<Node> rightArithExprP = new Pointer<Node>();
            semStack.push(rightArithExprP);
            semStack.push(relOpP);
            semStack.push(leftArithExprP);

            derivTemp.add(derivIndex, "<ArithExpr>"); derivTemp.add(derivIndex+1, "<ExprTail>");
            derivations.add(String.join(" ", derivTemp));
            if(ArithExpr() & ExprTail()){
                if(debug) System.out.println("<Expr> ::= <ArithExpr> <ExprTail> ");
                if(relOpP.ref==null){
                    result.ref = leftArithExprP.ref;
                }
                else{
                    result.ref = ASTnodeFactory.makeFamily(NodeType.relExpr, leftArithExprP.ref, relOpP.ref, rightArithExprP.ref);
                }
                return true;
            }
            else return false;
        }
        else return false;
    }


    private boolean ExprTail(){
        //*relOp and *arithExpr are null. Both remain null or become their appropriate nodes   
        if(debug){
            System.out.println("DEBUG: Parsing <ExprTail> at line "+lookahead.getLocation());
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> relOpP = semStack.pop();
        Pointer<Node> arithExprP = semStack.pop();
        if(!skipErrors(first(Symbol.ExprTail), follow(Symbol.ExprTail))){return false;}
        if(first(Symbol.RelOp).contains(lookahead.getType())){
            semStack.push(arithExprP);
            semStack.push(relOpP);

            derivTemp.add(derivIndex, "<RelOp>"); derivTemp.add(derivIndex+1, "<ArithExpr>");
            derivations.add(String.join(" ", derivTemp));
            if(RelOp() & ArithExpr()){
                if(debug) System.out.println("<ExprTail> ::= <RelOp> <ArithExpr> ");
                return true;
            }
            else return false;
        }

        else if(follow(Symbol.ExprTail).contains(lookahead.getType())){
            if(debug) System.out.println("<ExprTail> ::= EPSILON ");
            derivations.add(String.join(" ", derivTemp));
            return true;
        }
        else return false;
    }


    private boolean FParams(){
        //*result is fParamList. Potentially adds 1 child, then more in FParamsTail()
        if(debug){
            System.out.println("DEBUG: Parsing <FParams> at line "+lookahead.getLocation());
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> result = semStack.pop();
        if(!skipErrors(first(Symbol.FParams), follow(Symbol.FParams))){return false;}
        if(first(Symbol.Type).contains(lookahead.getType())){
            Pointer<Token> idToken = new Pointer<Token>();
            Pointer<Node> typeP = new Pointer<Node>();
            Pointer<Node> dimP = new Pointer<Node>();
            Pointer<Node> nestedFParamP = new Pointer<Node>();
            semStack.push(nestedFParamP);
            semStack.push(dimP);
            semStack.push(typeP);

            derivTemp.add(derivIndex, "<Type>"); derivTemp.add(derivIndex+1, "'id'"); derivTemp.add(derivIndex+2, "<ArraySizeRept>"); derivTemp.add(derivIndex+3, "<FParamsTail>");
            derivations.add(String.join(" ", derivTemp));
            if(Type() & match(TokenType.id, idToken) & ArraySizeRept() & FParamsTail()){
                if(debug) System.out.println("<FParams> ::= <Type> 'id' <ArraySizeRept> <FParamsTail> ");
                Node id = ASTnodeFactory.makeNode(NodeType.id, idToken.ref);
                Node dimList = ASTnodeFactory.makeNode(NodeType.dimList);
                if(dimP.ref!=null){
                    dimList.adoptChildren(dimP.ref);
                }
                Node fParam = ASTnodeFactory.makeFamily(NodeType.fParam, typeP.ref, id, dimList);
                if(nestedFParamP.ref!=null){
                    fParam.makeSiblings(nestedFParamP.ref);
                }
                result.ref.adoptChildren(fParam);
                return true;
            }
            else return false;
        }

        else if(follow(Symbol.FParams).contains(lookahead.getType())){
            if(debug) System.out.println("<FParams> ::= EPSILON ");
            derivations.add(String.join(" ", derivTemp));
            return true;
        }
        else return false;
    }


    private boolean FParamsTail(){
        //*result is null, becomes fParam or remains null.
        //note that after if statement, *dimP remains null or EPSILON/num with potentially other siblings EPSILON/num
        if(debug){
            System.out.println("DEBUG: Parsing <FParamsTail> at line "+lookahead.getLocation());
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> result = semStack.pop();
        if(!skipErrors(first(Symbol.FParamsTail), follow(Symbol.FParamsTail))){return false;}
        if(lookahead.getType().equals(TokenType.comma)){
            Pointer<Token> idToken = new Pointer<Token>();
            Pointer<Node> typeP = new Pointer<Node>();
            Pointer<Node> dimP = new Pointer<Node>();
            Pointer<Node> nestedFParamP = new Pointer<Node>();
            semStack.push(nestedFParamP);
            semStack.push(dimP);
            semStack.push(typeP);

            derivTemp.add(derivIndex, "','"); derivTemp.add(derivIndex+1, "<Type>"); derivTemp.add(derivIndex+2, "'id'"); derivTemp.add(derivIndex+3, "<ArraySizeRept>"); derivTemp.add(derivIndex+4, "<FParamsTail>");
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.comma) & Type() & match(TokenType.id, idToken) & ArraySizeRept() & FParamsTail()){
                if(debug) System.out.println("<FParamsTail> ::= ',' <Type> 'id' <ArraySizeRept> <FParamsTail>");
                Node id = ASTnodeFactory.makeNode(NodeType.id, idToken.ref);
                Node dimList = ASTnodeFactory.makeNode(NodeType.dimList);
                if(dimP.ref!=null){
                    dimList.adoptChildren(dimP.ref);
                }
                result.ref = ASTnodeFactory.makeFamily(NodeType.fParam, typeP.ref, id, dimList);
                if(nestedFParamP.ref!=null){
                    result.ref.makeSiblings(nestedFParamP.ref);
                }
                return true;
            }
            else return false;
        }

        else if(follow(Symbol.FParamsTail).contains(lookahead.getType())){
            if(debug) System.out.println("<FParamsTail> ::= EPSILON ");
            derivations.add(String.join(" ", derivTemp));
            return true;
        }
        else return false;
    }


    private boolean Factor(){
        //*result is null. Becomes some appropriate version of <factor>
        if(debug){
            System.out.println("DEBUG: Parsing <Factor> at line "+lookahead.getLocation());
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> result = semStack.pop();
        if(!skipErrors(first(Symbol.Factor), follow(Symbol.Factor))){return false;}
        if(first(Symbol.FuncOrVar).contains(lookahead.getType())){
            semStack.push(result);

            derivTemp.add(derivIndex, "<FuncOrVar>"); 
            derivations.add(String.join(" ", derivTemp));
            if(FuncOrVar()){
                if(debug) System.out.println("<Factor> ::= <FuncOrVar> ");
                return true;
            }
            else return false;
        }
        else if(lookahead.getType().equals(TokenType.intnum)){
            Pointer<Token> numToken = new Pointer<Token>();

            derivTemp.add(derivIndex, "'intnum'"); 
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.intnum, numToken)){
                if(debug) System.out.println("<Factor> ::= 'intnum' ");
                result.ref = ASTnodeFactory.makeNode(NodeType.num, numToken.ref);
                return true;
            }
            else return false;
        }
        else if(lookahead.getType().equals(TokenType.floatnum)){
            Pointer<Token> numToken = new Pointer<Token>();

            derivTemp.add(derivIndex, "'floatnum'");
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.floatnum, numToken)){
                if(debug) System.out.println("<Factor> ::= 'floatnum' ");
                result.ref = ASTnodeFactory.makeNode(NodeType.num, numToken.ref);
                return true;
            }
            else return false;
        }
        else if(lookahead.getType().equals(TokenType.stringlit)){
            Pointer<Token> stringToken = new Pointer<Token>();

            derivTemp.add(derivIndex, "'stringlit'"); 
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.stringlit, stringToken)){
                if(debug) System.out.println("<Factor> ::= 'stringlit' ");
                result.ref = ASTnodeFactory.makeNode(NodeType.stringlit, stringToken.ref);
                return true;
            }
            else return false;
        }
        else if(lookahead.getType().equals(TokenType.lpar)){
            semStack.push(result);

            derivTemp.add(derivIndex, "'('"); derivTemp.add(derivIndex+1, "<Expr>"); derivTemp.add(derivIndex+2, "')'");
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.lpar) & Expr() & match(TokenType.rpar)){
                if(debug) System.out.println("<Factor> ::= '(' <Expr> ')' ");
                return true;
            }
            else return false;
        }
        else if(lookahead.getType().equals(TokenType.not)){
            Pointer<Node> factorP = new Pointer<Node>();
            semStack.push(factorP);

            derivTemp.add(derivIndex, "'not'"); derivTemp.add(derivIndex+1, "<Factor>");
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.not) & Factor()){
                if(debug) System.out.println("<Factor> ::= 'not' <Factor> ");
                result.ref = ASTnodeFactory.makeFamily(NodeType.not, factorP.ref);
                return true;
            }
            else return false;
        }
        else if(first(Symbol.Sign).contains(lookahead.getType())){
            Pointer<Node> factorP = new Pointer<Node>();
            semStack.push(factorP);
            semStack.push(result);

            derivTemp.add(derivIndex, "<Sign>"); derivTemp.add(derivIndex+1, "<Factor>");
            derivations.add(String.join(" ", derivTemp));
            if(Sign() & Factor()){
                if(debug) System.out.println("<Factor> ::= <Sign> <Factor> ");
                result.ref.adoptChildren(factorP.ref);
                return true;
            }
            else return false;
        }
        else if(lookahead.getType().equals(TokenType.qm)){
            Pointer<Node> expr1P = new Pointer<Node>();
            Pointer<Node> expr2P = new Pointer<Node>();
            Pointer<Node> expr3P = new Pointer<Node>();
            semStack.push(expr3P);
            semStack.push(expr2P);
            semStack.push(expr1P);

            derivTemp.add(derivIndex, "'qm'"); derivTemp.add(derivIndex+1, "'['"); derivTemp.add(derivIndex+2, "<Expr>"); derivTemp.add(derivIndex+3, "':'"); derivTemp.add(derivIndex+4, "<Expr>"); derivTemp.add(derivIndex+5, "':'"); derivTemp.add(derivIndex+6, "<Expr>");derivTemp.add(derivIndex+7, "']'");
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.qm) & match(TokenType.lsqbra) & Expr() & match(TokenType.colon) & Expr() & match(TokenType.colon) & Expr() & match(TokenType.rsqbra)){
                if(debug) System.out.println("<Factor> ::= 'qm' '[' <Expr> ':' <Expr> ':' <Expr> ']' ");
                result.ref = ASTnodeFactory.makeFamily(NodeType.ternary, expr1P.ref, expr2P.ref, expr3P.ref);
                return true;
            }
            else return false;
        }
        else return false;
    }


    private boolean FuncBody(){
        //*result is funcBody. Adds varDeclList and statList childs, who will potentially get their children in in MethodBodyVar() and StatementList()
        if(debug){
            System.out.println("DEBUG: Parsing <FuncBody> at line "+lookahead.getLocation());
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> result = semStack.pop();
        if(!skipErrors(first(Symbol.FuncBody), follow(Symbol.FuncBody))){return false;}
        if(lookahead.getType().equals(TokenType.lcurbra)){
            Pointer<Node> varDeclListP = new Pointer<Node>();
            varDeclListP.ref = ASTnodeFactory.makeNode(NodeType.varDeclList);
            result.ref.adoptChildren(varDeclListP.ref);
            Pointer<Node> statP = new Pointer<Node>();
            semStack.push(statP);
            semStack.push(varDeclListP);
            
            derivTemp.add(derivIndex, "'{'"); derivTemp.add(derivIndex+1, "<MethodBodyVar>"); derivTemp.add(derivIndex+2, "<StatementList>"); derivTemp.add(derivIndex+3, "'}'"); 
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.lcurbra) & MethodBodyVar() & StatementList() & match(TokenType.rcurbra)){
                if(debug) System.out.println("<FuncBody> ::= '{' <MethodBodyVar> <StatementList> '}' ");
                Node statList = ASTnodeFactory.makeNode(NodeType.statList);
                result.ref.adoptChildren(statList);
                if(statP.ref!=null){
                    statList.adoptChildren(statP.ref);
                }
                return true;
            }
            else return false;
        }
        else return false;
    }


    private boolean FuncDecl(){
        //*result is null at first, becomes funcDecl
        if(debug){
            System.out.println("DEBUG: Parsing <FuncDecl> at line "+lookahead.getLocation());
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> result = semStack.pop();
        if(!skipErrors(first(Symbol.FuncDecl), follow(Symbol.FuncDecl))){return false;}
        if(lookahead.getType().equals(TokenType.Func)){
            Pointer<Token> idToken = new Pointer<Token>();
            Pointer<Node> fParamListP = new Pointer<Node>();
            fParamListP.ref = ASTnodeFactory.makeNode(NodeType.fParamList);
            Pointer<Node> fTypeP = new Pointer<Node>();
            semStack.push(fTypeP);
            semStack.push(fParamListP);

            derivTemp.add(derivIndex, "'func'"); derivTemp.add(derivIndex+1, "'id'"); derivTemp.add(derivIndex+2, "'('"); derivTemp.add(derivIndex+3, "<FParams>"); derivTemp.add(derivIndex+4, "')'"); derivTemp.add(derivIndex+5, "':'"); derivTemp.add(derivIndex+6, "<FuncDeclTail>"); derivTemp.add(derivIndex+7, "';'"); 
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.Func) & match(TokenType.id, idToken) & match(TokenType.lpar) & FParams() & match(TokenType.rpar) & match(TokenType.colon) & FuncDeclTail() & match(TokenType.semicol)){
                if(debug) System.out.println("<FuncDecl> ::= 'func' 'id' '(' <FParams> ')' ':' <FuncDeclTail> ';' ");
                Node id = ASTnodeFactory.makeNode(NodeType.id, idToken.ref);
                result.ref = ASTnodeFactory.makeFamily(NodeType.funcDecl, fTypeP.ref, id, fParamListP.ref);
                return true;
            }
            else return false;
        }
        else return false;
    }


    private boolean FuncDeclTail(){
        //*result is null, becomes type
        if(debug){
            System.out.println("DEBUG: Parsing <FuncDeclTail> at line "+lookahead.getLocation());
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> result = semStack.pop();
        if(!skipErrors(first(Symbol.FuncDeclTail), follow(Symbol.FuncDeclTail))){return false;}
        if(first(Symbol.Type).contains(lookahead.getType())){
            semStack.push(result);

            derivTemp.add(derivIndex, "<Type>");
            derivations.add(String.join(" ", derivTemp));
            if(Type()){
                if(debug) System.out.println("<FuncDeclTail> ::= <Type> ");
                return true;
            }
            else return false;
        }
        else if(lookahead.getType().equals(TokenType.Void)){
            Pointer<Token> typeToken = new Pointer<Token>();

            derivTemp.add(derivIndex, "'void'"); 
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.Void, typeToken)){
                if(debug) System.out.println("<FuncDeclTail> ::= 'void' ");
                result.ref = ASTnodeFactory.makeNode(NodeType.type, typeToken.ref);
                return true;
            }
            else return false;
        }
        else return false;
    }


    private boolean FuncDef(){
        //*result is funcDefList. Every call adds a new child until follow condition
        if(debug){
            System.out.println("DEBUG: Parsing <FuncDef> at line "+lookahead.getLocation());
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> result = semStack.pop();
        if(!skipErrors(first(Symbol.FuncDef), follow(Symbol.FuncDef))){return false;}
        if(first(Symbol.Function).contains(lookahead.getType())){
            Pointer<Node> funcDefP = new Pointer<Node>();
            funcDefP.ref = ASTnodeFactory.makeNode(NodeType.funcDef);
            result.ref.adoptChildren(funcDefP.ref);
            semStack.push(result);
            semStack.push(funcDefP);

            derivTemp.add(derivIndex, "<Function>"); derivTemp.add(derivIndex+1, "<FuncDef>"); 
            derivations.add(String.join(" ", derivTemp));
            if(Function() & FuncDef()){
                if(debug) System.out.println("<FuncDef> ::= <Function> <FuncDef> ");
                return true;
            }
            else return false;
        }

        else if(follow(Symbol.FuncDef).contains(lookahead.getType())){
            if(debug) System.out.println("<FuncDef> ::= EPSILON ");
            derivations.add(String.join(" ", derivTemp));
            return true;
        }
        else return false;
    }


    private boolean FuncHead(){
        //*result is funcDef. Adds all required childs (except funcBody)
        /*
            id and scopeSpec are a little tricky because of ClassMethod()
            if *scopeSpecP is null after ClassMethod(), then idToken is funcDef's id, and scopeSpec is EPSILON
            if *scopeSpecP is id after ClassMethod(), then idToken was funcDef's scopeSpec, and scopeSpec is idToken
        */
        if(debug){
            System.out.println("DEBUG: Parsing <FuncHead> at line "+lookahead.getLocation());
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> result = semStack.pop();
        if(!skipErrors(first(Symbol.FuncHead), follow(Symbol.FuncHead))){return false;}
        if(lookahead.getType().equals(TokenType.Func)){
            Pointer<Token> idToken = new Pointer<Token>();
            Pointer<Node> scopeSpecP = new Pointer<Node>();
            Pointer<Node> fParamListP = new Pointer<Node>();
            fParamListP.ref = ASTnodeFactory.makeNode(NodeType.fParamList);
            Pointer<Node> typeP = new Pointer<Node>();
            semStack.push(typeP);
            semStack.push(fParamListP);
            semStack.push(scopeSpecP);

            derivTemp.add(derivIndex, "'func'"); derivTemp.add(derivIndex+1, "'id'"); derivTemp.add(derivIndex+2, "<ClassMethod>"); derivTemp.add(derivIndex+3, "'('"); derivTemp.add(derivIndex+4, "<FParams>"); derivTemp.add(derivIndex+5, "')'"); derivTemp.add(derivIndex+6, "':'"); derivTemp.add(derivIndex+7, "<FuncDeclTail>"); 
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.Func) & match(TokenType.id, idToken) &  ClassMethod() & match(TokenType.lpar) & FParams() & match(TokenType.rpar) & match(TokenType.colon) & FuncDeclTail()){
                if(debug) System.out.println("<FuncHead> ::= 'func' 'id' <ClassMethod> '(' <FParams> ')' ':' <FuncDeclTail>");
                Node id, scopeSpec;
                if(scopeSpecP.ref==null){
                    id = ASTnodeFactory.makeNode(NodeType.id, idToken.ref);
                    scopeSpec = ASTnodeFactory.makeNode(NodeType.EPSILON);
                }
                else{
                    id = scopeSpecP.ref;
                    scopeSpec = ASTnodeFactory.makeNode(NodeType.id, idToken.ref);
                }
                result.ref.adoptChildren(typeP.ref);
                result.ref.adoptChildren(scopeSpec);
                result.ref.adoptChildren(id);
                result.ref.adoptChildren(fParamListP.ref);
                return true;
            }
            else return false;
        }
        else return false;
    }


    private boolean FuncOrAssignStat(){
        //*result is null, becomes assignStat or var with 1+ childs dataMember/fCall(semantically fCall if last child is fCall)
        //if *result still null after FuncOrAssignStatIdnest(), then *result becomes var with rightmost child fCall
        //note that *varElement comes back from FuncOrAssignStatIdNest() as fcall/dataMember with a child, but missing id child
        if(debug){
        System.out.println("DEBUG: Parsing <FuncOrAssignStat> at line "+lookahead.getLocation());
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> result = semStack.pop();
        if(!skipErrors(first(Symbol.FuncOrAssignStat), follow(Symbol.FuncOrAssignStat))){return false;}
        if(lookahead.getType().equals(TokenType.id)){
            Pointer<Token> idToken = new Pointer<Token>();
            Pointer<Node> varElementP = new Pointer<Node>();
            semStack.push(varElementP);
            semStack.push(result);

            derivTemp.add(derivIndex, "'id'"); derivTemp.add(derivIndex+1, "<FuncOrAssignStatIdnest"); 
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.id, idToken) & FuncOrAssignStatIdnest()){
                if(debug) System.out.println("<FuncOrAssignStat> ::= 'id' <FuncOrAssignStatIdnest> ");
                Node id = ASTnodeFactory.makeNode(NodeType.id, idToken.ref);
                varElementP.ref.adoptChildrenLeftmost(id);
                Node var = ASTnodeFactory.makeNode(NodeType.var);
                var.adoptChildren(varElementP.ref);//and all its siblings
                if(result.ref==null){
                    result.ref = var;
                }
                else{
                    result.ref.adoptChildrenLeftmost(var);
                }

                return true;
            }
            else return false;
        }
        else return false;
    }


    private boolean FuncOrAssignStatIdnest(){
        //*result is null and becomes assignStat or remains null. If remains null, becomes var after return with *varElementP child and all its siblings
        //varElementP is null, becomes dataMember/fcall with only 1 child(missing id) and siblings dataMember/fcall(with 2 childs, id incl)
        if(debug){
            System.out.println("DEBUG: Parsing <FuncOrAssignStatIdnest> at line "+lookahead.getLocation());
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> result = semStack.pop();
        Pointer<Node> varElementP = semStack.pop();
        if(!skipErrors(first(Symbol.FuncOrAssignStatIdnest), follow(Symbol.FuncOrAssignStatIdnest))){return false;}
        if(first(Symbol.IndiceRep).contains(lookahead.getType()) || first(Symbol.FuncOrAssignStatIdnestVarTail).contains(lookahead.getType())){
            Pointer<Node> exprP = new Pointer<Node>();
            Pointer<Node> varElementSibP = new Pointer<Node>();
            semStack.push(varElementSibP);
            semStack.push(result);
            semStack.push(exprP);

            derivTemp.add(derivIndex, "<IndiceRep>"); derivTemp.add(derivIndex+1, "<FuncOrAssignStatIdnestVarTail>"); 
            derivations.add(String.join(" ", derivTemp));
            if(IndiceRep() & FuncOrAssignStatIdnestVarTail()){
                if(debug) System.out.println("<FuncOrAssignStatIdnest> ::= <IndiceRep> <FuncOrAssignStatIdnestVarTail> ");
                Node indexList = ASTnodeFactory.makeNode(NodeType.indexList);
                if(exprP.ref!=null){
                    indexList.adoptChildren(exprP.ref);
                }
                varElementP.ref = ASTnodeFactory.makeFamily(NodeType.dataMember, indexList);
                if(varElementSibP.ref!=null){
                    varElementP.ref.makeSiblings(varElementSibP.ref);
                }
                return true;
            }
            else return false;
        }
        else if(lookahead.getType().equals(TokenType.lpar)){//GUARANTEED FUNCTIONCALL            
            Pointer<Node> fCallParamsP = new Pointer<Node>();
            Pointer<Node> varElementSibP = new Pointer<Node>();
            semStack.push(varElementSibP);
            semStack.push(fCallParamsP);

            derivTemp.add(derivIndex, "'('"); derivTemp.add(derivIndex+1, "<AParams>"); derivTemp.add(derivIndex+2, "')'"); derivTemp.add(derivIndex+3, "<FuncOrAssignStatIdnestFuncTail>");
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.lpar) & AParams() & match(TokenType.rpar) & FuncOrAssignStatIdnestFuncTail()){
                if(debug) System.out.println("<FuncOrAssignStatIdnest> ::= '(' <AParams> ')' <FuncOrAssignStatIdnestFuncTail> ");             
                varElementP.ref = ASTnodeFactory.makeFamily(NodeType.fCall, fCallParamsP.ref);
                if(varElementSibP.ref!=null){
                    varElementP.ref.makeSiblings(varElementSibP.ref);
                }
                return true;
            }
            else return false;
        }
        else return false;
    }

    //GUARANTEED TO BE FUNC CALL
    private boolean FuncOrAssignStatIdnestFuncTail(){
        //there's no *result since will remain null as can't be assignStat anymore.
        //*varElementP is null, remains so or becomes dataMember/fcall with siblings of dataMember/fcall, but rightmost sibling is fcall
        if(debug){
            System.out.println("DEBUG: Parsing <FuncOrAssignStatIdnestFuncTail> at line "+lookahead.getLocation());
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> varElementP = semStack.pop();
        if(!skipErrors(first(Symbol.FuncOrAssignStatIdnestFuncTail), follow(Symbol.FuncOrAssignStatIdnestFuncTail))){return false;}
        if(lookahead.getType().equals(TokenType.dot)){         
            Pointer<Token> idToken = new Pointer<Token>();
            semStack.push(varElementP);//after if is dataMember/fcall with child, but missing id

            derivTemp.add(derivIndex, "'.'"); derivTemp.add(derivIndex+1, "'id'"); derivTemp.add(derivIndex+2, "<FuncStatTail>"); 
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.dot) & match(TokenType.id, idToken) & FuncStatTail()){
                if(debug) System.out.println("<FuncOrAssignStatIdnestFuncTail> ::= '.' 'id' <FuncStatTail> ");
                Node id = ASTnodeFactory.makeNode(NodeType.id, idToken.ref);
                varElementP.ref.adoptChildrenLeftmost(id);
                return true;
            }
            else return false;
        }

        else if(follow(Symbol.FuncOrAssignStatIdnestFuncTail).contains(lookahead.getType())){
            if(debug) System.out.println("<FuncOrAssignStatIdnestFuncTail> ::= EPSILON");
            derivations.add(String.join(" ", derivTemp));
            return true;
        }
        else return false;
    }


    private boolean FuncStatTail(){
        //there's no *result since will remain null as can't be assignStat anymore.
        //*varElementP is null and becomes dataMember/fcall, but with only 1 child(missing id)
        if(debug){
            System.out.println("DEBUG: Parsing <FuncStatTail> at line "+lookahead.getLocation());
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> varElementP = semStack.pop();       
        if(!skipErrors(first(Symbol.FuncStatTail), follow(Symbol.FuncStatTail))){return false;}
        if(first(Symbol.IndiceRep).contains(lookahead.getType()) || lookahead.getType().equals(TokenType.dot)){   
            Pointer<Node> exprP = new Pointer<Node>();
            Pointer<Token> idToken = new Pointer<Token>();
            Pointer<Node> varElementSibP = new Pointer<Node>();
            semStack.push(varElementSibP);
            semStack.push(exprP);

            derivTemp.add(derivIndex, "<IndiceRep>"); derivTemp.add(derivIndex+1, "'.'"); derivTemp.add(derivIndex+2, "'id'"); derivTemp.add(derivIndex+3, "<FuncStatTail>"); 
            derivations.add(String.join(" ", derivTemp));
            if(IndiceRep() & match(TokenType.dot) & match(TokenType.id, idToken) & FuncStatTail()){
                if(debug) System.out.println("<FuncStatTail> ::= <IndiceRep> '.' 'id' <FuncStatTail> ");
                Node indexList = ASTnodeFactory.makeNode(NodeType.indexList);
                if(exprP.ref!=null){
                    indexList.adoptChildren(exprP.ref);
                }
                varElementP.ref = ASTnodeFactory.makeFamily(NodeType.dataMember, indexList);
                Node id = ASTnodeFactory.makeNode(NodeType.id, idToken.ref);
                varElementSibP.ref.adoptChildrenLeftmost(id);
                varElementP.ref.makeSiblings(varElementSibP.ref);
                return true;
            }
            else return false;
        }
        else if(lookahead.getType().equals(TokenType.lpar)){
            Pointer<Node> fCallParamsP = new Pointer<Node>();
            Pointer<Node> varElementSibP = new Pointer<Node>();
            semStack.push(varElementSibP);
            semStack.push(fCallParamsP);

            derivTemp.add(derivIndex, "'('"); derivTemp.add(derivIndex+1, "<AParams>"); derivTemp.add(derivIndex+2, "')'"); derivTemp.add(derivIndex+3, "<FuncStatTailIdnest>"); 
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.lpar) & AParams() & match(TokenType.rpar) & FuncStatTailIdnest()){
                if(debug) System.out.println("<FuncStatTail> ::= '(' <AParams> ')' <FuncStatTailIdnest> ");
                varElementP.ref = ASTnodeFactory.makeFamily(NodeType.fCall, fCallParamsP.ref);
                if(varElementSibP.ref!=null){
                    varElementP.ref.makeSiblings(varElementSibP.ref);
                }    
                return true;
            }
            else return false;
        }
        else return false;
    }


    private boolean FuncStatTailIdnest(){
        //there's no *result since will remain null as can't be assignStat anymore.
        //*varElementP is null, remains so or becomes dataMember/fcall with other siblings, but rightmost is fcall
        if(debug){
            System.out.println("DEBUG: Parsing <FuncStatTailIdnest> at line "+lookahead.getLocation());
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> varElementP = semStack.pop();
        if(!skipErrors(first(Symbol.FuncStatTailIdnest), follow(Symbol.FuncStatTailIdnest))){return false;}
        if(lookahead.getType().equals(TokenType.dot)){
            Pointer<Token> idToken = new Pointer<Token>();
            semStack.push(varElementP);//after if is membDecl/fcall with 1 child but missing id   
            
            derivTemp.add(derivIndex, "'.'"); derivTemp.add(derivIndex+1, "'id'"); derivTemp.add(derivIndex+2, "<FuncStatTail>");
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.dot) & match(TokenType.id, idToken) & FuncStatTail()){
                if(debug) System.out.println("<FuncStatTailIdnest> ::= '.' 'id' <FuncStatTail> ");
                Node id = ASTnodeFactory.makeNode(NodeType.id, idToken.ref);
                varElementP.ref.adoptChildrenLeftmost(id);
                return true;
            }
            else return false;
        }

        else if(follow(Symbol.FuncStatTailIdnest).contains(lookahead.getType())){
            if(debug) System.out.println("<FuncStatTailIdnest> ::= EPSILON");
            derivations.add(String.join(" ", derivTemp));
            return true;
        }
        else return false;
    }


    private boolean FuncOrAssignStatIdnestVarTail(){
        //*result is null. Remains so, or becomes assignStat with <expr> child, but missing var which gets added after return in FuncOrAssignStat
        //*varElement is null, remains so if *result becomes assignStat. Or becomes dataMember/fcall
        if(debug){
            System.out.println("DEBUG: Parsing <FuncOrAssignStatIdnestVarTail> at line "+lookahead.getLocation());
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> result = semStack.pop();
        Pointer<Node> varElementP = semStack.pop();
        if(!skipErrors(first(Symbol.FuncOrAssignStatIdnestVarTail), follow(Symbol.FuncOrAssignStatIdnestVarTail))){return false;}
        if(lookahead.getType().equals(TokenType.dot)){
            Pointer<Token> idToken = new Pointer<Token>();
            semStack.push(varElementP);//after if comes back as dataMember/fcall with 1 child, but missing id
            semStack.push(result);

            derivTemp.add(derivIndex, "'.'"); derivTemp.add(derivIndex+1, "'id'"); derivTemp.add(derivIndex+2, "<FuncOrAssignStatIdnest>"); 
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.dot) & match(TokenType.id, idToken) & FuncOrAssignStatIdnest()){
                if(debug) System.out.println("<FuncOrAssignStatIdnestVarTail> ::= '.' 'id' <FuncOrAssignStatIdnest>");
                Node id = ASTnodeFactory.makeNode(NodeType.id, idToken.ref);
                varElementP.ref.adoptChildrenLeftmost(id);
                return true;
            }
            else return false;
        }
        else if(first(Symbol.AssignStatTail).contains(lookahead.getType())){
            semStack.push(result);

            derivTemp.add(derivIndex, "<AssignStatTail>"); 
            derivations.add(String.join(" ", derivTemp));
            if(AssignStatTail()){
                if(debug) System.out.println("<FuncOrAssignStatIdnestVarTail> ::= <AssignStatTail> ");
                return true;
            }
            else return false;
        }
        else return false;
    }


    private boolean FuncOrVar(){
        //*result is null, becomes var with 1+ childs dataMember/fCall.
        //FuncCall semantically is when rightmost child is fCall
        if(debug){
            System.out.println("DEBUG: Parsing <FuncOrVar> at line "+lookahead.getLocation());
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> result = semStack.pop();
        result.ref = ASTnodeFactory.makeNode(NodeType.var);
        if(!skipErrors(first(Symbol.FuncOrVar), follow(Symbol.FuncOrVar))){return false;}
        if(lookahead.getType().equals(TokenType.id)){
            Pointer<Token> idToken = new Pointer<Token>();
            Pointer<Node> varElementP = new Pointer<Node>();
            semStack.push(varElementP);

            derivTemp.add(derivIndex, "'id'"); derivTemp.add(derivIndex+1, "<FuncOrVarIdnest"); 
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.id, idToken) & FuncOrVarIdnest()){
                if(debug) System.out.println("<FuncOrVar> ::= 'id' <FuncOrVarIdnest> ");
                Node id = ASTnodeFactory.makeNode(NodeType.id, idToken.ref);
                varElementP.ref.adoptChildrenLeftmost(id);
                result.ref.adoptChildren(varElementP.ref);
                return true;
            }
            else return false;
        }
        else return false;
    }


    private boolean FuncOrVarIdnest(){
        //*result is null, becomes dataMember/fCall with only 1 child(missing id)
        if(debug){
            System.out.println("DEBUG: Parsing <FuncOrVarIdnest> at line "+lookahead.getLocation());
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> result = semStack.pop();
        if(!skipErrors(first(Symbol.FuncOrVarIdnest), follow(Symbol.FuncOrVarIdnest))){return false;}
        if(first(Symbol.IndiceRep).contains(lookahead.getType()) || first(Symbol.FuncOrVarIdnestTail).contains(lookahead.getType())){
            Pointer<Node> exprP = new Pointer<Node>();
            Pointer<Node> varElementSibP = new Pointer<Node>();
            semStack.push(varElementSibP);
            semStack.push(exprP);

            derivTemp.add(derivIndex, "<IndiceRep>"); derivTemp.add(derivIndex+1, "<FuncOrVarIdnestTail"); 
            derivations.add(String.join(" ", derivTemp));
            if(IndiceRep() & FuncOrVarIdnestTail()){
                if(debug) System.out.println("<FuncOrVarIdnest> ::= <IndiceRep> <FuncOrVarIdnestTail> ");
                Node indexList = ASTnodeFactory.makeNode(NodeType.indexList);
                if(exprP.ref!=null){
                    indexList.adoptChildren(exprP.ref);
                }
                result.ref = ASTnodeFactory.makeFamily(NodeType.dataMember, indexList);
                if(varElementSibP.ref!=null){
                    result.ref.makeSiblings(varElementSibP.ref);
                }
                return true;
            }
            else return false;
        }
        else if(lookahead.getType().equals(TokenType.lpar)){
            Pointer<Node> fCallParamsP = new Pointer<Node>();
            Pointer<Node> varElementSibP = new Pointer<Node>();
            semStack.push(varElementSibP);
            semStack.push(fCallParamsP);

            derivTemp.add(derivIndex, "'('"); derivTemp.add(derivIndex+1, "<AParams>"); derivTemp.add(derivIndex+2, "')'"); derivTemp.add(derivIndex+3, "<FuncOrVarIdnestTail>"); 
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.lpar) & AParams() & match(TokenType.rpar) & FuncOrVarIdnestTail()){
                if(debug) System.out.println("<FuncOrVarIdnest> ::= '(' <AParams> ')' <FuncOrVarIdnestTail> ");
                result.ref = ASTnodeFactory.makeFamily(NodeType.fCall, fCallParamsP.ref);
                if(varElementSibP.ref!=null){
                    result.ref.makeSiblings(varElementSibP.ref);
                }
                return true;
            }
            else return false;
        }

        //was not in grammar, check if right. Both symbols are nullable, so implies its nullable too
        else if(follow(Symbol.FuncOrVarIdnest).contains(lookahead.getType())){
            if(debug) System.out.println("<FuncOrVarIdnest> ::= EPSILON");
            derivations.add(String.join(" ", derivTemp));
            Node indexList = ASTnodeFactory.makeNode(NodeType.indexList);
            result.ref = ASTnodeFactory.makeFamily(NodeType.dataMember, indexList);
            return true;
        }
        else return false;
    }




    private boolean FuncOrVarIdnestTail(){
        //*result is null, remains so or becomes dataMember/fCall
        if(debug){
            System.out.println("DEBUG: Parsing <FuncOrVarIdnestTail> at line "+lookahead.getLocation());
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> result = semStack.pop();
        if(!skipErrors(first(Symbol.FuncOrVarIdnestTail), follow(Symbol.FuncOrVarIdnestTail))){return false;}
        if(lookahead.getType().equals(TokenType.dot)){
            Pointer<Token> idToken = new Pointer<Token>();
            semStack.push(result);

            derivTemp.add(derivIndex, "'.'"); derivTemp.add(derivIndex+1, "'id'"); derivTemp.add(derivIndex+2, "<FuncOrVarIdnest");
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.dot) & match(TokenType.id, idToken) & FuncOrVarIdnest()){
                if(debug) System.out.println("<FuncOrVarIdnestTail> ::= '.' 'id' <FuncOrVarIdnest> ");
                Node id = ASTnodeFactory.makeNode(NodeType.id, idToken.ref);
                result.ref.adoptChildrenLeftmost(id);
                return true;
            }
            else return false;
        }

        else if(follow(Symbol.FuncOrVarIdnestTail).contains(lookahead.getType())){
            if(debug) System.out.println("<FuncOrVarIdnestTail> ::= EPSILON ");
            derivations.add(String.join(" ", derivTemp));
            return true;
        }
        else return false;
    }


    private boolean Function(){
        //*result is funcDef. Adds all required childs
        if(debug){
            System.out.println("DEBUG: Parsing <Function> at line "+lookahead.getLocation());
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> result = semStack.pop();
        if(!skipErrors(first(Symbol.Function), follow(Symbol.Function))){return false;}
        if(first(Symbol.FuncHead).contains(lookahead.getType())){
            Pointer<Node> funcBodyP = new Pointer<Node>();
            funcBodyP.ref = ASTnodeFactory.makeNode(NodeType.funcBody);
            semStack.push(funcBodyP);
            semStack.push(result);

            derivTemp.add(derivIndex, "<FuncHead>"); derivTemp.add(derivIndex+1, "<FuncBody>"); 
            derivations.add(String.join(" ", derivTemp));
            if(FuncHead() & FuncBody()){
                if(debug) System.out.println("<Function> ::= <FuncHead> <FuncBody> ");
                derivations.add(String.join(" ", derivTemp));
                result.ref.adoptChildren(funcBodyP.ref);
                return true;
            }
            else return false;
        }
        else return false;
    }


    private boolean IndiceRep(){
        //*result is null, remains so if follow condition, or becomes <expr> with potentially more <expr> siblings for each IndiceRep()
        if(debug){
            System.out.println("DEBUG: Parsing <IndiceRep> at line "+lookahead.getLocation());
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> result = semStack.pop();
        if(!skipErrors(first(Symbol.IndiceRep), follow(Symbol.IndiceRep))){return false;}
        if(lookahead.getType().equals(TokenType.lsqbra)){
            Pointer<Node> nestedExprP = new Pointer<Node>();
            semStack.push(nestedExprP);
            semStack.push(result);

            derivTemp.add(derivIndex, "'['"); derivTemp.add(derivIndex+1, "<Expr>"); derivTemp.add(derivIndex+2, "']'"); derivTemp.add(derivIndex+3, "<IndiceRep>"); 
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.lsqbra) & Expr() & match(TokenType.rsqbra) & IndiceRep()){
                if(debug) System.out.println("<IndiceRep> ::= '[' <Expr> ']' <IndiceRep> ");
                if(nestedExprP.ref!=null){
                    result.ref.makeSiblings(nestedExprP.ref);
                }
                return true;
            }
            else return false;
        }
        else if(follow(Symbol.IndiceRep).contains(lookahead.getType())){
            if(debug) System.out.println("<IndiceRep> ::= EPSILON ");
            derivations.add(String.join(" ", derivTemp));
            return true;
        }
        else return false;
    }


    private boolean Inherit(){
        //*result is inherList. Potentially adds a child and several more in NestedId()
        if(debug){
            System.out.println("DEBUG: Parsing <Inherit> at line "+lookahead.getLocation());
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> result = semStack.pop();
        if(!skipErrors(first(Symbol.Inherit), follow(Symbol.Inherit))){return false;}
        if(lookahead.getType().equals(TokenType.Inherits)){
            Pointer<Token> idToken = new Pointer<Token>();
            Pointer<Node> nestedIdP = new Pointer<Node>();
            semStack.push(nestedIdP);

            derivTemp.add(derivIndex, "'inherits'"); derivTemp.add(derivIndex+1, "'id'"); derivTemp.add(derivIndex+2, "<NestedId"); 
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.Inherits) & match(TokenType.id, idToken) & NestedId()){
                if(debug) System.out.println("<Inherit> ::= 'inherits' 'id' <NestedId> ");
                Node id = ASTnodeFactory.makeNode(NodeType.id, idToken.ref);
                result.ref.adoptChildren(id);
                if(nestedIdP.ref!=null){
                    id.makeSiblings(nestedIdP.ref);
                }
                return true;
            }
            else return false;
        }
        else if(follow(Symbol.Inherit).contains(lookahead.getType())){
            if(debug) System.out.println("<Inherit> ::= EPSILON ");
            derivations.add(String.join(" ", derivTemp));
            return true;
        }
        else return false;
    }


    private boolean IntNum(){
        //*result is null, becomes EPSILON/num
        if(debug){
            System.out.println("DEBUG: Parsing <IntNum> at line "+lookahead.getLocation());
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> result = semStack.pop();
        if(!skipErrors(first(Symbol.IntNum), follow(Symbol.IntNum))){return false;}
        if(lookahead.getType().equals(TokenType.intnum)){
            Pointer<Token> intNumToken = new Pointer<Token>();

            derivTemp.add(derivIndex, "'intnum'"); 
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.intnum, intNumToken)){
                if(debug) System.out.println("<IntNum> ::= 'intnum' ");
                result.ref = ASTnodeFactory.makeNode(NodeType.num, intNumToken.ref);
                return true;
            }
            else return false;
        }
        else if(follow(Symbol.IntNum).contains(lookahead.getType())){
            if(debug) System.out.println("<IntNum> ::= EPSILON ");
            derivations.add(String.join(" ", derivTemp));
            result.ref = ASTnodeFactory.makeNode(NodeType.EPSILON);
            return true;
        }
        else return false;
    }


    private boolean MemberDecl(){
        //*result is null, becomes funcDecl or varDecl
        if(debug){
            System.out.println("DEBUG: Parsing <MemberDecl>");
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> result = semStack.pop();
        if(!skipErrors(first(Symbol.MemberDecl), follow(Symbol.MemberDecl))){return false;}
        if(first(Symbol.FuncDecl).contains(lookahead.getType())){
            semStack.push(result);

            derivTemp.add(derivIndex, "<FuncDecl>");
            derivations.add(String.join(" ", derivTemp));
            if(FuncDecl()){
                if(debug) System.out.println("<MemberDecl> ::= <FuncDecl> ");
                return true;
            }
            else return false;
        }
        else if(first(Symbol.VarDecl).contains(lookahead.getType())){
            semStack.push(result);

            derivTemp.add(derivIndex, "<VarDecl>");
            derivations.add(String.join(" ", derivTemp));
            if(VarDecl()){
                if(debug) System.out.println("<MemberDecl> ::= <VarDecl> ");
                return true;
            }
            else return false;
        }
        else return false;
    }


    private boolean MethodBodyVar(){
        //*result is varDeclList,  potentially adds varDecl childs to it. 
        if(debug){
            System.out.println("DEBUG: Parsing <MethodBodyVar> at line "+lookahead.getLocation());
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> result = semStack.pop();
        if(!skipErrors(first(Symbol.MethodBodyVar), follow(Symbol.MethodBodyVar))){return false;}
        if(lookahead.getType().equals(TokenType.Var)){
            Pointer<Node> varDeclP = new Pointer<Node>();
            semStack.push(varDeclP);

            derivTemp.add(derivIndex, "'var'"); derivTemp.add(derivIndex+1, "'{'"); derivTemp.add(derivIndex+2, "<VarDeclRep>"); derivTemp.add(derivIndex+3, "'}'");
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.Var) & match(TokenType.lcurbra) & VarDeclRep() & match(TokenType.rcurbra)){
                if(debug) System.out.println("<MethodBodyVar> ::= 'var' '{' <VarDeclRep> '}' ");
                if(varDeclP.ref!=null){
                    result.ref.adoptChildren(varDeclP.ref);//and all its siblings
                }
                return true;
            }
            else return false;
        }
        else if(follow(Symbol.MethodBodyVar).contains(lookahead.getType())){
            if(debug) System.out.println("<MethodBodyVar> ::= EPSILON ");
            derivations.add(String.join(" ", derivTemp));
            return true;
        }
        else return false;
    }


    private boolean MultOp(){
        //*result is null, becomes multOp
        if(debug){
            System.out.println("DEBUG: Parsing <MultOp> at line "+lookahead.getLocation());
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> result = semStack.pop();
        Pointer<Token> multOpToken = new Pointer<Token>();
        if(!skipErrors(first(Symbol.MultOp), follow(Symbol.MultOp))){return false;}
        if(lookahead.getType().equals(TokenType.mult)){

            derivTemp.add(derivIndex, "'*'");
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.mult, multOpToken)){
                if(debug) System.out.println("<MultOp> ::= '*' ");
                result.ref = ASTnodeFactory.makeNode(NodeType.multOp, multOpToken.ref);
                return true;
            }
            else return false;
        }
        else if(lookahead.getType().equals(TokenType.div)){

            derivTemp.add(derivIndex, "'/'"); 
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.div, multOpToken)){
                if(debug) System.out.println("<MultOp> ::= '/' ");
                result.ref = ASTnodeFactory.makeNode(NodeType.multOp, multOpToken.ref);
                return true;
            }
            else return false;
        }
        else if(lookahead.getType().equals(TokenType.and)){

            derivTemp.add(derivIndex, "'and'");
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.and, multOpToken)){
                if(debug) System.out.println("<MultOp> ::= 'and' ");
                result.ref = ASTnodeFactory.makeNode(NodeType.multOp, multOpToken.ref);
                return true;
            }
            else return false;
        }
        else return false;
    }


    private boolean NestedId(){
        //*result is null. Becomes id or or remains null
        if(debug){
            System.out.println("DEBUG: Parsing <NestedId> at line "+lookahead.getLocation());
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> result = semStack.pop();
        if(!skipErrors(first(Symbol.NestedId), follow(Symbol.NestedId))){return false;}
        if(lookahead.getType().equals(TokenType.comma)){
            Pointer<Token> idToken = new Pointer<Token>();
            Pointer<Node> nestedIdP = new Pointer<Node>();
            semStack.push(nestedIdP);

            derivTemp.add(derivIndex, "','"); derivTemp.add(derivIndex+1, "'id'"); derivTemp.add(derivIndex+2, "<NestedId>"); 
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.comma) & match(TokenType.id, idToken) & NestedId()){
                if(debug) System.out.println("<NestedId> ::= ',' 'id' <NestedId> ");
                Node id = ASTnodeFactory.makeNode(NodeType.id, idToken.ref);
                if(nestedIdP.ref!=null){
                    id.makeSiblings(nestedIdP.ref);
                }
                result.ref = id;
                return true;
            }
            else return false;
        }
        else if(follow(Symbol.NestedId).contains(lookahead.getType())){
            if(debug) System.out.println("<NestedId> ::= EPSILON ");
            derivations.add(String.join(" ", derivTemp));
            return true;
        }
        else return false;
    }


    private boolean Prog(){
        //*result is null at first. Adds 3 children that can potentially have no children of their own
        if(debug){
            System.out.println("DEBUG: Parsing <Prog> at line "+lookahead.getLocation());
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> result = semStack.pop();
        if(!skipErrors(first(Symbol.Prog), follow(Symbol.Prog))){return false;}  
        if(first(Symbol.ClassDecl).contains(lookahead.getType()) || first(Symbol.FuncDef).contains(lookahead.getType()) || lookahead.getType().equals(TokenType.Main)){  
            Pointer<Node> classDeclListP = new Pointer<Node>(ASTnodeFactory.makeNode(NodeType.classDeclList));
            Pointer<Node> funcDefListP = new Pointer<Node>(ASTnodeFactory.makeNode(NodeType.funcDefList));
            Pointer<Node> mainFuncBodyP = new Pointer<Node>(ASTnodeFactory.makeNode(NodeType.funcBody));
            semStack.push(mainFuncBodyP);
            semStack.push(funcDefListP);
            semStack.push(classDeclListP);
            
            derivTemp.add(derivIndex, "<ClassDecl>"); derivTemp.add(derivIndex+1, "<FuncDef>"); derivTemp.add(derivIndex+2, "'main'"); derivTemp.add(derivIndex+3, "<FuncBody>");
            derivations.add(String.join(" ", derivTemp));
            if(ClassDecl() & FuncDef() & match(TokenType.Main) & FuncBody()){
                if(debug) System.out.println("<Prog> ::= <ClassDecl> <FuncDef> 'main' <FuncBody> ");
                result.ref = ASTnodeFactory.makeFamily(NodeType.prog, classDeclListP.ref, funcDefListP.ref, mainFuncBodyP.ref);
                return true;
            }
            else return false;
        }
        else return false;
    }


    private boolean RelOp(){
        //*result is null, becomes relOp
        if(debug){
            System.out.println("DEBUG: Parsing <RelOp> at line "+lookahead.getLocation());
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> result = semStack.pop();
        Pointer<Token> relOpToken = new Pointer<Token>();
        if(!skipErrors(first(Symbol.RelOp), follow(Symbol.RelOp))){return false;}
        if(lookahead.getType().equals(TokenType.eq)){

            derivTemp.add(derivIndex, "'eq'"); 
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.eq, relOpToken)){
                if(debug) System.out.println("<RelOp> ::= 'eq' ");
                result.ref = ASTnodeFactory.makeNode(NodeType.relOp, relOpToken.ref);
                return true;
            }
            else return false;
        }
        else if(lookahead.getType().equals(TokenType.neq)){

            derivTemp.add(derivIndex, "'neq'"); 
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.neq, relOpToken)){
                if(debug) System.out.println("<RelOp> ::= 'neq' ");
                result.ref = ASTnodeFactory.makeNode(NodeType.relOp, relOpToken.ref);
                return true;
            }
            else return false;
        }
        else if(lookahead.getType().equals(TokenType.lt)){

            derivTemp.add(derivIndex, "'lt'");
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.lt, relOpToken)){
                if(debug) System.out.println("<RelOp> ::= 'lt' ");
                result.ref = ASTnodeFactory.makeNode(NodeType.relOp, relOpToken.ref);
                return true;
            }
            else return false;
        }
        else if(lookahead.getType().equals(TokenType.gt)){

            derivTemp.add(derivIndex, "'gt'");
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.gt, relOpToken)){
                if(debug) System.out.println("<RelOp> ::= 'gt' ");
                result.ref = ASTnodeFactory.makeNode(NodeType.relOp, relOpToken.ref);
                return true;
            }
            else return false;
        }
        else if(lookahead.getType().equals(TokenType.leq)){

            derivTemp.add(derivIndex, "'leq'"); 
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.leq, relOpToken)){
                if(debug) System.out.println("<RelOp> ::= 'leq' ");
                result.ref = ASTnodeFactory.makeNode(NodeType.relOp, relOpToken.ref);
                return true;
            }
            else return false;
        }
        else if(lookahead.getType().equals(TokenType.geq)){

            derivTemp.add(derivIndex, "'geq'"); 
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.geq, relOpToken)){
                if(debug) System.out.println("<RelOp> ::= 'geq' ");
                result.ref = ASTnodeFactory.makeNode(NodeType.relOp, relOpToken.ref);
                return true;
            }
            else return false;
        }
        else return false;
    }


    private boolean Sign(){
        //*result is null, becomes sign
        if(debug){
            System.out.println("DEBUG: Parsing <Sign> at line "+lookahead.getLocation());
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> result = semStack.pop();
        Pointer<Token> signToken = new Pointer<Token>();
        if(!skipErrors(first(Symbol.Sign), follow(Symbol.Sign))){return false;}
        if(lookahead.getType().equals(TokenType.plus)){

            derivTemp.add(derivIndex, "'+'"); 
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.plus, signToken)){
                if(debug) System.out.println("<Sign> ::= '+' ");
                result.ref = ASTnodeFactory.makeNode(NodeType.sign, signToken.ref);
                return true;
            }
            else return false;
        }
        else if(lookahead.getType().equals(TokenType.minus)){

            derivTemp.add(derivIndex, "'-'"); 
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.minus, signToken)){
                if(debug) System.out.println("<Sign> ::= '-' ");
                result.ref = ASTnodeFactory.makeNode(NodeType.sign, signToken.ref);
                return true;
            }
            else return false;
        }
        else return false;
    }



    private boolean StatBlock(){
        //*result is null, becomes statList with 0+ <statement> children
        if(debug){
            System.out.println("DEBUG: Parsing <StatBlock> at line "+lookahead.getLocation());
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> result = semStack.pop();
        result.ref = ASTnodeFactory.makeNode(NodeType.statList);
        if(!skipErrors(first(Symbol.StatBlock), follow(Symbol.StatBlock))){return false;}
        if(lookahead.getType().equals(TokenType.lcurbra)){
            Pointer<Node> statP = new Pointer<Node>();
            semStack.push(statP);

            derivTemp.add(derivIndex, "'{'"); derivTemp.add(derivIndex+1, "<StatementList>"); derivTemp.add(derivIndex+2, "'}'");
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.lcurbra) & StatementList() & match(TokenType.rcurbra)){
                if(debug) System.out.println("<StatBlock> ::= '{' <StatementList> '}' ");
                if(statP.ref!=null){
                    result.ref.adoptChildren(statP.ref);
                }
                return true;
            }
            else return false;
        }
        else if(first(Symbol.Statement).contains(lookahead.getType())){
            Pointer<Node> statP = new Pointer<Node>();
            semStack.push(statP);

            derivTemp.add(derivIndex, "<Statement>"); 
            derivations.add(String.join(" ", derivTemp));
            if(Statement()){
                if(debug) System.out.println("<StatBlock> ::= <Statement> ");
                result.ref.adoptChildren(statP.ref);
                return true;
            }
            else return false;
        }

        else if(follow(Symbol.StatBlock).contains(lookahead.getType())){
            if(debug) System.out.println("<StatBlock> ::= EPSILON ");
            derivations.add(String.join(" ", derivTemp));
            return true;
        }
        else return false;
    }

    private boolean Statement(){
        //*result is null. Becomes a variation of <Statement>
        if(debug){
            System.out.println("DEBUG: Parsing <Statement> at line "+lookahead.getLocation());
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> result = semStack.pop();
        if(!skipErrors(first(Symbol.Statement), follow(Symbol.Statement))){return false;}
        if(first(Symbol.FuncOrAssignStat).contains(lookahead.getType())){
            semStack.push(result);

            derivTemp.add(derivIndex, "<FuncOrAssignStat>"); derivTemp.add(derivIndex+1, "';'"); 
            derivations.add(String.join(" ", derivTemp));
            if(FuncOrAssignStat() & match(TokenType.semicol)){
                if(debug) System.out.println("<Statement> ::= <FuncOrAssignStat> ';' ");
                return true;
            }
            else return false;
        }
        else if(lookahead.getType().equals(TokenType.If)){
            Pointer<Node> relExprP = new Pointer<Node>();
            Pointer<Node> posStatBlockP = new Pointer<Node>();
            Pointer<Node> negStatBlockP = new Pointer<Node>();
            semStack.push(negStatBlockP);
            semStack.push(posStatBlockP);
            semStack.push(relExprP);

            derivTemp.add(derivIndex, "'if'"); derivTemp.add(derivIndex+1, "'('"); derivTemp.add(derivIndex+2, "<Expr>"); derivTemp.add(derivIndex+3, "')'"); derivTemp.add(derivIndex+4, "'then'"); derivTemp.add(derivIndex+5, "<StatBlock>"); derivTemp.add(derivIndex+6, "'else'"); derivTemp.add(derivIndex+7, "'<StatBlock>'"); derivTemp.add(derivIndex+8, "';'");
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.If) & match(TokenType.lpar) & Expr() & match(TokenType.rpar) & match(TokenType.Then) & StatBlock() & match(TokenType.Else) & StatBlock() & match(TokenType.semicol)){
                if(debug) System.out.println("<Statement> ::= 'if' '(' <Expr> ')' 'then' <StatBlock> 'else' <StatBlock> ';' ");
                result.ref = ASTnodeFactory.makeFamily(NodeType.ifStat, relExprP.ref, posStatBlockP.ref, negStatBlockP.ref);
                return true;
            }
            else return false;
        }
        else if(lookahead.getType().equals(TokenType.While)){
            Pointer<Node> relExprP = new Pointer<Node>();
            Pointer<Node> statBlockP = new Pointer<Node>();
            semStack.push(statBlockP);
            semStack.push(relExprP);

            derivTemp.add(derivIndex, "'while'"); derivTemp.add(derivIndex+1, "'('"); derivTemp.add(derivIndex+2, "<Expr>"); derivTemp.add(derivIndex+3, "')'"); derivTemp.add(derivIndex+4, "<StatBlock>"); derivTemp.add(derivIndex+5, "';'"); 
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.While) & match(TokenType.lpar) & Expr() & match(TokenType.rpar) & StatBlock() & match(TokenType.semicol)){
                if(debug) System.out.println("<Statement> ::= 'while' '(' <Expr> ')' <StatBlock> ';' ");
                result.ref = ASTnodeFactory.makeFamily(NodeType.whileStat, relExprP.ref, statBlockP.ref);
                return true;
            }
            else return false;
        }
        else if(lookahead.getType().equals(TokenType.Read)){
            Pointer<Node> varP = new Pointer<Node>();
            semStack.push(varP);
            Pointer<Token> readToken = new Pointer<Token>();

            derivTemp.add(derivIndex, "'read'"); derivTemp.add(derivIndex+1, "'('"); derivTemp.add(derivIndex+2, "<Variable>"); derivTemp.add(derivIndex+3, "')'"); derivTemp.add(derivIndex+4, "';'"); 
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.Read, readToken) & match(TokenType.lpar) & Variable() & match(TokenType.rpar) & match(TokenType.semicol)){
                if(debug) System.out.println("<Statement> ::= 'read' '(' <Variable> ')' ';' ");
                result.ref = ASTnodeFactory.makeFamily(NodeType.readStat, varP.ref);
                result.ref.linenum = String.valueOf(readToken.ref.getLocation());
                return true;
            }
            else return false;
        }
        else if(lookahead.getType().equals(TokenType.Write)){
            Pointer<Node> exprP = new Pointer<Node>();
            semStack.push(exprP);
            Pointer<Token> writeToken = new Pointer<Token>();

            derivTemp.add(derivIndex, "'write'"); derivTemp.add(derivIndex+1, "'('"); derivTemp.add(derivIndex+2, "<Expr>"); derivTemp.add(derivIndex+3, "')'"); derivTemp.add(derivIndex+4, "';'"); 
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.Write, writeToken) & match(TokenType.lpar) & Expr() & match(TokenType.rpar) & match(TokenType.semicol)){
                if(debug) System.out.println("<Statement> ::= 'write' '(' <Expr> ')' ';' ");
                result.ref = ASTnodeFactory.makeFamily(NodeType.writeStat, exprP.ref);
                result.ref.linenum = String.valueOf(writeToken.ref.getLocation());
                return true;
            }
            else return false;
        }
        else if(lookahead.getType().equals(TokenType.Return)){
            Pointer<Node> exprP = new Pointer<Node>();
            semStack.push(exprP);
            Pointer<Token> returnToken = new Pointer<Token>();

            derivTemp.add(derivIndex, "'return'"); derivTemp.add(derivIndex+1, "'('"); derivTemp.add(derivIndex+2, "<Expr>"); derivTemp.add(derivIndex+3, "')'"); derivTemp.add(derivIndex+4, "';'"); 
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.Return, returnToken) & match(TokenType.lpar) & Expr() & match(TokenType.rpar) & match(TokenType.semicol)){
                if(debug) System.out.println("<Statement> ::= 'return' '(' <Expr> ')' ';' ");
                result.ref = ASTnodeFactory.makeFamily(NodeType.returnStat, exprP.ref);
                result.ref.linenum = String.valueOf(returnToken.ref.getLocation());
                return true;
            }
            else return false;
        }
        else if(lookahead.getType().equals(TokenType.Break)){
            result.ref = ASTnodeFactory.makeNode(NodeType.breakStat);

            derivTemp.add(derivIndex, "'break'"); derivTemp.add(derivIndex+1, "';'"); 
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.Break) & match(TokenType.semicol)){
                if(debug) System.out.println("<Statement> ::= 'break' ';' ");
                return true;
            }
            else return false;
        }
        else if(lookahead.getType().equals(TokenType.Continue)){
            result.ref = ASTnodeFactory.makeNode(NodeType.continueStat);

            derivTemp.add(derivIndex, "'continue'"); derivTemp.add(derivIndex+1, "';'"); 
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.Continue) & match(TokenType.semicol)){
                if(debug) System.out.println("<Statement> ::= 'continue' ';' ");
                return true;
            }
            else return false;
        }
        else return false;
    }

    private boolean StatementList(){
        //*result is null. Remains so, or becomes <Statement> with 0+ <Statement> siblings
        if(debug){
            System.out.println("DEBUG: Parsing <StatementList> at line "+lookahead.getLocation());
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> result = semStack.pop();
        if(!skipErrors(first(Symbol.StatementList), follow(Symbol.StatementList))){return false;}
        if(first(Symbol.Statement).contains(lookahead.getType())){
            Pointer<Node> statSibP = new Pointer<Node>();
            semStack.push(statSibP);
            semStack.push(result);

            derivTemp.add(derivIndex, "<Statement>"); derivTemp.add(derivIndex+1, "<StatementList>");
            derivations.add(String.join(" ", derivTemp));
            if(Statement() & StatementList()){
                if(debug) System.out.println("<StatementList> ::= <Statement> <StatementList> ");
                if(statSibP.ref!=null){
                    result.ref.makeSiblings(statSibP.ref);
                }
                return true;
            }
            else return false;
        }
        else if(follow(Symbol.StatementList).contains(lookahead.getType())){
            if(debug) System.out.println("<StatementList> ::= EPSILON ");
            derivations.add(String.join(" ", derivTemp));
            return true;
        }
        else return false;
    }

    private boolean Term(){
        //*result is null, becomes <factor> or multOp with <factor>,<term> childs
        if(debug){
            System.out.println("DEBUG: Parsing <Term> at line "+lookahead.getLocation());
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> result = semStack.pop();
        if(!skipErrors(first(Symbol.Term), follow(Symbol.Term))){return false;}
        if(first(Symbol.Factor).contains(lookahead.getType())){
            Pointer<Node> leftFactorP = new Pointer<Node>();
            Pointer<Node> multOpP = new Pointer<Node>();
            Pointer<Node> termP = new Pointer<Node>();
            semStack.push(termP);
            semStack.push(multOpP);
            semStack.push(leftFactorP);

            derivTemp.add(derivIndex, "<Factor>"); derivTemp.add(derivIndex+1, "<TermTail>"); 
            derivations.add(String.join(" ", derivTemp));
            if(Factor() & TermTail()){
                if(debug) System.out.println("<Term> ::= <Factor> <TermTail> ");
                if(multOpP.ref==null){
                    result.ref = leftFactorP.ref;
                }
                else{
                    result.ref = multOpP.ref;
                    result.ref.adoptChildren(leftFactorP.ref);
                    result.ref.adoptChildren(termP.ref);
                }
                return true;
            }
            else return false;
        }
        else return false;
    }

    private boolean TermTail(){
        //*multOpP and *termP are null. Both remain null, or *multOpP->multOp and *termP->factor/multOp(since recursive)
        if(debug){
            System.out.println("DEBUG: Parsing <TermTail> at line "+lookahead.getLocation());
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> multOpP = semStack.pop();
        Pointer<Node> termP = semStack.pop();
        if(!skipErrors(first(Symbol.TermTail), follow(Symbol.TermTail))){return false;}
        if(first(Symbol.MultOp).contains(lookahead.getType())){
            Pointer<Node> factorP = new Pointer<Node>();
            Pointer<Node> nestedMultOpP = new Pointer<Node>();
            Pointer<Node> nestedTermP = new Pointer<Node>();
            semStack.push(nestedTermP);
            semStack.push(nestedMultOpP);
            semStack.push(factorP);
            semStack.push(multOpP);

            derivTemp.add(derivIndex, "<MultOp>"); derivTemp.add(derivIndex+1, "<Factor>"); derivTemp.add(derivIndex+2, "<TermTail>");
            derivations.add(String.join(" ", derivTemp));
            if(MultOp() & Factor() & TermTail()){
                if(debug) System.out.println("<TermTail> ::= <MultOp> <Factor> <TermTail> ");
                if(nestedMultOpP.ref==null){
                    termP.ref = factorP.ref;
                }
                else{
                    termP.ref = nestedMultOpP.ref;
                    termP.ref.adoptChildren(factorP.ref);
                    termP.ref.adoptChildren(nestedTermP.ref);
                }
                return true;
            }
            else return false;
        }
        else if(follow(Symbol.TermTail).contains(lookahead.getType())){
            if(debug) System.out.println("<TermTail> ::= EPSILON ");
            derivations.add(String.join(" ", derivTemp));
            return true;
        }
        else return false;
    }

    private boolean Type(){
        //*result is null, becomes type
        if(debug){
            System.out.println("DEBUG: Parsing <Type> at line "+lookahead.getLocation());
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> result = semStack.pop();
        Pointer<Token> typeToken = new Pointer<Token>();
        if(!skipErrors(first(Symbol.Type), follow(Symbol.Type))){return false;}
        if(lookahead.getType().equals(TokenType.IntegerWord)){

            derivTemp.add(derivIndex, "'integer'"); 
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.IntegerWord, typeToken)){
                if(debug) System.out.println("<Type> ::= 'integer' ");
                result.ref = ASTnodeFactory.makeNode(NodeType.type, typeToken.ref);
                return true;
            }
            else return false;
        }
        else if(lookahead.getType().equals(TokenType.FloatWord)){

            derivTemp.add(derivIndex, "'float'");
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.FloatWord, typeToken)){
                if(debug) System.out.println("<Type> ::= 'float' ");
                result.ref = ASTnodeFactory.makeNode(NodeType.type, typeToken.ref);
                return true;
            }
            else return false;
        }
        else if(lookahead.getType().equals(TokenType.StringWord)){

            derivTemp.add(derivIndex, "'string'"); 
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.StringWord, typeToken)){
                if(debug) System.out.println("<Type> ::= 'string' ");
                result.ref = ASTnodeFactory.makeNode(NodeType.type, typeToken.ref);
                return true;
            }
            else return false;
        }
        else if(lookahead.getType().equals(TokenType.id)){

            derivTemp.add(derivIndex, "'id'"); 
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.id, typeToken)){
                if(debug) System.out.println("<Type> ::= 'id' ");
                result.ref = ASTnodeFactory.makeNode(NodeType.type, typeToken.ref);
                return true;
            }
            else return false;
        }
        else return false;
    }

    private boolean VarDecl(){
        //*result is null, becomes varDecl
        if(debug){
            System.out.println("DEBUG: Parsing <VarDecl> at line "+lookahead.getLocation());
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> result = semStack.pop();
        if(!skipErrors(first(Symbol.VarDecl), follow(Symbol.VarDecl))){return false;}
        if(first(Symbol.Type).contains(lookahead.getType())){
            Pointer<Node> typeP = new Pointer<Node>();
            Pointer<Token> idToken = new Pointer<Token>();
            Pointer<Node> dimP = new Pointer<Node>();
            semStack.push(dimP);
            semStack.push(typeP);

            derivTemp.add(derivIndex, "<Type>"); derivTemp.add(derivIndex+1, "'id'"); derivTemp.add(derivIndex+2, "<ArraySizeRept>>"); derivTemp.add(derivIndex+3, "';'"); 
            derivations.add(String.join(" ", derivTemp));
            if(Type() & match(TokenType.id, idToken) & ArraySizeRept() & match(TokenType.semicol)){
                if(debug) System.out.println("<VarDecl> ::= <Type> 'id' <ArraySizeRept> ';' ");
                Node dimList = ASTnodeFactory.makeNode(NodeType.dimList);
                if(dimP.ref!=null){
                    dimList.adoptChildren(dimP.ref);
                }
                Node id = ASTnodeFactory.makeNode(NodeType.id, idToken.ref);
                result.ref = ASTnodeFactory.makeFamily(NodeType.varDecl, typeP.ref, id, dimList);
                return true;
            }
            else return false;
        }
        else return false;
    }

    private boolean VarDeclRep(){
        //*result is null. Remains so or becomes varDecl with 0+ siblings varDecl
        if(debug){
            System.out.println("DEBUG: Parsing <VarDeclRep> at line "+lookahead.getLocation());
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> result = semStack.pop();
        if(!skipErrors(first(Symbol.VarDeclRep), follow(Symbol.VarDeclRep))){return false;}
        if(first(Symbol.VarDecl).contains(lookahead.getType())){
            Pointer<Node> varDeclSibP = new Pointer<Node>();
            semStack.push(varDeclSibP);
            semStack.push(result);

            derivTemp.add(derivIndex, "<VarDecl>"); derivTemp.add(derivIndex+1, "<VarDeclRep>"); 
            derivations.add(String.join(" ", derivTemp));
            if(VarDecl() & VarDeclRep()){
                if(debug) System.out.println("<VarDeclRep> ::= <VarDecl> <VarDeclRep> ");
                if(varDeclSibP.ref!=null){
                    result.ref.makeSiblings(varDeclSibP.ref);
                }
                return true;
            }
            else return false;
        }
        else if(follow(Symbol.VarDeclRep).contains(lookahead.getType())){
            if(debug) System.out.println("<VarDeclRep> ::= EPSILON ");
            derivations.add(String.join(" ", derivTemp));
            return true;
        }
        else return false;
    }


    private boolean Variable(){
        //*result is null, becomes var
        if(debug){
            System.out.println("DEBUG: Parsing <Variable> at line "+lookahead.getLocation());
        }
        derivTemp.remove(derivIndex);
        
        Pointer<Node> result = semStack.pop();
        result.ref = ASTnodeFactory.makeNode(NodeType.var);
        if(!skipErrors(first(Symbol.Variable), follow(Symbol.Variable))){return false;}
        if(lookahead.getType().equals(TokenType.id)){
            Pointer<Token> idToken = new Pointer<Token>();
            Pointer<Node> dataMemberP = new Pointer<Node>();
            dataMemberP.ref = ASTnodeFactory.makeNode(NodeType.dataMember);

            semStack.push(dataMemberP);

            derivTemp.add(derivIndex, "'id'"); derivTemp.add(derivIndex+1, "<VariableIdnest");
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.id, idToken) & VariableIdnest()){
                if(debug) System.out.println("<Variable> ::= 'id' <VariableIdnest> ");
                Node id = ASTnodeFactory.makeNode(NodeType.id, idToken.ref);
                dataMemberP.ref.adoptChildrenLeftmost(id);
                result.ref.adoptChildren(dataMemberP.ref);//and all siblings
                return true;
            }
            else return false;
        }
        else return false;
    }

    private boolean VariableIdnest(){
        //*result is dataMember with id child missing. Adds indexList child, potentially without any childs of its own if follow condition
        if(debug){
            System.out.println("DEBUG: Parsing <VariableIdnest> at line "+lookahead.getLocation());
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> result = semStack.pop();
        if(!skipErrors(first(Symbol.VariableIdnest), follow(Symbol.VariableIdnest))){return false;}
        if(first(Symbol.IndiceRep).contains(lookahead.getType()) || first(Symbol.VariableIdnestTail).contains(lookahead.getType())){
            Pointer<Node> exprP = new Pointer<Node>();
            Pointer<Node> nestedDataMemberP = new Pointer<Node>();
            semStack.push(nestedDataMemberP);
            semStack.push(exprP);

            derivTemp.add(derivIndex, "<IndiceRep>"); derivTemp.add(derivIndex+1, "<VariableIdnestTail"); 
            derivations.add(String.join(" ", derivTemp));
            if(IndiceRep() & VariableIdnestTail()){
                if(debug) System.out.println("<VariableIdnest> ::= <IndiceRep> <VariableIdnestTail>");
                Node indexList = ASTnodeFactory.makeNode(NodeType.indexList);
                if(exprP.ref!=null){
                    indexList.adoptChildren(exprP.ref);//and all its siblings
                }
                result.ref.adoptChildren(indexList);
                if(nestedDataMemberP.ref!=null){
                    result.ref.makeSiblings(nestedDataMemberP.ref);
                }
                return true;
            }
            else return false;
        }
        else if(follow(Symbol.VariableIdnest).contains(lookahead.getType())){
            if(debug) System.out.println("<VariableIdnest> ::= EPSILON ");
            derivations.add(String.join(" ", derivTemp));
            Node indexList = ASTnodeFactory.makeNode(NodeType.indexList);
            result.ref.adoptChildren(indexList);
            return true;
        }
        else return false;
    }

    private boolean VariableIdnestTail(){
        //*result is null, remains so if follow condition, or becomes dataMember with childs: id, indexList
        if(debug){
            System.out.println("DEBUG: Parsing <VariableIdnestTail> at line "+lookahead.getLocation());
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> result = semStack.pop();
        if(!skipErrors(first(Symbol.VariableIdnestTail), follow(Symbol.VariableIdnestTail))){return false;}
        if(lookahead.getType().equals(TokenType.dot)){
            Pointer<Token> idToken = new Pointer<Token>();
            result.ref = ASTnodeFactory.makeNode(NodeType.dataMember);

            semStack.push(result);

            derivTemp.add(derivIndex, "'.'"); derivTemp.add(derivIndex+1, "'id'"); derivTemp.add(derivIndex+2, "<VariableIdnest>"); 
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.dot) & match(TokenType.id) & VariableIdnest()){
                if(debug) System.out.println("<VariableIdnestTail> ::= '.' 'id' <VariableIdnest> ");
                Node id = ASTnodeFactory.makeNode(NodeType.id, idToken.ref);
                result.ref.adoptChildrenLeftmost(id);
                return true;
            }
            else return false;
        }
        else if(follow(Symbol.VariableIdnestTail).contains(lookahead.getType())){
            if(debug) System.out.println("<VariableIdnestTail> ::= EPSILON ");
            derivations.add(String.join(" ", derivTemp));
            return true;
        }
        else return false;
    }

    private boolean Visibility(){
        //*result is null, becomes visibility or EPSILON
        if(debug){
            System.out.println("DEBUG: Parsing <Visibility> at line "+lookahead.getLocation());
        }
        derivTemp.remove(derivIndex);
        Pointer<Node> result = semStack.pop();
        Pointer<Token> visibilityToken = new Pointer<Token>();
        if(!skipErrors(first(Symbol.Visibility), follow(Symbol.Visibility))){return false;}
        if(lookahead.getType().equals(TokenType.Public)){

            derivTemp.add(derivIndex, "'public'"); 
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.Public, visibilityToken)){
                if(debug) System.out.println("<Visibility> ::= 'public' ");
                result.ref = ASTnodeFactory.makeNode(NodeType.visibility, visibilityToken.ref);
                return true;
            }
            else return false;
        }
        else if(lookahead.getType().equals(TokenType.Private)){

            derivTemp.add(derivIndex, "'private'"); 
            derivations.add(String.join(" ", derivTemp));
            if(match(TokenType.Private, visibilityToken)){
                if(debug) System.out.println("<Visibility> ::= 'private' ");
                result.ref = ASTnodeFactory.makeNode(NodeType.visibility, visibilityToken.ref);
                return true;
            }
            else return false;
        }

        else if(follow(Symbol.Visibility).contains(lookahead.getType())){
            if(debug) System.out.println("<Visibility> ::= EPSILON ");
            derivations.add(String.join(" ", derivTemp));
            result.ref = ASTnodeFactory.makeNode(NodeType.EPSILON);
            return true;
        }
        else return false;
    }








    private EnumSet<TokenType> first(Symbol symbol){
        EnumSet<TokenType> set= null;
        switch(symbol){
            case Start:{
                set = EnumSet.of(TokenType.Main, TokenType.Class, TokenType.Func);
            } break;

            case AParams:{
                set = EnumSet.of(TokenType.intnum, TokenType.floatnum, TokenType.stringlit, 
                                    TokenType.lpar, TokenType.not, TokenType.qm,
                                    TokenType.id, TokenType.plus, TokenType.minus,
                                    TokenType.EPSILON);
            } break;

            case AParamsTail:{
                set = EnumSet.of(TokenType.comma, TokenType.EPSILON);
            } break;

            case AddOp:{
                set = EnumSet.of(TokenType.plus, TokenType.minus, TokenType.or);
            } break;

            case ArithExpr:{
                set = EnumSet.of(TokenType.intnum, TokenType.floatnum, TokenType.stringlit, 
                                    TokenType.lpar, TokenType.not, TokenType.qm,
                                    TokenType.id, TokenType.plus, TokenType.minus);
            } break;

            case ArithExprTail:{
                set = EnumSet.of(TokenType.plus, TokenType.minus, TokenType.or, 
                                    TokenType.EPSILON);
            } break;

            case ArraySizeRept:{
                set = EnumSet.of(TokenType.lsqbra, TokenType.EPSILON);
            } break;

            case AssignOp:{
                set = EnumSet.of(TokenType.assign);
            } break;

            case AssignStatTail:{
                set = EnumSet.of(TokenType.assign);
            } break;

            case ClassDecl:{
                set = EnumSet.of(TokenType.Class, TokenType.EPSILON);
            } break;

            case ClassDeclBody:{
                set = EnumSet.of(TokenType.Public, TokenType.Private, TokenType.Func,
                                    TokenType.IntegerWord, TokenType.FloatWord, TokenType.StringWord,
                                    TokenType.id, TokenType.EPSILON);
            } break;

            case ClassMethod:{
                set = EnumSet.of(TokenType.dcolon, TokenType.EPSILON);
            } break;
            
            case Expr:{
                set = EnumSet.of(TokenType.intnum, TokenType.floatnum, TokenType.stringlit, 
                                    TokenType.lpar, TokenType.not, TokenType.qm,
                                    TokenType.id, TokenType.plus, TokenType.minus);
            } break;

            case ExprTail:{
                set = EnumSet.of(TokenType.eq, TokenType.neq, TokenType.lt,
                                    TokenType.gt, TokenType.leq, TokenType.geq,
                                    TokenType.EPSILON);
            } break;
            
            case FParams:{
                set = EnumSet.of(TokenType.IntegerWord, TokenType.FloatWord, TokenType.StringWord,
                                    TokenType.id, TokenType.EPSILON);
            } break;

            case FParamsTail:{
                set = EnumSet.of(TokenType.comma, TokenType.EPSILON);
            } break;

            case Factor:{
                set = EnumSet.of(TokenType.intnum, TokenType.floatnum, TokenType.stringlit, 
                                    TokenType.lpar, TokenType.not, TokenType.qm,
                                    TokenType.id, TokenType.plus, TokenType.minus);
            } break;

            case FuncBody:{
                set = EnumSet.of(TokenType.lcurbra);
            } break;

            case FuncDecl:{
                set = EnumSet.of(TokenType.Func);
            } break;

            case FuncDeclTail:{
                set = EnumSet.of(TokenType.Void, TokenType.IntegerWord, TokenType.FloatWord,
                                    TokenType.StringWord, TokenType.id);
            } break;

            case FuncDef:{
                set = EnumSet.of(TokenType.Func, TokenType.EPSILON);
            } break;

            case FuncHead:{
                set = EnumSet.of(TokenType.Func);
            } break;

            case FuncOrAssignStat:{
                set = EnumSet.of(TokenType.id);
            } break;

            case FuncOrAssignStatIdnest:{
                set = EnumSet.of(TokenType.lpar, TokenType.lsqbra, TokenType.dot,
                                    TokenType.assign);
            } break;

            case FuncOrAssignStatIdnestFuncTail:{
                set = EnumSet.of(TokenType.dot, TokenType.EPSILON);
            } break;

            case FuncStatTail:{
                set = EnumSet.of(TokenType.dot, TokenType.lpar, TokenType.lsqbra);
            } break;

            case FuncStatTailIdnest:{
                set = EnumSet.of(TokenType.dot, TokenType.EPSILON);
            } break;

            case FuncOrAssignStatIdnestVarTail:{
                set = EnumSet.of(TokenType.dot, TokenType.assign);
            } break;

            case FuncOrVar:{
                set = EnumSet.of(TokenType.id);
            } break;

            case FuncOrVarIdnest:{
                set = EnumSet.of(TokenType.lpar, TokenType.lsqbra, TokenType.dot, TokenType.EPSILON);
            } break;

            case FuncOrVarIdnestTail:{
                set = EnumSet.of(TokenType.dot, TokenType.EPSILON);
            } break;

            case Function:{
                set = EnumSet.of(TokenType.Func);
            } break;

            case IndiceRep:{
                set = EnumSet.of(TokenType.lsqbra, TokenType.EPSILON);
            } break;

            case Inherit:{
                set = EnumSet.of(TokenType.Inherits, TokenType.EPSILON);
            } break;

            case IntNum:{
                set = EnumSet.of(TokenType.intnum, TokenType.EPSILON);
            } break;

            case MemberDecl:{
                set = EnumSet.of(TokenType.Func, TokenType.IntegerWord, TokenType.FloatWord,
                                    TokenType.StringWord, TokenType.id);
            } break;

            case MethodBodyVar:{
                set = EnumSet.of(TokenType.Var, TokenType.EPSILON);
            } break;

            case MultOp:{
                set = EnumSet.of(TokenType.mult, TokenType.div, TokenType.and);
            } break;

            case NestedId:{
                set = EnumSet.of(TokenType.comma, TokenType.EPSILON);
            } break;

            case Prog:{
                set = EnumSet.of(TokenType.Main, TokenType.Class, TokenType.Func);
            } break;

            case RelOp:{
                set = EnumSet.of(TokenType.eq, TokenType.neq, TokenType.lt,
                                    TokenType.gt, TokenType.leq, TokenType.geq);
            } break;

            case Sign:{
                set = EnumSet.of(TokenType.plus, TokenType.minus);
            } break;

            case StatBlock:{
                set = EnumSet.of(TokenType.lcurbra, TokenType.If, TokenType.While,
                                    TokenType.Read, TokenType.Write, TokenType.Return,
                                    TokenType.Break, TokenType.Continue, TokenType.id, TokenType.EPSILON);
            } break;

            case Statement:{
                set = EnumSet.of(TokenType.If, TokenType.While, TokenType.Read,
                                    TokenType.Write, TokenType.Return, TokenType.Break,
                                    TokenType.Continue, TokenType.id);
            } break;

            case StatementList:{
                set = EnumSet.of(TokenType.If, TokenType.While, TokenType.Read,
                                    TokenType.Write, TokenType.Return, TokenType.Break,
                                    TokenType.Continue, TokenType.id, TokenType.EPSILON);
            } break;

            case Term:{
                set = EnumSet.of(TokenType.intnum, TokenType.floatnum, TokenType.stringlit, 
                                    TokenType.lpar, TokenType.not, TokenType.qm,
                                    TokenType.id, TokenType.plus, TokenType.minus);
            } break;

            case TermTail:{
                set = EnumSet.of(TokenType.mult, TokenType.div, TokenType.and, TokenType.EPSILON);
            } break;

            case Type:{
                set = EnumSet.of(TokenType.IntegerWord, TokenType.FloatWord, TokenType.StringWord,
                                    TokenType.id);
            } break;

            case VarDecl:{
                set = EnumSet.of(TokenType.IntegerWord, TokenType.FloatWord, TokenType.StringWord,
                                    TokenType.id);
            } break;

            case VarDeclRep:{
                set = EnumSet.of(TokenType.IntegerWord, TokenType.FloatWord, TokenType.StringWord,
                                    TokenType.id, TokenType.EPSILON);
            } break;

            case Variable:{
                set = EnumSet.of(TokenType.id);
            } break;

            case VariableIdnest:{
                set = EnumSet.of(TokenType.lsqbra, TokenType.dot, TokenType.EPSILON);
            } break;

            case VariableIdnestTail:{
                set = EnumSet.of(TokenType.dot, TokenType.EPSILON);
            } break;

            case Visibility:{
                set = EnumSet.of(TokenType.Public, TokenType.Private, TokenType.EPSILON);
            } break;

            default: ;
        }
        return set;
    }

    private EnumSet<TokenType> follow(Symbol symbol){
        EnumSet<TokenType> set = null;
        switch(symbol){
            case Start:{
                set = EnumSet.noneOf(TokenType.class);
            } break;

            case AParams:{
                set = EnumSet.of(TokenType.rpar);
            } break;

            case AParamsTail:{
                set = EnumSet.of(TokenType.rpar);
            } break;

            case AddOp:{
                set = EnumSet.of(TokenType.intnum, TokenType.floatnum, TokenType.stringlit,
                                    TokenType.lpar, TokenType.not, TokenType.qm,
                                    TokenType.id, TokenType.plus, TokenType.minus);
            } break;

            case ArithExpr:{
                set = EnumSet.of(TokenType.semicol, TokenType.eq, TokenType.neq,
                                    TokenType.lt, TokenType.gt, TokenType.leq,
                                    TokenType.geq, TokenType.comma, TokenType.colon,
                                    TokenType.rsqbra, TokenType.rpar);
            } break;

            case ArithExprTail:{
                set = EnumSet.of(TokenType.semicol, TokenType.eq, TokenType.neq,
                                    TokenType.lt, TokenType.gt, TokenType.leq,
                                    TokenType.geq, TokenType.comma, TokenType.colon,
                                    TokenType.rsqbra, TokenType.rpar);
            } break;

            case ArraySizeRept:{
                set = EnumSet.of(TokenType.rpar, TokenType.comma, TokenType.semicol);
            } break;

            case AssignOp:{
                set = EnumSet.of(TokenType.intnum, TokenType.floatnum, TokenType.stringlit,
                                    TokenType.lpar, TokenType.not, TokenType.qm,
                                    TokenType.id, TokenType.plus, TokenType.minus);
            } break;

            case AssignStatTail:{
                set = EnumSet.of(TokenType.semicol);
            } break;

            case ClassDecl:{
                set = EnumSet.of(TokenType.Func, TokenType.Main);
            } break;

            case ClassDeclBody:{
                set = EnumSet.of(TokenType.rcurbra);
            } break;

            case ClassMethod:{
                set = EnumSet.of(TokenType.lpar);
            } break;
            
            case Expr:{
                set = EnumSet.of(TokenType.semicol, TokenType.comma, TokenType.colon,
                                    TokenType.rsqbra, TokenType.rpar);
            } break;

            case ExprTail:{
                set = EnumSet.of(TokenType.semicol, TokenType.comma, TokenType.colon,
                                    TokenType.rsqbra, TokenType.rpar);
            } break;
            
            case FParams:{
                set = EnumSet.of(TokenType.rpar);
            } break;

            case FParamsTail:{
                set = EnumSet.of(TokenType.rpar);
            } break;

            case Factor:{
                set = EnumSet.of(TokenType.mult, TokenType.div, TokenType.and,
                                    TokenType.semicol, TokenType.eq, TokenType.neq,
                                    TokenType.lt, TokenType.gt, TokenType.leq,
                                    TokenType.geq, TokenType.plus, TokenType.minus,
                                    TokenType.or, TokenType.comma, TokenType.colon,
                                    TokenType.rsqbra, TokenType.rpar);
            } break;

            case FuncBody:{
                set = EnumSet.of(TokenType.Main, TokenType.Func);
            } break;

            case FuncDecl:{
                set = EnumSet.of(TokenType.Public, TokenType.Private, TokenType.Func,
                                    TokenType.IntegerWord, TokenType.FloatWord, TokenType.StringWord,
                                    TokenType.id, TokenType.rcurbra);
            } break;

            case FuncDeclTail:{
                set = EnumSet.of(TokenType.lcurbra, TokenType.semicol);
            } break;

            case FuncDef:{
                set = EnumSet.of(TokenType.Main);
            } break;

            case FuncHead:{
                set = EnumSet.of(TokenType.lcurbra);
            } break;

            case FuncOrAssignStat:{
                set = EnumSet.of(TokenType.semicol);
            } break;

            case FuncOrAssignStatIdnest:{
                set = EnumSet.of(TokenType.semicol);
            } break;

            case FuncOrAssignStatIdnestFuncTail:{
                set = EnumSet.of(TokenType.semicol);
            } break;

            case FuncStatTail:{
                set = EnumSet.of(TokenType.semicol);
            } break;

            case FuncStatTailIdnest:{
                set = EnumSet.of(TokenType.semicol);
            } break;

            case FuncOrAssignStatIdnestVarTail:{
                set = EnumSet.of(TokenType.semicol);
            } break;

            case FuncOrVar:{
                set = EnumSet.of(TokenType.mult, TokenType.div, TokenType.and,
                                    TokenType.semicol, TokenType.eq, TokenType.neq,
                                    TokenType.lt, TokenType.gt, TokenType.leq,
                                    TokenType.geq, TokenType.plus, TokenType.minus,
                                    TokenType.or, TokenType.comma, TokenType.colon,
                                    TokenType.rsqbra, TokenType.rpar);
            } break;

            case FuncOrVarIdnest:{
                set = EnumSet.of(TokenType.mult, TokenType.div, TokenType.and,
                                    TokenType.semicol, TokenType.eq, TokenType.neq,
                                    TokenType.lt, TokenType.gt, TokenType.leq,
                                    TokenType.geq, TokenType.plus, TokenType.minus,
                                    TokenType.or, TokenType.comma, TokenType.colon,
                                    TokenType.rsqbra, TokenType.rpar);
            } break;

            case FuncOrVarIdnestTail:{
                set = EnumSet.of(TokenType.mult, TokenType.div, TokenType.and,
                                    TokenType.semicol, TokenType.eq, TokenType.neq,
                                    TokenType.lt, TokenType.gt, TokenType.leq,
                                    TokenType.geq, TokenType.plus, TokenType.minus,
                                    TokenType.or, TokenType.comma, TokenType.colon,
                                    TokenType.rsqbra, TokenType.rpar);
            } break;

            case Function:{
                set = EnumSet.of(TokenType.Main, TokenType.Func);
            } break;

            case IndiceRep:{
                set = EnumSet.of(TokenType.mult, TokenType.div, TokenType.and,
                                    TokenType.semicol, TokenType.assign, TokenType.dot,
                                    TokenType.eq, TokenType.neq, TokenType.lt,
                                    TokenType.gt, TokenType.leq, TokenType.geq,
                                    TokenType.plus, TokenType.minus, TokenType.or, 
                                    TokenType.comma, TokenType.colon,
                                    TokenType.rsqbra, TokenType.rpar);
            } break;

            case Inherit:{
                set = EnumSet.of(TokenType.lcurbra);
            } break;

            case IntNum:{
                set = EnumSet.of(TokenType.rsqbra);
            } break;

            case MemberDecl:{
                set = EnumSet.of(TokenType.Public, TokenType.Private, TokenType.Func,
                                    TokenType.IntegerWord, TokenType.FloatWord, TokenType.StringWord,
                                    TokenType.id, TokenType.rcurbra);
            } break;

            case MethodBodyVar:{
                set = EnumSet.of(TokenType.If, TokenType.While, TokenType.Read, TokenType.Write,
                                    TokenType.Return, TokenType.Break, TokenType.Continue,
                                    TokenType.id, TokenType.rcurbra);
            } break;

            case MultOp:{
                set = EnumSet.of(TokenType.intnum, TokenType.floatnum, TokenType.stringlit,
                                    TokenType.lpar, TokenType.not, TokenType.qm,
                                    TokenType.id, TokenType.plus, TokenType.minus);
            } break;

            case NestedId:{
                set = EnumSet.of(TokenType.lcurbra);
            } break;

            case Prog:{
                set = EnumSet.noneOf(TokenType.class);

            } break;

            case RelOp:{
                set = EnumSet.of(TokenType.intnum, TokenType.floatnum, TokenType.stringlit,
                                    TokenType.lpar, TokenType.not, TokenType.qm,
                                    TokenType.id, TokenType.plus, TokenType.minus);
            } break;

            case Sign:{
                set = EnumSet.of(TokenType.intnum, TokenType.floatnum, TokenType.stringlit,
                                    TokenType.lpar, TokenType.not, TokenType.qm,
                                    TokenType.id, TokenType.plus, TokenType.minus);
            } break;

            case StatBlock:{
                set = EnumSet.of(TokenType.Else, TokenType.semicol);
            } break;

            case Statement:{
                set = EnumSet.of(TokenType.If, TokenType.While, TokenType.Read,
                                    TokenType.Write, TokenType.Return, TokenType.Break,
                                    TokenType.Continue, TokenType.id, TokenType.Else,
                                    TokenType.semicol, TokenType.rcurbra);
            } break;

            case StatementList:{
                set = EnumSet.of(TokenType.rcurbra);
            } break;

            case Term:{
                set = EnumSet.of(TokenType.semicol, TokenType.eq, TokenType.neq,
                                    TokenType.lt, TokenType.gt, TokenType.leq,
                                    TokenType.geq, TokenType.plus, TokenType.minus,
                                    TokenType.or, TokenType.comma, TokenType.colon,
                                    TokenType.rsqbra, TokenType.rpar);
            } break;

            case TermTail:{
                set = EnumSet.of(TokenType.semicol, TokenType.eq, TokenType.neq,
                                    TokenType.lt, TokenType.gt, TokenType.leq,
                                    TokenType.geq, TokenType.plus, TokenType.minus,
                                    TokenType.or, TokenType.comma, TokenType.colon,
                                    TokenType.rsqbra, TokenType.rpar);
            } break;

            case Type:{
                set = EnumSet.of(TokenType.lcurbra, TokenType.semicol, TokenType.id);
            } break;

            case VarDecl:{
                set = EnumSet.of(TokenType.Public, TokenType.Private, TokenType.Func,
                                    TokenType.IntegerWord, TokenType.FloatWord, TokenType.StringWord,
                                    TokenType.id, TokenType.rcurbra);
            } break;

            case VarDeclRep:{
                set = EnumSet.of(TokenType.rcurbra);
            } break;

            case Variable:{
                set = EnumSet.of(TokenType.rpar);
            } break;

            case VariableIdnest:{
                set = EnumSet.of(TokenType.rpar);
            } break;

            case VariableIdnestTail:{
                set = EnumSet.of(TokenType.rpar);
            } break;

            case Visibility:{
                set = EnumSet.of(TokenType.Func, TokenType.IntegerWord, TokenType.FloatWord,
                                    TokenType.StringWord, TokenType.id);
            } break;

            default: ;
        }
        return set;
    }


    

    



}