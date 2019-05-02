import java.awt.*;
import javax.swing.*;
import java.io.*;
class Testing extends JFrame
{
   public Testing()
   {
      //BufferedReader br = null;
      File file = new File("file.txt");
      setLocation(300,200);
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      JTextArea area = new JTextArea(20,40);
      getContentPane().add(new JScrollPane(area));
      area.setEditable(false);
      JScrollPane scrol = new JScrollPane();
      pack();
      
      try
      {
         BufferedReader br = new BufferedReader(new FileReader(file));
         try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
         
            while (line != null) {
               sb.append(line);
               line = br.readLine();
            }
            String everything = sb.toString();
         } finally {
            br.close();
         }
      }
      catch(Exception e)
      {
      
      }
   }
   public static void main(String[] args)
   {
      new Testing().setVisible(true);
   }
}

