package Server;

import java.net.InetAddress;
import java.util.Objects;

/**
 * Created by Shahad Ishraq on 3/2/17.
 * This is the data structure to store the credentials of the different clients connectiong to the server.
 */
public class Student implements Comparable{

    private int studentID;
    private InetAddress inetAddress;

    public Student (int studentID , InetAddress inetAddress)
    {
        this.studentID = studentID;
        this.inetAddress = inetAddress;
    }

    public int getStudentID() {return studentID;}
    public InetAddress getInetAddress() {return inetAddress;}



    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     */
    @Override
    public int compareTo(Object o) {
        if (o instanceof Student) throw new ClassCastException();
        if (o == null) throw new NullPointerException();
        Student ob = (Student) o;
        if (ob.studentID == this.studentID || ob.inetAddress == this.inetAddress) return 0;
        if (ob.studentID < this.studentID ) return -1;
        return 1;
    }

    @Override
    public boolean equals(Object o)
    {
        if ( !(o instanceof Student) ) return false;
        if (o == null) return false;
        Student ob = (Student)o ;
        if (ob.studentID == this.studentID || ob.inetAddress == this.inetAddress) return true;
        return false;
    }
}
