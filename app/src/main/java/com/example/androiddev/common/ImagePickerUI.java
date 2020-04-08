package com.example.androiddev.common;

import android.Manifest;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.esafirm.imagepicker.features.ImagePickerConfig;
import com.esafirm.imagepicker.features.ImagePickerFragment;
import com.esafirm.imagepicker.features.ImagePickerInteractionListener;
import com.esafirm.imagepicker.features.cameraonly.CameraOnlyConfig;
import com.esafirm.imagepicker.helper.IpLogger;
import com.esafirm.imagepicker.helper.LocaleManager;
import com.esafirm.imagepicker.model.Folder;
import com.esafirm.imagepicker.model.Image;
import com.example.androiddev.R;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ImagePickerUI extends AppCompatActivity {
    //==============================================================================================
    // NESTED CLASS Define
    //==============================================================================================
    public class ImagePickerAlbumListAdpater extends RecyclerView.Adapter<ImagePickerAlbumListAdpater.ImagePickerAlbumListViewHolder> {
        //==============================================================================================
        // NESTED CLASS Define
        //==============================================================================================

        //==============================================================================================
        // View Holder - ImagePickerAlbumListViewHolder
        //==============================================================================================
        public class ImagePickerAlbumListViewHolder extends RecyclerView.ViewHolder{

            //==============================================================================================
            // Instance Layout xml Variable Define With ButterKnife
            //==============================================================================================
            @BindView(R.id.cl_container)           ConstraintLayout cl_container;
            @BindView(R.id.iv_representationImage) ImageView iv_representationImage;
            @BindView(R.id.tv_albumSubject)        TextView tv_albumSubject;
            @BindView(R.id.tv_albumImageCount)     TextView         tv_albumImageCount;

            //==============================================================================================
            // Instance Variable Define
            //==============================================================================================

            //==============================================================================================
            // Construct View Holder
            //==============================================================================================
            public ImagePickerAlbumListViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            //==============================================================================================
            // Event xml Variable With ButterKnife
            //==============================================================================================
            @OnClick({
                    R.id.cl_container
            })
            public void onClick(View po_view){
                switch ( po_view.getId() ){

                    case R.id.cl_container:
                        // 앨범 변경
                        o_imagePickerFragment.changeAlbum(arrO_folderList.get(getAdapterPosition()));

                        // 레이아웃 적용
                        fl_imageFragmentHolder.bringToFront();

                        tv_selectCount.setVisibility(View.VISIBLE);
                        iv_camera.setVisibility(View.VISIBLE);
                        iv_textDone.setVisibility(View.VISIBLE);

                        // 가운데 텍스트 앨범명으로 변경
                        tv_textAlbumSubject.setText(arrO_folderList.get(getAdapterPosition()).getFolderName());

                        // 애니메이션 적용
                        viewAnimationToggle(rv_albumList);
                        b_viewAlbum = false;

                        break;
                }
            }
        }

        //==============================================================================================
        // Constant Define
        //==============================================================================================

        //==============================================================================================
        // Instance Variable Define
        //==============================================================================================
        private Context        o_context      = null;
        private List<Folder>   arrO_folders   = new ArrayList<>();
        private RequestManager o_glideManager = null;

        //==============================================================================================
        // Constructor
        //==============================================================================================
        public ImagePickerAlbumListAdpater(Context po_context, List<Folder> parrO_folderList, RequestManager po_glideManager) {
            this.o_context      = po_context;
            this.arrO_folders   = parrO_folderList;
            this.o_glideManager = po_glideManager;
        }

        //==============================================================================================
        // Adpater LifeCycle
        //==============================================================================================
        @NonNull
        @Override
        public ImagePickerAlbumListViewHolder onCreateViewHolder(@NonNull ViewGroup po_viewGroup, int pi_viewType) {
            View lo_view = LayoutInflater.from(po_viewGroup.getContext()).inflate(R.layout.activity_image_picker_item, po_viewGroup, false);
            return new ImagePickerAlbumListViewHolder(lo_view);
        }

        @Override
        public void onBindViewHolder(@NonNull ImagePickerAlbumListViewHolder lo_viewHolder, int pi_position) {
            // 앨범에 사진이 있을때 제일 첫번째 사진을 대표이미지로 사용
            if ( arrO_folderList.get(pi_position).getImages() != null && arrO_folderList.get(pi_position).getImages().size() > 0 ) {
                o_glideManager.asBitmap()
                        .load(arrO_folderList.get(pi_position).getImages().get(0).getPath())
                        .apply(new RequestOptions()
                                .transforms(new CenterCrop(), new GlideBorderRoundedCornersTransformation(o_context, 0, 2, "#F2F2F2", 2))
                                .placeholder(R.drawable.img_preloader_mini)
                                .signature(new ObjectKey(String.valueOf(System.currentTimeMillis()/(Constant.GLIDE_CACHE_REMAIN_TIME))))
                                .error(R.drawable.img_preloader_mini)
                        )
                        .into(lo_viewHolder.iv_representationImage);
            }
            // 사진이 없을때
            else {
                o_glideManager.asBitmap()
                        .load(R.color.white)
                        .apply(new RequestOptions()
                                .transforms(new CenterCrop(), new GlideBorderRoundedCornersTransformation(o_context, 0, 2, "#F2F2F2", 2))
                                .signature(new ObjectKey(String.valueOf(System.currentTimeMillis()/(Constant.GLIDE_CACHE_REMAIN_TIME))))
                        )
                        .into(lo_viewHolder.iv_representationImage);
            }

            // 앨범명 적용
            lo_viewHolder.tv_albumSubject.setText(arrO_folders.get(pi_position).getFolderName());

            // 앨범 이미지 갯수 적용
            lo_viewHolder.tv_albumImageCount.setText(String.valueOf(arrO_folderList.get(pi_position).getImages().size()));

            ConstraintSet lo_set = new ConstraintSet();
            lo_set.clone(lo_viewHolder.cl_container);

            // 마지막 아이템
            if ( getItemCount() - 1 == pi_position ) {
                lo_set.setMargin(lo_viewHolder.iv_representationImage.getId(), ConstraintSet.BOTTOM, Math.round(GF_DP_TO_PIXEL(o_context, 48)));
            }
            else {
                lo_set.setMargin(lo_viewHolder.iv_representationImage.getId(), ConstraintSet.BOTTOM, 0);
            }

            lo_set.applyTo(lo_viewHolder.cl_container);
        }

        @Override
        public int getItemCount() {
            return arrO_folders.size();
        }

        //==============================================================================================
        // User Functions
        //==============================================================================================
        public float GF_DP_TO_PIXEL(Context po_context, int pi_dp) {
            return (pi_dp * po_context.getResources().getDisplayMetrics().density);
        }
    }

    //==============================================================================================
    // Constant Define
    //==============================================================================================
    private static final int LCI_INTENT_TAKE_PICTURE = 5000;

    //==============================================================================================
    // Instance Layout xml Variable Define With ButterKnife
    //==============================================================================================
    @BindView(R.id.cl_topMenuContainer)    ConstraintLayout cl_topMenuContainer; // 상단 메뉴 영역
    @BindView(R.id.cl_albumSubjectArea)    ConstraintLayout cl_albumSubjectArea; // 앨범 제목 영역

    @BindView(R.id.iv_xButton)             ImageView    iv_xButton;
    @BindView(R.id.iv_albumArrow)          ImageView    iv_albumArrow;
    @BindView(R.id.tv_textAlbumSubject)    TextView     tv_textAlbumSubject;
    @BindView(R.id.iv_camera)              ImageView    iv_camera;
    @BindView(R.id.iv_textDone)            ImageView    iv_textDone;
    @BindView(R.id.tv_selectCount)         TextView     tv_selectCount;
    @BindView(R.id.fl_imageFragmentHolder) FrameLayout fl_imageFragmentHolder;
    @BindView(R.id.fl_rvContainer)         FrameLayout  fl_rvContainer;
    @BindView(R.id.rv_albumList)           RecyclerView rv_albumList;

    //==============================================================================================
    // Instance Variable Define
    //==============================================================================================
    private List<Image>                 arrO_imageList                = new ArrayList<>();
    private List<Folder>                arrO_folderList               = new ArrayList<>();
    private ImagePickerFragment         o_imagePickerFragment         = null;
    private boolean                     b_noSelect                    = false;
    private LinearLayoutManager         o_manager                     = null;
    private CameraOnlyConfig            o_cameraOnlyConfig            = null;
    private ImagePickerConfig           o_imagePickerConfig           = null;
    private RequestManager              o_glideManager                = null;
    private boolean                     b_firstGetFolder              = false;
    private boolean                     b_viewAlbum                   = false;
    private String                      s_cameraFilePath              = null;
    private Bundle                      savedInstanceStateTemp        = null;
    private ImagePickerAlbumListAdpater o_imagePickerAlbumListAdpater = null;

    //==============================================================================================
    // Activity LifeCycle
    //==============================================================================================
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleManager.updateResources(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 스테이터스바 설정
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(getColor(R.color.color_status_bar_white));

        // config 설정 가져옴
        o_imagePickerConfig = getIntent().getExtras().getParcelable(ImagePickerConfig.class.getSimpleName());
        o_cameraOnlyConfig  = getIntent().getExtras().getParcelable(CameraOnlyConfig.class.getSimpleName());

        // 테마 적용
        setTheme(o_imagePickerConfig.getTheme());

        setContentView(R.layout.activity_image_picker_ui);
        ButterKnife.bind(ImagePickerUI.this);

        // Glide Manager
        o_glideManager = Glide.with(ImagePickerUI.this);

        try {
            // 권한 설정 or 앱 죽음 등으로 복구되어서 들어오는 경우
            if( savedInstanceState != null ) {
                savedInstanceStateTemp = savedInstanceState;
                IpLogger.getInstance().e("Fragment has been restored");
                o_imagePickerFragment = (ImagePickerFragment) getSupportFragmentManager().findFragmentById(R.id.fl_imageFragmentHolder);
                o_imagePickerFragment.setInteractionListener(new CustomInteractionListener());
            }

            // 권한 체크
            permissionCheck(savedInstanceState);
        }
        catch ( Exception e ) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // refresh image
        getFolderList();
    }

    //==============================================================================================
    // 초기 설정
    //==============================================================================================
    private void initUI() {
        // RecyclerView 레이아웃 설정
        o_manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        o_manager.setStackFromEnd(false);
        o_manager.setReverseLayout(false);

        // 레이아웃 적용
        rv_albumList.setLayoutManager(o_manager);
        rv_albumList.setScrollbarFadingEnabled(false);
        rv_albumList.setNestedScrollingEnabled(false);
        rv_albumList.setItemViewCacheSize(10);

        initData();
    }

    private void initData(){
        int li_limitCount = o_imagePickerConfig.getLimit();

        StringBuilder lo_builder = new StringBuilder();
        lo_builder.append(0);
        lo_builder.append(" / ");
        lo_builder.append(li_limitCount);

        tv_selectCount.setText(lo_builder.toString());

        getFolderList();
    }

    //==============================================================================================
    // 기본 Listener Override 재 정의
    //==============================================================================================
    @OnClick({
            R.id.iv_xButton, R.id.iv_textDone, R.id.cl_albumSubjectArea, R.id.iv_camera
    })
    public void onClick(View po_view) {
        switch (po_view.getId()){
            case R.id.iv_xButton:
                this.onBackPressed();
                break;

            case R.id.iv_textDone:
                // 선택 안함을 선택했다면
                if ( b_noSelect ) {
                    o_imagePickerFragment.onDone();

                    // 선택안함을 선택하지 않았을 경우
                }
                else {
                    // 아무 이미지도 선택 하지 않았을 경우
                    if (arrO_imageList.size() == 0){
                        Toast.makeText(this, "사진을 선택해주세요", Toast.LENGTH_SHORT).show();
                        // 이미지를 선택 했을 경우
                    }
                    else {
                        o_imagePickerFragment.onDone();
                    }
                }

                break;

            case R.id.cl_albumSubjectArea:
                // 이미지 리스트를 보고있는 상태였다면
                if ( !b_viewAlbum ) {
                    fl_rvContainer.bringToFront();

                    tv_selectCount.setVisibility(View.GONE);
                    iv_camera.setVisibility(View.GONE);
                    iv_textDone.setVisibility(View.GONE);

                    b_viewAlbum = true;
                }
                // 앨범 리스트를 보고있는 상태였다면
                else {
                    fl_imageFragmentHolder.bringToFront();

                    tv_selectCount.setVisibility(View.VISIBLE);
                    iv_camera.setVisibility(View.VISIBLE);
                    iv_textDone.setVisibility(View.VISIBLE);

                    b_viewAlbum = false;
                }

                // refresh image
                getFolderList();

                // animation toggle
                viewAnimationToggle(rv_albumList);
                break;

            case R.id.iv_camera:
                TedPermission.with(this)
                        .setRationaleTitle("카메라 권한 요청")
                        .setRationaleMessage("사진 촬영을 위해 카메라 사용권한이 필요합니다(필수)")
                        .setDeniedTitle("거부")
                        .setDeniedMessage("사진을 촬영하시려면\n앱 설정으로 이동 후 [권한]-[카메라]를 허용해주세요")
                        .setGotoSettingButtonText("앱 설정 이동")
                        .setPermissionListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted() {
                                try {
                                    // 파일 저장 경로는 getExternalCacheDir(); 카메라로 찍은 사진을 사용자가 갤러리에서 볼수있게 하려면 다른 FileProvider를 쓸것
                                    File lo_cacheDirFile = getExternalCacheDir();

                                    if ( !lo_cacheDirFile.exists() ) {
                                        lo_cacheDirFile.mkdir();
                                    }

                                    // 이미지를 담을 TempFile 생성 및 경로 저장
                                    File lo_tempFile = File.createTempFile("IMG_" + (new SimpleDateFormat("yyyyMMddHHmmss")).format(Calendar.getInstance().getTime()) , ".jpg" , lo_cacheDirFile);
                                    s_cameraFilePath = lo_tempFile.getAbsolutePath();

                                    // 카메라로 이동
                                    Intent lo_cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                    Uri lo_photoUri = FileProvider.getUriForFile(ImagePickerUI.this, ImagePickerUI.this.getPackageName() + ".camera.fileprovider", lo_tempFile);
                                    lo_cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, lo_photoUri);
                                    ImagePickerUI.this.startActivityForResult(lo_cameraIntent, LCI_INTENT_TAKE_PICTURE);
                                }
                                catch ( Exception e ){
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onPermissionDenied(List<String> deniedPermissions) {
                                Toast.makeText(ImagePickerUI.this, "카메라 접근이 거부되었습니다", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setPermissions(Manifest.permission.CAMERA)
                        .check();
                break;
        }
    }

    //==============================================================================================
    // 추가적인 Override 정의
    //==============================================================================================
    /**
     * 백버튼 제어
     */
    @Override
    public void onBackPressed() {
        // 앨범 리스트를 보고 있는 상태였다면
        if ( b_viewAlbum ) {
            // 프래그먼트 최상위로 끌어냄
            fl_imageFragmentHolder.bringToFront();

            tv_selectCount.setVisibility(View.VISIBLE);
            iv_camera.setVisibility(View.VISIBLE);
            iv_textDone.setVisibility(View.VISIBLE);

            b_viewAlbum = false;

            // refresh image
            getFolderList();

            // animation toggle : 실제로는 프래그먼트를 먼저 최상위로 했기 때문에 애니메이션이 보이지는 않는다.
            viewAnimationToggle(rv_albumList);
        }
        // 이미지 리스트를 보고있는 상태였다면
        else {
            if ( !o_imagePickerFragment.handleBack() ) {
                super.onBackPressed();
            }
        }
    }

    /**
     * 이미지피커 라이브러리 리스너
     */
    class CustomInteractionListener implements ImagePickerInteractionListener {
        @Override
        public void setTitle(String title) {
        }

        @Override
        public void cancel() {
            finish();
        }

        @Override
        public void selectionChanged(List<Image> larrO_imageList, boolean pb_noSelect) {
            // 선택한 이미지 수, 이미지 최대 선택 가능 수 설정
            int li_selectCount = larrO_imageList.size();
            int li_limitCount  = o_imagePickerConfig.getLimit();

            // tv_selectCount에 사용할 텍스트 쌓기
            StringBuilder lo_builder = new StringBuilder();
            lo_builder.append(li_selectCount);
            lo_builder.append(" / ");
            lo_builder.append(li_limitCount);

            // 텍스트 설정 [ex: 0/4]
            tv_selectCount.setText(lo_builder.toString());

            // 선택안함을 눌렀는지 체크
            b_noSelect = pb_noSelect;

            // 일반 이미지를 눌렀을때
            if ( !pb_noSelect ) {
                arrO_imageList = larrO_imageList;
            }
            // 선택 안함을 눌렀을때
            else {
                arrO_imageList.clear();
            }
        }

        @Override
        public void finishPickImages(Intent result) {
            setResult(RESULT_OK, result);
            finish();
        }
    }

    /**
     * TedPermission이 Granted 됐지만 실제 권한은 얻지못한 경우 권한 재요청 ResultListener
     * @param pi_requestCode
     * @param parrs_permissions
     * @param parrI_grantResults
     */
    @Override
    public void onRequestPermissionsResult(int pi_requestCode, String parrs_permissions[], int[] parrI_grantResults) {
        try {
            switch (pi_requestCode) {
                case 0:
                    if ( parrI_grantResults != null && parrI_grantResults.length > 0 && parrI_grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                        permissionGrantedFunction(savedInstanceStateTemp);
                    }
                    else{
                        permissionDeniedFunction();
                    }
                    break;
            }
        }
        catch ( Exception e ) {

        }
    }

    //==============================================================================================
    // Activity Result
    //==============================================================================================
    @Override
    protected void onActivityResult(int pi_requestCode, int pi_resultCode, @Nullable Intent po_data) {
        super.onActivityResult(pi_requestCode, pi_resultCode, po_data);

        switch ( pi_requestCode ){
            case LCI_INTENT_TAKE_PICTURE:
                if( pi_resultCode == RESULT_OK ){
                    try {
                        File lo_file = new File(s_cameraFilePath);

                        if ( !lo_file.exists() ) {
                            Toast.makeText(ImagePickerUI.this, "카메라 촬영 이미지 가져오기 실패\n잠시 후 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Bitmap lo_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(lo_file));

                        if ( lo_bitmap == null ) {
                            Toast.makeText(ImagePickerUI.this, "카메라 촬영 이미지 가져오기 실패\n잠시 후 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if ( arrO_imageList == null ) {
                            arrO_imageList = new ArrayList<>();
                        }

                        arrO_imageList.clear();
                        arrO_imageList.add(new Image(-1, lo_file.getName(), lo_file.getPath()));

                        o_imagePickerFragment.onDone();
                    }
                    catch ( Exception e ) {
                        Toast.makeText(ImagePickerUI.this, "카메라 촬영 이미지 가져오기 실패\n잠시 후 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                    }
                }

                break;
        }
    }

    //==============================================================================================
    // User Functions
    //==============================================================================================
    /**
     * 권한 체크
     * 승인 : fragment 생성 및 Listener 등록 후 initUI & initData
     * 거절 : 해당 액티비티 finish
     * @param savedInstanceState
     */
    private void permissionCheck(Bundle savedInstanceState) throws Exception {
        TedPermission.with(this)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        try {
                            // TedPermission에서 Granted 됐지만 실제로는 PERMISSION_DENIED 되어있는 경우 새로 요청
                            if ( PackageManager.PERMISSION_DENIED == ActivityCompat.checkSelfPermission(ImagePickerUI.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ) {
                                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                                requestPermissions(permissions, 0);
                                return;
                            }

                            permissionGrantedFunction(savedInstanceState);
                        }
                        catch ( Exception e ) {

                        }
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        try {
                            permissionDeniedFunction();
                        }
                        catch ( Exception e ) {

                        }
                    }
                })
                .setRationaleTitle("저장소 권한 요청") // 요청사요 제목
                .setRationaleMessage("사진첩에 저장된 이미지들을 불러오기 위해\n저장소 읽기 권한이 필요합니다(필수)") // 요청하는 이유
                .setDeniedTitle("거부")
                .setDeniedMessage("이미지들을 불러오려면\n앱 설정으로 이동 후 [권한]-[저장공간]을 허용해주세요")
                .setGotoSettingButtonText("앱 설정 이동")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

    /**
     * 권한이 허용 된 경우 실행 할 함수
     */
    private void permissionGrantedFunction(Bundle savedInstanceState) throws Exception {
        // 이미지피커 프래그먼트 설정
        if ( savedInstanceState == null ) {
            IpLogger.getInstance().e("Making fragment");
            o_imagePickerFragment = ImagePickerFragment.newInstance(o_imagePickerConfig, o_cameraOnlyConfig);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fl_imageFragmentHolder, o_imagePickerFragment);
            ft.commit();

            o_imagePickerFragment.setInteractionListener(new CustomInteractionListener());
        }

        // 뷰 설정
        initUI();
    }

    /**
     * 권한 요청 거절 된 경우 실행 할 함수
     */
    private void permissionDeniedFunction() throws Exception {
        Toast.makeText(ImagePickerUI.this, "사진첩 접근이 거부되었습니다", Toast.LENGTH_SHORT).show();
        finish();
    }

    /**
     * FolderList를 불러오는 함수
     */
    private void getFolderList() {
        new Handler().postDelayed(() -> {
            try{
                arrO_folderList = o_imagePickerFragment.getImageFolderList();

                // 프래그먼트에서 아직 ImageLoad가 수행되지 않았다면 null이 반환되므로 반드시 체크하고 다시 불러온다.
                if ( arrO_folderList == null ) {
                    getFolderList();
                    return;
                }
                // 프래그먼트에서 정상적으로 이미지를 가지고 왔을때
                else {
                    // 정렬을 위한 TempList 생성
                    ArrayList<Folder> larrO_tempList = new ArrayList<>();

                    // 모든 사진은 항상 첫번째에 위치하도록한다
                    larrO_tempList.add(arrO_folderList.get(0));
                    arrO_folderList.remove(0);

                    // 폴더 이름 순으로 정렬
                    Collections.sort(arrO_folderList, new Comparator<Folder>(){
                        public int compare(Folder obj1, Folder obj2) {
                            return obj1.getFolderName().compareTo(obj2.getFolderName());
                        }
                    });

                    // tempList에 저장 시킨후 다시 arrO_folderList에 저장
                    larrO_tempList.addAll(arrO_folderList);
                    arrO_folderList.clear();
                    arrO_folderList.addAll(larrO_tempList);

                    // 어댑터 설정
                    o_imagePickerAlbumListAdpater = new ImagePickerAlbumListAdpater(ImagePickerUI.this, arrO_folderList, o_glideManager);
                    rv_albumList.setAdapter(o_imagePickerAlbumListAdpater);

                    // 첫 수행인지 구분
                    if(!b_firstGetFolder){
                        b_firstGetFolder = true;

                        // 첫 수행을 할때 태그에 리사이클러뷰의 영역의 높이를 넣어준다
                        rv_albumList.post(() -> {
                            rv_albumList.setTag(fl_rvContainer.getHeight());
                            rv_albumList.setVisibility(View.GONE);
                        });
                    }

                }

            } catch (Exception e){
                getFolderList();
            }
        }, 50);
    }

    /**
     * 뷰에 애니메이션 적용 (ON/OFF)
     * @param po_view - 애니메이션 대상
     */
    private void viewAnimationToggle(View po_view) {
        cl_albumSubjectArea.setClickable(false);

        po_view.post(() -> {
            ValueAnimator lo_ani = null;

            if (null == po_view.getTag()) {
                po_view.setTag(fl_rvContainer.getHeight());
            }

            int li_height = (int) po_view.getTag();

            if (po_view.getVisibility() == View.GONE) {

                lo_ani = ValueAnimator.ofInt(1, li_height);
                lo_ani.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        po_view.setVisibility(View.VISIBLE);

                        // 앨범 옆 화살표 0도에서 180도로 회전
                        GF_ROTATE_ANIMATION(iv_albumArrow, 0, 180, 150);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
            }
            else {
                lo_ani = ValueAnimator.ofInt(li_height, 1);
                lo_ani.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        // 앨범 옆 화살표 180도에서 360도로 회전
                        GF_ROTATE_ANIMATION(iv_albumArrow, 180, 360, 150);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        po_view.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
            }

            lo_ani.addUpdateListener(va -> {
                int li_value = (int) va.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = po_view.getLayoutParams();
                layoutParams.height = li_value;
                po_view.setLayoutParams(layoutParams);
            });

            lo_ani.setDuration(150);
            lo_ani.start();
        });

        new Handler().postDelayed(() -> cl_albumSubjectArea.setClickable(true), 150);
    }

    public static void GF_ROTATE_ANIMATION(View po_view, float pf_fromDegrees, float pf_toDegrees, int pi_duration){
        // 애니메이션 설정
        RotateAnimation lo_rotateAnimation = new RotateAnimation(pf_fromDegrees, pf_toDegrees, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        lo_rotateAnimation.setDuration(pi_duration); // 애니메이션 수행 시간
        lo_rotateAnimation.setFillAfter(true);       // 애니메이션 끝나고 유지
        lo_rotateAnimation.setInterpolator(new AccelerateInterpolator());

        // 애니메이션 시작
        po_view.startAnimation(lo_rotateAnimation);

    }
}