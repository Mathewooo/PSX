package gg.psx.event;

import gg.psx.Main;
import gg.psx.util.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class StockAndInfo extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("stock_a_info")) return;
        if (!event.getMember().getRoles().contains(event.getGuild().getRoleById(Main.getShopRoleID(event.getGuild().getId())))) {
            event.reply("Z určitého dôvodu boli tvoje permisie pre shop odobraté! Pre ďalšie informácie kontaktujte majiteľa alebo adminov.").queue();
            return;
        }
        String shopName = Utils.returnShopName(event.getUser().getName());
        Category category = Utils.returnCategory(event.getGuild());
        if (Utils.checkIfChannelExists(event, shopName, false) != null) {
            if (category == null) {
                event.reply("Channel z rovnakým menom ako tvoj shop existuje ale nenachádza sa vo svojej kategórii!").queue();
                return;
            }
            category.getTextChannels().forEach(textChannel -> {
                if (textChannel.getName().equalsIgnoreCase(shopName)) {
                    textChannel.sendTyping().queue();
                    sendStockEmbed(event, textChannel);
                }
            });
            event.reply("Úspešne si odoslal nový stock/info do tvojho shopu!").queue();
        } else event.reply("Ešte nemáš vytvorený shop!").queue();
    }

    private void sendStockEmbed(SlashCommandInteractionEvent event, TextChannel textChannel) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.ORANGE);
        builder.setTitle("Nový stock/Info - " + event.getUser().getName());
        var ref = new Object() {
            String predaj = null;
            String info = null;
            Message.Attachment attachment = null;
        };
        Utils.ignore(() -> ref.predaj = event.getOption("predaj").getAsString());
        Utils.ignore(() -> ref.info = event.getOption("info").getAsString());
        builder.setDescription((ref.predaj != null ? "Nový stock:\n" + ref.predaj + "\n" : "") + (ref.info != null ? "Info:\n" + ref.info : ""));
        Utils.ignore(() -> ref.attachment = event.getOption("fotka").getAsAttachment());
        if (ref.attachment != null) {
            builder.addField("", "Predaj:", false);
            Utils.ignore(() -> builder.setImage(ref.attachment.getUrl()));
        }
        textChannel.sendMessageEmbeds(builder.build()).queue();
    }
}
