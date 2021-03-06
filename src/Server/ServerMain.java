package Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;


/**
 * Created by Shahad Ishraq on 2/28/17.
 * This is the class with the main method of the server side application.
 * Upon receipt of the connection request from a client, the server will
 *      -save the <IP Address, Student ID> mapping .
 *      -create a folder named after the ‘Student ID’ and all the files received through this connection
 *       will be saved under this folder.
 *       [The ‘Student IDs’ are treated as numbers and should  be within the range pre-configured ]
 *      -If the ‘Student ID’ of an incoming request in not within the list, the main notifies the requesting
 *       client accordingly and ask him to send a valid ID.
 * If two different Student IDs try to register from the same IP or same Student ID tries to
 * register from two different IPs, the server admin will be explicitly asked, through a prompt, whether to
 * accept or reject the connection request. In case of rejection, the requesting client will be notified ).
 *
 * The main method provides interface for the following configurable options:
 *      (i)Root Directory: Location in the file system where the all the incoming files will
 *         be stored by the server, i.e., all the folders named after the ‘Student IDs’ of
 *         incoming requests as mentioned above, will be stored under this directory. By default,
 *         the root directory is the location from which the server program is running.
 *      (ii)File types: Allowable file types that a client can upload.
 *      (iii) Number of files and folder: Number of files a client is allowed to upload and
 *          whether a client is able to upload folder.
 *      (iv)Max file size: Maximum file size that a client is allowed to upload.
 *      (v)Allowable Student IDs: List of Student IDs who are allowed to upload files.
 *         IDs can be specified in range format such as 201305001-20130560 as well as comma separated
 *         individual IDs such as 200905100, 200805119.
 *
 * Optional: The server program, upon receipt of files from a client will again check
 * whether the incoming files conform to the configured constraints.
 */
public class ServerMain extends JFrame implements ActionListener {

    
    JLabel label;
    JLabel label1; //root
    JLabel label2; // File Types
    JLabel label3; //Number of folders
    JLabel label4; //Allowable Student IDs
    JLabel label5; //Is folder allowed?

    JTextField mSize;
    JTextField root;
    JTextField fType;
    JTextField nOf;
    JTextField stdIDs;
    JRadioButton yes ;
    JRadioButton no ;
    String path,types[];
    boolean folder,range;
    int ids [] , numberOfFiles;
    boolean isRange;

    ButtonGroup group;
    JButton jb;
    JButton browse;
    JFileChooser fc;
    Container c;
    JTextArea area;
    private ArrayList<Student> connected;
    protected int maxFileSize;
    private File rootDirectory;


    public ServerMain()
    {
        super("Configure");
        connected = new ArrayList();

        //Initializing labels
        label1 = new JLabel("Root");
        label2 = new JLabel("Fyle Type");
        label3 = new JLabel("Max No. of files/folders allowed");
        label = new JLabel("Max size of folder (kB)");
        label4 = new JLabel("Allowable Student IDs");
        label5 = new JLabel("Allow uploading folder?");

        //initializing text fields
        root = new JTextField(10);
        fType = new JTextField(10);
        nOf = new JTextField(5);
        stdIDs = new JTextField(10);
        mSize = new JTextField(10);
        
        //initializing buttons
        jb = new JButton("Save");
        browse = new JButton("Browse",new ImageIcon("Open16.gif"));
        yes = new JRadioButton("Yes",true);
        no = new JRadioButton("No");

        //adding the radio buttons to a group
        group = new ButtonGroup();
        group.add(yes);
        group.add(no);

        //adding the components to the content pane
        c=getContentPane();
        c.setLayout(new FlowLayout());
        c.add(label1);
        c.add(root);
        c.add(browse);
        c.add(label2);
        c.add(fType);
        c.add(label3);
        c.add(nOf);
        c.add(label4);
        c.add(stdIDs);
        c.add(label);
        c.add(mSize);
        c.add(label5);
        c.add(yes);
        c.add(no);
        c.add(jb);

        //attaching the action listeners
        jb.addActionListener(this);
        browse.addActionListener(this);

        //setting the frame
        setSize(300,200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocation(300,300);
        setVisible(true);

    }

    /**
     * This is the action listener.
     * @param ae is the ActionEvent that triggered this listener
     */
    public void actionPerformed(ActionEvent ae)
    {
        if(ae.getSource()== browse)
        {
            fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                //This is where a real application would open the file.
                System.out.println("Root: "+file.getPath());
                root.setText(file.getPath());
            } else {
                //label1 = new JLabel("Open command cancelled by user.");
                System.out.println("Open command cancelled by user.");
            }
        }
        if(ae.getSource()==jb) //The server wants to start
        {
            try {
                path = root.getText();
                rootDirectory = new File(path);
                types = fType.getText().split(",");
                numberOfFiles = Integer.parseInt(nOf.getText());
                maxFileSize = Integer.parseInt(mSize.getText());
                String in = stdIDs.getText();
                folder = yes.isSelected();
                isRange = false;
                String temp [];
                int startID,endID,IDs[];
                if (in.matches("(\\d)+\\-(\\d)+")) {
                    isRange = true ;
                    temp = in.split("\\-");
                    startID = Integer.parseInt(temp[0]);
                    endID = Integer.parseInt(temp[1]);
                    System.out.println("Range: "+startID+"-"+endID);
                }
                else if (in.matches("((\\d)+(,(\\d)+)*)"))
                {
                    temp = in.split(",");
                }
                else {
                    JOptionPane.showMessageDialog(null, "Invalid input for student ID!!");
                    return;
                }


                //removing current elements
                c.removeAll();
                c.repaint();

                //Starting connection thread
                new Thread(new ConnectionThread(this)).start();
                this.setTitle("Connected Users");
                c.removeAll();
                label1 = new JLabel("Log");
                c.add(label1);
                area = new JTextArea(20,20);
                c.add(area);
                c.revalidate();
                c.repaint();


            }catch (Exception e)
            {
                JOptionPane.showMessageDialog(null, "Invalid input format!!");
            }


        }
    }






    public static void main(String[] args) { new ServerMain();    }


    /**
     * This is a static method used to generate message dialogue box with a given message.
     * @param s is the string to be shown as message
     */
    public static void showMessage(String s) {
        JOptionPane.showMessageDialog(null,s);
    }



    /**
     * A method used to update the server side interface when a client connects.
     * This implementation does not expect concurrent file transfer from the same IP.
     * @param newStudent
     */
    public boolean addClient(Student newStudent)
    {
        System.out.println("In addClient");

        boolean hit = false;
        boolean sameStudent = false;
        String message = "Connection request from Student ID: "+newStudent.getStudentID()+" IP: "+newStudent.getInetAddress()+"\nMatches: \n";
        for (int i = 0 ; i < connected.size() ; i++)
        {
            if (newStudent.getInetAddress().equals(connected.get(i).getInetAddress()))
            {
                hit = true ;
                //Another connection request from the same IP
                if (newStudent.getStudentID() == connected.get(i).getStudentID())
                {
                    //Another connection request from the same student ID and IP
                    sameStudent = true;
                    //message += " "+i+". Student ID: "+newStudent.getStudentID()+" IP: "+newStudent.getInetAddress()+"\n";
                }
                else
                {
                    //Only IP matches
                    message += " "+i+". Student ID: "+connected.get(i).getStudentID()+" IP: "+newStudent.getInetAddress()+"\n";
                }

            }
            else if (newStudent.getStudentID() == connected.get(i).getStudentID())
            {
                sameStudent = true;
                //Another connection request from the same student ID
                message += " "+i+". Student ID: "+newStudent.getStudentID()+" IP: "+connected.get(i).getInetAddress()+"\n";
            }
        }
        System.out.println("After the loop.Hit: "+hit);
        if ((hit && !sameStudent) || (!hit && sameStudent)) {
            message += "Allow the connection?";
            int reply = JOptionPane.showConfirmDialog(null, message, "Someone might me cheating", JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                if (!sameStudent) connected.add(newStudent);
                area.append("Student ID: "+newStudent.getStudentID()+" IP: "+newStudent.getInetAddress()+" connected\n");
                return true;
            }
            else {
                return false;
            }
        }
        else if (!sameStudent) {
            connected.add(newStudent);
            area.append("Student ID: " + newStudent.getStudentID() + " IP: " + newStudent.getInetAddress() + " connected\n");
            try {
                Files.createDirectory(Paths.get(rootDirectory.getAbsolutePath()+"/"+newStudent.getStudentID()));//root.
                System.out.println("Created directory : "+rootDirectory.getAbsolutePath()+"/"+newStudent.getStudentID());
            } catch (IOException e) {
                System.out.println("Problem creating directory : "+rootDirectory.getAbsolutePath()+"/"+newStudent.getStudentID());
            }
            return true;
        }
        area.append("Student ID: "+newStudent.getStudentID()+" IP: "+newStudent.getInetAddress()+" connected\n");
        return true;
    }


    /**
     * A method used to update the server side interface when a client disconnects.
     * @param student
     */
    public void removeClient(Student student) {

        area.append("Student ID: "+student.getStudentID()+" IP: "+student.getInetAddress()+" left.");
    }
}
