package xyz.prorickey.classicdupe;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import xyz.prorickey.classicdupe.database.PlayerData;
import xyz.prorickey.classicdupe.database.PlayerDatabase;

import java.util.List;

public class VaultEconomy implements Economy {

    private PlayerDatabase database;

    public VaultEconomy(PlayerDatabase database) {
        this.database = database;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "VaultEconomy";
    }

    @Override
    public boolean hasBankSupport() {
        return true;
    }

    @Override
    public int fractionalDigits() {
        return -1;
    }

    @Override
    // TODO: Do some formatting
    public String format(double amount) {
        return String.valueOf(amount);
    }

    @Override
    public String currencyNamePlural() {
        return "DBS";
    }

    @Override
    public String currencyNameSingular() {
        return "DB";
    }

    @Override
    public boolean hasAccount(String playerName) {
        return database.getPlayerData(Bukkit.getOfflinePlayer(playerName).getUniqueId()) != null;
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return database.getPlayerData(player.getUniqueId()) != null;
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return hasAccount(playerName);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        return hasAccount(player);
    }

    @Override
    public double getBalance(String playerName) {
        return database.getPlayerData(Bukkit.getOfflinePlayer(playerName).getUniqueId()).getBalance();
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return database.getPlayerData(player.getUniqueId()).getBalance();
    }

    @Override
    public double getBalance(String playerName, String world) {
        return getBalance(playerName);
    }

    @Override
    public double getBalance(OfflinePlayer player, String world) {
        return getBalance(player);
    }

    @Override
    public boolean has(String playerName, double amount) {
        return getBalance(playerName) >= amount;
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return getBalance(player) >= amount;
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return has(playerName, amount);
    }

    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        return has(player, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        PlayerData data = database.getPlayerData(Bukkit.getOfflinePlayer(playerName).getUniqueId());
        data.subtractBalance((int) amount);
        return new EconomyResponse(
                amount,
                data.getBalance(),
                EconomyResponse.ResponseType.SUCCESS,
                ""
        );
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        PlayerData data = database.getPlayerData(player.getUniqueId());
        data.subtractBalance((int) amount);
        return new EconomyResponse(
                amount,
                data.getBalance(),
                EconomyResponse.ResponseType.SUCCESS,
                ""
        );
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return withdrawPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        return withdrawPlayer(player, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        PlayerData data = database.getPlayerData(Bukkit.getOfflinePlayer(playerName).getUniqueId());
        data.addBalance((int) amount);
        return new EconomyResponse(
                amount,
                data.getBalance(),
                EconomyResponse.ResponseType.SUCCESS,
                ""
        );
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        PlayerData data = database.getPlayerData(player.getUniqueId());
        data.addBalance((int) amount);
        return new EconomyResponse(
                amount,
                data.getBalance(),
                EconomyResponse.ResponseType.SUCCESS,
                ""
        );
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return depositPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        return depositPlayer(player, amount);
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return null;
    }

    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        return null;
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return null;
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return null;
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        return null;
    }

    @Override
    public List<String> getBanks() {
        return null;
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        return true;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        return true;
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return true;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        return true;
    }
}
