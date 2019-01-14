package com.huangfuren.amusementparkmanagementsystem.queue;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.google.zxing.ResultPoint;
import com.huangfuren.amusementparkmanagementsystem.R;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.List;

/**
 * 扫描游乐设施二维码，进行排队，
 * 客户端提交设施和用户id进行排队，游客可查询自己的排队信息，
 * 游客可通过显示二维码（含游客id），通过设施二维码扫描处扫描进入设施。
 * 可查看的信息：
 *      1、在队伍中的位置
 *      2、预计排队时间
 *      3、
 */
public class ScanQRCodeActivity extends AppCompatActivity {


    private CaptureManager capture;
    private DecoratedBarcodeView bv_barcode;
    private BarcodeCallback barcodeCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qrcode);

        bv_barcode = (DecoratedBarcodeView) findViewById(R.id.bv_barcode);
        barcodeCallback = new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (result != null){
                    Log.e(getClass().getName(), "获取到的扫描结果是：" + result.getText());
                }
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {

            }
        };
        bv_barcode.decodeSingle(barcodeCallback);
        capture = new CaptureManager(this, bv_barcode);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();
    }


    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        capture.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return bv_barcode.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

}