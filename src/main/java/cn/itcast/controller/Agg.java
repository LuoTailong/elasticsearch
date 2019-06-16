package cn.itcast.controller;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.max.InternalMax;
import org.elasticsearch.search.aggregations.metrics.max.MaxAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.sum.InternalSum;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;

import java.util.Iterator;
import java.util.Map;

public class Agg {
    public static void main(String[] args) {
//        aggOne();
//        multiGroup();
        groupMax();
//        orderGroup();
    }

    //1 计算每个球队的球员数量
    //select team, count(*) as count from table group by team;
    private static void aggOne() {
        TransportClient client = Connection.getClient();
        //1.1 指定查询条件
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch("player").setTypes("player");

        //1.2 指定聚合条件
        TermsAggregationBuilder team = AggregationBuilders.terms("playerCount").field("team");

        //1.3 将聚合条件封装到查询条件中
        searchRequestBuilder.addAggregation(team);
        SearchResponse searchResponse = searchRequestBuilder.get();

        //1.4 获取聚合内容
        Map<String, Aggregation> aggMap = searchResponse.getAggregations().asMap();
        StringTerms stringTerms = (StringTerms) aggMap.get("playerCount");

        //1.5 迭代聚合内容
        Iterator<StringTerms.Bucket> iterator = stringTerms.getBuckets().iterator();
        while (iterator.hasNext()) {
            StringTerms.Bucket data = iterator.next();
            System.out.println("球队：" + data.getKey() + "球员数量：" + data.getDocCount());
        }
    }

    //2 计算每个球队每个位置的球员数
    private static void multiGroup() {
        TransportClient client = Connection.getClient();
        //2.1 构建查询条件
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch("player").setTypes("player");

        //2.2 指定聚合条件
        TermsAggregationBuilder team = AggregationBuilders.terms("team").field("team");
        TermsAggregationBuilder position = AggregationBuilders.terms("position").field("position");

        //2.3 指定聚合的分层关系
        team.subAggregation(position);
        //2.4 将聚合条件指定到查询条件中
        searchRequestBuilder.addAggregation(team).addAggregation(position);

        //2.5 提交查询
        SearchResponse searchResponse = searchRequestBuilder.get();
        //2.6 获取父层的结果
        Map<String, Aggregation> aggMap = searchResponse.getAggregations().asMap();

        //2.7 获取父层中的数据
        StringTerms stringTerms = (StringTerms) aggMap.get("team");

        //2.8 获取子层
        Iterator<StringTerms.Bucket> iterator = stringTerms.getBuckets().iterator();
        while (iterator.hasNext()) {
            StringTerms.Bucket data = iterator.next();
            Map<String, Aggregation> subAgg = data.getAggregations().asMap();
            StringTerms aggStringTerm = (StringTerms) subAgg.get("position");
            Iterator<StringTerms.Bucket> subIterator = aggStringTerm.getBuckets().iterator();
            while (subIterator.hasNext()) {
                StringTerms.Bucket subData = subIterator.next();
                System.out.println("球队：" + data.getKey() + "每个位置；" + subData.getKey() + "球员数量：" + subData.getDocCount());
            }
        }
    }

    //3 求每个球队的球员年龄最值
    //select team, max(age) as maxAge from tables group by team
    private static void groupMax() {
        TransportClient client = Connection.getClient();
        //3.1 指定查询条件
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch("player").setTypes("player");

        //3.2 指定聚合条件
        TermsAggregationBuilder team = AggregationBuilders.terms("team").field("team");
        MaxAggregationBuilder maxAge = AggregationBuilders.max("maxAge").field("age");

        //3.3 分层关系
        team.subAggregation(maxAge);

        //3.4 将聚合条件指定到查询条件中
        searchRequestBuilder.addAggregation(maxAge).addAggregation(team);

        //3.5 提交查询
        SearchResponse searchResponse = searchRequestBuilder.get();
        //3.6 获取分层内容
        Map<String, Aggregation> teamMap = searchResponse.getAggregations().asMap();
        StringTerms teamTerms = (StringTerms) teamMap.get("team");
        Iterator<StringTerms.Bucket> iterator = teamTerms.getBuckets().iterator();
        //3.7 迭代父层
        while (iterator.hasNext()) {
            StringTerms.Bucket data = iterator.next();
            Map<String, Aggregation> subAgg = data.getAggregations().asMap();
            int age = (int) ((InternalMax) (subAgg.get("maxAge"))).getValue();
            System.out.println("每个球队：" + data.getKey() + "年龄的最大值：" + age);
        }
    }

    //4 计算每个球队的总年薪 并按照总年薪倒序排列
    //select team, sum(salary) as totalSalary from table group by team;
    private static void orderGroup() {
        TransportClient client = Connection.getClient();
        //4.1 指定查询条件
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch("player").setTypes("player");
        //4.2 聚合条件
        TermsAggregationBuilder team = AggregationBuilders.terms("team").field("team").order(Terms.Order.aggregation("salary", false));
        SumAggregationBuilder salary = AggregationBuilders.sum("salary").field("salary");

        //4.3 分层关系
        team.subAggregation(salary);
        //4.4 将聚合条件封装到查询条件中
        searchRequestBuilder.addAggregation(team).addAggregation(salary);
        //4.5 提交查询
        SearchResponse searchResponse = searchRequestBuilder.get();
        //4.6 获取父层内容
        Map<String, Aggregation> agg = searchResponse.getAggregations().asMap();
        StringTerms teamTerms = (StringTerms) agg.get("team");
        Iterator<StringTerms.Bucket> iterator = teamTerms.getBuckets().iterator();
        while (iterator.hasNext()) {
            StringTerms.Bucket data = iterator.next();
            Map<String, Aggregation> subAgg = data.getAggregations().asMap();
            int totalSalary = (int) ((InternalSum) subAgg.get("salary")).getValue();
            System.out.println("球队:" + data.getKey() + "总年薪:" + totalSalary);
        }
    }
}
