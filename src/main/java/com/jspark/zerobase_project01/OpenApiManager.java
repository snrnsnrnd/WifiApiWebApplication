package com.jspark.zerobase_project01;

import com.google.gson.*;
import java.io.*;
import java.net.*;

//API 상의 LAT(위도), LNT(경도)가 뒤집혔음
//api 기준 LAT 126.96589 LNT 37.53875가 실제로는 LAT 37.53875, LNT 126.96589임
//고민결과 DB상에선 다시 이 두개의 데이터를 뒤집어주기로 함.

public class OpenApiManager {

    private static final String BASE_URL = "http://openapi.seoul.go.kr:8088/4858437473736e72343251426a4c71/json/TbPublicWifiInfo";
    private int infoSize;
    private final SqliteManager sqlManager;
    public int tmpInsertCount;

    public OpenApiManager() {
        try {
            JsonObject object = this.getRequestResult(BASE_URL + "/1/1");
            this.infoSize = object.getAsJsonPrimitive("list_total_count").getAsInt();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.sqlManager = new SqliteManager();
            this.tmpInsertCount = 0;
        }
    }

    public int getInfoSize() {
        return this.infoSize;
    }

    JsonObject getRequestResult(String stringUrl) throws IOException {
        URL url = new URL(stringUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/xml");
        BufferedReader rd;

        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        String str = rd.readLine();
        rd.close();
        conn.disconnect();

        return JsonParser.parseString(str).getAsJsonObject().getAsJsonObject("TbPublicWifiInfo");
    }

    public void saveToSqlAllData() {

        sqlManager.createConnection();
        sqlManager.createTable("WifiInfo", new String[] {
                "MGR_NO TEXT", "WRDOFC TEXT", "MAIN_NM TEXT",
                "ADRES1 TEXT", "ADRES2 TEXT", "INSTL_FLOOR TEXT",
                "INSTL_TY TEXT", "INSTL_MBY TEXT", "SVC_SE TEXT",
                "CMCWR TEXT", "CNSTC_YEAR TEXT", "INOUT_DOOR TEXT",
                "REMARS3 TEXT", "LAT REAL", "LNT REAL", "WORK_DTTM TEXT",
                "primary key (MGR_NO)"
        }, true);

        int cnt = 1;
        try {
            while(cnt <= this.infoSize) {
                if(cnt + 999 > this.infoSize) {
                    this.saveToSqlAllData(cnt, this.infoSize);
                } else {
                    this.saveToSqlAllData(cnt, cnt + 999);
                }
                cnt += 1000;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveToSqlAllData(int sIdx, int eIdx) throws IOException {

        JsonObject object = getRequestResult(BASE_URL + "/" + Integer.toString(sIdx) + "/" + Integer.toString(eIdx));
        this.saveToSqlAllData(object);
    }

    private void saveToSqlAllData(JsonObject object) {

        JsonArray rows = object.getAsJsonArray("row");
        for(int i = 0; i < rows.size(); i++) {
            sqlManager.insertData("WifiInfo", new String[] {
                    this.getDataFromRows(rows,i,"X_SWIFI_MGR_NO"),
                    this.getDataFromRows(rows,i,"X_SWIFI_WRDOFC"),
                    this.getDataFromRows(rows,i,"X_SWIFI_MAIN_NM"),
                    this.getDataFromRows(rows,i,"X_SWIFI_ADRES1"),
                    this.getDataFromRows(rows,i,"X_SWIFI_ADRES2"),
                    this.getDataFromRows(rows,i,"X_SWIFI_INSTL_FLOOR"),
                    this.getDataFromRows(rows,i,"X_SWIFI_INSTL_TY"),
                    this.getDataFromRows(rows,i,"X_SWIFI_INSTL_MBY"),
                    this.getDataFromRows(rows,i,"X_SWIFI_SVC_SE"),
                    this.getDataFromRows(rows,i,"X_SWIFI_CMCWR"),
                    this.getDataFromRows(rows,i,"X_SWIFI_CNSTC_YEAR"),
                    this.getDataFromRows(rows,i,"X_SWIFI_INOUT_DOOR"),
                    this.getDataFromRows(rows,i,"X_SWIFI_REMARS3"),
                    this.getDataFromRows(rows,i,"LNT"),
                    this.getDataFromRows(rows,i,"LAT"),
                    this.getDataFromRows(rows,i,"WORK_DTTM")
            });
            this.tmpInsertCount++;
        }
    }

    private String getDataFromRows(JsonArray rows, int idx, String memberName) {
        return rows.get(idx).getAsJsonObject().getAsJsonPrimitive(memberName).toString();
    }
}

