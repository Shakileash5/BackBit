package src.main.java;

import java.io.*;
import java.net.*;
import src.main.java.*;

/*
 * The DownloadManager class is responsible for managing the download of a file from URL.
 * It is responsible for downloading the file, saving it to the local disk.
 * 
 * @author: Shakileash
 * @version: 1.0
 */
public class DownloadManager {
    
    private String url;
    private String fileName;
    private String mimeType;
    private long fileSize;
    private long downloadedSize;
    private URL urlObj;
    private String extention;
    
    public DownloadManager(String url){
        this.url = url;
        this.fileName = "";
        this.mimeType = "";
        this.fileSize = 0;
        this.downloadedSize = 0;
        
    }

    /*
     * This method is responsible for handling the metadata of the file.
     * 
     * @param: None
     * @return: None
     */
    private void setMetadata(){
        try{
            this.urlObj = new URL(url);
            URLConnection conn = urlObj.openConnection();
            this.mimeType = conn.getContentType();
            this.fileSize = conn.getContentLength();
            // Get the file name from the URL
            this.fileName = url.substring( url.lastIndexOf('/')+1, url.length() );
            this.fileName = this.fileName.substring(0, this.fileName.lastIndexOf('.')); // remove the extension if any

            // set extention
            this.extention = MimeTypes.getDefaultExt(this.mimeType);
            this.fileName = this.fileName + "." + this.extention;
        }
        catch(IOException e){
            System.out.println("IOException: " + e.getMessage());
        }
        return ;
    }

    

}
