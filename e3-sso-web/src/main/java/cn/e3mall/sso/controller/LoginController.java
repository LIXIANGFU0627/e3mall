package cn.e3mall.sso.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.utils.CookieUtils;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.sso.service.LoginService;

@Controller
public class LoginController {
	@Autowired
	private LoginService loginService;
	@Value("${TOKEN_KEY}")
	private String TOKEN_KEY;
	@RequestMapping("/page/login")
	public String showLogin(){
		return "login";
	}
	@ResponseBody
	@RequestMapping("/user/login")
	public E3Result login(String username,String password,
			HttpServletRequest request,HttpServletResponse response){
		E3Result result = loginService.login(username, password);
		if(result.getStatus()==200){
			
			String token = result.getData().toString();
			CookieUtils.setCookie(request, response, TOKEN_KEY, token);
		}
	return result;
	}
	
}
