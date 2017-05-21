package com.apin.base.dao;

import com.apin.base.bean.ApinApplication;

/**
 * Created by Administrator on 2017/1/11.
 */
public interface ApplicationMapper {

    public ApinApplication selectApplicationById(String applicationId);
}
