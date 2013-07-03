DROP SCHEMA IF EXISTS `vitrofrontenddb`;
CREATE DATABASE  IF NOT EXISTS `vitrofrontenddb` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `vitrofrontenddb`;
-- MySQL dump 10.13  Distrib 5.5.16, for Win32 (x86)
--
-- Host: localhost    Database: vitrofrontenddb
-- ------------------------------------------------------
-- Server version	5.5.24-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `registeredgateway`
--

DROP TABLE IF EXISTS `registeredgateway`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `registeredgateway` (
  `idregisteredgateway` int(11) NOT NULL AUTO_INCREMENT,
  `registeredName` varchar(255) NOT NULL,
  `friendlyName` varchar(1023) DEFAULT NULL,
  `friendlyDescription` varchar(2048) DEFAULT NULL,
  `ip` varchar(1023) DEFAULT NULL,
  `listeningport` varchar(1023) DEFAULT NULL,
  `lastadvtimestamp` int(11) DEFAULT '0',
  `disabled` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`idregisteredgateway`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `registeredgateway`
--

LOCK TABLES `registeredgateway` WRITE;
/*!40000 ALTER TABLE `registeredgateway` DISABLE KEYS */;
INSERT INTO `registeredgateway` VALUES (1,'vitrogw_cti','CTI island',NULL,NULL,NULL,1363273534,0),(2,'vitrogw_hai','HAI island',NULL,NULL,NULL,0,0),(3,'vitrogw_wlab','WLAB island',NULL,NULL,NULL,0,0),(4,'vitrogw_tcs','TCS island',NULL,NULL,NULL,0,0),(5,'vitrogw_ssi','SSI island',NULL,NULL,NULL,0,0),(6,'vitrogw_tid','TID island',NULL,NULL,NULL,0,0),(7,'vitrogw_hai2','HAI island 2',NULL,NULL,NULL,0,0),(8,'vitrogw_hai3','HAI island 3',NULL,NULL,NULL,0,0);
/*!40000 ALTER TABLE `registeredgateway` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `roles` (
  `idroles` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `role_name` varchar(255) NOT NULL,
  PRIMARY KEY (`idroles`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,'anonymous'),(2,'vsp'),(3,'wsie'),(4,'user');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `userassociatedgwsmr`
--

DROP TABLE IF EXISTS `userassociatedgwsmr`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `userassociatedgwsmr` (
  `registeredgateway` int(11) NOT NULL,
  `iduser` int(11) unsigned NOT NULL,
  PRIMARY KEY (`registeredgateway`,`iduser`),
  KEY `useridFK2` (`iduser`),
  KEY `associatedgatewayFK` (`registeredgateway`),
  CONSTRAINT `associatedgateway` FOREIGN KEY (`registeredgateway`) REFERENCES `registeredgateway` (`idregisteredgateway`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `useridFK2` FOREIGN KEY (`iduser`) REFERENCES `users` (`idusers`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `userassociatedgwsmr`
--

LOCK TABLES `userassociatedgwsmr` WRITE;
/*!40000 ALTER TABLE `userassociatedgwsmr` DISABLE KEYS */;
/*!40000 ALTER TABLE `userassociatedgwsmr` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `userinrolesmr`
--

DROP TABLE IF EXISTS `userinrolesmr`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `userinrolesmr` (
  `idrole` int(11) unsigned NOT NULL,
  `iduser` int(11) unsigned NOT NULL,
  PRIMARY KEY (`idrole`,`iduser`),
  KEY `useridFK` (`iduser`),
  KEY `userroleFK` (`idrole`),
  CONSTRAINT `useridFK` FOREIGN KEY (`iduser`) REFERENCES `users` (`idusers`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `userroleFK` FOREIGN KEY (`idrole`) REFERENCES `roles` (`idroles`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `userinrolesmr`
--

LOCK TABLES `userinrolesmr` WRITE;
/*!40000 ALTER TABLE `userinrolesmr` DISABLE KEYS */;
INSERT INTO `userinrolesmr` VALUES (2,1),(3,2),(4,3);
/*!40000 ALTER TABLE `userinrolesmr` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `idusers` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `passwd` varchar(255) NOT NULL,
  `login` varchar(1024) NOT NULL,
  `email` varchar(2048) DEFAULT NULL,
  `lastadvtimestamp` int(11) DEFAULT '0',
  `disabled` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`idusers`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='for user auth and data';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'$shiro1$SHA-256$500000$/q5Lwij8aApXfaMEIorizQ==$zEVVA/pO7LBXKujDR7DZjvuHjCTOQv2mDIjf6F8Q3SM=','vsp','vsp@vitro.eu',NULL,0),(2,'$shiro1$SHA-256$500000$7eF+Vlbtwt/xLPfZcCzZ/w==$IyxHK4Zrjz6Ti2ITQYjEPKWP6Tp4L3CgnaHXQnQ9M5A=','wsie','dummy@foo.com',NULL,0),(3,'$shiro1$SHA-256$500000$E7bwrwB/TRt+bPeJYJvX4w==$gCv/Ym45We1Ascd4FcRXJ48GPIt5aYpP5kvQIjn9/8U=','user','dummy@foo.com',NULL,0);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usertovsnsmr`
--

DROP TABLE IF EXISTS `usertovsnsmr`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `usertovsnsmr` (
  `vsnid` int(11) NOT NULL,
  `iduser` int(11) unsigned NOT NULL,
  PRIMARY KEY (`vsnid`,`iduser`),
  KEY `useridFK3` (`iduser`),
  CONSTRAINT `useridFK3` FOREIGN KEY (`iduser`) REFERENCES `users` (`idusers`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usertovsnsmr`
--

LOCK TABLES `usertovsnsmr` WRITE;
/*!40000 ALTER TABLE `usertovsnsmr` DISABLE KEYS */;
/*!40000 ALTER TABLE `usertovsnsmr` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'vitrofrontenddb'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2013-03-15 10:13:19
