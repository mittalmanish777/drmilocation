/**
 * 
 */
package com.pge.drmi.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.pge.drmi.location.jobs.RetrieveLocationJob;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Component
public class GenericDao {

	@Value("${spring.datasource.url}")
	private String dbUrl;

	private DataSource ds = null;

	public void ensureDataSource() {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(dbUrl);
		config.setUsername("postgres");
		config.setPassword("default");
		ds = new HikariDataSource(config);
	}

	private static final Logger LOGGER = Logger.getLogger(RetrieveLocationJob.class);

	public String runScripts(String sql) {
		
		String result = "Failed";
		
		if(ds == null) {
			ensureDataSource();
		}

		LOGGER.info("Executing SQL: " + sql);
		Connection connection = null;
		PreparedStatement stmt = null;
		try {
			connection = ds.getConnection();
			stmt = connection.prepareStatement(sql.toString());

			stmt.executeUpdate();
			LOGGER.error("Successfully done");
			result = "Success";
		} catch (SQLException e) {
			LOGGER.error("Error executing the SQL: " + sql);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (connection != null)
					connection.close();
				if (ds != null) {

				}
			} catch (SQLException e) {
				LOGGER.error("Error executing the SQL: " + sql);
			}
		}
		return result;

	}
}
