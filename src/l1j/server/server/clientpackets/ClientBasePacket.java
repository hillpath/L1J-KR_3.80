/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.clientpackets;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ClientBasePacket {

	private static Logger _log = Logger.getLogger(ClientBasePacket.class
			.getName());

	private byte _decrypt[];

	private int _off;

	public ClientBasePacket(byte abyte0[]) {
		_log.finest("type=" + getType() + ", len=" + abyte0.length);
		_decrypt = abyte0;
		_off = 1;
	}

	public int readD() {
		int i = _decrypt[_off++] & 0xff;
		i |= _decrypt[_off++] << 8 & 0xff00;
		i |= _decrypt[_off++] << 16 & 0xff0000;
		i |= _decrypt[_off++] << 24 & 0xff000000;
		return i;
	}

	public int readC() {
		int i = _decrypt[_off++] & 0xff;
		return i;
	}

	public int readH() {
		int i = _decrypt[_off++] & 0xff;
		i |= _decrypt[_off++] << 8 & 0xff00;
		return i;
	}

	public int readCH() {
		int i = _decrypt[_off++] & 0xff;
		i |= _decrypt[_off++] << 8 & 0xff00;
		i |= _decrypt[_off++] << 16 & 0xff0000;
		return i;
	}

	public double readF() {
		long l = _decrypt[_off++] & 0xff;
		l |= _decrypt[_off++] << 8 & 0xff00;
		l |= _decrypt[_off++] << 16 & 0xff0000;
		l |= _decrypt[_off++] << 24 & 0xff000000;
		l |= (long) _decrypt[_off++] << 32 & 0xff00000000L;
		l |= (long) _decrypt[_off++] << 40 & 0xff0000000000L;
		l |= (long) _decrypt[_off++] << 48 & 0xff000000000000L;
		l |= (long) _decrypt[_off++] << 56 & 0xff00000000000000L;
		return Double.longBitsToDouble(l);
	}

	public String readS() {
		String s = null;
		try {
			s = new String(_decrypt, _off, _decrypt.length - _off, "EUC-KR");
			s = s.substring(0, s.indexOf('\0'));
			_off += s.getBytes("EUC-KR").length + 1;
		} catch (Exception e) {
			_log.log(Level.SEVERE, "OpCode=" + (_decrypt[0] & 0xff), e);
		}
		return s;
	}

	@SuppressWarnings("finally")
	public String readSS() {
		String text = null;
		int loc = 0;
		int start = 0;
		try {
			start = _off;
			while (readH() != 0) {
				loc += 2;
			}
			StringBuffer test = new StringBuffer();
			do {
				if ((_decrypt[start] & 0xff) >= 127
						|| (_decrypt[start + 1] & 0xff) >= 127) {
					/** 한글 * */
					byte[] t = new byte[2];
					t[0] = _decrypt[start + 1];
					t[1] = _decrypt[start];
					test.append(new String(t, 0, 2, "EUC-KR"));
				} else {
					/** 영문&숫자 * */
					test.append(new String(_decrypt, start, 1, "EUC-KR"));
				}
				start += 2;
				loc -= 2;
			} while (0 < loc);

			text = test.toString();
		} catch (Exception e) {
			text = null;
		} finally {
			return text;
		}
	}

	public byte[] readByte() {
		byte[] result = new byte[_decrypt.length - _off];
		try {
			System.arraycopy(_decrypt, _off, result, 0, _decrypt.length - _off);
			_off = _decrypt.length;
		} catch (Exception e) {
			_log.log(Level.SEVERE, "OpCode=" + (_decrypt[0] & 0xff), e);
		}
		return result;
	}

	public byte[] readB() {
		byte[] BYTE = new byte[_decrypt.length - _off];
		System.arraycopy(_decrypt, _off, BYTE, 0, BYTE.length);
		_off += (BYTE.length + 1);
		return BYTE;
	}

	/**
	 * 클라이언트 패킷의 종류를 나타내는 캐릭터 라인을 돌려준다. ("[C] C_DropItem" 등 )
	 */
	public String getType() {
		return "[C] " + this.getClass().getSimpleName();
	}
}
