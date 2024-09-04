package Email.Email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/Email")
public class EmailController {
  @Autowired
  private EmailService emailService;

  @PostMapping("/send")
  public ResponseEntity<EmailResponse> send(@RequestBody EmailRequest request) {

    emailService.sendEmail(request.getTo(), request.getSubject(), request.getMessage());
    EmailResponse response = EmailResponse.builder().message("Email Sent Success")
        .statusCode(HttpStatus.OK)
        .success(true).build();

    return ResponseEntity.ok(response);
  }

  @PostMapping(value = "/sendFile")
  public ResponseEntity<EmailResponse> sendFile(@RequestPart EmailRequest request,
      @RequestPart("file") MultipartFile file) {

    emailService.sendEmailWithFile(request.getTo(), request.getSubject(), request.getMessage(), file);

    return ResponseEntity.ok(EmailResponse.builder().message("Email With File Sent Successfully")
        .statusCode(HttpStatus.OK).success(true).build());
  }

}
