package com.example.androiddev;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androiddev.adapter.ChatActivityAdapter;
import com.example.androiddev.dto.ChatVO;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatActivity extends AppCompatActivity {
    // ====================================================================================
    // Constant
    // ====================================================================================
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String SERVER_URL = "http://192.168.192.175:5000/";

    // ====================================================================================
    // Object
    // ====================================================================================
    private Socket socket;
    private boolean is_connect = false;
    private ArrayList<ChatVO> arrO_list = new ArrayList<>();
    private ChatActivityAdapter adapter;

    // ====================================================================================
    // BindView
    // ====================================================================================
    @BindView(R.id.rcv_list) RecyclerView rcv_list;
    @BindView(R.id.tv_text)  EditText     tv_text;
    @BindView(R.id.btn_send) Button       btn_send;

    // ====================================================================================
    // Activity LifeCycle
    // ====================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager lo_layoutManager = new LinearLayoutManager(ChatActivity.this, LinearLayoutManager.VERTICAL, true);
        lo_layoutManager.setStackFromEnd(false);
        lo_layoutManager.setReverseLayout(false);
        rcv_list.setLayoutManager(lo_layoutManager);

        adapter = new ChatActivityAdapter(this, arrO_list);
        rcv_list.setAdapter(adapter);

        // 소켓연결
        connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socketClose();
    }

    // ====================================================================================
    // Action EVENT
    // ====================================================================================
    /**
     * 클릭 이벤트 선언
     * @param po_view
     */
    @OnClick({
            R.id.btn_send
    })
    public void onClick(View po_view) {
        switch ( po_view.getId() ) {
            case R.id.btn_send :
                if ( tv_text.getText().length() < 1 ) {
                    return;
                }

                message();
                break;

            default:break;
        }
    }

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
     * 메세지 보내기
     */
    private void message() {
        if ( null == socket || !is_connect ) {
            Toast.makeText(ChatActivity.this, "연결을 확인해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String text = tv_text.getText().toString();
            socket.emit("SEND", text);
            this.setData(new ChatVO(text, true));

            tv_text.setText("");
            tv_text.requestFocus();
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * 소켓 연결
     * @return
     */
    private void connect() {
        if ( socket != null && socket.connected() && is_connect ) {
            Toast.makeText(ChatActivity.this, "연결중 상태입니다", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            socket = IO.socket(SERVER_URL);
            socket.on(Socket.EVENT_CONNECT, onConnect);
            socket.on(Socket.EVENT_DISCONNECT, onDisConnect);
            socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            socket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            socket.on("SEND", onMessage);
            socket.connect();
            is_connect = true;
        }
        catch ( Exception e ) {
            e.printStackTrace();
            is_connect = false;
            socket.close();
            socket = null;
        }
    }

    /**
     * 소켓 중지
     */
    private void socketClose() {
        if ( null == socket || !is_connect ) {
            return;
        }

        socket.disconnect();
        socket.off(Socket.EVENT_CONNECT, onConnect);
        socket.off(Socket.EVENT_DISCONNECT, onDisConnect);
        socket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        socket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        socket.off("SEND", onMessage);
        is_connect = false;

        if ( null != socket ) {
            socket.close();
            socket = null;
        }
    }

    /**
     * 서버와 소켓 연결 성공 시 리스너
     */
    private Emitter.Listener onConnect = (args) -> {
        runOnUiThread(() -> {
            Toast.makeText(ChatActivity.this, "연결 성공", Toast.LENGTH_SHORT).show();
        });
    };

    /**
     * 서버와 소켓 연결 실패 시 리스너
     */
    private Emitter.Listener onConnectError = (args) -> {
        runOnUiThread(() -> {
            Toast.makeText(ChatActivity.this, "연결에 실패하였습니다 " + args[0], Toast.LENGTH_SHORT).show();

            if ( null != socket ) {
                socket.close();
                socket = null;
            }
        });
    };

    /**
     * 서버와 소켓 연결 중지
     */
    private Emitter.Listener onDisConnect = (args) -> {
        runOnUiThread(() -> {
            Toast.makeText(ChatActivity.this, "연결이 종료 되었습니다", Toast.LENGTH_SHORT).show();
            socketClose();
        });
    };

    /**
     * 서버에서 데이터 받기
     */
    private Emitter.Listener onMessage = (args) -> {
        runOnUiThread(() -> {
            this.setData(new ChatVO((String)args[0], false));
        });
    };

    private void setData(ChatVO data) {
        arrO_list.add(data);
        adapter.setArrO_list(this.arrO_list);
        adapter.notifyDataSetChanged();

        rcv_list.scrollToPosition(arrO_list.size() - 1);
    }
}
