# weibo-search-crawler
+ Maven管理的java工程，程序入口在WeiboCrawler文件中
+ 可实现未登录情况下的关键词检索爬虫，但最多只能爬取109页
+ 可选择不同条件检索，如：热门、实时和综合

# 实现介绍：
使用的是微博m的主页：`https://m.weibo.cn/search?containerid=231583`， 没有登录限制，也能检索。随便搜过一个关键词（在综合选项下搜索案例），f12从请求包中可以发现请求数据的api比较简单：
`https://m.weibo.cn/api/container/getIndex?containerid=100103type%3D1%26q%3D%E6%A1%88%E4%BE%8B&page_type=searchall`
进行url解码后：`https://m.weibo.cn/api/container/getIndex?containerid=100103type=1&q=案例&page_type=searchall`
再对比其他关键词和其他条件检索，发现type之后&page_type之前的内容被url编码了，也就是%3D1%26q%3D%E6%A1%88%E4%BE%8B = URLEncoder(1&q=案例)
