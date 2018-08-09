package cn.habitdiary.form.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Map;


@Component
public class EmailUtil {
    @Autowired
    private  JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${fromBody}")
    private String fromBody;

    @Value("${nickname}")
    private String nickname;

    @Value("${root-location}")
    private String rootLocation;

    private void sendHtmlMail(String toBody, String subject, String content,Integer userid,String formname,String uuid) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            //true表示需要创建一个multipart message
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(nickname + "<" + fromBody + ">");
            helper.setTo(toBody);
            helper.setSubject(subject);
            helper.setText(content, true);


            String filePath = rootLocation + "/" + userid + "/" + formname +
                    "(" + uuid + ")" + ".xls";
            String fileName = formname + "-表单君反馈导出.xls";
            FileSystemResource file = new FileSystemResource(new File(filePath));
            helper.addAttachment(fileName, file);

            mailSender.send(message);
        } catch (MessagingException e) {
        }
    }

    public void sendTemplateMail(String toBody,String subject,String templateName,Map<String,Object> variables) {
        //创建邮件正文
        Context context = new Context();
        context.setVariables(variables);
        Integer userid = (Integer) variables.get("userid");
        String formname = (String)variables.get("formname");
        String uuid = (String)variables.get("uuid");
        String emailContent = templateEngine.process(templateName,context);
        sendHtmlMail(toBody,subject,emailContent,userid,formname,uuid);
    }
}