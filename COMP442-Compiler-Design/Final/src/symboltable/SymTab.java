package symboltable;
import symboltable.SymTabEntry.Kind;

import java.util.ArrayList;
import java.util.Iterator;

public class SymTab {
	//for toString(), ensure same is used in SymTabEntry
    private static String[] colWidth = {
        "20.20",//name
        "10.10",//kind
        "50.50",//type
        "10.10",//visibility
        "40.40" //link
    };

	public String                 name       = null;
	public ArrayList<SymTabEntry> entries    = new ArrayList<SymTabEntry>(); 
	public SymTab                 uppertable = null;

	//public SymTab(){}
	public SymTab(String name){
		this.name = name;
	}
	public SymTab(String name, SymTab uppertable){
		this.name = name;
		this.uppertable = uppertable;
	}
		
	public void addEntry(SymTabEntry entry){
		entries.add(entry);	
	}
	

	//searches for SymTabEntry on this table only
	public SymTabEntry searchlocal(String entryname, Kind kind){
		for(final SymTabEntry entry : entries) {
			if(entry.name.equals(entryname) && entry.kind==kind) {
				return entry;
			}
		}
		return null;
	}

	//searches for SymTabEntry on this table and in class entries' scope
	public SymTabEntry search(String entryname, Kind kind){
		SymTabEntry returnvalue = searchlocal(entryname, kind);
		if(returnvalue!=null){return returnvalue;}
		//if didn't find entry locally:
		switch(kind){
			case Class:{//search is local to table this table only
				
			}
			break;
			case Func:{
				if(!this.name.equals("global")){//looking for func in global is only to find free funcs
					for(final SymTabEntry entry : entries) {
						if(entry.kind==Kind.Class) {
							returnvalue = entry.scope.search(entryname, kind);
							if(returnvalue!=null){
								return returnvalue;
							}
						}
					}
				}
			}
			break;
			case Param:{

			}
			break;
			case Var:{//look in inherited classes if in Class scope now or in all possible classes if from global) //TODO any reason to search from global?
				for(final SymTabEntry entry : entries) {
					if(entry.kind==Kind.Class) {
						returnvalue = entry.scope.search(entryname, kind);
						if(returnvalue!=null){
							return returnvalue;
						}
					}
				}
			}
			break;
			default:{
				
			}
		}

		return returnvalue;
	}


	//searches locally for function entry with matching name/params. Does not look in inherited class members
	public FuncEntry searchFunclocal(String name, ArrayList<VarEntry> params){
		for(SymTabEntry entry : entries){
			if(entry.kind!=Kind.Func || !entry.name.equals(name)){continue;}
			FuncEntry funcentry = (FuncEntry)entry;
			//ensure same param types, don't care param names, only type and NUMBER of dimensions. 
			//don't care about return type, has to be checked later
			//Don't care about individual dim size, have to check manualy later. Because can be defined as [] and called with [x]
			Iterator<VarEntry> paramIt = params.iterator();
			Iterator<VarEntry> entryparamIt = funcentry.params.iterator();
			boolean paramMismatch = false;
			while(paramIt.hasNext() && entryparamIt.hasNext()){
				VarEntry paramvar = paramIt.next();
				VarEntry entryparamvar = entryparamIt.next();
				if(!paramvar.type.equals(entryparamvar.type) || paramvar.dims.size()!=entryparamvar.dims.size()){
					paramMismatch=true;
					break;//param type or number of dims mismatch, can stop checking
				}
			}
			//if both had same length of params and we didn't see a param mismatch, we found the correct function
			if(!paramIt.hasNext() && !entryparamIt.hasNext() && !paramMismatch){
				return funcentry;
			}
		}
		return null;
	}


	//searches for function entry with matching name/params. If called on Class symboltable, will also look in inherited classes
	public FuncEntry searchFunc(String name, ArrayList<VarEntry> params){
		FuncEntry returnvalue = searchFunclocal(name, params);
		if(returnvalue!=null){return returnvalue;}
		//if not found in local table, look in inherited class if not in global table
		if(!this.name.equals("global")){//looking for funcs in global is only to find free funcs
			for(final SymTabEntry entry : entries){
				if(entry.kind==Kind.Class){
					FuncEntry funcentry = entry.scope.searchFunc(name, params);
					if(funcentry!=null){
						return funcentry;
					}
				}
			}
		}
		return null;
	}



	public String toString(){
        ArrayList<String> tableStrings = new ArrayList<String>();
        tableStrings.add("=".repeat(136));
        tableStrings.add(String.format("|Table: %s", name));
        tableStrings.add(String.format("|%-"+colWidth[0]+"s|%-"+colWidth[1]+"s|%-"+colWidth[2]+"s|%-"+colWidth[3]+"s|%-"+colWidth[4]+"s|", "name", "kind", "type", "visibility", "link"));
        tableStrings.add("-".repeat(136));
        for(SymTabEntry entry : entries){
            tableStrings.add(entry.toString());
        }
        tableStrings.add("=".repeat(136));
        return String.join("\n", tableStrings);
    }

    //used to generato recursively append Strings of its entries
    public void appendOutSymbolTables(ArrayList<String> outsymboltables){
        outsymboltables.add(this.toString());
        for(SymTabEntry entry : entries){
            if(entry.scope!=null){
                if(entry.kind==Kind.Class && !name.equals("global")){continue;}//append classes only from global scope(as opposed to from other classes)
                entry.scope.appendOutSymbolTables(outsymboltables);
            }
        }
    }	

}
