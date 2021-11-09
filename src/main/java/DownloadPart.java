package src.main.java;

import java.io.*;
import java.net.*;

import src.main.java.MimeTypes.*;

/*
 * This class downloads a part of file from a URL and saves it to a local file.
 * @author: Shakileash
 * @version: 1.0
 */

public class DownloadPart {
    
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
        System.out.println("Downloading " + fileName + " from " + url.toString() + "fileSize "+fileSize);
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
        int toBeRead = 1024;
        int bytesWritten = 0;
        int toBeWritten = 1024;

        System.out.println("Downloading file..."+bis.available());

        while(bis.available() > 0){
            // read from the input stream
            if(toBeRead > bis.available()){
                toBeRead = bis.available();
            }
            System.out.println("Reading: " + bytesFrom + " till: " + toBeRead + " Available: "+ bis.available());
            bytesRead = bis.read(data,bytesFrom,toBeRead);
            bytesFrom += bytesRead;

            // write into the file
            if(toBeWritten > fileSize - bytesWritten){
                toBeWritten = fileSize - bytesWritten;
            }
            bos.write(data,bytesWritten,toBeWritten);
            bytesWritten += toBeWritten;
            //break;

        }
        return true;
    }

   /* The function checks if binaries exists in the directory. if so, then the download resumes from the last point.
    * @return: None
    */
    public void download(){

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
            
            // create the output stream
            FileOutputStream fos = new FileOutputStream(this.fileName,this.append);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            long existingFileSize = fos.getChannel().size();

            if(existingFileSize<this.fileSize){
                this.rangeStart = (int)existingFileSize + this.rangeStart;
            }

            URL obj = new URL(this.url.toString());
            // create the input stream
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            //conn.setRequestProperty("Range", "bytes=" + this.rangeStart + "-" + this.rangeEnd);
            BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
            System.out.println("Downloading file..."+conn.getInputStream().available());
            System.out.println("Downloading code..."+conn.getResponseCode());
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
