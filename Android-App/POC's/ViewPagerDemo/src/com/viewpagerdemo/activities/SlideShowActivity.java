package com.viewpagerdemo.activities;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ViewFlipper;

public class SlideShowActivity extends Activity
{

	private ViewFlipper slideshowFlipper;
	private ArrayList<Drawable> list;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.slideshow);

		slideshowFlipper = (ViewFlipper) findViewById(R.id.Flipper);

		list = new ArrayList<Drawable>();
		list.add(getResources().getDrawable(R.drawable.ic_launcher));
		list.add(getResources().getDrawable(R.drawable.splashscreen));
		list.add(getResources().getDrawable(R.drawable.ic_launcher));
		list.add(getResources().getDrawable(R.drawable.splashscreen));
		list.add(getResources().getDrawable(R.drawable.ic_launcher));
		list.add(getResources().getDrawable(R.drawable.splashscreen));

		for (int i = 0; i < list.size(); i++)
		{
			ImageView img = new ImageView(this);
			img.setScaleType(ScaleType.FIT_XY);
			LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT);
			img.setLayoutParams(params);
			slideshowFlipper.addView(img);
			img.setImageDrawable(list.get(i));
			slideshowFlipper.setFlipInterval(1000);
			slideshowFlipper.startFlipping();

		}

		slideshowFlipper.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				slideshowFlipper.showNext();

			}
		});

	}
}