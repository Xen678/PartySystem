package de.nms.partysystem;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PartyAPI {
    private static final HashMap<Player, List<Player>> partyMap = new HashMap<>();
    private static final HashMap<Player, Player> leaderMap = new HashMap<>();

    // Create a new party for a player
    public static void createParty(Player player) {
        if (!partyMap.containsKey(player)) {
            partyMap.put(player, new ArrayList<>());
            leaderMap.put(player, player); // The player is the leader of their own party
        }
    }

    // Add a player to the party of the specified leader
    public static void addPlayerToParty(Player leader, Player member) {
        if (partyMap.containsKey(leader)) {
            partyMap.get(leader).add(member);
            leaderMap.put(member, leader); // Track the leader for each member
        }
    }

    // Remove a player from their party and clear party data
    public static void removePlayerFromParty(Player player) {
        // Remove from leader's party list
        leaderMap.values().forEach(leader -> partyMap.get(leader).remove(player));
        partyMap.remove(player);

        // Remove party leader association
        leaderMap.remove(player);
    }

    // Get the list of party members for a specific leader
    public static List<Player> getPartyMembers(Player leader) {
        return partyMap.getOrDefault(leader, new ArrayList<>());
    }

    // Check if a player is part of any party
    public static boolean isInParty(Player player) {
        return leaderMap.containsKey(player);
    }

    // Get the party leader of a given player
    public static Player getPartyLeader(Player player) {
        return leaderMap.getOrDefault(player, null);
    }
}
