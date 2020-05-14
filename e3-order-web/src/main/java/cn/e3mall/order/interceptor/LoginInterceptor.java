package cn.e3mall.order.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.common.utils.CookieUtils;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.sso.service.LoginService;

public class LoginInterceptor implements HandlerInterceptor {
	@Value("${SSO_URL}")
	private String SSO_URL;
	@Value("${TT_CART}")
	private String TT_CART;
	@Autowired
	private LoginService loginService;
	@Autowired
	private CartService cartService;
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		// 1、从cookie中取token
		String token = CookieUtils.getCookieValue(request, "token");
		// 2、如果没有取到，没有登录，跳转到sso系统的登录页面。拦截
		if(StringUtils.isBlank(token)){
			response.sendRedirect(SSO_URL+"/page/login?redirect="+request.getRequestURL());
			return false;
		}
		//跳转到登录页面
		// 3、如果取到token。判断登录是否过期，需要调用sso系统的服务，根据token取用户信息
		E3Result e3Result = loginService.getUserByToken(token);
		
		// 4、如果没有取到用户信息，登录已经过期，重新登录。跳转到登录页面。拦截
		if(e3Result.getStatus()!=200){
			response.sendRedirect(SSO_URL+"/page/login?redirect="+request.getRequestURL());
			return false;
		}
		// 5、如果取到用户信息，用户已经是登录状态，把用户信息保存到request中。放行
		TbUser user = (TbUser) e3Result.getData();
		request.setAttribute("user", user);
		// 6、判断cookie中是否有购物车信息，如果有合并购物车
		String json = CookieUtils.getCookieValue(request, "TT_CART",true);
		if(StringUtils.isNoneBlank(json)){
			cartService.mergeCart(user.getId(), JsonUtils.jsonToList(json, TbItem.class));
			//删除cookie中的购物车数据
			CookieUtils.setCookie(request, response, "TT_CART", "");
		}
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
