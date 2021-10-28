import java.io.PrintStream;
import java.io.FileNotFoundException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Kerly Titus
 */
public class Driver {

    /** 
     * main class
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        boolean isTestCase = true;//true if want to get output in a .txt file, false if want displayed on console
        if(isTestCase){
            //setup output stream for test case results
            PrintStream output=null;
            try{
            output = new PrintStream("output-testCase.txt");
            }
            catch(FileNotFoundException e){
                System.out.println("File output.txt couldn't be created/opened");
                System.exit(0);
            }
            System.setOut(output);//redirect output stream to the one writing to file
        }


    	Network objNetwork = new Network( );            /* Activate the network */
        objNetwork.start();

        Client objClient1 = new Client("sending");          /* Start the sending client thread */
        objClient1.start();
        Client objClient2 = new Client("receiving");        /* Start the receiving client thread */
        objClient2.start();
        
        Server objServer1 = new Server("Thread1");          /* Start the server Thread1 */ 
        objServer1.start();
        Server objServer2 = new Server("Thread2");          /* Start the server Thread2*/ 
        objServer2.start();
      
    }
    
 }
