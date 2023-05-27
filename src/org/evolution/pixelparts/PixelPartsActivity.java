/*
 * Copyright (C) 2023 The Evolution X Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.evolution.pixelparts;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceManager;

import com.android.settingslib.collapsingtoolbar.CollapsingToolbarBaseActivity;
import com.android.settingslib.widget.R;

import java.util.Arrays;
import java.util.Random;

import com.plattysoft.leonids.ParticleSystem;

import org.evolution.pixelparts.utils.ShakeUtils;

public class PixelPartsActivity extends CollapsingToolbarBaseActivity
       implements ShakeUtils.OnShakeListener {

    private static final String TAG = "PixelParts";

    private ShakeUtils mShakeUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(R.id.content_frame,
                new PixelParts(), TAG).commit();

        mShakeUtils = new ShakeUtils(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mShakeUtils.bindShakeListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mShakeUtils.unBindShakeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mShakeUtils.unBindShakeListener(this);
    }

    @Override
    public void onShake(double speed) {
        Random rand = new Random();
        int firstRandom = rand.nextInt(91-0);
        int secondRandom = rand.nextInt(181-90)+90;
        int thirdRandom = rand.nextInt(181-0);

        Drawable easteregg = getResources().getDrawable(R.drawable.easteregg,null);
        int randomColor;
        randomColor = Color.rgb(
                Color.red(rand.nextInt(0xFFFFFF)),
                Color.green(rand.nextInt(0xFFFFFF)),
                Color.blue(rand.nextInt(0xFFFFFF)));
        easteregg.setTint(randomColor);

        ParticleSystem ps = new ParticleSystem(this, 50, easteregg, 2000);
        ps.setScaleRange(0.7f,1.3f);
        ps.setSpeedRange(0.1f,0.25f);
        ps.setAcceleration(0.0001f,thirdRandom);
        ps.setRotationSpeedRange(firstRandom,secondRandom);
        ps.setFadeOut(300);
        ps.oneShot(this.findViewById(android.R.id.content),50);
    }
}
