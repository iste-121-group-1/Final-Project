import java.awt.*;
import javax.swing.*;
import java.io.*;
class Testing extends JFrame {
   public Testing() {
      //BufferedReader br = null;
      File file = new File("file.txt");
      setLocation(300,200);
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      JTextArea area = new JTextArea(20,40);
      getContentPane().add(new JScrollPane(area));
      area.setEditable(false);
      JScrollPane scrol = new JScrollPane();
      pack();
      setLocationRelativeTo(null);
      
      try {
         BufferedReader br = new BufferedReader(new FileReader(file));
         try {
            String line;
         
            while ((line = br.readLine()) != null) {
               area.append(line + "\n");
               System.out.println(line);
            }
         } finally {
            br.close();
         }
      } catch (Exception e) {
      
      }
   }
   public static void main(String[] args) {
      new Testing().setVisible(true);
   }
}

