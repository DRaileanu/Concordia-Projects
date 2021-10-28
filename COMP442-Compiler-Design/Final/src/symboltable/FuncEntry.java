package symboltable;

import java.util.ArrayList;

public class FuncEntry extends SymTabEntry {
	
	public ArrayList<VarEntry> params   = new ArrayList<VarEntry>();
	
	public FuncEntry(String name, String type, ArrayList<VarEntry> params, String visib, SymTab scope, String linenum){
		super(name, Kind.Func, type, visib, scope, linenum);
		this.params = params;
	}

	public String stringRepr(){
		ArrayList<String> paramsList = new ArrayList<String>();
		for(final VarEntry param : params){
			paramsList.add(String.format("%s%s", param.type, "[]".repeat(param.dims.size())));
		}
		return String.format("%s(%s)", name, String.join(",", paramsList));
	}

	public String toString(){
        return String.format("|%-"+colWidth[0]+"s|%-"+colWidth[1]+"s|%-"+colWidth[2]+"s|%-"+colWidth[3]+"s|%-"+colWidth[4]+"s|", name, kind, stringRepr()+":"+type, (visib!=null)? visib:"", (scope==null)? "":"Table: "+scope.name);
	}

}
