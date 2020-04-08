package com.example.androiddev.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androiddev.R;
import com.example.androiddev.dto.ChatVO;

public class ChatActivityAdapter extends RecyclerView.Adapter {
    private Context context;
    private ArrayList<ChatVO> arrO_list = new ArrayList<>();

    public ChatActivityAdapter(Context context, ArrayList<ChatVO> arrO_list) {
        this.context = context;
        this.arrO_list = arrO_list;
    }

    @Override
    public int getItemCount() {
        return arrO_list.size();
    }

    @Override
    public int getItemViewType(int position) { return 0; }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder po_viewHolder, int pi_position) {
        ((ChatActivityAdapter.ChatActivityAdapterItem) po_viewHolder).makeView(arrO_list.get(pi_position));
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup po_viewGroup, int pi_viewType) {
        View lo_view = LayoutInflater.from(po_viewGroup.getContext()).inflate(R.layout.activity_chat_item, po_viewGroup, false);
        return new ChatActivityAdapter.ChatActivityAdapterItem(lo_view);
    }

    public class ChatActivityAdapterItem extends RecyclerView.ViewHolder {
        @BindView(R.id.cl_content)    ConstraintLayout cl_content;
        @BindView(R.id.tv_otherTitle) TextView         tv_otherTitle;
        @BindView(R.id.iv_otherImage) ImageView        iv_otherImage;
        @BindView(R.id.tv_otherName)  TextView         tv_otherName;
        @BindView(R.id.tv_myTitle)    TextView         tv_myTitle;
        @BindView(R.id.iv_myImage)    ImageView        iv_myImage;
        @BindView(R.id.tv_myName)     TextView         tv_myName;

        public ChatActivityAdapterItem(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void makeView(ChatVO lo_data) {
            tv_otherTitle.setVisibility(View.GONE);
            iv_otherImage.setVisibility(View.GONE);
            tv_otherName.setVisibility(View.GONE);
            tv_myTitle.setVisibility(View.GONE);
            iv_myImage.setVisibility(View.GONE);
            tv_myName.setVisibility(View.GONE);

            if ( lo_data.is_my() ) {
                tv_myTitle.setVisibility(View.VISIBLE);
                iv_myImage.setVisibility(View.VISIBLE);
                tv_myName.setVisibility(View.VISIBLE);
                tv_myTitle.setText(lo_data.getContent());
            }
            else {
                tv_otherTitle.setVisibility(View.VISIBLE);
                iv_otherImage.setVisibility(View.VISIBLE);
                tv_otherName.setVisibility(View.VISIBLE);
                tv_otherTitle.setText(lo_data.getContent());
            }
        }
    }

    public void setArrO_list(ArrayList<ChatVO> arrO_list) {
        this.arrO_list = arrO_list;
    }
}
