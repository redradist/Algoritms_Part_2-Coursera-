import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

public class BaseballElimination {
    private class TeamData {
        final String name;
        final int index;
        final int gamesWins;
        final int gamesLoses;
        final int gamesRemains;
        final int[] remainingGames;
        List<String> eliminatedByTeams;

        TeamData(int numberOfTeams, String name, int index, int gamesWins, int gamesLoses, int gamesRemains) {
            this.name = name;
            this.index = index;
            this.gamesWins = gamesWins;
            this.gamesLoses = gamesLoses;
            this.gamesRemains = gamesRemains;
            this.remainingGames = new int[numberOfTeams];
            this.eliminatedByTeams = new ArrayList<>();
        }
    }

    private final int numberOfTeams;
    private final Map<String, TeamData> teams;
    private final Map<Integer, TeamData> teamsIndexes;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        In baseballTeamsData = new In(filename);
        String[] allLines = baseballTeamsData.readAllLines();
        numberOfTeams = Integer.parseInt(allLines[0]);
        teams = new HashMap<>();
        teamsIndexes = new HashMap<>();
        int teamIndex = 0;
        for (String line : Arrays.asList(allLines).subList(1, allLines.length)) {
            line = line.trim();
            String[] allSubStrings = line.split("\\s+");
            String teamName = allSubStrings[0];
            TeamData teamData = new TeamData(numberOfTeams,
                                             teamName,
                                             teamIndex,
                                             Integer.parseInt(allSubStrings[1]),
                                             Integer.parseInt(allSubStrings[2]),
                                             Integer.parseInt(allSubStrings[3]));
            teams.put(teamName, teamData);
            teamsIndexes.put(teamIndex, teamData);
            int lenght = allSubStrings.length;
            for (int j = 4; j < lenght; ++j) {
                teamData.remainingGames[j-4] = Integer.parseInt(allSubStrings[j]);
            }
            ++teamIndex;
        }
    }

    // number of teams
    public int numberOfTeams() {
        return numberOfTeams;
    }

    // all teams
    public Iterable<String> teams() {
        return teams.keySet();
    }

    // number of wins for given team
    public int wins(String teamName) {
        if (!teams.containsKey(teamName)) {
            throw new IllegalArgumentException(String.format("No such team = %s", teamName));
        }
        return teams.get(teamName).gamesWins;
    }

    // number of losses for given team
    public int losses(String teamName) {
        if (!teams.containsKey(teamName)) {
            throw new IllegalArgumentException(String.format("No such team = %s", teamName));
        }
        return teams.get(teamName).gamesLoses;
    }

    // number of remaining games for given team
    public int remaining(String teamName) {
        if (!teams.containsKey(teamName)) {
            throw new IllegalArgumentException(String.format("No such team = %s", teamName));
        }
        return teams.get(teamName).gamesRemains;
    }

    // number of remaining games between team1 and team2
    public int against(String team1Name, String team2Name) {
        if (!teams.containsKey(team1Name)) {
            throw new IllegalArgumentException(String.format("No such team = %s", team1Name));
        }
        if (!teams.containsKey(team2Name)) {
            throw new IllegalArgumentException(String.format("No such team = %s", team2Name));
        }
        TeamData team1Data = teams.get(team1Name);
        int team2Index = teams.get(team2Name).index;
        return team1Data.remainingGames[team2Index];
    }

    private boolean isTriviallyEliminated(String teamName) {
        List<String> eliminatedByTeams = new ArrayList<>();
        final TeamData teamData = teams.get(teamName);
        final int maxWins = teamData.gamesWins + teamData.gamesRemains;
        for (Map.Entry<String, TeamData> team : teams.entrySet()) {
            String opponentTeamName = team.getKey();
            if (teamName != opponentTeamName) {
                final TeamData opponentTeamData = teams.get(opponentTeamName);
                final int maxWinsForOpponent = opponentTeamData.gamesWins;
                if (maxWinsForOpponent > maxWins) {
                    eliminatedByTeams.add(team.getKey());
                }
            }
        }
        teams.get(teamName).eliminatedByTeams = eliminatedByTeams;
        return eliminatedByTeams.size() > 0;
    }

    private boolean isNonTriviallyEliminated(String teamName) {
        Set<String> handledIndexes = new HashSet<>();
        Map<Integer, Integer> mapIndexVertic = new HashMap<>();
        Map<Integer, Integer> mapVerticIndex = new HashMap<>();
        List<FlowEdge> flowEdges = new ArrayList<>();
        int verticIndex = 2;
        double maxPossibleFlow = 0.0;
        TeamData testTeamData = teams.get(teamName);
        int testTeamIndex = testTeamData.index;
        final int maxWins = testTeamData.gamesWins + testTeamData.gamesRemains;
        for (Map.Entry<Integer, TeamData> team : teamsIndexes.entrySet()) {
            if (testTeamIndex != team.getKey()) {
                TeamData opponent0TeamData = team.getValue();
                for (int opponent1Index = 0; opponent1Index < opponent0TeamData.remainingGames.length; ++opponent1Index) {
                    final int numGames = opponent0TeamData.remainingGames[opponent1Index];
                    if (testTeamIndex != opponent1Index && numGames > 0) {
                        String pairGame = null;
                        if (opponent0TeamData.index > opponent1Index) {
                            pairGame = String.format("%d-%d", opponent0TeamData.index, opponent1Index);
                        } else {
                            pairGame = String.format("%d-%d", opponent1Index, opponent0TeamData.index);
                        }
                        if (!handledIndexes.contains(pairGame)) {
                            handledIndexes.add(pairGame);
                            maxPossibleFlow += numGames;
                            int middleVerticIndex = verticIndex++;
                            flowEdges.add(new FlowEdge(0, middleVerticIndex, numGames));
                            {
                                Integer newVertixIndex = mapIndexVertic.get(opponent0TeamData.index);
                                if (newVertixIndex == null) {
                                    newVertixIndex = verticIndex++;
                                    mapIndexVertic.put(opponent0TeamData.index, newVertixIndex);
                                    mapVerticIndex.put(newVertixIndex, opponent0TeamData.index);
                                    flowEdges.add(new FlowEdge(newVertixIndex, 1, maxWins - opponent0TeamData.gamesWins));
                                }
                                flowEdges.add(new FlowEdge(middleVerticIndex, newVertixIndex, Double.POSITIVE_INFINITY));
                            }
                            {
                                Integer newVertixIndex = mapIndexVertic.get(opponent1Index);
                                if (newVertixIndex == null) {
                                    newVertixIndex = verticIndex++;
                                    mapIndexVertic.put(opponent1Index, newVertixIndex);
                                    mapVerticIndex.put(newVertixIndex, opponent1Index);
                                    flowEdges.add(new FlowEdge(newVertixIndex, 1, maxWins - teamsIndexes.get(opponent1Index).gamesWins));
                                }
                                flowEdges.add(new FlowEdge(middleVerticIndex, newVertixIndex, Double.POSITIVE_INFINITY));
                            }
                        }
                    }
                }
            }
        }
        FlowNetwork network = new FlowNetwork(verticIndex);
        for (FlowEdge edge : flowEdges) {
            network.addEdge(edge);
        }
        FordFulkerson maxFlow = new FordFulkerson(network, 0, 1);
        boolean eliminated = maxPossibleFlow > maxFlow.value();
        if (eliminated) {
            List<String> eliminatedByTeams = new ArrayList<>();
            for (int i = 0; i < verticIndex; ++i) {
                if (maxFlow.inCut(i)) {
                    Integer intg = mapVerticIndex.get(i);
                    if (intg != null) {
                        eliminatedByTeams.add(teamsIndexes.get(intg).name);
                    }
                }
            }
            teams.get(teamName).eliminatedByTeams = eliminatedByTeams;
        }
        return eliminated;
    }

    // is given team eliminated?
    public boolean isEliminated(String teamName) {
        if (!teams.containsKey(teamName)) {
            throw new IllegalArgumentException(String.format("No such team = %s", teamName));
        }
        return isTriviallyEliminated(teamName) || isNonTriviallyEliminated(teamName);
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String teamName) {
        if (!teams.containsKey(teamName)) {
            throw new IllegalArgumentException(String.format("No such team = %s", teamName));
        }
        return teams.get(teamName).eliminatedByTeams;
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
