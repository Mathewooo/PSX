package gg.psx.util;

import gg.psx.Main;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.Event;

import java.util.List;

public class Utils {
    public static String returnShopName(String name) {
        return Main.getInstance().getShopEmoji() + name + Main.getInstance().getShopEmoji();
    }

    public static TextChannel checkIfChannelExists(Event event, String channelName, boolean ID) {
        List<TextChannel> channels = event.getJDA().getTextChannels();
        if (!ID) {
            for (TextChannel channel : channels)
                if (channel.getName().equalsIgnoreCase(channelName)) return channel;
        } else for (TextChannel channel : channels)
            if (channel.getId().equalsIgnoreCase(channelName)) return channel;
        return null;
    }

    public static void ignore(Runnable runnable) {
        try {
            runnable.run();
        } catch (NullPointerException ignored) {}
    }

    @FunctionalInterface
    public interface Runnable {
        void run() throws NullPointerException;
    }
}
