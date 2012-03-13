package net.sue445.kulib.util;


import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Arrays;

import net.sue445.kulib.util.CompressMemcacheUtil;

import org.junit.Test;
import org.slim3.memcache.Memcache;
import org.slim3.tester.AppEngineTestCase;

import com.google.appengine.api.memcache.Expiration;


public class CompressMemcacheUtilTest extends AppEngineTestCase{

	private static final String TEST_DATA = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
	private static final String KEY = "key";

	@Test
	public void serialize() throws Exception {
		byte[] actual = CompressMemcacheUtil.serialize(TEST_DATA);

		assertThat(actual, is(notNullValue()));
		assertThat(actual.length, is(greaterThan(0)));
		//printBytes(actual);
	}

	@SuppressWarnings("unused")
	private void printBytes(byte[] src) {
		System.out.println("length=" + src.length + ", " + Arrays.toString(src));
	}

	@Test
	public void serialize_Null() throws Exception {
		byte[] actual = CompressMemcacheUtil.serialize(null);

		assertThat(actual, is(nullValue()));
	}

	@Test
	public void serializeAndDeserialize() throws Exception {
		byte[] src = CompressMemcacheUtil.serialize(TEST_DATA);

		String actual = CompressMemcacheUtil.deserialize(src);
		assertThat(actual, is(TEST_DATA));
	}

	@Test
	public void deserialize_Null() throws Exception {
		String actual = CompressMemcacheUtil.deserialize(null);
		assertThat(actual, is(nullValue()));
	}

	@Test
	public void compress() throws Exception {
		byte[] actual = CompressMemcacheUtil.compress(TEST_DATA.getBytes());

		assertThat(actual.length, is(greaterThan(0)));
		assertThat(actual.length, is(lessThan(TEST_DATA.length())));
		//System.out.println("str=" + TEST_DATA.length());
		//printBytes(actual);
	}

	@Test
	public void compressAndUncompress() throws Exception {
		byte[] src = CompressMemcacheUtil.compress(TEST_DATA.getBytes());

		byte[] actual = CompressMemcacheUtil.uncompress(src);
		assertThat(actual, is(TEST_DATA.getBytes()));
	}

	@Test
	public void compressAndUncompress_1MBover() throws Exception {
		StringBuilder sb = new StringBuilder();
		int length = 1024*1024*3;
		for(int i = 0; i < length; i++){
			sb.append("a");
		}

		String str = sb.toString();
		byte[] src = CompressMemcacheUtil.compress(str.getBytes());

		byte[] actual = CompressMemcacheUtil.uncompress(src);
		assertThat(actual.length, is(str.getBytes().length));
		assertThat(actual, is(str.getBytes()));
	}


	@Test
	public void put() throws Exception {
		CompressMemcacheUtil.put(KEY, TEST_DATA);

		byte[] actual = Memcache.get(KEY);
		assertThat(actual, is(notNullValue()));
		assertThat(actual.length, is(greaterThan(0)));
		//printBytes(actual);
	}

	@Test
	public void putAndGet() throws Exception {
		CompressMemcacheUtil.put(KEY, TEST_DATA);

		String actual = CompressMemcacheUtil.get(KEY);
		assertThat(actual, is(TEST_DATA));
	}

	@Test
	public void get_NotFound() throws Exception {
		Object actual = CompressMemcacheUtil.get(KEY);
		assertThat(actual, is(nullValue()));
	}

	@Test
	public void put_Null() throws Exception {
		CompressMemcacheUtil.put(KEY, null);
	}

	@Test
	public void putExpirationAndGet() throws Exception {
		CompressMemcacheUtil.put(KEY, TEST_DATA, Expiration.byDeltaSeconds(60*10));

		String actual = CompressMemcacheUtil.get(KEY);
		assertThat(actual, is(TEST_DATA));
	}

}
