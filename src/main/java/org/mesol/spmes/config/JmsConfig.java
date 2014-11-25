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

import java.lang.invoke.MethodHandles;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsOperations;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.destination.BeanFactoryDestinationResolver;
import org.springframework.jms.support.destination.DestinationResolver;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@Configuration
@EnableJms
public class JmsConfig 
{
    private static final Logger         logger = Logger.getLogger(MethodHandles.lookup().lookupClass());

    @Bean
    public DestinationResolver jmsDestinationResolver () {
        return new BeanFactoryDestinationResolver();
    }
    
    @Bean
    public Queue mesQueue () {
        return new ActiveMQQueue("mes");
    }
    
    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(
        ConnectionFactory connectionFactory,
        DestinationResolver jmsDestinationResolver) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setDestinationResolver(jmsDestinationResolver);
        factory.setConcurrency("3-10");
        return factory;
    }

    @Bean
    public ConnectionFactory jmsConnectionFactory() {
        final ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        factory.setBrokerURL("vm://mes");
        CachingConnectionFactory cf = new CachingConnectionFactory(factory);
        return cf;
    }

//    @Bean
//    public MessageListenerContainer jmsMessageListener () {
//        DefaultMessageListenerContainer ml = new DefaultMessageListenerContainer();
//        ml.setConnectionFactory(connectionFactory);
//        ml.setSessionTransacted(true);
//        ml.setDestinationResolver(new DynamicDestinationResolver());
//        return ml;
//    }
    
    @Bean
	public JmsOperations jmsOperations() {
		final JmsTemplate jmsTemplate = new JmsTemplate(jmsConnectionFactory());
		jmsTemplate.setDefaultDestination(mesQueue ());
		return jmsTemplate;
	}

    @Bean(destroyMethod = "stop")
    public BrokerService embeddedBroker () {
        BrokerService broker = null;
        try {
            broker = new BrokerService();
            broker.setBrokerName("mes");
            broker.addConnector("vm://mes");
            broker.start();
        } 
        catch (Exception ex) {
            logger.error(ex, ex);
        }
        
        return broker;
    }
}
