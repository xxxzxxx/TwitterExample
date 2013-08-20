package com.example.twitter.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class ImageLoadTask extends AsyncTask<URL, Void, Bitmap>
{
	final Activity activity;
	final ImageView imageView;
	final BitmapFactory.Options option;
	Bitmap bmp;
	private final Runnable refreshActioin = new Runnable()
	{
		@Override
		public void run()
		{
			if (bmp != null)
			{
				imageView.setImageBitmap(bmp);
			}
		}
	};

	public ImageLoadTask(Activity activity, ImageView imageView, BitmapFactory.Options option)
	{
		super();
		this.activity = activity;
		this.imageView = imageView;
		this.option = option;
	}

	@Override
	protected Bitmap doInBackground(URL... params)
	{
		InputStream input;
		try
		{
			input = params[0].openStream();
			final BitmapFactory.Options option = new BitmapFactory.Options();
			option.inPurgeable = true;
			bmp = BitmapFactory.decodeStream(input, null, option);
		} catch (final IOException ex)
		{
			Logger.err(ex);
		}
		return bmp;
	}

	@Override
	protected void onPostExecute(Bitmap result)
	{
		final long started = Logger.start();
		activity.runOnUiThread(refreshActioin);
		Logger.end(started);
	}

}
