package de.nms.partysystem;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class PartyCommand implements CommandExecutor {
    private final String cp = "§7[§5Party§7] ";
    private final HashMap<Player, Boolean> hasPendingRequest = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("§cBitte führe diesen Befehl als Spieler / Ingame aus!");
            return true;
        }
        Player player = (Player) commandSender;

        if (strings.length < 1) {
            player.sendMessage(cp + "§cBitte benutze einen Befehl! Benutze '/party help' für Hilfe.");
            return true;
        }

        switch (strings[0].toLowerCase()) {
            case "create":
                if (PartyAPI.isInParty(player)) {
                    player.sendMessage(cp + "Du bist bereits in einer Party.");
                    return true;
                }
                PartyAPI.createParty(player);
                player.sendMessage(cp + "Party wurde erstellt! Du bist jetzt der Partyleiter.");
                break;

            case "help":
                player.sendMessage("§cParty Help");
                player.sendMessage(cp + "§e/party create §7- Erstellt eine Party!");
                player.sendMessage(cp + "§e/party invite <Spieler> §7- Lädt einen Spieler in deine Party ein!");
                player.sendMessage(cp + "§e/party kick <Spieler> §7- Kickt einen Spieler aus deiner Party!");
                player.sendMessage(cp + "§e/party accept §7- Nimmt eine Einladung zu einer Party an!");
                player.sendMessage(cp + "§e/party decline §7- Lehne eine Einladung zu einer Party ab!");
                player.sendMessage(cp + "§e/party delete §7- Löscht deine Party!");
                player.sendMessage(cp + "§e/party list §7- Listet deine Party!");
                break;

            case "invite":
                if (strings.length < 2) {
                    player.sendMessage(cp + "Bitte gib den Namen des Spielers an.");
                    return true;
                }

                String playerToInviteName = strings[1];
                Player playerToInvite = Bukkit.getPlayer(playerToInviteName);

                if (playerToInvite == null) {
                    player.sendMessage(cp + "Der Spieler ist nicht online.");
                    return true;
                }

                if (!PartyAPI.isInParty(player)) {
                    player.sendMessage(cp + "Du musst in einer Party sein, um Spieler einzuladen.");
                    return true;
                }

                hasPendingRequest.put(playerToInvite, true);

                Component accept = Component.text("[JOIN]")
                        .hoverEvent(HoverEvent.showText(Component.text("Join der Party!")))
                        .clickEvent(ClickEvent.runCommand("/party accept"));

                playerToInvite.sendMessage("\n\n" + cp + "§aDu wurdest zu einer Party eingeladen! Party von:§b " + player.getName());
                playerToInvite.sendMessage(accept);
                player.sendMessage("§aEinladung gesendet an " + playerToInvite.getName());
                break;

            case "accept":
                if (hasPendingRequest.containsKey(player) && hasPendingRequest.get(player)) {
                    Player partyLeader = PartyAPI.getPartyLeader(player);
                    if (partyLeader != null) {
                        PartyAPI.addPlayerToParty(partyLeader, player);
                        hasPendingRequest.remove(player);
                        player.sendMessage(cp + "Du hast die Einladung angenommen und bist jetzt in der Party.");
                    } else {
                        player.sendMessage(cp + "Es konnte kein Party-Leiter gefunden werden.");
                    }
                } else {
                    player.sendMessage(cp + "Keine ausstehende Einladung gefunden.");
                }
                break;

            case "decline":
                if (hasPendingRequest.containsKey(player) && hasPendingRequest.get(player)) {
                    hasPendingRequest.remove(player);
                    player.sendMessage(cp + "Du hast die Einladung abgelehnt.");
                } else {
                    player.sendMessage(cp + "Keine ausstehende Einladung gefunden.");
                }
                break;

            case "kick":
                if (strings.length < 2) {
                    player.sendMessage(cp + "Bitte gib den Namen des Spielers an, den du kicken möchtest.");
                    return true;
                }

                Player playerToKick = Bukkit.getPlayer(strings[1]);

                if (playerToKick == null) {
                    player.sendMessage(cp + "Der Spieler ist nicht online.");
                    return true;
                }

                if (!PartyAPI.getPartyLeader(player).equals(player)) {
                    player.sendMessage(cp + "Nur der Partyleiter kann Spieler kicken.");
                    return true;
                }

                if (!PartyAPI.getPartyMembers(player).contains(playerToKick)) {
                    player.sendMessage(cp + "Der Spieler ist nicht in deiner Party.");
                    return true;
                }

                PartyAPI.removePlayerFromParty(playerToKick);
                playerToKick.sendMessage(cp + "Du wurdest aus der Party gekickt.");
                player.sendMessage(cp + "Spieler " + playerToKick.getName() + " wurde aus der Party gekickt.");
                break;

            case "delete":
                if (PartyAPI.getPartyLeader(player).equals(player)) {
                    PartyAPI.removePlayerFromParty(player);
                    player.sendMessage(cp + "Deine Party wurde gelöscht.");
                } else {
                    player.sendMessage(cp + "Du musst der Partyleiter sein, um die Party zu löschen.");
                }
                break;
            case "list":
                if (PartyAPI.isInParty(player)){
                    if (!PartyAPI.getPartyLeader(player).equals(player)){
                        player.sendMessage(cp + "Nur der Party-Leader kann diesen command ausführen!");
                        return true;
                    }
                    PartyAPI.getPartyMembers(player).forEach(message -> {
                        player.sendMessage(cp + message);

                    });
                }
                player.sendMessage(cp + "Du musst in einer Party sein um diesen Befehl nutzen zu können!");
            break;
            default:
                player.sendMessage(cp + "§cUnbekannter Befehl! Bitte benutze '/party help'.");
                return true;
        }

        return true;
    }
}