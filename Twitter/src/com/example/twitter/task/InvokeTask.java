package com.example.twitter.task;

import twitter4j.Twitter;
import android.app.Activity;
import android.os.AsyncTask;

public abstract class InvokeTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result>
{
	protected final Twitter twitter;
	protected final Activity activity;
	protected final Runnable refreshAction;

	public InvokeTask(Activity activity, Twitter twitter, Runnable refreshAction)
	{
		this.activity = activity;
		this.twitter = twitter;
		this.refreshAction = refreshAction;
	}
}