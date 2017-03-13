package Tesst;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

class FileTypeDetector
{
    /** 
     * Identify file type of file with provided path and name 
     * using JDK 7's Files.probeContentType(Path). 
     * 
     * @param fileName Name of file whose type is desired. 
     * @return String representing identified type of file with provided name. 
     */  
    public static String identifyFileTypeUsingFilesProbeContentType(final String fileName)
    {  
       String fileType = "Undetermined";  
       final File file = new File(fileName);
       try  
       {  
          fileType = Files.probeContentType(file.toPath());
       }  
       catch (IOException ioException)
       {  
          System.out.println(
               "ERROR: Unable to determine file type for " + fileName  
                  + " due to exception " + ioException);  
       }  
       return fileType;  
    }


    public static void main(String[] args) {
        System.out.println(identifyFileTypeUsingFilesProbeContentType(args[0]));
    }



}
