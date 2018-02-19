package me.redepicness.limbo;
import me.redepicness.gamemanager.api.*;
import me.redepicness.gamemanager.api.Utility;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R2.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class main extends JavaPlugin implements Listener{

    @Override
    public void onEnable() {
        Manager.setGameManager(new LimboGameManager());
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void respawn(PlayerRespawnEvent e){
        Location respawn = Manager.getLobbyLoc();
        respawn.setWorld(Bukkit.getWorld(e.getPlayer().getName()));
        e.setRespawnLocation(respawn);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e){
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            boolean unloaded = Bukkit.unloadWorld(e.getPlayer().getName(), true);
            if(unloaded){
                Util.log(ChatColor.GREEN+"Unloaded world "+e.getPlayer().getName()+"!");
            }
            else{
                System.out.println("world name: "+e.getPlayer().getName());
                System.out.println("world null: "+(Bukkit.getWorld(e.getPlayer().getName()) == null));
                System.out.println("world players: "+((CraftWorld)Bukkit.getWorld(e.getPlayer().getName())).getHandle().players.size());
                System.out.println("world dimension: "+((CraftWorld)Bukkit.getWorld(e.getPlayer().getName())).getHandle().dimension);
            }
        }, 10);
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e){
        if(!e.getTo().getWorld().getName().equals(e.getPlayer().getName())){
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED+"You're not allowed to teleport to worlds you don't own!");
        }
    }

    @EventHandler
    public void onIntwercat(PlayerInteractEvent e){
        if(e.getClickedBlock() == null || !e.getClickedBlock().getType().equals(Material.ENDER_PORTAL_FRAME)) return;
        ItemStack book = Utility.makeItemStack(Material.WRITTEN_BOOK, ChatColor.GREEN+"Testing books!");
        BookMeta meta = (BookMeta) book.getItemMeta();
        meta.setAuthor(e.getPlayer().getName());
        meta.setTitle("Title");
        meta.addPage("Testing", "pages", ChatColor.RED + "1 2 3 vgbhnijomk,njvcrtzvgubhinjomkp,lonuiztcuvbinom");
        book.setItemMeta(meta);
        e.getPlayer().getInventory().addItem(book);
    }

    /*PacketPlayOutSpawnPosition packetSpawnPosition = new PacketPlayOutSpawnPosition(new BlockPosition(0, 101, 0));
    PacketPlayOutServerDifficulty packetDifficulty = new PacketPlayOutServerDifficulty(EnumDifficulty.PEACEFUL, true);
    PacketPlayOutHeldItemSlot packetSlot = new PacketPlayOutHeldItemSlot(0);
    PacketPlayOutPosition packetPosition = new PacketPlayOutPosition(0, 101, 0, 90, 0, EnumSet.noneOf(PacketPlayOutPosition.EnumPlayerTeleportFlags.class));
    PacketPlayOutUpdateTime packetTime = new PacketPlayOutUpdateTime(0, 18000, false);

    @Override
    public void onEnable() {
        Bukkit.getWorlds().get(0).setSpawnLocation(0, 101, 0);
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, PacketType.values()) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (!event.isServerPacket()
                        || event.getPacketType().equals(PacketType.Status.Server.OUT_SERVER_INFO)
                        || event.getPacketType().equals(PacketType.Status.Server.OUT_PING)
                        || event.getPacketType().equals(PacketType.Login.Server.ENCRYPTION_BEGIN)
                        || event.getPacketType().equals(PacketType.Login.Server.SET_COMPRESSION)
                        || event.getPacketType().equals(PacketType.Login.Server.SUCCESS)
                        || event.getPacketType().equals(PacketType.Play.Server.LOGIN)
                        || event.getPacketType().equals(PacketType.Play.Server.CUSTOM_PAYLOAD)
                        || event.getPacketType().equals(PacketType.Play.Server.SPAWN_POSITION)
                        || event.getPacketType().equals(PacketType.Play.Server.SERVER_DIFFICULTY)
                        || event.getPacketType().equals(PacketType.Play.Server.LOGIN)
                        || event.getPacketType().equals(PacketType.Play.Server.HELD_ITEM_SLOT)
                        || event.getPacketType().equals(PacketType.Play.Server.POSITION)
                        || event.getPacketType().equals(PacketType.Play.Server.KEEP_ALIVE)
                        || event.getPacketType().equals(PacketType.Play.Server.UPDATE_SIGN)
                        || event.getPacketType().equals(PacketType.Play.Server.UPDATE_TIME)
                        || event.getPacketType().equals(PacketType.Play.Server.MAP_CHUNK)
                        || event.getPacketType().equals(PacketType.Play.Server.MAP_CHUNK_BULK)
                        || event.getPacketType().equals(PacketType.Play.Server.OPEN_WINDOW)
                        || event.getPacketType().equals(PacketType.Play.Server.WINDOW_ITEMS)
                        || event.getPacketType().equals(PacketType.Play.Server.SET_SLOT)) {
                    System.out.println("Sent packet to "+event.getPlayer().getName()+" - "+event.getPacketType().toString());
                    if(event.getPacketType().equals(PacketType.Play.Server.SPAWN_POSITION)){
                        event.setPacket(new PacketContainer(PacketType.Play.Server.SPAWN_POSITION, packetSpawnPosition));
                    }
                    if(event.getPacketType().equals(PacketType.Play.Server.SERVER_DIFFICULTY)){
                        event.setPacket(new PacketContainer(PacketType.Play.Server.SERVER_DIFFICULTY, packetDifficulty));
                    }
                    if(event.getPacketType().equals(PacketType.Play.Server.HELD_ITEM_SLOT)){
                        event.setPacket(new PacketContainer(PacketType.Play.Server.HELD_ITEM_SLOT, packetSlot));
                    }
                    if(event.getPacketType().equals(PacketType.Play.Server.POSITION)){
                        event.setPacket(new PacketContainer(PacketType.Play.Server.POSITION, packetPosition));
                    }
                    if(event.getPacketType().equals(PacketType.Play.Server.UPDATE_TIME)){
                        event.setPacket(new PacketContainer(PacketType.Play.Server.UPDATE_TIME, packetTime));
                    }
                    return;
                }
                System.out.println("Cancelled send to " + event.getPlayer().getName() + " - " + event.getPacketType().toString());
                event.setCancelled(true);
            }

            @Override
            public void onPacketReceiving(PacketEvent event) {
                if(event.getPacketType().equals(PacketType.Play.Client.BLOCK_PLACE)){
                    if(event.getPacket().getBlockPositionModifier().getValues().iterator().next().toVector().toLocation(Bukkit.getWorld("world")).getBlock().getType().equals(Material.CHEST)){
                        event.getPlayer().openInventory(Bukkit.createInventory(null, InventoryType.CHEST));
                        *//*PacketContainer inventory = new PacketContainer(PacketType.Play.Server.OPEN_WINDOW, new PacketPlayOutOpenWindow(1, "minecraft:container", new ChatComponentText("Chest")));
                        PacketContainer invview = new PacketContainer(PacketType.Play.Server.WINDOW_ITEMS, new PacketPlayOutWindowItems(1, Collections.singletonList(new ItemStack(Item.getById(1)))));
                        PacketContainer setslot = new PacketContainer(PacketType.Play.Server.SET_SLOT, new PacketPlayOutSetSlot(1, 0, new ItemStack(Item.getById(1))));
                        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
                        try {
                            manager.sendServerPacket(event.getPlayer(), inventory);
                            manager.sendServerPacket(event.getPlayer(), invview);
                            manager.sendServerPacket(event.getPlayer(), setslot);
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }*//*
                    }
                }
                if (event.isServerPacket()
                        || event.getPacketType().equals(PacketType.Handshake.Client.SET_PROTOCOL)
                        || event.getPacketType().equals(PacketType.Login.Client.START)
                        || event.getPacketType().equals(PacketType.Login.Client.ENCRYPTION_BEGIN)
                        || event.getPacketType().equals(PacketType.Play.Client.KEEP_ALIVE)) {
                    return;
                }
                event.setCancelled(true);
            }
        });
    }*/
}
