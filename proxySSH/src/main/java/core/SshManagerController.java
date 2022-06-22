package core;

public class SshManagerController extends Thread {
    public static void main(String[] args) throws InterruptedException {
        
        StartSSHServer sshS = new StartSSHServer();
        sshS.start();

        while (true){
            Thread.sleep(100);
        }
    }
}