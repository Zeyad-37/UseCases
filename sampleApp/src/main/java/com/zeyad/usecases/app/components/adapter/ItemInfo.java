package com.zeyad.usecases.app.components.adapter;

/**
 * @author by zeyad on 20/05/16.
 */
public class ItemInfo {
    public static final int HEADER = 1, FOOTER = 2, LOADING = 3, SECTION_HEADER = 4, CARD_SECTION_HEADER = 5;
    private final int layoutId;
    private Object data;
    private long id;
    private boolean isEnabled = true;

    public ItemInfo(Object data, int layoutId) {
        this.data = data;
        this.layoutId = layoutId;
    }

    public long getId() {
        return id;
    }

    public ItemInfo setId(long id) {
        this.id = id;
        return this;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getLayoutId() {
        return layoutId;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public ItemInfo setEnabled(boolean enabled) {
        isEnabled = enabled;
        return this;
    }
}

