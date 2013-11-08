package Elements;

import javax.swing.JFrame;
import java.io.IOException;

public class Elements extends JFrame {

    public Elements() throws IOException{
        add(new Board());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setTitle("Collision");
        setResizable(false);
        setVisible(true);
    }

    public static void main(String[] args) throws IOException{
        new Elements();
    }
}