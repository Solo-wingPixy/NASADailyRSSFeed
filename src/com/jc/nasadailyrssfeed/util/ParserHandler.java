package com.jc.nasadailyrssfeed.util;
/*
 * InputStream  只能被读取一次，下一次读取为空   解决方法：将读取到的inputStream 转换成字节数组，在进行多次读取
 */

import java.util.LinkedList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


import android.util.Log;

public class ParserHandler extends DefaultHandler{
    private static final String TAG = "saxtag";
    
    private LinkedList<NasaDailyImage> imageOfTheDayList=null;
    private NasaDailyImage nasaDailyImage=null;
    
    StringBuilder builder = new StringBuilder();
    String imageUri=null;
    
    @Override
    public void startDocument(){
    	Log.w(TAG, "解析文档开始");
    	imageOfTheDayList = new LinkedList<NasaDailyImage>();
    }
    
    @Override
    public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException{
    	
    	builder.delete(0, builder.length());
		if (localName.equals("item")) {
			nasaDailyImage = new NasaDailyImage();
		}
		if (localName.equals("enclosure")) {
		    imageUri = attributes.getValue("url");
			Log.w(TAG, "uri" + imageUri);
		}
    }
    
    @Override
    public void endElement(String uri, String localName, String qName)
                throws SAXException{
    	
		if (localName.equals("title") && nasaDailyImage != null) {
			Log.w(TAG, "title=: "+builder.toString());
			nasaDailyImage.setTitle(builder.toString());
		}

		if (localName.equals("pubDate") && nasaDailyImage != null) {
			Log.w(TAG,"pubDate=: "+ builder.toString());
			nasaDailyImage.setDate(builder.toString());
		}

		if (localName.equals("description") && nasaDailyImage != null) {
			Log.w(TAG, "description=: "+builder.toString());
			nasaDailyImage.setDescription(builder.toString());
			builder.setLength(0);
		}

		if (localName.equals("enclosure") && nasaDailyImage != null) {
			Log.w(TAG, "decode uri " + imageUri);
			nasaDailyImage.setImage(imageUri);
		}
		
		if (localName.equals("item") && nasaDailyImage!= null) {
    		imageOfTheDayList.add(nasaDailyImage);
    		nasaDailyImage = null;
		}
    }
    
    @Override
    public void characters(char ch[], int start, int length){
    	if (nasaDailyImage != null) {
			builder.append(ch, start, length);
		}
    }
    
    public LinkedList<NasaDailyImage> getLinkedList(){
    	Log.w(TAG, "object number=: "+imageOfTheDayList.size());
    	if(imageOfTheDayList.size()>0)
    	   return imageOfTheDayList;
    	else
    	   return null;
    }
}
