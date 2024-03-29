package gc.dtu.weeg.stuvi.bluetooth;

/**
 * Created by 吴建峰 on 2017/8/25/025.
 */

public class BluetoothState {

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       	// we're doing nothing
    public static final int STATE_LISTEN = 1;     	// now listening for incoming connections
    public static final int STATE_CONNECTING = 2; 	// now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  	// now connected to a remote device
    public static final int STATE_NULL = -1;  	 	// now service is null

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    public static final int MESSAGE_STATE_TIMEOUT = 6;
    public static final int MESSAGE_BLOCK_TIMEOUT = 7;

    public static final int MESSAGE_CONVERT_INFO = 8;

    // Intent request codes
    public static final int REQUEST_CONNECT_DEVICE = 384;
    public static final int REQUEST_ENABLE_BT = 385;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";


}
