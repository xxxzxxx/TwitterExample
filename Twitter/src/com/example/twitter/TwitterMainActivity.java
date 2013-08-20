package com.example.twitter;

import java.util.ArrayList;

import twitter4j.StatusUpdate;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.example.twitter.fragment.list.FollowerFragment;
import com.example.twitter.fragment.list.FollowingFragment;
import com.example.twitter.fragment.list.MentionsFragment;
import com.example.twitter.fragment.list.TimelineFragment;
import com.example.twitter.pager.OnRefreshListener;
import com.example.twitter.pager.TabChangedListener;
import com.example.twitter.pager.TabListener;
import com.example.twitter.task.TweetTask;

public class TwitterMainActivity extends TwitterBaseActivity implements TabChangedListener
{
	private ActionBar actionBar = null;
	private ViewPager viewPager = null;
	private TabListener tabListener = null;

	private static class TabDefine
	{
		final int title;
		final Class<?> klass;
		final int icon;

		public TabDefine(int title, int icon, Class<?> klass)
		{
			this.title = title;
			this.klass = klass;
			this.icon = icon;
		}
	}

	private final static ArrayList<TabDefine> TabDefines;
	static
	{
		TabDefines = new ArrayList<TwitterMainActivity.TabDefine>();
		TwitterMainActivity.TabDefines.add(new TabDefine(R.string.TwitterMainActivity_TabTimeline, 0,
				TimelineFragment.class));
		TwitterMainActivity.TabDefines.add(new TabDefine(R.string.TwitterMainActivity_TabMentions, 0,
				MentionsFragment.class));
		TwitterMainActivity.TabDefines.add(new TabDefine(R.string.TwitterMainActivity_TabFollowing, 0,
				FollowingFragment.class));
		TwitterMainActivity.TabDefines.add(new TabDefine(R.string.TwitterMainActivity_TabFollowers, 0,
				FollowerFragment.class));
	}

	public static Intent createIntent(Context context)
	{
		final Intent intent = new Intent(context, TwitterMainActivity.class);
		return intent;
	}

	public static Intent createIntent(Context context, String preferenceName)
	{
		final Intent intent = new Intent(context, TwitterMainActivity.class);
		intent.putExtra(TwitterBaseActivity.IntentPreferenceName, preferenceName);
		return intent;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (!preference.hasAccessToken())
		{
			showToast(R.string.TwitterBaseActivity_NotHasAccessToken, Toast.LENGTH_LONG);
			preference.clear();
			preference.commit();
			this.finish();
		}
		initConponent();
		twitter.setOAuthAccessToken(preference.buildAccessToken());
	}

	private void initConponent()
	{
		this.actionBar = getSupportActionBar();
		this.actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		this.viewPager = new ViewPager(this);
		this.viewPager.setId(R.id.pager);
		setContentView(this.viewPager);

		this.tabListener = new TabListener(this, this.viewPager);
		this.tabListener.addTabChangedListener(this);
		ActionBar.Tab tab;
		final Bundle bundle = new Bundle();
		bundle.putString("preference", this.preferenceTag);
		for (final TabDefine define : TwitterMainActivity.TabDefines)
		{
			tab = actionBar.newTab();
			tab.setText(getString(define.title));
			if (define.icon != 0)
			{
				tab.setIcon(define.icon);
			}

			this.tabListener.addTab(tab, define.klass, bundle);
		}
	}

	final int MENU_EDIT = android.view.Menu.FIRST;
	final int MENU_LOGOUT = android.view.Menu.FIRST + 1;
	final int MENU_RELOAD = android.view.Menu.FIRST + 2;

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		final boolean result = super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_EDIT, android.view.Menu.NONE, getString(R.string.TwitterMainActivity_MenuEdit))
				.setIcon(android.R.drawable.ic_menu_edit).setVisible(true)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		menu.add(0, MENU_RELOAD, android.view.Menu.NONE, getString(R.string.TwitterMainActivity_MenuRefresh))
				.setIcon(R.drawable.ic_menu_refresh).setVisible(true).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		menu.add(0, MENU_LOGOUT, android.view.Menu.NONE, getString(R.string.TwitterMainActivity_MenuLogout))
				.setIcon(android.R.drawable.ic_menu_revert).setVisible(true)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		return result;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		final boolean result = super.onOptionsItemSelected(item);

		final int itemId = item.getItemId();
		switch (itemId)
		{
			case MENU_EDIT:
				createTweetDialog().show();
				break;
			case MENU_LOGOUT:
				preference.clear();
				preference.commit();
				finish();
				break;
			case MENU_RELOAD:
				final Fragment fragment = (Fragment) tabListener.instantiateItem(this.pageIndex);
				if (fragment instanceof OnRefreshListener)
				{
					final OnRefreshListener refresh = (OnRefreshListener) fragment;
					refresh.onRefresh();
				}
				break;
		}
		return result;
	}

	private int pageIndex = 0;

	@Override
	public void onTabChanged(int pageIndex, Tab tab, View tabView)
	{
		this.pageIndex = pageIndex;
	}

	private AlertDialog.Builder createTweetDialog()
	{
		final EditText edit = new EditText(TwitterMainActivity.this);
		return new AlertDialog.Builder(TwitterMainActivity.this)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setTitle(getString(R.string.TwitterMainActivity_TweeetDialogTitle))
				.setView(edit)
				.setPositiveButton(getString(R.string.TwitterMainActivity_TweeetDialogTweetButton),
						new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int whichButton)
							{
								final TweetTask tweetTask = new TweetTask(TwitterMainActivity.this,
										TwitterMainActivity.this.twitter, new Runnable()
										{
											@Override
											public void run()
											{
												final int count = TwitterMainActivity.this.tabListener.getCount();
												for (int i = 0; i < count; i++)
												{
													final Fragment fragment = (Fragment) TwitterMainActivity.this.tabListener
															.instantiateItem(i);
													if (fragment instanceof TimelineFragment)
													{
														final TimelineFragment refresh = (TimelineFragment) fragment;
														refresh.onRefresh();
													}
												}
											}
										});
								final String tweet = edit.getText().toString();
								tweetTask.execute(new StatusUpdate(tweet));
							}
						})
				.setNegativeButton(getString(R.string.DialogCancelButton), new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int whichButton)
					{
					}
				});
	}
}
