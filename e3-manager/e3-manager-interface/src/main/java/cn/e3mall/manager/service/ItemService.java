package cn.e3mall.manager.service;

import cn.e3mall.common.pojo.EasyUIdateGridResult;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;

public interface ItemService {
	
	TbItem getItemById(long itemId);
	EasyUIdateGridResult getItemList(int page,int rows);
	E3Result addItem(TbItem item,String desc);
	E3Result deleteItemById(String params);
	TbItemDesc getItemDescById(long itemId);
}
