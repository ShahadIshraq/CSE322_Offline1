package Server;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * This is a modified version of the class with the same name provided as example in the lab.
 * This thread works for carrying out the communication between the server and one client.Each connected client
 * gets one WorkerThread .
 */
class WorkerThread implements Runnable
{
    //Fields for the TCP communication
    private Socket socket;
    private InputStream is;
    private OutputStream os;
    private ServerMain serverMain;
    private ObjectOutputStream outputStream;


    private Student student;
    private int studentID ;
    private int id = 0;
    private String strRecv;
    private File downloadDirectory;
    private File[] files;

    public WorkerThread(Socket s, int id , ServerMain serverMain)
    {
        this.socket = s;
        this.serverMain = serverMain ;

        try
        {
            this.is = this.socket.getInputStream();
            this.os = this.socket.getOutputStream();
        }
        catch(Exception e)
        {
            System.err.println("Sorry. Cannot manage client [" + id + "] properly.");
        }

        this.id = id;
    }

    public void run()
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(this.is));
        PrintWriter pr = new PrintWriter(this.os);


        try {

            //Getting the student ID
            String stID = br.readLine();
            studentID = Integer.parseInt(stID);
            student = new Student(studentID,socket.getInetAddress(),this,id);
            if(!serverMain.addClient(student))
            {
                pr.println("Go home");
                pr.flush();
                try
                {
                    this.is.close();
                    this.os.close();
                    this.socket.close();
                }
                catch(Exception e)
                {
                    System.out.println(e);
                }
                System.out.println("Denied connection to "+studentID+"@"+socket.getInetAddress());
                return;
            }
            else {
                pr.println("ok");
                pr.flush();
                downloadDirectory = new File(serverMain.path+"/"+stID);
            }

            /////////////////////////////////////////////////////////////////////////////////////////////////////////////
            ///     Chatting with the client about the specifications                                                 ///
            ///     Things to get:                                                                                    ///
            ///              -File types: Allowable file types that a client can upload.                              ///
            ///              - Number of files and folder: Number of files a client is allowed to upload and          ///
            ///               whether a client is able to upload folder.                                              ///
            ///              -Max file size: Maximum file size that a client is allowed to upload.                    ///
            ///                                                                                                       ///
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////


            //Sending the file types
            System.out.println("Sending file types to "+id);
            System.out.println("Sending no of types ("+serverMain.types.length+") to "+id);
            pr.println(serverMain.types.length);
            pr.flush();
//            if (!br.readLine().equals("Got it")) throw new Exception("Error in communication.");

            for (int i = 0 ; i < serverMain.types.length ; i++)
            {
                System.out.print(" "+serverMain.types[i]);
                pr.println(serverMain.types[i]) ;
                pr.flush();
            }

            //Whether folders are allowed
            System.out.println(serverMain.folder);
            pr.println(serverMain.folder);
            pr.flush();

            //The maximum number of files
            pr.println(serverMain.numberOfFiles);
            pr.flush();

            //The maximum file size
            pr.println(serverMain.maxFileSize);
            pr.flush();

            //The files uploaded by this client
            System.out.println("Sending already uploaded file list.");
            files = downloadDirectory.listFiles();
            pr.println(files.length);
            pr.flush();
            for (int i = 0 ; i < files.length ; i++)
            {
                pr.println(files[i].getName());
                System.out.println("  "+files[i].getName());
                pr.flush();
            }

        }catch (Exception e)
        {
            System.out.println(e);
        }

        String str;

        while(true)
        {
            try
            {
                if( (str = br.readLine()) != null )
                {
                    if(str.equals("BYE"))
                    {
                        System.out.println("[" + id + "] says: BYE. Worker thread will terminate now.");
                        serverMain.removeClient(student);
                        break; // terminate the loop; it will terminate the thread also
                    }
                    else if(str.equals("DL"))
                    {
                        int line = 153;
                        try
                        {

                            int sInde = 0,dataSize = 0;
                            strRecv = br.readLine();					//These two lines are used to determine
                            String fileName = strRecv;		            //the name of the receiving file

                            strRecv = br.readLine();					//These two lines are used to determine
                            int filesize = Integer.parseInt(strRecv);  //the size of the receiving file
                            System.out.println("Got size: "+filesize);
                            byte[] contents = new byte[1024];

                            FileOutputStream fos = new FileOutputStream(downloadDirectory+"/"+fileName);
                            //System.out.println("["+id+"] created the new file for downloading.");
                            BufferedOutputStream bos = new BufferedOutputStream(fos);
                            //System.out.println("["+id+"]bos initialized");
                            //InputStream is = socket.getInputStream();
                            //System.out.println("["+id+"]Got inputStream");
                            int bytesRead = 0;
                            String sContents ;
                            int total=0;			//how many bytes read

                            //System.out.print("\n"+"["+id+"] Receiving");
                            while(total < filesize)	//loop is continued until received byte=totalfilesize
                            {
                                System.out.println("["+id+"] Total:"+total+"   filesize:"+filesize+" fileName: "+fileName);
                                System.out.println("in while");
                                line = 177;
                                socket.setSoTimeout(3000);
                                try{
                                    System.out.println("Trying.");
                                    bytesRead = is.read(contents);
                                }
                                catch (SocketTimeoutException e)
                                {
                                    System.out.println("Time out");
                                    pr.println("no");
                                    pr.flush();
                                    continue;
                                }
                                //contents = sContents.getBytes();
                                System.out.println("Not caught? Bytes read: "+bytesRead);
                                line = 185;
                                pr.println("ok");
                                pr.flush();
                                //System.out.println("["+id+"] bytes read: "+bytesRead);
                                //System.out.println(".");
                                int i = 0,j = 0,k = 0;
                                while(contents[k] != ':') k++;
                                line = 192;
                                //System.out.println("..");
                                i = k - 1;
                                System.out.println("i: "+i);
                                k = k + 2;
                                while(contents[k] != ':') k++;
                                line = 198;
                                //System.out.println("...");
                                j = k - 1;
                                System.out.println("j: "+j);
                                k = k + 2;
                                while(contents[k] != ':') k++;
                                line = 204;
                                //System.out.println("....");
                                k--;
                                //Getting FileName
                                System.out.println("k: "+k);
                                byte [] name =new byte[i+1];
                                System.arraycopy(contents, 0, name, 0, name.length);
                                System.out.println(new String(name));
                                //Getting starting index
                                name =new byte[j - i - 2];
                                System.arraycopy(contents, i + 3, name, 0, name.length);
                                sInde = Integer.parseInt(new String(name));
                                System.out.println(sInde);
                                //Getting size
                                name =new byte[k - j - 2];
                                System.arraycopy(contents, j + 3, name, 0, name.length);
                                dataSize = bytesRead - k - 3 ;
                                System.out.println(dataSize);
                                //Getting content
                                //System.out.println(contents.length +" "+(result.length-(k+3)));
                                name =new byte[dataSize];
                                //System.out.println(contents.length == name.length);
                                System.arraycopy(contents, k + 3 , name, 0, dataSize);
                                System.out.println("decoded data amount : "+name.length);
                                bos.write(name);
                                bos.flush();
                                total += dataSize;
                                //bos.write(contents, 0, bytesRead);
                                System.out.println("["+id+"] written to bos");
                            }
                            bos.flush();
                            bos.close();
                            pr.println("all ok");
                            pr.flush();
                            System.out.println("\nDone.");
                        }
                        catch(Exception e)
                        {
                            System.err.println("Could not transfer file. "+e+" at line: "+line);
                        }


                    }
                    else
                    {
                        System.out.println("[" + id + "] says: " + str);
                        pr.println("Got it. You sent \"" + str + "\"");
                        pr.flush();
                    }
                }
                else
                {
                    System.out.println("[" + id + "] terminated connection. Worker thread will terminate now.");
                    serverMain.removeClient(student);
                    break;
                }
            }
            catch(Exception e)
            {
                System.out.println("Problem in communicating with the client [" + id + "]. Terminating worker thread."+e);
                serverMain.removeClient(student);
                break;
            }
        }

        try
        {
            this.is.close();
            this.os.close();
            this.socket.close();
        }
        catch(Exception e)
        {

        }

        ConnectionThread.workerThreadCount--;
        System.out.println("Client [" + id + "] is now terminating. No. of worker threads = "
                + ConnectionThread.workerThreadCount);
    }
}
