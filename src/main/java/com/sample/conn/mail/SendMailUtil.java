package com.sample.conn.mail;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.*;
import java.util.Date;
import java.util.Properties;

/**
 * 发送电子邮件
 *
 * @author winnie
 */
public class SendMailUtil {
    /**
     * Hand 邮件服务器 / 授权用户名 / 密码为第三方客户端登录时的授权码 / 发件人邮箱
     */
//    public static final String HOSTNAME = "smtp.126.com";
//    public static final String USERNAME = "hap_dev";
//    public static final String PASSWORD = "hapdev11";
//    public static final String SEND_ADDRESS = "hap_dev@126.com";
    /**
     * QQ 邮件服务器 / 授权用户名 / 密码为第三方客户端登录时的授权码 / 发件人邮箱
     */
    private static final String HOSTNAME = "smtp.qq.com";
    private static final String USERNAME = "1186167376@qq.com";
    private static final String PASSWORD = "qiichyecsrcpjcad";
    private static final String SEND_ADDRESS = "1186167376@qq.com";

    public static void main(String[] args) {
        String toAddress = "yachen.li@hand-china.com";
        String subject = "html 邮件";
        String content = "<div style='color:red'>html 邮件</div><br/><hr/><div>练习</div>";
        try {
            // 发送简单邮件
            sendSimpleEmail(toAddress, subject, content);
            // 发送 text 文本邮件
            sendHtmlEmail(toAddress, subject, content, "text");
            // 发送 html 文本邮件
            sendHtmlEmail(toAddress, subject, content, "html");
            // 发送带附件的邮件
            sendAttachmentEmail(toAddress, subject, content);
        } catch (Exception e) {
            System.out.print("【" + subject + "】邮件发送失败！");
            e.printStackTrace();
        }
    }

    /**
     * 1. 发送简单邮件
     *
     * @param toAddress 接收邮箱
     * @param subject   邮件主题
     * @param content   邮件内容
     */
    public static void sendSimpleEmail(String toAddress, String subject, String content) {
        try {
            SimpleEmail email = new SimpleEmail();
            // 邮件服务器信息
            email.setHostName(HOSTNAME);
            email.setAuthentication(USERNAME, PASSWORD);
            email.setFrom(SEND_ADDRESS);
            // 收件人邮箱
            email.addTo(toAddress);
            // 邮件主题
            email.setSubject(subject);
            // 邮件内容
            email.setMsg(content);
            // 发送邮件
            email.send();
        } catch (EmailException e) {
            e.printStackTrace();
        }
    }

    /**
     * 2. 发送 Html 邮件
     *
     * @param toAddress 接收邮箱
     * @param subject   邮件主题
     * @param content   邮件内容
     * @param type      邮件类型
     * @throws MessagingException           消息异常
     * @throws UnsupportedEncodingException 编码格式异常
     */
    private static void sendHtmlEmail(String toAddress, String subject, String content, String type) throws MessagingException, UnsupportedEncodingException {
        final Properties p = System.getProperties();
        p.setProperty("mail.smtp.host", HOSTNAME);
        p.setProperty("mail.smtp.auth", "true");
        p.setProperty("mail.smtp.user", USERNAME);
        p.setProperty("mail.smtp.pass", PASSWORD);

        // session 环境
        Session session = Session.getInstance(p, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(p.getProperty("mail.smtp.user"), p.getProperty("mail.smtp.pass"));
            }
        });
        session.setDebug(true);
        // 邮件
        Message message = new MimeMessage(session);
        // 主题
        message.setSubject(subject);
        // 发件人
        message.setFrom(new InternetAddress(SEND_ADDRESS, USERNAME));
        // 邮件回复人
        message.setReplyTo(InternetAddress.parse(SEND_ADDRESS));
        // 消息的接收者地址
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));
        // 消息发送的时间
        message.setSentDate(new Date());

        if ("html".equals(type)) {
            // 1. 发送 html
            // MiniMultipart类是一个容器类，包含 MimeBodyPart 类型的对象
            Multipart mainPart = new MimeMultipart();
            // 创建一个包含HTML内容的MimeBodyPart
            BodyPart html = new MimeBodyPart();
            // 设置HTML内容
            html.setContent(content, "text/html; charset=utf-8");
            mainPart.addBodyPart(html);
            // 将MiniMultipart对象设置为邮件内容
            message.setContent(mainPart);
            Transport.send(message);
        } else {
            // 2. 发送 text
            //消息发送的内容
            message.setText(content);
            Transport.send(message);
        }
    }

    /**
     * 3. 发送带附件的邮件
     *
     * @param toAddress 接收邮箱
     * @param subject   邮件主题
     * @param content   邮件内容
     * @throws MessagingException 消息异常
     * @throws IOException        输入输出异常
     */
    private static void sendAttachmentEmail(String toAddress, String subject, String content) throws MessagingException, IOException {
        final Properties p = System.getProperties();
        p.setProperty("mail.smtp.host", HOSTNAME);
        p.setProperty("mail.smtp.auth", "true");
        p.setProperty("mail.smtp.user", USERNAME);
        p.setProperty("mail.smtp.pass", PASSWORD);

        // session 环境
        Session session = Session.getInstance(p, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(p.getProperty("mail.smtp.user"), p.getProperty("mail.smtp.pass"));
            }
        });
        session.setDebug(true);
        // 邮件
        Message message = new MimeMessage(session);
        // 主题
        message.setSubject(subject);
        // 发件人
        message.setFrom(new InternetAddress(SEND_ADDRESS, MimeUtility.encodeText(USERNAME)));
        // 邮件回复人
        message.setReplyTo(InternetAddress.parse(SEND_ADDRESS));

        // 邮件内容：混合的组合关系
        Multipart multipart = new MimeMultipart("mixed");
        message.setContent(multipart);

        // 1. 内容-正文 ：html 格式文本（引用附件图片）
        BodyPart htmlPart = new MimeBodyPart();
        multipart.addBodyPart(htmlPart);
        content += "<img src='cid:pos1.png'>";
        htmlPart.setContent(content, "text/html; charset=utf-8");

        // 2. 内容-附件（被引用后出现在正文，不会出现在附件中）
        MimeBodyPart imgPart = new MimeBodyPart();
        multipart.addBodyPart(imgPart);
        DataSource dsImagePart = new FileDataSource(new File("D:\\posFile\\pos.png"));
        imgPart.setDataHandler(new DataHandler(dsImagePart));
        // 设置 cid 方便引用
        imgPart.setContentID("pos1.png");

        // 3. 内容-附件 1
        BodyPart image = new MimeBodyPart();
        multipart.addBodyPart(image);
        // 图片路径，指定名称
        DataSource dsImage = new FileDataSource(new File("D:\\posFile\\pos.png"));
        image.setDataHandler(new DataHandler(dsImage));
        image.setFileName("pos2.png");


        // 4. 内容-附件 2
        MimeBodyPart attch1 = new MimeBodyPart();
        multipart.addBodyPart(attch1);
        // 附件路径，指定名称
        DataSource ds1 = new FileDataSource(new File("D:\\posFile\\pos.xls"));
        attch1.setDataHandler(new DataHandler(ds1));
        attch1.setFileName(MimeUtility.encodeText("文件1.xls"));

        // 5. 内容-附件 3
        MimeBodyPart attch2 = new MimeBodyPart();
        multipart.addBodyPart(attch2);
        DataSource ds2 = new FileDataSource(new File("D:\\posFile\\pos.pdf"));
        attch2.setDataHandler(new DataHandler(ds2));
        attch2.setFileName(MimeUtility.encodeText("文件2.pdf"));

        // 接收者地址
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));
        message.setSentDate(new Date());
        // 发送邮件
        Transport.send(message);

        // 邮件生成文件
        message.saveChanges();
        OutputStream os = new FileOutputStream("D:\\posFile\\pos.txt");
        message.writeTo(os);
        os.close();
    }

}

