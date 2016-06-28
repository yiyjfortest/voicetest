package bompom;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import voice_test.dao.UserInfoMapper;
import voice_test.service.LoginInfoSVC;

public class ConnectTest {
	
	public static final String SERVICE_PROVIDER_XML = "applicationContext.xml";
	
	ApplicationContext context = new ClassPathXmlApplicationContext(SERVICE_PROVIDER_XML);
	UserInfoMapper userInfoMapper = (UserInfoMapper) context.getBean("mapperFactoryBean");
	LoginInfoSVC logininfo = (LoginInfoSVC) context.getBean("loginService");
	
	@Test
	public void test() {
//		UserInfo userInfo = userInfoMapper.selectByPrimaryKey(1);
//		System.out.println(userInfo.getUserName().toString());
//		System.out.println(userInfo.getUserPassword().toString());
		boolean b = false;
		System.out.println(b+"");
	}
	
	@Test
	public void loginTest() {
		boolean b = logininfo.userLogin("yiyj","yiyj");
		System.out.println(b);
	}

}
