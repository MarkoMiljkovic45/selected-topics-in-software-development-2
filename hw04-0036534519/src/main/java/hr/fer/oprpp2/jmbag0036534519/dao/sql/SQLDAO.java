package hr.fer.oprpp2.jmbag0036534519.dao.sql;

import hr.fer.oprpp2.jmbag0036534519.dao.DAO;
import hr.fer.oprpp2.jmbag0036534519.dao.DAOException;
import hr.fer.oprpp2.jmbag0036534519.model.Poll;

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
}