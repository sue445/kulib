package net.sue445.kulib.util;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Test;
import org.slim3.tester.AppEngineTestCase;


public class MemcacheUtilTest extends AppEngineTestCase{

	@Test
	public void getCurrentVersionId(){
		assertThat(MemcacheUtil.getCurrentVersionId(), is("1.0"));
	}

	@Test
	public void createKeyPrefix(){
		String actual = MemcacheUtil.createKeyPrefix(MemcacheUtilTest.class);
		assertThat(actual, is("1.0_MemcacheUtilTest_"));
	}

}
