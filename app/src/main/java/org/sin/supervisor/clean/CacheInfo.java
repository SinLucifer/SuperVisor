package org.sin.supervisor.clean;

import android.graphics.drawable.Drawable;

/**
 * Created by Sin on 2015/10/6.
 */
public class CacheInfo {
    private String name;
    private String packageName;
    private Drawable icon;
    private String codeSize;
    private String dataSize;
    private String cacheSize;

    public CacheInfo(){
        codeSize = "0byte";
        dataSize = "0byte";
        cacheSize = "0byte";
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getCodeSize() {
        return codeSize;
    }

    public void setCodeSize(String codeSize) {
        this.codeSize = codeSize;
    }

    public String getDataSize() {
        return dataSize;
    }

    public void setDataSize(String dataSize) {
        this.dataSize = dataSize;
    }

    public String getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(String cacheSize) {
        this.cacheSize = cacheSize;
    }


}
