package voice_test.service.imp;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import voice_test.dao.UserInfoMapper;
import voice_test.po.UserInfo;
import voice_test.po.UserInfoExample;
import voice_test.po.UserInfoExample.Criteria;
import voice_test.service.LoginInfoSVC;

@Service("loginService")
public class LoginInfoSVCImp implements LoginInfoSVC {
	
	@Resource
	UserInfoMapper userInfoMapper;

	@Override
	public boolean userLogin(String userName, String password) {
		
		UserInfoExample example = new UserInfoExample();
		Criteria criteria = example.createCriteria();
		criteria.andUserNameEqualTo(userName).andUserPasswordEqualTo(password);
		
		List<UserInfo> userInfos = userInfoMapper.selectByExample(example);
		
		if (userInfos.isEmpty()) {
			return false;
		}
		
		return true;
	}

}
