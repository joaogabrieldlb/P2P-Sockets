import java.net.SocketException;
import java.net.UnknownHostException;

import Utils.P2PFactory;

public class App {
    public static void main(String[] args) {
        if (!(args.length == 2 || args.length == 4)) {
            printHelpMessage();
            System.exit(1);
        }

        switch(args[0].toUpperCase()) {
            case "SERVER":
                P2PFactory.createP2PServer().run();
                break;
            case "CLIENT":
                try{
                    P2PFactory.createP2PClient(args[1], args[2], args[3]).run();
                }  catch(UnknownHostException ex) {
                    System.out.println("\n\tERROR: Invalid machine address!\n");
                } catch(NumberFormatException ex) {
                    System.out.println("\n\tERROR: Invalid local port!\n");
                } catch(SocketException ex) {
                    System.out.println("\n\tERROR: Port already being used!\n");
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
                break;
            default:
                printHelpMessage();
        }
        
        System.exit(0);
    }

    public static void printHelpMessage() {
        System.out.println("\n\tUsage: java App server <port> | java App client <local_port> <server_ip> <server_port>\n");
    }
}