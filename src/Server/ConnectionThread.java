package Server;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Shahad Ishraq on 3/1/17.
 * This is the thread for the server to wait for the incoming connection requests.
 */
public class ConnectionThread implements Runnable{
    ServerMain serverMain;
    private ServerSocket ss;

    int id;
    static int workerThreadCount;

    public ConnectionThread(ServerMain serverMain)
    {
        this.serverMain = serverMain ;
        id = 1;
        workerThreadCount = 0;
        try
        {
            ss = new ServerSocket(5555);
            serverMain.showMessage("Server has been started successfully.");

            //JOptionPane.showMessageDialog(serverMain, "Server has been started successfully.");
        }
        catch(Exception e)
        {
            System.err.println("Problem in ServerSocket operation. Exiting main.");
        }
    }



    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {

        while(true)
        {
            Socket s = null;		//TCP Connection
            try {
                s = ss.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            WorkerThread wt = new WorkerThread(s, id , serverMain);
            Thread t = new Thread(wt);
            t.start();
            workerThreadCount++;
            id++;
        }
    }
}
