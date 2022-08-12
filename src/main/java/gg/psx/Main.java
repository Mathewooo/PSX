package gg.psx;

import gg.psx.event.CreateShop;
import gg.psx.event.DeleteShop;
import gg.psx.event.StockAndInfo;
import gg.psx.event.OnNameChange;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.util.HashMap;
import java.util.List;

public class Main {
    private static final String token = Dotenv.configure().load().get("TOKEN");
    public static HashMap<String, String> shopRoleIDs = new HashMap<>();
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
                .setEnabledIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_EMOJIS_AND_STICKERS)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE)
                .setBulkDeleteSplittingEnabled(false).setCompression(Compression.NONE)
                .setActivity(Activity.watching("PSX BOT!"))
                .addEventListeners(new CreateShop(), new StockAndInfo(), new DeleteShop(), new OnNameChange())
                .build();
        jda.updateCommands().addCommands(Commands.slash("create_shop", "Vytvor si shop!")
                                .addOption(OptionType.STRING, "predaj", "Text, ktorý oznamuje čo chceš predaváť", true, false)
                                .addOption(OptionType.ATTACHMENT, "fotka", "Fotka na, ktorej bude čo chceš predávať", true, false)
                                .addOption(OptionType.STRING, "kontakt", "Kontakt pre kupujúceho", true, false),
                        Commands.slash("stock_a_info", "Pridaj nový stock do tvojho shopu!")
                                .addOption(OptionType.STRING, "predaj", "Text, ktorý oznamuje čo ponúkaš v novom stocku (nieje povinné)", false, false)
                                .addOption(OptionType.STRING, "info", "Random Info pre ostatných (nieje povinné)", false, false)
                                .addOption(OptionType.ATTACHMENT, "fotka", "Fotka s tvojim novým stockom (nieje povinné)", false, false),
                        Commands.slash("delete_shop", "Vymaž si shop!")
                                .addOption(OptionType.STRING, "shop", "Shop, ktorý chceš vymazať. (funguje iba pre adminov, ak nemáš admina tak to vymaže shop tebe!)", false, false))
                .queue();
        jda.awaitReady();
        jda.getGuilds().forEach(guild -> {
            Role shopRole = checkForRole(guild);
            if (shopRole != null) shopRoleIDs.put(guild.getId(), shopRole.getId());
            else
                guild.createRole().setName("Shop")
                        .setColor(Color.MAGENTA)
                        .setHoisted(true)
                        .setMentionable(true)
                        .queue(role -> shopRoleIDs.put(guild.getId(), role.getId()));
        });
    }

    private static Role checkForRole(Guild guild) {
        List<Role> roles = guild.getRoles();
        for (Role role : roles)
            if (role.getName().equals("Shop")) return role;
        return null;
    }

    public static String getShopRoleID(String ID) {
        return shopRoleIDs.get(ID);
    }

    public String getShopEmoji() {
        return shopEmoji;
    }

    public String getCategoryName() {
        return categoryName;
    }
}
