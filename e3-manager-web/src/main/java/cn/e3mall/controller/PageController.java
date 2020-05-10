package cn.e3mall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.pojo.EasyUIdateGridResult;
import cn.e3mall.manager.service.ItemService;

@Controller
public class PageController {
	@Autowired
	private ItemService itemService;
	@RequestMapping("/")
	public String showIndex(){
		return"index";
	}
	
	@RequestMapping("/{page}")
	public String showPage(@PathVariable String page){
		
		return page;
	}
	@ResponseBody
	@RequestMapping("/item/list")
	public EasyUIdateGridResult getItemList(Integer page,Integer rows){
		EasyUIdateGridResult itemList = itemService.getItemList(page, rows);
		return itemList;
	}
	
}
