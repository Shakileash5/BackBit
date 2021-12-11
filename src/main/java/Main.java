package src.main.java;

import src.main.java.*;

public class Main {

    private void computeArguments(String[] args){
        String url = "";
        int parts = 0;

        if(args.length == 1 && (args[0].equals("--h") || args[0].equals("--help"))){
            System.out.println("usage: [--url | --u] [--parts | --p]");
        }
        if(args.length == 2){
            if(args[0].equals("--u") || args[0].equals("--url")){
                url = args[1];
            }
            else if(args[0].equals("--p") || args[0].equals("--parts")){
                parts = Integer.parseInt(args[1]);
            }
        }

        if(args.length == 4){
            if(args[0].equals("--u") || args[0].equals("--url")){
                url = args[1];
                if(args[2].equals("--p") || args[2].equals("--parts")){
                    parts = Integer.parseInt(args[3]);
                    
                }
            }
            else if(args[0].equals("--p") || args[0].equals("--parts")){
                parts = Integer.parseInt(args[1]);
                if(args[2].equals("--u") || args[2].equals("--url")){
                    url = args[3];
                }
            }
        }   

        System.out.println(url+parts);
        if(url.length() != 0 ){
            DownloadManager dm;
            if(parts != 0){
                dm = new DownloadManager(url,parts);
            }
            else{
                dm = new DownloadManager(url);
            }
            dm.download();
        }
        else{
           System.out.println("The arguments are improper please use [--h | --help]"); 
        }


    }

    public static void main(String[] args) {
        // get string input from user
        //System.out.println("Enter a Url: ");
        //String url = System.console().readLine();
        // create a new instance of the download manager
        //DownloadManager dm = new DownloadManager(url);
        //dm.download();

        Main obj = new Main();
        obj.computeArguments(args);
        
    }

}
