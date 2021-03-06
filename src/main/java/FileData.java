package src.main.java;

import java.io.*;
import java.net.*;
import java.util.*;
import src.main.java.*;

public class FileData {
    
    private String url;
    private String fileName;
    private String mimeType;
    private int fileSize;
    private int downloadedSize;
    private URL urlObj;
    private String extention;
    private byte[] buffer;
    private String filePath = "";
    private String partFileSavePath = System.getProperty("user.dir") + "\\src\\main\\resources\\";
    private DownloadStatus status;
    private int toPrintDownloadSize;

    public FileData(String url, String filePath){
        this.url = url;
        this.fileName = "";
        this.mimeType = "";
        this.fileSize = 0;
        this.downloadedSize = 0;
        this.toPrintDownloadSize = 0;
        this.status = DownloadStatus.NOT_STARTED;
        this.filePath = filePath;  
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
        System.out.println("ex from url: " + extentionFromUrl);

        if(MimeTypes.lookupMimeType(extentionFromUrl.substring(1,extentionFromUrl.length())) == null){
            extentionFromUrl = "";
            // set extention
            this.extention = MimeTypes.getDefaultExt(this.mimeType);
        }
        else{
            this.extention = extentionFromUrl;// set extention
        }
        System.out.println("extention from url: "+extentionFromUrl);
        if(extentionFromUrl.length()<=0 || extentionFromUrl.equals("")){
            System.out.println("No extention found");
            this.fileName = "download_" + this.getHash(url); // if no extention found, use the hash of the url as the file name
        }

        // clean mimeType
        if(mimeType.contains(";")){
            this.mimeType = mimeType.substring(0, mimeType.indexOf(";"));
        }
        System.out.println("mimeType: "+mimeType);
        
        /*
        System.out.println("extention: "+this.extention);
        if(extentionFromUrl.length() > 0 && (this.extention.length() == 0 || this.extention.equals("unknown"))){
            this.extention = extentionFromUrl;
            System.out.println("extention from url: "+extentionFromUrl);
        }
        */
        System.out.println("File name: " + this.fileName + "." + this.extention + " Mime: " + this.mimeType);
        this.fileName = this.fileName + "." + this.extention;
    }

    /*
     * This method is responsible for handling the metadata of the file.
     * 
     * @param: None
     * @return: None
     */
    public void setMetadata(){
        try{
            this.urlObj = new URL(url);
            URLConnection conn = urlObj.openConnection();
            this.mimeType = conn.getContentType();
            this.fileSize = conn.getContentLength();
            this.buffer = new byte[this.fileSize];
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
     * This method is responsible joining the downloaded bytes from each part buffer.
     * 
     * @param: partBuffer - the byte array (buffer) of the part file.
     * @return: treu if the part file is successfully joined.
     */
    public boolean joinBytes(byte[] partBuffer){
        //System.out.println("Item to be stitched :: " + partBuffer.length);
        System.arraycopy(partBuffer, 0, this.buffer, this.downloadedSize, partBuffer.length);
        this.downloadedSize += partBuffer.length;
        //System.out.println("downloaded size: "+this.downloadedSize);
        if(this.downloadedSize == this.fileSize){
            this.status = DownloadStatus.FINISHED;
            this.saveFile();
        }
        return true;
    }

    /*
     * This method is responsible for saving the file.
     * 
     * @param: None
     * @return: None
     */
    public boolean saveFile(){
        try{
            FileOutputStream fos = new FileOutputStream(this.filePath + this.fileName);
            fos.write(this.buffer);
            fos.close();
            this.status = DownloadStatus.DOWNLOADED;
            return true;
        }
        catch(IOException e){
            System.out.println("IOException: " + e.getMessage());
            return false;
        }
    }

    public String getFileName(){
        return this.fileName;
    }

    public String getMimeType(){
        return this.mimeType;
    }

    public int getFileSize(){
        return this.fileSize;
    }

    public String getExtention(){
        return this.extention;
    }

    public String getUrl(){
        return this.url;
    }

    public int getDownloadedSize(){
        return this.downloadedSize;
    }

    public void addDownloadedSize(int size){
        if(this.status == DownloadStatus.STARTED ){
            System.out.print("\nContent Downloaded: " + this.toPrintDownloadSize + "% complete"); // Move cursor up one line
            this.setDownloadStatus(DownloadStatus.DOWNLOADING);
        }
        this.toPrintDownloadSize += size;
        if(this.downloadedSize>this.toPrintDownloadSize){
            this.toPrintDownloadSize = this.downloadedSize;
        }

        int percentage = (int)((this.toPrintDownloadSize*100)/this.fileSize);

        //System.out.print("\033[2J"); // Erase line content
        for(int i=0; i<11; i++){
            System.out.print("\b");
        }
        if (percentage > 10)
        {
            System.out.print('\b');
        }
        if (percentage > 100)
        {
            System.out.print('\b');
        }
        System.out.print(percentage);
        System.out.print("% complete");
        
    }

    public URL getUrlObj(){
        return this.urlObj;
    }

    public String getFilePath(){
        return this.filePath;
    }

    public String getPartFileSavePath(){
        return this.partFileSavePath;
    }

    public DownloadStatus getStatus(){
        return this.status;
    }

    public void setDownloadStatus(DownloadStatus status){
        this.status = status;
    }

}
