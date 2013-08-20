package com.example.twitter.pager;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class TabListener extends FragmentStatePagerAdapter implements ActionBar.TabListener,
		ViewPager.OnPageChangeListener
{
	private final Context context;
	private final ActionBar actionBar;
	private final ViewPager viewPager;
	private final ArrayList<TabInfo> tabs = new ArrayList<TabInfo>();
	private final ArrayList<TabChangedListener> tabChangedListeners = new ArrayList<TabChangedListener>();

	static final class TabInfo
	{
		private final Class<?> clss;
		private final Bundle args;

		TabInfo(Class<?> clss, Bundle args)
		{
			this.clss = clss;
			this.args = args;
		}
	}

	public TabListener(SherlockFragmentActivity activity, ViewPager pager)
	{
		super(activity.getSupportFragmentManager());
		this.context = activity;
		this.actionBar = activity.getSupportActionBar();
		this.viewPager = pager;
		this.viewPager.setAdapter(this);
		this.viewPager.setOnPageChangeListener(this);
	}

	public void addTab(ActionBar.Tab tab, Class<?> clss, Bundle args)
	{
		final TabInfo info = new TabInfo(clss, args);
		tab.setTag(info);
		tab.setTabListener(this);
		this.tabs.add(info);
		this.actionBar.addTab(tab);
		notifyDataSetChanged();
	}

	public void addTabChangedListener(TabChangedListener listener)
	{
		this.tabChangedListeners.add(listener);
	}

	@Override
	public int getCount()
	{
		return tabs.size();
	}

	@Override
	public Fragment getItem(int position)
	{
		final TabInfo info = tabs.get(position);
		return Fragment.instantiate(this.context, info.clss.getName(), info.args);
	}

	public Object instantiateItem(int position)
	{
		return super.instantiateItem(viewPager, position);
	}

	private void notifyTabChangedListeners(int tabIndex, Tab tab, View tabView)
	{
		for (final TabChangedListener listener : tabChangedListeners)
		{
			listener.onTabChanged(tabIndex, tab, tabView);
		}
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
	{
	}

	@Override
	public void onPageSelected(int position)
	{
		this.actionBar.setSelectedNavigationItem(position);
	}

	@Override
	public void onPageScrollStateChanged(int state)
	{
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft)
	{
		final Object tag = tab.getTag();
		for (int i = 0; i < tabs.size(); i++)
		{
			if (tabs.get(i) == tag)
			{
				viewPager.setCurrentItem(i);
				notifyTabChangedListeners(i, tab, tab.getCustomView());
			}
		}
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft)
	{
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft)
	{
	}
}
