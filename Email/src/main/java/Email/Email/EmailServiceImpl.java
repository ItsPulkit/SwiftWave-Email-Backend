package Email.Email;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.mail.BodyPart;
import jakarta.mail.Folder;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.NoSuchProviderException;
import jakarta.mail.Part;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class EmailServiceImpl implements EmailService {
  private Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
  @Autowired
  private JavaMailSender mailSender;

  @Value("${spring.mail.username}")
  private String senderEmail;

  @Override
  public void sendEmail(String to, String subject, String message) {
    SimpleMailMessage mailMessage = new SimpleMailMessage();
    mailMessage.setTo(to);
    mailMessage.setSubject(subject);
    mailMessage.setText(message);
    mailMessage.setFrom(senderEmail);

    mailSender.send(mailMessage);

    logger.info("Email Sent To Single User");
  }

  @Override
  public void sendEmailMultiple(String[] to, String subject, String message) {
    SimpleMailMessage mailMessage = new SimpleMailMessage();
    mailMessage.setTo(to);
    mailMessage.setSubject(subject);
    mailMessage.setText(message);
    mailMessage.setFrom(senderEmail);

    mailSender.send(mailMessage);

    logger.info("Email Sent To Multiple User");
  }

  @Override
  public void sendEmailWithHtml(String to, String subject, String htmlContent) {

    MimeMessage mailMessage = mailSender.createMimeMessage();
    MimeMessageHelper helper;
    try {
      helper = new MimeMessageHelper(mailMessage, true);
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(htmlContent, true);
    } catch (MessagingException e) {

      e.printStackTrace();
    }

    mailSender.send(mailMessage);

    logger.info("Email Sent To Multiple User");
  }

  @Override
  public void sendEmailWithFile(String to, String subject, String message, MultipartFile file) {
    MimeMessage mailMessage = mailSender.createMimeMessage();
    MimeMessageHelper helper;
    try {
      helper = new MimeMessageHelper(mailMessage, true);
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(message, true);
      // FileSystemResource resource = new FileSystemResource(file);
      helper.addAttachment(file.getOriginalFilename(), file);
    } catch (MessagingException e) {

      e.printStackTrace();
    }

    mailSender.send(mailMessage);

    logger.info("Email Sent To Multiple User");
  }

  @Value("${mail.store.protocol}")
  String protocol;
  @Value("${mail.imaps.host}")
  String host;
  @Value("${mail.imaps.port}")
  String port;

  @Value("${spring.mail.username}")
  String username;
  @Value("${spring.mail.password}")
  String password;

  @Override
  public List<Message> getMesages() {
    Properties configurtions = new Properties();
    configurtions.setProperty("mail.store.protocol", "imaps");
    configurtions.setProperty("mail.imaps.host", "imap.gmail.com");
    configurtions.setProperty("mail.imaps.port", "993");
    Session session = Session.getDefaultInstance(configurtions);
    try {
      Store store = session.getStore();
      store.connect(username, password);
      Folder inbox = store.getFolder("INBOX");
      inbox.open(Folder.READ_ONLY);
      jakarta.mail.Message[] messages = inbox.getMessages();
      int cont = 0;

      List<Message> list = new ArrayList<>();
      for (jakarta.mail.Message message : messages) {
        if (cont >= 10)
          break;
        cont++;
        String content = getContentFromEmailMessage(message);
        List<String> files = getFIleFromEmailMessage(message);

        list.add(Message.builder().subject(message.getSubject()).content(content).files(files).build());

      }
      return list;
    }

    catch (NoSuchProviderException e) {

      e.printStackTrace();
      throw new RuntimeException(e);
    } catch (MessagingException e) {

      e.printStackTrace();
      throw new RuntimeException(e);
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }

  }

  private String getContentFromEmailMessage(jakarta.mail.Message message) throws MessagingException, IOException {

    if (message.isMimeType("text/plain") || message.isMimeType("text/html")) {
      return (String) message.getContent();
    } else if (message.isMimeType("multipart/*")) {

      Multipart part = (Multipart) message.getContent();
      for (int i = 0; i < part.getCount(); i++) {
        BodyPart bodyPart = part.getBodyPart(i);
        if (bodyPart.isMimeType("test/plain")) {
          return (String) bodyPart.getContent();
        }
      }
    }

    return null;
  }

  private List<String> getFIleFromEmailMessage(jakarta.mail.Message message) throws MessagingException, IOException {
    List<String> files = new ArrayList<>();
    if (message.isMimeType("multipart/*")) {

      Multipart content = (Multipart) message.getContent();

      for (int i = 0; i < content.getCount(); i++) {
        BodyPart bodyPart = content.getBodyPart(i);
        if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {
          InputStream inputStream = bodyPart.getInputStream();
          File file = new File("src/main/resources/email" + bodyPart.getFileName());

          Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
          files.add(file.getAbsolutePath());
        }
      }

    }
    return files;
  }
}