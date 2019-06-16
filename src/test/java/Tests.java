import cn.itcast.controller.Connection;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;

import java.io.IOException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * Created by angel
 */
public class Tests {
    public static void main(String[] args) throws Exception {
        createDocument100();
    }
    public static void createDocument100() throws Exception {
        TransportClient client = Connection.getClient();
        for(int i=1;i<=100;i++){
            XContentBuilder builder = null;
            try {
                builder = jsonBuilder()
                        .startObject()
                        .field("title", "第"+i+"本书")
                        .field("author", "作者"+i)
                        .field("id" , i)
                        .field("message", i+"是英国物理学家斯蒂芬·霍金创作的科学著作，首次出版于1988年。全书共十二章，讲的全都是关于宇宙本性的最前沿知识，包括：我们的宇宙图像、空间和时间、膨胀的宇宙、不确定性原理、黑洞、宇宙的起源和命运等内容，深入浅出地介绍了遥远星系、黑洞、粒子、反物质等知识")
                        .endObject();
                String json = builder.string();
                IndexResponse response = client.prepareIndex("blog2", "article")
                        .setSource(json, XContentType.JSON)
                        .get();
                // 索引名称
                String _index = response.getIndex();
                // 类型
                String _type = response.getType();
                // 文档ID
                String _id = response.getId();
                // 版本
                long _version = response.getVersion();
                // 返回的操作状态
                RestStatus status = response.status();
                System.out.println("索引名称:"+_index+" "+"类型 :" +  _type + " 文档ID："+_id+" 版本 ："+_version+" 返回的操作状态："+status);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
