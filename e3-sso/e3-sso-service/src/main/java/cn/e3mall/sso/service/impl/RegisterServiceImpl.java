package cn.e3mall.sso.service.impl;



import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import cn.e3mall.common.utils.E3Result;
import cn.e3mall.mapper.TbUserMapper;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.pojo.TbUserExample;
import cn.e3mall.pojo.TbUserExample.Criteria;
import cn.e3mall.sso.service.RegisterService;
@Service
public class RegisterServiceImpl implements RegisterService {
	@Autowired
	private TbUserMapper userMapper;
	@Override
	public E3Result checkData(String param, int type) {
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		//type 1：用户名 2：手机号 3：邮箱
		if(type==1){
			criteria.andUsernameEqualTo(param);
			
		}else if( type == 2 ){
			criteria.andPhoneEqualTo(param);
			
		}else if(type == 3){
			criteria.andEmailEqualTo(param);
		}else{
			E3Result.build(500, "数据类型错误");
		}
		List<TbUser> user = userMapper.selectByExample(example);
		if(user !=null && user.size()>0){
			return E3Result.ok(false);
		}
		
		return E3Result.ok(true);
	}
	@Override
	public E3Result register(TbUser tbUser) {
		if(StringUtils.isBlank(tbUser.getUsername())||StringUtils.isBlank(tbUser.getPassword())||
				StringUtils.isBlank(tbUser.getPhone())){
			return E3Result.build(400, "用户信息不全!");
		}
		E3Result result = checkData(tbUser.getUsername(), 1);
		if(!(boolean)result.getData()){
			return E3Result.build(400, "此用户名已经被使用!");
		}
		result = checkData(tbUser.getPhone(), 2);
		if (!(boolean)result.getData()) {
			return E3Result.build(400, "手机号已经被占用");
		}
		//补全pojo的属性
		tbUser.setCreated(new Date());
		tbUser.setUpdated(new Date());
		//对密码进行md5加密
		String md5Pass = DigestUtils.md5DigestAsHex(tbUser.getPassword().getBytes());
		tbUser.setPassword(md5Pass);
		userMapper.insert(tbUser);
		
		return E3Result.ok();
	}


}
