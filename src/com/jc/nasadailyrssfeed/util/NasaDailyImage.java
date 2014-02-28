package com.jc.nasadailyrssfeed.util;

public class NasaDailyImage {
    
	private int key_code;
	private String title;
	private String date;
	private String image;
	private String description;
	
	public NasaDailyImage(){}
	
	public NasaDailyImage(int key_code,String title,String date,String img,String desc){
		this.key_code=key_code;
		this.title=title;
		this.date=date;
		this.image=img;
		this.description=desc;
	}
	
	public int getKey_code() {
		return key_code;
	}

	public void setKey_code(int key_code) {
		this.key_code = key_code;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
