import Utils.P2PFactory;

public class App {
    public static void main(String[] args) throws Exception {
        if (args.length < 3 && args.length > 5) {
            printCommandMessage();
            System.exit(1);
        }

        switch(args[0].toUpperCase()) {
            case "SERVER":
                P2PFactory.createP2PServer().run();
            case "CLIENT":
                P2PFactory.createP2PClient(args[1], args[2]).run();
            default:
                printCommandMessage();
        }
        
        System.exit(0);
    }

    public static void printCommandMessage() {
        System.out.println("\n\tUsage: java App server <machine_ip> <port> | java App client <machine_ip> <port> <server_ip> <server_port>\n");
    }
}