package src.main.java;

import src.main.java.*;

public class Main {

    public static void main(String[] args) {
        // get string input from user
        System.out.println("Enter a Url: ");
        String url = System.console().readLine();
        // create a new instance of the download manager
        DownloadManager dm = new DownloadManager(url);
        dm.download();
        
    }

}
