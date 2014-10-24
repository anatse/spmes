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
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.EmbeddableType;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Type;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.log4j.Logger;
import org.mesol.spmes.model.abs.AbstractEntity;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
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
public class Import extends DefaultHandler
{
    private static final Logger     logger = Logger.getLogger(MethodHandles.lookup().lookupClass());


    @PersistenceContext
    private EntityManager               entityManager;

    private final Stack<Container>      stack = new Stack<>();
    private final List<Container>       objects = new ArrayList<>();
    private Set<EntityType<?>>          types;
    private Set<EmbeddableType<?>>      embeddables;

    public Set<EntityType<?>> getTypes() {
        if (types == null)
            types = entityManager.getMetamodel().getEntities();

        return types;
    }

    public Set<EmbeddableType<?>> getEmbeddables() {
        if (embeddables == null)
            embeddables = entityManager.getMetamodel().getEmbeddables();

        return embeddables;
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
        
        if (cont.entity instanceof AbstractEntity) {
            AbstractEntity ent = entityManager.merge((AbstractEntity)cont.entity);
            BeanWrapper bwF = getWrapper(ent);
            bwT.setPropertyValue("id", bwF.getPropertyValue("id"));
        }
    }

    private void saveObjects () {
        objects.stream().forEach((obj) -> {
            saveObject (obj);
        });
    }
    
    @Transactional
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

    private void setParent (BeanWrapper bw, Object parent) {
        Class<?> cl = parent.getClass();
        for (PropertyDescriptor pd : bw.getPropertyDescriptors()) {
            if (pd.getPropertyType().equals(cl)) {
                if (bw.isWritableProperty(pd.getName())) {
                    try {
                        bw.setPropertyValue(pd.getName(), parent);
                    }
                    catch (BeansException ex) {
                        logger.error(ex, ex);
                    }

                    break;
                }
            }
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if ("data".equals(qName))
            return;

        if (qName.equals("EquipmentAttribute"))
            logger.info(qName);
        
        Type<?> foundObj = null;

        // Find enity class
        final Stream<EntityType<?>> found = getTypes().stream().filter(w -> (qName.equals(w.getName())));
        Optional optData = found.findFirst();
        if (!optData.isPresent()) {
            // Try to find in embeddables
            final Stream<EmbeddableType<?>> embFound = getEmbeddables().stream().filter(w -> (qName.equals(w.getJavaType().getSimpleName())));
            optData = embFound.findFirst();
            if (optData.isPresent())
                foundObj = (Type<?>)optData.get();
        }
        else {
            foundObj = (Type<?>)optData.get();
        }

        if (foundObj != null) {
            try {
                Class<?> cl = foundObj.getJavaType();
                Object obj = cl.newInstance();
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
                    // Check link from child to parent
                    boolean isp = Boolean.valueOf(attributes.getValue("parent"));
                    if (isp)
                        setParent (bw, parentCont.entity);
                    
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

    private static class Container {

        private final Object entity;
        private final List<Container>       children = new ArrayList<>();

        public Container(Object entity) {
            this.entity = entity;
        }
    }
}
