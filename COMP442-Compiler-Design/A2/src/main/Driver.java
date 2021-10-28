package main;
import java.util.EnumSet;
import java.util.Set;
import java.util.ArrayList;

import AST.ASTnode;
import lexer.Lexer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PushbackReader;
import java.nio.file.Files;
import java.nio.file.Paths;



public class Driver {
    public static void main(String[] args) throws Exception {
        

        //setup input files and output path
        String inDir = "tests/";
        String outDir = "out/";
        File testDir = new File(inDir);
        File[] testFiles = testDir.listFiles();
        Files.createDirectories(Paths.get(outDir));

        for(File file : testFiles){
            String filename = file.getName();
            System.out.println("Attempting to parse "+filename);
            String fileExtension = filename.substring(filename.lastIndexOf('.'));
            if(!fileExtension.equals(".src")){continue;}//only parse .src files
            filename = filename.substring(0, filename.lastIndexOf('.'));
            //create output files
            FileWriter errorsFW = new FileWriter(outDir+filename+".outerrors");
            FileWriter outderivationFW = new FileWriter(outDir+filename+".outderivation");
            FileWriter outastFW = new FileWriter(outDir+filename+".outast");

            //create lexer and parser
            PushbackReader reader = new PushbackReader(new FileReader(file));
            Lexer lex = new Lexer(reader);
            Parser parser = new Parser(lex);
            
            //parser.debug=true;
            //parse then output to files
            if(parser.parse()){
                ArrayList<String> outast = new ArrayList<String>();
                parser.ast.appendOutAST(outast);
                outastFW.write("digraph name{\n");
                for(String el : outast){
                    outastFW.write(el+"\n");
                }
                outastFW.write("}");
            }
            for(String msg : parser.errorMessages){
                errorsFW.write(msg+"\n");
            }
            for(String el : parser.derivations){
                outderivationFW.write(el+"\n");
            }
            
            //close streams
            errorsFW.close();
            outderivationFW.close();
            outastFW.close();   
        }


    }
}
