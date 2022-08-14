package gg.psx.event;

import gg.psx.Main;
import gg.psx.util.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.atomic.AtomicBoolean;

public class DeleteShop extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("delete_shop")) return;
        if (!event.getMember().getRoles().contains(event.getGuild().getRoleById(Main.getShopRoleID(event.getGuild().getId())))) {
            event.reply("Z určitého dôvodu boli tvoje permisie pre shop odobraté! Pre ďalšie informácie kontaktujte majiteľa alebo adminov.").queue();
            return;
        }
        String channelName = null;
        try {
            channelName = event.getOption("shop").getAsString().replace("#", "").replace("<", "").replace(">", "");
        } catch (NullPointerException ignored) {}
        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR) || (event.getMember().hasPermission(Permission.ADMINISTRATOR) && channelName == null)) {
            if (Utils.checkIfChannelExists(event, Utils.returnShopName(event.getUser().getName()), false) != null) {
                if (deleteChannel(event.getGuild(), event.getUser(), Utils.returnShopName(event.getUser().getName()), false))
                    event.reply("Tvoj shop bol úspešne odstránený!").queue();
            } else event.reply("Ešte nemáš vytvorený shop!").queue();
        } else {
            if (Utils.checkIfChannelExists(event, channelName, true) == null) {
                event.reply("Tento shop neexistuje!").queue();
                return;
            }
            if (deleteChannel(event.getGuild(), event.getUser(), channelName, true))
                event.reply("Shop " + event.getGuild().getTextChannelById(channelName).getName() + " bol úspešne vymazaný!").queue();
            else event.reply("Tento channel sa nedá ostrádniť lebo nieje shop!").queue();
        }
    }

    private boolean deleteChannel(Guild guild, User user, String channelName, boolean ID) {
        AtomicBoolean bool = new AtomicBoolean(false);
        if (!ID) guild.getTextChannels().forEach(textChannel -> {
            if (textChannel.getName().equalsIgnoreCase(Utils.returnShopName(user.getName()))) {
                textChannel.delete().reason("Deleted shop by Owner").queue();
                bool.set(true);
            }
        });
        else guild.getTextChannels().forEach(textChannel -> {
            if (textChannel.getId().equals(channelName) && textChannel.getName().contains("🛒")) {
                textChannel.delete().reason("Shop deleted by Admin").queue();
                bool.set(true);
            }
        });
        return bool.get();
    }
}

