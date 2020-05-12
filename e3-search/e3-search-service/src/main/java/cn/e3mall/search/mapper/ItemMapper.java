package cn.e3mall.search.mapper;

import java.util.List;

import cn.e3mall.common.pojo.SearchItem;
import cn.e3mall.pojo.TbItem;

public interface ItemMapper {

	List<SearchItem> getItemList();
	
	SearchItem getItemById(long itemId);
}
