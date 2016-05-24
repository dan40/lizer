package de.dukadinov.lizer;

import android.app.Activity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

public class SettingsActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prefs_layout);

        SeekBar dragForNext = (SeekBar) findViewById(R.id.seekBar);
        final TextView dragForNextText = (TextView) findViewById(R.id.textView3);
        dragForNext.setProgress((int) (LizerSettings.DRAG_FOR_NEXT * 100));
        setProgressText(dragForNextText, dragForNext.getProgress());
        dragForNext.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                LizerSettings.DRAG_FOR_NEXT = (float) progress / 100;
                setProgressText(dragForNextText, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        SeekBar scaleInStartFrom = (SeekBar) findViewById(R.id.seekBar2);
        final TextView scaleInStartFromText = (TextView) findViewById(R.id.textView4);
        scaleInStartFrom.setProgress((int) (LizerSettings.SCALE_IN_START_FROM * 100));
        setProgressText(scaleInStartFromText, scaleInStartFrom.getProgress());
        scaleInStartFrom.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                LizerSettings.SCALE_IN_START_FROM = (float) progress / 100;
                setProgressText(scaleInStartFromText, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setProgressText(TextView textView, int progress) {
        textView.setText(progress < 100 ? "0." + (progress < 10 ? "0" : "") + progress : "1.00");
    }
}