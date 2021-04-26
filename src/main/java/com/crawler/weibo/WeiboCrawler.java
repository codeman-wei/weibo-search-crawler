package com.crawler.weibo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.crawler.weibo.utils.DBConnection;
import com.crawler.weibo.utils.GetMethod;
import com.crawler.weibo.utils.ProcessHTMLTag;
import com.crawler.weibo.utils.URLCoder;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class WeiboCrawler {
    static Map<String, Integer> typeMap = new HashMap<String, Integer> (){{
        put("实时", 61);
        put("热门", 60);
        put("综合", 1);
    }};
    public String baseURL = "https://m.weibo.cn/api/container/getIndex?";
    public String keyword;
    public final int TOTALPAGES = 4;
    Connection connection;

    public static void main(String[] args) {
        Connection connection = DBConnection.getConn();
        WeiboCrawler weiboCrawler = new WeiboCrawler();
//        String[] words = new String[] {"蹲成绩", "蹲答案", "蹲直播", "蹲网址", "蹲闲鱼"};
        String[] words = new String[] {"蹲"};
        for (String word: words) {
            weiboCrawler.keyword = word;
            weiboCrawler.setConnection(connection);
            weiboCrawler.getWeiBoPage("热门");
        }
    }


    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void getWeiBoPage(String type) {
        int totalPages = 50;
        String keywordEncode = URLCoder.URLEncoderUTF("="+ typeMap.get(type) +"&q=" + this.keyword);

        for(int i = 1; i <= totalPages; ++i) {
            String params = "containerid=100103type" + keywordEncode + "&page_type=searchall";
            if (i != 1) {
                params = params + "&page=" + i;
            }

            String url = this.baseURL + params;
            System.out.println(url);
            Map<String, String> header = new LinkedHashMap();
            header.put(":authority", "m.weibo.cn");
            header.put(":method", "GET");
            header.put(":path", "/api/container/getIndex?"+params);
            header.put(":scheme", "https");
            header.put("accept", "application/json, text/plain, */*");
            header.put("accept-encoding", "gzip, deflate, br");
            header.put("accept-language", "m.weibo.cn");
            header.put("mweibo-pwa", "1");
            header.put("referer", url);
            header.put("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.125 Safari/537.36");
            header.put("x-requested-with", "XMLHttpRequest");
            header.put("x-xsrf-token", "9f4dd8");
            String result = GetMethod.httpGetMethod(url, header, "UTF-8");
            int numPage = 0;
            try {
                numPage = phasePage(result);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.print("跳过关键词[" + keyword + "]的第" + i + "页爬取");
                continue;
            }
            if(numPage == -1){
                System.out.println("无最新相关数据");
                totalPages = 0;
            }else {
                if(TOTALPAGES>numPage){
                    totalPages = numPage;
                }
            }
            try {
                Thread.sleep(24000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
//
//		System.out.println(result);
    }
    public int phasePage(String result){
//		Connection connection = DBConnection.getConn();
        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONObject cardlistInfo = jsonObject.getJSONObject("data").getJSONObject("cardlistInfo");
        int totalElements = cardlistInfo.getIntValue("total");
        if(totalElements == 0){
            return -1;
        }

        JSONArray cards = jsonObject.getJSONObject("data").getJSONArray("cards");
//		System.out.println(cards.size());
        for(int i =0; i<cards.size(); i++){
            String content = "";
            String time = "";
            String textWithUrl = "";
            String contentUrl = "";
            String timeEdit = "";
            String id = "";
            JSONObject card = cards.getJSONObject(i);
//			System.out.println(card.getString("card_type"));
            /** card_type为9时为单节点数据 */
            if(card.getInteger("card_type") == 9){
                JSONObject mblog = card.getJSONObject("mblog");
                textWithUrl = mblog.getString("text");

                /** 有效采集到信息则写入数据库 */
                if(textWithUrl != null){
                    content = ProcessHTMLTag.delHTMLTag(textWithUrl);
                    contentUrl = ProcessHTMLTag.getContentUrl(textWithUrl);
                    content = content+contentUrl;
                    timeEdit = mblog.getString("edit_at");
                    if(timeEdit == null){
                        timeEdit = timePhase();
                    }
                    id = mblog.getString("id");
                    String sql = "insert into weibo_copy (title, content, time, url, source, keyword, createtime) value ('"
                            +content+"','"+content+"','"+timeEdit+"','"+id+"','微博搜索','"+keyword+"',Now()) on duplicate key update "
                            + "content='"+content+"',time='"+timeEdit+"'";
                    System.out.println(sql);
					DBConnection.queryDML(sql, connection, content);
                }
            }

            /** card_type为11时为多节点数据 */
            else if(card.getInteger("card_type") == 11){
                JSONArray cardArray = card.getJSONArray("card_group");
                if(cardArray != null){
                    for(int j=0; j<cardArray.size(); j++){
                        JSONObject cardArrayObject = cardArray.getJSONObject(j);
                        if(cardArrayObject.getInteger("card_type")== 9){
                            JSONObject mblogArray = cardArrayObject.getJSONObject("mblog");
                            textWithUrl = mblogArray.getString("text");

                            /** 有效采集到信息则写入数据库 */
                            if(textWithUrl != null){
                                content = ProcessHTMLTag.delHTMLTag(textWithUrl);
                                contentUrl = ProcessHTMLTag.getContentUrl(textWithUrl);
                                content = content+contentUrl;
                                timeEdit = mblogArray.getString("edit_at");
                                if(timeEdit == null){
                                    timeEdit = timePhase();
                                }
                                id = mblogArray.getString("id");
                                String sql = "insert into weibo (title, content, time, url, source, keyword, createtime) value ('"
                                        +content+"','"+content+"','"+timeEdit+"','"+id+"','微博搜索','"+keyword+"',Now() on duplicate key update "
                                        + "content='"+content+"',time='"+timeEdit+"'";
//								System.out.println(sql);
//								DBConnection.queryDML(sql, connection, content);
                            }
                        }
                    }
                }

            }
        }
        int numPages = getNumPage(totalElements);
        return numPages;
    }

    /** 时间处理，尝试了很久，可能只能从createtime进行转换处理，暂未实现 */
    public String timePhase(){
//		System.out.println(timeJson);
//		if(timeItem == "null"){
//			return "";
//		}
//		String regTime="qtime=[.[^\"]]*&"; //定义HTML标签的正则表达式
//		Pattern pTime=Pattern.compile(regTime);
//
//		Matcher mTime=pTime.matcher(timeItem);
//		String urlList = "";
//		if(mTime.find()){
//			System.out.println( mTime.group());	//匹配到的就输出
//			}
//		String timeStamp = timeObject.getJSONArray("action").getJSONObject(0).getString("time");
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		String time = sdf.format(new Date(Integer.parseInt(timeStamp)*1000L));
//		return time;
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String time = formatter.format(date);
        return time;
    }
    public int getNumPage(int totalElements){
//		System.out.println(numElements);
        int numPages = (int)Math.ceil((double)totalElements/10.0);
        return numPages;
    }
}
