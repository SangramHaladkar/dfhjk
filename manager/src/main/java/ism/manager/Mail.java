package ism.manager;

import java.util.Date;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import android.content.Context;

public class Mail extends javax.mail.Authenticator
{
	private String userName; 
	private String password; 

	private String[] emailTo; 
	private String emailFrom; 

	private String port; 
	private String socketFactoryPort; 

	private String hostSMTP; 
	private String emailSubject; 
	private String emailBody; 

	private boolean auth; 
	private boolean debuggable; 
	private Multipart multipart; 

	Context context;


	public Mail() 
	{ 
		try 
		{
			//hostSMTP = "smtp.gmail.com"; // default smtp server 
			//port = "465"; // default smtp port 
			//socketFactoryPort = "465"; // default socketfactory port 

			hostSMTP = "smtp.1and1.com"; // default smtp server 
			port = "465"; // default smtp port 
			socketFactoryPort = "465"; // default socketfactory port 

			userName = ""; // username 
			password = ""; // password 
			emailFrom = ""; // email sent from 
			emailSubject = ""; // email subject 
			emailBody = ""; // email body 

			debuggable = false; // debug mode on or off - default off 
			auth = true; // smtp authentication - default on 

			multipart = new MimeMultipart(); 

			MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap(); 
			mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html"); 
			mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml"); 
			mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain"); 
			mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed"); 
			mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822"); 
			CommandMap.setDefaultCommandMap(mc);
		} 
		catch (Exception e) 
		{
			Utility.writeErrorInDirectory(emailBody,context);
			e.printStackTrace();
		} 
	} 

	public Mail(String user, String pass, Context mContext) 
	{ 
		this(); 
		userName = user; 
		password = pass;
		context = mContext;
	} 

	public boolean send() throws Exception 
	{ 
		Properties props = setProperties(); 

		try 
		{
			if(!userName.equals("") && !password.equals("") && emailTo.length > 0 && !emailFrom.equals("") && !emailSubject.equals("") && !emailBody.equals("")) 
			{ 
				Session session = Session.getInstance(props, this); 

				MimeMessage msg = new MimeMessage(session); 

				msg.setFrom(new InternetAddress(emailFrom)); 

				InternetAddress[] addressTo = new InternetAddress[emailTo.length]; 
				for (int i = 0; i < emailTo.length; i++) 
				{ 
					addressTo[i] = new InternetAddress(emailTo[i]); 
				} 
				msg.setRecipients(MimeMessage.RecipientType.TO, addressTo); 
				msg.setSubject(emailSubject); 
				msg.setSentDate(new Date()); 

				// setup message body 
				BodyPart messageBodyPart = new MimeBodyPart(); 
				messageBodyPart.setText(emailBody); 
				multipart.addBodyPart(messageBodyPart); 

				// Put parts in message 
				msg.setContent(multipart); 

				// send email 
				Transport.send(msg);

				return true; 
			}
		}
		catch (Exception e) 
		{
			Utility.writeErrorInDirectory(emailBody,context);
			e.printStackTrace();
		} 
		return false; 
	} 

	/*	  public void addAttachment(String filename)
	  { 
	    try 
	    {
			BodyPart messageBodyPart = new MimeBodyPart(); 
			DataSource source = new FileDataSource(filename); 
			messageBodyPart.setDataHandler(new DataHandler(source)); 
			messageBodyPart.setFileName(filename); 

			multipart.addBodyPart(messageBodyPart);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	  } */

	@Override 
	public PasswordAuthentication getPasswordAuthentication() { 
		return new PasswordAuthentication(userName, password); 
	} 

	private Properties setProperties() 
	{ 
		Properties props = null;
		try 
		{
			props = new Properties(); 

			props.put("mail.smtp.host", hostSMTP); 

			if(debuggable) 
			{ 
				props.put("mail.debug", "true"); 
			} 
			if(auth) 
			{ 
				props.put("mail.smtp.auth", "true"); 
			} 
			props.put("mail.smtp.port", port); 
			props.put("mail.smtp.socketFactory.port", socketFactoryPort); 
			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); 
			props.put("mail.smtp.socketFactory.fallback", "false"); 

		} 
		catch (Exception e) 
		{
			Utility.writeErrorInDirectory(emailBody,context);
			e.printStackTrace();
		} 
		return props;
	}

	// the getters and setters

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String[] getEmailTo() {
		return emailTo;
	}

	public void setEmailTo(String[] emailTo) {
		this.emailTo = emailTo;
	}

	public String getEmailFrom() {
		return emailFrom;
	}

	public void setEmailFrom(String emailFrom) {
		this.emailFrom = emailFrom;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getSocketFactoryPort() {
		return socketFactoryPort;
	}

	public void setSocketFactoryPort(String socketFactoryPort) {
		this.socketFactoryPort = socketFactoryPort;
	}

	public String getHostSMTP() {
		return hostSMTP;
	}

	public void setHostSMTP(String hostSMTP) {
		this.hostSMTP = hostSMTP;
	}

	public String getEmailSubject() {
		return emailSubject;
	}

	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}

	public String getEmailBody() {
		return emailBody;
	}

	public void setEmailBody(String emailBody) {
		this.emailBody = emailBody;
	}

	public boolean isAuth() {
		return auth;
	}

	public void setAuth(boolean auth) {
		this.auth = auth;
	}

	public boolean isDebuggable() {
		return debuggable;
	}

	public void setDebuggable(boolean debuggable) {
		this.debuggable = debuggable;
	}

	public Multipart getMultipart() {
		return multipart;
	}

	public void setMultipart(Multipart multipart) {
		this.multipart = multipart;
	} 



}
