package timothypaetz.com.recyclersectionheader;

import androidx.annotation.NonNull;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brandio.ads.Controller;
import com.brandio.ads.InfeedPlacement;
import com.brandio.ads.containers.InfeedAdContainer;
import com.brandio.ads.exceptions.DioSdkException;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int SIMPLE_VIEW = 0;
    private static final int AD_VIEW = 1;

    private final List itemCount;
    private String requestId;

    public RecyclerViewAdapter(List itemCount, int adPosition) {
        this.itemCount = itemCount;
        itemCount.add(adPosition, null);
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    @Override
    public int getItemViewType(int position) {
        return itemCount.get(position) == null ? AD_VIEW : SIMPLE_VIEW;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == SIMPLE_VIEW) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.infeed_list_item, parent, false);
            return new SimpleViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_item_layout, parent, false);
            return new AdViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if ((holder instanceof AdViewHolder)) {

            if (requestId == null) return;
            try {
                InfeedPlacement infeedPlacement = (InfeedPlacement) Controller.getInstance().getPlacement(MainActivity.PLACEMENT_ID);
                infeedPlacement.setFrameless(true);
                infeedPlacement.setFullWidth(true);
                InfeedAdContainer infeedContainer = infeedPlacement.getInfeedContainer(Controller.getInstance().getContext(), requestId);
                infeedContainer.bindTo(((AdViewHolder) holder).adContent);

//                RelativeLayout adContent = ((AdViewHolder) holder).adContent;
//                RelativeLayout.LayoutParams adContentlayoutParams = (RelativeLayout.LayoutParams)adContent.getLayoutParams();
//                adContentlayoutParams.removeRule(RelativeLayout.CENTER_IN_PARENT);
//
//                RelativeLayout videoWrappingLayout = (RelativeLayout) adContent.getChildAt(0);
//                View videoView = videoWrappingLayout.getChildAt(0);
//                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)videoView.getLayoutParams();
//
//                layoutParams.removeRule(RelativeLayout.CENTER_IN_PARENT);
//                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);
//                layoutParams.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
//                adContent.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            } catch (DioSdkException e) {
                e.printStackTrace();
            }

            return;
        }

        ((SimpleViewHolder) holder).itemNumber.setText(String.valueOf(position));

    }

    @Override
    public int getItemCount() {
        return itemCount.size();
    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        TextView itemNumber;

        public SimpleViewHolder(View view) {
            super(view);
            itemNumber = view.findViewById(R.id.item_number);
        }
    }

    public static class AdViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout adContent;
        public AdViewHolder(View view) {
            super(view);
            adContent = view.findViewById(R.id.ad_content);
        }
    }
}
