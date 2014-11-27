/*
 * Copyright © 2014 YAOCHEN Corporation, All Rights Reserved.
 */
package com.easyooo.framework.demo.mapper;

import org.springframework.stereotype.Repository;

import com.easyooo.framework.demo.domain.TUserDetail;
import com.easyooo.framework.sharding.annotation.Table;
import com.easyooo.framework.support.mybatis.Pagination;

@Repository
@Table("t_user_detail")
public interface TUserDetailMapper {
    int deleteByPrimaryKey(TUserDetail user);

    int insert(TUserDetail record);

    int insertSelective(TUserDetail record);

    TUserDetail selectByPrimaryKey(Long userId);

    int updateByPrimaryKeySelective(TUserDetail record);

    int updateByPrimaryKey(TUserDetail record);
    
    void selectAllWithPage(Pagination page);
}