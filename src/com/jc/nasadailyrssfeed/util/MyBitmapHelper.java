package com.jc.nasadailyrssfeed.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class MyBitmapHelper {
    
	//load a scaled down version int memory
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth,int reqHeight){
		
		 // Raw height and width of image
		final int height=options.outHeight;
		final int width=options.outWidth;
		int inSampleSize=1;
		
		if(height>reqHeight || width>reqWidth){
			
			final int halfHeight=height/2;
			final int halfWidth=height/2;
			
			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
	        // height and width larger than the requested height and width.
			while( (halfHeight/inSampleSize)>reqHeight || 
					(halfWidth/inSampleSize)>reqWidth ){
				 inSampleSize *=2;
			}
		}
			
				return inSampleSize;	
	}
	
	public static Bitmap decodeSampleBitmap(String imageUri,int reqWidth,int reqHeight){
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds=true;
		BitmapFactory.decodeFile(imageUri, options);
		
		 // Calculate inSampleSize
		options.inSampleSize=calculateInSampleSize(options,reqWidth,reqHeight);
		
		// Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeFile(imageUri, options);
	}
}
