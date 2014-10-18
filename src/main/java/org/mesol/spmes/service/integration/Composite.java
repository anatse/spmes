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
package org.mesol.spmes.service.integration;

import java.lang.invoke.MethodHandles;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Description;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.PollableChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@Service
@Description("Class used to work with spring integration composites")
public class Composite 
{
    private static final Logger     logger = Logger.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private ApplicationContext      ctx;
    
    @Transactional
    public Object testControlBus () {
        Object obj = null;

        try (ConfigurableApplicationContext ac = 
            new ClassPathXmlApplicationContext (new String[]{"integration/controlbus-demo.xml"}, ctx)) {
            MessageChannel controlChannel = ac.getBean("controlChannel", MessageChannel.class);
            PollableChannel adapterOutputChanel = ac.getBean("adapterOutputChanel", PollableChannel.class);
            logger.info("Received before adapter started: " + adapterOutputChanel.receive(1000));
            controlChannel.send(new GenericMessage<>("@inboundAdapter.start()"));
            logger.info("Received before adapter started: " + adapterOutputChanel.receive(1000));
            controlChannel.send(new GenericMessage<>("@inboundAdapter.stop()"));
            logger.info("Received after adapter stopped: " + adapterOutputChanel.receive(1000));
        }

        return obj;
    }

    public void testGateway () {
        try (ConfigurableApplicationContext ac = 
            new ClassPathXmlApplicationContext (new String[]{"integration/cafeDemo-xml.xml"}, ctx)) {
            BeanGateway bg = ac.getBean("bgate", BeanGateway.class);
            Object ret = bg.placeOrder("WC-1");
        }
    }

    @Scheduled(fixedRate = 50_000)
    public void scheduler () {
        logger.info("scheduler called");
    }
}
