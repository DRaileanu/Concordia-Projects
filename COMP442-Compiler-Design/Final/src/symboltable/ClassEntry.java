package symboltable;

public class ClassEntry extends SymTabEntry {

	public ClassEntry(String name, SymTab scope, String linenum){
		super(name, Kind.Class, name, null, scope, linenum);
	}
	
		
}

