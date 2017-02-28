package Client;

/**
 * Created by shahad on 2/28/17.
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
public class ClientMain {
    public static void main(String[] args) {

    }
}
