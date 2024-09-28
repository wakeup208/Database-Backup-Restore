package com.prof.dbtest.models;

import java.io.Serializable;

public class RingtoneInfo implements Serializable {
    private int audioResource;
    private String fileName;
    private int imageResource;
    private boolean isFavorite;
    private boolean isPlaying;
    private String name;

    public RingtoneInfo() {
        setAudioResource(0);
        setName("");
        setImageResource(0);
        setFavorite(false);
        setPlaying(false);
        setFileName("");
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public boolean isFavorite() {
        return this.isFavorite;
    }

    public void setFavorite(boolean z) {
        this.isFavorite = z;
    }

    public boolean isPlaying() {
        return this.isPlaying;
    }

    public void setPlaying(boolean z) {
        this.isPlaying = z;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String str) {
        this.fileName = str;
    }

    public int getAudioResource() {
        return this.audioResource;
    }

    public void setAudioResource(int i) {
        this.audioResource = i;
    }

    public int getImageResource() {
        return this.imageResource;
    }

    public void setImageResource(int i) {
        this.imageResource = i;
    }
}
