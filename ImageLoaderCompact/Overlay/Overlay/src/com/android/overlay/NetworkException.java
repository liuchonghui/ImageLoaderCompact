package com.android.overlay;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Exception with specified error's string resource.
 * 
 */
public class NetworkException extends Exception {
	private static final long serialVersionUID = 1L;
	private final int resourceId;
	private final Throwable wrappedThrowable;

	public NetworkException(int resourceId) {
		this(resourceId, null);
	}

	public NetworkException(int resourceId, Throwable wrappedThrowable) {
		super();
		this.resourceId = resourceId;
		this.wrappedThrowable = wrappedThrowable;
	}

	public int getResourceId() {
		return resourceId;
	}

	@Override
	public void printStackTrace(PrintStream out) {
		super.printStackTrace(out);
		if (wrappedThrowable != null) {
			out.println("Nested Exception: ");
			wrappedThrowable.printStackTrace(out);
		}
	}

	@Override
	public void printStackTrace(PrintWriter out) {
		super.printStackTrace(out);
		if (wrappedThrowable != null) {
			out.println("Nested Exception: ");
			wrappedThrowable.printStackTrace(out);
		}
	}

}