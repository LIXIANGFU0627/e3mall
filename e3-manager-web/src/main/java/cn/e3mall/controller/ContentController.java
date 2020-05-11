package cn.e3mall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.utils.E3Result;
import cn.e3mall.content.service.ContentService;
import cn.e3mall.pojo.TbContent;
@Controller
public class ContentController {
	@Autowired
	private ContentService contentService;
	@RequestMapping("/content/save")
	@ResponseBody
	public E3Result saveContent(TbContent tbContent){
		E3Result e3Result = contentService.saveContentCategory(tbContent);
		return e3Result;
	}
}
