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
    private int fileSize;
    private long downloadedSize;
    private URL urlObj;
    private String extention;
    private FileData fileData;
    private int DEFAULT_PART = 8;
    private List<ArrayList> parts;
    
    public DownloadManager(String url){
        this.fileData = new FileData(url);
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
            System.out.println("the file size :: "+ conn.getHeaderField("content-length"));
            System.out.println("the file size :: " + this.fileSize);
            
            this.setFileData(url, mimeType);
        }
        catch(IOException e){
            System.out.println("IOException: " + e.getMessage());
        }
        return ;
    }

    /*
     * This method is responsible for dividing the file into equal parts
     * and set ranges for each parts to be downloaded.
     * 
     * @param: fileSize - the size of the file to be downloaded.
     * @param: parts - the number of parts to be downloaded.
     * 
     * @return: partList - the list of ranges to be downloaded.
     */
    private List divideParts(int fileSize, int parts){

        List<ArrayList<Integer>> partList = new ArrayList<>(); // list of ranges to be downloaded
        ArrayList<Integer>part;
        int partSize = fileSize/parts; // size of each part
        int remainingBytes = fileSize%parts; // remaining bytes after dividing the file into parts
        int startByte = 0;
        int endByte = partSize;

        for(int i=0; i<parts; i++){
            part = new ArrayList<>();
            part.add(startByte);
            part.add(endByte);

            partList.add(part);
            startByte = endByte + 1;
            endByte += partSize;

            if(i == parts-2){ // last part has remaining bytes
                System.out.println("last part has remaining bytes");
                endByte += remainingBytes;
            }
            
        }
        return partList;
    }

    private void joinParts(DownloadPart obj){
        try{
            
            //FileInputStream fis = new FileInputStream(obj.getFileName());
            //BufferedInputStream bis = new BufferedInputStream(fis);

            FileOutputStream fos = new FileOutputStream("download.jpg", true);//this.fileData.getFileName()
            BufferedOutputStream bos = new BufferedOutputStream(fos);

            /*int data;
            while((data = bis.read()) != -1){
                bos.write(data);
            }
            */
            fileData.joinBytes(obj.getData());

            bos.close();
            //bis.close();

        }
        catch(IOException e){
            System.out.println("IOException: " + e.getMessage());
        }
        
    }


    public void download(){
        this.fileData.setMetadata();
        this.parts= this.divideParts(this.fileData.getFileSize(),4); // this.DEFAULT_PART
        System.out.println("The file Size: "+ this.fileData.getFileSize());
        System.out.println(this.parts.toString());
        System.out.println("Working Directory = " + System.getProperty("user.dir"));

        List<DownloadPart> downloadParts = new ArrayList<>();
        String fileName = this.fileData.getFileName();
        URL url = this.fileData.getUrlObj();
        this.fileData.setDownloadStatus(DownloadStatus.DOWNLOADING);
        for(int i=0; i<this.parts.size(); i++){
            ArrayList<Integer> part = this.parts.get(i);
            System.out.println("Part: "+i+" Start: "+part.get(0)+" End: "+part.get(1));
            String partFileName = fileName.substring(0, fileName.lastIndexOf('.')) + "_" + i + fileName.substring(fileName.lastIndexOf('.'), fileName.length());
            System.out.println("Part File Name: "+partFileName);
            DownloadPart downloadPart = new DownloadPart(this.fileData,partFileName,part.get(0),part.get(1));
            downloadPart.start();
            downloadParts.add(downloadPart);
                
        }
        
        for(DownloadPart downloadPart: downloadParts){
            try{
                downloadPart.join();
                System.out.println("-------- completed ----------" + downloadPart.getFileName());
                this.joinParts(downloadPart);
            }
            catch(InterruptedException e){
                System.out.println("InterruptedException: " + e.getMessage());
            }
        }

        fileData.saveFile();

        return;
    }

}
