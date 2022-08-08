package gg.psx.event;

import gg.psx.util.Utils;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.user.update.UserUpdateNameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class OnNameChange extends ListenerAdapter {
    @Override
    public void onUserUpdateName(UserUpdateNameEvent event) {
        if (!event.getUser().isBot())
            event.getJDA().getTextChannelsByName(Utils.returnShopName(event.getOldName()), true).forEach(textChannel -> textChannel.getManager().setName(Utils.returnShopName(event.getNewName())).queue());
        sendMessage(event.getUser(), "Tvoj shop bol premenovaný na " + Utils.returnShopName(event.getNewName()) + " - kvôli zmene mena");
    }

    private void sendMessage(User user, String content) {
        user.openPrivateChannel().flatMap(channel -> channel.sendMessage(content)).queue();
    }
}
