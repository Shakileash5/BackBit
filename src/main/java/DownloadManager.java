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

    /*
     * This method is responsible for joining each part of the file after downloading.
     * 
     * @param: obj - the object of part of file to be joined.
     * @return: none
     */
    private void joinParts(DownloadPart obj){
        try{
            
            //FileInputStream fis = new FileInputStream(obj.getFileName());
            //BufferedInputStream bis = new BufferedInputStream(fis);

            FileOutputStream fos = new FileOutputStream("download.mp4", true);//this.fileData.getFileName()
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

    /*
     * This method is responsible for dividing the file into equal parts and download each part of the file and join them.
     * 
     * @param: none
     * @return: none
     */
    public void download(){

        this.fileData.setMetadata();
        this.parts= this.divideParts(this.fileData.getFileSize(),4); // this.DEFAULT_PART
        System.out.println("The file Size: "+ this.fileData.getFileSize());
        System.out.println(this.parts.toString());
        System.out.println("Working Directory = " + System.getProperty("user.dir"));

        List<DownloadPart> downloadParts = new ArrayList<>(); // list of download parts
        String fileName = this.fileData.getFileName();
        URL url = this.fileData.getUrlObj();
        this.fileData.setDownloadStatus(DownloadStatus.DOWNLOADING);

        for(int i=0; i<this.parts.size(); i++){ // for each part start a new thread to download
            ArrayList<Integer> part = this.parts.get(i);
            
            // create name for each part
            System.out.println("Part: "+i+" Start: "+part.get(0)+" End: "+part.get(1));
            String partFileName = fileName.substring(0, fileName.lastIndexOf('.')) + "_" + i + fileName.substring(fileName.lastIndexOf('.'), fileName.length());
            System.out.println("Part File Name: "+partFileName);
            
            // start a new thread to download each part
            DownloadPart downloadPart = new DownloadPart(this.fileData,partFileName,part.get(0),part.get(1));
            downloadPart.start();
            downloadParts.add(downloadPart);
                
        }
        
        for(DownloadPart downloadPart: downloadParts){ // wait for all the threads to finish
            try{
                downloadPart.join();
                System.out.println("-------- completed ----------" + downloadPart.getFileName());
                this.joinParts(downloadPart);
            }
            catch(InterruptedException e){
                System.out.println("InterruptedException: " + e.getMessage());
            }
        }

        boolean flag = fileData.saveFile();
        if(flag){ // if file is downloaded successfully delete the parts
            for(DownloadPart downloadPart: downloadParts){
                downloadPart.deleteFile();
            }
        }
        

        return;
    }

}
