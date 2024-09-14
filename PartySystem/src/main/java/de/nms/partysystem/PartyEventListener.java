package de.nms.partysystem;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.entity.Player;

public class PartyEventListener implements Listener {

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        Player partyLeader = PartyAPI.getPartyLeader(player);

        if (partyLeader != null) {
            // Check if the player is the party leader and has changed worlds
            if (partyLeader.equals(player)) {
                String newWorldName = player.getWorld().getName();
                for (Player member : PartyAPI.getPartyMembers(player)) {
                    // Move all party members to the new world of the party leader
                    if (!member.getWorld().getName().equals(newWorldName)) {
                        member.teleport(player.getLocation()); // Teleport to the leader's location
                        member.sendMessage("§eDu wurdest automatisch in die Welt von " + player.getName() + " teleportiert.");
                    }
                }
            }
        }
    }
}
