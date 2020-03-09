import GUI.ChatGUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Array;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringJoiner;

public class Client extends Thread implements ActionListener {
    public Client(String serverString, int serverPort, String requestMessage, String name){
        // Create an endPoint on this computer to this program identified by the provided port
        setServerParameters(serverString, serverPort);
        clientEnd = new EndPoint();
        this.name = name;
    }

    // Client parameters include server references for processing transmissions
    public void setServerParameters(String serverAddressString, int serverPortNumber){
        InetAddress address = null;

        try {
            address = InetAddress.getByName(serverAddressString);
        } catch (Exception e) {
            System.err.println("Couldn't find host with that address");
        }

        try {
            socket = new Socket(address, serverPortNumber);
        } catch (Exception e) {
            System.err.println("Couldn't create socket with this address and/or port number");
        }
    }

    public void setRequestMessage(String requestMessage){
        this.requestMessage = requestMessage;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getClientName(){
        return name;
    }

    public String getCommand() {
        return command;
    }

    // Checks message for potential command
    private void checkAndGetCommand(){
        String tempMessage = "";
        String commandSymbol = "/";
        String[] messageChars;
        String[] splitMessage = message.split(" ");

        if(message.startsWith(commandSymbol)){
            command = splitMessage[0];
        }

        switch (command){
            case "/tell":
                splitMessage[0] += "|";
                splitMessage[1] += "|";
                splitMessage[1] = splitMessage[1].trim();
                message = "";

                for(int i = 2; i < splitMessage.length; i++){
                    message += splitMessage[i] + " ";
                }

                chatGUI.displayMessage(getClientName() + ": " + message);
                message = Arrays.toString(splitMessage);
                messageChars = message.split("");
                message = "";

                for(int i = 0; i < messageChars.length; i++){
                    if (messageChars[i].equals(",") && !messageChars[i+1].equals(",")) {
                        messageChars[i] = "";
                    }else if(messageChars[i].equals(" ") && messageChars[i -2].equals("|")){
                        messageChars[i] = "";
                    }
                    message += messageChars[i];
                }

                message = message.replaceFirst("\\[", "").replaceAll("]$", "");

                System.out.println(message);
                break;

            case "/broadcast":
                splitMessage[0] += "|";
                splitMessage[1] = splitMessage[1].trim();

                message = Arrays.toString(splitMessage);
                messageChars = message.split("");
                message = "";

                for(int i = 0; i < messageChars.length; i++){
                    if (messageChars[i].equals(",") && !messageChars[i+1].equals(",")) {
                        messageChars[i] = "";
                    }else if(messageChars[i].equals(" ") && messageChars[i -2].equals("|")){
                        messageChars[i] = "";
                    }
                    message += messageChars[i];
                }

                message = message.replaceFirst("\\[", "").replaceAll("]$", "");
                System.out.println(message);
                break;
        }
    }

    private void extractMessageInfo(){
        // get the text typed in input field, using ChatGUI utility method
        message = chatGUI.getInput();
        checkAndGetCommand();
    }

    public void actionPerformed(ActionEvent e) {
        // There is only one event coming out from the GUI and that’s
        // the carriage return in the text input field, which indicates the
        // message/command in the chat input field to be sent to the server
        // Make a request packet
        extractMessageInfo();
        clientEnd.writeStream(socket, message + "|" + name);
        // clear the GUI input field, using a utility function of ChatGUI
        chatGUI.clearInput();
    }

    public void run(){
        chatGUI = new ChatGUI(this, name);
        message = "/handshake" + "|" + name;
        clientEnd.writeStream(socket, message);
        // send the message
        do {
            receivedMessage = clientEnd.readStream(socket);
            if(receivedMessage != null){
                chatGUI.displayMessage(receivedMessage);
            }
        }while(true);
    }

    private EndPoint clientEnd;
    private InetAddress serverAddress = null;
    private int serverPortNumber;
    private String requestMessage;
    private String name;
    private ChatGUI chatGUI;
    private String command;
    private String message;
    private String receivedMessage;
    private Socket socket;
}
