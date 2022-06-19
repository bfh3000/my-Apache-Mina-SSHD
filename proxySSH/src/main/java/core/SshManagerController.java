package core;

public class SshManagerController extends Thread {
    public static void main(String[] args) throws InterruptedException {
        
        //start SShdServer
//        StartSSHServer sshS = new StartSSHServer();
//        sshS.start();

        StartSSHClient sshC = new StartSSHClient();
        sshC.start();

        while(true){
            Thread.sleep(10000);
        }
    }
}