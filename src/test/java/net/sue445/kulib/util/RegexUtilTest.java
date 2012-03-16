package net.sue445.kulib.util;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;


public class RegexUtilTest{
	@Test
	public void getSubexpressionGroup_Match() throws Exception{
		String regex = "@([A-Za-z]+)\\s(.+)";
		String str = "   @kinenbitter 教えて";

		List<String> actual = RegexUtil.getSubexpressionGroup(regex, str);
		assertThat(actual, contains("@kinenbitter 教えて", "kinenbitter", "教えて"));
	}

	@Test
	public void getSubexpressionGroup_NotMatch() throws Exception{
		String regex = "@([A-Za-z]+)\\s(.+)";
		String str = "   @kinenbitter";

		List<String> actual = RegexUtil.getSubexpressionGroup(regex, str);

		assertThat(actual.size(), is(0));
	}
	
	@Test
	public void isInexactMatch() throws Exception {
		String regex = "@([A-Za-z]+)\\s(.+)";
		String str = "   @kinenbitter 教えて";
		
		boolean actual = RegexUtil.isInexactMatch(regex, str);
		assertThat(actual, is(true));
	}
}
