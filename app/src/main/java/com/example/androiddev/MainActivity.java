package com.example.androiddev;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ImagePickerConfig;
import com.esafirm.imagepicker.features.IpCons;
import com.esafirm.imagepicker.helper.ConfigUtils;
import com.example.androiddev.adapter.MainActivityAdapter;
import com.example.androiddev.common.CommonEnum;
import com.example.androiddev.common.CommonInterface;
import com.example.androiddev.common.DividerItemDecoration;
import com.example.androiddev.common.ImagePickerUI;
import com.example.androiddev.dto.MenuVO;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    // ====================================================================================
    // Constant
    // ====================================================================================

    // ====================================================================================
    // Object
    // ====================================================================================
    private ArrayList<MenuVO>    arrO_menu           = new ArrayList<>();
    private MainActivityAdapter  adapter;
    private ImagePickerConfig    o_imagePickerConfig = null; // 커스텀UI 이미지피커로 이동하기 위한 변수
    private Intent               o_imagePickerIntent = null; // 커스텀UI 이미지피커로 이동하기 위한 변수
    private boolean              b_isPermmision      = false;

    // ====================================================================================
    // BindView
    // ====================================================================================
    @BindView(R.id.rcv_list) RecyclerView rcv_list;

    // ====================================================================================
    // Activity LifeCycle
    // ====================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        arrO_menu.add(new MenuVO(CommonEnum.MenuCode.CHAT, "채팅"));
        arrO_menu.add(new MenuVO(CommonEnum.MenuCode.IMAGE_PICKER, "Image-Picker"));
        arrO_menu.add(new MenuVO(CommonEnum.MenuCode.GOOGLE_MAP_SAMPLE, "구글지도-내위치(샘플소스)"));
        arrO_menu.add(new MenuVO(CommonEnum.MenuCode.CALENDAR_SELECTED, "날짜 선택"));
        arrO_menu.add(new MenuVO(CommonEnum.MenuCode.DATE_PICKER, "DATE_PICKER"));

        LinearLayoutManager lo_layoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, true);
        lo_layoutManager.setStackFromEnd(false);
        lo_layoutManager.setReverseLayout(false);
        rcv_list.setLayoutManager(lo_layoutManager);

        rcv_list.addItemDecoration(new DividerItemDecoration(this, R.drawable.recycler_view_divider_line));

        adapter = new MainActivityAdapter(this, arrO_menu, callback);
        rcv_list.setAdapter(adapter);

        //permmisionCheck();
    }

    // ====================================================================================
    // User Function
    // ====================================================================================
    /**
     * Manifest 부여된 권한 가져오기(granted 가 안된거에 한해서만)
     * @return
     */
    private String[] getDeniedPermissionList() {
        PackageInfo info;

        try {
            info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_PERMISSIONS);
        }
        catch ( PackageManager.NameNotFoundException e ) {
            return new String[0];
        }

        if ( info.requestedPermissions == null ) {
            return new String[0];
        }

        ArrayList<String> missingPermissions = new ArrayList<>();

        for ( int i = 0; i < info.requestedPermissions.length; i++ ) {
            if ( (info.requestedPermissionsFlags[i] & PackageInfo.REQUESTED_PERMISSION_GRANTED) == 0 ) {
                missingPermissions.add(info.requestedPermissions[i]);
            }
        }

        return missingPermissions.toArray(new String[missingPermissions.size()]);
    }

    /**
     * 권한체크
     */
    private void permmisionCheck() {
        String[] missingPermissions = getDeniedPermissionList();

        if ( missingPermissions.length < 1 ) {
            b_isPermmision = true;
        }
        else {
            TedPermission.with(this)
                    .setPermissionListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted() {
                            b_isPermmision = true;
                        }

                        @Override
                        public void onPermissionDenied(List<String> deniedPermissions) {
                            b_isPermmision = false;
                            Toast.makeText(MainActivity.this, "권한을 다 허용하길바람...", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setPermissions(missingPermissions)
                    .check();
        }
    }

    /**
     * 리스트뷰 Row 클릭 이벤트
     */
    private CommonInterface.OnItemClickEvent callback = (index, object) -> {
        if ( object instanceof MenuVO ) {
            MenuVO data = (MenuVO) object;

            if ( data.getCode().equals(CommonEnum.MenuCode.CHAT) ) {
                startActivity(new Intent(this, ChatActivity.class));
            }
            else if ( data.getCode().equals(CommonEnum.MenuCode.IMAGE_PICKER) ) {
                ImagePicker imagePicker = ImagePicker.create(MainActivity.this)
                        //.single()
                        .multi()
                        .showCamera(false)
                        .limit(10)
                        .theme(R.style.AppTheme_NoActionBar);

                o_imagePickerConfig = ConfigUtils.checkConfig(imagePicker.getConfig());
                o_imagePickerIntent = new Intent(MainActivity.this, ImagePickerUI.class);
                o_imagePickerIntent.putExtra(ImagePickerConfig.class.getSimpleName(), o_imagePickerConfig);
                startActivityForResult(o_imagePickerIntent, IpCons.RC_IMAGE_PICKER);
            }
            else if ( data.getCode().equals(CommonEnum.MenuCode.GOOGLE_MAP_SAMPLE) ) {
                startActivity(new Intent(this, GoogleMapActivity.class));
            }
            else if ( data.getCode().equals(CommonEnum.MenuCode.CALENDAR_SELECTED) ) {
                startActivity(new Intent(this, CalendarSelectedActivity.class));
            }
            else if ( data.getCode().equals(CommonEnum.MenuCode.DATE_PICKER) ) {
                startActivity(new Intent(this, CalendarSelectedActivity.class));
            }
        }
    };

    /**
     * ForActivityResult Callback
     * @param pi_requestCode
     * @param pi_resultCode
     * @param po_data
     */
    @Override
    protected void onActivityResult(int pi_requestCode, int pi_resultCode, Intent po_data) {
        super.onActivityResult(pi_requestCode, pi_resultCode, po_data);

        if ( pi_requestCode == IpCons.RC_IMAGE_PICKER ) {
            Toast.makeText(MainActivity.this, "이미지 콜백", Toast.LENGTH_SHORT).show();
        }
    }
}
