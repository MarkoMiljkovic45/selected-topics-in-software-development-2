package hr.fer.oprpp2.jmbag0036534519.dao;

import hr.fer.oprpp2.jmbag0036534519.model.Poll;
import hr.fer.oprpp2.jmbag0036534519.model.PollOption;

import java.util.List;

/**
 * Sučelje prema podsustavu za perzistenciju podataka.
 * 
 * @author marcupic
 *
 */
public interface DAO {
    List<Poll> getAllPolls();
    Poll getPollById(long pollId);
    List<PollOption> getAllPollOptions(long pollId);
    PollOption getPollOptionById(long pollOptionId);
    void addVoteByPollOptionId(long pollOptionId);
}