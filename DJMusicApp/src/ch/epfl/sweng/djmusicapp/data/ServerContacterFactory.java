package ch.epfl.sweng.djmusicapp.data;

public class ServerContacterFactory {

    public static ServerContacterInterface getServerContacter() {
        
        // Just modify below so all app chooses ServerContacter or DummyServerContacter
        return ServerContacter.getInstance();
    }
}
