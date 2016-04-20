package com.android.overlay;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.overlay.utils.LogUtils;

/**
 * @author liu_chonghui
 * 
 */
public class BugReportPage extends Activity {

	public static final String STRING_EXTRA_APP_NAME = "name";
	public static final String STRING_EXTRA_APP_VERSION = "id";
	public static final String STRING_EXTRA_REPORT_CONTENT = "content";

	protected StringBuilder reportTitle;
	protected StringBuilder reportText;
	protected OnSendListener mSendListener;
	protected OnCancelListener mCancelListener;

	protected void setOnSendListener(OnSendListener listener) {
		mSendListener = listener;
	}

	protected void setOnCancelListener(OnCancelListener listener) {
		mCancelListener = listener;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String bugContent = getIntent().getStringExtra(
				STRING_EXTRA_REPORT_CONTENT);
		String appName = getIntent().getStringExtra(STRING_EXTRA_APP_NAME);
		String appVersion = getIntent()
				.getStringExtra(STRING_EXTRA_APP_VERSION);

		if (bugContent == null || bugContent.length() == 0) {
			// gotoHomePage();
		}
		buildTitle(bugContent);
		buildText(appName, appVersion);

		initController();
		initView();
	}

	protected void initController() {
		if (mSendListener == null) {
			setOnSendListener(new OnSendListener() {
				@Override
				public void postOnSend() {
					LogUtils.logToEmailAddress(getActivity(),
							"13811873805@163.com", reportTitle.toString(),
							reportText.toString(), null);
				}
			});
		}

		if (mCancelListener == null) {
			setOnCancelListener(new OnCancelListener() {
				@Override
				public void postOnCancel() {
					finish();
				}
			});
		}
	}

	protected void initView() {
		TextView report = new TextView(this);
		report.setMovementMethod(ScrollingMovementMethod.getInstance());
		report.setClickable(false);
		report.setLongClickable(false);
		report.append(reportTitle);
		report.append(reportText);
		report.setTextSize(20.0f);
		report.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1.0f));

		LinearLayout buttonLayout = new LinearLayout(this);
		buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
		buttonLayout.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

		Button send = new Button(this);
		send.setText("Send");
		send.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mSendListener.postOnSend();
			}
		});
		send.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT, 1.0f));

		Button cancel = new Button(this);
		cancel.setText("Cancel");
		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mCancelListener.postOnCancel();
				Process.killProcess(Process.myPid());
				System.exit(10);
			}
		});
		cancel.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT, 1.0f));

		buttonLayout.addView(send);
		buttonLayout.addView(cancel);

		LinearLayout contentLayout = new LinearLayout(this);
		contentLayout.setOrientation(LinearLayout.VERTICAL);
		contentLayout.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

		contentLayout.addView(report);
		contentLayout.addView(buttonLayout);

		// FaText bug = new FaText(this);
		// bug.setIcon("fa-bug");
		// bug.setTextSize(333.0f);
		// final int color = R.color.warning;
		// bug.setTextColor(color);
		// RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
		// LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		// lp.addRule(RelativeLayout.CENTER_IN_PARENT);
		// bug.startAnimation(AnimationEffect.createFlashingAnimation());

		RelativeLayout pageLayout = new RelativeLayout(this);
		pageLayout.setLayoutParams(new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		// pageLayout.addView(bug, lp);
		pageLayout.addView(contentLayout);

		addContentView(pageLayout, new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	}

	protected void buildTitle(String bugContent) {
		reportText = new StringBuilder();
		reportText.append("Model:").append(Build.MODEL).append("\n");
		reportText.append("Device:").append(Build.DEVICE).append("\n");
		reportText.append("Product:").append(Build.PRODUCT).append("\n");
		reportText.append("Manufacturer:").append(Build.MANUFACTURER)
				.append("\n");
		reportText.append("Version:").append(Build.VERSION.RELEASE)
				.append("\n");
		reportText.append(bugContent);
	}

	protected void buildText(String appName, String appVersion) {
		reportTitle = new StringBuilder();
		if (appName != null && appName.length() > 0) {
			reportTitle.append(appName);
			if (appVersion != null && appVersion.length() > 0) {
				reportTitle.append(" ");
				reportTitle.append(appVersion);
			}
		} else {
			reportTitle.append("Application");
		}
		reportTitle
				.append(" has been crached, sorry."
						+ " You can help to fix this bug by"
						+ " sending the report below to developers."
						+ " The report will be sent by e-mail. Thank you in advance!\n\n");
	}

	public static interface OnSendListener {
		public abstract void postOnSend();
	}

	public static interface OnCancelListener {
		public abstract void postOnCancel();
	}

	protected Activity getActivity() {
		return this;
	}
}
