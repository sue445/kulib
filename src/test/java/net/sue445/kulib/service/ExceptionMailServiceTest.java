package net.sue445.kulib.service;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import junit.framework.AssertionFailedError;

import org.junit.Test;
import org.slim3.tester.ControllerTestCase;

import com.google.appengine.api.mail.MailServicePb.MailMessage;
import com.google.apphosting.api.DeadlineExceededException;

public class ExceptionMailServiceTest extends ControllerTestCase {

	private ExceptionMailService service = new ExceptionMailService("ExceptionMailServiceTest");

	@Test
	public void test() throws Exception {
		assertThat(service, is(notNullValue()));
	}

	@Test
	public void getString() throws Exception {
		String actual = service.getString("subject");
		assertThat(actual, is("[ERROR] Kulib Error Mail"));
	}

	@Test
	public void hasKey_Found() throws Exception {
		boolean actual = service.hasKey("subject");
		assertThat(actual, is(true));
	}

	@Test
	public void hasKey_NotFound() throws Exception {
		boolean actual = service.hasKey("aaaaa");
		assertThat(actual, is(false));
	}

	@Test
	public void sendMail() throws Exception {
		tester.request.addHeader("name", "value");
		boolean actual = service.send(new IllegalAccessError("test"), tester.request);
		assertThat(actual, is(true));
		assertThat(tester.mailMessages.size(), is(1));

		MailMessage mailMessage = tester.mailMessages.get(0);
		assertThat(mailMessage.getSender(), is("UnitTest <sender@gmail.com>"));
		assertThat(mailMessage.getTo(0), is("sendto@gmail.com"));
		assertThat(mailMessage.getSubject(), is("[ERROR] Kulib Error Mail"));
		assertThat(mailMessage.getTextBody(), containsString("java.lang.IllegalAccessError: "));
	}

	@Test
	public void sendMail_Ignored() throws Exception {
		boolean actual = service.send(new DeadlineExceededException("test"), tester.request);
		assertThat(actual, is(false));
	}

	@Test
	public void isIgnoreException_CompleteMatch_Ignore() throws Exception {
		Throwable t = new DeadlineExceededException("test");
		boolean actual = service.isIgnoreException(t);

		assertThat(actual, is(true));
	}

	@Test
	public void isIgnoreException_CompleteMatch_NotIgnore() throws Exception {
		Throwable t = new IllegalArgumentException("test");
		boolean actual = service.isIgnoreException(t);

		assertThat(actual, is(false));
	}

	@Test
	public void isIgnoreException_LeftHandMatch_Ignore() throws Exception {
		Throwable t = new AssertionFailedError("test");
		boolean actual = service.isIgnoreException(t);

		assertThat(actual, is(true));
	}

	@Test
	public void join() throws Exception {
		List<String> list = Arrays.asList("aa", "bb", "cc");
		String actual = service.join(list, ".");

		assertThat(actual, is("aa.bb.cc"));
	}

	@Test
	public void getStackTraceMessage() throws Exception {
		try{
			throw new IllegalArgumentException("test");

		} catch (Exception e) {
			String actual = service.getStackTraceMessage(e);
			assertThat(actual, startsWith("java.lang.IllegalArgumentException: test"));
		}
	}

	@Test
	public void getStackTraceMessage_HasCause() throws Exception {
		try{
			throw new IllegalArgumentException("test", new NullPointerException("cause"));

		} catch (Exception e) {
			String actual = service.getStackTraceMessage(e);
			assertThat(actual, containsString("Caused by: java.lang.NullPointerException"));
		}
	}

	@Test
	public void sendMailToAdmins() throws Exception {
		tester.request.addHeader("name", "value");
		boolean actual = service.sendToAdmins(new IllegalAccessError("test"), tester.request);
		assertThat(actual, is(true));
		assertThat(tester.mailMessages.size(), is(1));

		MailMessage mailMessage = tester.mailMessages.get(0);
		assertThat(mailMessage.getSender(), is("UnitTest <sender@gmail.com>"));
		assertThat(mailMessage.getTo(0), is("sendto@gmail.com"));
		assertThat(mailMessage.getSubject(), is("[ERROR] Kulib Error Mail"));
		assertThat(mailMessage.getTextBody(), containsString("java.lang.IllegalAccessError: "));
	}

}
