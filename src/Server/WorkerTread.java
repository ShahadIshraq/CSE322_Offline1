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
                            File file = new File("capture.jpg");
                            FileInputStream fis = new FileInputStream(file);
                            BufferedInputStream bis = new BufferedInputStream(fis);
                            OutputStream os = socket.getOutputStream();
                            byte[] contents;
                            long fileLength = file.length();
                            pr.println(String.valueOf(fileLength));		//These two lines are used
                            pr.flush();									//to send the file size in bytes.

                            long current = 0;

                            long start = System.nanoTime();
                            while(current!=fileLength){
                                int size = 10000;
                                if(fileLength - current >= size)
                                    current += size;
                                else{
                                    size = (int)(fileLength - current);
                                    current = fileLength;
                                }
                                contents = new byte[size];
                                bis.read(contents, 0, size);
                                os.write(contents);
                                System.out.println("Sending file ... "+(current*100)/fileLength+"% complete!");
                            }
                            os.flush();
                            System.out.println("File sent successfully in "+(System.nanoTime()-start)/1000000000.0 + " seconds!");
                        }
                        catch(Exception e)
                        {
                            System.err.println("Could not transfer file.");
                        }
                        pr.println("Downloaded.");
                        pr.flush();

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
