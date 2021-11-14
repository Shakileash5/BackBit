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

    public FileData(String url){
        this.url = url;
        this.fileName = "";
        this.mimeType = "";
        this.fileSize = 0;
        this.downloadedSize = 0;
        this.status = DownloadStatus.NOT_STARTED;
        
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
        System.arraycopy(partBuffer, 0, this.buffer, this.downloadedSize, partBuffer.length);
        this.downloadedSize += partBuffer.length;
        System.out.println("downloaded size: "+this.downloadedSize);
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
    public void saveFile(){
        try{
            FileOutputStream fos = new FileOutputStream(this.filePath + this.fileName);
            fos.write(this.buffer);
            fos.close();
            this.status = DownloadStatus.DOWNLOADED;
        }
        catch(IOException e){
            System.out.println("IOException: " + e.getMessage());
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
