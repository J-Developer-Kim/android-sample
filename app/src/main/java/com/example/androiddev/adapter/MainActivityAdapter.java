package com.example.androiddev.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androiddev.R;
import com.example.androiddev.common.CommonInterface;
import com.example.androiddev.dto.MenuVO;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivityAdapter extends RecyclerView.Adapter {
    private Context context;
    private ArrayList<MenuVO> arrO_menu = new ArrayList<>();
    private CommonInterface.OnItemClickEvent callback;

    public MainActivityAdapter(Context context, ArrayList<MenuVO> arrO_menu, CommonInterface.OnItemClickEvent callback) {
        this.context = context;
        this.arrO_menu = arrO_menu;
        this.callback = callback;
    }

    @Override
    public int getItemCount() {
        return arrO_menu.size();
    }

    @Override
    public int getItemViewType(int position) { return 0; }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder po_viewHolder, int pi_position) {
        ((MainActivityAdapterItem) po_viewHolder).makeView(arrO_menu.get(pi_position));
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup po_viewGroup, int pi_viewType) {
        View lo_view = LayoutInflater.from(po_viewGroup.getContext()).inflate(R.layout.activity_main_item, po_viewGroup, false);
        return new MainActivityAdapterItem(lo_view);
    }

    public class MainActivityAdapterItem extends RecyclerView.ViewHolder {
        @BindView(R.id.cl_content) ConstraintLayout cl_content;
        @BindView(R.id.tv_title)   TextView tv_title;

        public MainActivityAdapterItem(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void makeView(MenuVO lo_data) {
            tv_title.setText(lo_data.getTitle());
        }

        @OnClick({
                R.id.cl_content
        })
        public void onClick(View po_view) {
            switch ( po_view.getId() ) {
                case R.id.cl_content :
                    callback.itemClickEvent(getAdapterPosition(), arrO_menu.get(getAdapterPosition()));
                    break;

                default:break;
            }
        }
    }
}
