package main;

import java.util.ArrayList;
import java.util.Comparator;

//singleton container to hold possible outputs that need to be written to file later.
public class OutputHolder {
    private static OutputHolder instance = null;

    private final ArrayList<KeyString> outsemanticerrors = new ArrayList<KeyString>();
    private final ArrayList<KeyString> outsemanticwarnings = new ArrayList<KeyString>();;

    //constructor
    private OutputHolder(){}
    public static OutputHolder getInstance(){
        if(instance==null){
            instance = new OutputHolder();
        }
        return instance;
    }


    public void clear(){
        outsemanticerrors.clear();
        outsemanticwarnings.clear();
    }


    public void addSemanticError(int line, String msg){
        outsemanticerrors.add(new KeyString(line, msg));
    }
    public void addSemanticWarning(int line, String msg){
        outsemanticwarnings.add(new KeyString(line, msg));
    }

    public ArrayList<String> getOutSemanticErrors(){
        outsemanticerrors.sort(Comparator.comparing(KeyString::getKey));
        ArrayList<String> errorList = new ArrayList<String>();
        for(KeyString el : outsemanticerrors){
            errorList.add(el.value);
        }
        return errorList;
    }
    public ArrayList<String> getOutSemanticWarnings(){
        outsemanticwarnings.sort(Comparator.comparing(KeyString::getKey));
        ArrayList<String> warningList = new ArrayList<String>();
        for(KeyString el : outsemanticwarnings){
            warningList.add(el.value);
        }
        return warningList;
    }
    
}
