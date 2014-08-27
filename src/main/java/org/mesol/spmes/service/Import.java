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
package org.mesol.spmes.service;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.Collection;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.EntityType;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.log4j.Logger;
import org.mesol.spmes.model.abs.AbstractEntity;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Class used to import XML data into database
 * @version 1.0.0
 * @author ASementsov
 */
@Service
@Transactional
public class Import extends DefaultHandler implements ApplicationContextAware
{
    private static final Logger     logger = Logger.getLogger(Import.class);

    private static final class Container {
        private AbstractEntity              entity;
        private final List<Container>       children = new ArrayList<>();

        public Container(AbstractEntity entity) {
            this.entity = entity;
        }
    }

    @PersistenceContext
    private EntityManager               entityManager;

    private ApplicationContext          appContext;
    private final Stack<Container>      stack = new Stack<>();
    private final List<Container>       objects = new ArrayList<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        appContext = applicationContext;
    }

    public BeanWrapper getWrapper (Object obj) {
        final BeanWrapper bw = new BeanWrapperImpl(obj);
        return bw;
    }
    
    private void saveObject (Container cont) {
        cont.children.forEach(child -> {
            saveObject(child);
        });

        BeanWrapper bwT = getWrapper(cont.entity);
        AbstractEntity ent = entityManager.merge(cont.entity);
        BeanWrapper bwF = getWrapper(ent);
        bwT.setPropertyValue("id", bwF.getPropertyValue("id"));
    }
    
    private void saveObjects () {
        objects.stream().forEach((obj) -> {
            saveObject (obj);
        });
    }
    
    public void parse (final InputStream xml) {
		try {
     		final SAXParserFactory spf = SAXParserFactory.newInstance();
			final SAXParser sp = spf.newSAXParser();
            sp.parse(xml, this);
            saveObjects ();
     	}
		catch (ParserConfigurationException | SAXException | IOException e) {
			logger.error(e, e);
		}
	}

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (!stack.isEmpty()) {
            Container obj = stack.pop();
            if (stack.isEmpty()) {
                objects.add(obj);
            }
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if ("data".equals(qName))
            return;

        // Find enity class
        final Set<EntityType<?>> types = entityManager.getMetamodel().getEntities();
        final EntityType<?> et = types.stream().filter(w -> (w.getName() == null ? qName == null : w.getName().equals(qName))).findFirst().get();
        if (et != null) {
            try {
                Class<?> cl = et.getJavaType();
                AbstractEntity obj = (AbstractEntity)cl.newInstance();
                Container cont = new Container(obj);

                BeanWrapper bw = getWrapper (obj);
                for (int i=0;i<attributes.getLength();i++) {
                    if (bw.isWritableProperty(attributes.getQName(i)))
                        bw.setPropertyValue(attributes.getQName(i), attributes.getValue(i));
                }

                if (!stack.isEmpty()) {
                    String linkName = attributes.getValue("link");
                    Container parentCont = stack.peek();
                    parentCont.children.add(cont);

                    BeanWrapper parent = getWrapper (parentCont.entity);
                    PropertyDescriptor pd = parent.getPropertyDescriptor(linkName);
                    if (Collection.class.isAssignableFrom(pd.getPropertyType())) {
                        Collection children = (Collection)parent.getPropertyValue(linkName);
                        if (children == null) {
                            children = new ArrayList();
                            children.add(obj);
                            parent.setPropertyValue(linkName, children);
                        }
                        else {
                            children.add(obj);
                        }
                    }
                    else {
                        parent.setPropertyValue(linkName, obj);
                    }
                }

                stack.push(cont);
            } 
            catch (InstantiationException | IllegalAccessException ex) {
                logger.error(ex, ex);
            }
        }
    }
}
