package com.prof.dbtest.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.prof.dbtest.R;
import com.prof.dbtest.activities.MainActivity;
import com.prof.dbtest.activities.ApplyActivity;


public class SongListAdaptr extends BaseAdapter {
    private LayoutInflater inflater = null;
    public Context mContext;
    Typeface samsung_thin;
    private String[] songsList;

    public long getItemId(int i) {
        return (long) i;
    }

    public SongListAdaptr(Context context, String[] strArr) {
        this.mContext = context;
        this.songsList = strArr;
        //this.samsung_thin = Typeface.createFromAsset(context.getAssets(), "sf_regular.otf");
    }

    public int getCount() {
        return this.songsList.length;
    }

    public Object getItem(int i) {
        return this.songsList[i];
    }

    public View getView(final int i, View view, ViewGroup viewGroup) {
       @SuppressLint("ViewHolder") View inflate = ((LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.name_list_adapter, (ViewGroup) null);
        TextView textView = (TextView) inflate.findViewById(R.id.song_name);
        textView.setTypeface(this.samsung_thin);
        textView.setText(this.songsList[i]);
        inflate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ((MainActivity) SongListAdaptr.this.mContext).playSong(i);
            }
        });
        ((ImageView) inflate.findViewById(R.id.menu_btn)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (((MainActivity) SongListAdaptr.this.mContext).mediaPlayer != null && ((MainActivity) SongListAdaptr.this.mContext).mediaPlayer.isPlaying()) {
                    ((MainActivity) SongListAdaptr.this.mContext).mediaPlayer.reset();
                }
                Intent intent = new Intent(SongListAdaptr.this.mContext, ApplyActivity.class);
                intent.putExtra("position", i);
                SongListAdaptr.this.mContext.startActivity(intent);
            }
        });
        ((ImageView) inflate.findViewById(R.id.play_btn)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ((MainActivity) SongListAdaptr.this.mContext).playSong(i);
            }
        });
        return inflate;
    }
}
