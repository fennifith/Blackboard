package james.blackboard.adapters;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import james.blackboard.R;
import james.blackboard.data.BreadcrumbData;
import james.blackboard.data.content.AnnouncementContentData;
import james.blackboard.data.content.ContentData;
import james.blackboard.data.content.FileContentData;
import james.blackboard.data.content.FolderContentData;
import james.blackboard.data.content.WebLinkContentData;

public class ContentsAdapter extends RecyclerView.Adapter<ContentsAdapter.ViewHolder> {

    private List<ContentData> contents;
    private BreadcrumbsAdapter.BreadcrumbCallback callback;

    public ContentsAdapter(List<ContentData> contents) {
        this.contents = contents;
    }

    public void setBreadcrumbCallback(BreadcrumbsAdapter.BreadcrumbCallback callback) {
        this.callback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 4)
            return new AnnouncementViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_announcement, parent, false));
        else
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_content, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case 4:
                AnnouncementViewHolder announcementHolder = (AnnouncementViewHolder) holder;
                AnnouncementContentData announcement = (AnnouncementContentData) contents.get(position);

                holder.title.setText(announcement.title);
                holder.description.setText(Html.fromHtml(announcement.description));
                holder.description.setMovementMethod(LinkMovementMethod.getInstance());
                announcementHolder.date.setText(announcement.date);
                break;
            default:
                ContentData content = contents.get(position);
                holder.title.setText(content.title);

                if (content.description.length() > 0) {
                    holder.description.setVisibility(View.VISIBLE);
                    holder.description.setText(Html.fromHtml(content.description));
                    holder.description.setMovementMethod(getItemViewType(position) == 0 ? LinkMovementMethod.getInstance() : null);
                } else holder.description.setVisibility(View.GONE);

                switch (getItemViewType(position)) {
                    case 1:
                        holder.image.setImageResource(R.drawable.ic_link);
                        holder.itemView.setTag(((WebLinkContentData) content).url);
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (view.getTag() != null && view.getTag() instanceof String)
                                    view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse((String) view.getTag())));
                            }
                        });
                        break;
                    case 2:
                        holder.image.setImageResource(R.drawable.ic_folder);
                        holder.itemView.setTag(position);
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (callback != null && view.getTag() != null && view.getTag() instanceof Integer) {
                                    FolderContentData folder = (FolderContentData) contents.get((Integer) view.getTag());
                                    callback.moveTo(new BreadcrumbData(folder.title, folder.action));
                                }
                            }
                        });
                        break;
                    case 3:
                        holder.image.setImageResource(R.drawable.ic_file);
                        holder.itemView.setTag(((FileContentData) content).url);
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (view.getTag() != null && view.getTag() instanceof String)
                                    view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse((String) view.getTag())));
                            }
                        });
                        break;
                    default:
                        holder.image.setImageResource(R.drawable.ic_message);
                        break;
                }
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (contents.get(position) instanceof WebLinkContentData)
            return 1;
        else if (contents.get(position) instanceof FolderContentData)
            return 2;
        else if (contents.get(position) instanceof FileContentData)
            return 3;
        else if (contents.get(position) instanceof AnnouncementContentData)
            return 4;

        return 0;
    }

    @Override
    public int getItemCount() {
        return contents.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView description;
        private ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            image = itemView.findViewById(R.id.image);
        }
    }

    private static class AnnouncementViewHolder extends ViewHolder {

        private TextView date;

        public AnnouncementViewHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
        }
    }

}
