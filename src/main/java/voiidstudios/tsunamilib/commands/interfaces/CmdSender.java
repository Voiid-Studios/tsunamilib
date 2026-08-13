package voiidstudios.tsunamilib.commands.interfaces;

public interface CmdSender {
    String prefix = "&8[&9TsunamiLib&8]&6 ";

    void sendMsg(String msg, Object... args);

    default void sendMsg(){
        sendMsg("");
    }

    default void sendPrefixedMsg(String msg, Object... args){
        sendMsg(prefix + msg, args);
    }

    boolean isPlayer();
}