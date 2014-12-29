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

package org.mesol.spmes.gwt.shared;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.MetaElement;
import com.google.gwt.dom.client.NodeList;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.DSDataFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
public class BaseDataSource extends DataSource
{
    private static Map              headers;
    
    private static Map buildHeaders () {
        if (headers != null && headers.isEmpty()) {
            return headers;
        }

        String value = getCsrfValue (), 
               name = getCsrfName ();
        
        if (value == null || name == null) {
            NodeList<Element> tags = Document.get().getElementsByTagName("meta");
            for (int i = 0; i < tags.getLength(); i++) {
                MetaElement metaTag = ((MetaElement) tags.getItem(i));
                switch (metaTag.getName()) {
                    case "_csrf":
                        value = metaTag.getContent();
                        break;
                    case "_csrf_header":
                        name = metaTag.getContent();
                        break;
                }
            }
        }

        headers = new HashMap();
        headers.put(name, value);
        return headers;
    }

    private static native String getCsrfName() /*-{
        return $wnd.csrfName;
    }-*/;

    private static native String getCsrfValue() /*-{
        return $wnd.csrfValue;
    }-*/;
    
    protected BaseDataSource() {
        setDataFormat(DSDataFormat.JSON);
        setPreventHTTPCaching(Boolean.TRUE);
        DSRequest reqProps = new DSRequest();
        reqProps.setHttpMethod("POST");
        reqProps.setHttpHeaders(buildHeaders());
        setRequestProperties(reqProps);
    }
}
