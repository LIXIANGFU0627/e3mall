package cn.e3mall.testsolr;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Test;

public class TestSolrQuery {
	@Test
	public void fun1() throws SolrServerException{
		SolrServer solrServer = new HttpSolrServer("http://192.168.119.128:8090/solr");
		SolrQuery query = new SolrQuery();
		query.setQuery("*:*");
		QueryResponse response = solrServer.query(query);
		SolrDocumentList results = response.getResults();
		results.getNumFound();
		for (SolrDocument solrDocument : results) {
			System.out.println(solrDocument.get("id"));
			System.out.println(solrDocument.get("item_title"));
			System.out.println(solrDocument.get("item_price"));
			
		}
	}

}
