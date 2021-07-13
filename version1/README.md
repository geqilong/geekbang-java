# geekbang-java stage 1 submit logs

v1.2 第2周作业  
1.在 my-configuration 基础上，实现 ServletRequest 请求参数的 ConfigSource（MicroProfile Config），提供参考：Apache Commons Configuration中的org.apache.commons.configuration.web.ServletRequestConfiguration。
答：在模块my-configuration中类org.geektimes.configuration.microprofile.config.source.servlet.ServletRequestConfigSource里实现。

v1.0 第1周作业  
1.参考com.salesmanager.shop.tags.CommonResponseHeadersTag 实现一个自定义的 Tag，将 Hard Code 的 Header 名值对，变为属性配置的方式
答：在com.salesmanager.shop.tags.CustomResponseHeadersTag中实现了，在sm-shop模块的src/main/webapp/WEB-INF/shopizer-tags.tld中增加对应tag。

