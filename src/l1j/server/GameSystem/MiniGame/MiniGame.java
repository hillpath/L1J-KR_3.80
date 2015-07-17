package l1j.server.GameSystem.MiniGame;

import javolution.util.FastTable;
import java.util.Random;

import l1j.server.server.model.Instance.L1PcInstance;

public abstract class MiniGame {

	public enum Status {
		ENTERREADY, READY, PLAY, END, REST
	};

	public Status MiniGameStatus;

	protected final Random _random = new Random(System.nanoTime());

	protected FastTable<L1PcInstance> entermembers = new FastTable<L1PcInstance>();

	protected FastTable<L1PcInstance> playmembers = new FastTable<L1PcInstance>();

	protected static final short DEATHMATCH_MAPID = 5153;

	protected static final short PETRACE_MAPID = 5143;

	protected static final short GHOSTHOUSE_MAPID = 5140;

	protected abstract void addWaitListMember(L1PcInstance pc);

	protected abstract void addPlayMember(L1PcInstance pc);

	protected abstract void giveBackAdena(L1PcInstance pc);

	protected abstract void StartMiniGame();

	protected abstract void ReadyMiniGame();

	protected abstract void NoReadyMiniGame();

	protected abstract void remainOnlyWinner();

	protected abstract void EndMiniGame();

	protected abstract void ClearMiniGame();

	protected void setMiniGameStatus(Status i) {
		MiniGameStatus = i;
	}

	public Status getMiniGameStatus() {
		return MiniGameStatus;
	}

	public void addEnterMember(L1PcInstance pc) {
		entermembers.add(pc);
	}

	public void removeEnterMember(L1PcInstance pc) {
		entermembers.remove(pc);
	}

	public void clearEnterMember() {
		entermembers.clear();
	}

	public boolean isEnterMember(L1PcInstance pc) {
		return entermembers.contains(pc);
	}

	public int getEnterMemberCount() {
		return entermembers.size();
	}

	public void addPlayerMember(L1PcInstance pc) {
		playmembers.add(pc);
	}

	public void removePlayerMember(L1PcInstance pc) {
		playmembers.remove(pc);
	}

	public void clearPlayerMember() {
		playmembers.clear();
	}

	public boolean isPlayerMember(L1PcInstance pc) {
		return playmembers.contains(pc);
	}

	public int getPlayerMemberCount() {
		return playmembers.size();
	}
}
