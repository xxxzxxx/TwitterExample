package com.example.twitter.task;

import java.util.ArrayList;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import android.app.Activity;
import android.app.ProgressDialog;

import com.example.twitter.R;
import com.example.twitter.util.Logger;

public class DeleteTweetTask extends BaseTask<Long, Void, ArrayList<twitter4j.Status>>
{
	public DeleteTweetTask(Activity activity, Twitter twitter, Runnable refreshAction)
	{
		super(activity, twitter, refreshAction);
	}

	private ProgressDialog dialog;

	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();
		dialog = new ProgressDialog(activity);
		dialog = new ProgressDialog(activity);
		dialog.setTitle(activity.getString(R.string.DeleteTweetTask_ProgressDialogTitle));
		dialog.setMessage(activity.getString(R.string.DeleteTweetTask_ProgressDialogMessage));
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setCancelable(false);
		dialog.show();
	}

	@Override
	protected ArrayList<twitter4j.Status> doInBackground(Long... params)
	{
		final ArrayList<twitter4j.Status> result = new ArrayList<twitter4j.Status>();
		for (final long id : params)
		{
			try
			{
				result.add(twitter.destroyStatus(id));
			} catch (final TwitterException ex)
			{
				Logger.err(ex);
			}
		}
		return result;
	}

	@Override
	protected void onPostExecute(ArrayList<twitter4j.Status> result)
	{
		if (result != null)
		{
			if (activity != null && refreshAction != null)
			{
				activity.runOnUiThread(refreshAction);
			}
		}
		dialog.hide();
	}
}
