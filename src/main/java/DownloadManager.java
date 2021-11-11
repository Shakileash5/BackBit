package src.main.java;

import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;
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
    private int DEFAULT_PART = 8;
    private List<ArrayList> parts;
    
    public DownloadManager(String url){
        this.url = url;
        this.fileName = "";
        this.mimeType = "";
        this.fileSize = 0;
        this.downloadedSize = 0;
        
    }

    /*
     * This method is responsible for creating short hash the URL.
     * the short hash is used to create a unique file name, if file name is not present in the url.
     * 
     * @param: url - the URL of the file to be downloaded.
     * @return: hash - the short hash of the URL.
     */
    private String getHash(String url){
        char[] characters=url.toCharArray();
        Arrays.sort(characters);
        return Integer.toString(new String(characters).hashCode());
    }

    /*
     * This method is responsible for finding and creating extension of the file
     * and also creating the file name.
     * 
     * @param: url - the URL of the file to be downloaded.
     * @param: mimeType - the mime type of the file.
     * @return: None
     */
    private void setFileData(String url, String mimeType){
        String extentionFromUrl = new String();

        // Get the file name from the URL
        this.fileName = url.substring( url.lastIndexOf('/')+1, url.length() );
        if(this.fileName.lastIndexOf('.') != -1){
            extentionFromUrl = this.fileName.substring(this.fileName.lastIndexOf('.'), this.fileName.length()); // get the extention from the URL
            this.fileName = this.fileName.substring(0, this.fileName.lastIndexOf('.')); // remove the extension if any
        }
        
        if(MimeTypes.lookupMimeType(extentionFromUrl) == null){
            extentionFromUrl = "";
        }
        System.out.println("extention from url: "+extentionFromUrl);
        if(extentionFromUrl.length()<0 || extentionFromUrl.equals("")){
            System.out.println("No extention found");
            this.fileName = "download_" + this.getHash(url); // if no extention found, use the hash of the url as the file name
        }

        // clean mimeType
        if(mimeType.contains(";")){
            this.mimeType = mimeType.substring(0, mimeType.indexOf(";"));
        }
        System.out.println("mimeType: "+mimeType+"mime: ");
        // set extention
        this.extention = MimeTypes.getDefaultExt(this.mimeType);
        if(extentionFromUrl.length() > 0 && (this.extention.length() == 0 || this.extention.equals("unknown"))){
            this.extention = extentionFromUrl;
        }
        System.out.println("File name: " + this.fileName + "." + this.extention + " Mime: " + this.mimeType);
        this.fileName = this.fileName + "." + this.extention;
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
            
            this.setFileData(url, mimeType);
        }
        catch(IOException e){
            System.out.println("IOException: " + e.getMessage());
        }
        return ;
    }


    private List divideParts(long fileSize, int parts){
        List<ArrayList<Long>> partList = new ArrayList<>();
        ArrayList<Long>part;
        long partSize = fileSize/parts;
        long remainingBytes = fileSize%parts;
        long startByte = 0;
        long endByte = partSize;

        for(int i=0; i<parts; i++){
            part = new ArrayList<>();
            part.add(startByte);
            part.add(endByte);

            partList.add(part);
            startByte = endByte + 1;
            endByte += partSize;

            if(i == parts-2){
                endByte += remainingBytes;
            }
            
        }
        return partList;
    }

    public void download(){
        setMetadata();
        this.parts= this.divideParts(fileSize, this.DEFAULT_PART);
        System.out.println("The file Size: "+ this.fileSize);
        System.out.println(this.parts.toString());
        //DownloadPart obj = new DownloadPart(this.urlObj, this.fileName, "/", 0, (int)this.fileSize);
        //obj.download();

        return;
    }

}
