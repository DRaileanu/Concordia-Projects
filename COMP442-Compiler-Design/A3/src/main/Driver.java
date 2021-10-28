package main;
import lexer.Lexer;
import visitors.*;

import java.util.ArrayList;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
        OutputHolder outputHolder = OutputHolder.getInstance();

        for(File file : testFiles){
            outputHolder.clear();
            String filename = file.getName();
            System.out.println("Attempting to parse "+filename);
            String fileExtension = filename.substring(filename.lastIndexOf('.'));
            if(!fileExtension.equals(".src")){continue;}//only parse .src files
            filename = filename.substring(0, filename.lastIndexOf('.'));
            //create output files
            FileWriter outsymboltablesFW = new FileWriter(outDir+filename+".outsymboltables");
            FileWriter outsemanticerrorsFW = new FileWriter(outDir+filename+".outsemanticerrors");

            //create lexer and parser
            PushbackReader reader = new PushbackReader(new FileReader(file));
            Lexer lex = new Lexer(reader);
            Parser parser = new Parser(lex);
            
            //parser.debug=true;
            //parse then output to files
            if(parser.parse()){
                SymTabCreatorVisitor symtabCreationVisitor = new SymTabCreatorVisitor();
                TypeCheckVisitor typecheckVisitor = new TypeCheckVisitor();
                parser.ast.accept(symtabCreationVisitor);
                parser.ast.accept(typecheckVisitor);

                //write outSymbolTasbles
                ArrayList<String> outsymboltables = new ArrayList<String>();
                parser.ast.symtab.appendOutSymbolTables(outsymboltables);
                for(String el : outsymboltables){
                    outsymboltablesFW.write(el+"\n");
                }
                //write outsemanticerrors
                ArrayList<String> semanticerrors = outputHolder.getOutSemanticErrors();
                ArrayList<String> semanticwarnings = outputHolder.getOutSemanticWarnings();
                outsemanticerrorsFW.write("Semantic Errors:\n");
                for(String error : semanticerrors){
                    outsemanticerrorsFW.write(error+"\n");
                }
                outsemanticerrorsFW.write("========================================================================\n");
                outsemanticerrorsFW.write("Semantic Warnings:\n");
                for(String warning : semanticwarnings){
                    outsemanticerrorsFW.write(warning+"\n");
                }
            }
            
            outsymboltablesFW.close();
            outsemanticerrorsFW.close();
        }

    }

}
