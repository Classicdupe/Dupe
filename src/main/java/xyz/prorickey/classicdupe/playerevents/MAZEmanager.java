package xyz.prorickey.classicdupe.playerevents;

public class MAZEmanager {

    //AUTO START
    public static boolean AutoStart = false;
    public static int BetweenAutoStarts = 60 * 60;


    //BASIC SETTINGS
    public static int CountDownTilStart = 30;
    public static int MaxLength = 15 * 60;

    //MAZE DATA
    public static int lastMazeRan = getDateNumber();

    //THREAD HANDLERS
    public static boolean AutoManagerThread = true;
    public static Thread AMThread;


    public MAZEmanager() {
        if (AutoStart) {

            //THREAD OF the method auto manager and the thread's var name is AMThread
            AMThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (AutoManagerThread) {
                        if (AutoManagerThread == false) {
                            AMThread.interrupt();
                            break;
                        }
                        AutoManager();
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            });
            if (AutoManagerThread) {
                AMThread.start();
            }



        }
    }

    //AUTO MANAGER
    public static void AutoManager() {

    }


    //GET SECONDS FROM TWO DATE NUMBERS
    public static int getSecondsFromTwoDateNumbers(int date1, int date2) {
        int seconds = 0;
        int difference = date1 - date2;
        if (difference > 0) {
            seconds = difference;
        } else {
            seconds = difference * -1;
        }
        return seconds;
    }

    //GET DATE NUMBER
    public static int getDateNumber() {
        int date = 0;
        date = (int) (System.currentTimeMillis() / 1000);
        return date;
    }
}
