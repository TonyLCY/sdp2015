package Judge;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Connection extends Thread {
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private ControllerServer controllerServer;
    private BlockingQueue<JSONObject> sendQueue;
    private Thread sendThread;

    class ProcessSendQueueThread extends Thread {
        public ProcessSendQueueThread() {
        }

        public void run() {
            sendQueue.clear();
            try {
                while (true) {
                    JSONObject msg = sendQueue.take();
                    try {
                        output.writeUTF(msg.toString());
                    }
                    catch (IOException e) {
                        sendQueue.put(msg);
                    }
                }
            }
            catch (InterruptedException e) {
            }
        }
    }

    public Connection(Socket socket) {
        this.socket = socket;
        this.controllerServer = null;
        this.sendQueue = new LinkedBlockingQueue<JSONObject>();
        try {
            this.input = new DataInputStream(this.socket.getInputStream());
            this.output = new DataOutputStream(this.socket.getOutputStream());
        }
        catch (IOException e) {
        }
    }

    public void setControllerServer(ControllerServer controllerServer) {
        this.controllerServer = controllerServer;
    }

    public void send(JSONObject msg) throws InterruptedException {
        this.sendQueue.put(msg);
    }

    @Override
    public void interrupt() {
        sendThread.interrupt();
        super.interrupt();
    }

    public void run() {
        sendThread = new ProcessSendQueueThread();
        sendThread.start();
        try {
            while (true) {
                String msg = this.input.readUTF();
                try {
                    this.controllerServer.handle(new JSONObject(msg));
                }
                catch (JSONException e) {
                }
            }
        }
        catch (IOException e) {
            this.controllerServer.logout();
        }
    }
}
