package gg.psx.event;

import gg.psx.Main;
import gg.psx.util.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.List;

public class CreateShop extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("create_shop")) return;
        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            event.reply("Vytváranie shopov je zaťial v beta verzii!").queue();
            return;
        }
        Category category = event.getJDA().getCategoriesByName(Main.getInstance().getCategoryName(), true).get(0);
        if (!checkIfChannelExists(event, Utils.returnShopName(event.getUser().getName()))) {
            if (category == null) {
                Category newCategory = event.getGuild().createCategory(Main.getInstance().getCategoryName()).complete();
                event.getJDA().getCategories().add(newCategory);
            }
            category.createTextChannel(Utils.returnShopName(event.getUser().getName())).queue(textChannel -> {
                textChannel.sendTyping().queue();
                sendEmbedAfterCreation(event, event.getUser(), textChannel);
                textChannel.getManager().setTopic("Shop - " + event.getUser().getName()).queue();
            });
            event.reply("Tvoj shop bol úspešne vytvorený!").queue();
        } else event.reply("Už máš vytvorený shop!").queue();
    }

    private boolean checkIfChannelExists(Event event, String channelName) {
        List<TextChannel> channels = event.getJDA().getTextChannels();
        for (GuildChannel channel : channels)
            if (channel.getName().equalsIgnoreCase(channelName)) return true;
        return false;
    }

    private void sendEmbedAfterCreation(SlashCommandInteractionEvent event, User user, TextChannel textChannel) {
        String text = event.getOption("predaj").getAsString();
        Message.Attachment proof = event.getOption("proof").getAsAttachment();
        String contact = event.getOption("kontakt").getAsString();
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.CYAN);
        builder.setAuthor(user.getName() + "#" + user.getDiscriminator() + " vytvoril/a shop", null, user.getAvatarUrl());
        builder.setTitle("Shop - " + user.getName());
        builder.setDescription(text);
        builder.setImage(proof.getUrl());
        builder.setFooter("Kontakt: " + contact, null);
        textChannel.sendMessageEmbeds(builder.build()).queue();
    }
}