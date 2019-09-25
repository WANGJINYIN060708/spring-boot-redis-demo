package com.wangjinyin.smf.service.imp;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.wangjinyin.smf.exception.OperationException;
import com.wangjinyin.smf.mapper.SysUserMapper;
import com.wangjinyin.smf.model.SysUser;
import com.wangjinyin.smf.service.ISysUserService;
import com.wangjinyin.smf.vo.UserVo;


/**
 * @author 汪进银
 */
@Service   //@Service注解表示 该类为service层
public class SysUserService implements ISysUserService{
	
	//自动装配
	@Autowired
	private SysUserMapper sysUserMapper;
	
	@Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

	@Override
	public void saveUser(SysUser user) throws OperationException {
		
	}

	@Override
	public void saveUsers(List<SysUser> users) throws OperationException {
		
	}

	@Override
	public void updateUser(SysUser user) throws OperationException {
		
	}

	@Override
	public void enableUser(String userId) throws OperationException {
		
	}

	@Override
	public void enableUser(String[] userIds) throws OperationException {
		
	}

	@Override
	public void disableUser(String userId) throws OperationException {
		
	}

	@Override
	public void disableUser(String[] userIds) throws OperationException {
		
	}

	@Override
	public void updateUserPassword(String userId, String oldPassword, String newPassword) throws OperationException {
		
	}

	@Override
	public void resetUserPassword(String userId) throws OperationException {
		
	}

	@Override
	public void resetUserPassword(String[] userIds) throws OperationException {
		
	}

	@Override
	public SysUser getUserById(String userId) {
		return null;
	}

	@Override
	public SysUser getUserByUsername(String userName) {
		
		SysUser sysUsers = sysUserMapper.selectByUserName(userName);
		redisTemplate.opsForValue().set("k1", JSON.toJSONString(sysUsers), 1, TimeUnit.MINUTES);
		
		return sysUsers;
	}

	@Override
	public List<SysUser> getUserList(UserVo userVo) {
		
		System.out.println("123");
		List<SysUser> sysUsers = sysUserMapper.select(userVo);
		
		redisTemplate.opsForValue().set("user:list", JSON.toJSONString(sysUsers));
		
	    System.out.println(redisTemplate.opsForValue().get("user:list") + "测试连接");
		
        stringRedisTemplate.opsForValue().set("user:name", "张三");
		return sysUsers;
	}

//	@Override
//	public PageInfo<SysUser> getUserPage(UserVo userVo, int currentPage, int pageSize, String sortName,
//			String sortOrder) {
//		return null;
//	}

	@Override
	public List<String> getUserStringPermissions(String userId) {
		return null;
	}

	@Override
	public void deleteUserById(String userId) throws OperationException {
		
	}

	@Override
	public void deleteUserByIds(String[] userIds) throws OperationException {
		
	}
}
