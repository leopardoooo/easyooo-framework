/*
 * Copyright © 2014 YAOCHEN Corporation, All Rights Reserved.
 */
package com.easyooo.framework.demo.mapper;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.easyooo.framework.cache.annotations.GroupStrategy;
import com.easyooo.framework.demo.domain.TUser;
import com.easyooo.framework.sharding.annotation.Table;
import com.easyooo.framework.support.mybatis.Pagination;

@Repository
@Table("t_user")
public interface TUserMapper {
    int deleteByPrimaryKey(TUser record);

    int insert(TUser record);

    int insertSelective(TUser record);

    TUser selectByPrimaryKey(TUser user);

    int updateByPrimaryKeySelective(TUser record);

    int updateByPrimaryKey(TUser record);
    
    List<TUser> selectAll();
    
    @GroupStrategy
    List<TUser> selectByUserName(TUser user);
    
    @GroupStrategy("status")
    List<TUser> selectByStatus(TUser user);
    
    List<TUser> selectAllWithPage(Pagination pag);
}