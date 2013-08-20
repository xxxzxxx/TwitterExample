package com.example.twitter.pager;

import android.view.View;

import com.actionbarsherlock.app.ActionBar;

public interface TabChangedListener
{
	void onTabChanged(int pageIndex, ActionBar.Tab tab, View tabView);

}
