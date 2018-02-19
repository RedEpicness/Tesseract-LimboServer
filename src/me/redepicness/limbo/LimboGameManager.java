package me.redepicness.limbo;

import me.redepicness.gamemanager.api.Game;
import me.redepicness.gamemanager.api.GameManager;
import me.redepicness.gamemanager.api.Utility;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class LimboGameManager extends GameManager {

    @Override
    public Location getLobbyLocation() {
        return Utility.makeLocation(0.5, 100, 0.5);
    }

    @Override
    public void playerJoin(Player player) {
        if(Bukkit.getWorldContainer().listFiles((dir, name) -> { return name.equals(player.getName()); }).length == 0){
            try {
                new File(Bukkit.getWorldContainer(), player.getName()).mkdir();
                Files.copy(Paths.get(URI.create("file:///" + Bukkit.getWorldContainer().getAbsolutePath() + "/world-main/level.dat")),
                        Paths.get(URI.create("file:///" + Bukkit.getWorldContainer().getAbsolutePath() + "/" + player.getName() + "/level.dat")));
                Files.copy(Paths.get(URI.create("file:///"+Bukkit.getWorldContainer().getAbsolutePath()+"/world-main/session.lock")),
                        Paths.get(URI.create("file:///"+Bukkit.getWorldContainer().getAbsolutePath()+"/"+player.getName()+"/session.lock")));
                new File(Bukkit.getWorldContainer().getAbsolutePath()+"/"+player.getName()+"/region").mkdir();
                for(File original : new File(Bukkit.getWorldContainer().getAbsolutePath()+"/world-main/region").listFiles()){
                    Files.copy(Paths.get(URI.create("file:///"+original.getAbsolutePath())),
                            Paths.get(URI.create("file:///"+Bukkit.getWorldContainer().getAbsolutePath()+"/"+player.getName()+"/region/"+original.getName())));
                }
            }
            catch (IOException e){
                player.kickPlayer(ChatColor.RED+"Something went wrong in limbo!");
                e.printStackTrace();
            }
        }
        World world = Bukkit.createWorld(new WorldCreator(player.getName()));
        Location spawn = getLobbyLocation().clone();
        spawn.setWorld(world);
        player.teleport(spawn);
    }

    @Override
    public Game registerGame() {
        return new LimboGame();
    }

    @Override
    public GameManagerType getType() {
        return GameManagerType.GAME;
    }

    public class LimboGame extends Game{

        public LimboGame() {
            super(Utility.makeLocation(0, 255, 0));
            setStage(Stage.WAITING_FOR_PLAYERS);
        }

        @Override
        public boolean isJoinable() {
            return true;
        }

        @Override
        public void join(Player player) {

        }

        @Override
        public void leave(Player player) {

        }

        @Override
        public int getMaxPlayers() {
            return 999999;
        }

        @Override
        public String getName() {
            return "Limbo";
        }

        @Override
        public void disable() {

        }
    }

}
