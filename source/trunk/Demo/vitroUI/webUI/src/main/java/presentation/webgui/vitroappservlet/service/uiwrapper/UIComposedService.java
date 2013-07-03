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
package presentation.webgui.vitroappservlet.service.uiwrapper;

import vitro.vspEngine.service.common.abstractservice.model.FullComposedService;
import vitro.vspEngine.service.query.IndexOfQueries;
import vitro.vspEngine.service.query.QueryDefinition;

import java.util.List;

/**
 * A class for UI purposes of presenting Full Composed Services
 */
public class UIComposedService {
    FullComposedService theComposedService;
    private String status;
    private String uniqueQid;
    private String currentSamplingPeriod;
    private String appPath;

    public UIComposedService(FullComposedService pComposedService){
        this.theComposedService = pComposedService;
        this.setAppPath("");
    }

    public FullComposedService getComposedService() {
        return theComposedService;
    }

    public void setComposedService(FullComposedService pComposedService) {
        this.theComposedService = pComposedService;
    }

    public String getSearchTagsString(){
        StringBuffer sb = new StringBuffer();

        List<String> sarchTagList = theComposedService.getSearchTagList();

        for (int i = 0; i < sarchTagList.size(); i++) {
            if(i != 0){
                sb.append(", ");
            }
            sb.append(sarchTagList.get(i).trim());
        }

        return sb.toString();
    }

    public String getStatus() {
        return status;
    }

    public String getStatusImgHtml() {
        if(getStatus().compareToIgnoreCase("start") == 0 ) { //means it is stopped
           return "<img title=\"Start service\" src=\""+getAppPath()+ "/img/startVSN52h.png\" style=\"height: 32px;width: 32px;\" />";
        } else if(getStatus().compareToIgnoreCase("stop") == 0 ) { //means it is running
           return "<img title=\"Pause service\" src=\""+getAppPath()+ "/img/pauseVSN52h.png\" style=\"height: 32px;width: 32px;\" />";
        }
        else{
            return "";
        }
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUniqueQid() {
        return uniqueQid;
    }

    public void setUniqueQid(String uniqueQid) {
        this.uniqueQid = uniqueQid;
    }

    public Boolean isDeployed() {

        return IndexOfQueries.getIndexOfQueries().getQueryDefinitionById(uniqueQid) != null;
    }

    public String getCurrentSamplingPeriod() {
        if(!isDeployed()) {
            return "0";
        } else {
            int resultedPeriod = IndexOfQueries.getIndexOfQueries().getQueryDefinitionById(uniqueQid).getDesiredPeriod();
            if(resultedPeriod == QueryDefinition.noPeriodicSubmission){
                resultedPeriod = 0;
            }
            return Integer.toString(resultedPeriod);
        }
    }

    public String getAppPath() {
        return appPath;
    }

    public void setAppPath(String appPath) {
        this.appPath = appPath;
    }
}
