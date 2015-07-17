package l1j.server.server.TimeController;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import l1j.server.Config;
import l1j.server.SpecialEventHandler;


public class BuffTimeController implements Runnable {
private static BuffTimeController _instance;

public static BuffTimeController getInstance() {
if (_instance == null) {
_instance = new BuffTimeController();
}
return _instance;
}
public void run() {
try {
while (true) {
checkBuffTime(); 
Thread.sleep(60000);
}
} catch (Exception e1) {
}
}
private Calendar getRealTime() {
TimeZone _tz = TimeZone.getTimeZone(Config.TIME_ZONE);
Calendar cal = Calendar.getInstance(_tz);
return cal;
}
private void checkBuffTime() {
SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
int nowtime = Integer.valueOf(sdf.format(getRealTime().getTime()));

int BuffTime = Config.CLAN_BUFFTIME; 
if (nowtime % BuffTime == 0) { 
SpecialEventHandler.getInstance().clanBuff();
}
return;

}
}