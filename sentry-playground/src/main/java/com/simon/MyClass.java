package com.simon;

import java.util.UUID;

import io.sentry.Sentry;
import io.sentry.SentryClient;
import io.sentry.SentryClientFactory;
import io.sentry.context.Context;
import io.sentry.event.BreadcrumbBuilder;
import io.sentry.event.UserBuilder;

/**
 * https://sentry.io/liberty-mutual/java/#welcome
 * 
 * For DSN, see:
 * https://sentry.io/liberty-mutual/java/settings/keys/
 * 
 * @author Simon
 */
public class MyClass {
	private static SentryClient sentry;

	public static void main(String... args) {
		
		/*
		 * It is recommended that you use the DSN detection system, which will check the
		 * environment variable "SENTRY_DSN", the Java System Property "sentry.dsn", or
		 * the "sentry.properties" file in your classpath. This makes it easier to
		 * provide and adjust your DSN without needing to change your code. See the
		 * configuration page for more information.
		 */
		//Sentry.init();

		// You can also manually provide the DSN to the ``init`` method.
		// String dsn = ""; //args[0];
		Sentry.init("https://3895462286c24cf3ab8bbe4e083b18bc:eee24ba8c5af40fdbb1bd2c9bfe7cbb7@sentry.io/641191");

		/*
		 * It is possible to go around the static ``Sentry`` API, which means you are
		 * responsible for making the SentryClient instance available to your code.
		 */
		sentry = SentryClientFactory.sentryClient();

		MyClass myClass = new MyClass();
		//myClass.logWithStaticAPI();
		myClass.logWithInstanceAPI();
		
		// Sleep for a while to allow event to get sent.
		try {
			Thread.currentThread().sleep(5000);
		} catch (InterruptedException e) {
		}
		System.out.println("Exiting...");
	}

	/**
	 * An example method that throws an exception.
	 */
	void unsafeMethod() {
		throw new UnsupportedOperationException("You shouldn't call this again!");
	}

	/**
	 * Examples using the (recommended) static API.
	 */
	void logWithStaticAPI() {
		// Note that all fields set on the context are optional. Context data is copied
		// onto
		// all future events in the current context (until the context is cleared).

		// Record a breadcrumb in the current context. By default the last 100
		// breadcrumbs are kept.
		Sentry.getContext().recordBreadcrumb(new BreadcrumbBuilder().setMessage("User made an action").build());

		// Set the user in the current context.
		Sentry.getContext().setUser(new UserBuilder().setEmail("hello@sentry.io").build());

		// Add extra data to future events in this context.
		Sentry.getContext().addExtra("extra", "thing");

		// Add an additional tag to future events in this context.
		Sentry.getContext().addTag("tagName", "tagValue");

		/*
		 * This sends a simple event to Sentry using the statically stored instance that
		 * was created in the ``main`` method.
		 */
		Sentry.capture("This is a test");

		try {
			unsafeMethod();
		} catch (Exception e) {
			// This sends an exception event to Sentry using the statically stored instance
			// that was created in the ``main`` method.
			Sentry.capture(e);
		}
	}

	/**
	 * Examples that use the SentryClient instance directly.
	 */
	void logWithInstanceAPI() {
		// Retrieve the current context.
		Context context = sentry.getContext();

		// Record a breadcrumb in the current context. By default the last 100
		// breadcrumbs are kept.
		context.recordBreadcrumb(new BreadcrumbBuilder().setMessage("User made an action").build());

		// Set the user in the current context.
		context.setUser(new UserBuilder().setEmail("s.curran@liberty-it.co.uk").build());

		// Add extra data to future events in this context.
		context.addExtra("transactionId", UUID.randomUUID().toString());

		// Add an additional tag to future events in this context.
		context.addTag("operationName", "createOption");
		
		// This sends a simple event to Sentry.
		sentry.sendMessage("This is a test with the instance API");

		try {
			unsafeMethod();
		} catch (Exception e) {
			// This sends an exception event to Sentry.
			sentry.sendException(e);
		}
	}
}