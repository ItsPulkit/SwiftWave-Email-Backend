package Email.Email;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface EmailService {

  void sendEmail(String to, String subject, String message);

  void sendEmailMultiple(String[] to, String subject, String message);

  void sendEmailWithHtml(String to, String subject, String htmlContent);

  void sendEmailWithFile(String to, String subject, String message, MultipartFile file);

  List<Message> getMesages();
}
