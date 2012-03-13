package net.sue445.kulib.service;

import java.io.IOException;
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


public class AlertMailService {
	protected static final Logger logger = Logger.getLogger(AlertMailService.class.getName());

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private static final String SEND_FROM = "sendFrom";
	private static final String KEY_SEND_TO = "sendTo";
	private static final String KEY_SUBJECT = "subject";
	private static final String DEFAULT_BUNDLE_NAME = "mail";
	private static final String IGNORE = "ignore";

	private final String bundleName;


	/**
	 * mail.propertiesで初期化する
	 */
	public AlertMailService(){
		this.bundleName = DEFAULT_BUNDLE_NAME;
	}

	/**
	 *
	 * @param bundleName	プロパティファイルから拡張子を除いたもの
	 */
	public AlertMailService(String bundleName){
		this.bundleName = bundleName;
	}

	/**
	 * エラーメールを送る
	 * @param t
	 * @param request
	 * @throws IOException
	 * @return true:メールを送信した / false:除外リストに登録されていたので送信していない
	 */
	public boolean sendMail(Throwable t, HttpServletRequest request){
		try {
			if(isIgnoreException(t)){
				return false;
			}

			MailService.Message msg = new MailService.Message();
			msg.setSender(getString(SEND_FROM));
			msg.setTo(getString(KEY_SEND_TO));
			msg.setSubject(getString(KEY_SUBJECT));
			msg.setTextBody(createTextBody(t, request));

			if(logger.isLoggable(Level.FINEST)){
				logger.log(Level.FINEST, "sender=" + msg.getSender() + ", to=" + msg.getTo() + ", subject=" + msg.getSubject() + ", textBody=" + msg.getTextBody());
			}

			MailService mailService = MailServiceFactory.getMailService();
			mailService.send(msg);

			logger.log(Level.INFO, "mail send: " + msg.getSender() + " -> " + msg.getTo());

			return true;

		} catch (Throwable e) {
			logger.log(Level.WARNING, "mail cannot send", e);
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	private String createTextBody(Throwable t, HttpServletRequest request) {
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
	 * プロパティファイルからkeyに対応したvalueを取得する
	 * @param key
	 * @return
	 */
	// package private
	String getString(String key) {
		try {
			ResourceBundle bundle = ResourceBundle.getBundle(bundleName);
			return bundle.getString(key);
		} catch (MissingResourceException e) {
			return null;
		}
	}

	/**
	 * keyがあるかどうか
	 * @param key
	 * @return
	 */
	// package private
	boolean hasKey(String key){
		String value = getString(key);
		return value != null;
	}

	/**
	 * 無視するエラーかどうか
	 * @param t
	 * @return
	 */
	// package private
	boolean isIgnoreException(Throwable t){
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
	// package private
	String join(List<String> list, String separator){
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

	// package private
	String getStackTraceMessage(Throwable t){
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
