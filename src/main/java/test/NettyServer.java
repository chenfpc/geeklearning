package test;

public class NettyServer {
    public static void main(String[] args) {
        Thread thread = new Thread(()->{
            System.out.println("sleep之前,interrupted="+Thread.interrupted());
            try {
                Thread.sleep(11000l);
            } catch (InterruptedException e) {
                System.out.println("sleep been interrupted");
                e.printStackTrace();
            }
            System.out.println("sleep之后,interrupted="+Thread.interrupted());

        });
        thread.start();
        thread.interrupt();
    }
}
