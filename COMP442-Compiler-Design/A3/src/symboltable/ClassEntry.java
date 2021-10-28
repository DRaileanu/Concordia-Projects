package symboltable;

public class ClassEntry extends SymTabEntry {

	public ClassEntry(String name, SymTab scope, String linenum){
		super(name, Kind.Class, name, null, scope, linenum);
	}
	
	
	// public String toString(){
	// 	return 	String.format("%-12s" , "| " + m_kind) +
	// 			String.format("%-40s" , "| " + m_name) + 
	// 			"|" + 
	// 			m_subtable;
	// }
	
}

