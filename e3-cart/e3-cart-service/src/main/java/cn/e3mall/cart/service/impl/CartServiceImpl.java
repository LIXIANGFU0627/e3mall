package cn.e3mall.cart.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.json.JSON;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.mapper.TbItemMapper;
import cn.e3mall.pojo.TbItem;
@Service
public class CartServiceImpl implements CartService {
	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private JedisClient jedisClient;
	
	@Override
	public E3Result addCart(long userId, long itemId, int num) {
		Boolean flag = jedisClient.hexists("CART_KEY"+userId, itemId+"");
		if(flag){
			String json = jedisClient.hget("CART_KEY"+userId, itemId+"");
			TbItem item = JsonUtils.jsonToPojo(json, TbItem.class);
			item.setNum(num+item.getNum());
			jedisClient.hset("CART_KEY"+userId, itemId+"", JsonUtils.objectToJson(item));
			return E3Result.ok();
		}
		TbItem item = itemMapper.selectByPrimaryKey(itemId);
		item.setNum(num);
		String image = item.getImage();
		if(StringUtils.isNotBlank(image)){
			item.setImage(image.split(",")[0]);
		}
		jedisClient.hset("CART_KEY"+userId, itemId+"", JsonUtils.objectToJson(item));
		return E3Result.ok();
	}

	@Override
	public E3Result mergeCart(long userId, List<TbItem> itemList) {
		for (TbItem tbItem : itemList) {
			addCart(userId, tbItem.getId(), tbItem.getNum());
		}
		
		return E3Result.ok();
	}

	@Override
	public List<TbItem> getCartList(Long userId) {
		List<String> strList = jedisClient.hvals("CART_KEY"+userId);
		List<TbItem> itemList =  new ArrayList<>();
		for (String string : strList) {
			TbItem item = JsonUtils.jsonToPojo(string, TbItem.class);
			itemList.add(item);
		}
		return itemList;
	}

	@Override
	public E3Result updateCartItemNum(Long userId, Long itemId, Integer num) {
		String json = jedisClient.hget("CART_KEY"+userId, itemId+"");
		TbItem item = JsonUtils.jsonToPojo(json, TbItem.class);
		item.setNum(num);
		jedisClient.hset("CART_KEY"+userId, itemId+"", JsonUtils.objectToJson(item));
		
		return E3Result.ok();
	}

	@Override
	public E3Result deleteCartItemById(Long userId, Long itemId) {
		jedisClient.hdel("CART_KEY"+userId, itemId+"");
		return E3Result.ok();
	}

	@Override
	public E3Result clearCartByUserId(Long userId) {
		jedisClient.del("CART_KEY"+userId);
		return E3Result.ok();
	}
	
}
