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
package l1j.server.server.model;

public class L1TaxCalculator {
	/**
	 * 전쟁세는15% 고정
	 */
	private static final int WAR_TAX_RATES = 0;

	/**
	 * 국세는10% 고정(지역세에 대한 비율)
	 */
	private static final int NATIONAL_TAX_RATES = 10;

	/**
	 * 디아드세는10% 고정(전쟁세에 대한 비율)
	 */
	private static final int DIAD_TAX_RATES = 10;

	private final int _taxRatesCastle;

	private final int _taxRatesTown;

	private final int _taxRatesWar = WAR_TAX_RATES;

	/**
	 * @param merchantNpcId
	 *            계산 대상 상점의 NPCID
	 */
	public L1TaxCalculator(int merchantNpcId) {
		_taxRatesCastle = L1CastleLocation
				.getCastleTaxRateByNpcId(merchantNpcId);
		_taxRatesTown = L1TownLocation.getTownTaxRateByNpcid(merchantNpcId);
	}

	public int calcTotalTaxPrice(int price) {
		int taxCastle = (price * _taxRatesCastle) / 100;
		int taxTown = (price * _taxRatesTown) / 100;
		int taxWar = (price * WAR_TAX_RATES) / 100;
		return taxCastle + taxTown + taxWar;
	}

	// XXX 개별적으로 계산하기 때문에(위해), 둥근 오차가 나온다.
	public int calcCastleTaxPrice(int price) {
		return (price * _taxRatesCastle) / 100 - calcNationalTaxPrice(price);
	}

	public int calcNationalTaxPrice(int price) {
		return (price * _taxRatesCastle) / 100 / (100 / NATIONAL_TAX_RATES);
	}

	public int calcTownTaxPrice(int price) {
		return (price * _taxRatesTown) / 100;
	}

	public int calcWarTaxPrice(int price) {
		return (price * _taxRatesWar) / 100;
	}

	public int calcDiadTaxPrice(int price) {
		return (price * _taxRatesWar) / 100 / (100 / DIAD_TAX_RATES);
	}

	/**
	 * 과세 후의 가격을 요구한다.
	 * 
	 * @param price
	 *            과세전의 가격
	 * @return 과세 후의 가격
	 */
	public int layTax(int price) {
		return price + calcTotalTaxPrice(price);
	}

	/**
	 * 세금 없는 NPC에 대한 기본세율 부가
	 * 
	 * @param price
	 *            세금부과 전 가격
	 * @return 세금 부화 후 가격
	 */
	public int NoTaxPrice(int price) {
		return price + calcWarTaxPrice(price);
	}
}
