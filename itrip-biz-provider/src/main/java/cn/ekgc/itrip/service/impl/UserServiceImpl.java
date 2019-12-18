package cn.ekgc.itrip.service.impl;

import cn.ekgc.itrip.dao.UserDao;
import cn.ekgc.itrip.pojo.entity.User;
import cn.ekgc.itrip.service.UserService;
import cn.ekgc.itrip.util.ConstantUtil;
import cn.ekgc.itrip.util.SystemCodeUtil;
import com.netflix.eureka.cluster.PeerEurekaNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <b>用户信息业务层接口实现类</b>
 * @author Qiaojia
 * @version 3.1.1 2019-12-12
 * @since 3.1.1
 */
@Service("userService")
@Transactional
public class UserServiceImpl implements UserService {
	@Autowired
	private UserDao userDao;
	@Autowired
	private StringRedisTemplate redisTemplate;
	@Autowired(required = false)
	private JavaMailSender MailSender;

	/**
	 * <b>通过userCode查询User对象</b>
	 * @param userCode
	 * @return
	 * @throws Exception
	 */
	public User getUserByUserCode(String userCode) throws Exception {
		// 封装查询参数
		Map<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put("userCode", userCode);

		// 进行查询
		List<User> userList = userDao.findUserByQuery(queryMap);

		// 对于得到的结果进行判断
		if (userList != null && userList.size() > 0) {
			return userList.get(0);
		}
		return null;
	}

	/**
	 * <b>保存用户信息，保存成功后，根据用户的userCode类型进行相关验证码的发送工作</b>
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public boolean saveUser(User user) throws Exception {
		try {
			//将数据保存到数据库
			userDao.saveUser(user);
			//产生激活码
			String activeCode = SystemCodeUtil.createActiveCode();
			//将激活码保存到redis中
			redisTemplate.opsForValue().set(user.getUserCode(), activeCode);
			//对于该存入redis的key设置过期时间
			redisTemplate.expire(user.getUserCode(), ConstantUtil.ACTIVE_CODE_TIMEOUT*60, TimeUnit.SECONDS);
			//发送邮件到账户邮箱
			SimpleMailMessage mailMessage = new SimpleMailMessage();
			mailMessage.setTo(user.getUserCode());
			mailMessage.setFrom(ConstantUtil.MAIL_FORM);
			mailMessage.setSubject("爱旅行爱生活账户激活码");
			mailMessage.setText("您的激活码是：" + activeCode + "请在" + ConstantUtil.ACTIVE_CODE_TIMEOUT + "分钟内登录系统，输入本验证码激活您的账户！");
			MailSender.send(mailMessage);
			return true;
		}catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * <b>使用验证码激活账号</b>
	 * @param user
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public boolean activeUserCode(String user, String code) throws Exception {
		String activeCode = redisTemplate.opsForValue().get(user);
		if (code.equals(activeCode)){
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("userCode", user);
			try {
				userDao.activeUserCode(paramMap);
				return true;
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * <b>使用userCode和userPassword进行用户信息登录</b>
	 * @param userCode
	 * @param userPassword
	 * @return
	 * @throws Exception
	 */
	public User doLoginUser(String userCode, String userPassword) throws Exception {
		// 绑定查询Map集合
		Map<String,Object> queryMap = new HashMap<String,Object>();
		queryMap.put("userCode",userCode);
		queryMap.put("userPassword",userPassword);
		// 用户处于激活状态
		queryMap.put("activated",1);
		List<User> userList = userDao.findUserByQuery(queryMap);
		if (userList != null && userList.size() > 0){
			return userList.get(0);
		}
		return null;
	}
}
