# geekbang-java stage 1 submit logs
v2.4 第11周作业
1.利用 Spring Boot 自动装配特性，编写一个自定义 Starter，  
  规则如下：  
  利用 @EnableAutoConfiguration 加载一个自定义 Confugration 类  
    Configuration 类装配条件需要它非 Web 应用  
    WebApplicationType = NONE  
    Configuration 类中存在一个 @Bean 返回一个输出 HelloWorld ApplicationRunner 对象  
答：见my-configuration模块test中，pom.xml中引入spring-boot-autoconfigure，包org.geektimes.configuration.autoconf中为启动类和配置类，  
   在META-INF下创建spring.factories文件并配置org.springframework.boot.autoconfigure.EnableAutoConfiguration属性。


v2.3 第8周作业
1.利用 Reactor Mono API 配合 Reactive Streams Publisher 实现，让 Subscriber 实现能够获取到数据，可以参考以下代码：  
    SimplePublisher();  
    Mono.from(publisher).subscribe(new BusinessSubscriber(5));  
    for (int i = 0; i < 5; i++) {  
      publisher.publish(i);  
    }  
  答：答案见my-reactive-messaging模块中org.geektimes.reactive.streams.SimplePublisher，抄群友作业。  
  经调试发现，直接使用from()实际订阅的是reactor.core.publisher.MonoNext.NextSubscriber，其onNext方法执行一次即将complete标志改为true，后面不会调用BusinessSubscriber的onNext方法打印数字；  
  而使用fromDirect()实际订阅的是reactor.core.publisher.StrictSubscriber，该代理类会实际调用BusinessSubscriber的onNext方法打印数字。


v2.2 第7周作业
1.描述 Spring 校验注解org.springframework.validation.annotation.Validated 的⼯作原理，它与 Spring Validator 以及 JSR-303 Bean Validation @javax.validation.Valid 之间的关系    
  答：工作原理：@Validated支持支持类型、方法、参数级校验。方法参数级别的通过调用SpringMVC中org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor.resolveArgument里的  
     org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodArgumentResolver.validateIfApplicable中对标注@Validated注解的参数进行校验；  
     方法级别的校验通过org.springframework.validation.beanvalidation.MethodValidationPostProcessor以SpringAOP方式拦截方法执行校验；类级别的对其中每个方法都执行校验。  
     标注Validated注解声明类或方法或方法参数需要校验，Spring Validator接口可以让用户自定义校验过程,Validated注解是JSR-303 Bean Validation @javax.validation.Valid注解的一个变种，增加了组校验的支持。  

v2.1 第6周作业  
1.增加一个注解名为 @ConfigSources，使其能够关联多个@ConfigSource，并且在 @ConfigSource 使用Repeatable，可以对比参考 Spring 中 @PropertySources 与@PropertySource，  
  并且文字说明 Java 8 @Repeatable 实现原理。
  可选作业，根据 URL 与 URLStreamHandler 的关系，扩展一个自定义协议，可参考sun.net.www.protocol.classpath.Handler  
答：很简单，见my-configuration模块org.geektimes.configuration.microprofile.config.annotation.ConfigSources，  
   测试类为org.geektimes.configuration.microprofile.config.annotation.ConfigSourcesTest，时间有限，写的比较粗糙；  
   Java 8 @Repeatable看网上说法是一种语法糖，通过反射获取重复注解。

v1.8 第5周作业  
1.参考实现类org.geektimes.cache.interceptor.CachePutInterceptor，实现 @javax.cache.annotation.CacheRemove 注解的@Interceptor Class  
  答：实现比较简单，见org.geektimes.cache.interceptor.CacheRemoveInterceptor，测试类在org.geektimes.cache.interceptor.CachePutInterceptorTest中，  
    新增了NewCachePut、NewCacheRemove注解。

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

