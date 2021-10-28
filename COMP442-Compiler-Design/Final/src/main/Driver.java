package main;
import lexer.Lexer;
import visitors.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PushbackReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;



public class Driver {
    public static void main(String[] args) throws Exception {
        
        Scanner scanner = new Scanner(System.in);
        //setup input files and output path
        String testPath = "tests/";
        String outPath = "out/";
        File testDir = new File(testPath);
        if(!testDir.isDirectory()){
            System.out.println(testPath+" directory not found");
            System.exit(1);
        }
        
        //offer to compile only a single file, otherwise process all in test folder
        File[] testFiles;
        System.out.println("Enter filename to compile or hit enter to compile all files in "+testPath);
        String filenameInput = scanner.nextLine();
        if(filenameInput.equals("")){
            testFiles = testDir.listFiles();
        }
        else{
            File file = new File(testPath+filenameInput);
            testFiles = new File[]{file};
            if(!file.exists()){
                System.out.println(filenameInput+" does not exist in "+testPath);
                System.exit(-1);
            }
        }

        //create out folder and delete everything inside if it exists already
        Files.createDirectories(Paths.get(outPath));
        Files.walk(Paths.get(outPath))
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach(File::delete);

        //lexer, parser and visitors will add everything inside the outputHolder
        OutputHolder outputHolder = OutputHolder.getInstance();


        for(File file : testFiles){
            outputHolder.clear();//clear previous file output data
            //get filename and ensure .src
            String filename = file.getName();
            String fileExtension = filename.substring(filename.lastIndexOf('.'));
            if(!fileExtension.equals(".src")){
                System.out.println(filename+" does not have a .src extension, can't compile");
                continue;
            }
            System.out.println("Attempting to compile "+filename);
            filename = filename.substring(0, filename.lastIndexOf('.'));

            //create output file writers
            Files.createDirectories(Paths.get(outPath+filename));//individual folder for each test file
            //lex
            FileWriter outtokensFw = new FileWriter(outPath+filename+"/"+filename+".outlextokens");
            FileWriter outlexerrorsFw = new FileWriter(outPath+filename+"/"+filename+".outlexerrors");
            //syntax analsysis
            FileWriter outastFW = new FileWriter(outPath+filename+"/"+filename+".outast");
            FileWriter outderivationFW = new FileWriter(outPath+filename+"/"+filename+".outderivation");
            FileWriter outsyntaxerrorsFW = new FileWriter(outPath+filename+"/"+filename+".outsyntaxerrors");
            //semantic analysis
            FileWriter outsymboltablesFW = new FileWriter(outPath+filename+"/"+filename+".outsymboltables");
            FileWriter outsemanticerrorsFW = new FileWriter(outPath+filename+"/"+filename+".outsemanticerrors");
            //combined output
            FileWriter outmessagesFW = new FileWriter(outPath+filename+"/"+filename+".outmessages");

            //create lexer and parser
            PushbackReader reader = new PushbackReader(new FileReader(file));
            Lexer lex = new Lexer(reader);
            Parser parser = new Parser(lex);
            //parser.debug=true; //writes to console what grammar rule its parsing and what derivation it matched

            //parse file, but do semantic actions only if succesful
            if(parser.parse()){
                SymTabCreatorVisitor symtabCreationVisitor = new SymTabCreatorVisitor();
                parser.ast.accept(symtabCreationVisitor);       
            }


            //write to output files
            //tokens
            ArrayList<String> outtokens = outputHolder.getOutLexTokens();
            for(String el : outtokens){
                outtokensFw.write(el+"\n");
            }
            //lex errors
            ArrayList<String> outlexerrors = outputHolder.getOutLexErrors();
            for(String el : outlexerrors){
                outlexerrorsFw.write(el+"\n");
            }
            //ast
            if(parser.ast!=null){//only output ast if parsing was succesful
                ArrayList<String> outast = new ArrayList<String>();
                parser.ast.appendOutAST(outast);
                outastFW.write("digraph name{\n");
                for(String el : outast){
                    outastFW.write(el+"\n");
                }
                outastFW.write("}");
            }
            //derivation
            for(String el : parser.derivations){
                outderivationFW.write(el+"\n");
            }
            //syntax errors
            ArrayList<String> outsyntaxerrors = outputHolder.getOutSyntaxErrors();
            for(String el : outsyntaxerrors){
                outsyntaxerrorsFW.write(el+"\n");
            }
            //SymbolTasbles
            ArrayList<String> outsymboltables = new ArrayList<String>();
            if(parser.ast!=null){
                parser.ast.symtab.appendOutSymbolTables(outsymboltables);
            }
            for(String el : outsymboltables){
                outsymboltablesFW.write(el+"\n");
            }
            //semanticerrors and semanticwarnings
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
            //all messages combined
            ArrayList<String> allmessages = outputHolder.getAllOutput();
            for(String msg: allmessages){
                outmessagesFW.write(msg+"\n");
            }

            scanner.close();
            outtokensFw.close();
            outlexerrorsFw.close();
            outastFW.close();
            outderivationFW.close();
            outsyntaxerrorsFW.close();
            outsymboltablesFW.close();
            outsemanticerrorsFW.close();
            outmessagesFW.close();
        }

    }

}
