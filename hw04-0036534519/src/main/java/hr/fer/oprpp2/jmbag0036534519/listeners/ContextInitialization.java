package hr.fer.oprpp2.jmbag0036534519.listeners;

import java.beans.PropertyVetoException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;

@WebListener
public class ContextInitialization implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {

		Properties dbProps = new Properties();
		Path dbPropsPath = Path.of(sce.getServletContext().getRealPath("/dbsettings.properties"));

		String connectionURL = "";

		try {
			dbProps.load(Files.newInputStream(dbPropsPath));
			String dbHost = dbProps.getProperty("host");
			String dbPort = dbProps.getProperty("port");
			String dbName = dbProps.getProperty("name");
			String dbUser = dbProps.getProperty("user");
			String dbPass = dbProps.getProperty("password");

			connectionURL = "jdbc:derby://" + dbHost + ":" + dbPort + "/" +
							dbName + ";user=" + dbUser + ";password=" + dbPass;
		}
		catch (Exception e) {
			throw new RuntimeException("Pogreska prilikom ucitavanja svojstava aplikacije.", e);
		}

		ComboPooledDataSource cpds = new ComboPooledDataSource();
		try {
			cpds.setDriverClass("org.apache.derby.client.ClientAutoloadedDriver");
		} catch (PropertyVetoException e) {
			throw new RuntimeException("Pogreška prilikom inicijalizacije poola.", e);
		}
		cpds.setJdbcUrl(connectionURL);

		sce.getServletContext().setAttribute("hr.fer.zemris.dbpool", cpds);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		ComboPooledDataSource cpds = (ComboPooledDataSource)sce.getServletContext().getAttribute("hr.fer.zemris.dbpool");
		if(cpds!=null) {
			try {
				DataSources.destroy(cpds);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
