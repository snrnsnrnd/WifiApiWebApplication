package com.jspark.zerobase_project01;

import java.sql.*;

//경도 1도 거리 = cos(위도) * 6400 * 2 * 3.14 / 360
//위도 1도 거리 = 6400 * 2 * 3.14 / 360 = 111.6444444 km
//라디안 = 도 * pi / 180 = 도 * 3.14 / 180

//하버사인 계산
//select (6372*acos(cos(radians(37.5544069))*cos(radians(LAT))*cos(radians(LNT)-radians(126.8998666))+sin(radians(37.5544069))*sin(radians(LAT)))) as DISTANCE,*from WifiInfo where LAT<39 and LAT>34 and LNT>120 and LNT<130 order by DISTANCE limit 20;

public class SqliteManager {

    private static final String SQLITE_JDBC_DRIVER = "org.sqlite.JDBC";
    private static final String SQLITE_FILE_DB_URL = "jdbc:sqlite:/Users/jspark/IdeaProjects/ZeroBase_Project01/src/main/database/wifiInfo.sqlite";
    private static Connection conn = null;
    private static Statement stat;
    private final String driver;
    private final String url;
    private ResultSet loadData;

    public SqliteManager() {
        this.driver = SQLITE_JDBC_DRIVER;
        this.url = SQLITE_FILE_DB_URL;
        this.createConnection();
    }

    //    존재여부 체크 필요함
    public int createConnection() {
        try {
            Class.forName(this.driver);
            if(conn == null) {
                conn = DriverManager.getConnection(this.url);
                stat = conn.createStatement();
            }
        } catch(Exception e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    public int closeConnection() {
        try {
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    public void createTable(String table, String[] args, boolean dropAndNewGen) {

        if(this.isTableLive(table)) {
            if(dropAndNewGen) {
                this.executeUpdate("drop table "+table);
            } else {
                return;
            }
        }

        this.createTable(table, args);
    }

    private void createTable(String table, String[] args) {
        StringBuilder sql = new StringBuilder("create table " + table + " (" + args[0]);
        for(int i = 1; i < args.length; i++) {
            sql.append(",").append(args[i]);
        }
        sql.append(")");
        this.executeUpdate(sql.toString());
    }

    public void appendHistory(String lat, String lnt) {
        this.createTable("History", new String[] {
                "ID INTEGER", "LAT REAL", "LNT REAL", "DATE TEXT"
        }, false);
        int id = 1;
        String date = "\"" + DateTime.getNow() + "\"";
        int historySize = this.getHistorySize();

        try {
            if(historySize != 0) {
                ResultSet rs = this.selectData("History", "max(ID) as MAX_ID");
                rs.next();
                id = rs.getInt("MAX_ID") + 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.insertData("History", new String[]{Integer.toString(id), lat, lnt, date});
    }

    public int insertData(String table, String[] args) {
        if(args.length == 0 || table == "") {
            return -1;
        }
        StringBuilder sql = new StringBuilder("insert into " + table + " values(" + args[0]);
        for(int i = 1; i < args.length - 3; i++) {
            sql.append(",").append(args[i].replace("\\n", ""));
        }
        for(int i = args.length - 3; i < args.length - 1; i++) {
            sql.append(",").append(args[i].replace("\\n", "").replace("\"", ""));
        }
        sql.append(",").append(args[args.length-1].replace("\\n", "")).append(")");
        return this.executeUpdate(sql.toString());
    }

    public void deleteHistoryById(String id) {
        String table = "History";
        String query = "where ID=" + id;
        this.deleteData(table, query);
    }

    public int deleteData(String table, String query) {
        String sql = "delete from " + table + " " + query;
        return this.executeUpdate(sql);
    }

    public ResultSet selectData(String table, String query, String subQuery) {
        String sql = "select " + query + " from " + table + " " + subQuery;
        return this.executeReturn(sql);
    }

    public ResultSet selectData(String table, String query) {
        String sql = "select " + query + " from " + table;
        return this.executeReturn(sql);
    }

    public boolean isTableLive(String table) {
        boolean live = false;
        try {
            ResultSet rs = this.selectData("sqlite_master", "count(*) as TABLE_LIVE", "where name=\"" + table + "\"");
            rs.next();
            if(rs.getString("TABLE_LIVE").equals("1")) {
                live = true;
            }
            rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return live;
    }

    public void loadHistory() {

        String table = "History";
        String query = "*";
        String subQuery = "order by ID DESC";

        try {
            this.loadData = selectData(table, query, subQuery);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public int getHistorySize() {

        int size = 0;

        if(!this.isTableLive("History")) {
            return 0;
        }

        try {
            ResultSet rs = this.selectData("History", "count(*) as CNT");
            rs.next();
            size = rs.getInt("CNT");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return size;
    }

    public String[] getLoadedHistory() {
        String[] result = new String[4];
        try {
            this.loadData.next();
            result[0] = loadData.getString("ID");
            result[1] = loadData.getString("LAT");
            result[2] = loadData.getString("LNT");
            result[3] = loadData.getString("DATE");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public void loadWifiInfo20(String lat, String lnt) {

        String query = "round(6372*acos(cos(radians(" + lat + "))*cos(radians(LAT))*cos(radians(LNT)-radians(" + lnt + "))+sin(radians(" + lat + "))*sin(radians(LAT))),4) as DISTANCE,*";
        String table = "WifiInfo";
        String subQuery = "where LAT<44 and LAT>32 and LNT>123 and LNT<133 order by DISTANCE limit 20";

        try {
            this.loadData = selectData(table, query, subQuery);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public String[] getLoadedWifiInfo() {
        String[] result = new String[17];
        try {
            this.loadData.next();
            result[0] = loadData.getString("DISTANCE");
            result[1] = loadData.getString("MGR_NO");
            result[2] = loadData.getString("WRDOFC");
            result[3] = loadData.getString("MAIN_NM");
            result[4] = loadData.getString("ADRES1");
            result[5] = loadData.getString("ADRES2");
            result[6] = loadData.getString("INSTL_FLOOR");
            result[7] = loadData.getString("INSTL_TY");
            result[8] = loadData.getString("INSTL_MBY");
            result[9] = loadData.getString("SVC_SE");
            result[10] = loadData.getString("CMCWR");
            result[11] = loadData.getString("CNSTC_YEAR");
            result[12] = loadData.getString("INOUT_DOOR");
            result[13] = loadData.getString("REMARS3");
            result[14] = loadData.getString("LAT");
            result[15] = loadData.getString("LNT");
            result[16] = loadData.getString("WORK_DTTM");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public ResultSet executeReturn(String sql) {

        ResultSet result = null;

        try {
            result = stat.executeQuery(sql);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public int executeUpdate(String sql) {

        try {

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();
        } catch(Exception e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }
}

