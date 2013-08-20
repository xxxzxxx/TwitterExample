package com.example.twitter.fragment.list;

import java.util.ArrayList;

import twitter4j.PagableResponseList;
import twitter4j.User;
import android.app.Activity;
import android.os.AsyncTask;

import com.example.twitter.util.Logger;

public class FollowerFragment extends UserListFragment
{
	private class GetFollowerTask extends AsyncTask<Void, Void, ArrayList>
	{
		private final FollowerFragment self = FollowerFragment.this;
		private final Runnable refreshAction = new Runnable()
		{
			@Override
			public void run()
			{
				self.adapter.notifyDataSetChanged();
			}
		};

		@Override
		protected ArrayList doInBackground(Void... params)
		{
			final long started = Logger.start();
			twitter.setOAuthAccessToken(preference.buildAccessToken());
			try
			{
				if (cursor != 0)
				{
					Thread.sleep(2000);
					PagableResponseList<User> res;
					res = twitter.getFollowersList(preference.getScreenName(), cursor);
					cursor = res.getNextCursor();
					Logger.debug(cursor);
					return (ArrayList) res;
				}
			} catch (final Throwable ex)
			{
				Logger.err(ex);
			} finally
			{
				Logger.end(started);
			}
			return null;
		}

		@Override
		protected void onPostExecute(ArrayList result)
		{
			final long started = Logger.start();
			if (result != null)
			{
				self.items.addAll(result);
			}
			final Activity activity = getActivity();
			if (activity != null)
			{
				activity.runOnUiThread(refreshAction);
			}
			Logger.end(started);
		}
	}

	@Override
	protected GetFollowerTask generateGetArrayTask()
	{
		return new GetFollowerTask();
	}
}
