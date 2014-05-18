package com.narr.creategrid;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class GridDrawView extends View {

	private final int NUM_OF_ROW = 8;
	private final int NUM_OF_COLUMN = 6;

	// Make the margin px at least bigger than the stroke 1px to draw the borders of the edge cells(left, right, top, bottom)
	// and consider the rate of width and height(6(num of column):8(num of row))
	// and they should be multiple of 2 for center align and minimum figure.
	private final int MARGIN_FOR_WIDTH = 6;
	private final int MARGIN_FOR_HEIGHT = 8;
	private final int FOR_ALIGN_CENTER_H = MARGIN_FOR_WIDTH / 2;
	private final int FOR_ALIGN_CENTER_V = MARGIN_FOR_HEIGHT / 2;

	private int mCanvasW = -1;
	private int mCanvasH = -1;
	private int mOneCellW;
	private int mOneCellH;
	private int mIcDivide_MarginLeft;
	private int mIcDivide_MarginTop;
	private int mIcDivide_Width;
	private int mIcDivide_Height;

	private GridCellPosInfo[][] mCellPosInfos;
	private Rect mCellRect;
	private Rect mEventDownRect;
	private Rect mEventMoveRect;
	private Rect mIcDivide_Rect;
	private Paint mPaint;
	private Resources mResources;
	private Bitmap mIcDivide_Normal;
	private Bitmap mIcDivide_Pressed;

	private boolean mIsModeMerge = true;
	private boolean mIsCellPressed = false;

	private Toast mToast;




	public GridDrawView(Context context) {
		super(context);
		// Logger.d(this, "GridDrawView(Context context)..!!");
	}

	/**
	 * A constructor when this is inflated from XML
	 */
	public GridDrawView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// Logger.d(this, "GridDrawView(Context context, AttributeSet attrs)..!!");
	}

	public GridDrawView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// Logger.d(this, "GridDrawView(Context context, AttributeSet attrs, int defStyle)..!!");
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		Logger.d(this, "[onSizeChanged]canvas's width ==> " + w + " canvas's height ==> " + h);
		if (mCanvasW == -1 && mCanvasH == -1) {
			init(w, h);
		}
	}

	private void init(int canvasW, int canvasH) {
		// Logger.d(this, "[init]..!!");
		mCanvasW = canvasW;
		mCanvasH = canvasH;
		mOneCellW = (canvasW - MARGIN_FOR_WIDTH) / NUM_OF_COLUMN;
		mOneCellH = (canvasH - MARGIN_FOR_HEIGHT) / NUM_OF_ROW;

		mIcDivide_MarginLeft = mOneCellW / 6;
		mIcDivide_MarginTop = mOneCellH / 6;
		mIcDivide_Width = mOneCellW * 2 / 3;
		mIcDivide_Height = mOneCellH / 2;

		mCellPosInfos = new GridCellPosInfo[NUM_OF_ROW][NUM_OF_COLUMN];
		mCellRect = new Rect();
		mEventDownRect = new Rect();
		mEventMoveRect = new Rect();
		mIcDivide_Rect = new Rect();
		mPaint = new Paint();
		mResources = getResources();
		mIcDivide_Normal = BitmapFactory.decodeResource(mResources, R.drawable.ic_cell_division_normal);
		mIcDivide_Pressed = BitmapFactory.decodeResource(mResources, R.drawable.ic_cell_division_pressed);

		for (int i = 0; i < NUM_OF_ROW; i++) {
			for (int j = 0; j < NUM_OF_COLUMN; j++) {
				mCellPosInfos[i][j] = new GridCellPosInfo();
				GridCellPosInfo cellInfo = mCellPosInfos[i][j];

				Point firstPoint = new Point();
				firstPoint.x = FOR_ALIGN_CENTER_H + j * mOneCellW;
				firstPoint.y = FOR_ALIGN_CENTER_V + i * mOneCellH;
				cellInfo.mStartPoint = firstPoint;

				Point endPoint = new Point();
				endPoint.x = firstPoint.x + mOneCellW;
				endPoint.y = firstPoint.y + mOneCellH;
				cellInfo.mEndPoint = endPoint;

				cellInfo.mIsIncluded = false;
				cellInfo.mIsOneCellSize = true;
			}
		}

		mToast = Toast.makeText(getContext(), mResources.getString(R.string.cell_divide_err_msg), Toast.LENGTH_SHORT);
	}




	@Override
	protected void onDraw(Canvas canvas) {
		// Logger.d(this, "[onDraw]canvas's width ==> " + canvas.getWidth() + " canvas's height ==> " + canvas.getHeight());
		// Logger.d(this, "[onDraw]mIsModeMerge ==> " + mIsModeMerge);

		if (mCellPosInfos != null) {
			for (int i = 0; i < NUM_OF_ROW; i++) {
				for (int j = 0; j < NUM_OF_COLUMN; j++) {
					GridCellPosInfo cellInfo = mCellPosInfos[i][j];
					if (cellInfo.mIsIncluded == false) {
						Point firstPoint = cellInfo.mStartPoint;
						Point endPoint = cellInfo.mEndPoint;
						int left = firstPoint.x;
						int top = firstPoint.y;
						int right = endPoint.x;
						int bottom = endPoint.y;

						// Logger.d(this, "[onDraw]left ==> " + left + " right ==> " + right + " top ==> " + top + " bottom ==> " + bottom);
						mCellRect.set(left, top, right, bottom);

						if (mIsModeMerge) {
							mPaint.setStyle(Paint.Style.STROKE);
							mPaint.setStrokeWidth(1f);
							mPaint.setColor(Color.BLACK);
							canvas.drawRect(mCellRect, mPaint);
						}
						else {
							if (cellInfo.mIsOneCellSize) {
								mPaint.setStyle(Paint.Style.FILL);
								// mPaint.setARGB(255, 248, 248, 248);
								mPaint.setColor(mResources.getColor(R.color.cell_edit_mode_divide_fill));
								canvas.drawRect(mCellRect, mPaint);
							}
							else {
								mIcDivide_Rect.set(left + mIcDivide_MarginLeft, top + mIcDivide_MarginTop,
										left + mIcDivide_Width, top + mIcDivide_Height);
								canvas.drawBitmap(mIcDivide_Normal, null, mIcDivide_Rect, null);
							}
							mPaint.setStyle(Paint.Style.STROKE);
							mPaint.setStrokeWidth(1f);
							// mPaint.setARGB(255, 200, 200, 200);
							mPaint.setColor(mResources.getColor(R.color.cell_edit_mode_divide_stroke));
							canvas.drawRect(mCellRect, mPaint);
						}
					}
				}
			}


			if (mIsCellPressed) {
				// Logger.d(this, "[onDraw]mEventMoveRect left ==> " + mEventMoveRect.left + " right ==> " + mEventMoveRect.right);
				// Logger.d(this, "[onDraw]mEventMoveRect top ==> " + mEventMoveRect.top + " bottom ==> " + mEventMoveRect.bottom);

				mPaint.setStyle(Paint.Style.FILL);
				// mPaint.setARGB(255, 142, 229, 238);
				mPaint.setColor(mResources.getColor(R.color.cell_edit_mode_pressed_fill));
				canvas.drawRect(mEventMoveRect, mPaint);

				if (mIsModeMerge == false) {
					int left = mEventMoveRect.left;
					int top = mEventMoveRect.top;
					int right = mEventMoveRect.right;
					int bottom = mEventMoveRect.bottom;

					if (right - left > mOneCellW || bottom - top > mOneCellH) {
						mIcDivide_Rect.set(left + mIcDivide_MarginLeft, top + mIcDivide_MarginTop,
								left + mIcDivide_Width, top + mIcDivide_Height);
						canvas.drawBitmap(mIcDivide_Pressed, null, mIcDivide_Rect, null);
					}
				}

				mPaint.setStyle(Paint.Style.STROKE);
				mPaint.setStrokeWidth(2f);
				// mPaint.setARGB(255, 0, 134, 169);
				mPaint.setColor(mResources.getColor(R.color.cell_edit_mode_pressed_stroke));
				canvas.drawRect(mEventMoveRect, mPaint);
			}
		}
	}




	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int eventAction = event.getAction();
		int x = (int) event.getX();
		int y = (int) event.getY();

		switch (eventAction) {
		case MotionEvent.ACTION_DOWN:
			// Logger.d(this, "ACTION_DOWN..!! eventX ==> " + x + " eventY ==> " + y);
			if (FOR_ALIGN_CENTER_H < x && x < mCanvasW - FOR_ALIGN_CENTER_H && FOR_ALIGN_CENTER_V < y && y < mCanvasH - FOR_ALIGN_CENTER_V) {
				// Logger.d(this, "ACTION_DOWN..!! x, y coordinates are in boundary..!!");
				mIsCellPressed = true;
				handleDownE(x, y);
			}
			break;

		case MotionEvent.ACTION_MOVE:
			// Logger.d(this, "ACTION_MOVE..!! eventX ==> " + x + " eventY ==> " + y);
			if (mIsCellPressed) {
				if (mIsModeMerge) {
					if (FOR_ALIGN_CENTER_H < x && x < mCanvasW - FOR_ALIGN_CENTER_H && FOR_ALIGN_CENTER_V < y && y < mCanvasH - FOR_ALIGN_CENTER_V) {
						// Logger.d(this, "ACTION_MOVE..!! x, y coordinates are in boundary..!!");
						handleMoveE(x, y);
					}
					else {
						Logger.d(this, "ACTION_MOVE..!! x, y coordinates are not in boundary..!!");
						mIsCellPressed = false;
					}
				}
			}
			break;

		case MotionEvent.ACTION_UP:
			// Logger.d(this, "ACTION_UP..!! eventX ==> " + x + " eventY ==> " + y);
			if (mIsCellPressed) {
				if (mIsModeMerge) {
					handleUpE(true, x, y);
				}
				else {
					handleUpE(false, x, y);
				}
				mIsCellPressed = false;
			}
			break;

		default:
			Logger.d(this, "default..!! eventAction ==> " + eventAction + " eventX ==> " + x + " eventY ==> " + y);
			break;
		}

		invalidate(); // redraw the canvas
		return true;
	}


	private void handleDownE(int x, int y) {
		// Logger.d(this, "[handleDownE]..!!");
		if (mCellPosInfos != null) {
			for (int i = 0; i < NUM_OF_ROW; i++) {
				for (int j = 0; j < NUM_OF_COLUMN; j++) {
					GridCellPosInfo cellInfo = mCellPosInfos[i][j];
					if (cellInfo.mIsIncluded == false) {
						Point firstPoint = cellInfo.mStartPoint;
						Point endPoint = cellInfo.mEndPoint;
						int firstX = firstPoint.x;
						int firstY = firstPoint.y;
						int endX = endPoint.x;
						int endY = endPoint.y;
						if (firstX <= x && x < endX && firstY <= y && y < endY) {
							Logger.d(this, "[handleDownE]x ==> " + x + " y ==> " + y);
							Logger.d(this, "[handleDownE]firstX ==> " + firstX + " firstY ==> " + firstY);
							Logger.d(this, "[handleDownE]endX ==> " + endX + " endY ==> " + endY);
							mEventDownRect.set(firstX, firstY, endX, endY);
							mEventMoveRect.set(firstX, firstY, endX, endY);
							return;
						}
					}
				}
			}
		}
	}

	private void handleMoveE(int x, int y) {
		// Logger.d(this, "[handleMoveE]..!!");
		if (mCellPosInfos != null) {
			for (int i = 0; i < NUM_OF_ROW; i++) {
				for (int j = 0; j < NUM_OF_COLUMN; j++) {
					GridCellPosInfo cellInfo = mCellPosInfos[i][j];
					if (cellInfo.mIsIncluded == false) {
						Point firstPoint = cellInfo.mStartPoint;
						Point endPoint = cellInfo.mEndPoint;
						int firstX = firstPoint.x;
						int firstY = firstPoint.y;
						int endX = endPoint.x;
						int endY = endPoint.y;
						// Logger.d(this, "[handleMoveE]firstX ==> " + firstX + " firstY ==> " + firstY);
						// Logger.d(this, "[handleMoveE]endX ==> " + endX + " endY ==> " + endY);
						if (firstX <= x && x < endX && firstY <= y && y < endY) {
							int downLeft = mEventDownRect.left;
							int downRight = mEventDownRect.right;
							int downTop = mEventDownRect.top;
							int downBottom = mEventDownRect.bottom;

							int moveLeft = firstX < downLeft ? firstX : downLeft;
							int moveRight = endX > downRight ? endX : downRight;
							int moveTop = firstY < downTop ? firstY : downTop;
							int moveBottom = endY > downBottom ? endY : downBottom;
							Rect moveRect = new Rect(moveLeft, moveTop, moveRight, moveBottom);
							// Logger.d(this, "[handleMoveE]moveLeft ==> " + moveLeft + " moveRight ==> " + moveRight);
							// Logger.d(this, "[handleMoveE]moveTop ==> " + moveTop + " moveBottom ==> " + moveBottom);

							checkBoundaryForEditMode(moveRect);

							mEventMoveRect.set(moveRect.left, moveRect.top, moveRect.right, moveRect.bottom);
							return;
						}
					}
				}
			}
		}
	}

	private void checkBoundaryForEditMode(Rect moveRect) {
		int rowMin = (moveRect.top - FOR_ALIGN_CENTER_V) / mOneCellH;
		int rowMax = (moveRect.bottom - FOR_ALIGN_CENTER_V) / mOneCellH;
		int columnMin = (moveRect.left - FOR_ALIGN_CENTER_H) / mOneCellW;
		int columnMax = (moveRect.right - FOR_ALIGN_CENTER_H) / mOneCellW;
		// Logger.d(this, "[checkBoundaryForEditMode]rMin,rMax,cMin,cMax ==> " + rowMin + " " + rowMax + " " + columnMin + " " + columnMax);

		for (int row = rowMin; row < rowMax; row++) {
			for (int col = columnMin; col < columnMax; col++) {
				GridCellPosInfo cellInfo = mCellPosInfos[row][col];
				Point firstPoint = cellInfo.mStartPoint;
				Point endPoint = cellInfo.mEndPoint;
				int left = firstPoint.x;
				int right = endPoint.x;
				int top = firstPoint.y;
				int bottom = endPoint.y;

				boolean needToCheckAgain = false;

				if (left < moveRect.left) {
					moveRect.left = left;
					needToCheckAgain = true;
				}

				if (right > moveRect.right) {
					moveRect.right = right;
					needToCheckAgain = true;
				}

				if (top < moveRect.top) {
					moveRect.top = top;
					needToCheckAgain = true;
				}

				if (bottom > moveRect.bottom) {
					moveRect.bottom = bottom;
					needToCheckAgain = true;
				}

				if (needToCheckAgain) {
					checkBoundaryForEditMode(moveRect);
					return;
				}
			}
		}
	}

	private void handleUpE(boolean isModeMerge, int x, int y) {
		// Logger.d(this, "[handleUpE]isModeMerge ==> + " + isModeMerge + "..!!");
		if (isModeMerge) {
			int moveLeft = mEventMoveRect.left;
			int moveRight = mEventMoveRect.right;
			int moveTop = mEventMoveRect.top;
			int moveBottom = mEventMoveRect.bottom;

			int rowMin = (moveTop - FOR_ALIGN_CENTER_V) / mOneCellH;
			int rowMax = (moveBottom - FOR_ALIGN_CENTER_V) / mOneCellH;
			int columnMin = (moveLeft - FOR_ALIGN_CENTER_H) / mOneCellW;
			int columnMax = (moveRight - FOR_ALIGN_CENTER_H) / mOneCellW;
			// Logger.d(this, "[handleUpE]rMin,rMax,cMin,cMax ==> " + rowMin + " " + rowMax + " " + columnMin + " " + columnMax);

			if (rowMax - rowMin > 1 || columnMax - columnMin > 1) {
				if (mCellPosInfos != null) {
					for (int i = rowMin; i < rowMax; i++) {
						for (int j = columnMin; j < columnMax; j++) {
							GridCellPosInfo cellInfo = mCellPosInfos[i][j];

							Point firstPoint = cellInfo.mStartPoint;
							Point endPoint = cellInfo.mEndPoint;
							firstPoint.x = moveLeft;
							firstPoint.y = moveTop;
							endPoint.x = moveRight;
							endPoint.y = moveBottom;

							cellInfo.mIsIncluded = j == columnMin && i == rowMin ? false : true;
							cellInfo.mIsOneCellSize = false;
						}
					}
				}
			}
		}
		else {
			int downLeft = mEventDownRect.left;
			int downRight = mEventDownRect.right;
			int downTop = mEventDownRect.top;
			int downBottom = mEventDownRect.bottom;

			if (downLeft <= x && x <= downRight && downTop <= y && y <= downBottom) {
				if (downRight - downLeft > mOneCellW || downBottom - downTop > mOneCellH) {
					int rowMin = (downTop - FOR_ALIGN_CENTER_V) / mOneCellH;
					int rowMax = (downBottom - FOR_ALIGN_CENTER_V) / mOneCellH;
					int columnMin = (downLeft - FOR_ALIGN_CENTER_H) / mOneCellW;
					int columnMax = (downRight - FOR_ALIGN_CENTER_H) / mOneCellW;
					// Logger.d(this, "[handleUpE]rMin2,rMax2,cMin2,cMax2 ==> " + rowMin + " " + rowMax + " " + columnMin + " " + columnMax);

					if (mCellPosInfos != null) {
						for (int i = rowMin; i < rowMax; i++) {
							for (int j = columnMin; j < columnMax; j++) {
								GridCellPosInfo cellInfo = mCellPosInfos[i][j];

								Point firstPoint = cellInfo.mStartPoint;
								Point endPoint = cellInfo.mEndPoint;
								firstPoint.x = FOR_ALIGN_CENTER_H + j * mOneCellW;
								firstPoint.y = FOR_ALIGN_CENTER_V + i * mOneCellH;
								endPoint.x = firstPoint.x + mOneCellW;
								endPoint.y = firstPoint.y + mOneCellH;

								cellInfo.mIsIncluded = false;
								cellInfo.mIsOneCellSize = true;
							}
						}
					}
				}
				else {
					mToast.show();
				}
			}
		}
	}




	private static class GridCellPosInfo {
		Point mStartPoint;
		Point mEndPoint;
		boolean mIsIncluded;
		boolean mIsOneCellSize;
	}




	public void setEditMode(boolean isModeMerge) {
		// Logger.d(this, "[setEditMode]isMerge ==> " + isMerge);
		mIsModeMerge = isModeMerge ? true : false;
		invalidate();
	}

	public boolean areCellsAllDivided() {
		// Logger.d(this, "[areCellsAllDivided]..!!");
		if (mCellPosInfos != null) {
			for (int i = 0; i < NUM_OF_ROW; i++) {
				for (int j = 0; j < NUM_OF_COLUMN; j++) {
					GridCellPosInfo cellInfo = mCellPosInfos[i][j];
					if (cellInfo.mIsOneCellSize == false) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public List<Rect> getGridInfo() {
		// Logger.d(this, "[getGridInfo]..!!");
		List<Rect> gridInfo = new ArrayList<Rect>();
		if (mCellPosInfos != null) {
			for (int i = 0; i < NUM_OF_ROW; i++) {
				for (int j = 0; j < NUM_OF_COLUMN; j++) {
					GridCellPosInfo rci = mCellPosInfos[i][j];
					if (rci.mIsIncluded == false) {
						Point endPoint = rci.mEndPoint;
						int colSpan = ((endPoint.x - FOR_ALIGN_CENTER_H) / mOneCellW) - j;
						int rowSpan = ((endPoint.y - FOR_ALIGN_CENTER_V) / mOneCellH) - i;
						// Logger.d(this, "[getGridInfo]row,col,rowSpan,colSpan ==> " + i + " " + j + " " + rowSpan + " " + colSpan);

						// new Rect(row, col, rowSpan, colSpan)
						Rect cell = new Rect(i, j, rowSpan, colSpan);
						gridInfo.add(cell);
					}
				}
			}
		}
		return gridInfo;
	}

	public Toast getToast() {
		return mToast;
	}
}