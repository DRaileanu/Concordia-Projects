/*
    COMP442 - Compiler Design
    Assignment #1
    Author: Dan Raileanu
*/


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

        //tokenize every .src file
        for(File file : testFiles){
            String fileName = file.getName();
            String fileExtension = fileName.substring(fileName.lastIndexOf('.'));
            if(!fileExtension.equals(".src")){continue;}//only tokenize .src files
            fileName = fileName.substring(0, fileName.lastIndexOf('.'));
            //create output files
            FileWriter errorsFw = new FileWriter(outDir+fileName+".outlexerrors");
            FileWriter tokensFw = new FileWriter(outDir+fileName+".outlextokens");

            //create lexer
            PushbackReader reader = new PushbackReader(new FileReader(file));
            Lexer lex = new Lexer(reader);

            //tokenize file by writing tokens in .outlextokens file. If error, also to .outlexerrors
            Token token = null;
            int line = 1;
            while((token=lex.nextToken()) != null){
                int tokenLoc = token.getLocation();
                while(tokenLoc>line){//makes tokens appear on same line in .outlextokens as they were in .src
                    line++;
                    tokensFw.write('\n');
                }
                tokensFw.write(token.toString());
                if(token.isError()){
                    errorsFw.write(token.getErrorMessage()+'\n');
                }
            }
            //close streams
            errorsFw.close();
            tokensFw.close();
            reader.close();
        }
        

    }
}
