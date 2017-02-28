package Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;



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

    JLabel label1; //root
    JLabel label2; // File Types
    JLabel label3; //Number of folders
    JLabel label4; //Allowable Student IDs
    JLabel label5; //Is folder allowed?

    JTextField root;
    JTextField fType;
    JTextField nOf;
    JTextField stdIDs;
    JRadioButton yes ;
    JRadioButton no ;
    String path,types[];
    boolean folder,range;
    int ids [] , numberOfFiles;

    ButtonGroup group;
    JButton jb;
    JButton upload;
    JFileChooser fc;
    Container c;

    public ServerMain()
    {
        super("Configure");
        label1 = new JLabel("Root");
        label2 = new JLabel("Fyle Type");
        label3 = new JLabel("Max No. of files/folders allowed to be uploaded");
        label4 = new JLabel("Allowable Student IDs");
        label5 = new JLabel("Is folder uploading allowed?");

        root = new JTextField(20);
        fType = new JTextField(20);
        nOf = new JTextField(20);
        stdIDs = new JTextField(20);

        jb = new JButton("Save");
        yes = new JRadioButton("Yes",true);
        no = new JRadioButton("No");

        group = new ButtonGroup();
        group.add(yes);
        group.add(no);

        c=getContentPane();
        c.setLayout(new FlowLayout());
        c.add(label1);
        c.add(root);
        c.add(label2);
        c.add(fType);
        c.add(label3);
        c.add(nOf);
        c.add(label4);
        c.add(stdIDs);
        c.add(label5);
        c.add(yes);
        c.add(no);

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
        if(ae.getSource()==jb)
        {
            try {
                path = root.getText();
                types = fType.getText().split(",");
                numberOfFiles = Integer.parseInt(nOf.getText());
//                JOptionPane.showMessageDialog(null, IP + ":"+ port + "@" + stdID);
//
//                //removing current elements
//                c.remove(label1);
//                c.remove(label2);
//                c.remove(label3);
//                c.remove(tip);
//                c.remove(tstdid);
//                c.remove(tport);
//                c.remove(jb);
//                c.revalidate();
//                c.repaint();
//
//                //adding Upload button
//                upload =new JButton("Upload",
//                        createImageIcon("Open16.gif"));
//                upload.addActionListener(this);
//                c.setLayout(new FlowLayout());
//                c.add(upload);
//                c.revalidate();
//                c.repaint();
            }catch (Exception e)
            {
                JOptionPane.showMessageDialog(null, "Invalid input format!!");
            }


        }
    }
    public static void main(String[] args) {new ServerMain();    }
}
