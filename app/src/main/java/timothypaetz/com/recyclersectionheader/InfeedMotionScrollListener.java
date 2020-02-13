package timothypaetz.com.recyclersectionheader;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.TransitionDrawable;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class InfeedMotionScrollListener extends RecyclerView.OnScrollListener {
    public static final String TAG = "MotionScrollListener";
    private final int adPosition;
    private boolean waitForAnimation = true;
    private CountDownTimer stickyTimer;

    public InfeedMotionScrollListener(int adPosition) {
        this.adPosition = adPosition;
    }

    @Override
    public void onScrolled(@NonNull final RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        MotionLayout adViewContainer;
        try {
            adViewContainer = (MotionLayout) recyclerView.getLayoutManager().findViewByPosition(adPosition);
        } catch (Exception e) {
            adViewContainer = null;
            Log.e(TAG, "Failed to get AD view.");
            e.printStackTrace();
        }
        if (adViewContainer == null) return;

        int firstVisibleChild = getFirstVisibleChild(recyclerView);

        if (firstVisibleChild == adPosition && waitForAnimation) {
            final RecyclerView.SimpleOnItemTouchListener interceptScroll = new RecyclerView.SimpleOnItemTouchListener() {
                @Override
                public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                    // Stop only scrolling.
                    return rv.getScrollState() == RecyclerView.SCROLL_STATE_DRAGGING;
                }
            };
            recyclerView.smoothScrollToPosition(adPosition);
            recyclerView.addOnItemTouchListener(interceptScroll);

            stickyTimer = new CountDownTimer(500, 100) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    recyclerView.removeOnItemTouchListener(interceptScroll);
                }
            };
            stickyTimer.start();
            animateAdContainer(recyclerView.getContext(), adViewContainer);
            adViewContainer.transitionToEnd();
            waitForAnimation = false;
        }
    }

    private void animateAdContainer(Context context, final View adViewContainer) {

        int animationDuration = context.getResources().getInteger(R.integer.transition_animation_duration);

        TransitionDrawable transition = (TransitionDrawable) adViewContainer.getBackground();
        transition.startTransition(animationDuration);

        ValueAnimator animator = ValueAnimator.ofInt(adViewContainer.getHeight(),
                (int)context.getResources().getDimension(R.dimen.recycler_section_collapsed_header_height));
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator){
                adViewContainer.getLayoutParams().height = (Integer) valueAnimator.getAnimatedValue();
                adViewContainer.requestLayout();
            }
        });
        animator.setDuration(animationDuration);
        animator.start();
    }

    private int getFirstVisibleChild(@NonNull RecyclerView recyclerView) {
        int firstVisibleChild = 0;
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            firstVisibleChild = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        } else if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            firstVisibleChild = ((GridLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        }
        return firstVisibleChild;
    }
}
