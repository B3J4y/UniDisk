SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;


CREATE TABLE IF NOT EXISTS `2017_Hallo_ScoreStich` (
  `Stichwort` text,
  `id` text,
  `SolrScore` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `2017_Hallo_Stichwort` (
  `Id` mediumint(9) NOT NULL,
  `Stichwort` varchar(50) DEFAULT NULL,
  `Variable` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `2017_Hallo_varMeta` (
  `Variable` text,
  `Stichworte` text,
  `Hochschule` text,
  `Content` text,
  `SolrScore` text,
  `URL` text,
  `Depth` text,
  `Lat` double DEFAULT NULL,
  `Lon` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `Beruf_ScoreStich` (
  `Stichwort` text,
  `id` text,
  `SolrScore` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `Beruf_Stichwort` (
  `Id` mediumint(9) NOT NULL,
  `Stichwort` varchar(50) DEFAULT NULL,
  `Variable` varchar(50) DEFAULT NULL,
  `Metavariable` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `Beruf_varMeta` (
  `Variable` text,
  `Stichworte` text,
  `Hochschule` text,
  `Content` text,
  `SolrScore` text,
  `URL` text,
  `Depth` text,
  `Lat` double DEFAULT NULL,
  `Lon` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `Brückenkurs Mathematik_Stichwort` (
  `Id` mediumint(9) NOT NULL,
  `Stichwort` varchar(50) DEFAULT NULL,
  `Variable` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `Brückenkurs_ScoreStich` (
  `Stichwort` text,
  `id` text,
  `SolrScore` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `Brückenkurs_Stichwort` (
  `Id` mediumint(9) NOT NULL,
  `Stichwort` varchar(50) DEFAULT NULL,
  `Variable` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `Brückenkurs_varMeta` (
  `Variable` text,
  `Stichworte` text,
  `Hochschule` text,
  `Content` text,
  `SolrScore` text,
  `URL` text,
  `Depth` text,
  `Lat` double DEFAULT NULL,
  `Lon` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `elearningForschendesLernen_ScoreStich` (
  `Stichwort` text,
  `id` text,
  `SolrScore` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `elearningForschendesLernen_Stichwort` (
  `Id` mediumint(9) NOT NULL,
  `Stichwort` varchar(50) DEFAULT NULL,
  `Variable` varchar(50) DEFAULT NULL,
  `Metavariable` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `elearningForschendesLernen_varMeta` (
  `Variable` text,
  `Stichworte` text,
  `Hochschule` text,
  `Content` text,
  `SolrScore` text,
  `URL` text,
  `Depth` text,
  `Lat` double DEFAULT NULL,
  `Lon` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `elearning_Stichwort` (
  `Id` mediumint(9) NOT NULL,
  `Stichwort` varchar(50) DEFAULT NULL,
  `Variable` varchar(50) DEFAULT NULL,
  `Metavariable` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `ForschendesLernen_ScoreStich` (
  `Stichwort` text,
  `id` text,
  `SolrScore` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `ForschendesLernen_Stichwort` (
  `Id` mediumint(9) NOT NULL,
  `Stichwort` varchar(50) DEFAULT NULL,
  `Variable` varchar(50) DEFAULT NULL,
  `Metavariable` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `ForschendesLernen_varMeta` (
  `Variable` text,
  `Metavariable` text,
  `Stichworte` text,
  `Hochschule` text,
  `Content` text,
  `SolrScore` text,
  `URL` text,
  `Depth` text,
  `Lat` double DEFAULT NULL,
  `Lon` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `Forschungsnähe_Stichwort` (
  `Id` mediumint(9) NOT NULL,
  `Stichwort` varchar(50) DEFAULT NULL,
  `Variable` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `hochschulen_deutschland` (
  `HS-Nr` int(11) NOT NULL,
  `Hochschulkurzname` text NOT NULL,
  `Hochschulname` text NOT NULL,
  `Hochschultyp` text NOT NULL,
  `Trägerschaft` text NOT NULL,
  `Bundesland` text NOT NULL,
  `Anzahl Studierender` int(11) NOT NULL,
  `Grundungsjahr` int(11) NOT NULL,
  `Promotionsrecht` text NOT NULL,
  `Habilitationsrecht` text NOT NULL,
  `Straße` text NOT NULL,
  `Postleitzahl` text NOT NULL,
  `Ort` text NOT NULL,
  `Postfach` text NOT NULL,
  `Postleitzahl (Postanschrift)` text NOT NULL,
  `Ort (Postanschrift)` text NOT NULL,
  `Telefonvorwahl` int(11) NOT NULL,
  `Telefon` int(11) NOT NULL,
  `Fax` int(11) NOT NULL,
  `Homepage` text NOT NULL,
  `Mitglied HRK` tinyint(1) NOT NULL,
  `lat` double DEFAULT NULL,
  `lon` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `itdidaktikforschung_ScoreStich` (
  `Stichwort` text,
  `id` text,
  `SolrScore` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `itdidaktikforschung_Stichwort` (
  `Id` mediumint(9) NOT NULL,
  `Stichwort` varchar(50) DEFAULT NULL,
  `Variable` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `itdidaktikforschung_varMeta` (
  `Variable` text,
  `Stichworte` text,
  `Hochschule` text,
  `Content` text,
  `SolrScore` text,
  `URL` text,
  `Depth` text,
  `Lat` double DEFAULT NULL,
  `Lon` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `lebenslangeslernen_ScoreStich` (
  `Stichwort` text,
  `id` text,
  `SolrScore` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `lebenslangeslernen_Stichwort` (
  `Id` mediumint(9) NOT NULL,
  `Stichwort` varchar(50) DEFAULT NULL,
  `Variable` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `Lebenslanges Lernen_Stichwort` (
  `Id` mediumint(9) NOT NULL,
  `Stichwort` varchar(50) DEFAULT NULL,
  `Variable` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `lebenslangeslernen_varMeta` (
  `Variable` text,
  `Stichworte` text,
  `Hochschule` text,
  `Content` text,
  `SolrScore` text,
  `URL` text,
  `Depth` text,
  `Lat` double DEFAULT NULL,
  `Lon` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `NLP-Projekt_ScoreStich` (
  `Stichwort` text,
  `id` text,
  `SolrScore` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `NLP-Projekt_Stichwort` (
  `Id` mediumint(9) NOT NULL,
  `Stichwort` varchar(50) DEFAULT NULL,
  `Variable` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `NLP-Projekt_varMeta` (
  `Variable` text,
  `Stichworte` text,
  `Hochschule` text,
  `Content` text,
  `SolrScore` text,
  `URL` text,
  `Depth` text,
  `Lat` double DEFAULT NULL,
  `Lon` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `NLP_Stichwort` (
  `Id` mediumint(9) NOT NULL,
  `Stichwort` varchar(50) DEFAULT NULL,
  `Variable` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `overview` (
  `Name` text NOT NULL,
  `Id` int(11) NOT NULL,
  `Status` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `Performance Art Education_Stichwort` (
  `Id` mediumint(9) NOT NULL,
  `Stichwort` varchar(50) DEFAULT NULL,
  `Variable` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `Probe_ScoreStich` (
  `Stichwort` text,
  `id` text,
  `SolrScore` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `Probe_Stichwort` (
  `Id` mediumint(9) NOT NULL,
  `Stichwort` varchar(50) DEFAULT NULL,
  `Variable` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `Probe_varMeta` (
  `Variable` text,
  `Stichworte` text,
  `Hochschule` text,
  `Content` text,
  `SolrScore` text,
  `URL` text,
  `Depth` text,
  `Lat` double DEFAULT NULL,
  `Lon` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `qualitätsoffensive_ScoreStich` (
  `Stichwort` text,
  `id` text,
  `SolrScore` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `qualitätsoffensive_Stichwort` (
  `Id` mediumint(9) NOT NULL,
  `Stichwort` varchar(50) DEFAULT NULL,
  `Variable` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `qualitätsoffensive_varMeta` (
  `Variable` text,
  `Stichworte` text,
  `Hochschule` text,
  `Content` text,
  `SolrScore` text,
  `URL` text,
  `Depth` text,
  `Lat` double DEFAULT NULL,
  `Lon` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `SelfAssessment2_ScoreStich` (
  `Stichwort` text,
  `id` text,
  `SolrScore` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `SelfAssessment2_Stichwort` (
  `Id` mediumint(9) NOT NULL,
  `Stichwort` varchar(50) DEFAULT NULL,
  `Variable` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `SelfAssessment2_varMeta` (
  `Variable` text,
  `Stichworte` text,
  `Hochschule` text,
  `Content` text,
  `SolrScore` text,
  `URL` text,
  `Depth` text,
  `Lat` double DEFAULT NULL,
  `Lon` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `SelfAssessment3_ScoreStich` (
  `Stichwort` text,
  `id` text,
  `SolrScore` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `SelfAssessment3_Stichwort` (
  `Id` mediumint(9) NOT NULL,
  `Stichwort` varchar(50) DEFAULT NULL,
  `Variable` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `SelfAssessment3_varMeta` (
  `Variable` text,
  `Stichworte` text,
  `Hochschule` text,
  `Content` text,
  `SolrScore` text,
  `URL` text,
  `Depth` text,
  `Lat` double DEFAULT NULL,
  `Lon` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `selfassessment_ScoreStich` (
  `Stichwort` text,
  `id` text,
  `SolrScore` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `selfassessment_Stichwort` (
  `Id` mediumint(9) NOT NULL,
  `Stichwort` varchar(50) DEFAULT NULL,
  `Variable` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `selfassessment_varMeta` (
  `Variable` text,
  `Stichworte` text,
  `Hochschule` text,
  `Content` text,
  `SolrScore` text,
  `URL` text,
  `Depth` text,
  `Lat` double DEFAULT NULL,
  `Lon` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `Test-Test` (
  `asd` int(11) NOT NULL,
  `asdf` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `Test2_ScoreStich` (
  `Stichwort` text,
  `id` text,
  `SolrScore` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `Test2_Stichwort` (
  `Id` mediumint(9) NOT NULL,
  `Stichwort` varchar(50) DEFAULT NULL,
  `Variable` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `Test2_varMeta` (
  `Variable` text,
  `Stichworte` text,
  `Hochschule` text,
  `Content` text,
  `SolrScore` text,
  `URL` text,
  `Depth` text,
  `Lat` double DEFAULT NULL,
  `Lon` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `TestProjekt_ScoreStich` (
  `Stichwort` text,
  `id` text,
  `SolrScore` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `Test Projekt_Stichwort` (
  `Id` mediumint(9) NOT NULL,
  `Stichwort` varchar(50) DEFAULT NULL,
  `Variable` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `TestProjekt_Stichwort` (
  `Id` mediumint(9) NOT NULL,
  `Stichwort` varchar(50) DEFAULT NULL,
  `Variable` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `TestProjekt_varMeta` (
  `Variable` text,
  `Stichworte` text,
  `Hochschule` text,
  `Content` text,
  `SolrScore` text,
  `URL` text,
  `Depth` text,
  `Lat` double DEFAULT NULL,
  `Lon` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `testTable_ScoreStich` (
  `Stichwort` text,
  `id` text,
  `SolrScore` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `testTable_Stichwort` (
  `Id` mediumint(9) NOT NULL,
  `Stichwort` varchar(50) DEFAULT NULL,
  `Variable` varchar(50) DEFAULT NULL,
  `Metavariable` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `testTable_varMeta` (
  `Variable` text,
  `Metavariable` text,
  `Stichworte` text,
  `Hochschule` text,
  `Content` text,
  `SolrScore` text,
  `URL` text,
  `Depth` text,
  `Lat` double DEFAULT NULL,
  `Lon` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


ALTER TABLE `2017_Hallo_Stichwort`
  ADD PRIMARY KEY (`Id`);

ALTER TABLE `Beruf_Stichwort`
  ADD PRIMARY KEY (`Id`);

ALTER TABLE `Brückenkurs Mathematik_Stichwort`
  ADD PRIMARY KEY (`Id`);

ALTER TABLE `Brückenkurs_Stichwort`
  ADD PRIMARY KEY (`Id`);

ALTER TABLE `elearningForschendesLernen_Stichwort`
  ADD PRIMARY KEY (`Id`);

ALTER TABLE `elearning_Stichwort`
  ADD PRIMARY KEY (`Id`);

ALTER TABLE `ForschendesLernen_Stichwort`
  ADD PRIMARY KEY (`Id`);

ALTER TABLE `Forschungsnähe_Stichwort`
  ADD PRIMARY KEY (`Id`);

ALTER TABLE `itdidaktikforschung_Stichwort`
  ADD PRIMARY KEY (`Id`);

ALTER TABLE `lebenslangeslernen_Stichwort`
  ADD PRIMARY KEY (`Id`);

ALTER TABLE `Lebenslanges Lernen_Stichwort`
  ADD PRIMARY KEY (`Id`);

ALTER TABLE `NLP-Projekt_Stichwort`
  ADD PRIMARY KEY (`Id`);

ALTER TABLE `NLP_Stichwort`
  ADD PRIMARY KEY (`Id`);

ALTER TABLE `overview`
  ADD PRIMARY KEY (`Id`);

ALTER TABLE `Performance Art Education_Stichwort`
  ADD PRIMARY KEY (`Id`);

ALTER TABLE `Probe_Stichwort`
  ADD PRIMARY KEY (`Id`);

ALTER TABLE `qualitätsoffensive_Stichwort`
  ADD PRIMARY KEY (`Id`);

ALTER TABLE `SelfAssessment2_Stichwort`
  ADD PRIMARY KEY (`Id`);

ALTER TABLE `SelfAssessment3_Stichwort`
  ADD PRIMARY KEY (`Id`);

ALTER TABLE `selfassessment_Stichwort`
  ADD PRIMARY KEY (`Id`);

ALTER TABLE `Test2_Stichwort`
  ADD PRIMARY KEY (`Id`);

ALTER TABLE `Test Projekt_Stichwort`
  ADD PRIMARY KEY (`Id`);

ALTER TABLE `TestProjekt_Stichwort`
  ADD PRIMARY KEY (`Id`);

ALTER TABLE `testTable_Stichwort`
  ADD PRIMARY KEY (`Id`);


ALTER TABLE `2017_Hallo_Stichwort`
  MODIFY `Id` mediumint(9) NOT NULL AUTO_INCREMENT;
ALTER TABLE `Beruf_Stichwort`
  MODIFY `Id` mediumint(9) NOT NULL AUTO_INCREMENT;
ALTER TABLE `Brückenkurs Mathematik_Stichwort`
  MODIFY `Id` mediumint(9) NOT NULL AUTO_INCREMENT;
ALTER TABLE `Brückenkurs_Stichwort`
  MODIFY `Id` mediumint(9) NOT NULL AUTO_INCREMENT;
ALTER TABLE `elearningForschendesLernen_Stichwort`
  MODIFY `Id` mediumint(9) NOT NULL AUTO_INCREMENT;
ALTER TABLE `elearning_Stichwort`
  MODIFY `Id` mediumint(9) NOT NULL AUTO_INCREMENT;
ALTER TABLE `ForschendesLernen_Stichwort`
  MODIFY `Id` mediumint(9) NOT NULL AUTO_INCREMENT;
ALTER TABLE `Forschungsnähe_Stichwort`
  MODIFY `Id` mediumint(9) NOT NULL AUTO_INCREMENT;
ALTER TABLE `itdidaktikforschung_Stichwort`
  MODIFY `Id` mediumint(9) NOT NULL AUTO_INCREMENT;
ALTER TABLE `lebenslangeslernen_Stichwort`
  MODIFY `Id` mediumint(9) NOT NULL AUTO_INCREMENT;
ALTER TABLE `Lebenslanges Lernen_Stichwort`
  MODIFY `Id` mediumint(9) NOT NULL AUTO_INCREMENT;
ALTER TABLE `NLP-Projekt_Stichwort`
  MODIFY `Id` mediumint(9) NOT NULL AUTO_INCREMENT;
ALTER TABLE `NLP_Stichwort`
  MODIFY `Id` mediumint(9) NOT NULL AUTO_INCREMENT;
ALTER TABLE `overview`
  MODIFY `Id` int(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `Performance Art Education_Stichwort`
  MODIFY `Id` mediumint(9) NOT NULL AUTO_INCREMENT;
ALTER TABLE `Probe_Stichwort`
  MODIFY `Id` mediumint(9) NOT NULL AUTO_INCREMENT;
ALTER TABLE `qualitätsoffensive_Stichwort`
  MODIFY `Id` mediumint(9) NOT NULL AUTO_INCREMENT;
ALTER TABLE `SelfAssessment2_Stichwort`
  MODIFY `Id` mediumint(9) NOT NULL AUTO_INCREMENT;
ALTER TABLE `SelfAssessment3_Stichwort`
  MODIFY `Id` mediumint(9) NOT NULL AUTO_INCREMENT;
ALTER TABLE `selfassessment_Stichwort`
  MODIFY `Id` mediumint(9) NOT NULL AUTO_INCREMENT;
ALTER TABLE `Test2_Stichwort`
  MODIFY `Id` mediumint(9) NOT NULL AUTO_INCREMENT;
ALTER TABLE `Test Projekt_Stichwort`
  MODIFY `Id` mediumint(9) NOT NULL AUTO_INCREMENT;
ALTER TABLE `TestProjekt_Stichwort`
  MODIFY `Id` mediumint(9) NOT NULL AUTO_INCREMENT;
ALTER TABLE `testTable_Stichwort`
  MODIFY `Id` mediumint(9) NOT NULL AUTO_INCREMENT;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
