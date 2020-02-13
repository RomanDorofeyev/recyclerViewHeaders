package timothypaetz.com.recyclersectionheader;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int AD_POSITION = 8;
    private static final int RECYCLE_VIEW_SIZE = 40;
    private static final int ANIMATION_DURATION = 500;
    private static final int MOTION_LAYOUT_STATE_EXPANDED = 2131165341;
    private static final int MOTION_LAYOUT_STATE_COLLAPSED = 2131165340;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Snap Video Test");
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                                                              LinearLayoutManager.VERTICAL,
                                                              false));


//        RecyclerAdItemDecoration sectionItemDecoration =
//            new RecyclerAdItemDecoration(getResources().getDimensionPixelSize(R.dimen.recycler_section_header_height),
//                                              true, getAdPositionsList());
//        recyclerView.addItemDecoration(sectionItemDecoration);

        recyclerView.setAdapter(new RecyclerViewAdapter(RECYCLE_VIEW_SIZE, AD_POSITION));
        recyclerView.addOnScrollListener(new InfeedMotionScrollListener(AD_POSITION, getResources().getInteger(R.integer.transition_animation_duration)));

//        setFrame();
    }

    public static List<Integer> getAdPositionsList(){
        List<Integer> adPositions = new ArrayList<>();
        adPositions.add(8);
//        adPositions.add(20);
//        adPositions.add(34);
        return adPositions;
    }

    private void setFrame(){
        FrameLayout frameLayout = findViewById(R.id.container_for_motion);
        frameLayout.addView(View.inflate(this, R.layout.motion_ad_layout, null));

        MotionLayout motionLayout = findViewById(R.id.mainMotionLayout);
//        motionLayout.setTransitionListener(new MotionLayout.TransitionListener() {
//            @Override
//            public void onTransitionStarted(MotionLayout motionLayout, int i, int i1) {
//                motionLayout.findViewById(R.id.advertisement_text).setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onTransitionChange(MotionLayout motionLayout, int i, int i1, float v) {
//
//            }
//
//            @Override
//            public void onTransitionCompleted(MotionLayout motionLayout, int i) {
//                Log.e(TAG, "Motion state  = " + motionLayout.getCurrentState() + " i =  " + i);
//                if (i == MOTION_LAYOUT_STATE_EXPANDED) {
//                    motionLayout.findViewById(R.id.advertisement_text).setVisibility(View.VISIBLE);
//                    ((TextView)(motionLayout.findViewById(R.id.advertisement_text))).setText("Advertisement");
//                    motionLayout.setBackgroundColor(getResources().getColor(R.color.recyclerAdBackground));
//                }
//                else {
//                    motionLayout.findViewById(R.id.advertisement_text).setVisibility(View.VISIBLE);
//                    ((TextView)(motionLayout.findViewById(R.id.advertisement_text))).setText("Sponsored");
//                    motionLayout.setBackgroundColor(getResources().getColor(R.color.recyclerCollapsedAdBackground));
//                }
//            }
//
//            @Override
//            public void onTransitionTrigger(MotionLayout motionLayout, int i, boolean b, float v) {
//
//            }
//        });
    }
}
