package com.easyooo.framework.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.easyooo.framework.demo.domain.TUser;
import com.easyooo.framework.demo.domain.TUserDetail;
import com.easyooo.framework.demo.mapper.TUserDetailMapper;
import com.easyooo.framework.demo.mapper.TUserMapper;

/**
 * 
 * @author Killer
 */
@Service
public class SimpleService {

	@Autowired
	private TUserMapper tUserMapper;
	@Autowired
	private TUserDetailMapper tUserDetailMapper;

	public void insertUser(Long userId) {

		List<TUser> users = tUserMapper.selectAll();
		System.out.println(users);

		TUserDetail detail = new TUserDetail();
		detail.setUserId(userId);
		detail.setSex("M");
		detail.setTel(10000);
		detail.setLinkman("ZS");
		tUserDetailMapper.insert(detail);

		TUser user = new TUser(userId);
		user.setStatus("OK");
		user.setUserName("killer");
		tUserMapper.insert(user);
	}

	public boolean deleteUser(Long userId) {
		
		int row1 = tUserMapper.deleteByPrimaryKey(new TUser(userId));
		int row2 = tUserDetailMapper.deleteByPrimaryKey(new TUserDetail(userId));
		
		if(row1 != row2){
			return false;
		}
		return true;
	}

}
