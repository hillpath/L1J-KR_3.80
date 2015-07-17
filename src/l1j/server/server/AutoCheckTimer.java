package l1j.server.server;

import java.util.Random;

import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_Disconnect;

public class AutoCheckTimer implements Runnable {

	private static Random r = new Random();

	public static int aCode;

	private static AutoCheckTimer _instance;

	public static AutoCheckTimer getInstance() {
		if (_instance == null) {
			_instance = new AutoCheckTimer();
		}
		return _instance;
	}// �ν��Ͻ����

	public void run() {// ������ ����

		try {

			while (true) {// �ݺ�

				aCode = r.nextInt(9000) + 1000; // 1000~9999���� �������� Code
				// ����//ŸŬ�������� ��ӵǵ��� ���� Code ��������
				// System.out.println("[AutoCheckTimer]�� �� ü Ʈ �� �� �� ��
				// ��OK!");//���� ����Ȯ��
				Thread.sleep(60000 * 2);
				SetnCheck();// SetnCheck �޼ҵ带 ����
				Thread.sleep(60000 * 5);// �׵ڷ� 5�� �� ���
				Redo();// �̹��� pc isAuto �ƴϸ� �� �Ѿ�� ������ ���� �޽��� ġ��� ����
				Thread.sleep(60000 * 5);// �� 5�� ������
				Discon();// isAuto�� ��� ���� ����
				Thread.sleep(60000 * 60);// 30�е����� ����� ����. sleep()�� �����带 ���߰�
				// �ϴ� �޼ҵ�
			}

		} catch (Exception e) {
		}

	}// run()

	public void SetnCheck() {// ó���� ����Ǵ� �޼ҵ�
		// System.out.println("SetnChck �޼��� ����");//���� ����Ȯ�� �ǰ� ���� ���켼��
		for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {// 1.���峻��
																		// ������
																		// ���
			// ������ �˻�
			// ////////////////////////////////////////////////////////////////////////////�׽�Ʈ�ʿ�
			boolean AreaCK = false;
			for (int i = 0; i < 8; i++) { // �߰�
				int castle_id = i + 1;
				if (L1CastleLocation.checkInWarArea(castle_id, pc)) {
					AreaCK = true;
				}
			}

			if (!AreaCK) {
				if (!pc.getMap().isSafetyZone(pc.getLocation())
						&& !pc.isPinkName() && !pc.isGm()) {
					// /////////////////////////////////////////////////////////////////////////�׽�Ʈ�ʿ�
					pc.����Ȯ���ʿ���·ιٲٱ�();// 2.�������� ���������� �ʿ��� ���·� �����
					pc
							.sendPackets(new S_ChatPacket(pc, "[Authorization code] " + aCode
									+ "  Please enter a chat window.",
									Opcodes.S_OPCODE_NORMALCHAT, 2));
					;
				}
			}// SetnCheck
		}//
	}

	public void Redo() {// �Է��ڵ� �Է��϶�� �ϴ� �޼ҵ�
	// System.out.println("redo �޼��� ����");//���� ����Ȯ��
		for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {// 1.���峻
																		// ���
																		// ������
			// �˻��ϰ�
			// ////////////////////////////////////////////////////////////////////////////�׽�Ʈ�ʿ�
			boolean AreaCK = false;
			for (int i = 0; i < 8; i++) { // �߰�
				int castle_id = i + 1;
				if (L1CastleLocation.checkInWarArea(castle_id, pc)) {
					AreaCK = true;
				}
			}

			if (!AreaCK) {
				if (!pc.getMap().isSafetyZone(pc.getLocation())
						&& !pc.isPinkName() && !pc.isGm()) {
					// /////////////////////////////////////////////////////////////////////////�׽�Ʈ�ʿ�
					if (pc.����Ȯ�����ʿ��ѻ��������Լ�()) {// 2.���� ���� �ȵ� �������
						pc.sendPackets(new S_ChatPacket(pc, "[�����ڵ�] " + aCode
								+ "  �� ä��â�� �Է��ϼ���.",
								Opcodes.S_OPCODE_NORMALCHAT, 2));
						;
						// ��Ŷ��
						// ������

					}
				}
			}
		}
	}

	// Redo

	public void Discon() {// ¥����
	// System.out.println("discon �޼��� ����");//���� ����Ȯ��
		for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
			if (pc.����Ȯ�����ʿ��ѻ��������Լ�()) {
				pc.sendPackets(new S_Disconnect());
			} // ���� ���� �ȵ��� �� ����
		}
	}// Discon
}