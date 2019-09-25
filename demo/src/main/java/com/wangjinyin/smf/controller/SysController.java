package com.wangjinyin.smf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wangjinyin.smf.model.SysUser;
import com.wangjinyin.smf.service.ISysUserService;
import com.wangjinyin.smf.vo.UserVo;

@RestController
public class SysController {
	
	
	
	@Autowired
	private ISysUserService sysUserService;
	
	@RequestMapping("/testMybatis")
    public String  testMybatis(UserVo userVo) {
    			
		SysUser sysUser = sysUserService.getUserByUsername("zhangsan");
		
		return "login";
    }
}
