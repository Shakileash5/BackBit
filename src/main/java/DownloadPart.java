package src.main.java;

import java.io.*;
import java.net.*;
import java.nio.CharBuffer;
import java.lang.Thread;
import javax.net.ssl.HttpsURLConnection;


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

   /*
    * Constructor for DownloadPart class.
    * @param url: URL of the file to be downloaded.
    * @param fileName: name of the file to be downloaded.
    * @param savePath: path of the file to be saved.
    * @param fileSize: size of the file to be downloaded.
    * @param rangeStart: start of the range of the file to be downloaded.
    * @param rangeEnd: end of the range of the file to be downloaded.
    */
    public DownloadPart(URL url, String fileName, String savePath, int rangeStart, int rangeEnd) {
        this.url = url;
        this.fileName = fileName;
        this.savePath = savePath;
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
        this.fileSize = rangeEnd - rangeStart;
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
        byte[] data = new byte[this.fileSize];
        int bytesRead = 0;
        int bytesFrom = 0;
        int toBeRead = 1378; // 1378 bytes buffer size
        int bytesWritten = 0;
        
        System.out.println("\nDownloading " + fileName + " from " + url.toString() + "fileSize "+fileSize);

        if(fileSize<toBeRead) // if the file size is less than the buffer size
            toBeRead = fileSize;
        System.out.println("Downloading to be read" + toBeRead);

        while((bytesRead = bis.read(data, bytesFrom, toBeRead)) >0){ // read the input stream
            System.out.println("Reading: " + bytesFrom + " till: " + toBeRead + " Now read: "+bytesRead);
            bytesFrom += bytesRead;
            if((fileSize-bytesFrom) < toBeRead){ // if the remaining size is less than the buffer size
                toBeRead = fileSize - bytesFrom;
            }
            
            System.out.println("Writing: " + bytesWritten + " till: " + bytesRead);
            bos.write(data,bytesWritten,bytesRead);
            bytesWritten += bytesRead;
        }
        
        return true;
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
            System.out.println("fileName" + this.fileName);
            // create the output stream
            FileOutputStream fos = new FileOutputStream(this.fileName,this.append);
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
            System.out.println("\n\nDownloading code..."+conn.getContentLength());
            System.out.println("Downloading range... "+ this.rangeStart + "-" + this.rangeEnd);
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

}
