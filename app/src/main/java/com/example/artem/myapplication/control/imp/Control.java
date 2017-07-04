package com.example.artem.myapplication.control.imp;

import android.content.Context;
import android.graphics.Point;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.artem.myapplication.R;
import com.squareup.picasso.Picasso;

/**
 * Created by artem on 03.07.17.
 */

public class Control extends View  {

    private Point position;
    private int index;
    private int avalible;

    public Control(Context context) {
        super(context);
    }


    public Control(final ViewGroup container, int index, Point position, int avalible) {
        super(container.getContext());
        this.position = position;
        this.avalible = avalible;
        this.index = index;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(96, 96);
        setLayoutParams(params);
        onUIChanged(1, 1);

//        onMapMove(position);
    }

    public int getIndex(){
        return index;
    }

    public void onUIChanged(float zoom, long time) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams();

        params.height = (int)(96*zoom);
        params.width = (int)(96*zoom);
        setLayoutParams(params);
        switch (avalible){
            case 0:
                setBackgroundResource(R.drawable.fixable_background_small);
                break;
            case 1:
                setBackgroundResource(R.drawable.fixable_background_on_small);
                break;
        }
        //        Picasso.with(container.getContext()).load(R.drawable.square_passive).resize(96, 96).into(this);
        setX(position.x*zoom);
        setY(position.y*zoom);

    }

}
