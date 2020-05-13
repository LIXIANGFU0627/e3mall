package cn.e3mall.search.message;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;

import cn.e3mall.common.pojo.SearchItem;
import cn.e3mall.search.mapper.ItemMapper;

public class ItemChangeListener implements MessageListener{
	/*
	 * 监听商品添加消息,接受消息后,将对应的商品信息同步到索引库
	*/
	@Autowired
	private ItemMapper itemMapper; 
	@Autowired
	private SolrServer solrServer;
	public void onMessage(Message message) {
		try {
			TextMessage textMessage = null;
			Long itemId = null;
			if(message instanceof TextMessage){
				textMessage = (TextMessage) message;
			itemId = Long.valueOf(textMessage.getText());
			}
			SearchItem searchItem = itemMapper.getItemById(itemId);
			SolrInputDocument document = new SolrInputDocument();
			document.addField("id", searchItem.getId());
			document.addField("item_title", searchItem.getTitle());
			document.addField("item_sell_point", searchItem.getSell_point());
			document.addField("item_price", searchItem.getPrice());
			document.addField("item_image", searchItem.getImage());
			document.addField("item_category_name", searchItem.getCategory_name());
			solrServer.add(document);
			solrServer.commit();
		} catch (Exception e) {
			
			e.printStackTrace();
		} 
	
		
	}

}
