package hr.fer.oprpp2.jmbag0036534519.listeners;

import java.beans.PropertyVetoException;
import java.io.InputStream;
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
			"message CLOB(2048) NOT NULL)";

	private static final String createPollOptionsStatement = "CREATE TABLE PollOptions(" +
			"id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
			"optionTitle VARCHAR(100) NOT NULL, " +
			"optionLink VARCHAR(150) NOT NULL, " +
			"pollID BIGINT, " +
			"votesCount BIGINT, " +
			"FOREIGN KEY (pollID) REFERENCES Polls(id))";

	public static final String[] populatePollsStatements = {
			"INSERT INTO POLLS(title, message) VALUES('Glasanje za omiljeni bend', 'Od sljedećih bendova, koji Vam je bend najdraži? Kliknite na link kako biste glasali!')",
			"INSERT INTO POLLS(title, message) VALUES('Glasanje za omiljenu cokoladu', 'Koja od sljedecih cokolada vam je najdraza?')"
	};

	public static final String[] populatePollOptionsStatements = {
			"INSERT INTO POLLOPTIONS(optionTitle, optionLink, pollID, votesCount) VALUES('The Beatles', 'https://www.youtube.com/watch?v=z9ypq6_5bsg', ?, 150)",
			"INSERT INTO POLLOPTIONS(optionTitle, optionLink, pollID, votesCount) VALUES('The Platters', 'https://www.youtube.com/watch?v=H2di83WAOhU', ?, 60)",
			"INSERT INTO POLLOPTIONS(optionTitle, optionLink, pollID, votesCount) VALUES('The Beach Boys', 'https://www.youtube.com/watch?v=2s4slliAtQU', ?, 150)",
			"INSERT INTO POLLOPTIONS(optionTitle, optionLink, pollID, votesCount) VALUES('The Four Seasons', 'https://www.youtube.com/watch?v=y8yvnqHmFds', ?, 20)",
			"INSERT INTO POLLOPTIONS(optionTitle, optionLink, pollID, votesCount) VALUES('The Marcels', 'https://www.youtube.com/watch?v=qoi3TH59ZEs', ?, 33)",
			"INSERT INTO POLLOPTIONS(optionTitle, optionLink, pollID, votesCount) VALUES('The Everly Brothers', 'https://www.youtube.com/watch?v=tbU3zdAgiX8', ?, 25)",
			"INSERT INTO POLLOPTIONS(optionTitle, optionLink, pollID, votesCount) VALUES('The Mamas And The Papas', 'https://www.youtube.com/watch?v=N-aK6JnyFmk', ?, 28)",
			"INSERT INTO POLLOPTIONS(optionTitle, optionLink, pollID, votesCount) VALUES('Milka', 'https://www.milka.hr/hr-HR', ?, 180)",
			"INSERT INTO POLLOPTIONS(optionTitle, optionLink, pollID, votesCount) VALUES('Dorina', 'https://www.kras.hr/hr/proizvodi/cokolade/dorina', ?, 70)"
	};

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
				if (!rs.next()) {
					try (PreparedStatement ps = con.prepareStatement(createPollsStatement)) {
						ps.execute();
						System.out.println("Polls table created");
					}
				}
			}

			try (ResultSet rs = con.getMetaData().getTables(null, userName, "POLLOPTIONS", null)) {
				if (!rs.next()) {
					try (PreparedStatement ps = con.prepareStatement(createPollOptionsStatement)) {
						ps.execute();
						System.out.println("Poll Options table created");
					}
				}
			}

		}
		catch (SQLException sql) {
			throw new RuntimeException("SQL Error.", sql);
		}
	}

	private void initValues(DataSource dataSource) {
		try(Connection con = dataSource.getConnection()) {
			try (PreparedStatement ps = con.prepareStatement("SELECT * FROM POLLS")) {
				try (ResultSet rs = ps.executeQuery()) {
					if (!rs.next()) {
						String populatePollsStatement1 = populatePollsStatements[0];
						String populatePollsStatement2 = populatePollsStatements[1];

						try (PreparedStatement insertPolls = con.prepareStatement(populatePollsStatement1, Statement.RETURN_GENERATED_KEYS)) {
							int insertPollCount = insertPolls.executeUpdate();
							System.out.println("Inserted " + insertPollCount + " rows into Polls.");

							try (ResultSet pollKeys = insertPolls.getGeneratedKeys()) {
								pollKeys.next();
								long favouriteBandPollId = pollKeys.getLong(1);

								for (int i = 0; i < 7; i++) {
									try (PreparedStatement insertPollOptions = con.prepareStatement(
											populatePollOptionsStatements[i], Statement.RETURN_GENERATED_KEYS)) {
										insertPollOptions.setLong(1, favouriteBandPollId);
										int insertPollOptionCount = insertPollOptions.executeUpdate();
										System.out.println("Inserted " + insertPollOptionCount + " rows into PollOptions");
									}
								}
							}
						}

						try (PreparedStatement insertPolls = con.prepareStatement(populatePollsStatement2, Statement.RETURN_GENERATED_KEYS)) {
							int insertPollCount = insertPolls.executeUpdate();
							System.out.println("Inserted " + insertPollCount + " rows into Polls.");

							try (ResultSet pollKeys = insertPolls.getGeneratedKeys()) {
								pollKeys.next();
								long favouriteChocolatePollId = pollKeys.getLong(1);

								for (int i = 7; i < 9; i++) {
									try (PreparedStatement insertPollOptions = con.prepareStatement(
											populatePollOptionsStatements[i], Statement.RETURN_GENERATED_KEYS)) {
										insertPollOptions.setLong(1, favouriteChocolatePollId);
										int insertPollOptionCount = insertPollOptions.executeUpdate();
										System.out.println("Inserted " + insertPollOptionCount + " rows into PollOptions");
									}
								}
							}
						}
					}
				}
			}

			try (PreparedStatement ps = con.prepareStatement("SELECT * FROM POLLOPTIONS")) {
				try (ResultSet rs = ps.executeQuery()) {
					if (!rs.next()) {
						try (PreparedStatement getFavBandId = con.prepareStatement("SELECT id FROM POLLS WHERE TITLE = 'Glasanje za omiljeni bend'")) {
							try (ResultSet favBandId = getFavBandId.executeQuery()) {
								int favouriteBandPollId = favBandId.getInt(1);
								for (int i = 0; i < 7; i++) {
									try (PreparedStatement insertPollOptions = con.prepareStatement(
											populatePollOptionsStatements[i], Statement.RETURN_GENERATED_KEYS)) {
										insertPollOptions.setLong(1, favouriteBandPollId);
										int insertPollOptionCount = insertPollOptions.executeUpdate();
										System.out.println("Inserted " + insertPollOptionCount + " rows into PollOptions");
									}
								}
							}
						}

						try (PreparedStatement getFavBandId = con.prepareStatement("SELECT id FROM POLLS WHERE TITLE = 'Glasanje za omiljenu cokoladu'")) {
							try (ResultSet favChocoId = getFavBandId.executeQuery()) {
								int favouriteChocolatePollId = favChocoId.getInt(1);
								for (int i = 7; i < 9; i++) {
									try (PreparedStatement insertPollOptions = con.prepareStatement(
											populatePollOptionsStatements[i], Statement.RETURN_GENERATED_KEYS)) {
										insertPollOptions.setLong(1, favouriteChocolatePollId);
										int insertPollOptionCount = insertPollOptions.executeUpdate();
										System.out.println("Inserted " + insertPollOptionCount + " rows into PollOptions");
									}
								}
							}
						}
					}
				}
			}
		}
		catch (SQLException sql) {
			throw new RuntimeException("SQL Error.", sql);
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
