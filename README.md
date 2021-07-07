# geekbang-java

v1.0 第1周作业  
 1.参考com.salesmanager.shop.tags.CommonResponseHeadersTag 实现一个自定义的 Tag，将 Hard Code 的 Header 名值对，变为属性配置的方式
 答：在com.salesmanager.shop.tags.CustomResponseHeadersTag中实现了，在sm-shop模块的src/main/webapp/WEB-INF/shopizer-tags.tld中增加对应tag。
 
--------------------------------------------------------------------------------------------------------
--------------------------------------------------------------------------------------------------------
--------------------------------------------------------------------------------------------------------

v0.14 - v0.15 周作业
  my-cache 模块
  1.提供一套抽象 API 实现对象的序列化和反序列化
    使用fastjson、jackson、objectstream实现，包org/geektimes/cache/serialization下；
  2.通过 Lettuce 实现一套 Redis CacheManager 以及 Cache
    包org/geektimes/cache/lettuce下实现
  

v0.12 - v0.13 周作业
  作业1 修复本程序 org.geektimes.reactive.streams 包下
  作业2 继续完善 my-rest-client POST 方法
  
v0.11
  作业1 完善 my dependency-injection 模块
        脱离 web.xml 配置实现 ComponentContext 自动初始化
        使用独立模块并且能够在 user-web 中运行成功
  作业2 完善 my-configuration 模块
        Config 对象如何能被 my-web-mvc 使用
        可能在 ServletContext 获取如何通过 ThreadLocal 获取
  
v0.9- v0.10 2021年3月16日23:25:46
  
  作业1 整合 https://jolokia.org/
        实现一个自定义 JMX MBean，通过 Jolokia 做
        Servlet 代理
  答：新增Jolokia相关dependency,新增UserManager MBean;在web.xml中新增org.jolokia.http.AgentServlet配置做Servlet代理，
      在org.geektimes.projects.user.web.listener.TestingListener中注册MBean
  
  作业2 继续完成 Microprofile config API 中的实现
		扩展 org.eclipse.microprofile.config.spi.ConfigSource实现，包括 OS 环境变量，以及本地配置文件
		扩展 org.eclipse.microprofile.config.spi.Converter实现，提供 String 类型到简单类型
		通过 org.eclipse.microprofile.config.Config 读取当前应用名称 property name = "application.name"
  答：新增OS 环境变量，以及本地配置文件ConfigSource实现:org.geektimes.configuration.microprofile.org.geektimes.configuration.microprofile.config.source.impl.OSPropertiesConfigSource,
      org.geektimes.configuration.microprofile.config.source.ConfigFilesConfigSource,并在配置文件org.eclipse.microprofile.config.spi.ConfigSource
      中新增对应实现配置
      新增MyConverter接口，增加org.geektimes.configuration.microprofile.config.converter.impl.BooleanConverter等8中基本类型Converter,
      并在配置文件org.eclipse.microprofile.config.spi.MyConverter中配置，在org.geektimes.configuration.microprofile.config.JavaConfig
      中新增类型和Converter关联，在JavaConfig#getValue中实现目标类型转换
  
  
  v0.0 - v0.8 ......

