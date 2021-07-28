# geekbang-java stage 1 submit logs
v1.6 第4周作业
1.将 my-interceptor工程代码增加JDK动态代理，将@BulkHead 等注解标注在接口上，实现方法拦截。  
  步骤：  
     1. 通过 JDK 动态代理实现类似 InterceptorEnhancer 的代码  
     2. 实现 JDK 动态代理方法的 InvocationContext  
  答：主要通过my-interceptor模块中org.geektimes.interceptor.jdk.DynamicProxyEnhancer、  
     org.geektimes.interceptor.DynoxyMethodInvocationContext来实现，测试类org.geektimes.interceptor.jdk.DynamicProxyEnhancerTest。

v1.4 第3周作业  
1.通过 MicroProfile REST Client 实现 POST 接口去请求项目中的 ShutdownEndpoint，URI：http://127.0.0.1:8080/actuator/shutdown  
  可选：完善 my-rest-client 框架 POST方法，实现org.geektimes.rest.client.DefaultInvocationBuilder#buildPost方法  
 答：在模块sm-shop的test中，com.salesmanager.test.shop.rest.service.MicroProfilePostRestClientTest里实现，org.geektimes.rest.client.DefaultInvocationBuilder#buildPost也已实现。  

v1.2 第2周作业  
1.在 my-configuration 基础上，实现 ServletRequest 请求参数的 ConfigSource（MicroProfile Config），提供参考：Apache Commons Configuration中的org.apache.commons.configuration.web.ServletRequestConfiguration。  
答：在模块my-configuration中类org.geektimes.configuration.microprofile.config.source.servlet.ServletRequestConfigSource里实现。

v1.0 第1周作业  
1.参考com.salesmanager.shop.tags.CommonResponseHeadersTag 实现一个自定义的 Tag，将 Hard Code 的 Header 名值对，变为属性配置的方式
答：在com.salesmanager.shop.tags.CustomResponseHeadersTag中实现了，在sm-shop模块的src/main/webapp/WEB-INF/shopizer-tags.tld中增加对应tag。

