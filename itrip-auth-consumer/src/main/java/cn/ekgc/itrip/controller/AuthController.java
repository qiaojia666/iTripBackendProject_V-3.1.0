package cn.ekgc.itrip.controller;

import cn.ekgc.itrip.base.controller.BaseController;
import cn.ekgc.itrip.pojo.entity.User;
import cn.ekgc.itrip.pojo.vo.Dto;
import cn.ekgc.itrip.transport.UserTransport;
import cn.ekgc.itrip.util.JWTUtil;
import cn.ekgc.itrip.util.MD5Util;
import cn.ekgc.itrip.util.UserUtil;
import com.auth0.jwt.JWTCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <b>认证子项目控制器</b>
 * @author Qiaojia
 * @version 3.1.0 2019-12-12
 * @since 3.1.0
 */
@RestController("authController")
@RequestMapping("/auth")
public class AuthController extends BaseController {
	@Autowired
	private UserTransport userTransport;

	/**
	 * <b>检查用户所填写的注册时所填写的邮箱/手机号码是否可用</b>
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/api/ckusr", method = RequestMethod.GET)
	public Dto<User> checkUserCodeForRegistry(String name) throws Exception {
		// 创建数据返回对象
		Dto<User> result = new Dto<User>();
		// 判断所提交的邮箱/手机号码是否符合格式要求
		if (UserUtil.checkUserCodePattern(name)) {
			// 在格式正确的情况下，进行唯一性校验
			User user = userTransport.getUserByUserCode(name);
			if (user == null) {
				// 此时该注册信息可用
				result.setSuccess("true");
				return result;
			}
		}
		// 如果格式正确，那么在校验是否唯一
		result.setSuccess("false");
		result.setMsg("该邮箱地址或手机号码格式不正确，或者已被占用");
		return result;
	}

	/**
	 * <b>使用邮箱注册新用户</b>
	 * @param user
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/api/doregister", method = RequestMethod.POST)
	public Dto<User> registryUserByEmail(@RequestBody User user) throws Exception {
		// 创建数据返回对象
		Dto<User> result = new Dto<User>();
		// 1、对于用户的登录密码进行MD5加密
		user.setUserPassword(MD5Util.encrypt(user.getUserPassword()));
		if (userTransport.saveUser(user)){
			result.setSuccess("true");
			result.setMsg("请在30分钟内登录您的邮箱，查看激活码");
		}else {
			result.setSuccess("false");
			result.setMsg("系统错误，请联系管理员赵文强：18149335891");
		}
		return result;
	}


	/**
	 * <b>使用邮箱激活码激活账户</b>
	 * @param user
	 * @param code
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/api/activate", method = RequestMethod.PUT)
	public Dto<User> activeUserCode(String user,String code) throws Exception {
		Dto<User> result = new Dto<User>();
		if (user != null && !"".equals(user.trim()) && code != null && !"".equals(code.trim())){
			if (userTransport.activeUserCode(user,code)){
				result.setSuccess("true");
				result.setMsg("激活成功！");
			}else {
				result.setSuccess("false");
				result.setMsg("激活失败！请输入正确的验证码！");
			}
		}else {
			result.setErrorCode("false");
			result.setMsg("请输入正确的邮箱和验证码！");
		}
		return result;
	}

	/**
	 * <b>根据用户的userCode和userPassword进行用户信息登录</b>
	 * @param name
	 * @param password
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/api/dologin", method = RequestMethod.POST)
	public Dto<User> doLoginUser(String name, String password) throws Exception{
		// 创建返回数据DTO对象
		Dto<User> result = new Dto<User>();
		// 校验用户所提供的用户名和密码正确有效
		if (name != null && !"".equals(name.trim()) && password != null && !"".equals(password.trim())){
			// 对于用户的密码进行MD5加密
			password = MD5Util.encrypt(password);
			// 使用登录名和密码进行登录
			User user = userTransport.doLoginUser(name, password);
			if (user != null){
				// 对于前后端的分离来说，登录成功之后，不在使用，或者是很少使用HTTPSession绑定用户信息
				// 对于用户的信息进行进一步Token
				String json = JWTUtil.createToken(user.getId());
				// 将对应的Token绑定到response的header部分
				response.setHeader("Authorization", json);
				result.setSuccess("true");
				result.setData(user);
				return result;
			}
		}
		result.setSuccess("false");
		result.setMsg("登陆失败，请确认登录信息后再次进行登录");
		return result;
	}
}
