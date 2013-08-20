package com.example.twitter.fragment.list;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import twitter4j.Status;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.twitter.R;
import com.example.twitter.task.ReTweetTask;
import com.example.twitter.util.ImageLoadTask;
import com.example.twitter.util.Logger;

public abstract class TwitteListFragment extends
		TwitterBaseListFragment<AsyncTask<Void, Void, ArrayList>, ArrayAdapter<Status>, ArrayList<Status>>
{
	private final static BitmapFactory.Options Options;
	static
	{
		Options = new BitmapFactory.Options();
		TwitteListFragment.Options.inPurgeable = true;
	}

	public class StatusArrayAdapter extends ArrayAdapter<twitter4j.Status>
	{
		private final ArrayList<twitter4j.Status> mItems;
		private final LayoutInflater mInflater;

		public StatusArrayAdapter(final Context context, final int textViewResourceId, final ArrayList<Status> items)
		{
			super(context, textViewResourceId, items);
			this.mItems = items;
			this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public twitter4j.Status getItem(final int position)
		{
			return this.mItems.get(position);
		}

		@Override
		public View getView(final int position, final View convertView, final ViewGroup parent)
		{
			View view = convertView;
			final twitter4j.Status status = this.getItem(position);
			if (view == null)
			{
				view = this.mInflater.inflate(R.layout.cell_tweet, null);
			}
			if (status != null)
			{
				final ImageView imageView = (ImageView) view.findViewById(R.id.cell_tweet_thumb);
				final TextView sourceView = (TextView) view.findViewById(R.id.cell_tweet_source);
				final TextView textView = (TextView) view.findViewById(R.id.cell_tweet_text);
				try
				{
					final URL imageURL = new URL(status.getUser().getProfileImageURL());
					final ImageLoadTask task = new ImageLoadTask(getActivity(), imageView, TwitteListFragment.Options);
					task.execute(imageURL);
				} catch (final MalformedURLException ex)
				{
					Logger.err(ex);
				}
				sourceView.setText(status.getCreatedAt().toLocaleString());
				textView.setText(status.getText());
				view.setTag(status);
			}
			return view;
		}
	}

	protected int paging = 1;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	protected StatusArrayAdapter generateArrayAdapter()
	{
		return new StatusArrayAdapter(this.getActivity(), R.layout.cell_tweet, this.items);
	}

	@Override
	protected ArrayList<Status> generateArrayList()
	{
		return new ArrayList<Status>();
	}

	@Override
	public void onRefresh()
	{
		paging = 1;
		super.onRefresh();
	}

	private Status selectStatus;

	@Override
	public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
	{
		final Status status = (Status) paramView.getTag();
		if (status != null)
		{
			selectStatus = status;
			if (status.getUser().getId() != preference.getUserId())
			{
				createReTweetDialog().show();
			} else
			{
				createDeleteDialog().show();
			}
		}
	}

	private AlertDialog.Builder createDeleteDialog()
	{
		return new AlertDialog.Builder(getActivity())
				.setIcon(android.R.drawable.ic_dialog_info)
				.setMessage(getString(R.string.TimelineFragment_DeletetTweetDialogMessage))
				.setTitle(getString(R.string.TimelineFragment_DeletetTweetDialogTitle))
				.setPositiveButton(getString(R.string.TimelineFragment_DeletetTweeetDialogTweetButton),
						new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int whichButton)
							{
								final ReTweetTask retweetTask = new ReTweetTask(TwitteListFragment.this.getActivity(),
										TwitteListFragment.this.twitter, new Runnable()
										{
											@Override
											public void run()
											{
												TwitteListFragment.this.onRefresh();
											}
										});
								retweetTask.execute(TwitteListFragment.this.selectStatus.getId());
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

	private AlertDialog.Builder createReTweetDialog()
	{
		return new AlertDialog.Builder(getActivity())
				.setIcon(android.R.drawable.ic_dialog_info)
				.setMessage(getString(R.string.TimelineFragment_RetweetDialogMessage))
				.setTitle(getString(R.string.TimelineFragment_RetweetDialogTitle))
				.setPositiveButton(getString(R.string.TimelineFragment_ReTweeetDialogTweetButton),
						new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int whichButton)
							{
								final ReTweetTask retweetTask = new ReTweetTask(TwitteListFragment.this.getActivity(),
										TwitteListFragment.this.twitter, new Runnable()
										{
											@Override
											public void run()
											{
												TwitteListFragment.this.onRefresh();
											}
										});
								retweetTask.execute(TwitteListFragment.this.selectStatus.getId());
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
