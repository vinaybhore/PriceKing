package com.viewpagerdemo.activities;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

public class ViewPagerDemoActivity extends Activity implements OnGestureListener
{

	private MyFragmentAdapter myFragmentAdapter;
	private ViewPager mPager;
	// private TextView pageIndicator;
	private PagerControl control;
	private ArrayList<Drawable> list;
	private int currentPage = 0;
	private static final int SWIPE_MIN_DISTANCE = 50;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private GestureDetector detector = new GestureDetector(this);

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		list = new ArrayList<Drawable>();
		list.add(getResources().getDrawable(R.drawable.asn_visa));
		list.add(getResources().getDrawable(R.drawable.bmw));
		list.add(getResources().getDrawable(R.drawable.finles));
		list.add(getResources().getDrawable(R.drawable.gold_mastercard));
		list.add(getResources().getDrawable(R.drawable.hcc));
		list.add(getResources().getDrawable(R.drawable.heineken));

		mPager = (ViewPager) findViewById(R.id.pager);
		control = (PagerControl) findViewById(R.id.control);
		myFragmentAdapter = new MyFragmentAdapter(getApplicationContext());
		mPager.setAdapter(myFragmentAdapter);

		// sets the current page at the position which you want.
		// mPager.setCurrentItem(2);

		// pageIndicator = (TextView) findViewById(R.id.text);
		if (list != null && list.size() > 0)
			control.setNumPages(list.size());

		// sets the indicator according to current page.
		// control.setCurrentPage(2);

		mPager.setOnTouchListener(new View.OnTouchListener()
		{

			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				return detector.onTouchEvent(event);
			}
		});

		mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
		{
			@Override
			public void onPageSelected(int position)
			{
				currentPage = position;
				// if (list != null && list.size() > 0)
				currentPage = currentPage % list.size();
				control.setCurrentPage(currentPage);
				// pageIndicator.setText((mPager.getCurrentItem() + 1) + " /" + list.size());

			}

			@Override
			public void onPageScrollStateChanged(int state)
			{
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2)
			{
			}
		});

	}

	public class MyFragmentAdapter extends PagerAdapter
	{
		private Context context;
		private View layout = null;

		public MyFragmentAdapter(Context context)
		{
			this.context = context;
		}

		@Override
		public int getCount()
		{
			// TODO Auto-generated method stub
			return list.size() * 2;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object)
		{
			((ViewGroup) container).removeView((View) object);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1)
		{
			// TODO Auto-generated method stub
			return arg0 == ((View) arg1);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position)
		{
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			layout = inflater.inflate(R.layout.images, null);
			ImageView img = (ImageView) layout.findViewById(R.id.image);

			img.setImageDrawable(list.get(position % list.size()));
			container.addView(layout);
			return layout;
		}

	}

	@Override
	public boolean onDown(MotionEvent e)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
	{
		try
		{
			if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
			{

				return false;
			}

			// right to left swipe
			if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY && currentPage == (list
					.size() - 1))
			{

				mPager.setCurrentItem(0, false);
				return true;
			}
			else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY && currentPage == 0)
			{
				mPager.setCurrentItem(list.size() - 1, false);
				return true;
			}
		}
		catch (Exception e)
		{
			Toast.makeText(this, "Error while Swipping", Toast.LENGTH_SHORT).show();
			// nothing
		}

		return false;
	}

	@Override
	public void onLongPress(MotionEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e)
	{
		// TODO Auto-generated method stub
		return false;
	}

}