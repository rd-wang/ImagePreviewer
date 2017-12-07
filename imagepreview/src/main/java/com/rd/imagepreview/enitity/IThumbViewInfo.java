package com.rd.imagepreview.enitity;

import android.graphics.Rect;
import android.os.Parcelable;

/**
 *  Deprecated: 图片预览接口
 */
public interface IThumbViewInfo extends Parcelable {
    /****
     * 图片地址
     * @return String
     * ****/
    String getUrl();

    /**
     * 记录坐标
     * @return Rect
     ***/
    Rect getBounds();


}
