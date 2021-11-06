

import java.io.*;
import java.net.*;

public class Proto {
    
    public static void main(String[] args){

        final String link = "https://www.w3schools.com/images/myw3schoolsimage.jpg"; // link to image

        try{
            URL url = new URL(link); // Create an URL object
            HttpURLConnection conn = (HttpURLConnection) url.openConnection(); // Open a connection to the URL

            int fieldValue = conn.getContentLength();// Get the content disposition field value
            System.out.println(fieldValue);
            System.out.println("Content-Disposition: " + conn.getHeaderField("Content-Type"));
            System.out.println(MimeTypes.lookupExt(conn.getHeaderField("Content-Type")));

            // Write the content to a file
            BufferedInputStream in = new BufferedInputStream(conn.getInputStream()); 
            String fileName = "download." + MimeTypes.lookupExt(conn.getHeaderField("Content-Type")); // Create a file name
            FileOutputStream fos = new FileOutputStream(fileName); // Create a file output stream

            byte[] buffer = new byte[1024]; // Create a buffer
            int len; // Length of the buffer

            while((len = in.read(buffer)) != -1){ // Read the content
                fos.write(buffer, 0, len); // Write the buffer to the file
            }

        }
        catch(Exception e){
            System.out.println("Error: " + e);
        }

    }

}
