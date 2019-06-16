package cn.itcast.controller;

import cn.itcast.bean.Articles;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class HighLightSearch {
    public static void main(String[] args) {
        try {
            searchHighLight();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void searchHighLight() throws IOException {
        TransportClient client = Connection.getClient();
        //1 通过关键词进行查询
        SearchRequestBuilder builder = client.prepareSearch("sanguo").setTypes("dahan").setQuery(QueryBuilders.fuzzyQuery("address", "北京"));

        //2 对查询出来的关键词设置高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("address");
        highlightBuilder.preTags("<em>");//前置元素
        highlightBuilder.postTags("</em>");//后置元素

        //将高亮设置信息放入查询条件中
        builder.highlighter(highlightBuilder);
        SearchResponse searchResponse = builder.get();
        SearchHits hits = searchResponse.getHits();
        Iterator<SearchHit> iterator = hits.iterator();
        ObjectMapper objectMapper = new ObjectMapper();
        while (iterator.hasNext()) {
            SearchHit searchHitFields = iterator.next();
            //将高亮处理后的内容，替换原有的内容
            Map<String, HighlightField> highlightFields = searchHitFields.getHighlightFields();
            HighlightField content = highlightFields.get("address");

            //fragments就是高亮显示的片段
            Text[] fragments = content.fragments();
            StringBuffer sb = new StringBuffer();

            //拼接查询出来的高亮字段
            for (Text fragment : fragments) {
                sb.append(fragment);
            }
            //将查询结果转化为对象 因为我们要进行替换 所以要转化成对象 然后直接将高亮片段替换原有的对象内容
            Articles article = objectMapper.readValue(searchHitFields.getSourceAsString(), Articles.class);
            //开始进行高亮替换 但是要做一次性校验 因为可能查询不到据体的内容 那么就是没有高亮显示的 所以不要把空内容替换原有内容
            if (sb.toString() != null || !"".equals(sb.toString())) {
                //替换
                article.setAddress(sb.toString());
            }
            System.out.println(article.toString());
        }
    }
}
