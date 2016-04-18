package com.zmyh.r.seek;

import java.util.List;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zmyh.r.R;
import com.zmyh.r.box.TroopObj;
import com.zmyh.r.handler.TroopObjHandler;
import com.zmyh.r.main.server.ServerBaseAdapter;
import com.zmyh.r.photo.adapter.MntTroopBaseAdapter;
import com.zmyh.r.photo.adapter.OnlineTroopBaseAdapter;
import com.zmyh.r.photo.adapter.TroopBaseAdapter;
import com.zmyh.r.tool.ShowMessage;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.AbsListView.OnScrollListener;

public class SeekPhoneActivity extends Activity {

	private Context context;

	private int pageIndex = 1, totalPage = 1;
	private String seekStr = "";

	private InputMethodManager imm = null;
	private TroopBaseAdapter troopBaseAdapter;

	@ViewInject(R.id.title_seekInput)
	private EditText seekInput;
	@ViewInject(R.id.seek_dataList)
	private ListView dataList;
	@ViewInject(R.id.seek_progress)
	private ProgressBar progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_seek);
		ViewUtils.inject(this);
		context = this;

		initActivity();
		setDataListScrollListener();
		setInputOnKeyListener();
	}

	@OnClick({ R.id.title_back })
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.title_back:
				finish();
				break;
		}
	}

	private void setDataListScrollListener() {
		dataList.setOnScrollListener(new OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					if (view.getLastVisiblePosition() >= (view.getCount() - 1)) {
						if (progress.getVisibility() == View.GONE) {
							if (totalPage > pageIndex) {
								seekData();
							} else {
								ShowMessage.showLast(context);
							}
						}
					}
				}
			}

			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {

			}
		});
	}

	private void setInputOnKeyListener() {
		seekInput.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View arg0, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER
						&& event.getAction() == KeyEvent.ACTION_DOWN) {
					if (imm.isActive()) {
						imm.hideSoftInputFromWindow(
								seekInput.getApplicationWindowToken(), 0);
					}
					seekStr = seekInput.getText().toString();
					pageIndex = 0;
					seekData();
					return true;
				}
				return false;
			}

		});
	}

	private void initActivity() {
		seekInput.setVisibility(View.VISIBLE);
		imm = (InputMethodManager) seekInput.getContext().getSystemService(
				Context.INPUT_METHOD_SERVICE);
	}

	private void seekData() {
		progress.setVisibility(View.VISIBLE);
		List<TroopObj> list = TroopObjHandler.getTroopObjListForMuName(context,
				seekStr, pageIndex);
		setTroopDataList(list, false);
	}

	private void setTroopDataList(List<TroopObj> list, boolean isOnline) {
		if (troopBaseAdapter == null) {
			if (isOnline) {
				troopBaseAdapter = new OnlineTroopBaseAdapter(context, list);
			} else {
				troopBaseAdapter = new MntTroopBaseAdapter(context, list);
			}
			dataList.setAdapter(troopBaseAdapter);
		} else {
			troopBaseAdapter.addItem(list);
		}
		pageIndex += 1;
		progress.setVisibility(View.GONE);
	}

}
