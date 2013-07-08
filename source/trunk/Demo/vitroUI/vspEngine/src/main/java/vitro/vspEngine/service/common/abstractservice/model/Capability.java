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
package vitro.vspEngine.service.common.abstractservice.model;

import vitro.vspEngine.service.persistence.DBSelectionOfGateways;
import vitro.vspEngine.service.persistence.DBSelectionOfRegions;
import vitro.vspEngine.service.persistence.DBSelectionOfSmartNodes;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Capability {

    public static final int defaultSamplingPeriod = 60; //seconds
    public static final int minSamplingPeriod = 30; //seconds

    public static final String defaultThresholdSign = "";   //meaning monitor only values higher than/lower than/equal etc (not for triggers)
    public static final String NO_THRESHOLD_VALUE = "";
    public static final String defaultThresholdValue = NO_THRESHOLD_VALUE;
    public static final String defaultTriggerAction = "notifyAndContinue";
    public static final String NO_TRIGGER_ACTION = "";
    public static final String defaultTriggerActuationName = "";
    public static final String WITH_TRIGGER = "YES";
    public static final String WITHOUT_TRIGGER = "NO";
    public static final String defaultHasTrigger = WITHOUT_TRIGGER;

    public static final String defaultTriggerActuationNodes = "";
    public static final String defaultTriggerActuationValue = "";
    public static final String defaultTriggerConditionSign = "gt"; // this is for triggering alerts
    public static final String NO_TRIGGER_CONDITION_SIGN = ""; // when no trigger is set

    public static final String defaultTriggerGenTextValue= "";



    private String name;
	private String function;

    private String functionThresholdSign;
    private String functionThresholdValue;
    private String hasTrigger;
    private String triggerConditionSign;
    private String triggerConditionValue;
    private String triggerAction;
    private String triggerGenTextValue;
    private String triggerActuationName;
    private String triggerActuationValue;
    private String triggerActuationNodes;
    private String functionSamplingFrequency;




    public Capability(){
		super();
	}
	
	public Capability(String name, String function) {
		super();
		this.name = name;
		this.function = function;
        this.setFunctionThresholdSign("");
        this.setFunctionThresholdValue("");
        this.setFunctionSamplingFrequency("");
        this.setHasTrigger("");
        this.setTriggerConditionSign("");
        this.setTriggerConditionValue("");
        this.setTriggerAction("");
        this.setTriggerGenTextValue("");
        this.setTriggerActuationName("");
        this.setTriggerActuationValue("");
        this.setTriggerActuationNodes("");
        setDBSelectionOfRegionsList(new ArrayList<DBSelectionOfRegions>());
        setDBSelectionOfGatewaysList(new ArrayList<DBSelectionOfGateways>());
        setDBSelectionOfSmartNodesList(new ArrayList<DBSelectionOfSmartNodes>());
	}

    public Capability(String pname, String pfunction,
                      String pfunctionThresholdSign, String pfunctionThresholdValue,
                      String phasTrigger, String ptriggerConditionSign, String ptriggerConditionValue, String ptriggerAction, String ptriggerGenTextValue,
                      String ptriggerActuationName, String ptriggerActuationValue, String ptriggerActuationNodes) {
        super();
        this.name = pname;
        this.function = pfunction;
        this.setFunctionThresholdSign(pfunctionThresholdSign);
        this.setFunctionThresholdValue(pfunctionThresholdValue);
        this.setFunctionSamplingFrequency(Integer.toString(defaultSamplingPeriod)); // set it to one minute. Default
        this.setHasTrigger(phasTrigger);
        this.setTriggerConditionSign(ptriggerConditionSign);
        this.setTriggerConditionValue(ptriggerConditionValue);
        this.setTriggerAction(ptriggerAction);
        this.setTriggerGenTextValue(ptriggerGenTextValue);
        this.setTriggerActuationName(ptriggerActuationName);
        this.setTriggerActuationValue(ptriggerActuationValue);
        this.setTriggerActuationNodes(ptriggerActuationNodes);
        setDBSelectionOfRegionsList(new ArrayList<DBSelectionOfRegions>());
        setDBSelectionOfGatewaysList(new ArrayList<DBSelectionOfGateways>());
        setDBSelectionOfSmartNodesList(new ArrayList<DBSelectionOfSmartNodes>());
    }

    // TODO: we need another capability definition to include mapping to node/gateway/region selections

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	//Used for optimistic locking
	@Version
	private int version;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
    // TODO do we need separate Managers for these?. Was: ManyToMany, reverted to OneToMany
    @OneToMany(cascade = { CascadeType.MERGE})
    private List<DBSelectionOfSmartNodes> DBSelectionOfSmartNodesList;
    @OneToMany(cascade = { CascadeType.MERGE})
    private List<DBSelectionOfRegions> DBSelectionOfRegionsList;
    @OneToMany(cascade = { CascadeType.MERGE})
    private List<DBSelectionOfGateways> DBSelectionOfGatewaysList;

    public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
        boolean objectsEqual = false;
		if (this == obj)
        {
            objectsEqual= true;
			return objectsEqual;
        }
		if (obj == null)   {
            objectsEqual = false;
            return objectsEqual;
        }
		if (getClass() != obj.getClass()){
            objectsEqual = false;
            return objectsEqual;
        }
		Capability other = (Capability) obj;
		if (name == null) {
			if (other.name != null){
                objectsEqual = false;
                return objectsEqual;
            }
		} else if (!name.equals(other.name)){
            objectsEqual = false;
            return objectsEqual;
        }
        // TODO: Add comparison on selection of nodes/gateways/regions
        //          getDBSelectionOfRegionsList()
        //          getDBSelectionOfGatewaysList()
        //          getDBSelectionOfSmartNodesList()
        // TODO: comparison needs optimization
        if(getDBSelectionOfRegionsList().size() != getDBSelectionOfRegionsList().size() ||
                getDBSelectionOfGatewaysList().size() != other.getDBSelectionOfGatewaysList().size() ||
                getDBSelectionOfSmartNodesList().size() != other.getDBSelectionOfSmartNodesList().size() )
        {
            objectsEqual = false;
            return objectsEqual;
        }
        for (int i = 0; i < getDBSelectionOfRegionsList().size(); i++) {
             if(! getDBSelectionOfRegionsList().containsAll(other.getDBSelectionOfRegionsList()) ||
                !  other.getDBSelectionOfRegionsList().containsAll(getDBSelectionOfRegionsList())  )
             {
                 objectsEqual = false;
                 return objectsEqual;
             }
        }
        for (int i = 0; i < getDBSelectionOfGatewaysList().size(); i++) {
            if(! getDBSelectionOfGatewaysList().containsAll(other.getDBSelectionOfGatewaysList()) ||
                    !  other.getDBSelectionOfGatewaysList().containsAll(getDBSelectionOfGatewaysList())  )
            {
                objectsEqual = false;
                return objectsEqual;
            }

        }
        for (int i = 0; i < getDBSelectionOfSmartNodesList().size(); i++) {

            if(! getDBSelectionOfSmartNodesList().containsAll(other.getDBSelectionOfSmartNodesList()) ||
                    !  other.getDBSelectionOfSmartNodesList().containsAll(getDBSelectionOfSmartNodesList())  )
            {
                objectsEqual = false;
                return objectsEqual;
            }
        }
        objectsEqual = true;
        return objectsEqual;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Capability [name=");
		builder.append(name);
		builder.append(", function=");
		builder.append(function);
		builder.append(", id=");
		builder.append(id);
		builder.append(", version=");
		builder.append(version);
        builder.append(", functionThresholdSign=");
        builder.append(getFunctionThresholdSign());
        builder.append(", functionThresholdValue=");
        builder.append(getFunctionThresholdValue());
        builder.append(", functionSamplingFrequency=");
        builder.append(getFunctionSamplingFrequency());
        builder.append(", hasTrigger=");
        builder.append(getHasTrigger());
        builder.append(", triggerConditionSign=");
        builder.append(getTriggerConditionSign());
        builder.append(", triggerConditionValue=");
        builder.append(getTriggerConditionValue());
        builder.append(", triggerAction=");
        builder.append(getTriggerAction());
        builder.append(", triggerGenTextValue=");
        builder.append(getTriggerGenTextValue());
        builder.append(", triggerActuationName=");
        builder.append(getTriggerActuationName());
        builder.append(", triggerActuationValue=");
        builder.append(getTriggerActuationValue());
        builder.append(", triggerActuationNodes=");
        builder.append(getTriggerActuationNodes());

        // TODO: Listing of selection of nodes/gateways/regions (later)
		builder.append("]");

		return builder.toString();
	}

     // additional getters
    public String getFunctionThresholdSign() {
        return functionThresholdSign;
    }

    public void setFunctionThresholdSign(String functionThresholdSign) {
        this.functionThresholdSign = functionThresholdSign;
    }

    public String getFunctionThresholdValue() {
        return functionThresholdValue;
    }

    public void setFunctionThresholdValue(String functionThresholdValue) {
        this.functionThresholdValue = functionThresholdValue;
    }

    public String getHasTrigger() {
        return hasTrigger;
    }

    public void setHasTrigger(String hasTrigger) {
        this.hasTrigger = hasTrigger;
    }

    public String getTriggerConditionSign() {
        return triggerConditionSign;
    }

    public void setTriggerConditionSign(String triggerConditionSign) {
        this.triggerConditionSign = triggerConditionSign;
    }

    public String getTriggerConditionValue() {
        return triggerConditionValue;
    }

    public void setTriggerConditionValue(String triggerConditionValue) {
        this.triggerConditionValue = triggerConditionValue;
    }

    public String getTriggerAction() {
        return triggerAction;
    }

    public void setTriggerAction(String triggerAction) {
        this.triggerAction = triggerAction;
    }

    public String getTriggerGenTextValue() {
        return triggerGenTextValue;
    }

    public void setTriggerGenTextValue(String triggerGenTextValue) {
        this.triggerGenTextValue = triggerGenTextValue;
    }

    public String getTriggerActuationName() {
        return triggerActuationName;
    }

    public void setTriggerActuationName(String triggerActuationName) {
        this.triggerActuationName = triggerActuationName;
    }

    public String getTriggerActuationValue() {
        return triggerActuationValue;
    }

    public void setTriggerActuationValue(String triggerActuationValue) {
        this.triggerActuationValue = triggerActuationValue;
    }

    public String getTriggerActuationNodes() {
        return triggerActuationNodes;
    }

    public void setTriggerActuationNodes(String triggerActuationNodes) {
        this.triggerActuationNodes = triggerActuationNodes;
    }

    public List<DBSelectionOfSmartNodes> getDBSelectionOfSmartNodesList() {
        return DBSelectionOfSmartNodesList;
    }

    public void setDBSelectionOfSmartNodesList(List<DBSelectionOfSmartNodes> DBSelectionOfSmartNodesList) {
        this.DBSelectionOfSmartNodesList = DBSelectionOfSmartNodesList;
    }

    public List<DBSelectionOfRegions> getDBSelectionOfRegionsList() {
        return DBSelectionOfRegionsList;
    }

    public void setDBSelectionOfRegionsList(List<DBSelectionOfRegions> DBSelectionOfRegionsList) {
        this.DBSelectionOfRegionsList = DBSelectionOfRegionsList;
    }

    public List<DBSelectionOfGateways> getDBSelectionOfGatewaysList() {
        return DBSelectionOfGatewaysList;
    }

    public void setDBSelectionOfGatewaysList(List<DBSelectionOfGateways> DBSelectionOfGatewaysList) {
        this.DBSelectionOfGatewaysList = DBSelectionOfGatewaysList;
    }

    public String getFunctionSamplingFrequency() {
        return functionSamplingFrequency;
    }

    public void setFunctionSamplingFrequency(String functionSamplingFrequency) {
        this.functionSamplingFrequency = functionSamplingFrequency;
    }
}
