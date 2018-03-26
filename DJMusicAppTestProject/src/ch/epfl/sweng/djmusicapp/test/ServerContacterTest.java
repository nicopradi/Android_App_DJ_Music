package ch.epfl.sweng.djmusicapp.test;

import junit.framework.TestCase;
import ch.epfl.sweng.djmusicapp.Room;
import ch.epfl.sweng.djmusicapp.data.ServerContacter;
import ch.epfl.sweng.djmusicapp.data.ServerContacterInterface;
import ch.epfl.sweng.djmusicapp.data.ServerContacterInterface.GetRoomCallBack;

public class ServerContacterTest extends TestCase {

//    private ServerContacter contacter;
//    private User user;

    protected void setUp() throws Exception {
        super.setUp();

//        user = new User("9879876", "Jean-Michel");

        // contacter = Mockito.mock(ServerContacter.class);
    }

//    public void testGetRoom() {
//        ServerContacterInterface contacter = ServerContacter.getInstance();
//
//        GetRoomCallBack getRoomCallback = Mockito.mock(GetRoomCallBack.class);
//        contacter.getRoom("1", getRoomCallback);
//
//        ArgumentCaptor<Room> captor = ArgumentCaptor.forClass(Room.class);
//
//        Mockito.verify(getRoomCallback).onGotRoom(captor.capture());
//        Room capturedRoom = captor.getValue();
//
//        Log.i("testing getRoom", capturedRoom.getId());
//    }
    
    public void testGetRoomWithMyMockCallback() throws InterruptedException {
        MockGetRoomCallback mockCallback =  new MockGetRoomCallback();
        
        ServerContacterInterface contacter = ServerContacter.getInstance();
        
        contacter.getRoom("1", mockCallback);
        
        synchronized (mockCallback) {
            mockCallback.wait(3000);
        }
        
        Room room = mockCallback.getRoom();
        
        assertNotNull(room);
//        assertE
    }

    class MockGetRoomCallback implements GetRoomCallBack {

        private Room room;

        @Override
        public void onGotRoom(Room room) {
            this.room = room;

            synchronized (this) {
                notifyAll();
            }

        }

        @Override
        public void onFail(String errorMessage) {
            
        }

        public Room getRoom() {
            return room;
        }

    }
}
