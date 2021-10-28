package main;

import java.util.ArrayList;
import java.util.Comparator;

//singleton container to hold possible outputs that need to be written to file later.
public class OutputHolder {
    private static OutputHolder instance = null;

    private final ArrayList<KeyString> outlextokens = new ArrayList<KeyString>();
    private final ArrayList<KeyString> outlexerrors = new ArrayList<KeyString>();

    private final ArrayList<KeyString> outsyntaxerrors = new ArrayList<KeyString>();

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
        outlextokens.clear();
        outlexerrors.clear();
        outsyntaxerrors.clear();
        outsemanticerrors.clear();
        outsemanticwarnings.clear();
    }


    public void addLexToken(int line, String msg){
        outlextokens.add(new KeyString(line, msg));
    }
    public void addLexError(int line, String msg){
        outlexerrors.add(new KeyString(line, msg));
    }

    public void addSyntaxError(int line, String msg){
        outsyntaxerrors.add(new KeyString(line, msg));
    }

    public void addSemanticError(int line, String msg){
        outsemanticerrors.add(new KeyString(line, msg));
    }
    public void addSemanticWarning(int line, String msg){
        outsemanticwarnings.add(new KeyString(line, msg));
    }


    public ArrayList<String> getOutLexTokens(){
        return keyStringsToStrings(outlextokens);
    }
    public ArrayList<String> getOutLexErrors(){
        return keyStringsToStrings(outlexerrors);
    }
    public ArrayList<String> getOutSyntaxErrors(){
        return keyStringsToStrings(outsyntaxerrors);
    }
    public ArrayList<String> getOutSemanticErrors(){
        return keyStringsToStrings(outsemanticerrors);
    }
    public ArrayList<String> getOutSemanticWarnings(){
        return keyStringsToStrings(outsemanticwarnings);
    }

    //combines all errors/warnings by appending type of error to message, sorts and returns ArrayList of messages in right order
    public ArrayList<String> getAllOutput(){
        ArrayList<KeyString> outAllKeyStrings = new ArrayList<KeyString>();
        //append type of error to message in new ArrayList of KeyStrings
        for(KeyString el : outlexerrors){
            outAllKeyStrings.add(new KeyString(el.key, "Lexical Error, "+el.value));
        }
        for(KeyString el : outsyntaxerrors){
            outAllKeyStrings.add(new KeyString(el.key, "Syntax Error, "+el.value));
        }
        for(KeyString el : outsemanticerrors){
            outAllKeyStrings.add(new KeyString(el.key, "Semantic Error, "+el.value));
        }
        for(KeyString el : outsemanticwarnings){
            outAllKeyStrings.add(new KeyString(el.key, "Semantic Warning, "+el.value));
        }

        //return sorted messages
        return keyStringsToStrings(outAllKeyStrings);
    }


    private ArrayList<String> keyStringsToStrings(ArrayList<KeyString> keyStringList){
        keyStringList.sort(Comparator.comparing(KeyString::getKey));
        ArrayList<String> returnList = new ArrayList<String>();
        for(KeyString el : keyStringList){
            returnList.add("Line "+el.key+": "+el.value);
        }
        return returnList;
    }
}
