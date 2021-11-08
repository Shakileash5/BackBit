import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.*;

public class DownloadFile {
    
    public static void main(String[] args) {
        String url = "http://www.google.com/images/srpr/logo3w.png";
        String fileName = "logo";
        String fileNameUrl = url.substring( url.lastIndexOf('/')+1, url.length() );
        // remove extension if there is one
        fileNameUrl = fileNameUrl.substring( 0, fileNameUrl.lastIndexOf('.') );
        int flagFileExist = 0;
        String extension;
        int fileSize = 0;
        try {
            URL urlObj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
            System.out.println("URL: "+url);
            
            if(conn.getResponseCode() != 200){
                System.out.println("Error: "+conn.getResponseCode());
                System.exit(0);
            }

            // get the file extension
            extension = conn.getHeaderField("content-type"); //  store the mime type
            extension = MimeTypes.getDefaultExt(extension); // convert the mime type into extention
            fileName += "." + extension; // add extention to the file name
            fileNameUrl += "." + extension; // add extention to the file name
            System.out.println("Extension: " + extension);

            // get the file size
            fileSize = Integer.parseInt(conn.getHeaderField("content-length"));
            System.out.println("File Size: " + fileSize);

            // check if the file already exists
            System.out.println("File Name: " + fileNameUrl);
            if(Files.exists(Paths.get(fileNameUrl))){
                System.out.println("File already exists");
                //flagFileExist = 1;
                //System.exit(0);
            }

            FileOutputStream fos = new FileOutputStream(fileNameUrl);
            BufferedOutputStream bos = new BufferedOutputStream(fos);

            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

            // download the file
            if(flagFileExist == 0){
                byte[] data = new byte[fileSize];
                int bytesRead = 0;
                int bytesFrom = 0;
                int toBeRead = 1024;
                int bytesWritten = 0;
                int toBeWritten = 1024;

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

                }

                bos.close();
                bis.close();
                is.close();
                fos.close();
            }
            else{
                System.out.println("File already exists");
            }
            

        }
        catch(Exception e) {
            System.out.println("Error: " + e);
        }
    }

}
