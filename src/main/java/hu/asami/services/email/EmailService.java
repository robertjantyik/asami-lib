package hu.asami.services.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EmailService {
    //region Fields
    private JavaMailSender mailSender;
    private TemplateEngine templateEngine;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    //endregion
    public void sendMail(final String toEmail, Locale locale, Map<String, Object> vars, List<EmailInlineImage> images, final String template, final String fromEmail, final String fromName, final String subject) throws MessagingException, UnsupportedEncodingException {
        final Context ctx = new Context(locale);
        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        final String htmlContent = this.templateEngine.process(template, ctx);
        message.setText(htmlContent);

        for (var entry : vars.entrySet()) {
            ctx.setVariable(entry.getKey(), entry.getValue());
        }

        message.setSubject(subject);
        message.setFrom(fromEmail, fromName);
        message.setTo(toEmail);

        for (EmailInlineImage emailInlineImage : images) {
            message.addInline(emailInlineImage.getName(), emailInlineImage.getData(), emailInlineImage.getType());
        }

        this.mailSender.send(mimeMessage);
    }
}
