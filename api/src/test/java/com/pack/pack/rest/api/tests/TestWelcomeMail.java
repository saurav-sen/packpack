package com.pack.pack.rest.api.tests;

import com.pack.pack.markup.gen.MarkupGenerator;
import com.pack.pack.services.ext.email.SmtpMessage;
import com.pack.pack.services.ext.email.SmtpTLSMessageService;

public class TestWelcomeMail {

	public static void main(String[] args) throws Exception {
		String htmlContent = MarkupGenerator.INSTANCE
				.generateWelcomeEmailHtmlContent("Rituparna Sen", 5,
						"http://www.squill.co.in/");
		SmtpMessage smtpMessage = new SmtpMessage("ritgho@gmail.com",
				"Welcome To SQUILL", htmlContent, true);
		SmtpTLSMessageService.INSTANCE.sendMessage(smtpMessage);
		System.out.println("Done");
	}

}
