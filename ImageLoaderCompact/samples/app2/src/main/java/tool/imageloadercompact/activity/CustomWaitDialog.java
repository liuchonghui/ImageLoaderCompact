package tool.imageloadercompact.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import tool.imageloadercompact.app2.R;

/**
 * 自定义加载 请求等等待dialog
 *
 * @author liu_chonghui
 *
 */
public class CustomWaitDialog extends Dialog {
	private int titleId;
	private Context context;
	private View view;
	private LayoutInflater inflater;

	public CustomWaitDialog(Context context) {
		super(context, R.style.waitDialog);
		this.context = context;
		init();
	}

	public CustomWaitDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
		init();
	}

	private void init(){
		inflater = LayoutInflater.from(this.context);
		if (view == null) {
			view = inflater.inflate(R.layout.wait_dialog, null);
		}
	}

	@SuppressLint("InflateParams")
	public void setWaitTitle(int titleId) {
		if (view == null) {
			view = inflater.inflate(R.layout.wait_dialog, null);
		}
		this.titleId = titleId;
		TextView titleView = (TextView) view
				.findViewById(R.id.wait_dialog_title);
		titleView.setText(this.titleId);
	}

	@SuppressLint("InflateParams")
	public void setWaitTitle(CharSequence title) {
		if (view == null) {
			view = inflater.inflate(R.layout.wait_dialog, null);
		}
		TextView titleView = (TextView) view
				.findViewById(R.id.wait_dialog_title);
		titleView.setText(title);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(view);
	}

	/**
	 * 显示
	 */
	public void showMe() {
		if (!isShowing()) {
			this.show();
		}
	}

	/**
	 * 关闭
	 */
	public void close() {
		if (isShowing()) {
			this.dismiss();
		}
	}
}
