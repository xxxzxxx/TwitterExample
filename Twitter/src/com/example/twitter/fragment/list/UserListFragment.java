package com.example.twitter.fragment.list;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import twitter4j.PagableResponseList;
import twitter4j.TwitterException;
import twitter4j.User;
import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.twitter.R;
import com.example.twitter.util.ImageLoadTask;
import com.example.twitter.util.Logger;

public abstract class UserListFragment extends
		TwitterBaseListFragment<AsyncTask<Void, Void, ArrayList>, ArrayAdapter<User>, ArrayList<User>>
{
	private final static BitmapFactory.Options Options;
	static
	{
		Options = new BitmapFactory.Options();
		UserListFragment.Options.inPurgeable = true;
	}

	public class UserArrayAdapter extends ArrayAdapter<User>
	{
		private final ArrayList<User> mItems;
		private final LayoutInflater mInflater;

		public UserArrayAdapter(final Context context, final int textViewResourceId, final ArrayList<User> items)
		{
			super(context, textViewResourceId, items);
			this.mItems = items;
			this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public User getItem(final int position)
		{
			return this.mItems.get(position);
		}

		@Override
		public View getView(final int position, final View convertView, final ViewGroup parent)
		{
			final long started = Logger.start();
			View view = convertView;
			if (view == null)
			{
				view = this.mInflater.inflate(R.layout.cell_follower, null);
			}

			final User user = this.getItem(position);
			if (user != null)
			{
				final ImageView imageView = (ImageView) view.findViewById(R.id.cell_follower_thumb);
				final TextView textView = (TextView) view.findViewById(R.id.cell_follower_name);
				try
				{
					final URL imageURL = new URL(user.getProfileImageURL());
					final ImageLoadTask task = new ImageLoadTask(getActivity(), imageView, UserListFragment.Options);
					task.execute(imageURL);
				} catch (final MalformedURLException ex)
				{
					ex.printStackTrace();
				}
				textView.setText(user.getName());
			}
			Logger.end(started);
			return view;
		}
	}

	private class GetFollowerTask extends AsyncTask<Void, Void, ArrayList>
	{
		private final UserListFragment self = UserListFragment.this;
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
					PagableResponseList<User> res;
					res = twitter.getFollowersList(preference.getScreenName(), cursor);
					cursor = res.getNextCursor();
					Logger.debug(cursor);
					return (ArrayList) res;
				}
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

	protected long cursor = -1L;

	@Override
	public void onRefresh()
	{
		cursor = -1L;
		super.onRefresh();
	}

	@Override
	protected UserArrayAdapter generateArrayAdapter()
	{
		return new UserArrayAdapter(this.getActivity(), R.layout.cell_follower, this.items);
	}

	@Override
	protected ArrayList<User> generateArrayList()
	{
		return new ArrayList<User>();
	}

	@Override
	public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
	{
	}
}
