package com.xforceplus.wapp.common.utils;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Send Email Utils
 *
 * @author Simba Liu
 */
public class MailUtils {

    private static String MAIL_FROM_EAMIL;
    private static String MAIL_FROM_NAME;
    private static String MAIL_SMTP_HOST;
    private static int MAIL_SMTP_PORT;
    private static Boolean MAIL_SMTP_AUTH;
    private static String MAIL_SMTP_USERNAME;
    private static String MAIL_SMTP_PASSWORD;
    private static String MAIL_ENCODING;
    private static Boolean MAIL_SMTP_SSL;

    static {
        try {
            /*String path = MailUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            int firstIndex = path.lastIndexOf(System.getProperty("path.separator")) + 1;
            int lastIndex = path.lastIndexOf(File.separator) + 1;
            String filePath = path.substring(firstIndex, lastIndex) + "dqci.properties";*/
            //System.out.println(filePath);
            PropertiesConfiguration config = new PropertiesConfiguration("config.properties");
            //System.out.println(config.getString("mail.from.alias"));
            MAIL_FROM_EAMIL = config.getString("mail.smtp.from", "cnpipeline@wal-mart.com");
            MAIL_FROM_NAME = config.getString("mail.from.alias", "CNPipeline");
            MAIL_SMTP_HOST = config.getString("mail.smtp.host", "exchange.cn.wal-mart.com");
            MAIL_SMTP_PORT = config.getInt("mail.smtp.port", 25);
            MAIL_SMTP_AUTH = config.getBoolean("mail.smtp.auth", false);
            MAIL_SMTP_USERNAME = config.getString("mail.smtp.username", "cnpipeline");
            MAIL_SMTP_PASSWORD = config.getString("mail.smtp.password", "cnpipeline");
            MAIL_ENCODING = config.getString("mail.encoding", "GBK");
            MAIL_SMTP_SSL = config.getBoolean("mail.smtp.ssl", false);
            System.out.println("Load email properties.");
        } catch (Exception e) {
            System.err.println("Can't read properties! " + e.getMessage());
        }
    }

    public static void sendSimpleEmail(String toEmail, String subject, String message)
            throws Exception {
        sendSimpleEmail(toEmail, null, null, subject, message);
    }

    public static void sendSimpleEmail(String toEmail, String ccEmail, String bccEmail,
                                       String subject, String message) throws Exception {
        try {
            Email email = new SimpleEmail();
            email.setHostName(MAIL_SMTP_HOST);
            email.setSmtpPort(MAIL_SMTP_PORT);
            email.setCharset(MAIL_ENCODING);
            if (MAIL_SMTP_AUTH) {
                email.setAuthentication(MAIL_SMTP_USERNAME, MAIL_SMTP_PASSWORD);
            }
            email.setSSLOnConnect(MAIL_SMTP_SSL);
            email.setFrom(MAIL_FROM_EAMIL, MAIL_FROM_NAME);

            for (String to : toEmail.split(",")) {
                email.addTo(to);
            }
            if (StringUtils.isNotBlank(ccEmail)) {
                email.addCc(ccEmail);
            }
            if (StringUtils.isNotBlank(bccEmail)) {
                email.addBcc(bccEmail);
            }
            email.setSubject(subject);
            email.setMsg(message);
            email.send();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(
                    String.format(
                            "Can't send email: Host:%s, Port:%d, User:%s, From:%s, To:%s, Subject:%s, Message:%s",
                            MAIL_SMTP_HOST, MAIL_SMTP_PORT, MAIL_SMTP_USERNAME, MAIL_FROM_EAMIL,
                            toEmail, subject, message),
                    e);
        }
    }

    public static void sendHtmlEmail(String toEmail, String ccEmail, String bccEmail,
                                     String subject, String message)
            throws Exception {
        try {
            HtmlEmail email = new HtmlEmail();
            email.setHostName(MAIL_SMTP_HOST);
            email.setSmtpPort(MAIL_SMTP_PORT);
            email.setCharset(MAIL_ENCODING);
            if (MAIL_SMTP_AUTH) {
                email.setAuthentication(MAIL_SMTP_USERNAME, MAIL_SMTP_PASSWORD);
            }
            email.setSSLOnConnect(MAIL_SMTP_SSL);
            email.setFrom(MAIL_FROM_EAMIL, MAIL_FROM_NAME);

            for (String to : toEmail.split(",")) {
                email.addTo(to);
            }
            if (StringUtils.isNotBlank(ccEmail)) {
                email.addCc(ccEmail);
            }
            if (StringUtils.isNotBlank(bccEmail)) {
                email.addBcc(bccEmail);
            }
            email.setSubject(subject);
            email.setHtmlMsg(message);
            email.send();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(
                    String.format(
                            "Can't send email: Host:%s, Port:%d, User:%s, From:%s, To:%s, Subject:%s, Message:%s",
                            MAIL_SMTP_HOST, MAIL_SMTP_PORT, MAIL_SMTP_USERNAME, MAIL_FROM_EAMIL,
                            toEmail, subject, message),
                    e);
        }
    }

    public static void sendEmailWithAttachment(String toEmail, String ccEmail, String bccEmail,
                                               String subject, String message, File attachment)
            throws Exception {
        try {
            MultiPartEmail email = new MultiPartEmail();
            email.setHostName(MAIL_SMTP_HOST);
            email.setSmtpPort(MAIL_SMTP_PORT);
            email.setCharset(MAIL_ENCODING);
            if (MAIL_SMTP_AUTH) {
                email.setAuthentication(MAIL_SMTP_USERNAME, MAIL_SMTP_PASSWORD);
            }
            email.setSSLOnConnect(MAIL_SMTP_SSL);
            email.setFrom(MAIL_FROM_EAMIL, MAIL_FROM_NAME);
            for (String to : toEmail.split(",")) {
                email.addTo(to);
            }
            if (StringUtils.isNotBlank(ccEmail)) {
                email.addCc(ccEmail);
            }
            if (StringUtils.isNotBlank(bccEmail)) {
                email.addBcc(bccEmail);
            }
            email.setSubject(subject);
            email.setMsg(message);
            email.attach(attachment);
            email.send();
        } catch (Exception e) {
            throw new RuntimeException(
                    String.format(
                            "Can't send email: Host:%s, Port:%d, User:%s, From:%s, To:%s, Subject:%s, Message:%s",
                            MAIL_SMTP_HOST, MAIL_SMTP_PORT, MAIL_SMTP_USERNAME, MAIL_FROM_EAMIL,
                            toEmail, subject, message),
                    e);
        }
    }

    /**
     * Validate Email address
     *
     * @param email Email address
     * @return
     */
    public static boolean validateEmail(String email) {
        if (StringUtils.isBlank(email))
            return false;
        String regex = "^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}
