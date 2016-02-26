package com.demoy.testsurfcaeview;

public class Lyrics {
	public int begintime; // 开始时间
    public int endtime; // 结束时间  
    public int timeline; // 单句歌词用时  
    public String lrc; // 单句歌词


    public Lyrics(String lrc,int timeline) {

    	this.lrc = lrc;
    	this.timeline = timeline;
	}
}
