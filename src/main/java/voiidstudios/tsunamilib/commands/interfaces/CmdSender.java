package voiidstudios.tsunamilib.commands.interfaces;

public interface CmdSender {
    String getPrefix();

    void sendMsg(String msg, Object... args);

    default void sendMsg(){
        sendMsg("");
    }

    default void sendPrefixedMsg(String msg, Object... args){
        sendMsg(getPrefix() + msg, args);
    }

    boolean isPlayer();
}