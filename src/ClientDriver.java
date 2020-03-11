public class ClientDriver {
public static void main(String[] args){
    String serverString = "localhost";
    int serverPort = 123;
    int numberOfClients = 2;
    String name = "matrosen";

    Client client;
    Client client2;
    Client client3;

    client = new Client(serverString, serverPort, "Hello from client " + name, name);

    client.start();

    client2 = new Client(serverString, serverPort, "Hello from client " + "test", "test");

    client2.start();

    client3 = new Client(serverString, serverPort, "Hello from client " + "Felix", "Felix");

    client3.start();
    }
}
