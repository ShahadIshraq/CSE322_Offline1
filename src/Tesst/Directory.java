package Tesst;
/******************************************************************************
 *  Compilation:  javac Directory.java
 *  Execution:    java Directory directory-name
 *  Dependencies: Queue.java
 *  
 *  Prints out all of the files in the given directory and any
 *  subdirectories in level-order by using a queue. Also prints
 *  out their file sizes in bytes.
 *
 *  % java Directory .
 *
 ******************************************************************************/

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class Directory { 

    static void tree(File directory, String level)
    {
        System.out.println(level + directory.getName());
        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory())
            {
                tree(files[i], level + "-");
            }
            else {
                System.out.println(level + "-" + files[i].getName());
            }
        }
        System.out.println();
    }


    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        File root = new File(in.nextLine());     // root directory
        tree(root,"");
//        queue.add(root);
//        while (!queue.isEmpty()) {
//            File directory = queue.remove();
//            File[] files = directory.listFiles();
//            if (files != null) {
//                for (int i = 0; i < files.length; i++) {
//                    if (files[i].isDirectory()) queue.add(files[i]);
//                    else System.out.println(files[i].length() + ":\t" + files[i]);
//                }
//            }
//        }
    }

}

