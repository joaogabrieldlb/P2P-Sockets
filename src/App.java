import Utils.P2PFactory;

public class App {
    public static void main(String[] args) throws Exception {
        if (args.length == 1 || args.length == 3) {
            printHelpMessage();
            System.exit(1);
        }

        switch(args[0].toUpperCase()) {
            case "SERVER":
                P2PFactory.createP2PServer(args[1]).run();
            case "CLIENT":
                P2PFactory.createP2PClient(args[1], args[2], args[3]).run();
            default:
                printHelpMessage();
        }
        
        System.exit(0);
    }

    public static void printHelpMessage() {
        System.out.println("\n\tUsage: java App server <port> | java App client <local_port> <server_ip> <server_port>\n");
    }
}