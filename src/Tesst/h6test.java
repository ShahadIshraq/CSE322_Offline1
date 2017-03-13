package Tesst;

import java.io.*;

/**
 * Created by shahad on 3/10/17.
 */
public class h6test {
    public static int mystery(int[] A) {
        return mystery(A, 0, A.length - 1);
    }

    private static int mystery(int[] A, int first, int last) {
        if (first == last) return A[first];
        int middle = (first + last)/2;
        return mystery(A, first, middle) + mystery(A, middle+1, last);
    }

    public static void main(String[] args) {
        File file = new File("capture.jpg");
        File newFile = new File("capture1.jpg");
        long fileLength = file.length();
        System.out.println("The size : "+String.valueOf(fileLength));

        try {
            FileInputStream fis = null;

            fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);

            FileOutputStream fos = null;

            fos = new FileOutputStream(newFile);

            BufferedOutputStream bos = new BufferedOutputStream(fos);

            byte[] contents;
            long current = 0;
            long tCurrent = 0;
            long start = System.nanoTime();
            while (current != fileLength) {
                tCurrent = current;
                int size = 512;
                if (fileLength - current >= size)
                    current += size;
                else {
                    size = (int) (fileLength - current);
                    current = fileLength;
                }
                contents = new byte[size];
                //File Name::Starting Byte Number::Size of Segment::File Data
                bis.read(contents, 0, size);
                byte[] a = ("FileName::"+tCurrent+"::"+size+"::").getBytes();
                System.out.println(new String(a));
                byte[] result = new byte[a.length + contents.length];
                // copy a to result
                System.arraycopy(a, 0, result, 0, a.length);
                // copy b to result
                System.arraycopy(contents, 0, result, a.length, contents.length);
                System.out.println(new String(result));
                int i = 0,j = 0,k = 0;
                while(result[k] != ':') k++;
                i = k - 1;
                System.out.println("i: "+i);
                k = k + 2;
                while(result[k] != ':') k++;
                j = k - 1;
                System.out.println("j: "+j);
                k = k + 2;
                while(result[k] != ':') k++;
                k--;
                //Getting FileName
                System.out.println("k: "+k);
                byte [] name =new byte[i+1];
                System.arraycopy(result, 0, name, 0, name.length);
                System.out.println(new String(name));
                //Getting starting index
                name =new byte[j - i - 2];
                System.arraycopy(result, i + 3, name, 0, name.length);
                System.out.println(new String(name));
                //Getting size
                name =new byte[k - j - 2];
                System.arraycopy(result, j + 3, name, 0, name.length);
                System.out.println(new String(name));
                //Getting content
                System.out.println(contents.length +" "+(result.length-(k+3)));
                name =new byte[result.length-(k+3)];
                System.out.println(contents.length == name.length);
                System.arraycopy(result, k + 3 , name, 0, name.length);

                bos.write(contents);
                bos.flush();
                System.out.println("Sending file ... " + (current * 100) / fileLength + "% complete!");
            }
            bis.close();
            fis.close();
            System.out.println("File sent successfully in " + (System.nanoTime() - start) / 1000000000.0 + " seconds!");

        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }
}
