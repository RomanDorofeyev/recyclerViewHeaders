package timothypaetz.com.recyclersectionheader;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brandio.ads.Controller;
import com.brandio.ads.InfeedPlacement;
import com.brandio.ads.exceptions.DioSdkException;

public abstract class InfeedMotionScrollListener extends RecyclerView.OnScrollListener {
    public static final String TAG = "MotionScrollListener";
    private static final int ANIMATION_DURATION = 500;
    private final int adPosition;
    private boolean animationStarted = false;
    private boolean animationFinished = false;
    private CountDownTimer stickyTimer;
    private int collapsedHeight;

    public InfeedMotionScrollListener(Context context, int adPosition) {
        this.adPosition = adPosition;
        collapsedHeight = (int) context.getResources().getDimension(R.dimen.recycler_section_collapsed_header_height);
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

//        if (animationFinished){
//            adViewContainer.removeAllViews();
//            adViewContainer.getLayoutParams().height = 0;
//            adViewContainer.requestLayout();
//        }

        int firstVisibleChild = getFirstVisibleChild(recyclerView);

        if (firstVisibleChild == adPosition && !animationFinished && !animationStarted) {

            final RecyclerView.SimpleOnItemTouchListener interceptScroll = new RecyclerView.SimpleOnItemTouchListener() {
                @Override
                public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                    // Stop only scrolling.
                    return rv.getScrollState() == RecyclerView.SCROLL_STATE_DRAGGING;
                }
            };
            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, new RecyclerView.State(), adPosition);
            recyclerView.addOnItemTouchListener(interceptScroll);

            final ViewGroup finalAdViewContainer = adViewContainer;
            stickyTimer = new CountDownTimer(ANIMATION_DURATION + 100, ANIMATION_DURATION) {
                @Override
                public void onTick(long millisUntilFinished) {
                    scrollToPositionWithOffset(recyclerView);

                }

                @Override
                public void onFinish() {
                    recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, new RecyclerView.State(), adPosition);
                    recyclerView.removeOnItemTouchListener(interceptScroll);
//                    RecyclerAdItemDecoration sectionItemDecoration =
//                            new RecyclerAdItemDecoration(recyclerView.getContext()
//                                    .getResources().getDimensionPixelSize(R.dimen.recycler_section_header_height),
//                                    true,
//                                    MainActivity.getAdPositionsList(), finalAdViewContainer);
//                    recyclerView.addItemDecoration(sectionItemDecoration);


                    Activity activity = (Activity) recyclerView.getContext();
                    ViewGroup mainContentWindow = activity.findViewById(android.R.id.content);
                    ViewGroup adContainerParent = (ViewGroup) finalAdViewContainer.getParent();
                    if (mainContentWindow != null && adContainerParent != null) {
                        adContainerParent.removeView(finalAdViewContainer);
                        mainContentWindow.addView(finalAdViewContainer);
                        removeAdPositionFromList(adPosition);


                        finalAdViewContainer.setOnTouchListener(new View.OnTouchListener() {
                            float dX = 0;
                            float startX = finalAdViewContainer.getX();
                            Runnable removeView = new Runnable() {
                                @Override
                                public void run() {
                                    ((ViewGroup) finalAdViewContainer.getParent()).removeView(finalAdViewContainer);
                                }
                            };

                            @Override
                            public boolean onTouch(View view, MotionEvent event) {

                                Log.e(TAG, "OnTouchEvent");

                                switch (event.getAction()) {

                                    case MotionEvent.ACTION_UP:

                                        if (view.getX() > view.getWidth() / 2) {
                                            view.animate()
                                                    .withEndAction(removeView)
                                                    .x(view.getX() + view.getWidth())
                                                    .setDuration(250)
                                                    .start();
                                        } else if (-view.getX() > view.getWidth() / 2) {
                                            view.animate()
                                                    .withEndAction(removeView)
                                                    .x(view.getX() - view.getWidth())
                                                    .setDuration(250)
                                                    .start();
                                        } else {
                                            view.animate()
                                                    .x(startX)
                                                    .setDuration(250)
                                                    .start();
                                        }
                                        break;

                                    case MotionEvent.ACTION_DOWN:
                                        dX = view.getX() - event.getRawX();
                                        break;

                                    case MotionEvent.ACTION_MOVE:
                                        view.animate()
                                                .x(event.getRawX() + dX)
                                                .setDuration(0)
                                                .start();
                                        break;
                                }
                                return true;
                            }
                        });

                    }
                    this.cancel();
                    animationFinished = true;

                }
            };
            animateAdContainer(adViewContainer);
            animationStarted = true;
            stickyTimer.start();

        }
    }

    private void animateAdContainer(final ViewGroup adViewContainer) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            adViewContainer.setTranslationZ(10);
            adViewContainer.setElevation(2);
        }
        //animate background change
        TransitionDrawable transition = (TransitionDrawable) adViewContainer.getBackground();
        transition.startTransition(ANIMATION_DURATION);

        //animate changing height of root view
        ValueAnimator rootAnimator = ValueAnimator.ofInt(adViewContainer.getHeight(), collapsedHeight);
        rootAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                adViewContainer.getLayoutParams().height = (Integer) valueAnimator.getAnimatedValue();
                adViewContainer.requestLayout();
            }
        });
        rootAnimator.setDuration(ANIMATION_DURATION);
        rootAnimator.start();

        scaleView(adViewContainer.getChildAt(0), 0.65f, ANIMATION_DURATION);
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

    private void scrollToPositionWithOffset(RecyclerView recyclerView) {

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(adPosition, -1);
        } else if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            ((GridLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(adPosition, -1);
        }
    }

    public abstract void removeAdPositionFromList(int adPosition);


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
                .setDuration(ANIMATION_DURATION / 4)
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
                .setDuration(ANIMATION_DURATION / 2)
                .setStartDelay(ANIMATION_DURATION / 2)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                    }
                });
    }
}
