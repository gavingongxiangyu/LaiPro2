package com.yx.yxcustomprogress;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;

public class MainActivity extends Activity {

    private ColorfulRingProgressView project_schedul_crpv = null;
    private ObjectAnimator anim = null;
    EditText et_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        project_schedul_crpv = (ColorfulRingProgressView) findViewById(R.id.project_schedul_crpv);
        project_schedul_crpv.setPercent(60);//设置进度条的进度，最大值为10

        project_schedul_crpv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                project_schedul_crpv.startAnim();
            }
        });
        et_progress = (EditText)findViewById(R.id.et_progress);
        findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                project_schedul_crpv.setPercent(Float.parseFloat(et_progress.getText().toString()),true);
                project_schedul_crpv.startAnim();
            }
        });
    }
}
