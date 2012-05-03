package net.sue445.kulib.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.slim3.util.StringUtil;

/**
 * Utility for URL generate
 * @author sue445
 * @since 0.0.3
 */
public final class UrlGenerateUtil {
	private static final Logger logger = Logger.getLogger(UrlGenerateUtil.class.getName());

	/**
	 * default encoding (UTF-8)
	 */
	public static final Charset DEFAULT_ENCODING = Charset.forName("UTF-8");


	private UrlGenerateUtil(){

	}

	/**
	 * URL encode with default encoding. (ignore exception)
	 * @param str	source string
	 * @return
	 * <ul>
	 * <li>success: encoded string</li>
	 * <li>failed: source string</li>
	 * </ul>
	 */
	public static String urlEncode(String str){
		return urlEncode(str, DEFAULT_ENCODING);
	}

	/**
	 * URL encode. (ignore exception)
	 * @param str	source string
	 * @param encoding
	 * <ul>
	 * <li>success: encoded string</li>
	 * <li>failed: source string</li>
	 * </ul>
	 */
	public static String urlEncode(String str, Charset encoding){
		if(StringUtil.isEmpty(str)){
			return "";
		}

		try {
			return URLEncoder.encode(str, encoding.toString());
		} catch (UnsupportedEncodingException e) {
			logger.log(Level.WARNING, "url encode failed:" + str, e);
			return str;
		}
	}

	/**
	 * URL decode with default encoding. (ignore exception)
	 * @param str	source string
	 * @return
	 * <ul>
	 * <li>success: decoded string</li>
	 * <li>failed: source string</li>
	 * </ul>
	 */
	public static String urlDecode(String str){
		return urlDecode(str, DEFAULT_ENCODING);
	}

	/**
	 * URL decode. (ignore exception)
	 * @param str	source string
	 * @param encoding
	 * @return
	 * <ul>
	 * <li>success: decoded string</li>
	 * <li>failed: source string</li>
	 * </ul>
	 */
	public static String urlDecode(String str, Charset encoding){
		if(StringUtil.isEmpty(str)){
			return "";
		}

		try {
			return URLDecoder.decode(str, encoding.toString());
		} catch (UnsupportedEncodingException e) {
			logger.log(Level.WARNING, "url encode failed:" + str, e);
			return str;
		}
	}

	/**
	 * add parameter(ex. &name=value) to buffer<br>
	 * if name or value is empty, not append
	 * @param sb		destination
	 * @param name
	 * @param value
	 */
	public static void appendParameter(StringBuilder sb, String name, Object value){
		if(StringUtil.isEmpty(name) || value == null || value.toString().length() == 0){
			return;
		}

		if(sb.length() > 0){
			sb.append("&");
		}

		sb.append(name).append("=").append(urlEncode(value.toString()));
	}

	/**
	 * add parameters (ex. &name=value1&name=value2) to buffer
	 * @param sb		destination
	 * @param name
	 * @param values
	 */
	public static <T> void appendParameters(StringBuilder sb, String name, T[] values){
		if(EmptyCheckUtil.isEmpty(values)){
			return;
		}

		for(T value : values){
			appendParameter(sb, name, value);
		}
	}

	/**
	 * create url with parameter map
	 * @param baseUrl
	 * @param params
	 * @return
	 */
	public static String createUrl(String baseUrl, Map<String, Object> params){
		if(EmptyCheckUtil.isEmpty(params)){
			return baseUrl;
		}

		StringBuilder sb = new StringBuilder();
		for(Map.Entry<String, Object> entry : params.entrySet()){
			appendParameter(sb, entry.getKey(), entry.getValue());
		}

		return baseUrl + "?" + sb.toString();
	}
}
