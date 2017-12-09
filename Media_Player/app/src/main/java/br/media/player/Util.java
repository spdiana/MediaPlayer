package br.media.player;

import java.util.concurrent.TimeUnit;

/**
 * Created by pc on 15/11/2017.
 */

public class Util {

    public static String timeConvert(long time) {

        long hrs = TimeUnit.MILLISECONDS.toHours(time);
        long mns = TimeUnit.MILLISECONDS.toMinutes(time) -  TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time));
        long scs = TimeUnit.MILLISECONDS.toSeconds(time) -  TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time));

        return String.format("%02d:%02d:%02d", hrs,  mns, scs);
    }
}
