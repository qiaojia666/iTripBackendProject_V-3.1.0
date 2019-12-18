package cn.ekgc.itrip.service;

import cn.ekgc.itrip.pojo.entity.User;
import cn.ekgc.itrip.pojo.entity.User;

/**
 * <b>用户信息业务层接口</b>
 * @author Qiaojia
 * @version 3.1.1 2019-12-12
 * @since 3.1.1
 */
public interface UserService {
	/**
	 * <b>通过userCode查询User对象</b>
	 * @param userCode
	 * @return
	 * @throws Exception
	 */
	User getUserByUserCode(String userCode) throws Exception;


	/**
	 * <b>保存用户信息</b>
	 * @param user
	 * @return
	 * @throws Exception
	 */
	boolean saveUser(User user) throws Exception;


	/**
	 * <b>使用验证码激活账号</b>
	 * @param user
	 * @param code
	 * @return
	 * @throws Exception
	 */
	boolean activeUserCode(String user, String code) throws Exception;

	/**
	 * <b>使用userCode和userPassword进行用户信息登录</b>
	 * @param userCode
	 * @param userPassword
	 * @return
	 * @throws Exception
	 */
	User doLoginUser(String userCode, String userPassword) throws Exception;
}
