package com.jc.nasadailyrssfeed;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

public class DetailFragmentActvity extends FragmentActivity{
     
    NasaDailyPagerAdapter pagerAdapter;
    ViewPager viewPager;
    
    @Override
    public void onCreate(Bundle savedInstanceState){
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.viewpager);
    	
    	Intent intent = getIntent();
    	int countNum= intent.getIntExtra("countNum", 0);
    	pagerAdapter = new NasaDailyPagerAdapter(getSupportFragmentManager(),countNum);
    	
    	viewPager = (ViewPager)findViewById(R.id.pager);
    	viewPager.setAdapter(pagerAdapter);
    }
}
