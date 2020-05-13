package cn.e3mall.manager.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.pojo.EasyUIdateGridResult;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.IDUtils;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.manager.service.ItemService;
import cn.e3mall.mapper.TbItemDescMapper;
import cn.e3mall.mapper.TbItemMapper;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;
import cn.e3mall.pojo.TbItemExample;
@Service
public class ItemServiceImpl implements ItemService {
	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbItemDescMapper itemDescMapper;
	@Autowired
	private JmsTemplate jmsTemplate;
	@Resource
	private Destination topicDestination;
	@Autowired
	private JedisClient jedisClient;
	
	@Value("${REDIS_ITEM_PRE}")
	private String REDIS_ITEM_PRE;
	@Value("${ITEM_CACHE_EXPIRE}")
	private Integer ITEM_CACHE_EXPIRE;
	@Override
	public EasyUIdateGridResult getItemList(int page, int rows) {
		PageHelper.startPage(page, rows);
		
		TbItemExample example = new TbItemExample();
		List<TbItem> list = itemMapper.selectByExample(example);
		
		PageInfo<TbItem> info = new PageInfo<>(list);
		EasyUIdateGridResult result = new EasyUIdateGridResult();
		long total = info.getTotal();
		result.setTotal(total);
		result.setRows(list);
		return result;
	}
	@Override
	public E3Result addItem(TbItem item, String desc) {
		try {
		//向商品表中插入数据,补全
		Date date = new Date();
		final long itemId = IDUtils.genItemId();
		
			Thread.sleep(1000);
		
		item.setId(itemId);
		item.setStatus((byte) 1);
		item.setUpdated(date);
		item.setCreated(date);
		itemMapper.insert(item);
		item.setImage("https://img.alicdn.com/imgextra/i1/263726286/O1CN01Ti2AIi1wJ2CtaMYLT_!!2-item_pic.png_430x430q90.jpg");
		TbItemDesc itemDesc =new TbItemDesc();
		itemDesc.setCreated(date);
		itemDesc.setUpdated(date);
		itemDesc.setItemId(itemId);
		itemDesc.setItemDesc(desc);
		itemDescMapper.insert(itemDesc);
		//发送商品添加消息消息
		jmsTemplate.send(topicDestination, new MessageCreator() {
			
			@Override
			public Message createMessage(Session session) throws JMSException {
				TextMessage message = session.createTextMessage(itemId+"");
				return message;
			}
		});
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return E3Result.ok();
	}
	@Override
	public E3Result deleteItemById(String ids) {
		String[] sid = ids.split(",");
		for (String sId : sid) {
			Long id = Long.valueOf(sId);
			itemMapper.deleteByPrimaryKey(id);
			itemDescMapper.deleteByPrimaryKey(id);
		}
		
		return E3Result.ok();
	}
	@Override
	public TbItemDesc getItemDescById(long itemId) {
	
		try {
			String itemJson = jedisClient.get(REDIS_ITEM_PRE +":"+ itemId+ ":DESC");
			if(StringUtils.isNotBlank(itemJson)){
				
				TbItemDesc itemDesc = JsonUtils.jsonToPojo(itemJson, TbItemDesc.class);
				
				return itemDesc;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		TbItemDesc tbItemDesc = itemDescMapper.selectByPrimaryKey(itemId);
		try {
			jedisClient.set(REDIS_ITEM_PRE +":"+ itemId+ ":DESC", JsonUtils.objectToJson(tbItemDesc));
			jedisClient.expire(REDIS_ITEM_PRE +":"+ itemId+ ":DESC", ITEM_CACHE_EXPIRE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tbItemDesc;
	}
	@Override
	public TbItem getItemById(long itemId) {
		try {
			String itemJson = jedisClient.get(REDIS_ITEM_PRE +":"+ itemId+ ":BASE");
			if(StringUtils.isNotBlank(itemJson)){
				
				TbItem item = JsonUtils.jsonToPojo(itemJson, TbItem.class);
				
				return item;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		TbItem item = itemMapper.selectByPrimaryKey(itemId);
		
		if(item!=null){
			try {
				jedisClient.set(REDIS_ITEM_PRE +":"+ itemId+ ":BASE", JsonUtils.objectToJson(item));
				jedisClient.expire(REDIS_ITEM_PRE +":"+ itemId+ ":BASE", ITEM_CACHE_EXPIRE);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return item;
		}
		return null;
	
	}
	

}
