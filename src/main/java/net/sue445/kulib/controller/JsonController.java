package net.sue445.kulib.controller;

import java.io.IOException;
import java.nio.charset.Charset;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.controller.SimpleController;
import org.slim3.util.StringUtil;

/**
 * A {@link Controller} which is print response as JSON or JSONP
 * <pre>
 * example
 * <code>
 * public Navigation run() throws Exception {
 *     String json = ... ;
 *     String jsonp = toJsonp(json);
 *     return responseJson(jsonp);
 * }
 * </code>
 * </pre>
 * @author sue445
 * @since 0.0.4
 */
public abstract class JsonController extends SimpleController{
	protected static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

	protected static final String CONTENT_TYPE_PREFIX = "text/javascript;charset=";

	/**
	 * JSONP callback
	 */
	protected String callback;

	/* (非 Javadoc)
	 * @see org.slim3.controller.Controller#setUp()
	 */
	@Override
	protected Navigation setUp() {
		callback = param("callback");
		return super.setUp();
	}

	/**
	 * if exists callback, add this to json content
	 * @param jsonContent
	 * @return
	 */
	protected String toJsonp(String jsonContent){
		if(StringUtil.isEmpty(callback)){
			return jsonContent;
		}
		return callback + "(" + jsonContent + ")";
	}

	/**
	 * print json response(UTF-8) and finish contoller
	 * @param jsonContent
	 * @return
	 * @throws IOException
	 */
	protected Navigation responseJson(String jsonContent) throws IOException {
		return responseJson(jsonContent, DEFAULT_CHARSET);
	}

	/**
	 * print json response and finish contoller
	 * @param jsonContent
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	protected Navigation responseJson(String jsonContent, Charset charset) throws IOException {
		return response(jsonContent, charset, CONTENT_TYPE_PREFIX + charset.toString());
	}

	/**
	 * print string to response
	 * @param content
	 * @param charset
	 * @param contentType
	 * @return
	 * @throws IOException
	 */
	protected Navigation response(String content, Charset charset, String contentType) throws IOException {
		byte[] bytes = content.getBytes(charset);
		response.setContentType(contentType);
		response.setContentLength(bytes.length);
		response.getOutputStream().write(bytes);
		response.flushBuffer();
		return null;
	}
}
