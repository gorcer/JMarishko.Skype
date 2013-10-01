-- phpMyAdmin SQL Dump
-- version 2.11.7
-- http://www.phpmyadmin.net
--
-- Хост: localhost
-- Время создания: Июн 29 2011 г., 12:03
-- Версия сервера: 5.0.51
-- Версия PHP: 5.2.6

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- База данных: `ai`
--

-- --------------------------------------------------------

--
-- Структура таблицы `dialogdet`
--

CREATE TABLE IF NOT EXISTS `dialogdet` (
  `ID` int(11) NOT NULL auto_increment,
  `dtm` datetime default NULL,
  `head_id` int(11) NOT NULL,
  `talink_id` int(11) NOT NULL,
  PRIMARY KEY  (`ID`),
  KEY `head_id` (`head_id`),
  KEY `talink_id` (`talink_id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=383 ;

-- --------------------------------------------------------

--
-- Структура таблицы `dialoghead`
--

CREATE TABLE IF NOT EXISTS `dialoghead` (
  `id` int(11) NOT NULL auto_increment,
  `user_id` int(11) NOT NULL,
  `create_dtm` datetime NOT NULL,
  `respect` int(11) NOT NULL default '0',
  `rating` int(11) NOT NULL default '0',
  `tags` tinytext,
  PRIMARY KEY  (`id`),
  KEY `user_id` (`user_id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=87 ;

-- --------------------------------------------------------

--
-- Структура таблицы `talink`
--

CREATE TABLE IF NOT EXISTS `talink` (
  `ID` int(11) NOT NULL auto_increment,
  `ID_1` int(11) default NULL,
  `ID_2` int(11) default NULL,
  `IC` int(11) default '0',
  PRIMARY KEY  (`ID`),
  KEY `ID_1` (`ID_1`,`ID_2`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=976991 ;

-- --------------------------------------------------------

--
-- Структура таблицы `talk`
--

CREATE TABLE IF NOT EXISTS `talk` (
  `ID` int(11) NOT NULL auto_increment,
  `SODER` varchar(300) default NULL,
  `IGNOR` smallint(6) default '0',
  `OTNO` int(11) NOT NULL default '0',
  `MASK` varchar(250) default NULL,
  `IC` int(11) default '0',
  `md` varchar(150) default NULL,
  PRIMARY KEY  (`ID`),
  KEY `OTNO` (`OTNO`),
  FULLTEXT KEY `MASK` (`MASK`),
  FULLTEXT KEY `SODER` (`SODER`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=491527 ;

-- --------------------------------------------------------

--
-- Структура таблицы `talk_attr`
--

CREATE TABLE IF NOT EXISTS `talk_attr` (
  `talk_id` int(11) default NULL,
  `ID` int(11) NOT NULL auto_increment,
  `isHello` int(11) NOT NULL default '0',
  `isSomething` int(11) NOT NULL default '0',
  PRIMARY KEY  (`ID`),
  KEY `talk_id` (`talk_id`,`isHello`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=979911 ;

-- --------------------------------------------------------

--
-- Структура таблицы `users`
--

CREATE TABLE IF NOT EXISTS `users` (
  `id` int(11) NOT NULL auto_increment,
  `name` varchar(250) NOT NULL,
  `respect` int(11) NOT NULL default '0',
  `tags` tinytext,
  `create_dtm` datetime NOT NULL,
  `isAllow` bit(1) NOT NULL default '\0',
  PRIMARY KEY  (`id`),
  KEY `name` (`name`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=59 ;

-- --------------------------------------------------------

--
-- Дублирующая структура для представления `v_talk_attr`
--
CREATE TABLE IF NOT EXISTS `v_talk_attr` (
`id` int(11)
,`talk_id` int(11)
,`soder` varchar(300)
,`isHello` int(11)
,`isSomething` int(11)
);
-- --------------------------------------------------------

--
-- Дублирующая структура для представления `v_talk_log`
--
CREATE TABLE IF NOT EXISTS `v_talk_log` (
`id` int(11)
,`headid` int(11)
,`user_id` int(11)
,`t1_id` int(11)
,`soder1` varchar(300)
,`t2_id` int(11)
,`soder2` varchar(300)
);
-- --------------------------------------------------------

--
-- Структура для представления `v_talk_attr`
--
DROP TABLE IF EXISTS `v_talk_attr`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `ai`.`v_talk_attr` AS select `ta`.`ID` AS `id`,`t`.`ID` AS `talk_id`,`t`.`SODER` AS `soder`,`ta`.`isHello` AS `isHello`,`ta`.`isSomething` AS `isSomething` from (`ai`.`talk` `t` join `ai`.`talk_attr` `ta` on((`ta`.`talk_id` = `t`.`ID`)));

-- --------------------------------------------------------

--
-- Структура для представления `v_talk_log`
--
DROP TABLE IF EXISTS `v_talk_log`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `ai`.`v_talk_log` AS select `d`.`ID` AS `id`,`h`.`id` AS `headid`,`h`.`user_id` AS `user_id`,`t1`.`ID` AS `t1_id`,`t1`.`SODER` AS `soder1`,`t2`.`ID` AS `t2_id`,`t2`.`SODER` AS `soder2` from ((((`ai`.`dialogdet` `d` join `ai`.`dialoghead` `h` on((`h`.`id` = `d`.`head_id`))) join `ai`.`talink` `tl` on((`tl`.`ID` = `d`.`talink_id`))) left join `ai`.`talk` `t1` on((`t1`.`ID` = `tl`.`ID_1`))) left join `ai`.`talk` `t2` on((`t2`.`ID` = `tl`.`ID_2`))) order by `d`.`ID` desc limit 0,30;
