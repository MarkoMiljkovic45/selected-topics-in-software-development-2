package hr.fer.oprpp2.jmbag0036534519.listeners;

import java.beans.PropertyVetoException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;

@WebListener
public class ContextInitialization implements ServletContextListener {

	private static final String createPollsStatement = "CREATE TABLE Polls(" +
			"id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
			"title VARCHAR(150) NOT NULL, " +
			"message CLOB(2048) NOT NULL);";

	private static final String createPollOptionsStatement = "CREATE TABLE PollOptions(" +
			"id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
			"optionTitle VARCHAR(100) NOT NULL, " +
			"optionLink VARCHAR(150) NOT NULL, " +
			"pollID BIGINT, " +
			"votesCount BIGINT, " +
			"FOREIGN KEY (pollID) REFERENCES Polls(id));";

	public static final String populatePollsStatement = "INSERT INTO POLLS(title, message) VALUES" +
			"('Glasanje za omiljeni bend', 'Od sljedećih bendova, koji Vam je bend najdraži? Kliknite na link kako biste glasali!')," +
			"('Glasanje za omiljenu cokoladu', 'Koja od sljedecih cokolada am je najdraza?');";

	public static final String populatePollOptionsStatement = "INSERT INTO POLLOPTIONS(optionTitle, optionLink, pollID) VALUES" +
			"('The Beatles', 'https://www.youtube.com/watch?v=z9ypq6_5bsg', ?)," +
			"('The Platters', 'https://www.youtube.com/watch?v=H2di83WAOhU', ?)," +
			"('The Beach Boys', 'https://www.youtube.com/watch?v=2s4slliAtQU', ?)," +
			"('The Four Seasons', 'https://www.youtube.com/watch?v=y8yvnqHmFds', ?)," +
			"('The Marcels', 'https://www.youtube.com/watch?v=qoi3TH59ZEs', ?)," +
			"('The Everly Brothers', 'https://www.youtube.com/watch?v=tbU3zdAgiX8', ?)," +
			"('The Mamas And The Papas', 'https://www.youtube.com/watch?v=N-aK6JnyFmk', ?)," +
			"('Milka', 'https://www.milka.hr/hr-HR', ?)," +
			"('Dorina', 'https://www.kras.hr/hr/proizvodi/cokolade/dorina', ?);";

	@Override
	public void contextInitialized(ServletContextEvent sce) {

		Properties dbProps = new Properties();
		String connectionURL = "";

		try (InputStream is = sce.getServletContext().getResourceAsStream("/WEB-INF/dbsettings.properties")) {
			dbProps.load(is);
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

		initTables(cpds);
		initValues(cpds);
	}

	private void initTables(DataSource dataSource) {
		try (Connection con = dataSource.getConnection()) {
			String userName = con.getMetaData().getUserName().toUpperCase();

			try (ResultSet rs = con.getMetaData().getTables(null, userName, "POLLS", null)) {
				int colNumber = rs.getMetaData().getColumnCount();

				if (colNumber != 1) {
					try (PreparedStatement ps = con.prepareStatement(createPollsStatement)) {
						ps.execute();
						System.out.println("Polls table created");
					}
				}
			}

			try (ResultSet rs = con.getMetaData().getTables(null, userName, "POLLOPTIONS", null)) {
				int colNumber = rs.getMetaData().getColumnCount();

				if (colNumber != 1) {
					try (PreparedStatement ps = con.prepareStatement(createPollOptionsStatement)) {
						ps.execute();
						System.out.println("Poll Options table created");
					}
				}
			}

		}
		catch (SQLException sql) {
			throw new RuntimeException("Failed to connect to the database.", sql);
		}
	}

	private void initValues(DataSource dataSource) {
		try(Connection con = dataSource.getConnection()) {
			try (PreparedStatement ps = con.prepareStatement("SELECT * FROM POLLS")) {
				try (ResultSet rs = ps.executeQuery()) {
					if (!rs.next()) {
						try (PreparedStatement insertPolls = con.prepareStatement(populatePollsStatement, Statement.RETURN_GENERATED_KEYS)) {
							insertPolls.executeUpdate();

							try (ResultSet pollKeys = insertPolls.getGeneratedKeys()) {
								//TODO Add Poll Options
							}
						}
					}
				}
			}
		}
		catch (SQLException sql) {
			throw new RuntimeException("Failed to connect to the database.", sql);
		}
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
