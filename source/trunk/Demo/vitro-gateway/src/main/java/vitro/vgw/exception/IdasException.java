/*
 * #--------------------------------------------------------------------------
 * # Copyright (c) 2013 VITRO FP7 Consortium.
 * # All rights reserved. This program and the accompanying materials
 * # are made available under the terms of the GNU Lesser Public License v3.0 which accompanies this distribution, and is available at
 * # http://www.gnu.org/licenses/lgpl-3.0.html
 * #
 * # Contributors:
 * #     Antoniou Thanasis (Research Academic Computer Technology Institute)
 * #     Paolo Medagliani (Thales Communications & Security)
 * #     D. Davide Lamanna (WLAB SRL)
 * #     Alessandro Leoni (WLAB SRL)
 * #     Francesco Ficarola (WLAB SRL)
 * #     Stefano Puglia (WLAB SRL)
 * #     Panos Trakadas (Technological Educational Institute of Chalkida)
 * #     Panagiotis Karkazis (Technological Educational Institute of Chalkida)
 * #     Andrea Kropp (Selex ES)
 * #     Kiriakos Georgouleas (Hellenic Aerospace Industry)
 * #     David Ferrer Figueroa (Telefonica Investigación y Desarrollo S.A.)
 * #
 * #--------------------------------------------------------------------------
 */
package vitro.vgw.exception;

public class IdasException extends VitroGatewayException {

	private String code;
	private String locator;
	
	public IdasException(String code, String locator) {
		this.code = code;
		this.locator = locator;
	}

	public IdasException(String arg0, String code, String locator) {
		super(arg0);
		this.code = code;
		this.locator = locator;
	}

	public IdasException(Throwable arg0, String code, String locator) {
		super(arg0);
		this.code = code;
		this.locator = locator;
	}

	public IdasException(String arg0, Throwable arg1, String code, String locator) {
		super(arg0, arg1);
		this.code = code;
		this.locator = locator;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getLocator() {
		return locator;
	}

	public void setLocator(String locator) {
		this.locator = locator;
	}

	@Override
	public String getMessage() {
		return super.getMessage() + ";  code = "  + code + "; locator = " + locator;
	}
	
	
	
	

}
