package lab.b425.module2;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class pathTest {
    public static void main(String[] args) throws IOException {
        File file = new File("src/main/resources/application.properties");


        //first check if Desktop is supported by Platform or not
        if(!Desktop.isDesktopSupported()){
            System.out.println("Desktop is not supported");
            return;
        }

        Desktop desktop = Desktop.getDesktop();
        if(file.exists()) desktop.open(file);

        //let's try to open PDF file
//        file = new File("/Users/pankaj/java.pdf");
//        if(file.exists()) desktop.open(file);

    }
}
