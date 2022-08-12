package gg.psx.event;

import gg.psx.Main;
import gg.psx.util.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DeleteShop extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("delete_shop")) return;
        if (!event.getMember().getRoles().contains(event.getJDA().getRoleById(Main.getShopRoleID(event.getGuild().getId())))) {
            event.reply("Z určitého dôvodu boli tvoje permisie pre shop odobraté! Pre ďalšie informácie kontaktujte majiteľa alebo adminov.").queue();
            return;
        }
        String channelName = null;
        try {
            channelName = event.getOption("shop").getAsString().replace("#", "").replace("<", "").replace(">", "");
        } catch (NullPointerException ignored) {}
        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR) || (event.getMember().hasPermission(Permission.ADMINISTRATOR) && channelName == null)) {
            if (Utils.checkIfChannelExists(event, Utils.returnShopName(event.getUser().getName()), false) != null){
                deleteChannel(event.getGuild(), event.getUser(), Utils.returnShopName(event.getUser().getName()), false);
                event.reply("Tvoj shop bol vymazaný!").queue();
            } else event.reply("Ešte nemáš vytvorený shop!").queue();
        } else {
            if (Utils.checkIfChannelExists(event, channelName, true) == null) {
                event.reply("Tento shop neexistuje!").queue();
                return;
            }
            deleteChannel(event.getGuild(), event.getUser(), channelName, true);
            event.reply("Shop " + event.getGuild().getTextChannelById(channelName).getName() + " bol úspešne vymazaný!").queue();
        }
    }

    private void deleteChannel(Guild guild, User user, String channelName, boolean ID) {
        if (!ID) guild.getTextChannels().forEach(textChannel -> {
            if (textChannel.getName().equalsIgnoreCase(Utils.returnShopName(user.getName()))) textChannel.delete().reason("Deleted shop by Owner").queue();
        });
        else guild.getTextChannels().forEach(textChannel -> {
            if (textChannel.getId().equalsIgnoreCase(channelName)) textChannel.delete().reason("Shop deleted by Admin").queue();
        });
    }
}

