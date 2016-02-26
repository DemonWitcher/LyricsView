package com.demoy.testsurfcaeview;

import java.util.ArrayList;
import java.util.List;

import com.demoy.testsurfcaeview.LyricsView.OnProgressListener;

import android.app.Activity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MainActivity extends Activity implements OnProgressListener {

	private LyricsView mLyricsView;
	private List<Lyrics> lycs;
	private SeekBar sb;
	int allTime;
	boolean isTouchSeek;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		init();
	}

	private void init() {
		mLyricsView = (LyricsView) findViewById(R.id.lyricsview);
		mLyricsView.setmOnChangeLineListener(this);
		sb = (SeekBar) findViewById(R.id.sb);
		sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				System.out.println("start");
				isTouchSeek = false;
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				System.out.println("pause");
				isTouchSeek = true;
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				System.out.println("onProgressChanged");
				if (!isTouchSeek) {
					return;
				}
				int time = 0;
				for (int i = 0; i < lycs.size(); ++i) {
					time += lycs.get(i).timeline;
					int index = time * 100 / allTime;
					if (index > progress) {
						mLyricsView.setmCurrentLyc(i);
						mLyricsView.resetAll();
						break;
					}
				}
			}
		});
		lycs = new ArrayList<Lyrics>();
		lycs.add(new Lyrics("我睁开眼睛 却感觉不到天亮", 3000));
		lycs.add(new Lyrics("东西吃一半 莫名其妙哭一场", 4000));
		lycs.add(new Lyrics("我忍住不想 时间变得更漫长", 4000));
		lycs.add(new Lyrics("也与你有关 否则又开始胡思乱想", 4000));
		lycs.add(new Lyrics("我日月无光 忙得不知所以然", 2000));
		lycs.add(new Lyrics("找朋友交谈 其实全帮不上忙", 3000));
		lycs.add(new Lyrics("以为会习惯 有你在才是习惯", 2000));
		lycs.add(new Lyrics("你曾住在我心上", 3000));
		lycs.add(new Lyrics("现在空了一个地方", 3000));
		lycs.add(new Lyrics("原来爱情这么伤 比想象中还难", 4000));
		lycs.add(new Lyrics("泪水总是不听话", 2000));
		lycs.add(new Lyrics("幸福躲起来不声不响", 3000));
		lycs.add(new Lyrics("太多道理太牵强 道理全是一样", 3000));
		lycs.add(new Lyrics("说的时候很简单", 3000));
		lycs.add(new Lyrics("爱上后却正巧打乱", 2000));
		lycs.add(new Lyrics("我日月无光 忙得不知所以然", 3000));
		lycs.add(new Lyrics("找朋友交谈 其实全帮不上忙", 4000));
		lycs.add(new Lyrics("以为会习惯 有你在才是习惯", 3000));
		lycs.add(new Lyrics("你曾住在我心上", 3000));
		lycs.add(new Lyrics("现在空了一个地方", 2000));
		lycs.add(new Lyrics("原来爱情这么伤 比想象中还难", 3000));
		lycs.add(new Lyrics("泪水总是不听话", 2000));
		lycs.add(new Lyrics("幸福躲起来不声不响", 2000));
		lycs.add(new Lyrics("太多道理太牵强 道理全是一样", 2000));
		lycs.add(new Lyrics("说的时候很简单", 4000));
		lycs.add(new Lyrics("爱上后却正巧打乱", 2000));
		lycs.add(new Lyrics("只想变的坚强 强到能够去忘", 3000));
		lycs.add(new Lyrics("无所谓悲伤 只要学会抵抗", 2000));
		lycs.add(new Lyrics("原来爱情这么伤", 3000));
		lycs.add(new Lyrics("原来爱情是这样 这样峰回路转", 3000));
		lycs.add(new Lyrics("泪水明明流不干", 2000));
		lycs.add(new Lyrics("瞎了眼还要再爱一趟", 2000));
		lycs.add(new Lyrics("有一天终于打完 思念的一场战", 3000));
		lycs.add(new Lyrics("回过头再看一看", 2000));
		lycs.add(new Lyrics("原来爱情那么伤", 2000));
		lycs.add(new Lyrics("下次还会不会这样", 3000));

		for (Lyrics lyc : lycs) {
			allTime += lyc.timeline;
		}
		mLyricsView.setmAllTime(allTime);

		mLyricsView.setLycs(lycs);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		System.out.println("onResume");
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onPause() {
		super.onPause();
		System.out.println("onPause");
	}

	@Override
	public void progress(int index) {
		if(sb.getProgress() == index)
			return;
		sb.setProgress(index);
	}

}
