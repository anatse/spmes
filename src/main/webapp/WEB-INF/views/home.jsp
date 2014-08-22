<%-- 
    Document   : welcome
    Created on : Apr 9, 2014, 11:20:38 AM
    Author     : ASementsov
--%>
<%@page import="java.net.URLDecoder"%>
<%@page import="java.io.File"%>
<%@page import="java.net.URL"%>
<%@page import="java.util.Locale"%>
<%@page import="org.springframework.web.servlet.support.RequestContextUtils"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <info value="www.slideshare.net/knowfrominfo/titan-big-graph-data-with-cassandra"/>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
        <meta name="_csrf" content="${_csrf.token}"/>
        <meta name="_csrf_header" content="${_csrf.headerName}"/>

        <title id="page-title"><spring:message code="App.title"/></title>
        <link rel="stylesheet" type="text/css" href="ext/resources/ext5-crisp/ext-theme-crisp-all.css"/>
        <script type="text/javascript" charset="utf-8" src="ext/ext-all.js"></script>
        <%!
        public boolean testResource (final HttpServletRequest request, final String path) {
            final String realPath = getServletContext().getRealPath ("/" + path);
            if (realPath == null)
                return false;

            final File fp = new File (realPath);
            return fp.exists();
        }
        %>
        <!-- localization -->
        <%
        final Locale locale = RequestContextUtils.getLocale(request);    
        final String language = locale.getLanguage();
        String path = "ext/locale/ext-locale-" + language + ".js";
        if (!testResource (request, path)) {
            path = "ext/locale/ext-locale-en.js";
        }
        %>
        <script src="<%=path%>"></script>
        <script>
            Ext.Loader.setConfig({
                paths: {
                    'NeoMes': 'app'
                }
            });
            
            Ext.require ('NeoMes.locale.<%=language%>');
            Ext.require('NeoMes.utils.CommonUtils');

            Ext.onReady (function() {
                GLocale = NeoMes.locale.<%=language%>;
                GLang = '<%=language%>';
            });
        </script> 
        <script type="text/javascript" src="app/app.js"></script>
    </head>
    <body>
        <input type="hidden"
    name="${_csrf.parameterName}"
    value="${_csrf.token}"/>
    </body>
</html>
