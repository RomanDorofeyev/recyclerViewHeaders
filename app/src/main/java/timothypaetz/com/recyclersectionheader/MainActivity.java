package timothypaetz.com.recyclersectionheader;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brandio.ads.AdProvider;
import com.brandio.ads.AdRequest;
import com.brandio.ads.Controller;
import com.brandio.ads.Placement;
import com.brandio.ads.ads.Ad;
import com.brandio.ads.exceptions.DIOError;
import com.brandio.ads.exceptions.DioSdkException;
import com.brandio.ads.listeners.AdLoadListener;
import com.brandio.ads.listeners.AdRequestListener;
import com.brandio.ads.listeners.SdkInitListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int AD_POSITION = 14;
    private static final int RECYCLE_VIEW_SIZE = 40;
    private static final int MOTION_LAYOUT_STATE_EXPANDED = 2131165341;
    private static final int MOTION_LAYOUT_STATE_COLLAPSED = 2131165340;

    public static final String APP_ID = "7729";
    public static final String PLACEMENT_ID = "5134";
    public static String requestId;
    private List adList;

    RecyclerViewAdapter recyclerViewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadDioAd();

        setTitle("Snap Video Test");
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL,
                false));

//        RecyclerAdItemDecoration sectionItemDecoration =
//            new RecyclerAdItemDecoration(getResources().getDimensionPixelSize(R.dimen.recycler_section_header_height),
//                                              true, getAdPositionsList());
//        recyclerView.addItemDecoration(sectionItemDecoration);
//        recyclerView.setHasFixedSize(true);

        adList = new ArrayList(RECYCLE_VIEW_SIZE);
        for (int i = 0; i < RECYCLE_VIEW_SIZE; i++)
        {
            adList.add(i);
        }
            recyclerViewAdapter = new RecyclerViewAdapter(adList, AD_POSITION);

        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.addOnScrollListener(new InfeedMotionScrollListener(this, AD_POSITION){
            @Override
            public void removeAdPositionFromList(int adPosition) {
                adList.remove(AD_POSITION);
                recyclerViewAdapter.notifyDataSetChanged();
            }
        });

//        setFrame();
    }

    public static List<Integer> getAdPositionsList() {
        List<Integer> adPositions = new ArrayList<>();
        adPositions.add(8);
//        adPositions.add(20);
//        adPositions.add(34);
        return adPositions;
    }

    private void setFrame() {
        FrameLayout frameLayout = findViewById(R.id.container_for_motion);
        ViewGroup adViewGroup = (ViewGroup) View.inflate(this, R.layout.ad_item_layout, null);
        frameLayout.addView(adViewGroup);

        final float[] dX = {0};
        final float[] dY = {0};
        adViewGroup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {


                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:

                        dX[0] = view.getX() - event.getRawX();
//                        dY[0] = view.getY() - event.getRawY();
                        break;

                    case MotionEvent.ACTION_MOVE:

                        view.animate()
                                .x(event.getRawX() + dX[0])
//                                .y(event.getRawY() + dY[0])
                                .setDuration(0)
                                .start();
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });


//        MotionLayout motionLayout = findViewById(R.id.mainMotionLayout);
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

    private void loadDioAd() {
         Controller controller = Controller.getInstance();

         if (controller.isInitialized()){
             requestAndLoadAd();
         }else {
             controller.init(this, null, APP_ID, new SdkInitListener() {
                 @Override
                 public void onInit() {
                     requestAndLoadAd();
                 }

                 @Override
                 public void onInitError(DIOError dioError) {
                     Toast.makeText(MainActivity.this, "SDK init error", Toast.LENGTH_SHORT).show();
                 }
             });
         }
    }

    private void requestAndLoadAd() {
        Placement placement = null;
        try {
            placement = Controller.getInstance().getPlacement(PLACEMENT_ID);
        } catch (DioSdkException e) {
            e.printStackTrace();
        }
        if (placement == null) {
            Toast.makeText(MainActivity.this, "No such placement", Toast.LENGTH_SHORT).show();
            return;
        }

        final AdRequest adRequest = placement.newAdRequest();

        adRequest.setAdRequestListener(new AdRequestListener() {
            @Override
            public void onAdReceived(AdProvider adProvider) {
                adProvider.setAdLoadListener(new AdLoadListener() {
                    @Override
                    public void onLoaded(Ad ad) {
                        Toast.makeText(MainActivity.this, "Ad loaded", Toast.LENGTH_SHORT).show();
                        requestId = adRequest.getId();
                        recyclerViewAdapter.setRequestId(requestId);
                    }

                    @Override
                    public void onFailedToLoad(DIOError dioError) {
                        Toast.makeText(MainActivity.this, "Failed to load ad", Toast.LENGTH_SHORT).show();
                    }
                });
                try {
                    adProvider.loadAd();
                } catch (DioSdkException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Failed to load ad", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNoAds(DIOError dioError) {
                Toast.makeText(MainActivity.this, "No ads", Toast.LENGTH_SHORT).show();

            }
        });
        adRequest.requestAd();
    }
}
