package james.blackboard.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import james.blackboard.R;
import james.blackboard.data.BreadcrumbData;

public class BreadcrumbsAdapter extends RecyclerView.Adapter<BreadcrumbsAdapter.ViewHolder> {

    private List<BreadcrumbData> breadcrumbs;
    private BreadcrumbCallback callback;

    public BreadcrumbsAdapter(List<BreadcrumbData> breadcrumbs, BreadcrumbCallback callback) {
        this.breadcrumbs = breadcrumbs;
        this.callback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_breadcrumb, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.title.setText(breadcrumbs.get(position).title);
        holder.image.setVisibility(position == breadcrumbs.size() - 1 ? View.GONE : View.VISIBLE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.moveTo(breadcrumbs.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return breadcrumbs.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            image = itemView.findViewById(R.id.image);
        }
    }

    public interface BreadcrumbCallback {
        void moveTo(BreadcrumbData breadcrumb);
    }

}
