package xyz.prorickey.classicdupe.events;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;

import java.util.ArrayList;
import java.util.Random;

import static xyz.prorickey.classicdupe.ClassicDupe.plugin;

public class ChatRewards implements Listener {

    public static ArrayList<String> wordsList = new ArrayList<String>();

    private static boolean rewardActive;

    private static String currentWord;

    @EventHandler
    public void onPlayerChat(PlayerChatEvent e) {
        if(isRewardActive()){
            if(e.getMessage().toLowerCase().contains(getRewardWord())){
                rewardActive = false;
                // Add DB to player idk how
                ClassicDupe.getPlugin().getServer().getOnlinePlayers().forEach(player -> {
                    player.sendMessage(Utils.format("<green>-----------------------------------------------------"));
                    player.sendMessage(Utils.format("<aqua><bold>UNSCRAMBLE</bold> <green>the word was guessed by <italic>"+e.getPlayer().getName()+" <green>!")
                            .append(Utils.format("<green>The word was "+"<yellow>"+getRewardWord())));
                    player.sendMessage(Utils.format("<green>-----------------------------------------------------"));
                });
            }
        }
    }

    public static class ChatRewardsTask extends BukkitRunnable {

        private JavaPlugin pl = plugin;
        @Override
        public void run() {
            String rewardWord = getRandomWord();
            currentWord = rewardWord;
            rewardActive = true;
            ClassicDupe.getPlugin().getServer().getOnlinePlayers().forEach(player -> {
                player.sendMessage(Utils.format("<green>-----------------------------------------------------"));
                player.sendMessage(Utils.format("<aqua><bold>UNSCRAMBLE</bold> <green>the word below to win DB! ")
                        .append(Utils.format("<yellow>"+scrambleWord(rewardWord))));
                player.sendMessage(Utils.format("<green>-----------------------------------------------------"));
            });
            new ChatRewards.EndChatRewards().runTaskTimer(pl, 0, 10*90);
        }
    }

    public static class EndChatRewards extends BukkitRunnable {

        @Override
        public void run(){
            if(isRewardActive()){
                rewardActive = false;
                ClassicDupe.getPlugin().getServer().getOnlinePlayers().forEach(player -> {
                    player.sendMessage(Utils.format("<green>-----------------------------------------------------"));
                    player.sendMessage(Utils.format("<aqua><bold>UNSCRAMBLE</bold> <green>the word was not guessed. ")
                            .append(Utils.format("<yellow>"+getRewardWord()+" <green>was the word.")));
                    player.sendMessage(Utils.format("<green>-----------------------------------------------------"));
                });
            }
        }

    }

    public static boolean isRewardActive(){
        return rewardActive;
    }

    public static String getRewardWord(){ return currentWord; }
    private static String getRandomWord(){
        int randomIndex = new Random().nextInt(wordsList.size());
        return wordsList.get(randomIndex);
    }

    private static String scrambleWord(String word){
        StringBuilder scrambledWord = new StringBuilder(word);
        for(int c=0;c<word.length();c++){
            int randomIndex = new Random().nextInt(word.length());
            char tempChar = scrambledWord.charAt(c);
            scrambledWord.setCharAt(c, scrambledWord.charAt(randomIndex));
            scrambledWord.setCharAt(randomIndex, tempChar);
        }
        return scrambledWord.toString();
    }

}
