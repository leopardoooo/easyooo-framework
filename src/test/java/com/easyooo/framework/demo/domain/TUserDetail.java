/*
 * Copyright © 2014 YAOCHEN Corporation, All Rights Reserved.
 */
package com.easyooo.framework.demo.domain;

import com.easyooo.framework.cache.annotations.Cache;
import com.easyooo.framework.cache.annotations.KeyProperty;
import com.easyooo.framework.cache.annotations.MiniTable;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Cache
@MiniTable
public class TUserDetail {
	@KeyProperty
    private Long userId;

    private String sex;

    private String linkman;

    private Integer tel;
    
    public TUserDetail(Long userId){
    	this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex == null ? null : sex.trim();
    }

    public String getLinkman() {
        return linkman;
    }

    public void setLinkman(String linkman) {
        this.linkman = linkman == null ? null : linkman.trim();
    }

    public Integer getTel() {
        return tel;
    }

    public void setTel(Integer tel) {
        this.tel = tel;
    }
}