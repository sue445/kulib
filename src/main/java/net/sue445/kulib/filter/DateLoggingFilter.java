package net.sue445.kulib.filter;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slim3.util.DateUtil;
import org.slim3.util.StringUtil;

/**
 * print timestamp when controller begin and end.
 * example
 * <pre>
 *     &lt;filter&gt;
 *         &lt;filter-name&gt;DateLoggingFilter&lt;/filter-name&gt;
 *         &lt;filter-class&gt;net.sue445.kulib.filter.DateLoggingFilter&lt;/filter-class&gt;
 *         &lt;init-param&gt;
 *             &lt;param-name&gt;timestampFormat&lt;/param-name&gt;
 *             &lt;param-value&gt;yyyy/MM/dd HH:mm:ss.SSS z&lt;/param-value&gt;
 *         &lt;/init-param&gt;
 *         &lt;init-param&gt;
 *             &lt;param-name&gt;timeZone&lt;/param-name&gt;
 *             &lt;param-value&gt;Asia/Tokyo&lt;/param-value&gt;
 *         &lt;/init-param&gt;
 *     &lt;/filter&gt;
 *
 *     &lt;!-- insert this before FrontController --&gt;
 *     &lt;filter-mapping&gt;
 *         &lt;filter-name&gt;DateLoggingFilter&lt;/filter-name&gt;
 *         &lt;url-pattern&gt;/*&lt;/url-pattern&gt;
 *         &lt;dispatcher&gt;REQUEST&lt;/dispatcher&gt;
 *     &lt;/filter-mapping&gt;
 *
 *     &lt;filter-mapping&gt;
 *         &lt;filter-name&gt;FrontController&lt;/filter-name&gt;
 *         ....
 *
 * </pre>
 * @author sue445
 *
 */
public class DateLoggingFilter implements Filter {
	protected static final Logger logger = Logger.getLogger(DateLoggingFilter.class.getName());

	private static final String TIMESTAMP_FORMAT_KEY = "timestampFormat";
	private static final String TIME_ZONE_KEY = "timeZone";

	private String timestampFormat;

	private TimeZone timeZone;


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(FilterConfig config) throws ServletException {
		initTimestampFormat(config);
		initTimeZone(config);

		if(logger.isLoggable(Level.FINEST)){
			logger.log(Level.FINEST, "Initialized :timestampFormat=" + timestampFormat + ",timeZone=" + timeZone.getDisplayName());
		}
	}

	/**
	 * initialize timestamp format with &lt;init-param&gt; (default: yyyy-MM-dd'T'HH:mm:ss)
	 * @param config
	 */
	private void initTimestampFormat(FilterConfig config) {
		String timestampFormat = config.getInitParameter(TIMESTAMP_FORMAT_KEY);
		if(StringUtil.isEmpty(timestampFormat)){
			this.timestampFormat = DateUtil.ISO_DATE_TIME_PATTERN;
		} else{
			this.timestampFormat = timestampFormat;
		}
	}

	/**
	 * initialize timezone format with &lt;init-param&gt;
	 * @param config
	 */
	private void initTimeZone(FilterConfig config){
		String timeZone = config.getInitParameter(TIME_ZONE_KEY);
		if(StringUtil.isEmpty(timeZone)){
			this.timeZone = TimeZone.getDefault();
		} else{
			this.timeZone = TimeZone.getTimeZone(timeZone);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void destroy() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		DateFormat df = createDateFormat();

		printStartLog(df);

		long startTime = System.currentTimeMillis();

		try {
			chain.doFilter(request, response);

		} finally{
			printEndLog(df, startTime);
		}
	}

	/**
	 *
	 * @return
	 */
	private DateFormat createDateFormat() {
		DateFormat df = new SimpleDateFormat(this.timestampFormat);
		df.setTimeZone(this.timeZone);
		return df;
	}

	/**
	 *
	 * @param df
	 */
	private void printStartLog(DateFormat df) {
		if(logger.isLoggable(Level.FINEST)){
			String currentTime = createCurrentTime(df);
			logger.log(Level.FINEST, currentTime);
		}
	}

	/**
	 *
	 * @param df
	 * @param startTime
	 */
	private void printEndLog(DateFormat df, long startTime) {
		if(logger.isLoggable(Level.FINEST)){
			String currentTime = createCurrentTime(df);
			long endTime = System.currentTimeMillis();

			StringBuilder sb = new StringBuilder();
			sb.append(currentTime);
			sb.append(" (");
			sb.append(endTime - startTime);
			sb.append("ms)");

			logger.log(Level.FINEST, sb.toString());
		}
	}

	/**
	 *
	 * @return
	 */
	private String createCurrentTime(DateFormat df) {
		return df.format(new Date());
	}

	/**
	 * @return timestampFormat
	 */
	protected String getTimestampFormat() {
		return timestampFormat;
	}

	/**
	 * @return timeZone
	 */
	protected TimeZone getTimeZone() {
		return timeZone;
	}

}
