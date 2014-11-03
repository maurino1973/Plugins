-- MySQL dump 10.13  Distrib 5.5.38, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: uvrelease
-- ------------------------------------------------------
-- Server version	5.5.38-0ubuntu0.14.04.1

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
-- Dumping data for table `dpu_template`
--

LOCK TABLES `dpu_template` WRITE;
/*!40000 ALTER TABLE `dpu_template` DISABLE KEYS */;
UPDATE `dpu_template` SET  `jar_name` = 'uv-e-filesFromLocal-1.4.0.jar' WHERE `jar_name` LIKE 'uv-e-filesFromLocal%.jar';
UPDATE `dpu_template` SET  `jar_name` = 'uv-e-httpDownload-1.4.0.jar'  WHERE `jar_name` LIKE 'uv-e-httpDownload-%.jar';
UPDATE `dpu_template` SET  `jar_name` = 'uv-e-rdfFromSparql-1.4.0.jar'  WHERE `jar_name` LIKE 'uv-e-rdfFromSparql-%.jar';
UPDATE `dpu_template` SET  `jar_name` = 'uv-e-uploadToFiles-1.4.0.jar'  WHERE `jar_name` LIKE 'uv-e-uploadToFiles-%.jar';
UPDATE `dpu_template` SET  `jar_name` = 'uv-l-filesToLocalFS-1.4.0.jar'  WHERE `jar_name` LIKE 'uv-l-filesToLocalFS-%.jar';
UPDATE `dpu_template` SET  `jar_name` = 'uv-l-filesToScp-1.4.0.jar'  WHERE `jar_name` LIKE 'uv-l-filesToScp-%.jar';
UPDATE `dpu_template` SET  `jar_name` = 'uv-l-filesToVirtuoso-1.4.0.jar'  WHERE `jar_name` LIKE 'uv-l-filesToVirtuoso-%.jar';
UPDATE `dpu_template` SET  `jar_name` = 'uv-l-rdfToSparql-1.4.0.jar'  WHERE `jar_name` LIKE 'uv-l-rdfToSparql-%.jar';
UPDATE `dpu_template` SET  `jar_name` = 'uv-t-filesFilter-1.4.0.jar'  WHERE `jar_name` LIKE 'uv-t-filesFilter-%.jar';
UPDATE `dpu_template` SET  `jar_name` = 'uv-t-filesRenamer-1.4.0.jar'  WHERE `jar_name` LIKE 'uv-t-filesRenamer-%.jar';
UPDATE `dpu_template` SET  `jar_name` = 'uv-t-filesToRdf-1.4.0.jar'  WHERE `jar_name` LIKE 'uv-t-filesToRdf-%.jar';
UPDATE `dpu_template` SET  `jar_name` = 'uv-t-metadata-1.4.0.jar'  WHERE `jar_name` LIKE 'uv-t-metadata-%.jar';
UPDATE `dpu_template` SET  `jar_name` = 'uv-t-rdfToFiles-1.4.0.jar'  WHERE `jar_name` LIKE 'uv-t-rdfToFiles-%.jar';
UPDATE `dpu_template` SET  `jar_name` = 'uv-t-sparql-1.4.0.jar'  WHERE `jar_name` LIKE 'uv-t-sparql-%.jar';
UPDATE `dpu_template` SET  `jar_name` = 'uv-t-sparqlSelect-1.4.0.jar'  WHERE `jar_name` LIKE 'uv-t-sparqlSelect-%.jar';
UPDATE `dpu_template` SET  `jar_name` = 'uv-t-tabular-1.4.0.jar'  WHERE `jar_name` LIKE 'uv-t-tabular-%.jar';
UPDATE `dpu_template` SET  `jar_name` = 'uv-t-unzipper-1.4.0.jar'  WHERE `jar_name` LIKE 'uv-t-unzipper-%.jar';
UPDATE `dpu_template` SET  `jar_name` = 'uv-t-xslt-1.4.0.jar'  WHERE `jar_name` LIKE 'uv-t-xslt-%.jar';
UPDATE `dpu_template` SET  `jar_name` = 'uv-t-zipper-1.4.0.jar'  WHERE `jar_name` LIKE 'uv-t-zipper-%.jar';
/*!40000 ALTER TABLE `dpu_template` ENABLE KEYS */;
UNLOCK TABLES; 
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-09-11 15:41:45
DELETE FROM `properties` WHERE `key` = 'UV.Plugins.version';
INSERT INTO `properties` SET `key` = 'UV.Plugins.version', `value` = '001.004.000';
