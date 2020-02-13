package timothypaetz.com.recyclersectionheader;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import androidx.annotation.NonNull;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.view.View.VISIBLE;

public class InfeedMotionScrollListener extends RecyclerView.OnScrollListener {
    public static final String TAG = "MotionScrollListener";
    private final int adPosition;
    private boolean waitForAnimation = true;
    private CountDownTimer stickyTimer;
    private int animationDuration;

    public InfeedMotionScrollListener(int adPosition, int animationDuration) {
        this.adPosition = adPosition;
        this.animationDuration = animationDuration;
    }

    @Override
    public void onScrolled(@NonNull final RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        ViewGroup adViewContainer;
        try {
            adViewContainer = (ViewGroup) recyclerView.getLayoutManager().findViewByPosition(adPosition);
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
//            recyclerView.smoothScrollToPosition(adPosition);
            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView,new RecyclerView.State(),adPosition);
            recyclerView.addOnItemTouchListener(interceptScroll);

            stickyTimer = new CountDownTimer(animationDuration, 100) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    recyclerView.removeOnItemTouchListener(interceptScroll);
                    RecyclerAdItemDecoration sectionItemDecoration =
                            new RecyclerAdItemDecoration(recyclerView.getContext()
                                    .getResources().getDimensionPixelSize(R.dimen.recycler_section_header_height),
                                    true,
                                    MainActivity.getAdPositionsList());
                    recyclerView.addItemDecoration(sectionItemDecoration);
                    this.cancel();
                }
            };
            stickyTimer.start();
            animateAdContainer(recyclerView.getContext(), adViewContainer);
            waitForAnimation = false;
        }
    }

    private void animateAdContainer(Context context, final ViewGroup adViewContainer) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            adViewContainer.setTranslationZ(10);
            adViewContainer.setElevation(2);
        }
        //animate background change
        TransitionDrawable transition = (TransitionDrawable) adViewContainer.getBackground();
        transition.startTransition(animationDuration);

        //animate changing height of root view
        ValueAnimator rootAnimator = ValueAnimator.ofInt(adViewContainer.getHeight(),
                (int)context.getResources().getDimension(R.dimen.recycler_section_collapsed_header_height));
        rootAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator){
                adViewContainer.getLayoutParams().height = (Integer) valueAnimator.getAnimatedValue();
                adViewContainer.requestLayout();
            }
        });
        rootAnimator.setDuration(animationDuration);
        rootAnimator.start();

        scaleView(adViewContainer.getChildAt(0), 0.65f, animationDuration);
        viewGoneAnimator(adViewContainer.getChildAt(1));
        viewGoneAnimator(adViewContainer.getChildAt(2));
        viewVisibleAnimator(adViewContainer.getChildAt(3));
        viewVisibleAnimator(adViewContainer.getChildAt(4));
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

    public void scaleView(final View v, final float endScale, int animationDuration) {
        v.setPivotX(0f);
        v.setPivotY(0f);
        v.animate()
                .scaleX(endScale)
                .scaleY(endScale)
                .setDuration(animationDuration);
    }

    private void viewGoneAnimator(final View view) {

        view.animate()
                .alpha(0f)
                .translationY(-100)
                .setDuration(animationDuration/4)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
//                        view.setVisibility(View.GONE);
                    }
                });
    }

    private void viewVisibleAnimator(final View view) {

        view.animate()
                .alpha(1f)
                .setDuration(animationDuration/2)
                .setStartDelay(animationDuration/2)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                    }
                });
    }
}
