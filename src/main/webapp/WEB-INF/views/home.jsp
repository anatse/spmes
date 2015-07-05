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
    <head>
        <meta name="viewport" content="initial-scale=1, width=device-width, user-scalable=no, minimum-scale=1, maximum-scale=1">
        <meta name="apple-mobile-web-app-capable" content="yes">
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
        <meta name="_csrf" content="${_csrf.token}"/>
        <meta name="_csrf_header" content="${_csrf.headerName}"/>
    </head>
    <body>
        <iframe id="__gwt_historyFrame" style="width:0;height:0;border:0"></iframe>
        <script src=app/sc/modules/ISC_Core.js></script>
        <script src=app/sc/modules/ISC_Foundation.js></script>
        <script src=app/sc/modules/ISC_Containers.js></script>
        <script src=app/sc/modules/ISC_Grids.js></script>
        <script src=app/sc/modules/ISC_Forms.js></script>
        <script src=app/sc/modules/ISC_RichTextEditor.js></script>
        <script src=app/sc/modules/ISC_Calendar.js></script>
        <script src=app/sc/modules/ISC_DataBinding.js></script>
        <!--<script src="app/raphael-min.js"></script>-->
        <script>
            var csrfName = '${_csrf.headerName}';
            var csrfValue = '${_csrf.token}';
            
            function readCookie(name) {
                var nameEQ = name + "=";
                var ca = document.cookie.split(';');
                for (var i = 0; i < ca.length; i++) {
                    var c = ca[i];
                    while (c.charAt(0) === ' ')
                        c = c.substring(1, c.length);

                    if (c.indexOf(nameEQ) === 0)
                        return c.substring(nameEQ.length, c.length);
                }

                return null;
            }

            // Determine what skin file to load
            var currentSkin = readCookie('skin');
            if (currentSkin === null) {
                currentSkin = "Enterprise";
            }
        </script>
        <script type="text/javascript">
            document.write("<" + "script src=app/sc/skins/" + currentSkin + "/load_skin.js><" + "/script>");
        </script>

        <script language='javascript' src='app/app.nocache.js'></script>
    </body>
</html>
