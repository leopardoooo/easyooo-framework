/*
 * Copyright © 2014 YAOCHEN Corporation, All Rights Reserved.
 */
package com.easyooo.framework.demo.domain;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import com.easyooo.framework.cache.CacheLevel;
import com.easyooo.framework.cache.annotations.Cache;
import com.easyooo.framework.cache.annotations.Group;
import com.easyooo.framework.cache.annotations.KeyProperty;
import com.easyooo.framework.cache.annotations.MiniTable;
import com.easyooo.framework.sharding.ShardingStrategy;

@AllArgsConstructor
@NoArgsConstructor
@Cache(level=CacheLevel.JVM_TO_REDIS)
@lombok.ToString
@MiniTable
public class TUser implements ShardingStrategy{
	@KeyProperty
    private Long userId;
	@Group
    private String userName;
	@Group(name="status")
	private String status;
	
	private Date createDate1;
	private Date createDate2;
	private Date createDate3;
	
	public TUser(Long userId){
		this.userId = userId;
	}
	
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

	@Override
	public Long getShardingKeyword() {
		return userId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getCreateDate1() {
		return createDate1;
	}

	public void setCreateDate1(Date createDate1) {
		this.createDate1 = createDate1;
	}

	public Date getCreateDate2() {
		return createDate2;
	}

	public void setCreateDate2(Date createDate2) {
		this.createDate2 = createDate2;
	}

	public Date getCreateDate3() {
		return createDate3;
	}

	public void setCreateDate3(Date createDate3) {
		this.createDate3 = createDate3;
	}
}