package hr.fer.oprpp2.servlets.voting.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class BandEntry {
    private final long id;
    private final String name;
    private final String representativeUrl;
    private int voteCount;

    public BandEntry(long id, String name, String representativeUrl, int voteCount) {
        this.id = id;
        this.name = name;
        this.representativeUrl = representativeUrl;
        this.voteCount = voteCount;
    }

    public BandEntry(long id, String name, String representativeUrl) {
        this(id, name, representativeUrl, 0);
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRepresentativeUrl() {
        return representativeUrl;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public static List<BandEntry> load(Path bandsPath, Path votesPath) {
        List<BandEntry> bands = new ArrayList<>();

        try {
            Map<Long, Integer> votesMap = loadVotes(votesPath);
            List<String> entries  = Files.readAllLines(bandsPath);

            for (String entry: entries) {
                String[] columns = entry.split("\\t");

                long id = Long.parseLong(columns[0]);
                String name = columns[1];
                String url = columns[2];
                Integer voteCount = votesMap.get(id);

                if (voteCount == null) {
                    bands.add(new BandEntry(id, name, url));
                } else {
                    bands.add(new BandEntry(id, name, url, voteCount));
                }
            }
        } catch (IOException ignore) {}

        return bands;
    }

    public static List<BandEntry> load(Path bandsPath) {
        return load(bandsPath, null);
    }

    public static Map<Long, Integer> loadVotes(Path votesPath) throws IOException {
        Map<Long, Integer> votesMap = new HashMap<>();

        if (votesPath != null) {
            if (!Files.exists(votesPath)) {
                Files.createFile(votesPath);
            }

            List<String> entries = Files.readAllLines(votesPath);

            for (String entry: entries) {
                String[] columns = entry.split("\\t");

                long id = Long.parseLong(columns[0]);
                int voteCount = Integer.parseInt(columns[1]);

                votesMap.put(id, voteCount);
            }
        }

        return votesMap;
    }
}
