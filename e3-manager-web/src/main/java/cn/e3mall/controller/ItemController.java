package cn.e3mall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.utils.E3Result;
import cn.e3mall.manager.service.ItemService;
import cn.e3mall.pojo.TbItem;

@Controller
public class ItemController {
	@Autowired
	private ItemService itemService;
	

	@ResponseBody
	@RequestMapping("/item/save" )
	public E3Result addItem(TbItem item,String desc){
		
		E3Result result = itemService.addItem(item, desc);
		return result;
	}
	@ResponseBody
	@RequestMapping("/rest/item/delete")
	public E3Result deleteItemById(String ids){
		
		E3Result result = itemService.deleteItemById(ids);
		return result;
	}

	

}
