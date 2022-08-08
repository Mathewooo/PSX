package gg.psx;

import gg.psx.event.CreateShop;
import gg.psx.event.OnNameChange;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.util.List;

public class Main {
    private static final String token = Dotenv.configure().load().get("TOKEN");
    public static String shopRoleID;
    private static Main instance;
    public String shopEmoji = "🛒";
    public String categoryName = shopEmoji + "shops" + shopEmoji;

    public static synchronized Main getInstance() {
        if (instance == null) instance = new Main();
        return instance;
    }

    public static void main(String[] args) throws LoginException, InterruptedException {
        JDA jda = JDABuilder.createDefault(token).setEnabledIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_EMOJIS_AND_STICKERS).setMemberCachePolicy(MemberCachePolicy.ALL).disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE).setBulkDeleteSplittingEnabled(false).setCompression(Compression.NONE).setActivity(Activity.watching("PSX BOT!")).addEventListeners(new CreateShop(), new OnNameChange()).build();
        jda.updateCommands().addCommands(Commands.slash("create_shop", "Vytvor si shop!").addOption(OptionType.STRING, "predaj", "Text, ktorý oznamuje čo chceš predaváť", false, false).addOption(OptionType.ATTACHMENT, "proof", "Fotka na proof", false, false).addOption(OptionType.STRING, "kontakt", "Kontakt pre kupujúceho", false, false)).queue();
        jda.awaitReady();
        Role shopRole = checkForRole(jda);
        if (shopRole != null) shopRoleID = shopRole.getId();
        else
            jda.getGuilds().get(0).createRole().setName("Shop").setColor(Color.MAGENTA).setHoisted(true).setMentionable(true).queue(role -> shopRoleID = role.getId());
    }

    private static Role checkForRole(JDA jda) {
        List<Role> roles = jda.getGuilds().get(0).getRoles();
        for (Role role : roles)
            if (role.getName().equals("Shop")) return role;
        return null;
    }

    public static String getShopRoleID() {
        return shopRoleID;
    }

    public String getShopEmoji() {
        return shopEmoji;
    }

    public String getCategoryName() {
        return categoryName;
    }
}
