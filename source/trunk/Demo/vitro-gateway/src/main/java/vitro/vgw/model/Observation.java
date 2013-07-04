/*******************************************************************************
 * Copyright (c) 2013 VITRO FP7 Consortium.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Contributors:
 *     Antoniou Thanasis
 *     Paolo Medagliani
 *     D. Davide Lamanna
 *     Panos Trakadas
 *     Andrea Kropp
 *     Kiriakos Georgouleas
 *     Panagiotis Karkazis
 *     David Ferrer Figueroa
 *     Francesco Ficarola
 *     Stefano Puglia
 ******************************************************************************/
package vitro.vgw.model;

public class Observation {
	
	private Node node;
	
	private Resource resource;
	
	private String value;
	private String uom;
	
	private long timestamp;

    public Observation() {
        resource = null;
        value = "";
        uom = Resource.UOM_DIMENSIONLESS;
    }


	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public String getValue() {
        if(resource != null && value == null){
            value = "";
        }
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getUom() {
        if(resource != null && uom == null){
            uom = resource.getUnityOfMeasure();
		}
        else if (resource == null) {
            uom = Resource.UOM_DIMENSIONLESS;
        }
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}
	
	
	
	

}
