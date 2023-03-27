package com.example.cookieclicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;

import android.view.animation.Animation;

import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {

    LinearLayout linearLayout;
    ConstraintLayout constraintLayout;
    ImageView twitch;
    TextView displayScore;
    Score score;
    int numSpawn;
    int numClicked;
    int passiveGen;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        passiveGen = 1;

        linearLayout = findViewById(R.id.upgrades);

        constraintLayout = findViewById(R.id.constraintLayout);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(750);
        animationDrawable.setExitFadeDuration(750);
        animationDrawable.start();

        displayScore = findViewById(R.id.textView);
        twitch = findViewById(R.id.imageView);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f, Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f);
        scaleAnimation.setDuration(100);
        twitch.setOnClickListener(view -> {
            view.startAnimation(scaleAnimation);
            score.setScore(Objects.requireNonNull(score.getScore()).get()+1);
            plusOne();
        });

        score = new Score();
        score.setListener(() -> {
            displayScore.setText("Views: " + Objects.requireNonNull(score.getScore()).get());
            if(score.getScore().get()/50>numSpawn){
                fadeViewIn();
                numSpawn++;
            }
        });
        score.setScore(0);

        Thread passiveIncome = new Thread(() -> {
            while(true){
                score.setListener(() -> runOnUiThread(() -> {
                    displayScore.setText("Views: " + Objects.requireNonNull(score.getScore()).get());
                    if(score.getScore().get()/50>numSpawn){
                        fadeViewIn();
                        numSpawn++;
                    }
                }));
                score.setScore(Objects.requireNonNull(score.getScore()).get()+numClicked*passiveGen);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        passiveIncome.start();
    }
    public void plusOne(){
        ImageView imageView = new ImageView(this);
        imageView.setId(View.generateViewId());
        imageView.setImageResource(R.drawable.viewer);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintLayout.addView(imageView);
        constraintSet.clone(constraintLayout);
        constraintSet.connect(imageView.getId(), ConstraintSet.TOP, constraintLayout.getId(), ConstraintSet.TOP);
        constraintSet.connect(imageView.getId(), ConstraintSet.BOTTOM, constraintLayout.getId(), ConstraintSet.BOTTOM);
        constraintSet.connect(imageView.getId(), ConstraintSet.RIGHT, constraintLayout.getId(), ConstraintSet.RIGHT);
        constraintSet.connect(imageView.getId(), ConstraintSet.LEFT, constraintLayout.getId(), ConstraintSet.LEFT);
        constraintSet.setVerticalBias(imageView.getId(), (float)Math.random()/2);
        constraintSet.setHorizontalBias(imageView.getId(), (float)Math.random());
        constraintSet.constrainWidth(imageView.getId(), 100);
        constraintSet.constrainHeight(imageView.getId(), 100);
        constraintSet.applyTo(constraintLayout);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(imageView, "translationY", -1050f);
        objectAnimator.setDuration(2000);
        objectAnimator.start();
    }
    public void fadeViewIn(){
        ImageView imageView = new ImageView(this);
        imageView.setId(View.generateViewId());
        imageView.setImageResource(R.drawable.pogchamp);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintLayout.addView(imageView);
        constraintSet.clone(constraintLayout);
        constraintSet.connect(imageView.getId(), ConstraintSet.TOP, constraintLayout.getId(), ConstraintSet.TOP);
        constraintSet.connect(imageView.getId(), ConstraintSet.BOTTOM, constraintLayout.getId(), ConstraintSet.BOTTOM);
        constraintSet.connect(imageView.getId(), ConstraintSet.RIGHT, constraintLayout.getId(), ConstraintSet.RIGHT);
        constraintSet.connect(imageView.getId(), ConstraintSet.LEFT, constraintLayout.getId(), ConstraintSet.LEFT);
        constraintSet.setVerticalBias(imageView.getId(), (float)Math.random()*.5f + .5f);
        constraintSet.setHorizontalBias(imageView.getId(), (float)Math.random());
        constraintSet.constrainWidth(imageView.getId(), 250);
        constraintSet.constrainHeight(imageView.getId(), 250);
        constraintSet.applyTo(constraintLayout);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(imageView, "alpha", .1f, 1f);
        fadeIn.setDuration(1000);
        fadeIn.start();
        imageView.setOnClickListener(view -> {
            ObjectAnimator fadeOut = ObjectAnimator.ofFloat(imageView, "alpha", 1f, 0.0f);
            fadeOut.setDuration(1000);
            fadeOut.start();
            Thread removeView = new Thread(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(() -> {
                    constraintLayout.removeView(imageView);
                    ImageView copy = new ImageView(MainActivity.this);
                    copy.setId(View.generateViewId());
                    copy.setImageResource(R.drawable.pogchamp);
                    linearLayout.addView(copy, 125, 125);
                    RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, .5f,Animation.RELATIVE_TO_SELF,.5f);
                    rotateAnimation.setDuration(1000);
                    copy.startAnimation(rotateAnimation);
                });
            });
            removeView.start();
            numSpawn--;
            score.setScore(Objects.requireNonNull(score.getScore()).get()-50);
            numClicked++;
        });
    }
    public static class Score{
        private final AtomicInteger score = new AtomicInteger();
        private updateListener listener;

        public AtomicInteger getScore(){
            return score;
        }

        public void setScore(int score){
            this.score.set(score);
            listener.onChanged();
        }

        public void setListener(updateListener listener) {
            this.listener = listener;
        }

        public interface updateListener{
            void onChanged();
        }
    }

}