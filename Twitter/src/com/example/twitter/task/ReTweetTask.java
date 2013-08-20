package com.example.twitter.task;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

import com.example.twitter.R;
import com.example.twitter.util.Logger;

public class ReTweetTask extends BaseTask<Long, Void, twitter4j.Status> implements OnCancelListener
{
	public ReTweetTask(Activity activity, Twitter twitter, Runnable refreshAction)
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
		dialog.setTitle(activity.getString(R.string.ReTweetTask_ProgressDialogTitle));
		dialog.setMessage(activity.getString(R.string.ReTweetTask_ProgressDialogMessage));
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setCancelable(false);
		dialog.show();
	}

	@Override
	protected twitter4j.Status doInBackground(Long... params)
	{
		final long started = Logger.start();
		final long id = params[0];
		try
		{
			return twitter.retweetStatus(id);
		} catch (final TwitterException ex)
		{
			Logger.err(ex);
		} finally
		{
			Logger.end(started);
		}
		return null;
	}

	@Override
	protected void onPostExecute(twitter4j.Status result)
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

	@Override
	public void onCancel(DialogInterface dialog)
	{
	}
}
