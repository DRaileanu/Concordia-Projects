package symboltable;

import java.util.ArrayList;

public class VarEntry extends SymTabEntry {
		
	public ArrayList<Integer> dims = new ArrayList<Integer>();

	public VarEntry(String name, String type, ArrayList<Integer> dims, String visib, String linenum){
		super(name, Kind.Var, type, visib, null, linenum);
		this.dims = dims;
	}
		
	public String toString(){
		StringBuilder str = new StringBuilder();
		for(Integer num : dims){
			str.append(String.format("[%s]", (num==null)? "":String.valueOf(num)));
		}
        return String.format("|%-"+colWidth[0]+"s|%-"+colWidth[1]+"s|%-"+colWidth[2]+"s|%-"+colWidth[3]+"s|%-"+colWidth[4]+"s|", name, kind, type+str.toString(), (visib!=null)? visib:"", "");
	}

	// public String toString(){
	// 	return 	String.format("%-12s" , "| " + m_kind) +
	// 			String.format("%-12s" , "| " + m_name) + 
	// 			String.format("%-12s"  , "| " + m_type) + 
    //           //String.format("%-12"  , "| " + m_dims) + 
	// 			String.format("%-8s"  , "| " + m_size) + 
	// 			String.format("%-8s"  , "| " + m_offset)
	// 	        + "|";
	// }
}
