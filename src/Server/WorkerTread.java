package Server;

import java.io.*;
import java.net.Socket;

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
                        try
                        {
                            strRecv = br.readLine();					//These two lines are used to determine
                            String fileName = strRecv;		            //the name of the receiving file

                            strRecv = br.readLine();					//These two lines are used to determine
                            int filesize=Integer.parseInt(strRecv);		//the size of the receiving file
                            byte[] contents = new byte[10000];

                            FileOutputStream fos = new FileOutputStream(downloadDirectory+"/"+fileName);
                            System.out.println("created the new file for downloading.");
                            BufferedOutputStream bos = new BufferedOutputStream(fos);
                            System.out.println("bos initialized");
                            InputStream is = socket.getInputStream();
                            System.out.println("GOt inputStream");
                            int bytesRead = 0;
                            int total=0;			//how many bytes read
                            System.out.print("\nReceiving");
                            while(total!=filesize)	//loop is continued until received byte=totalfilesize
                            {
                                System.out.print(".");
                                bytesRead=is.read(contents);
                                total+=bytesRead;
                                bos.write(contents, 0, bytesRead);
                            }
                            bos.flush();
                            System.out.println("\nDone.");
                        }
                        catch(Exception e)
                        {
                            System.err.println("Could not transfer file.");
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
                System.out.println("Problem in communicating with the client [" + id + "]. Terminating worker thread.");
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
