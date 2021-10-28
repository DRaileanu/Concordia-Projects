package symboltable;

//import java.util.ArrayList;

public class SymTabEntry {
	//for toString(), ensure same as SymTab
	protected static String[] colWidth = {
        "20.20",//name
        "10.10",//kind
        "50.50",//type
        "10.10",//visibility
        "40.40" //link
    };
	public enum Kind{
		Class, Func, Var, Param
	}

	public String	name	= null;
	public Kind		kind	= null;
	public String   type	= null;
	public String 	visib 	= null;
	public SymTab   scope	= null;
	public String	linenum = null;
	//public int             size       = 0;
	//public int             offset     = 0;
	
	public SymTabEntry(String name, Kind kind, String type, String visib, SymTab scope, String linenum){
		this.kind = kind;
		this.type = type;
		this.name = name;
		this.visib = visib;
		this.scope = scope;
		this.linenum = linenum;
	}

	public String toString(){
        return String.format("|%-"+colWidth[0]+"s|%-"+colWidth[1]+"s|%-"+colWidth[2]+"s|%-"+colWidth[3]+"s|%-"+colWidth[4]+"s|", name, kind, type, (visib!=null)? visib:"", (scope==null)? "":"Table: "+scope.name);
	}
}
