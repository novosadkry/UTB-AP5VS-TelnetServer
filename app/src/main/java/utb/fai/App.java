package utb.fai;

import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class App {

    public static void main(String[] args) {
        int port = 12345;
        int max_threads = 10;
        ExecutorService executor;

        if (args.length >= 2) {
            try {
                port = Integer.parseInt(args[0]);
                max_threads = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid arguments. Usage: <port> <max_threads>");
                return;
            }
        }

        if (max_threads > 0) {
            executor = java.util.concurrent.Executors.newFixedThreadPool(max_threads);
        } else {
            executor = java.util.concurrent.Executors.newCachedThreadPool();
        }

        try (var serverSocket = new ServerSocket(port)) {
            while (true) {
                var incoming = serverSocket.accept();
                System.out.println("[CLIENT JOIN]: " + incoming.getInetAddress().getHostName());

                var clientThread = new ClientThread(incoming);
                executor.submit(clientThread);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
        }
    }
}
