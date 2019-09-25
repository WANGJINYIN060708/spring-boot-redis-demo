package com.wangjinyin.smf.impl.mapper;

import java.util.List;

/**
 * 
 * @author 汪进银
  * 通用Mapper层接口
 */
public interface IMapper<T> {
	
	/**
	 * 插入pojo
	 * @param pojo
	 */
	public void insert(T pojo) throws RuntimeException;
	
	/**
	 * 根据id更新pojo
	 * @param pojo
	 */
	public void updateByPrimaryKey(T pojo) throws RuntimeException;
	
	/**
	 * 根据Id查询pojo
	 * @param id
	 * @return
	 */
	public T selectByPrimaryKey(String id);
	
	/**
	 * 按条件查询pojo列表
	 * @param pojo
	 * @return
	 */
	public List<T> select(T pojo);
	
	/**
	 * 按照Id删除pojo
	 * @param id
	 */
	public void deleteByPrimaryKey(String id) throws RuntimeException;
	
	/**
	 * 按照id批量删除pojo
	 * @param ids
	 */
	public void deleteByPrimaryKeys(String[] ids) throws RuntimeException;

}
