package hr.fer.oprpp2.jmbag0036534519.dao.sql;

import hr.fer.oprpp2.jmbag0036534519.dao.DAO;
import hr.fer.oprpp2.jmbag0036534519.dao.DAOException;
import hr.fer.oprpp2.jmbag0036534519.model.Poll;
import hr.fer.oprpp2.jmbag0036534519.model.PollOption;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Ovo je implementacija podsustava DAO uporabom tehnologije SQL. Ova
 * konkretna implementacija očekuje da joj veza stoji na raspolaganju
 * preko {@link SQLConnectionProvider} razreda, što znači da bi netko
 * prije no što izvođenje dođe do ove točke to trebao tamo postaviti.
 * U web-aplikacijama tipično rješenje je konfigurirati jedan filter 
 * koji će presresti pozive servleta i prije toga ovdje ubaciti jednu
 * vezu iz connection-poola, a po zavrsetku obrade je maknuti.
 *  
 * @author marcupic
 */
public class SQLDAO implements DAO {

    @Override
    public List<Poll> getAllPolls() {
        List<Poll> polls = new ArrayList<>();

        Connection con = SQLConnectionProvider.getConnection();
        //                             1    2       3
        String sqlStatement = "SELECT id, title, message FROM Polls";

        try (PreparedStatement pst = con.prepareStatement(sqlStatement)){
            try (ResultSet rs = pst.executeQuery()) {
                while (rs != null && rs.next()) {
                    Poll poll = new Poll();

                    poll.setId(rs.getLong(1));
                    poll.setTitle(rs.getString(2));
                    poll.setMessage(rs.getString(3));
                    polls.add(poll);
                }
            }
        }
        catch(Exception ex) {
            throw new DAOException("Pogreška prilikom dohvata liste izbora.", ex);
        }

        return polls;
    }

    @Override
    public Poll getPollById(long pollId) {
        Connection con = SQLConnectionProvider.getConnection();

        String sqlStatement = "SELECT id, title, message FROM POLLS WHERE id = ?";

        try (PreparedStatement pst = con.prepareStatement(sqlStatement)) {
            pst.setLong(1, pollId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs != null && rs.next()) {
                    Poll poll = new Poll();

                    poll.setId(rs.getLong(1));
                    poll.setTitle(rs.getString(2));
                    poll.setMessage(rs.getString(3));

                    return poll;
                }
            }
        }
        catch(Exception ex) {
            throw new DAOException("Pogreška prilikom dohvata glasanja.", ex);
        }

        return null;
    }

    @Override
    public List<PollOption> getAllPollOptions(long pollId) {
        List<PollOption> pollOptions = new ArrayList<>();

        Connection con = SQLConnectionProvider.getConnection();
        //                             1       2           3          4         5
        String sqlStatement = "SELECT id, optionTitle, optionLink, pollId, votesCount FROM POLLOPTIONS WHERE pollId = ?";

        try (PreparedStatement pst = con.prepareStatement(sqlStatement)){
            pst.setLong(1, pollId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs != null && rs.next()) {
                    PollOption pollOption = new PollOption();

                    pollOption.setId(rs.getLong(1));
                    pollOption.setOptionTitle(rs.getString(2));
                    pollOption.setOptionLink(rs.getString(3));
                    pollOption.setPollID(rs.getLong(4));
                    pollOption.setVotesCount(rs.getLong(5));

                    pollOptions.add(pollOption);
                }
            }
        }
        catch(Exception ex) {
            throw new DAOException("Pogreška prilikom dohvata liste izbora.", ex);
        }

        return pollOptions;
    }

    @Override
    public PollOption getPollOptionById(long pollOptionId) {
        Connection con = SQLConnectionProvider.getConnection();

        //                             1        2          3         4         5
        String sqlStatement = "SELECT id, optionTitle, optionLink, pollId, votesCount FROM POLLOPTIONS WHERE id = ?";

        try (PreparedStatement pst = con.prepareStatement(sqlStatement)) {
            pst.setLong(1, pollOptionId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs != null && rs.next()) {
                    PollOption option = new PollOption();

                    option.setId(rs.getLong(1));
                    option.setOptionTitle(rs.getString(2));
                    option.setOptionLink(rs.getString(3));
                    option.setPollID(rs.getLong(4));
                    option.setVotesCount(rs.getLong(5));

                    return option;
                }
            }
        }
        catch(Exception ex) {
            throw new DAOException("Pogreška prilikom dohvata glasanja.", ex);
        }

        return null;
    }

    @Override
    public void addVoteByPollOptionId(long pollOptionId) {
        PollOption option = getPollOptionById(pollOptionId);

        if (option != null) {
            Connection con = SQLConnectionProvider.getConnection();
//                                                                     1            2
            String sqlStatement = "UPDATE POLLOPTIONS SET votesCount = ? WHERE id = ?";

            try (PreparedStatement pst = con.prepareStatement(sqlStatement)) {
                pst.setLong(1, option.getVotesCount() + 1);
                pst.setLong(2, pollOptionId);
                pst.executeUpdate();
            }
            catch(Exception ex) {
                throw new DAOException("Pogreška prilikom dohvata glasanja.", ex);
            }
        }
    }
}