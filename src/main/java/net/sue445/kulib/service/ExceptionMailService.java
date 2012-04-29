package net.sue445.kulib.service;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.google.appengine.api.mail.MailService;
import com.google.appengine.api.mail.MailServiceFactory;


public class ExceptionMailService {
	protected static final Logger logger = Logger.getLogger(ExceptionMailService.class.getName());

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private static final String SEND_FROM = "sendFrom";
	private static final String KEY_SEND_TO = "sendTo";
	private static final String KEY_SUBJECT = "subject";
	private static final String DEFAULT_BUNDLE_NAME = "mail";
	private static final String IGNORE = "ignore";

	private final String bundleName;


	/**
	 * initialize as mail.properties
	 */
	public ExceptionMailService(){
		this.bundleName = DEFAULT_BUNDLE_NAME;
	}

	/**
	 *
	 * @param bundleName
	 */
	public ExceptionMailService(String bundleName){
		this.bundleName = bundleName;
	}

	/**
	 * send exception mail
	 * @param t
	 * @param request
	 * @return true:sended mail / false:not send mail(ex. Throwable is ignored)
	 */
	public boolean send(Throwable t, HttpServletRequest request){
		try {
			if(isIgnoreException(t)){
				return false;
			}

			MailService.Message msg = createMessage(t, request);

			MailService mailService = MailServiceFactory.getMailService();
			mailService.send(msg);

			logger.log(Level.INFO, "mail send: " + msg.getSender() + " -> " + msg.getTo());

			return true;

		} catch (Throwable e) {
			logger.log(Level.WARNING, "mail cannot send", e);
			return false;
		}
	}

	protected MailService.Message createMessage(Throwable t, HttpServletRequest request) {
		MailService.Message msg = new MailService.Message();
		msg.setSender(getString(SEND_FROM));
		msg.setTo(getString(KEY_SEND_TO));
		msg.setSubject(getString(KEY_SUBJECT));
		msg.setTextBody(createTextBody(t, request));

		if(logger.isLoggable(Level.FINEST)){
			logger.log(Level.FINEST, "sender=" + msg.getSender() + ", to=" + msg.getTo() + ", subject=" + msg.getSubject() + ", textBody=" + msg.getTextBody());
		}

		return msg;
	}

	/**
	 * send exception mail to admins
	 * @param t
	 * @param request
	 * @return true:sended mail / false:not send mail(ex. Throwable is ignored)
	 * @since 0.0.5
	 */
	public boolean sendToAdmins(Throwable t, HttpServletRequest request){
		try {
			if(isIgnoreException(t)){
				return false;
			}

			MailService.Message msg = createMessage(t, request);

			MailService mailService = MailServiceFactory.getMailService();
			mailService.sendToAdmins(msg);

			logger.log(Level.INFO, "mail send: " + msg.getSender() + " -> " + msg.getTo());

			return true;

		} catch (Throwable e) {
			logger.log(Level.WARNING, "mail cannot send", e);
			return false;
		}
	}

	/**
	 *
	 * @param t
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected String createTextBody(Throwable t, HttpServletRequest request) {
		StringBuilder sb = new StringBuilder();

		sb.append("[Header]").append(LINE_SEPARATOR);
		for (Enumeration<String> e = request.getHeaderNames(); e.hasMoreElements();){
			String name = e.nextElement();
			sb.append(name).append("=").append(request.getHeader(name)).append(LINE_SEPARATOR);
		}
		sb.append(LINE_SEPARATOR);
		sb.append("[StackTrace]").append(LINE_SEPARATOR);
		sb.append(getStackTraceMessage(t));

		return sb.toString();
	}

	/**
	 * get value in prop
	 * @param key
	 * @return
	 */
	protected String getString(String key) {
		try {
			ResourceBundle bundle = ResourceBundle.getBundle(bundleName);
			return bundle.getString(key);
		} catch (MissingResourceException e) {
			return null;
		}
	}

	/**
	 * exists key
	 * @param key
	 * @return
	 */
	protected boolean hasKey(String key){
		String value = getString(key);
		return value != null;
	}

	/**
	 *
	 * @param t
	 * @return
	 */
	protected boolean isIgnoreException(Throwable t){
		String sourceClassName = t.getClass().getName();

		String[] array = sourceClassName.split("\\.");
		LinkedList<String> list = new LinkedList<String>(Arrays.asList(array));

		while(!list.isEmpty()){
			String className = join(list, ".");
			String value = getString(className);

			if(value != null){
				return IGNORE.equals(value);
			}

			list.removeLast();
		}

		return false;
	}

	/**
	 *
	 * @param list
	 * @param separator
	 * @return
	 */
	protected String join(List<String> list, String separator){
		StringBuilder sb = new StringBuilder();

		boolean isFirst = true;
		for(String str : list){
			if(isFirst){
				isFirst = false;
			} else{
				sb.append(separator);
			}
			sb.append(str);
		}

		return sb.toString();
	}

	/**
	 *
	 * @param t
	 * @return
	 */
	protected String getStackTraceMessage(Throwable t){
		StringBuilder sb = new StringBuilder();

		sb.append(t).append(LINE_SEPARATOR);

		for(StackTraceElement e : t.getStackTrace()){
			sb.append("\t").append(e).append(LINE_SEPARATOR);
		}

		if(t.getCause() != null){
			sb.append("Caused by: ").append(getStackTraceMessage(t.getCause()));
		}

		return sb.toString();
	}
}
