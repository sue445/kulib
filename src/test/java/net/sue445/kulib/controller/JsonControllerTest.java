package net.sue445.kulib.controller;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.slim3.tester.ControllerTestCase;
import org.slim3.tester.ControllerTester;

@RunWith(Enclosed.class)
public class JsonControllerTest {

	public static class WhenNoCallback extends ControllerTestCase{
		private JsonController controller;

		@Override
		public void setUp() throws Exception {
			super.setUp();
			controller = startControllerBase(tester);
		}

		@Override
		public void tearDown() throws Exception{
			assertControllerBase(tester, controller);
			tester.tearDown();
		}

		@Test
		public void run() throws Exception {
		}

		@Test
		public void toJsonp() throws Exception {
			String jsonContent = "aaa";
			String actual = controller.toJsonp(jsonContent);

			assertThat(actual, is("aaa"));
		}

		@Test
		public void responseJson() throws Exception {
			String jsonContent = "aaa";
			controller.responseJson(jsonContent);

			assertJsonContent(jsonContent, "UTF-8");
		}

		private void assertJsonContent(String expected, String encoding)
				throws IOException {
			int length = expected == null ? 0 : expected.length();

			assertThat(tester.response.getContentLength(), is(length));
			assertThat(tester.response.getContentType(), is("text/javascript;charset=" + encoding));
			assertThat(tester.response.getCharacterEncoding(), is(encoding));
			String actual = getResponse();
			assertThat(actual, is(expected));
		}

		@Test
		public void responseJson_SJIS() throws Exception {
			String jsonContent = "aaa";
			controller.responseJson(jsonContent, Charset.forName("Shift_JIS"));

			assertJsonContent(jsonContent, "Shift_JIS");
		}

		protected String getResponse() throws IOException {
			return tester.response.getOutputAsString();
		}

		@Test
		public void responseJson_Null() throws Exception {
			String jsonContent = null;
			controller.responseJson(jsonContent);

			assertJsonContent("", "UTF-8");
		}
	}

	public static class WhenHasCallback extends ControllerTestCase{
		private JsonController controller;

		@Override
		public void setUp() throws Exception {
			super.setUp();
			tester.param("callback", "testCallback");
			controller = startControllerBase(tester);
		}

		@Override
		public void tearDown() throws Exception{
			assertControllerBase(tester, controller);
			tester.tearDown();
		}

		@Test
		public void testSetUp() throws Exception {
			controller.setUp();
			assertThat(controller.callback, is("testCallback"));
		}

		@Test
		public void toJsonp() throws Exception {
			String jsonContent = "aaa";
			String actual = controller.toJsonp(jsonContent);

			assertThat(actual, is("testCallback(aaa)"));
		}
	}

	private static JsonController startControllerBase(ControllerTester tester) throws Exception {
		tester.start("/dummy");
		DummyController controller = tester.getController();
		assertThat(controller, is(notNullValue()));
		return controller;
	}

	private static void assertControllerBase(ControllerTester tester, JsonController controller) {
		assertThat(tester.isRedirect(), is(false));
		assertThat(tester.getDestinationPath(), is(nullValue()));

		int statusCode = tester.response.getStatus();
		assertThat(statusCode, is(HttpServletResponse.SC_OK));
	}


}
