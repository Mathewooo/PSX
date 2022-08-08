package gg.psx.event;

import gg.psx.Main;
import gg.psx.util.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.List;

public class CreateShop extends ListenerAdapter {
    static void ignore(Runnable runnable) {
        try {
            runnable.run();
        } catch (NullPointerException ignored) {}
    }

    private boolean checkIfChannelExists(Event event, String channelName) {
        List<TextChannel> channels = event.getJDA().getTextChannels();
        for (TextChannel channel : channels)
            if (channel.getName().equalsIgnoreCase(channelName)) return true;
        return false;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("create_shop")) return;
        if (!event.getMember().getRoles().contains(event.getJDA().getRoleById(Main.getShopRoleID()))) {
            event.reply("Nemáš permissie na vytvorenie shopu! Permisie si môžeš zakúpiť u majiteľa.").queue();
            return;
        }
        Category category = null;
        try {
            category = event.getJDA().getCategoriesByName(Main.getInstance().getCategoryName(), true).get(0);
        } catch (IndexOutOfBoundsException ignored) {}
        if (!checkIfChannelExists(event, Utils.returnShopName(event.getUser().getName()))) {
            if (category == null)
                category = event.getGuild().createCategory(Main.getInstance().getCategoryName()).complete();
            category.createTextChannel(Utils.returnShopName(event.getUser().getName())).queue(textChannel -> {
                textChannel.sendTyping().queue();
                sendEmbedAfterCreation(event, event.getUser(), textChannel);
                textChannel.getManager().setTopic("Shop - " + event.getUser().getName()).queue();
            });
            event.reply("Tvoj shop bol úspešne vytvorený!").queue();
        } else event.reply("Už máš vytvorený shop!").queue();
    }

    private void sendEmbedAfterCreation(SlashCommandInteractionEvent event, User user, TextChannel textChannel) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.LIGHT_GRAY);
        builder.setAuthor(user.getName() + "#" + user.getDiscriminator() + " vytvoril/a shop", null, user.getAvatarUrl());
        builder.setTitle("Shop - " + user.getName());
        ignore(() -> builder.setDescription(event.getOption("predaj").getAsString()));
        ignore(() -> builder.setImage(event.getOption("proof").getAsAttachment().getUrl()));
        ignore(() -> builder.setFooter("Kontakt: " + event.getOption("kontakt").getAsString(), null));
        textChannel.sendMessageEmbeds(builder.build()).queue();
    }

    @FunctionalInterface
    interface Runnable {
        void run() throws NullPointerException;
    }
}