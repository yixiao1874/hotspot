package com.gtja.dao;

import com.gtja.util.JDBCUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StockDao {
    Connection connection = JDBCUtil.getConnection();
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    //根据股票代码查询股票名称
    public String getNameFromCode(String code){
        String result= null;
        String sql = "SELECT stock_name FROM stock_info WHERE stock_code = ?";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,code);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                result =  resultSet.getString("stock_name");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        JDBCUtil.closeResouce(connection,preparedStatement,resultSet);
        return result;
    }
    //根据股票名称查询股票代码
    public String getCodeFromName(String name){
        String result= null;
        String sql = "SELECT stock_code FROM stock_info WHERE stock_name = ?" ;
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,name);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                result =  resultSet.getString("stock_code");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //JDBCUtil.closeResouce(connection,preparedStatement,resultSet);
        return result;
    }
}
