package gg.psx;

import gg.psx.event.CreateShop;
import gg.psx.event.OnNameChange;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;

public class Main {
    private static final String token = "MTAwNTgyMDE3NDE1MjI1NzUzNg.Gz10b5.61rqIcDP2bTL0txXRT16mJk5Y1FOvBL4suJkbc";
    private static Main instance;
    public String shopEmoji = "🛒";
    public String categoryName = shopEmoji + "shops" + shopEmoji;

    public static synchronized Main getInstance() {
        if (instance == null) instance = new Main();
        return instance;
    }

    public static void main(String[] args) throws LoginException, InterruptedException {
        JDA jda = JDABuilder
                .createDefault(token)
                .disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE)
                .setBulkDeleteSplittingEnabled(false).setCompression(Compression.NONE)
                .setActivity(Activity.watching("PSX BOT!"))
                .addEventListeners(new CreateShop(), new OnNameChange())
                .build();
        jda.updateCommands()
                .addCommands(Commands.slash("create_shop", "Vytvor si shop!")
                        .addOption(OptionType.STRING, "predaj", "Text, ktorý oznamuje čo chceš predaváť", false, false)
                        .addOption(OptionType.ATTACHMENT, "proof", "Fotka na proof", false, false)
                        .addOption(OptionType.STRING, "kontakt", "Kontakt pre kupujúceho", false, false)).queue();
        jda.awaitReady();
    }

    public String getShopEmoji() {
        return shopEmoji;
    }

    public String getCategoryName() {
        return categoryName;
    }
}
