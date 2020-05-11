package cn.e3mall.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.pojo.EasyUITreeNode;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.content.service.ContentCategoryService;

@Controller
public class ContentCategoryController {
	@Autowired
	private ContentCategoryService contentCategoruService;
	@RequestMapping("/content/category/list")
	@ResponseBody
	public List<EasyUITreeNode> getContenCatogoryList(
			@RequestParam(name="id",defaultValue="0") Long parentId){
		List<EasyUITreeNode> categoryList = contentCategoruService.getContentCategoryList(parentId);
		return categoryList;
	}
	@RequestMapping("/content/category/create")
	@ResponseBody
	public E3Result addContentCategory(Long parentId,String name){
		E3Result e3Result = contentCategoruService.addContentCategory(parentId, name);
		return e3Result;
	}		
	@RequestMapping("/content/category/update")
	@ResponseBody
	public E3Result updateContentCategory(Long parentId,String name){
		E3Result e3Result = contentCategoruService.updateContentCategory(parentId, name);
		return e3Result;
	}		
		
}
	
	
