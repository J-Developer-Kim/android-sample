package com.example.androiddev.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.example.androiddev.R;
import com.example.androiddev.views.DateMonthView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DateSelectViewPagerAdapter extends PagerAdapter {
    //==============================================================================================
    // Constant Define
    //==============================================================================================

    //==============================================================================================
    // Instance Variable Define && getter
    //==============================================================================================
    private Context o_context;

    //==============================================================================================
    // Adapter Constructor
    //==============================================================================================
    public DateSelectViewPagerAdapter(Context o_context) {
        this.o_context = o_context;
    }

    //==============================================================================================
    // Adapter LifeCycle
    //==============================================================================================
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(o_context).inflate(R.layout.date_select_view_pager_item, container, false);
        final DateSelectViewPagerAdapterViewHolder viewHolder = new DateSelectViewPagerAdapterViewHolder(view, o_context);

        viewHolder.dateMonthView.changeData(position);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return (view == (View) o);
    }

    //==============================================================================================
    // ViewHolder - MonthlyCalendarViewHolder
    //==============================================================================================
    public class DateSelectViewPagerAdapterViewHolder {
        private Context context;
        @BindView(R.id.dateMonthView) DateMonthView dateMonthView;

        public DateSelectViewPagerAdapterViewHolder(View itemView, Context po_context) {
            ButterKnife.bind(this, itemView);
            this.context = po_context;
        }
    }
}
