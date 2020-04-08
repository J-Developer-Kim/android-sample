package com.example.androiddev;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CalendarSelectedActivity extends AppCompatActivity {
    // ====================================================================================
    // Constant
    // ====================================================================================

    // ====================================================================================
    // Object
    // ====================================================================================

    // ====================================================================================
    // BindView
    // ====================================================================================
    @BindView(R.id.btn_call) Button btn_call;

    // ====================================================================================
    // Activity LifeCycle
    // ====================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_selected);

        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // ====================================================================================
    // Override
    // ====================================================================================
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home :
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 클릭 이벤트 선언
     * @param po_view
     */
    @OnClick({
            R.id.btn_call
    })
    public void onClick(View po_view) {
        switch ( po_view.getId() ) {
            case R.id.btn_call :
                //startActivity(new Intent(this, DateSelectActivity.class));
                startActivity(new Intent(this, DatePickerActivity.class));
                this.overridePendingTransition(R.anim.sliding_up, R.anim.stay);
                break;

            default:break;
        }
    }
}
