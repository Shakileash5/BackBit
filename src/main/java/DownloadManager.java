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

    private void setFileData(String url, String mimeType){
        String extentionFromUrl = new String();

        // clean mimeType
        if(mimeType.contains(";")){
            mimeType = mimeType.substring(0, mimeType.indexOf(";"));
        }
         // Get the file name from the URL
         this.fileName = url.substring( url.lastIndexOf('/')+1, url.length() );
         if(this.fileName.lastIndexOf('.') != -1){
             extentionFromUrl = this.fileName.substring(this.fileName.lastIndexOf('.'), this.fileName.length()); // get the extention from the URL
             this.fileName = this.fileName.substring(0, this.fileName.lastIndexOf('.')); // remove the extension if any
         }
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
            String extentionFromUrl = new String();

            // Get the file name from the URL
            this.fileName = url.substring( url.lastIndexOf('/')+1, url.length() );
            if(this.fileName.lastIndexOf('.') != -1){
                extentionFromUrl = this.fileName.substring(this.fileName.lastIndexOf('.'), this.fileName.length()); // get the extention from the URL
                this.fileName = this.fileName.substring(0, this.fileName.lastIndexOf('.')); // remove the extension if any
            }

            // clean mimeType
            if(mimeType.contains(";")){
                mimeType = mimeType.substring(0, mimeType.indexOf(";"));
            }
            // set extention
            this.extention = MimeTypes.getDefaultExt(this.mimeType);
            if(extentionFromUrl.length() > 0 && (this.extention.length() == 0 || this.extention.equals("unknown"))){
                this.extention = extentionFromUrl;
            }
            System.out.println("File name: " + this.fileName + this.extention + "Mime: " + this.mimeType);
            this.fileName = this.fileName + "." + this.extention;
            
        }
        catch(IOException e){
            System.out.println("IOException: " + e.getMessage());
        }
        return ;
    }

    public void download(){
        setMetadata();
        DownloadPart obj = new DownloadPart(this.urlObj, this.fileName, "/", 0, (int)this.fileSize);
        obj.download();

        return;
    }

}
