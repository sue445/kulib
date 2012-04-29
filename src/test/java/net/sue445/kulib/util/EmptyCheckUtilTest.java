package net.sue445.kulib.util;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;


public class EmptyCheckUtilTest {
	@Test
	public void isEmptyCollection_Null(){
		assertThat(EmptyCheckUtil.isEmpty((List<?>)null), is(true));
	}

	@Test
	public void isEmptyCollection_Empty(){
		ArrayList<String> list = new ArrayList<String>();
		assertThat(EmptyCheckUtil.isEmpty(list), is(true));
	}

	@Test
	public void isEmptyCollection_NotEmpty(){
		ArrayList<String> list = new ArrayList<String>();
		list.add("aaa");
		assertThat(EmptyCheckUtil.isEmpty(list), is(false));
	}

	@Test
	public void isNotEmptyCollection_Null(){
		assertThat(EmptyCheckUtil.isNotEmpty((List<?>)null), is(false));
	}

	@Test
	public void isEmptyMap_Null(){
		assertThat(EmptyCheckUtil.isEmpty((Map<?,?>)null), is(true));
	}

	@Test
	public void isEmptyMap_Empty(){
		Map<String, String> map = new HashMap<String, String>();
		assertThat(EmptyCheckUtil.isEmpty(map), is(true));
	}

	@Test
	public void isEmptyMap_NotEmpty(){
		Map<String, String> map = new HashMap<String, String>();
		map.put("aaa", "bbb");
		assertThat(EmptyCheckUtil.isEmpty(map), is(false));
	}

	@Test
	public void isNotEmptyMap_Null(){
		assertThat(EmptyCheckUtil.isNotEmpty((Map<?,?>)null), is(false));
	}

	@Test
	public void isEmptyArray_Null(){
		assertThat(EmptyCheckUtil.isEmpty((String[])null), is(true));
	}

	@Test
	public void isEmptyArray_Empty(){
		assertThat(EmptyCheckUtil.isEmpty(new String[0]), is(true));
	}

	@Test
	public void isEmptyArray_NotEmpty(){
		assertThat(EmptyCheckUtil.isEmpty(new String[]{"1"}), is(false));
	}

	@Test
	public void isNotEmptyArray_Null(){
		assertThat(EmptyCheckUtil.isNotEmpty((String[])null), is(false));
	}
}

