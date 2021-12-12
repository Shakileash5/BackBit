package src.main.java;

import java.io.*;
import java.net.*;
import java.nio.CharBuffer;
import java.lang.Thread;
import javax.net.ssl.HttpsURLConnection;
import src.main.java.*;


/*
 * This class downloads a part of file from a URL and saves it to a local file.
 * @author: Shakileash
 * @version: 1.0
 */

public class DownloadPart extends Thread {
    
    private URL url;
    private String fileName;
    private String savePath;
    private int fileSize;
    private int downloadedSize;
    private int rangeStart;
    private int rangeEnd;
    private boolean append;
    private byte[] data;
    private FileData fileData;

   /*
    * Constructor for DownloadPart class.
    * @param url: URL of the file to be downloaded.
    * @param fileName: name of the file to be downloaded.
    * @param savePath: path of the file to be saved.
    * @param fileSize: size of the file to be downloaded.
    * @param rangeStart: start of the range of the file to be downloaded.
    * @param rangeEnd: end of the range of the file to be downloaded.
    */
    public DownloadPart(FileData fileData, String fileName, int rangeStart, int rangeEnd) {
        this.url = fileData.getUrlObj();
        this.fileName = fileName;
        this.savePath = fileData.getPartFileSavePath();
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
        this.fileData = fileData;
        if (rangeStart == 0) {
            this.fileSize = rangeEnd;
        } else {
            this.fileSize = rangeEnd - rangeStart;
        }
        
        this.downloadedSize = 0;
        System.out.println("\n\nDownloading " + fileName + " from " + url.toString() + "fileSize "+fileSize);
    }

    public String getFileName() {
        return this.fileName;
    }

   /*
    * Downloads the file from the URL.
    * @param fileSize: size of the file to be downloaded.
    * @return: true if the file is downloaded successfully, false otherwise.
    */
    private boolean downloadContent( BufferedInputStream bis, BufferedOutputStream bos) throws IOException {
        this.data = new byte[this.fileSize];
        int bytesRead = 0;
        int bytesFrom = 0;
        int toBeRead = 1378; // 1378 bytes buffer size
        int bytesWritten = 0;
        int count = 0;
        
        //System.out.println("\nDownloading " + fileName + " from " + url.toString() + "fileSize "+fileSize);

        if(fileSize<toBeRead) // if the file size is less than the buffer size
            toBeRead = fileSize;
        //System.out.println("Downloading to be read " + toBeRead);

        while((bytesRead = bis.read(data, bytesFrom, toBeRead)) >0){ // read the input stream
            //System.out.println("Reading: " + bytesFrom + " till: " + toBeRead + " Now read: "+bytesRead);
            bytesFrom += bytesRead;
            if((fileSize-bytesFrom) < toBeRead){ // if the remaining size is less than the buffer size
                toBeRead = fileSize - bytesFrom;
            }
            
            //System.out.println("Writing: " + bytesWritten + " till: " + bytesRead);
            bos.write(data,bytesWritten,bytesRead);
            bytesWritten += bytesRead;
            if(count == 5){
                this.fileData.addDownloadedSize(bytesRead);
                count = 0;
            }
            else{
                count++;
            }
            
        }

        return true;
    }

    /*
     * Saves the file to the local file system.
     * @return: true if the file is saved successfully, false otherwise.
     */
    private boolean saveFile() {
        File file = new File(this.savePath + this.fileName);
        
        try {
            // create the output stream
            FileOutputStream fos = new FileOutputStream(this.fileName,this.append);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            bos.write(this.data);
            
            bos.flush();
            bos.close();
            fos.close();
            //bis.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

   /* The function checks if binaries exists in the directory. if so, then the download resumes from the last point.
    * @return: None
    */
    @Override
    public void run(){

        try{
            // create the file
            File file = new File(this.savePath + this.fileName);
            
            // append the binaries into file if the file exists
            if(file.exists()){
                this.append = true;
                System.out.println("Appending to file: " + this.fileName);
            }
            else{
                this.append = false;
            }
            //System.out.println("fileName" + this.fileName);
            // create the output stream
            FileOutputStream fos = new FileOutputStream(this.savePath + this.fileName,this.append);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            long existingFileSize = fos.getChannel().size();

            if(existingFileSize<this.fileSize){
                this.rangeStart = (int)existingFileSize + this.rangeStart;
            }

            URL obj = new URL(this.url.toString());
            // create the input stream
            HttpsURLConnection conn = (HttpsURLConnection) obj.openConnection();
            //HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setRequestProperty("Range", "bytes=" + this.rangeStart + "-" + this.rangeEnd);
            BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
            //System.out.println("\n\nDownloading code..."+conn.getContentLength());
            //System.out.println("Downloading range... "+ this.rangeStart + "-" + this.rangeEnd);
            // download the file
            this.downloadContent(bis, bos);

            // close the streams
            bis.close();
            bos.close();
        }
        catch(Exception e){
            System.out.println("Error: "+e.getMessage());
            e.printStackTrace();
        }
        return ;
    }

    public void deleteFile(){
        File file = new File(this.savePath + this.fileName);
        file.delete();
    }

    public byte[] getData() {
        return data;
    }

}
