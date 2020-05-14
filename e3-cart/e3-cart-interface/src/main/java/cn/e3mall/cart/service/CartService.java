package cn.e3mall.cart.service;

import java.util.List;

import cn.e3mall.common.utils.E3Result;
import cn.e3mall.pojo.TbItem;

public interface CartService {
	E3Result addCart(long userId,long itemId,int num);
	E3Result mergeCart(long userId,List<TbItem> itemList);
	List<TbItem> getCartList(Long userId);
	E3Result updateCartItemNum(Long userId, Long itemId, Integer num);
	E3Result deleteCartItemById(Long userId, Long itemId);
	E3Result clearCartByUserId(Long id);
}
