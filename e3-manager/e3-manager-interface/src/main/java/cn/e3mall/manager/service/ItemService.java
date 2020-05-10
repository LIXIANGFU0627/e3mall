package cn.e3mall.manager.service;

import cn.e3mall.common.pojo.EasyUIdateGridResult;
import cn.e3mall.pojo.TbItem;

public interface ItemService {
	
	TbItem getItemById(long itemId);
	EasyUIdateGridResult getItemList(int page,int rows);

}
