/*
 * Copyright (C) 2024 techarts.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.techarts.srt.util;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * A lightweight utility that's designed to access database 
 * based on APACHE DBUTILS and HIKARI connection pool.
 */
public final class Persister {
	private HikariDataSource dataSource = null;
	
	public Persister(String driver, String url, String user, String token) {
		this.prepareDataSource(driver, url, user, token);
	}
	
	public void close() {
		if(dataSource == null) return;
		this.dataSource.close(); //Shutdown
	}
	
	private void prepareDataSource(String driver, String url, String user, String token) {
		var config = new HikariConfig();
		config.setJdbcUrl(url);
		config.setUsername(user);
		config.setPassword(token);
		config.setDriverClassName(driver);
		//config.setDataSourceClassName(driver);
		dataSource = new HikariDataSource(config);
	}
	
	public QueryRunner getExecutor() {
		if(dataSource == null) return null;
		return new QueryRunner(this.dataSource);
	}
	
	/**
	 * The method is designed to handle the INSERT, UPDATE, DELETE statements 
	 */
	public int update(String sql, Object... params) throws RuntimeException{
		if(sql == null) return -1;
		try {
			getExecutor().update(sql, params);
			return 0;
		}catch(SQLException e) {
			throw new RuntimeException("Failed save data.", e);
		}
	}
	
	/**
	 * Execute a batch operation of INSERT, UPDATE OR DELETE
	 * @param params It's required
	 */
	public int update(String sql, Object[][] params) throws RuntimeException{
		if(sql == null) return -1;
		if(params == null || params.length == 0) return -1;
		try {
			getExecutor().batch(sql, params);
			return 0;
		}catch(SQLException e) {
			throw new RuntimeException("Failed save data.", e);
		}
	}
	
	public<T> T get(String sql, Class<T> classOfTarget, Object... params) throws RuntimeException{
		if(sql == null || classOfTarget == null) return null;
		try {
			var target = new BeanHandler<T>(classOfTarget);
			return getExecutor().query(sql, target, params);
		}catch(SQLException e) {
			throw new RuntimeException("Failed to search data with SQL[" + sql + "]", e);
		}
	}
	
	public<T> List<T> getAll(String sql, Class<T> classOfTarget, Object... params)  throws RuntimeException{
		if(sql == null || classOfTarget == null) return null;
		try {
			var target = new BeanListHandler<T>(classOfTarget);
			return getExecutor().query(sql, target, params);
		}catch(SQLException e) {
			throw new RuntimeException("Failed to search data with SQL[" + sql + "]", e);
		}
	}	
}