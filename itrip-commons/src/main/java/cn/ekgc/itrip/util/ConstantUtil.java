package cn.ekgc.itrip.util;


import java.util.Properties;

/**
 * <b>系统常量工具类</b>
 * @author Qiaojia
 * @version 3.1.1 2019-12-13
 * @since 3.1.1
 */
public class ConstantUtil {
	private static final Properties props = new Properties();
	static {
		try {
			props.load(ConstantUtil.class.getClassLoader().getResourceAsStream("itrip.properties"));
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public static final Long ACTIVE_CODE_TIMEOUT = Long.parseLong(props.getProperty("active.code.timeout"));

	public static final String MAIL_FORM = props.getProperty("mail.from");

	public static final String AUTH_SECRET = props.getProperty("auth.secret");
}
