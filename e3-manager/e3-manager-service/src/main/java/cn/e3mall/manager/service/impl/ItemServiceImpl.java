package cn.e3mall.manager.service.impl;

import java.util.Date;
import java.util.List;

import org.jboss.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.e3mall.common.pojo.EasyUIdateGridResult;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.IDUtils;
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
		//向商品表中插入数据,补全
		Date date = new Date();
		long itemId = IDUtils.genItemId();
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
	


}
