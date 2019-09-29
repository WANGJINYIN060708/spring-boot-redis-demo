package com.wangjinyin.smf.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;

@Configuration
@SuppressWarnings("all")
public class DruidConfiguration {
	
	/**
	 * 低版本需要配置数据源
	 * @return
	 */
	@Bean
	public ServletRegistrationBean statViewServlet() {
		
		ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
        //下面这个allow属性你填写你允许的登录的ip地址
        servletRegistrationBean.addInitParameter("allow", "192.168.10.101,127.0.0.1");
        //IP地址黑名单 根据官方配置信息 deny会优于allow
        servletRegistrationBean.addInitParameter("deny", "192.168.18.108");
        //登录druid后台监控的账号密码
        servletRegistrationBean.addInitParameter("loginUsername", "root");
        servletRegistrationBean.addInitParameter("loginPassword", "root");
        //是否能够重置数据
        servletRegistrationBean.addInitParameter("resetEnable", "true");
        return servletRegistrationBean;
	} 
	
	/**
     * 配置监控拦截器
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
    	
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new WebStatFilter());
        //拦截的路径
        filterRegistrationBean.addUrlPatterns("/*");
        //不需要拦截的信息
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        return filterRegistrationBean;
    }

	

}
