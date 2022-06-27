<%--
  Created by IntelliJ IDEA.
  User: jspark
  Date: 2022/06/24
  Time: 9:37 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="com.jspark.zerobase_project01.OpenApiManager"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>와이파이 정보 가져오기</title>
    <style>
        h1 {
            margin-top: 30px;
        }
    </style>
</head>
<body>

<%
    OpenApiManager apiManager = new OpenApiManager();
    int wifiCount = apiManager.getInfoSize();
    apiManager.saveToSqlAllData();
    out.println("<h1 align=\"center\">" + Integer.toString(wifiCount) + "개의 WIFI 정보를 정상적으로 저장하였습니다.</h1>");
    out.println("<div align=\"center\"><a href=\"index.jsp\">홈으로 가기</a></div>");
%>

</body>
</html>
