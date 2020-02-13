package timothypaetz.com.recyclersectionheader;

import androidx.annotation.NonNull;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int SIMPLE_VIEW = 0;
    private static final int AD_VIEW = 1;

    private final int itemCount;
    private final int adPosition;

    public RecyclerViewAdapter(int itemCount, int adPosition) {
        this.itemCount = itemCount;
        this.adPosition = adPosition;
    }

    @Override
    public int getItemViewType(int position) {
        return position == adPosition ? AD_VIEW : SIMPLE_VIEW;
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
        if (!(holder instanceof AdViewHolder)) return;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemCount;
    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {

        public SimpleViewHolder(View view) {
            super(view);
        }
    }

    public static class AdViewHolder extends RecyclerView.ViewHolder {
        ViewGroup motionLayout;
        public AdViewHolder(View view) {
            super(view);
            motionLayout = (ViewGroup) view;
        }
    }
}
