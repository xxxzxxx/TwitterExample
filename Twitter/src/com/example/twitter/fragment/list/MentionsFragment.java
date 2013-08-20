package com.example.twitter.fragment.list;

import java.util.ArrayList;

import twitter4j.Paging;
import twitter4j.ResponseList;
import android.app.Activity;
import android.os.AsyncTask;

import com.example.twitter.util.Logger;

public class MentionsFragment extends TwitteListFragment
{
	@SuppressWarnings("rawtypes")
	private class MentionsTask extends AsyncTask<Void, Void, ArrayList>
	{
		private final MentionsFragment self = MentionsFragment.this;
		private final Runnable refreshAction = new Runnable()
		{
			@Override
			public void run()
			{
				self.adapter.notifyDataSetChanged();
			}
		};

		@Override
		protected ArrayList<twitter4j.Status> doInBackground(Void... params)
		{
			final long started = Logger.start();
			twitter.setOAuthAccessToken(preference.buildAccessToken());
			try
			{
				Thread.sleep(2000);
				final ArrayList<twitter4j.Status> response = new ArrayList<twitter4j.Status>();
				final Paging paging = new Paging(MentionsFragment.this.paging);
				ResponseList<twitter4j.Status> res;
				res = twitter.getMentions(paging);
				response.addAll(res);
				MentionsFragment.this.paging += 1;
				return response;
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
	protected MentionsTask generateGetArrayTask()
	{
		return new MentionsTask();
	}
}
