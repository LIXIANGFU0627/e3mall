package cn.e3mall.content.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.content.service.ContentService;
import cn.e3mall.mapper.TbContentMapper;
import cn.e3mall.pojo.TbContent;
import cn.e3mall.pojo.TbContentExample;
import cn.e3mall.pojo.TbContentExample.Criteria;
@Service
public class ContentServiceImpl implements ContentService {
	@Autowired
	private TbContentMapper contentMapper;
	@Autowired
	private JedisClient jedisClient;
	@Value(value="${CONTENT_KEY}")
	private String CONTENT_KEY;
	@Override
	public E3Result saveContentCategory(TbContent tbContent) {
		tbContent.setCreated(new Date());
		tbContent.setUpdated(new Date());
		contentMapper.insert(tbContent);
		jedisClient.hdel(CONTENT_KEY, tbContent.getCategoryId().toString());
		return E3Result.ok();
	}
	@Override
	public List<TbContent> getContentList(long cid) {
		try {
			String json = jedisClient.hget(CONTENT_KEY, cid+"");
			if(StringUtils.isNotBlank(json)){
				List<TbContent> list = JsonUtils.jsonToList(json, TbContent.class);
				return list;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		TbContentExample example = new TbContentExample();
		Criteria criteria = example.createCriteria();
		criteria.andCategoryIdEqualTo(cid);
		List<TbContent> contentList = contentMapper.selectByExample(example);
		try {
			jedisClient.hset(CONTENT_KEY, cid+"", JsonUtils.objectToJson(contentList));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return contentList;
	}
}
