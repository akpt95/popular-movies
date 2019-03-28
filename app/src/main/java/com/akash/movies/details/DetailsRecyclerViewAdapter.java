package com.akash.movies.details;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.akash.movies.R;
import com.akash.movies.network.model.reviews.ReviewResponse;
import com.akash.movies.network.model.videos.VideoResponse;

public class DetailsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String viewType="";
    private VideoResponse videoResponse;
    private ReviewResponse reviewResponse;
    private OnItemClickListner itemClickListner;

    DetailsRecyclerViewAdapter(String viewType, VideoResponse videoResponse, ReviewResponse reviewResponse){

        this.viewType = viewType;
        this.videoResponse = videoResponse;
        this.reviewResponse = reviewResponse;
    }

    @Override
    public int getItemViewType(int position) {

        if(viewType.equalsIgnoreCase("video"))
            return 0;
        else
            return 1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if(viewType == 0){
            View view = inflater.inflate(R.layout.video_recycler_view_item, parent, false);
            return new ViewHolderVideos(view);
        } else {
            View view = inflater.inflate(R.layout.review_recycler_view_item, parent, false);
            return new ViewHolderReviews(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if(holder.getItemViewType() == 0){

            ViewHolderVideos viewHolderVideos = (ViewHolderVideos) holder;
            if (videoResponse!=null) {
                viewHolderVideos.textVideoTitle.setText(videoResponse.getResults().get(position).getName());
            }

        } else {

            ViewHolderReviews viewHolderReviews = (ViewHolderReviews) holder;
            if (reviewResponse!=null) {
                viewHolderReviews.textReviewContent.setText(reviewResponse.getResults().get(position).getContent());
                viewHolderReviews.textReviewAuthor.setText(String.format("~by %s", reviewResponse.getResults().get(position).getAuthor()));
            }
        }

    }

    @Override
    public int getItemCount() {

        int size =0;

        if(viewType.equalsIgnoreCase("video")){
            if (videoResponse!=null)
                size = videoResponse.getResults().size();

        } else{
            if (reviewResponse!=null)
                size = reviewResponse.getResults().size();
        }

        return size;
    }

    private class ViewHolderVideos extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView textVideoTitle;

        ViewHolderVideos(View view) {
            super(view);

            textVideoTitle = view.findViewById(R.id.text_video_title);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListner.onItemCLick(v, getAdapterPosition());
        }
    }

    private class ViewHolderReviews extends RecyclerView.ViewHolder {

        private TextView textReviewContent;
        private TextView textReviewAuthor;

        ViewHolderReviews(View view) {
            super(view);

            textReviewContent = view.findViewById(R.id.text_review_content);
            textReviewAuthor = view.findViewById(R.id.text_review_author);

        }


    }

    public interface OnItemClickListner {

        void onItemCLick(View view, int position);
    }

    void setItemClickListner(OnItemClickListner itemClickListner) {
        this.itemClickListner = itemClickListner;
    }
}
