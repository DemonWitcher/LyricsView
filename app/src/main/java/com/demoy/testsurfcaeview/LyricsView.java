package com.demoy.testsurfcaeview;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * @author demonY
 *
 */
public class LyricsView extends SurfaceView implements SurfaceHolder.Callback {

	private SurfaceHolder mSHolder;
	private UiThread mUiThread;
	private int mCurrentLyc, mHeightOffset, mLineIndex, mWidth, mHeight,
			mColorCurrent, mColorElse, mColorDivider,mAllTime,mCountIndex,mLycHeight;
	private float mFgradientIndex,mTouchX;
	private List<Lyrics> mListlycs;
	private boolean mIsTouch;
	private boolean mIsGridetCurrent = true;
	private boolean mIsFirstLyc = true;

	private static final int TOUCH_CHANGE_HEIGHT = 20;
	private static final int CHANGE_LINE_TIME = 500;
	private static final int LYC_INTERVAL = 62;
	private static final int FRAMES = 16;
	private static final float FRAMES_F = Float.valueOf(FRAMES);
	private static final float LYC_SIZE = 42.0f;
	private static final float CURRENT_LYC_SIZE = 50.0f;
	private static float CHANGE_LYC_SIZE = LYC_SIZE;
	private static float CHANGE_CURRENT_LYC_SIZE = CURRENT_LYC_SIZE;
	private static Bitmap mBtBg;
	private static final float TVSIZE_CHANGE_INDEX = (CURRENT_LYC_SIZE - LYC_SIZE)
			/ (CHANGE_LINE_TIME / FRAMES_F);

	private OnProgressListener mOnChangeLineListener;

	public LyricsView(Context context) {
		super(context);
		init();
	}

	public LyricsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public LyricsView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		mColorCurrent = getResources().getColor(R.color.current_lyc);
		mColorElse = getResources().getColor(R.color.else_lyc);
		mColorDivider = getResources().getColor(R.color.divider);
		mBtBg = BitmapFactory.decodeResource(getResources(), R.drawable.bg);
		mSHolder = this.getHolder();
		mSHolder.addCallback(this);
		mUiThread = new UiThread(mSHolder);// 创建一个绘图线程
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mHeight = getHeight();
		mWidth = getWidth();
	}

	public void pause() {
		mUiThread.isRun = false;
	}

	public void start() {
		if (mUiThread.getState() == Thread.State.TERMINATED) {
			mUiThread = new UiThread(mSHolder);
			mUiThread.start();
		} else {
			if (!mUiThread.isAlive()) {
				mUiThread.isRun = true;
				mUiThread.start();
			}
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		System.out.println("surfaceChanged");
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		System.out.println("surfaceCreated");
		start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		System.out.println("surfaceDestroyed");
		pause();
	}

	class UiThread extends Thread {
		private SurfaceHolder holder;
		public boolean isRun;

		public UiThread(SurfaceHolder holder) {
			this.holder = holder;
			isRun = true;
		}

		@Override
		public void run() {
			while (isRun) {
				Canvas c = null;
				try {
					synchronized (holder) {
						if(mColorCurrent == mListlycs.size()){
							return;
						}
						c = holder.lockCanvas();
						c.drawBitmap(mBtBg, 0, 0, null);
						Paint p = getPaint(c);

						// 计算歌词高度
						Rect rect = new Rect();
						p.getTextBounds(mListlycs.get(mCurrentLyc).lrc, 0,
								getCurrentLyrics().lrc.length(), rect);
						mLycHeight = rect.height();

						// 绘制进度线
						if (mIsTouch) {
							p.setColor(mColorDivider);
							c.drawLine(0, mHeight / 2, mWidth, mHeight / 2, p);
						}
						// 绘制当前歌词
						if (mIsGridetCurrent) {
							p.setShader(getLinearGradient(
									Float.valueOf(getCurrentLyrics().timeline),
									p.measureText(getCurrentLyrics().lrc)));
						}
						c.drawText(getCurrentLyrics().lrc, mWidth / 2, mHeight
								/ 2 + mHeightOffset + mLycHeight / 2, p);

						// 绘制上面歌词
						p.setShader(null);
						p.setTextSize(LYC_SIZE);
						int oldHeight = mHeight / 2;
						for (int i = mCurrentLyc - 1; i > -1; --i) {
							oldHeight = oldHeight - LYC_INTERVAL;
							c.drawText(mListlycs.get(i).lrc, mWidth / 2,
									oldHeight + mHeightOffset + mLycHeight / 2, p);
						}

						// 绘制下面歌词
						int nextHeight = mHeight / 2;
						for (int i = mCurrentLyc + 1; i < mListlycs.size(); ++i) {
							if (i == mCurrentLyc + 1 && !mIsGridetCurrent) {
								p.setTextSize(CHANGE_LYC_SIZE);
								p.setShader(getLinearGradient(
										Float.valueOf(mListlycs
												.get(mCurrentLyc + 1).timeline),
										p.measureText(mListlycs
												.get(mCurrentLyc + 1).lrc)));
							}
							nextHeight = nextHeight + LYC_INTERVAL;
							c.drawText(mListlycs.get(i).lrc, mWidth / 2,
									nextHeight + mHeightOffset + mLycHeight / 2,
									p);
							p.setShader(null);
							p.setTextSize(LYC_SIZE);
						}

						// 切换下一句
						mLineIndex += FRAMES;
						mCountIndex += FRAMES;
						if(mOnChangeLineListener!=null){
							mOnChangeLineListener.progress(mCountIndex*100/mAllTime);
						}
						if (mLineIndex > (mIsFirstLyc ? getCurrentLyrics().timeline
								: (getCurrentLyrics().timeline - CHANGE_LINE_TIME))) {
							if (mIsGridetCurrent) {
								mFgradientIndex = 0.0f;
								CHANGE_LYC_SIZE = LYC_SIZE;
							}
							mIsGridetCurrent = false;
							mHeightOffset -= LYC_INTERVAL
									/ (CHANGE_LINE_TIME / FRAMES);
							CHANGE_LYC_SIZE += TVSIZE_CHANGE_INDEX;
							CHANGE_CURRENT_LYC_SIZE -= TVSIZE_CHANGE_INDEX;
							if (mHeightOffset + LYC_INTERVAL == 0) {
								CHANGE_CURRENT_LYC_SIZE = CURRENT_LYC_SIZE;
								mIsGridetCurrent = true;
								mIsFirstLyc = false;
								++mCurrentLyc;
								mHeightOffset = 0;
								mLineIndex = 0;
							}
						}

					}
					Thread.sleep(FRAMES);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (c != null) {
						holder.unlockCanvasAndPost(c);
					}
				}
			}
			System.out.println("thread dead");
		}

	}

	private Shader getLinearGradient(float lineTime, float textWith) {
		int[] colors = new int[] { mColorCurrent, mColorElse };
		mFgradientIndex += 1 / (lineTime / FRAMES_F);
		float[] f = new float[] { mFgradientIndex, mFgradientIndex };
		Shader shader = new LinearGradient((mWidth - textWith) / 2, 0,
				(mWidth + textWith) / 2, 0, colors, f, TileMode.CLAMP);
		return shader;
	}

	private Paint getPaint(Canvas c) {
		Paint p = new Paint(); // 创建画笔
		p.setColor(mColorElse);
		p.setTextSize(CHANGE_CURRENT_LYC_SIZE);
		p.setTextAlign(Paint.Align.CENTER);
		return p;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		return super.dispatchTouchEvent(event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		float x = event.getX();
		switch (action) {
		case MotionEvent.ACTION_DOWN: {
			mIsTouch = true;
			this.mTouchX = x;
		}
			break;
		case MotionEvent.ACTION_MOVE: {
			if(x-this.mTouchX>TOUCH_CHANGE_HEIGHT){
				if(mCurrentLyc == mListlycs.size()-1){
					return true;
				}
				this.mTouchX = x;
				++mCurrentLyc;
				resetAll();
			}else if(x-this.mTouchX<-TOUCH_CHANGE_HEIGHT){
				if(mCurrentLyc == 0){
					return true;
				}
				this.mTouchX = x;
				--mCurrentLyc;
				resetAll();
			}
		}
			break;
		case MotionEvent.ACTION_UP: {
			mIsTouch = false;
		}
			break;
		case MotionEvent.ACTION_CANCEL: {
			mIsTouch = false;
		}
			break;

		default:
			break;
		}
		return true;
	}

	private Lyrics getCurrentLyrics() {
		return mListlycs.get(mCurrentLyc);
	}

	public void resetAll() {
		mHeightOffset = 0;
		mLineIndex = 0;
		mFgradientIndex = 0.0f;
		CHANGE_LYC_SIZE = LYC_SIZE;
		CHANGE_CURRENT_LYC_SIZE = CURRENT_LYC_SIZE;
		mIsGridetCurrent = true;
		mIsFirstLyc = true;
		mCountIndex = 0;
		for (int i = 0; i < mCurrentLyc; ++i) {
			mCountIndex += mListlycs.get(i).timeline;
		}
	}

	public List<Lyrics> getLycs() {
		return mListlycs;
	}

	public void setLycs(List<Lyrics> lycs) {
		this.mListlycs = lycs;
	}

	public int getmCurrentLyc() {
		return mCurrentLyc;
	}

	public void setmCurrentLyc(int mCurrentLyc) {
		this.mCurrentLyc = mCurrentLyc;
	}

	public OnProgressListener getmOnChangeLineListener() {
		return mOnChangeLineListener;
	}

	public int getmAllTime() {
		return mAllTime;
	}

	public void setmAllTime(int mAllTime) {
		this.mAllTime = mAllTime;
	}

	public void setmOnChangeLineListener(
			OnProgressListener mOnChangeLineListener) {
		this.mOnChangeLineListener = mOnChangeLineListener;
	}
	public static interface OnProgressListener {
		void progress(int index);
	}
}