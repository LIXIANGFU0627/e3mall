package cn.e3mall.cart.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import cn.e3mall.common.utils.CookieUtils;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.sso.service.LoginService;

public class LonginInterceptor implements HandlerInterceptor {
	@Value("${COOKIE_TOKEN_KEY}")
	private String COOKIE_TOKEN_KEY;
	@Autowired
	private LoginService loginService;
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		//拦截器作用:从cookie中取用户信息,有就放到request域中
		String token = CookieUtils.getCookieValue(request, COOKIE_TOKEN_KEY);
		if(StringUtils.isBlank(token)){
			return true;
		}
		E3Result result = loginService.getUserByToken(token);
		if(result.getStatus()!=200){
			return true;
		}
		TbUser user = (TbUser) result.getData();
		request.setAttribute("user", user);
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {
	}


}
