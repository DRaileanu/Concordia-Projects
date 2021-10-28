import java.io.FileNotFoundException;
import java.io.PrintStream;
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
        

    	 /*******************************************************************************************************************************************
    	  * TODO : implement all the operations of main class   																					*
    	  ******************************************************************************************************************************************/
        
        Network objNetwork = new Network("network");            /* Activate the network */
        Thread t1 = new Thread(objNetwork);
        t1.start();
        Server objServer = new Server();                        /* Start the server */ 
        Thread t2 = new Thread(objServer);
        t2.start();
        Client objClient1 = new Client("sending");              /* Start the sending client */
        Thread t3 = new Thread(objClient1);
        t3.start();
        Client objClient2 = new Client("receiving");            /* Start the receiving client */
        Thread t4 = new Thread(objClient2);
        t4.start();
    }
}
