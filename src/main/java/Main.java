package src.main.java;

import src.main.java.*;

// TODO: Implement the download progress

public class Main {

    /*
     * This function handles the input and output of the program.
     * It takes in the input from the user as command line arguments.
     * 
     * @param args the command line arguments
     * returns nothing
     */
    private void computeArguments(String[] args){
        String url = "";
        int parts = 0;
        
        // Check if the user has entered the help argument
        if(args.length == 1 && (args[0].equals("--h") || args[0].equals("--help"))){
            System.out.println("usage: [--url | --u] [--parts | --p]");
            System.out.println("--url   | --u: the url to be Downloaded");
            System.out.println("--parts | --p: the number of parts to split the download into");
            return;
        }

        for(int i = 0; i < args.length; i++){ // Loop through the arguments and assign them to the correct variables
            if(args[i].equals("--url") || args[i].equals("--u")){
                if(i + 1 < args.length){
                    url = args[i + 1];
                }
            }
            if(args[i].equals("--parts") || args[i].equals("--p")){
                if(i + 1 < args.length){
                    parts = Integer.parseInt(args[i + 1]);
                }
            }
        }

        if(url.length() != 0 ){ // if url is nnot empty, then we can start the program

            try{
                DownloadManager dm;
                if(parts != 0){ // if parts is specifeid, then we can create a DownloadManager with the specified number of parts
                    dm = new DownloadManager(url,parts); 
                }
                else{
                    dm = new DownloadManager(url);
                }
                
                dm.download();
            }
            catch(Exception e){
                System.out.println("Download Failed \n\n Error: " + e.getMessage());
            }
            
        }
        else{
           System.out.println("The arguments are improper please use [--h | --help]"); 
        }


    }

    public static void main(String[] args) {

        Main obj = new Main();
        obj.computeArguments(args);
        
    }

}
