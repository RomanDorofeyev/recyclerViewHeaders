package timothypaetz.com.recyclersectionheader;

import android.graphics.Canvas;
import android.graphics.Rect;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.Collections;
import java.util.List;


public class RecyclerAdItemDecoration extends RecyclerView.ItemDecoration {

    private static final String TAG = "RecyclerAdItemDecor";

    private int headerOffset;
    private final boolean sticky;
    private List<Integer> adPositions;
    private ViewGroup adView;


    public RecyclerAdItemDecoration(int headerHeight, boolean sticky, List<Integer> adPositions, ViewGroup adView) {
        headerOffset = adView.getHeight();
        this.sticky = sticky;
        this.adPositions = adPositions;
        this.adView = adView;

    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

//        int pos = parent.getChildAdapterPosition(view);
//        if (adPositions.contains(pos)) {
//            outRect.top = headerOffset;
//        }
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c,
                         parent,
                         state);

        if (adView != null) {
//            adView = inflateAdView(parent);
            fixLayoutSize(adView, parent);
        }

        boolean isPreviousItemAd = false;
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            final int position = parent.getChildAdapterPosition(child);

            if ((!isPreviousItemAd || adPositions.contains(position)) && position >= Collections.min(adPositions)) {

                int firstVisibleChild = 0;
                if (parent.getLayoutManager() instanceof LinearLayoutManager){
                    firstVisibleChild = ((LinearLayoutManager)parent.getLayoutManager()).findFirstVisibleItemPosition();
                }else if (parent.getLayoutManager() instanceof GridLayoutManager){
                    firstVisibleChild = ((GridLayoutManager)parent.getLayoutManager()).findFirstVisibleItemPosition();
                }
                if (firstVisibleChild >= adPositions.get(0)) {
                    drawHeader(c, child, adView, parent);
                    isPreviousItemAd = true;
                }
            }
        }

//        Rect rect = new Rect();
//        adView.getDrawingRect(rect);
//
//        adView.setScaleY(0);
//        adView.setScaleX(0);
//
//        adView.animate()
//                .scaleY(10)
//                .scaleX(10)
//                .setDuration(1000)
//                .setInterpolator(new DecelerateInterpolator())
//                .start();


//        Log.d(TAG, "rect.top  = " + rect.top  +  "   adView.getTop() =  "
//                                +  adView.getTop() +  "   rect.bottom  "  +  rect.bottom);
//        Log.d(TAG, "adView.getX()  = " + adView.getX()  +  "   adView.getY() =  " +  adView.getY());
    }

    private void transformStickyHeader(Canvas c, View child, LinearLayout adView) {

        View adContainer = adView.getChildAt(0);

        if (child.getTop() - adView.getHeight() < 0){
//            adView.setBackgroundColor(Color.DKGRAY);

            //            adContainer.setBackgroundColor(Color.YELLOW);

//            ViewGroup.LayoutParams params = adContainer.getLayoutParams();
//            params.height = 50;
//            adContainer.requestLayout();

        }else {
//            adContainer.setBackgroundColor(Color.DKGRAY);

//            adView.setBackgroundColor(Color.LTGRAY);

        }
    }

    private void drawHeader(Canvas c, View child, View adView,  RecyclerView parent) {
        c.save();
        if (sticky) {
            c.translate(0, Math.max(0, child.getTop() - adView.getHeight()));
        } else {
            c.translate(0, child.getTop() - adView.getHeight());
        }

        if (child.getTop() - adView.getHeight() < 0){
//            c.scale(1.0f, 0.5f);
        }else {
//            c.scale(1f, 1f);
        }

        adView.draw(c);
        c.restore();
    }

    private ConstraintLayout inflateAdView(RecyclerView parent) {
        return (ConstraintLayout)LayoutInflater.from(parent.getContext())
                             .inflate(R.layout.collapsed_ad_item_layout,
                                      parent,
                                      false);
    }

    /**
     * Measures the header view to make sure its size is greater than 0 and will be drawn
     * https://yoda.entelect.co.za/view/9627/how-to-android-recyclerview-item-decorations
     */
    private void fixLayoutSize(View view, ViewGroup parent) {
        int widthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth(),
                                                         View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight(),
                                                          View.MeasureSpec.UNSPECIFIED);

        int childWidth = ViewGroup.getChildMeasureSpec(widthSpec,
                                                       parent.getPaddingLeft() + parent.getPaddingRight(),
                                                       view.getLayoutParams().width);
        int childHeight = ViewGroup.getChildMeasureSpec(heightSpec,
                                                        parent.getPaddingTop() + parent.getPaddingBottom(),
                                                        view.getLayoutParams().height);

        view.measure(childWidth, childHeight);

        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
    }


}


