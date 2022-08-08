package gg.psx.util;

import gg.psx.Main;

public class Utils {
    public static String returnShopName(String name) {
        return Main.getInstance().getShopEmoji() + name + Main.getInstance().getShopEmoji();
    }
}
