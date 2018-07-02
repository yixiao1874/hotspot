package com.gtja.dao;

import com.gtja.util.JDBCUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SpotDao {
    public static List<String> guanchaUrl(){
        List<String> list = hotspot();
        List<String> newList = new ArrayList<>();
        for (String s:list){
            newList.add("http://www.guancha.cn/Search/?k="+s);
        }
        return newList;
    }

    public static List<String> spotUrl(){
        List<String> list = hotspot();
        List<String> newList = new ArrayList<>();
        for (String s:list){
            newList.add("http://weixin.sogou.com/weixin?type=2&ie=utf8&query="+s);
        }
        return newList;
    }

    public static List<String> eastMoneyUrl(){
        List<String> list = hotspot();
        List<String> newList = new ArrayList<>();
        for (String s:list){
            newList.add("http://so.eastmoney.com/Web/GetSearchList?type=20&pageindex=1&pagesize=10&keyword="+s);
        }
        return newList;
    }

    public static List<String> hotspot(){
        List<String> list = new ArrayList<>();
        Connection connection = JDBCUtil.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String sql = "SELECT topic FROM hotspot_event WHERE `status`=1";
        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                list.add(resultSet.getString("topic"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        JDBCUtil.closeResouce(connection,preparedStatement,resultSet);
        return list;
    }

    public static void main(String[] args) {
        List<String> list = SpotDao.spotUrl();
        for (String s:list){
            System.out.println(s);
        }
    }

}
