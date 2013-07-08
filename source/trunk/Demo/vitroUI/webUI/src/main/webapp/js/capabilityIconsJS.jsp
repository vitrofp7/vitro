<%@page session='false' contentType='application/x-javascript' %>
<%@ page import="vitro.vspEngine.logic.model.Capability" %>
<%--
  ~ #--------------------------------------------------------------------------
  ~ # Copyright (c) 2013 VITRO FP7 Consortium.
  ~ # All rights reserved. This program and the accompanying materials
  ~ # are made available under the terms of the GNU Lesser Public License v3.0 which accompanies this distribution, and is available at
  ~ # http://www.gnu.org/licenses/lgpl-3.0.html
  ~ #
  ~ # Contributors:
  ~ #     Antoniou Thanasis (Research Academic Computer Technology Institute)
  ~ #     Paolo Medagliani (Thales Communications & Security)
  ~ #     D. Davide Lamanna (WLAB SRL)
  ~ #     Alessandro Leoni (WLAB SRL)
  ~ #     Francesco Ficarola (WLAB SRL)
  ~ #     Stefano Puglia (WLAB SRL)
  ~ #     Panos Trakadas (Technological Educational Institute of Chalkida)
  ~ #     Panagiotis Karkazis (Technological Educational Institute of Chalkida)
  ~ #     Andrea Kropp (Selex ES)
  ~ #     Kiriakos Georgouleas (Hellenic Aerospace Industry)
  ~ #     David Ferrer Figueroa (Telefonica Investigación y Desarrollo S.A.)
  ~ #
  ~ #--------------------------------------------------------------------------
  --%>

function mapCapToIcon32(capabilityName, selectedSwitch, enabledSwitch)
{
    var pSelectedSwitch = false;
    var pEnabledSwitch = true;
    if (!(typeof selectedSwitch == 'undefined') && selectedSwitch != null)
        pSelectedSwitch = selectedSwitch;

    if (!(typeof enabledSwitch == 'undefined') && enabledSwitch != null)
        pEnabledSwitch = enabledSwitch;

    var iconStringPrefix = '<%=request.getContextPath()%>/img/';
    var undefinedUnselectedIcon = '<%=Capability.getDefaultIcon(Capability.PHENOMENOM_UNKNOWN)%>';
    var undefinedSelectedIcon = '<%=Capability.getDefaultSelectedIcon(Capability.PHENOMENOM_UNKNOWN)%>';
    var undefinedDisabledIcon = '<%=Capability.getDefaultDisabledIcon(Capability.PHENOMENOM_UNKNOWN)%>';

    var iconString = '';
    if(pEnabledSwitch == false) {
        iconString = iconStringPrefix + undefinedDisabledIcon;
    }else {
        iconString = iconStringPrefix + ((pSelectedSwitch == true) ? undefinedSelectedIcon : undefinedUnselectedIcon);
    }

    switch(capabilityName){
        case 'allCaps':
            if(pEnabledSwitch == false) {
                iconString = iconStringPrefix + 'smartNodeIconDisabled32.png';
            }else {
                iconString = iconStringPrefix + ((pSelectedSwitch == true) ? 'smartNodeIconSelected32.png' :'smartNodeIcon32.png');
            }
        break;
        case 'replcNode':
            iconString = iconStringPrefix + 'smartNodeIconHalo32.png';
        break;
        <% for (String capPhenom: Capability.getSupportedCapabilitiesIncludingUnknown()) { %>
        case '<%=capPhenom %>':
            if(pEnabledSwitch == false) {
                iconString = iconStringPrefix + '<%=Capability.getDefaultDisabledIcon(capPhenom)%>';
            }else {
                iconString = iconStringPrefix + ((pSelectedSwitch == true) ?'<%=Capability.getDefaultSelectedIcon(capPhenom)%>' :'<%=Capability.getDefaultIcon(capPhenom)%>');
            }
        break;
        <% } %>
        default: break;
    }
    return iconString;
}

function drawSelectedIcon(marker, capabilityName) {
    var image = new google.maps.MarkerImage(mapCapToIcon32(capabilityName, true),
    // This marker is 20 pixels wide by 32 pixels tall.
    new google.maps.Size(32, 32),
    // The origin for this image is 0,0.
    new google.maps.Point(0,0),
    // The anchor for this image is the base of the flagpole at 0,32.
    new google.maps.Point(0, 32));
    marker.setIcon(image);
}

function drawDefaultUnselectedIcon(marker, capabilityName) {
    var image = new google.maps.MarkerImage(mapCapToIcon32(capabilityName, false),
    // This marker is 20 pixels wide by 32 pixels tall.
    new google.maps.Size(32, 32),
    // The origin for this image is 0,0.
    new google.maps.Point(0,0),
    // The anchor for this image is the base of the flagpole at 0,32.
    new google.maps.Point(0, 32));
    marker.setIcon(image);
}

// TODO: Do we need to differentiate between selected and unselected disabled? For now selected will be colored just as a normal enabled node.
function drawDisabledIcon(marker, capabilityName) {
    var image = new google.maps.MarkerImage(mapCapToIcon32(capabilityName, false, false),
    // This marker is 20 pixels wide by 32 pixels tall.
    new google.maps.Size(32, 32),
    // The origin for this image is 0,0.
    new google.maps.Point(0,0),
    // The anchor for this image is the base of the flagpole at 0,32.
    new google.maps.Point(0, 32));
    marker.setIcon(image);
}