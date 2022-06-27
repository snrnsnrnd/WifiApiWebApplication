<%--
  Created by IntelliJ IDEA.
  User: jspark
  Date: 2022/06/24
  Time: 9:36 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.jspark.zerobase_project01.SqliteManager" %>
<html>
<head>
    <title>위치 히스토리 목록</title>
    <style>
        table {
            /*margin-top: 20px;*/
            border : 2px solid #000000;
            border-collapse: collapse;
            width: 100%;
        }
        td, th {
            border: 1px solid #808080;
        }
        table {
            width: 100%;
        }
        tr {
            height: 30px;
        }
        div {
            margin-top: 15px;
            margin-bottom: 15px;
        }
        .tableTitleTr {
            background-color: greenyellow;
            /*text-align-all: center;*/
            text-align: center;
            height: 50px;
        }
    </style>
</head>
<body>
    <h1>위치 히스토리 목록</h1>
    <div>
        <a href="index.jsp">홈</a> <label>|</label>
        <a href="history.jsp">위치 히스토리 목록</a> <label>|</label>
        <a href="getWifi.jsp">Open API 와이파이 정보 가져오기</a> <br>
    </div>
    <%
        SqliteManager sqlManager = new SqliteManager();
        String delIdx = request.getParameter("delIdx");
        if(delIdx != null) {
            sqlManager.deleteHistoryById(delIdx);
        }
    %>
    <div>
    <table>
        <tr class="tableTitleTr">
            <td>ID</td>
            <td>Y좌표</td>
            <td>X좌표</td>
            <td>조회일자</td>
            <td>비고</td>
        </tr>
        <%
            int historySize = sqlManager.getHistorySize();
            if(historySize == 0) {
                out.println("<tr><td colspan=\"5\" align=\"center\">위치 조회 히스토리가 없습니다</td></tr>");
            } else {
                sqlManager.loadHistory();
                for(int i = 0; i < historySize; i++) {
                    String[] historyInfo = sqlManager.getLoadedHistory();
                    out.println("<tr>");
                    for(int j = 0; j < 4; j++) {
                        out.println("<td>" + historyInfo[j] + "</td>");
                    }
                    out.println("<td align=\"center\"><button type=\"button\" onclick=\"location.href=\'history.jsp?delIdx="
                            +historyInfo[0]+"\' \">삭제</button></td>");
                    out.println("</tr>");
                }
            }
        %>
    </table>
</div>
</body>
</html>
