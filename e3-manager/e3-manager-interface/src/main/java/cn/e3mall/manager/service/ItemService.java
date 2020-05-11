package cn.e3mall.manager.service;

import cn.e3mall.common.pojo.EasyUIdateGridResult;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.pojo.TbItem;

public interface ItemService {
	
	
	EasyUIdateGridResult getItemList(int page,int rows);
	E3Result addItem(TbItem item,String desc);
	E3Result deleteItemById(String params);

}
