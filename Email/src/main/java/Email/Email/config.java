package Email.Email;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class config {

  @Bean
  public JavaMailSender javaMailSender() {
    return new JavaMailSenderImpl();
  }

}