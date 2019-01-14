package com.huangfuren.amusementparkmanagementsystem.queue;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.qrcode.QRCodeWriter;
import com.huangfuren.amusementparkmanagementsystem.R;
import com.huangfuren.amusementparkmanagementsystem.model.Project;
import com.huangfuren.amusementparkmanagementsystem.news.IssueNewActivity;
import com.socks.okhttp.plus.OkHttpProxy;
import com.socks.okhttp.plus.callback.OkCallback;
import com.socks.okhttp.plus.parser.OkTextParser;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import okhttp3.OkHttpClient;

import static com.huangfuren.amusementparkmanagementsystem.MainActivity.ipAddress;

public class QueueMainActivity2 extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private ImageView imageView;
    private TextView textView;
    private EditText idText, nameText, availableText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue_main2);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("维修啦");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        imageView = this.findViewById(R.id.imageView_zxing);
        textView = this.findViewById(R.id.textview_zxing);
        idText = this.findViewById(R.id.project_id);
        nameText = this.findViewById(R.id.project_name);
        availableText = this.findViewById(R.id.project_available);

        findViewById(R.id.button_zxing).setOnClickListener(this);
        findViewById(R.id.button_start).setOnClickListener(this);
        findViewById(R.id.button_native).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_zxing:
                String id = idText.getText().toString().trim();
                String name = nameText.getText().toString().trim();
                String available = availableText.getText().toString().trim();
                if (TextUtils.isEmpty(id)||TextUtils.isEmpty(name)||TextUtils.isEmpty(available)) {
                    Toasty.info(QueueMainActivity2.this,"请输入内容").show();
                    return;
                }
                String text = "{\"id\":\""+id+"\",\"name\":\""+name+"\",\"available\":\""+available+"\"}";
                //新设备增加
                Project project = new Project();
                project.setId(Integer.valueOf(id));
                project.setName(name);
                project.setAvailable(Integer.valueOf(available));
                OkHttpClient okHttpClient = OkHttpProxy.getInstance();
                OkHttpProxy.get()
                        .url("http://"+ipAddress+"/APMS/addProject")
                        .addParams("project", JSON.toJSONString(project))
                        .tag(this)
                        .enqueue(new OkCallback<String>(new OkTextParser() {
                        }) {
                            @Override
                            public void onSuccess(int code, String flag) {
                                if (flag.equals("1")){
                                    Toasty.success(QueueMainActivity2.this,"新设备添加成功").show();
                                    finish();
                                }
                                else {
                                    Toasty.error(QueueMainActivity2.this, "新设备添加失败").show();
                                }
                            }

                            @Override
                            public void onFailure(Throwable e) {
                                //Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                                Toasty.error(QueueMainActivity2.this,"网络异常").show();
                                Log.e("网络异常",e.getMessage());
                                e.printStackTrace();
                            }
                        });
                //生成二维码显示在imageView上
                imageView.setImageBitmap(generateBitmap(text, 600, 600));
//                Toasty.success(QueueMainActivity2.this,"可截屏保存哦").show();
                break;
            case R.id.button_start:
                new IntentIntegrator(this)
                        .setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)// 扫码的类型,可选：一维码，二维码，一/二维码
                        //.setPrompt("请对准二维码")// 设置提示语
                        .setCameraId(0)// 选择摄像头,可使用前置或者后置
                        .setBeepEnabled(true)// 是否开启声音,扫完码之后会"哔"的一声
                        .initiateScan();// 初始化扫码
                break;
            case R.id.button_native:
                new IntentIntegrator(this)
                        .setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)// 扫码的类型,可选：一维码，二维码，一/二维码
                        //.setPrompt("请对准二维码")// 设置提示语
                        .setCameraId(0)// 选择摄像头,可使用前置或者后置
                        .setBeepEnabled(true)// 是否开启声音,扫完码之后会"哔"的一声
                        .setCaptureActivity(QrCodeActivity.class)//自定义扫码界面
                        .initiateScan();// 初始化扫码
                break;
        }
    }

    /**
     * 生成固定大小的二维码(不需网络权限)
     *
     * @param content 需要生成的内容
     * @param width   二维码宽度
     * @param height  二维码高度
     * @return
     */
    private Bitmap generateBitmap(String content, int width, int height) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, String> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        try {
            BitMatrix encode = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            int[] pixels = new int[width * height];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (encode.get(j, i)) {
                        pixels[i * width + j] = 0x00000000;
                    } else {
                        pixels[i * width + j] = 0xffffffff;
                    }
                }
            }
            return Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.RGB_565);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //扫码结果
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                //扫码失败
            } else {
                String result = intentResult.getContents();//返回值
                textView.setText("扫码结果：" + result);
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
