package xyz.prorickey.classicdupe.customitems;

import org.bukkit.NamespacedKey;
import xyz.prorickey.classicdupe.ClassicDupe;

public class CIKeys {
    public static final NamespacedKey FBWAND;
    public static final NamespacedKey BURSTBOW;

    static {
        FBWAND = new NamespacedKey(ClassicDupe.getPlugin(), "FBWAND");
        BURSTBOW = new NamespacedKey(ClassicDupe.getPlugin(), "BURSTBOW");
    }
}
