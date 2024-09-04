package Email.Email;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest

public class EmailTest {
  @Autowired
  private EmailService service;

  @Test
  void emailTest() {
    System.out.println("Sending Email...");
    service.sendEmail("abcd@gmail.com", "Testing", "Testing Emai Sender");
    System.out.println("Email Send Test");

  }

  @Test
  void emailFileTest() {
    System.out.println("Sending Email...");
    service.sendEmailWithFile("abcd.@gmail.com", "Testing", "Heello",
        (MultipartFile) new File(
            "C:\\Users\\rpulk\\OneDrive\\Desktop\\Projects\\Email\\Email\\src\\main\\resources\\static\\images\\Firefly a simple potrait image of young man , which looks realistic 41546.jpg"));
    System.out.println("Email Send Test");

  }

  @Test
  void inboxMessages() {
    List<Message> inboxMessages = service.getMesages();
    inboxMessages.forEach(item -> {

      System.out.println("Subjects ----  " + item.getSubject());
      System.out.println("Contents ----  " + item.getContent());
      System.out.println("Files ----  " + item.getFiles());
      System.out.println("----------------");
    });

  }

}