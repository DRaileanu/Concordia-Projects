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

}
