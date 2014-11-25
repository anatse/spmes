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
package org.mesol.spmes;

import java.security.GeneralSecurityException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.mail.NoSuchProviderException;
import javax.transaction.Transactional;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mesol.spmes.config.PersistenceJPAConfig;
import org.mesol.spmes.config.RootConfiguration;
import org.mesol.spmes.config.WebMvcConfiguration;
import org.mesol.spmes.config.WebMvcSecurityConfig;
import org.mesol.spmes.model.factory.Equipment;
import org.mesol.spmes.model.factory.EquipmentAttribute;
import org.mesol.spmes.service.EquipmentService;
import org.mesol.spmes.service.Import;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.Assert;

/**
 *
 * @author ASementsov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration (classes = {
    RootConfiguration.class,
    PersistenceJPAConfig.class,
    WebMvcConfiguration.class,
    WebMvcSecurityConfig.class
})
@TransactionConfiguration(defaultRollback = true)
@WebAppConfiguration
public class EquipmentTest {
    @Autowired
    private EquipmentService    eqService;
    
    @Autowired
    private Import              imp;

    @Test
    @Ignore
    @Transactional
    public void testFM() throws GeneralSecurityException, NoSuchProviderException {
        System.out.println ("Import sample data");

        imp.parse(getClass().getClassLoader().getResourceAsStream("imp/equipment.xml"));

        System.out.println ("Check sample data");

        Equipment eqTemplate = new Equipment();
        eqTemplate.setName("1%0");

        List<Equipment> eqs = eqService.getAll();
        Equipment wc1 = eqService.findByName("WC-1");
        Assert.notNull(wc1, "Workcenter not found: WC-1" );
//        List<Equipment> found = eqService.findByTemplate(eqTemplate);
        
        Set<EquipmentAttribute> attrs;
        attrs = new HashSet<>();
        EquipmentAttribute eqa = new EquipmentAttribute();
        eqa.setOwner(wc1);
        eqa.setName("testAttr1");
        eqa.setAttrValue("001");
        eqa.setAttrType("num");
        attrs.add(eqa);
        
        eqa = new EquipmentAttribute();
        eqa.setOwner(wc1);
        eqa.setName("testAttr2");
        eqa.setAttrValue("002");
        eqa.setAttrType("num");
        attrs.add(eqa);

        eqa = new EquipmentAttribute();
        eqa.setOwner(wc1);
        eqa.setName("testAttr3");
        eqa.setAttrValue("003");
        eqa.setAttrType("num");
        attrs.add(eqa);

        eqa = new EquipmentAttribute();
        eqa.setOwner(wc1);
        eqa.setName("testAttr4");
        eqa.setAttrValue("004");
        eqa.setAttrType("num");
        attrs.add(eqa);                
        
        wc1.setAttributes(attrs);
        List<Equipment> found = eqService.findEquipmentByAttributes (attrs);
//        Assert.isTrue(found.size() == 1, "Found not only one entity for given attributes");
//
//        Equipment site = eqService.findByName("2000");
//        Assert.notNull(site, "site 2000 not found");
//        Assert.notEmpty(site.getAttributes(), "Attributes not found in site 1000");
//        Assert.notNull(site.getAttributes().iterator().next().getOwner(), "Owner of the first attribute site 2000 is empty");
//
        System.out.println ("Test criteria API for factory model");

//        List<Equipment> eqs = eqService.findFilteredRange (
//            // first record
//            0,
//            // last record
//            100,
//            // attribute for order
//            new AbstractCriteriaService.OrderField[] {
//                new AbstractCriteriaService.OrderField("name", false)
//            },
//            // filter values
//            new AbstractCriteriaService.FilterValue("description", "WorkCenter%")
//        );

//        Assert.notEmpty(eqs, "Not found any equipment");
    }

    @Configuration
    @EnableJpaRepositories
    @EnableJpaAuditing
    static class Config {
    }
}

//        System.setProperty("mail.debug", "true");
//        
//        Properties props = new Properties();
//		props.put("mail.smtp.host", "mail.ibs.ru");
//        props.put("mail.smtp.port", "587");
//		props.put("mail.smtp.auth", "true");
//		props.put("mail.smtp.auth.ntlm.domain", "IBS");
//        props.put("mail.smtp.ssl.enable", "true");
//        props.put("mail.transport.protocol", "smtp");
//        props.put("mail.smtp.starttls.enable", "true");
////        props.put("mail.protocol.ssl.trust", "false");
////        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
////        props.put("mail.smtp.socketFactory.fallback", "true");
//        props.put("mail.debug", "true"); 
////        props.put("mail.imap.ssl.checkserveridentity", "false");
////        props.put("mail.imap.ssl.trust", "*");
////        props.setProperty("mail.imap.ssl.socketFactory.fallback", "false");
//        props.put("mail.smtp.ssl.socketFactory.class", MySSLClass.class.getName());
//        
//        Session session = Session.getInstance(props, null);
//        Transport trx = session.getTransport();
//        
//		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//		mailSender.setUsername("asementsov");
//		mailSender.setPassword("Njkbr#1428"); 
//        mailSender.setSession(session);
//
//        SimpleMailMessage templateMessage = new SimpleMailMessage();
//		templateMessage.setFrom("asementsov@ibs.ru");
//		templateMessage.setReplyTo("asementsov@ibs.ru");
//		templateMessage.setTo("asementsov@ibs.ru");
//		templateMessage.setSubject("test"); 
//        templateMessage.setText("hey");
//		mailSender.send(templateMessage);
        
//        User admin = new User();
//        admin.setName("admin");
//        admin.setPassword("admin");
//        userRepo.save(admin);
//        
//        User demo = new User();
//        demo.setName("demo");
//        demo.setPassword("demo");
//        userRepo.save(demo);
//        
//        Assert.notNull(userRepo, "User repository must not be null");
//        User user = userRepo.findByName("admin");
//        Assert.notNull(user, "admin not found");
//
//        user = userRepo.findByName("demo");
//        Assert.notNull(user, "Demo user not found");
//        
//        userRepo.deleteByName("demo");
//        user = userRepo.findByName("demo");
//        Assert.isNull(user, "Demo user not found");

