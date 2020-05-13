package cn.e3mall.cart.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.common.utils.CookieUtils;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.manager.service.ItemService;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbUser;

@Controller
public class CartController {
	@Autowired
	private ItemService itemService;
	@Autowired
	private CartService cartService;
	@Value("${TT_CART}")
	private String TT_CART;
	@Value("${CART_EXPIRE}")
	private Integer CART_EXPIRE; 
	@RequestMapping("/cart/add/{itemId}")
	public String addCart(@PathVariable Long itemId,Integer num,
			HttpServletRequest request,HttpServletResponse response){
		TbUser user = (TbUser) request.getAttribute("user");
		if(user!=null){
			Long userId = user.getId();
			cartService.addCart(userId, itemId, num);
			return "cartSuccess";
		}
		List<TbItem> cartList = getCartListByCookie(request);
		boolean hasItem =false;
		for (TbItem tbItem : cartList) {
			if(itemId.longValue()==tbItem.getId()){
				tbItem.setNum(num+tbItem.getNum());
				hasItem=true;
				break;
			}
		}
		if(!hasItem){
			TbItem item = itemService.getItemById(itemId);
			item.setNum(num);
			String image = item.getImage();
			if(StringUtils.isNoneBlank(image)){
				String[] images = image.split(",");
				item.setImage(images[0]);
			}
			cartList.add(item);
		}
		CookieUtils.setCookie(request, response, TT_CART, JsonUtils.objectToJson(cartList), CART_EXPIRE,true);
		return "cartSuccess";
	}
	
	public List<TbItem> getCartListByCookie(HttpServletRequest request){
		String json = CookieUtils.getCookieValue(request, TT_CART, true);
		if(StringUtils.isNotBlank(json)){
			List<TbItem> itemList = JsonUtils.jsonToList(json, TbItem.class);
			return itemList;
		}
		return new ArrayList<>();
		
	}
	@RequestMapping("/cart/cart")
	public String showCartList(HttpServletRequest request,HttpServletResponse response,Model model){
		
		List<TbItem> cartList = getCartListByCookie(request);
		//登录状态判断
		TbUser user = (TbUser) request.getAttribute("user");
		if(user!=null){
			if(cartList!=null){
				cartService.mergeCart(user.getId(), cartList);
				//合并后保存到redis中,并删除cookie,
				CookieUtils.setCookie(request, response, TT_CART, "");
			}
			List<TbItem> list = cartService.getCartList(user.getId());
			request.setAttribute("cartList", list);
			return "cart";
		}
		//未登录状态
		model.addAttribute("cartList", cartList);
		return "cart";
	}
	@ResponseBody
	@RequestMapping("/cart/update/num/{itemId}/{num}")
	public E3Result updataNum(@PathVariable Long itemId,@PathVariable Integer num,
			HttpServletRequest request,HttpServletResponse response){
		TbUser user = (TbUser) request.getAttribute("user");
		if(user!=null){
			cartService.updateCartItemNum(user.getId(),itemId,num);
			return E3Result.ok();
		}
		List<TbItem> cartList = getCartListByCookie(request);
		for (TbItem tbItem : cartList) {
			if(itemId.longValue()==tbItem.getId()){
				tbItem.setNum(num);
				break;
			}
		}
		CookieUtils.setCookie(request, response, TT_CART, JsonUtils.objectToJson(cartList),CART_EXPIRE, true);
		return E3Result.ok();
	}
	@RequestMapping("/cart/delete/{itemId}")
	private String deleteCartItem(@PathVariable Long itemId,
			HttpServletRequest request,HttpServletResponse response){
		TbUser user = (TbUser) request.getAttribute("user");
		if(user!=null){
			cartService.deleteCartItemById(user.getId(),itemId);
			return"redirect:/cart/cart.html";
		}
		List<TbItem> list = getCartListByCookie(request);
		for (TbItem tbItem : list) {
			if(itemId.longValue()==tbItem.getId()){
				list.remove(tbItem);
				break;
			}
		}
		CookieUtils.setCookie(request, response, TT_CART, JsonUtils.objectToJson(list),CART_EXPIRE, true);
		return"redirect:/cart/cart.html";
	}
}
