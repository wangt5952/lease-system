package com.elextec.persist.model.mybatis.ext;

import com.elextec.persist.model.mybatis.BizParts;

/**
 * 配件扩展类
 * Created by js_gg on 2018/2/2.
 */
public class BizPartsExt extends BizParts {
    //制商名称
    private String mfrsName;

    public String getMfrsName() {
        return mfrsName;
    }

    public void setMfrsName(String mfrsName) {
        this.mfrsName = mfrsName;
    }
}
