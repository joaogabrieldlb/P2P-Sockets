import Utils.P2PFactory;

public class App {
    public static void main(String[] args) throws Exception {
        if (args.length < 3 && args.length > 4) {
            printHelpMessage();
            System.exit(1);
        }

        switch(args[0].toUpperCase()) {
            case "SERVER":
                P2PFactory.createP2PServer().run();
            case "CLIENT":
                P2PFactory.createP2PClient(args[1], args[2], args[3]).run();
            default:
                printHelpMessage();
        }
        
        System.exit(0);
    }

    public static void printHelpMessage() {
        System.out.println("\n\tUsage: java App server <local_ip> <port> | java App client <local_port> <server_ip> <server_port>\n");
    }
}