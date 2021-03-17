# geekbang-java
v0.0 - v0.8 ......

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
  答：新增OS 环境变量，以及本地配置文件ConfigSource实现:org.geektimes.configuration.microprofile.config.source.OSPropertiesConfigSource,
      org.geektimes.configuration.microprofile.config.source.ConfigFilesConfigSource,并在配置文件org.eclipse.microprofile.config.spi.ConfigSource
      中新增对应实现配置
      新增MyConverter接口，增加org.geektimes.configuration.microprofile.config.converter.impl.BooleanConverter等8中基本类型Converter,
      并在配置文件org.eclipse.microprofile.config.spi.MyConverter中配置，在org.geektimes.configuration.microprofile.config.JavaConfig
      中新增类型和Converter关联，在JavaConfig#getValue中实现目标类型转换
  
  
  
