package net.sue445.kulib.util;


import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;
import org.slim3.tester.AppEngineTestCase;

public class UrlGenerateUtilTest extends AppEngineTestCase{
	private static final Charset UTF8 = Charset.forName("UTF-8");
	private static final Charset SHIFTJIS = Charset.forName("Shift_JIS");

	@Test
	public void appendParameter1(){
		StringBuilder sb = new StringBuilder();
		UrlGenerateUtil.appendParameter(sb, "name", "value");
		assertThat(sb.toString(), is("name=value"));
	}

	@Test
	public void appendParameter2(){
		StringBuilder sb = new StringBuilder();
		sb.append("name1=value1");
		UrlGenerateUtil.appendParameter(sb, "name2", "value2");
		assertThat(sb.toString(), is("name1=value1&name2=value2"));
	}

	@Test
	public void appendParameter3(){
		StringBuilder sb = new StringBuilder();
		UrlGenerateUtil.appendParameter(sb, "", "value");
		assertThat(sb.toString(), is(""));
	}

	@Test
	public void appendParameter4(){
		StringBuilder sb = new StringBuilder();
		UrlGenerateUtil.appendParameter(sb, "name", "");
		assertThat(sb.toString(), is(""));
	}

	@Test
	public void appendParameter5(){
		StringBuilder sb = new StringBuilder();
		UrlGenerateUtil.appendParameter(sb, "name", null);
		assertThat(sb.toString(), is(""));
	}

	@Test
	public void appendParameter6(){
		StringBuilder sb = new StringBuilder();
		UrlGenerateUtil.appendParameter(sb, "name", "福岡");
		assertThat(sb.toString(), is("name=%E7%A6%8F%E5%B2%A1"));
	}

	@Test
	public void appendParameters1(){
		StringBuilder sb = new StringBuilder();
		UrlGenerateUtil.appendParameters(sb, "name", new String[]{});
		assertThat(sb.toString(), is(""));
	}

	@Test
	public void appendParameters2(){
		StringBuilder sb = new StringBuilder();
		UrlGenerateUtil.appendParameters(sb, "name", new String[]{"value1", "value2"});
		assertThat(sb.toString(), is("name=value1&name=value2"));
	}

	@Test
	public void urlEncode1(){
		assertThat(UrlGenerateUtil.urlEncode("福岡"), is("%E7%A6%8F%E5%B2%A1"));
	}

	@Test
	public void urlEncode2(){
		assertThat(UrlGenerateUtil.urlEncode("福岡", UTF8), is("%E7%A6%8F%E5%B2%A1"));
	}

	@Test
	public void urlEncode4(){
		assertThat(UrlGenerateUtil.urlEncode(""), is(""));
	}

	@Test
	public void urlEncode5(){
		assertThat(UrlGenerateUtil.urlEncode(null), is(""));
	}

	@Test
	public void urlEncode6(){
		assertThat(UrlGenerateUtil.urlEncode("福岡", SHIFTJIS), is("%95%9F%89%AA"));
	}

	@Test
	public void createUrl_NoParam(){
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		String actual = UrlGenerateUtil.createUrl("http://www.sue445.net/", params);
		assertThat(actual, is("http://www.sue445.net/"));
	}

	@Test
	public void createUrl_WithParam(){
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("key1", "value1");
		params.put("key2", "value2");
		String actual = UrlGenerateUtil.createUrl("http://www.sue445.net/", params);
		assertThat(actual, is("http://www.sue445.net/?key1=value1&key2=value2"));
	}

	@Test
	public void urlDecode() throws Exception {
		String actual = UrlGenerateUtil.urlDecode("advent%2bcalendar", UTF8);
		assertThat(actual, is("advent+calendar"));
	}
}
