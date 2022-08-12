package gg.psx.event;

import gg.psx.Main;
import gg.psx.util.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class StockAndInfo extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("stock-a-info")) return;
        if (!event.getMember().getRoles().contains(event.getGuild().getRoleById(Main.getShopRoleID(event.getGuild().getId())))) {
            event.reply("Z určitého dôvodu boli tvoje permisie pre shop odobraté! Pre ďalšie informácie kontaktujte majiteľa alebo adminov.").queue();
            return;
        }
        if (Utils.checkIfChannelExists(event, Utils.returnShopName(event.getUser().getName()), false) != null) {
            event.getGuild().getTextChannels().forEach(textChannel -> {
                if (textChannel.getName().equalsIgnoreCase(Utils.returnShopName(event.getUser().getName()))) {
                    textChannel.sendTyping().queue();
                    sendStockEmbed(event, event.getUser(), textChannel);
                }
            });
            event.reply("Úspešne si odoslal nový stock/info do tvojho shopu!").queue();
        } else event.reply("Ešte nemáš vytvorený shop!").queue();
    }

    private void sendStockEmbed(SlashCommandInteractionEvent event, User user, TextChannel textChannel) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.ORANGE);
        builder.setTitle("Nový stock/Info - " + user.getName());
        var ref = new Object() {
            String predaj = null;
            String info = null;
        };
        Utils.ignore(() -> ref.predaj = event.getOption("predaj").getAsString());
        Utils.ignore(() -> ref.info = event.getOption("info").getAsString());
        builder.setDescription((ref.predaj != null ? "Nový stock:\n" + ref.predaj + "\n" : "") + (ref.info != null ? "Info:\n" + ref.info : ""));
        Message.Attachment attachment = event.getOption("fotka").getAsAttachment();
        if (attachment != null) {
            builder.addField("", "Predaj:", false);
            Utils.ignore(() -> builder.setImage(attachment.getUrl()));
        }
        textChannel.sendMessageEmbeds(builder.build()).queue();
    }
}
