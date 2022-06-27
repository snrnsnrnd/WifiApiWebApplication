<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.jspark.zerobase_project01.SqliteManager" %>
<!DOCTYPE html>
<html>
    <head>
        <title>와이파이 정보 구하기</title>
        <style>
            table {
                /*margin-top: 20px;*/
                border : 2px solid #000000;
                border-collapse: collapse;
                width: 100%;
            }

            tr {
                height: 50px;
            }

            td, th {
                border: 1px solid #808080;
            }

            div {
                margin-top: 15px;
                margin-bottom: 15px;
            }

            .tableTitleTr {
                background-color: greenyellow;
                /*text-align-all: center;*/
                text-align: center;
            }

        </style>
    </head>

    <body>
        <h1>와이파이 정보 구하기</h1>
        <%--<br>--%>
        <div>
            <a href="index.jsp">홈</a> <label>|</label>
            <a href="history.jsp">위치 히스토리 목록</a> <label>|</label>
            <a href="getWifi.jsp">Open API 와이파이 정보 가져오기</a> <br>
        </div>
        <%--<a href="hello-servlet">Hello Servlet</a><br>--%>
        <%--<p><%= Calendar.getInstance().getTime() %></p>--%>
        <%--<%= System.out.println("hello") %>--%>
        <%
            String xData = request.getParameter("xData");
            String yData = request.getParameter("yData");
            String wifiSearch = request.getParameter("wifiSearch");
            boolean isSearched = wifiSearch != null ? (wifiSearch.equals("true") ? true : false) : false;

            if(xData == null) {
                xData = "";
                yData = "";
            }
        %>
<%--        <form action="index.jsp" method="post">--%>
<%--            <label>LAT: </label>--%>
<%--            <input name="yData" type="text" value=<%= yData%>>--%>
<%--            <label>LNT: </label>--%>
<%--            <input name="xData" type="text" value="<%= xData%>">--%>
<%--            <input type="submit" value="근처 Wifi 정보 가져오기"><br>--%>
<%--        </form>--%>
        <label>LAT: </label>
        <input id="LAT" type="text" value=<%= yData %>>
        <label>LNT: </label>
        <input id="LNT" type="text" value=<%= xData %>>
        <button type="button" onclick=getUserLocation()>내 위치 가져오기</button>
        <button type="button" onclick=searchWifiInfo()>근처 Wifi 정보 가져오기</button>
        <div>
            <table>
                <tr class="tableTitleTr">
                    <td>거리(Km)</td>
                    <td>관리번호</td>
                    <td>자치구</td>
                    <td>와이파이명</td>
                    <td>도로명 주소</td>
                    <td>상세주소</td>
                    <td>설치위치(층)</td>
                    <td>설치유형</td>
                    <td>설치기관</td>
                    <td>서비스 구분</td>
                    <td>망 종류</td>
                    <td>설치년도</td>
                    <td>실내외 구분</td>
                    <td>와이파이 접속환경</td>
                    <td>Y좌표</td>
                    <td>X좌표</td>
                    <td>작업일자</td>
                </tr>
                <%
                    if(isSearched) {
                        SqliteManager sqlManager = new SqliteManager();
                        sqlManager.createConnection();
                        sqlManager.loadWifiInfo20(yData,xData);
                        for(int i = 0; i < 20; i++) {
                            String[] wifiInfo = sqlManager.getLoadedWifiInfo();
                            out.println("<tr>");
                            for(int j = 0; j < 17; j++) {
                                out.println("<td>" + wifiInfo[j] + "</td>");
                            }
                            out.println("</tr>");
                        }
                        sqlManager.appendHistory(yData,xData);
//                        sqlManager.closeConnection();
                    } else {
                        out.println("<tr><td colspan=\"17\" align=\"center\">위치 정보를 입력한 후에 조회해 주세요</td></tr>");
                    }
                %>
            </table>
        </div>
    <script>
        function searchWifiInfo() {
            const lat = document.getElementById("LAT").value;
            const lnt = document.getElementById("LNT").value;
            if(lat === "" || lnt === "") {
                alert("위치정보를 입력하세요");
                return;
            }
            location.href="index.jsp?yData="+lat+"&xData="+lnt+"&wifiSearch=true";
        }

        function success({ coords, timestamp}) {
            const lat = coords.latitude;
            const lnt = coords.longitude;
            location.href="index.jsp?yData="+lat+"&xData="+lnt+"&wifiSearch=false";
        }

        function getUserLocation() {

            if(!navigator.geolocation) {
                alert("위치 정보가 조회되지 않습니다.");
            }
            navigator.geolocation.getCurrentPosition(success);
        }
    </script>
    </body>
</html>