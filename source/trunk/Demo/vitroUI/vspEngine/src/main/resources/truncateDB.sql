TRUNCATE vitrofrontenddb.DBSelectionOfRegions_DBRegionSelection;
TRUNCATE vitrofrontenddb.DBSelectionOfGateways_registeredgateway;
TRUNCATE vitrofrontenddb.DBSelectionOfSmartNodes_DBSmartNodeOfGateway;
TRUNCATE vitrofrontenddb.Capability_DBSelectionOfRegions;
TRUNCATE vitrofrontenddb.Capability_DBSelectionOfGateways;

TRUNCATE vitrofrontenddb.Capability_DBSelectionOfSmartNodes;

TRUNCATE vitrofrontenddb.ServiceInstance_Capability;
TRUNCATE vitrofrontenddb.ServiceInstance_Observation;
TRUNCATE vitrofrontenddb.ServiceInstance_registeredgateway;
TRUNCATE vitrofrontenddb.ServiceInstance_searchTagList;

TRUNCATE vitrofrontenddb.FullComposedService_ServiceInstance;
TRUNCATE vitrofrontenddb.FullComposedService_searchTagList;

DELETE FROM vitrofrontenddb.DBSelectionOfRegions WHERE ID >= 0;
DELETE FROM  vitrofrontenddb.DBRegionSelection WHERE ID >= 0;
DELETE FROM  vitrofrontenddb.DBSelectionOfGateways WHERE ID >= 0;
DELETE FROM  vitrofrontenddb.DBSelectionOfSmartNodes WHERE ID >= 0;

DELETE FROM  SetOfEquivalentSensorNodes WHERE ID >= 0;
DELETE FROM  vitrofrontenddb.DBSmartNodeOfGateway WHERE ID >= 0;

DELETE FROM  vitrofrontenddb.Capability WHERE ID >= 0;

DELETE FROM  vitrofrontenddb.ServiceInstance WHERE ID >= 0;

DELETE FROM  vitrofrontenddb.FullComposedService WHERE ID >= 0;

DELETE FROM  vitrofrontenddb.Observation WHERE ID >= 0;

