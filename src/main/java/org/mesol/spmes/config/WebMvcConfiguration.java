/*
 * Copyright 2014 Mes Solutions.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mesol.spmes.config;

import com.fasterxml.jackson.core.JsonEncoding;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.EnableLoadTimeWeaving;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.Ordered;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

/**
 * $Rev:$
 * $Author:$
 * $Date:$
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@EnableWebMvc
@ComponentScan({"org.mesol.gmes.mvc.controller", "org.mesol.gmes.mvc.service"})
@EnableTransactionManagement(proxyTargetClass = true)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableLoadTimeWeaving(aspectjWeaving = EnableLoadTimeWeaving.AspectJWeaving.ENABLED)
public class WebMvcConfiguration extends WebMvcConfigurerAdapter
{
   @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
        registry.addResourceHandler("/app/**").addResourceLocations("/app/").setCachePeriod(31556926);
        registry.addResourceHandler("/ext/**").addResourceLocations("/ext/");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

    @Bean
    public ViewResolver getDefaultView() {
        ContentNegotiatingViewResolver df = new ContentNegotiatingViewResolver();
        Map<String, String> mediaTypes = new HashMap<>();
        mediaTypes.put("atom", "application/atom+xml");
        mediaTypes.put("html", "text/html");
        mediaTypes.put("json", "application/json");

        BeanNameViewResolver br = new BeanNameViewResolver();
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".jsp");

        df.setViewResolvers(Arrays.asList(new ViewResolver[]{
            br,
            resolver
        }));

        df.setMediaTypes(mediaTypes);
        MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
        GraphObjectMapper mapper = new GraphObjectMapper();
        jsonView.setObjectMapper(mapper);
        jsonView.setEncoding(JsonEncoding.UTF8);

        df.setDefaultViews(Arrays.asList(new View[]{
            jsonView
        }));

        return df;
    }

    /*---------------------------------------------------------------------------------
     Localization
     ----------------------------------------------------------------------------------*/
    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        String[] resources = {"classpath:locales/messages"};
        messageSource.setBasenames(resources);
        return messageSource;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("locale");
        return localeChangeInterceptor;
    }

    @Bean
    @Qualifier("localeResolver")
    public SessionLocaleResolver localeResolver() {
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        return localeResolver;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
}

