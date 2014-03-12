package com.jc.nasadailyrssfeed;

import com.jc.nasadailyrssfeed.MyListFragment.OnItemSelectedListener;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.LruCache;

public class UIActivity extends FragmentActivity implements OnItemSelectedListener{
    
	private String listFragmentTag="listfragment";
	private String detailFragmentTag="detailfragment";
	
	private FragmentManager fm;
	
	private LruCache<String, Bitmap> mMemoryCache;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acitivity_ui);
			
		// Get max available VM memory, exceeding this amount will throw an
		// OutOfMemory exception. Stored in kilobytes as LruCache takes an
		// int in its constructor.
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

		// Use 1/8th of the available memory for this memory cache.
		final int cacheSize = maxMemory / 8;
		
		RetainFragment retainFragment = RetainFragment
				.findOrCreateRetainFragment(getSupportFragmentManager());
		
		mMemoryCache = retainFragment.mRetainedCache;
		if (mMemoryCache == null) {
			mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {

				@SuppressLint("NewApi")
				@Override
				protected int sizeOf(String key, Bitmap bitmap) {
					// The cache size will be measured in kilobytes rather than
					// number of items.
					return bitmap.getByteCount() / 1024;
				}
			};

			retainFragment.mRetainedCache = mMemoryCache;
		}
		
		//begin list transaction
		fm = getSupportFragmentManager();
		FragmentTransaction fmTransaction = fm.beginTransaction();
		fmTransaction.add(R.id.ui_container, new MyListFragment(), listFragmentTag);
		fmTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		fmTransaction.commit();
	}
	
	@Override 
	protected void onSaveInstanceState(Bundle savedInstanceState){
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onItemSelected(int dataId) {
		// TODO Auto-generated method stub
		
		MyDetailFragment detailFragment = MyDetailFragment.newInstance(dataId+1);
			
		FragmentTransaction fmTransaction = fm.beginTransaction();
		fmTransaction.replace(R.id.ui_container, detailFragment, detailFragmentTag);
		fmTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		fmTransaction.addToBackStack(detailFragmentTag);
		fmTransaction.commit();
	}
	
	public  void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemoryCache(key) == null)
			mMemoryCache.put(key, bitmap);
	}

	public  Bitmap getBitmapFromMemoryCache(String key) {
		return mMemoryCache.get(key);
	}
}
