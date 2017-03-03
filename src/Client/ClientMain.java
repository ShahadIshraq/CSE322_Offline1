package Client;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

import static Client.FileChooserDemo.createImageIcon;


/**
 * Created by Shahad Ishraq on 2/28/17.
 * This is the class with the executable main method of the client side application.
 * On startup,this method will
 *      -prompt the user for the server’s ‘IP Address’ and ‘Port Number’ as well as the ‘Student ID’.
 *      -initiate a connection request with the server
 *      -send the ‘Student ID’.
 *      -show the user an option to upload a file.
 * When the user wants to upload a file by pressing the upload button in the client interface, this method will
 *      -request the server to send all the constraints  configured.
 *      -After receiving the constraints list, the client will then prompt the user to
 *       upload files accordingly and present with him a File Dialog box.
 *       -If the files/folder specified by the user mismatch with the constraints, those will be rejected and a warning
 *       will be showed to the user.
 *
 *  Every file is segmented before transfer by the client where the segment size will be maximum 512 bytes.
 *  Each segment will be sent according to the following format:
 *          File Name::Starting Byte Number::Size of Segment::File Data
 *
 * The next segment will be sent after receiving acknowledgement from the server.
 * (Optional and Bonus: If a client does not receive an acknowledgement for a sent segment,
 * it will resend it after waiting for a predefined time interval).
 *
 * The client must be able to resubmit files and in that case it will be prompted whether the
 * files will be overwritten or both the copies to be retained (as we see in Windows).
 */
public class ClientMain extends JFrame implements ActionListener {
    JLabel label1;
    JTextField tip;
    JLabel label2;
    JTextField tport;
    JLabel label3;
    JTextField tstdid;
    JButton jb;
    JButton upload;
    String IP;
    int port,stdID;
    Container c;
    JFileChooser fc;

    //Fields used for tcp communications
    private Socket s;
    private BufferedReader br;
    private PrintWriter pr;
    private ObjectInputStream inputStream;

    //Specifications
    private ArrayList<String> fileTypes;
    private boolean isFolderAllowed = false;
    private int maxNoOfFiles;
    private int maxSizeOfFile;

    public ClientMain()
    {
        super("Connect");
        label1=new JLabel("IP");
        tip=new JTextField(20);
        label2=new JLabel("Port");
        tport=new JTextField(20);
        label3=new JLabel("Student ID");
        tstdid=new JTextField(20);
        jb=new JButton("Connect");


        c=getContentPane();
        c.setLayout(new FlowLayout());
        c.add(label1);
        c.add(tip);
        c.add(label2);
        c.add(tport);
        c.add(label3);
        c.add(tstdid);

        c.add(jb);
        jb.addActionListener(this);

        setSize(550,70);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocation(300,300);
        setVisible(true);

    }

    public void actionPerformed(ActionEvent ae)
    {
        if(ae.getSource()== upload)
        {
            fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                //This is where a real application would open the file.
                label1 = new JLabel("Opening: " + file.getAbsolutePath());
            } else {
                label1 = new JLabel("Open command cancelled by user.");
            }

            c.remove(upload);
            c.revalidate();
            c.repaint();
            c.add(label1);
            c.revalidate();
            c.repaint();
        }
        if(ae.getSource()==jb) //Client wants to connect
        {
            try {
                IP = tip.getText();
                port = Integer.parseInt(tport.getText());
                stdID = Integer.parseInt(tstdid.getText());
                if (connect(IP,port)) JOptionPane.showMessageDialog(null, "Connection Successful!");
                else
                {
                    JOptionPane.showMessageDialog(null, "Could not connect to the server!");
                    return;
                }


                /////////////////////////////////////////////////////////////////////////////////////////////////////////////
                ///     Chatting with the server about the specifications                                                 ///
                ///     Things to get:                                                                                    ///
                ///              -File types: Allowable file types that a client can upload.                              ///
                ///              - Number of files and folder: Number of files a client is allowed to upload and          ///
                ///               whether a client is able to upload folder.                                              ///
                ///              -Max file size: Maximum file size that a client is allowed to upload.                    ///
                ///                                                                                                       ///
                /////////////////////////////////////////////////////////////////////////////////////////////////////////////


                //getting the allowed file types
                System.out.print("Waiting for the no. of file types.");
                fileTypes = new ArrayList();
                System.out.print("..\n");
                String tt = br.readLine() ;
                System.out.println(".\n  Got no of types: "+tt);
//                pr.println("Got it");
//                pr.flush();
                int i = Integer.parseInt(tt);

                //Getting the file types
                for (; i > 0 ; i-- ) {
                    tt = br.readLine();
                    System.out.print(" "+tt);
                    fileTypes.add(tt);
                }
                System.out.println("Creating the alert...");
                String fTypes = "";
                for (int j = 0 ; j < fileTypes.size() ; j++) fTypes += fileTypes.get(j)+" ";
                System.out.println("Added them all.");

//                if (fileTypes.contains("py")) System.out.println("oka");
                //Whether folders are allowed
                tt = br.readLine();
                if (tt.equals("true")) isFolderAllowed = true;

                //The maximum number of files
                tt = br.readLine();
                maxNoOfFiles = Integer.parseInt(tt);

                //The maximum file size
                tt = br.readLine();
                maxSizeOfFile = Integer.parseInt(tt);

                fTypes += "\nFolder uploading allowed: "+isFolderAllowed+
                            "\nMaximum number of files allowed: "+maxNoOfFiles+
                            "\nMaximum allowed size of file: "+maxSizeOfFile+" kB.";
                JOptionPane.showMessageDialog(null, "Allowed file types: "+fTypes);


                //removing all elements from the current content pane
                System.out.println("Changing...");
                c.removeAll();
                c.repaint();
                //adding Upload button
                this.setTitle("Upload");
                upload =new JButton("Upload", new ImageIcon("Open16.gif"));
                upload.addActionListener(this);
                c.setLayout(new FlowLayout());
                c.add(upload);
                c.revalidate();
                c.repaint();
            }catch (Exception e)
            {
                JOptionPane.showMessageDialog(null, "Invalid input format!!");
            }


        }
    }

    private boolean connect(String IP, int port) {
        System.out.println("Trying to connect...");
        try
        {
            s = new Socket(IP, port);
            br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            pr = new PrintWriter(s.getOutputStream());


            //Sending StudentID
            System.out.println("Sending student ID");
            pr.println(Integer.toString(stdID));
            pr.flush();
            System.out.println("Waiting for confirmation...");
            String confirmation = br.readLine();
            System.out.println("    Received reply: "+confirmation);
            if(confirmation.equals("Go home")) {
                System.out.println("Failed!");;
                return false;
            }
        }
        catch(Exception e)
        {
            System.err.println("Problem in connecting with the server. Exiting main.");
            return false;
        }
        System.out.println("Successful!");
        return true;
    }

    public static void main(String args[]) { new ClientMain(); }
}
