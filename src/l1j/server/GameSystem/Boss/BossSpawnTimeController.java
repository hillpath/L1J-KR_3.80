package l1j.server.GameSystem.Boss;

import java.util.Calendar;
import java.util.Random;

import l1j.server.server.datatables.BossSpawnTable;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.gametime.BaseTime;
import l1j.server.server.model.gametime.RealTime;
import l1j.server.server.model.gametime.RealTimeClock;
import l1j.server.server.model.gametime.TimeListener;
import l1j.server.server.serverpackets.S_SystemMessage;

public class BossSpawnTimeController implements TimeListener{
	private static BossSpawnTimeController _instance;
	
	public static void start() {
		if (_instance == null) {
			_instance = new BossSpawnTimeController();
		}
		_instance.init();
		RealTimeClock.getInstance().addListener(_instance);
	}
		
	private void init() {
		RealTime time = RealTimeClock.getInstance().getRealTime();
		try{
		for(L1BossCycle b : L1BossCycle.getBossCycleList()) {
			if(b == null)continue;
			try{
			if(b.getBaseDay() == 0)
				b.setBaseDay(time.get(Calendar.DAY_OF_MONTH));
			setBaseTime(b, time);
			setNextBossSpawnTime(b);
			}catch(Exception e){}
		}
		}catch(Exception e){
			System.out.println(e+"BossSpawnTimeController init() Error");
		}
	}
	
	private void setBaseTime(L1BossCycle b, RealTime time) {
		if(b == null || time == null)return;
		try{
		final int H = time.get(Calendar.HOUR_OF_DAY);
		final int M = time.get(Calendar.MINUTE);
		boolean isOverDay = false; 
		
		for(;b.getBaseHour() < H && !isOverDay;) {
			int nM = (b.getBaseMinute() + b.getPeriodMinute());
			int nH = (b.getBaseHour() + b.getPeriodHour() + (nM / 60));
			
			if((nH / 24) >= 1) isOverDay = true;
			
			b.setBaseHour(nH % 24);
			b.setBaseMinute(nM % 60);
		}
		
		for(; b.getBaseHour() == H && b.getBaseMinute() <= M && b.getPeriodMinute() > 0;) {
			int plusM = b.getBaseMinute() + b.getPeriodMinute();
			if(plusM < 60)		b.setBaseMinute(plusM);
			else				b.setBaseHour(b.getBaseHour() + 1);
		}
		
		setNextBossSpawnTime(b);
		}catch(Exception e){
			System.out.println(e+"BossSpawnTimeController setBaseTime() Error");
		}
	}

	private void setNextBossSpawnTime(L1BossCycle b) {	
		if(b == null){ return; }
		try{
		Random rnd = new Random(System.nanoTime());
		int newH = 0, newM = 0;
		newH = b.getBaseHour() + b.getStartHour();
		newM = b.getBaseMinute() + b.getStartMinute();

		int eH = b.getEndHour();
		int eM = b.getEndMinute();
		int rndr = (eH*60+eM)-(b.getStartHour()*60+b.getStartMinute());
		rndr = rndr-(rndr/4);
		int rndM = 0;
		// 뜨는 시간 랜덤 설정
		if(rndr>0) rndM = rnd.nextInt(rndr);
		else rndM = rnd.nextInt(10);
		newH += rndM / 60;
		newM += rndM % 60;
		
		newH += newM / 60;
		newM %= 60;
		newH %= 24;
		b.setKillHour(b.getNewKillHour());
		b.setKillMinute(b.getNewKillMinute());
		
		int kM = b.getBaseMinute() + b.getEndMinute();
		int kH = b.getBaseHour() + b.getEndHour() + (kM / 60);
		kM %= 60;
		kH %= 24;
		
		b.setNewKillHour(kH);
		b.setNewKillMinute(kM);
		
		int plusH = (b.getBaseMinute() + b.getPeriodMinute()) / 60;
		b.setBaseMinute((b.getBaseMinute() + b.getPeriodMinute()) % 60);
		b.setBaseHour((b.getBaseHour() + b.getPeriodHour() + plusH) % 24); 
		// 새로운 스폰 시간 설정
		b.setNextSpawnHour(newH);
		b.setNextSpawnMinute(newM);
		}catch(Exception e){
			System.out.println(e+"BossSpawnTimeController setNextBossSpawnTime() Error");
		}
		
	}

	@Override
	public void onDayChanged(BaseTime time) {}

	@Override
	public void onHourChanged(BaseTime time) {}

	@Override
	public void onMinuteChanged(BaseTime time) {
		if(time == null){
			return;
		}
		try{
		final int H = time.get(Calendar.HOUR_OF_DAY);
		final int M = time.get(Calendar.MINUTE);
		int sH, sM;
		
		for(L1BossCycle b : L1BossCycle.getBossCycleList()) {
			try{
			if(b == null || b.getName() == null || b.getName() == ""){
				continue;
			}
			if(H == b.getKillHour() && M == b.getKillMinute()) {
			//	eva.LogServerAppend(" ["+time.get(Calendar.HOUR)+"시"+time.get(Calendar.MINUTE)+"분] "+b.getName() , "[알림] 보스 타임이 종료되었습니다" );
				//BossSpawnTable.killBoss(b.getName());
			}
			
			sH = b.getNextSpawnHour();
			sM = b.getNextSpawnMinute();
			
			if(sH == H && sM == M) {
			//	eva.LogServerAppend(" ["+time.get(Calendar.HOUR)+"시"+time.get(Calendar.MINUTE)+"분] "+b.getName(),  "[알림] 보스 타임이 시작되었습니다");
				BossSpawnTable.spawnBoss(b.getName());
				setNextBossSpawnTime(b); // 스폰 타임 재설정
			}
			}catch(Exception e){}
		}
		}catch(Exception e){
			System.out.println(e+"BossSpawnTimeController onMinuteChanged() Error");
		}
	}

	@Override
	public void onMonthChanged(BaseTime time) {}
	public static void getBossTime(L1PcInstance pc){		
		for(L1BossCycle b : L1BossCycle.getBossCycleList()){
			pc.sendPackets(new S_SystemMessage("["+b.getName()+"] "+b.getNextSpawnDay()+"일 "+b.getNextSpawnHour()+"시 "+b.getNextSpawnMinute()+"분"));			
		}
	}
}

