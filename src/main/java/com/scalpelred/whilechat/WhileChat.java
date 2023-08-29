package com.scalpelred.whilechat;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;

@Mod("whilechat")
public class WhileChat
{
    public Minecraft Instance;

    private static final Logger LOGGER = LogUtils.getLogger();

    //private final ArrayList<LowLevelController> Programs = new ArrayList<>();

    public WhileChat()
    {
        MinecraftForge.EVENT_BUS.register(this);

        Instance = net.minecraft.client.Minecraft.getInstance();

        File dir = new File(System.getProperty("user.dir") + "/whilechat");
        if (!dir.exists()){
            dir.mkdirs();
        }

        //Programs.add(new LowLevelController("test", this));

        LOGGER.info("Yowza! It's all so complicated!");

    }

    @SubscribeEvent
    public void onCommandsRegister(RegisterCommandsEvent event){
        WhileChatCommand.register(event.getDispatcher(), this);
    }


    private final ArrayList<String> programMessages = new ArrayList<>();
    private final ArrayList<String> clientMessages = new ArrayList<>();
    private volatile boolean processingClientMessage = false;

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent ev) {

        /*String message = ev.getMessage().getString();

        while (processingClientMessage) {
            Thread.onSpinWait();
        }

        for (int i = 0; i < programMessages.size(); i++) {
            String programMessage = programMessages.get(i);
            if (message.contains(programMessage)) {
                programMessages.remove(i);
                return;
            }
        }

        boolean isClientMessage = false;
        for (int i = 0; i < clientMessages.size(); i++) {
            if (message.contains(clientMessages.get(i))) {
                clientMessages.remove(i);
                isClientMessage = true;
                break;
            }
        }

        for (LowLevelController program : Programs) {
            program.run(message, isClientMessage);
        }*/
    }

    @SubscribeEvent
    public void onClientChat(ClientChatEvent ev) {

        /*if (ev.getMessage().contains(".reload")){
            for (LowLevelController pr : Programs) pr.loadFromFile();
            ev.setCanceled(true);
            return;
        }

        processingClientMessage = true;
        clientMessages.add(ev.getMessage().trim());
        processingClientMessage = false;*/
    }

    public void addProgramMessage(String message){
        programMessages.add(message.trim());
    }

    public void sendChatMessage(String message) {
        Instance.player.chat(message);
    }

    public void sendLocalChatMessage(String message) {
        Instance.player.sendMessage(new TextComponent(message), Instance.player.getUUID());
    }

    public void sendModInfo(String message) {
        /*addProgramMessage(message);
        Instance.player.sendMessage(new TextComponent("[While(chat)] " + message), Instance.player.getUUID());*/

        LOGGER.info(message);
    }

    public void sendModError(String message) {
        /*addProgramMessage(message);
        Instance.player.sendMessage(new TextComponent("[While(chat)] " + message), Instance.player.getUUID());*/

        LOGGER.error(message);
    }


    public boolean loadProgram(String name) {

        /*for (LowLevelController program : Programs) {
            if (program.ProgramName.equals(name)) {
                program.loadFromFile();
                return true;
            }
        }

        LowLevelController program = new LowLevelController(name, this);
        program.loadFromFile();
        Programs.add(program);*/
        return false;
    }
}
