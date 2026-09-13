package com.hpu.xinqingcommon.utils;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.hpu.xinqingcommon.constant.RegexConstant;
import com.hpu.xinqingcommon.exception.EmailException;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;


@Data
@Slf4j
public class MailUtil {
    // 邮件协议
    private static final String emailProtocol = "smtp";
    // 发件人的SMTP服务器地址（普通QQ邮箱）
    private static final String emailSMTPHost = System.getenv("SMTP_HOST");
    // 端口
    private static final String emailPort = "465";
    // 发件人邮箱地址
    private static final String emailAccount = System.getenv("SMTP_ACCOUNT"); // 这个是普通QQ邮箱
    // 发件人邮箱授权码
    private static final String emailPassword = System.getenv("SMTP_PASSWORD");

    /**
     * 发送邮件
     *
     * @param email   目标邮件地址
     * @param title   邮件标题
     * @param content 邮件内容
     */
    public static boolean sendEmail(String email, String title, String content) {
        // 未传收件人邮箱地址则直接返回
        if (email.isEmpty()) return false;
        try {
            // 1. 创建参数配置, 用于连接邮件服务器的参数配置
            Properties props = new Properties();
            props.setProperty("mail.transport.protocol", emailProtocol); // 使用的协议（JavaMail规范要求）
            props.setProperty("mail.smtp.host", emailSMTPHost); // 指定smtp服务器地址
            props.setProperty("mail.smtp.port", emailPort); // 指定smtp端口号
            // 使用smtp身份验证
            props.setProperty("mail.smtp.auth", "true"); // 需要请求认证
            props.put("mail.smtp.ssl.enable", "true"); // 开启SSL
            props.put("mail.smtp.ssl.protocols", "TLSv1.2"); // 指定SSL版本
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            // 由于Properties默认不限制请求时间，可能会导致线程阻塞，所以指定请求时长
            props.setProperty("mail.smtp.connectiontimeout", "10000");// 与邮件服务器建立连接的时间限制
            props.setProperty("mail.smtp.timeout", "10000");// 邮件smtp读取的时间限制
            props.setProperty("mail.smtp.writetimeout", "10000");// 邮件内容上传的时间限制
            // 2. 根据配置创建会话对象, 用于和邮件服务器交互
            Session session = Session.getDefaultInstance(props);
            session.setDebug(false); // 设置为debug模式, 可以查看详细的发送log
            // 3. 创建邮件
            MimeMessage message = new MimeMessage(session);
            // 4. From: 发件人（昵称有广告嫌疑，避免被邮件服务器误认为是滥发广告以至返回失败，请修改昵称）
            message.setFrom(new InternetAddress(emailAccount, "心情APP服务信息", "UTF-8"));
            // 5. To: 收件人（可以增加多个收件人、抄送、密送）
            // MimeMessage.RecipientType.TO: 发送 MimeMessage.RecipientType.CC：抄送 MimeMessage.RecipientType.BCC：密送
            // 单个目标邮箱还是多个
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(email, email, "UTF-8"));
            // 6. Subject: 邮件主题（标题有广告嫌疑，避免被邮件服务器误认为是滥发广告以至返回失败，请修改标题）
            message.setSubject(title, "UTF-8");
            // 7. Content: 邮件正文（可以使用html标签）（内容有广告嫌疑，避免被邮件服务器误认为是滥发广告以至返回失败，请修改发送内容）
            message.setContent(content, "text/html;charset=UTF-8");
            // 8. 设置发件时间
            message.setSentDate(new Date());
            // 9. 保存设置
            message.saveChanges();
            // 10. 根据 Session 获取邮件传输对象
            Transport transport = session.getTransport();
            transport.connect(emailAccount, emailPassword);
            // 11. 发送邮件, 发到所有的收件地址, message.getAllRecipients()获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
            transport.sendMessage(message, message.getAllRecipients());
            // 12. 关闭传输连接
            transport.close();
            return true;
        } catch (Exception e) {
            log.error("发送邮件失败,系统错误！", e);
            return false;
        }
    }

    //发送关于心理预警的
    public static boolean sendWarningEmail(String email, String title,String content) {
        // 未传收件人邮箱地址则直接返回
        if (email.isEmpty()){
            log.error("邮箱为空");
            return false;
        }
        //校验邮箱
        if (!ReUtil.isMatch(RegexConstant.EMAIL, email)) {
            log.error("邮箱格式错误");
            return false;
        }
        if(StrUtil.isBlank(content)){
            log.error("内容为空");
            return false;
        }
        return sendEmail(email,title,content);
    }
}
