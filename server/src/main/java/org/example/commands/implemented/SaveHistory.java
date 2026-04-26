 package org.example.commands.implemented;

 import org.example.commands.Command;

 import java.io.BufferedWriter;
 import java.io.FileWriter;
 import java.io.IOException;
 import java.util.List;

 /**
  * Команда save_history
  */

 public class SaveHistory implements Command {

     String envPath = System.getenv("HISTORY_PATH");
     List<String> history;

     public SaveHistory(List<String> history) {
         this.history = history;
     }

     @Override
     public String execute() throws IOException, InterruptedException {
         String path = envPath;
         try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
             writer.write(history.toString());
             writer.flush();
         } catch (IOException e) {
             throw new IOException("Ошибка ввода-вывода");
         }
         return null;
     }
 }
