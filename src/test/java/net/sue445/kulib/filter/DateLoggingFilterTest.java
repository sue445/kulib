package net.sue445.kulib.filter;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.junit.Test;
import org.slim3.tester.ControllerTestCase;
import org.slim3.tester.MockFilterChain;
import org.slim3.tester.MockFilterConfig;


public class DateLoggingFilterTest extends ControllerTestCase{
	private final DateLoggingFilter filter = new DateLoggingFilter();

	private final MockFilterConfig filterConfig = new MockFilterConfig(tester.servletContext);


	@Test
	public void initTimestampFormat1() throws Exception{
		setTimestampFormat("");
		filter.init(filterConfig);
		assertThat(filter.getTimestampFormat(), is("yyyy-MM-dd'T'HH:mm:ss"));
	}

	private void setTimestampFormat(String str) {
		filterConfig.setInitParameter("timestampFormat", str);
	}

	@Test
	public void initTimestampFormat2() throws Exception{
		setTimestampFormat("yyyy-MM-dd");
		filter.init(filterConfig);
		assertThat(filter.getTimestampFormat(), is("yyyy-MM-dd"));
	}

	@Test
	public void initTimeZone1() throws Exception{
		setTimeZone("");
		filter.init(filterConfig);
		assertThat(filter.getTimeZone(), is(TimeZone.getDefault()));
	}

	private void setTimeZone(String str) {
		filterConfig.setInitParameter("timeZone", str);
	}

	@Test
	public void initTimeZone2() throws Exception{
		setTimeZone("JST");
		filter.init(filterConfig);
		assertThat(filter.getTimeZone(), is(TimeZone.getTimeZone("JST")));
	}

	@Test
	public void destroy(){
		filter.destroy();
	}

	@Test
	public void doFilter() throws Exception{
		DummyChain filterChain = new DummyChain();
		setTimestampFormat("yyyy-MM-dd");
		setTimeZone("JST");
		filter.init(filterConfig);
		filter.doFilter(tester.request, tester.response, filterChain);

		assertThat(filterChain.isRun, is(true));
	}

	private static class DummyChain extends MockFilterChain{
		public boolean isRun = false;

		@Override
		public void doFilter(ServletRequest request, ServletResponse response)
				throws IOException, ServletException {
			super.doFilter(request, response);
			isRun = true;
		}

	}
}
