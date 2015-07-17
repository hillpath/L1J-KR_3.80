package l1j.server.server.model.Instance;

import l1j.server.server.templates.L1Npc;

public class L1ModelInstance extends L1NpcInstance {

	private static final long serialVersionUID = 1L;

	public L1ModelInstance(L1Npc template) {
		super(template);
	}

	@Override
	public void onAction(L1PcInstance pc) {
	}

	@Override
	public void deleteMe() {
	}
}
