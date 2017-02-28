package Server;

/**
 * Created by shahad on 2/28/17.
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
public class ServerMain {
    public static void main(String[] args) {

    }
}
