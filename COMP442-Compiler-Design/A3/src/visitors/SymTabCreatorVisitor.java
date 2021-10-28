package visitors;
import ast.*;
import symboltable.*;
import symboltable.SymTabEntry.Kind;
import main.OutputHolder;

import java.util.ArrayList;

public class SymTabCreatorVisitor implements Visitor{
    OutputHolder out = OutputHolder.getInstance();

    public SymTabCreatorVisitor(){

    }

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
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(ClassDeclListNode node) {
        //pass down the global table so classDecl can add entries in it
        for (Node child : node.getChildren()) {
			child.symtab = node.symtab;
			child.accept(this);
        }  
    }

    @Override
    public void visit(ClassDeclNode node) {
        //setup required variables
        ArrayList<Node> childs = node.getChildren();
        SymTab globaltable = node.symtab;
        String classname = childs.get(0).data;
        String linenum = childs.get(0).linenum;
        SymTab localtable = new SymTab(classname, globaltable);
        node.symtab = localtable;//change scope to be for current class from now on
        SymTabEntry similarEntry = null;//used to search for duplicate classes and hiding members

        //ensure class not already declared
        similarEntry = globaltable.search(classname, Kind.Class);
        if(similarEntry==null){
            node.symtabentry = new ClassEntry(classname, localtable, linenum);
            globaltable.addEntry(node.symtabentry);//adds class entry to global scope
        }
        else{
            out.addSemanticError(Integer.valueOf(linenum), String.format("Line %s : Duplicate class definition. %s is defined at line %s", linenum, similarEntry.name, similarEntry.linenum));
            //we continue even if duplicate class error, since can catch some more local errors
        }

        //add inherit classes
        for(Node inherIdNode : childs.get(1).getChildren()){
            String inheridname = inherIdNode.data;
            SymTabEntry inherClassEntry = globaltable.search(inheridname, Kind.Class);
            if(inherClassEntry!=null){
                localtable.addEntry(inherClassEntry);
            }
            else{
                out.addSemanticError(Integer.valueOf(inherIdNode.linenum), String.format("Line %s : Inherited class %s is not defined", inherIdNode.linenum, inheridname));
            }
        }

        //visit membDeclListNode so it generates a temp table with all member entries
        //Duplicates are taken care of, only first declared duplicate is kept
        Node membDeclList = childs.get(2);
        membDeclList.accept(this);
        SymTab membListTable = membDeclList.symtab;

        //check for potential member hiding or overloaded functions
        for(SymTabEntry membEntry : membListTable.entries){
            if(membEntry.kind==Kind.Var){
                //TODO check in each inherited table manually to know which class the hiden var belongs to, for now just giving linenum
                similarEntry = localtable.search(membEntry.name, Kind.Var);
                if(similarEntry!=null){//var hiding
                    out.addSemanticWarning(Integer.valueOf(membEntry.linenum), String.format("Line %s : Variable %s is hiding the one declared at %s", membEntry.linenum, membEntry.name, similarEntry.linenum));
                }
            }
            if(membEntry.kind==Kind.Func){
                FuncEntry funcEntry= (FuncEntry)membEntry;
                similarEntry = localtable.searchFunc(funcEntry.name, funcEntry.params);
                if(similarEntry!=null){//overiden function
                    out.addSemanticWarning(Integer.valueOf(membEntry.linenum), String.format("Line %s : Function %s is overiding the one declared at %s", membEntry.linenum, funcEntry.stringRepr(), similarEntry.linenum));
                }
                else{//check if overloading
                    similarEntry = localtable.search(funcEntry.name, Kind.Func);
                    if(similarEntry!=null){
                        out.addSemanticWarning(Integer.valueOf(membEntry.linenum), String.format("Line %s : Function %s is an overload", membEntry.linenum, funcEntry.stringRepr()));
                    }
                }
            }
            localtable.addEntry(membEntry);
        }

        
    }

    @Override
    public void visit(ContinueStatNode node) {
        // TODO Auto-generated method stub
        
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
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(FCallParamsNode node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(FParamListNode node) {
        //NOTHING since skipping over it and directly calling accept on FParamNode
    }

    @Override
    public void visit(FParamNode node){
        //setup required variables
        ArrayList<Node> childs = node.getChildren();
        String paramtype = childs.get(0).data;
        String paramname = childs.get(1).data;
        String linenum = childs.get(1).linenum;

        //loop over dimListNode to get dimensions
        ArrayList<Integer> dimList = new ArrayList<Integer>();
        for(Node dim : childs.get(2).getChildren()){
            //TODO check if dim.data >0
            if(dim.data!=null){
                dimList.add(Integer.valueOf(dim.data));
            }
            else{//could be epislon if []
                dimList.add(null);
            }
        }
        node.symtabentry = new VarEntry(paramname, paramtype, dimList, null, linenum);
        node.symtabentry.kind=Kind.Param;
    }

    @Override
    public void visit(FuncBodyNode node) {
        SymTab functable = node.symtab;
        //visit varDeclListNode so it generates a temp table with all var entries
        //Duplicates are taken care of, only first declared duplicate is kept
        Node varDeclList = node.getChildren().get(0);
        varDeclList.accept(this);
        SymTab varDeclListTable = varDeclList.symtab;

        //add entries to functable, while checking for duplicate conflicts with function params
        for(SymTabEntry varEntry : varDeclListTable.entries){
            SymTabEntry similarEntry = functable.searchlocal(varEntry.name, Kind.Param);
            if(similarEntry!=null){
                out.addSemanticError(Integer.valueOf(varEntry.linenum), String.format("Line %s : Duplicate Variable. %s is already declared at %s", varEntry.linenum, varEntry.name, similarEntry.linenum));
            }
            else{
                functable.addEntry(varEntry);
            }
        }  
    }

    @Override
    public void visit(FuncDeclNode node) {
        //setup required variables
        ArrayList<Node> childs = node.getChildren();
        String functype = childs.get(0).data;
        String funcname = childs.get(1).data;
        String linenum = childs.get(1).linenum;

        //loop over and visit childs of FParamListNode to generates all FParam entries(are really just VarEntry with Kind.Param)
        ArrayList<VarEntry> fparamlist = new ArrayList<VarEntry>();
        for(Node fparam : childs.get(2).getChildren()){
            fparam.accept(this);
            fparamlist.add((VarEntry) fparam.symtabentry);
        }
        //create funcentry to be picked up by in membDeclNode
        node.symtabentry = new FuncEntry(funcname, functype, fparamlist, null,  null, linenum);
    }

    @Override
    public void visit(FuncDefListNode node) {
        //pass down global table so funcDefNode can add themselves in it or provide scope for Func entries in class scope
        for (Node child : node.getChildren()) {
			child.symtab = node.symtab;
			child.accept(this);
        }  
    }

    @Override
    public void visit(FuncDefNode node) {
        //setup required variables
        ArrayList<Node> childs = node.getChildren();
        SymTab globaltable = node.symtab;
        String type = childs.get(0).data;
        String scopeSpec = childs.get(1).data;
        String funcname = childs.get(2).data;
        String linenum = childs.get(2).linenum;
        
        //loop over and visit childs of FParamListNode to generates all FParam entries(are really just VarEntry with Kind.Param)
        ArrayList<VarEntry> fparamlist = new ArrayList<VarEntry>();
        for(Node fparam : childs.get(3).getChildren()){
            fparam.accept(this);
            fparamlist.add((VarEntry) fparam.symtabentry);
        }

        //create func symtab and fill with param entries. Note that name and uppertable will be set later
        SymTab functable = new SymTab(String.format("%s:%s", "temp", type));//leaving type in name, since even if invalid func, type checking could be done on it
        node.symtab = functable;
        for(VarEntry paramentry : fparamlist){
            SymTabEntry similarentry = functable.searchlocal(paramentry.name, Kind.Param);
            if(similarentry==null){
                functable.addEntry(paramentry);
            }
            else{
                out.addSemanticError(Integer.valueOf(paramentry.linenum), String.format("Line %s : Duplicate function param name %s", paramentry.linenum, paramentry.name));
            }
        }

        //link functable to its funcentry in class table, or to new funcentry in global table if free func
        if(scopeSpec!=null){//class member function
            SymTabEntry classentry = globaltable.searchlocal(scopeSpec, Kind.Class);
            if(classentry==null){//function's class scope doesn't exist
                out.addSemanticError(Integer.valueOf(linenum), String.format("Line %s : Class %s for member function isn't defined.", linenum, scopeSpec));
            }
            else{
                //check if function was declared
                FuncEntry funcentry = (FuncEntry) classentry.scope.searchFunclocal(funcname, fparamlist);
                if(funcentry==null){
                    out.addSemanticError(Integer.valueOf(linenum), String.format("Line %s : Definition provided for undeclared function in class %s", linenum, classentry.name));
                }
                else{
                    //check if return type matches the one declared
                    if(!funcentry.type.equals(type)){
                        out.addSemanticError(Integer.valueOf(linenum), String.format("Line %s : Function definition return type %s does not match %s declared at line %s", linenum, type, funcentry.type ));
                    }
                    else{
                        funcentry.scope = functable;
                        functable.name = String.format("%s::%s:%s", scopeSpec, funcentry.stringRepr(), funcentry.type);
                        functable.uppertable = classentry.scope;
                        //TODO must check for override? or funcdecl handles it? do some tests

                        // //visit funcBody to add variable declarations to symtab
                        // childs.get(4).symtab = functable;
                        // childs.get(4).accept(this);
                        // //check for class variable hiding
                        // for(SymTabEntry varentry : functable.entries){//entries can be var or param, but can only hide var members from class
                        //     SymTabEntry similarentry = classentry.scope.search(varentry.name, Kind.Var);
                        //     if(similarentry!=null){
                        //         out.addSemanticWarning(Integer.valueOf(varentry.linenum), String.format("Line %s : %s %s is hiding variable declared at line %s", varentry.linenum, varentry.kind, varentry.name, similarentry.linenum));
                        //     }
                        }
                    }
                } 
                //visit funcBody to add variable declarations to symtab
                childs.get(4).symtab = functable;
                childs.get(4).accept(this);
                //check for class variable hiding
                for(SymTabEntry varentry : functable.entries){//entries can be var or param, but can only hide var members from class
                    SymTabEntry similarentry = classentry.scope.search(varentry.name, Kind.Var);
                    if(similarentry!=null){
                        out.addSemanticWarning(Integer.valueOf(varentry.linenum), String.format("Line %s : %s %s is hiding variable declared at line %s", varentry.linenum, varentry.kind, varentry.name, similarentry.linenum));
                    }
            }
        }
        else{//free func
            //check if already defined
            FuncEntry funcentry = (FuncEntry) globaltable.searchFunclocal(funcname, fparamlist);
            if(funcentry!=null){
                out.addSemanticError(Integer.valueOf(linenum), String.format("Line %s : Duplicate function definition %s. Function already defined at %s", linenum, funcentry.stringRepr(), funcentry.linenum));
            }
            else{
                funcentry = new FuncEntry(funcname, type, fparamlist, null, functable, linenum);
                functable.name = String.format("%s:%s", funcentry.stringRepr(), funcentry.type);
                //check if overload
                if(globaltable.searchlocal(funcname, Kind.Func)!=null){
                    out.addSemanticWarning(Integer.valueOf(linenum), String.format("Line %s : Function %s is an overload", linenum, funcentry.stringRepr()));
                }
                globaltable.addEntry(funcentry);
                functable.uppertable = globaltable;

                // //visit funcBody to add variable declarations to symtab
                // childs.get(4).symtab = functable;
                // childs.get(4).accept(this);
            }   
            //visit funcBody to add variable declarations to symtab
            childs.get(4).symtab = functable;
            childs.get(4).accept(this);
        }

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
        //generate temporary SymTab from which clasDecl will pick up entries later
        SymTab localtable = new SymTab("temp");
        node.symtab = localtable;
        for (Node child : node.getChildren()) {
			child.accept(this);
            //check for duplicate entries before adding
            SymTabEntry membentry = child.symtabentry;
            if(membentry.kind==Kind.Var){
                SymTabEntry similarentry = localtable.searchlocal(membentry.name, Kind.Var);
                if(similarentry!=null){//duplicate
                    out.addSemanticError(Integer.valueOf(membentry.linenum), String.format("Line %s : Duplicate variable declaration. %s was already defined at line %s", membentry.linenum, similarentry.name, similarentry.linenum));
                }
                else{
                    localtable.addEntry(membentry);
                }
            }
            if(membentry.kind==Kind.Func){
                FuncEntry funcentry = (FuncEntry)membentry;
                FuncEntry similarEntry = localtable.searchFunclocal(membentry.name, funcentry.params);
                if(similarEntry!=null){
                    out.addSemanticError(Integer.valueOf(membentry.linenum), String.format("Line %s : Duplicate function declaration. %s was already declared at line %s", membentry.linenum, funcentry.stringRepr(), similarEntry.linenum));
                }
                else{
                    localtable.addEntry(funcentry);
                }
            }
        }
    }

    @Override
    public void visit(MembDeclNode node) {
        //setup required variables
        ArrayList<Node> childs = node.getChildren();
        String visib = childs.get(0).data;
        Node varOrFuncDecl = childs.get(1);
        //visit to generate entry, then set visib
        varOrFuncDecl.accept(this);
        varOrFuncDecl.symtabentry.visib = visib;
        node.symtabentry=varOrFuncDecl.symtabentry;
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
        //create global symbtab
        node.symtab = new SymTab("global");
        //create main program symtab/entry and add to global table
        SymTab maintable = new SymTab("main", node.symtab);
        SymTabEntry mainentry = new FuncEntry("main", "", new ArrayList<VarEntry>(), null, maintable, "0");
        node.symtab.addEntry(mainentry);
        node.getChildren().get(2).symtab = maintable;
        //pass scope to childs and visit them
        for(Node child : node.getChildren()){
            child.symtab = node.symtab;
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
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(SignNode node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(StatListNode node) {
        // TODO Auto-generated method stub
        
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
        //generate temporary SymTab from which funcBodyNode will pick up entries later
        SymTab localtable = new SymTab("temp");
        node.symtab = localtable;
        for (Node child : node.getChildren()) {
			child.accept(this);
            //check for duplicate entries before adding
            SymTabEntry varentry = child.symtabentry;
            SymTabEntry similarentry = localtable.searchlocal(varentry.name, Kind.Var);
                if(similarentry!=null){//duplicate
                    out.addSemanticError(Integer.valueOf(varentry.linenum), String.format("Line %s : Duplicate variable declaration. %s was already defined at line %s", varentry.linenum, similarentry.name, similarentry.linenum));
                }
                else{
                    localtable.addEntry(varentry);
                }
        }
    
    }

    @Override
    public void visit(VarDeclNode node) {
        //setup required variables
        ArrayList<Node> childs = node.getChildren();
        String vartype = childs.get(0).data;
        String varname = childs.get(1).data;
        String linenum = childs.get(1).linenum;

        //loop over dimListNode to get dimensions
        ArrayList<Integer> dimList = new ArrayList<Integer>();
        for(Node dim : childs.get(2).getChildren()){
            //TODO check if dim.data >0
            if(dim.data!=null){//because could be epsilon node, which is not allowed for varDecl
                dimList.add(Integer.valueOf(dim.data));
            }
            else{//varDecl with [] is not allowed, must specify dims
                out.addSemanticError(Integer.valueOf(linenum), String.format("Line %s : Declaration of array variable % must specify all its dimensions' sizes", linenum, varname));
                dimList.add(null);//to be able to do typecheck later as if it was correctly specified
            }
        }
        node.symtabentry = new VarEntry(varname, vartype, dimList, null, linenum);

        //check if duplicate. If so, don't add
        // SymTabEntry similarEntry = membListTable.searchlocal(varname, Kind.Var);
        // if(similarEntry!=null){
        //     out.addSemanticError(Integer.valueOf(linenum), String.format("Line %s : Duplicate variable declaration. %s was already defined at line %s", linenum, similarEntry.name, similarEntry.linenum));
        // }
        // else{
        //     membListTable.addEntry(node.symtabentry);
        // }
    }

    @Override
    public void visit(VarNode node) {
        // TODO Auto-generated method stub
        
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
        // TODO Auto-generated method stub
        
    }

    


}
