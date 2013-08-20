package com.example.twitter.fragment.list;

import java.util.ArrayList;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;
import com.example.twitter.config.TwitterConfig;
import com.example.twitter.config.TwitterPreference;
import com.example.twitter.pager.OnRefreshListener;
import com.example.twitter.util.Logger;

public abstract class TwitterBaseListFragment<TASK extends AsyncTask<Void, Void, ArrayList>, ADPT extends ArrayAdapter<?>, LIST extends ArrayList<?>>
		extends SherlockListFragment implements OnScrollListener, OnItemClickListener, OnRefreshListener
{
	protected final TwitterConfig config = TwitterConfig.getInstance();
	protected TwitterFactory factory = new TwitterFactory(config.buildConfiguration());
	protected Twitter twitter = factory.getInstance();
	protected TwitterPreference preference = null;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		final long started = Logger.start();
		try
		{
			this.activity = getActivity();
			this.preference = getTwPreference(activity);
			this.items = generateArrayList();
			this.adapter = generateArrayAdapter();
			this.task = generateGetArrayTask();
			setListAdapter(this.adapter);
			if (this.task != null)
			{
				this.task.execute();
			}
		} finally
		{
			Logger.end(started);
		}
	}

	protected Activity activity;

	private TwitterPreference getTwPreference(Activity activity)
	{
		final Bundle arg = getArguments();
		final String preferenceTag = arg.getString("preference");
		return new TwitterPreference(activity.getSharedPreferences(preferenceTag != null ? preferenceTag
				: "currentPreference", Context.MODE_PRIVATE));
	}

	private int executedItemCount = 0;

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
	{
		if (Status.FINISHED == this.task.getStatus())
		{
			if (totalItemCount == firstVisibleItem + visibleItemCount && totalItemCount != executedItemCount)
			{
				executedItemCount = totalItemCount;
				additionalReading();
			}
		}
	}

	protected void additionalReading()
	{
		this.task = generateGetArrayTask();
		this.task.execute();
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState)
	{
	}

	protected LIST items = null;
	protected ADPT adapter = null;
	protected TASK task = null;

	protected abstract ADPT generateArrayAdapter();

	protected abstract TASK generateGetArrayTask();

	protected abstract LIST generateArrayList();

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		getListView().setOnScrollListener(this);
		getListView().setOnItemClickListener(this);
	}

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
	}

	@Override
	public void onRefresh()
	{
		if (this.task.getStatus() == Status.FINISHED)
		{
			this.items = generateArrayList();
			this.adapter = generateArrayAdapter();
			this.task = generateGetArrayTask();
			setListAdapter(this.adapter);
			if (this.task != null)
			{
				this.task.execute();
			}
		} else
		{

		}
	}

	protected void showToast(int id, int duration)
	{
		Toast.makeText(this.getActivity(), getString(id), duration).show();
	}
}
