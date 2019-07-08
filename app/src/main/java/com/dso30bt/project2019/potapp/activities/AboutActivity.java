package com.dso30bt.project2019.potapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.dso30bt.project2019.potapp.BuildConfig;
import com.dso30bt.project2019.potapp.R;
import com.dso30bt.project2019.potapp.utils.NavUtil;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Joesta on 2019/07/07.
 */
public class AboutActivity extends AppCompatActivity {

    private TextView tvVersion;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        initUI();

        final String version = "Version " + BuildConfig.VERSION_NAME;
        tvVersion.setText(version);
    }

    private void initUI() {
        tvVersion = findViewById(R.id.appVersion);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        gotHome();
    }

    private void gotHome() {
        NavUtil.moveToNextActivity(AboutActivity.this, MainActivity.class);
    }
}
