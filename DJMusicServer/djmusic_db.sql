-- phpMyAdmin SQL Dump
-- version 4.1.14
-- http://www.phpmyadmin.net
--
-- Client :  127.0.0.1
-- Généré le :  Jeu 13 Novembre 2014 à 21:29
-- Version du serveur :  5.6.17
-- Version de PHP :  5.5.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Base de données :  `djmusic_db`
--

-- --------------------------------------------------------

--
-- Structure de la table `djmusic_playlists`
--

CREATE TABLE IF NOT EXISTS `djmusic_playlists` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userId` varchar(256) NOT NULL,
  `roomId` varchar(256) NOT NULL,
  `tracksId` varchar(1024) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=3 ;

--
-- Contenu de la table `djmusic_playlists`
--

INSERT INTO `djmusic_playlists` (`id`, `userId`, `roomId`, `tracksId`) VALUES
(1, '12345', '2', '1;2;3'),
(2, '123456', '2', '4');

-- --------------------------------------------------------

--
-- Structure de la table `djmusic_rooms`
--

CREATE TABLE IF NOT EXISTS `djmusic_rooms` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(256) NOT NULL,
  `genre` varchar(256) NOT NULL,
  `currentTrackId` int(11) NOT NULL,
  `currentTurn` int(11) NOT NULL,
  `totalUsers` int(11) NOT NULL,
  `startCurrentTrack` datetime NOT NULL,
  `startNextTrack` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=3 ;

--
-- Contenu de la table `djmusic_rooms`
--

INSERT INTO `djmusic_rooms` (`id`, `name`, `genre`, `currentTrackId`, `currentTurn`, `totalUsers`, `startCurrentTrack`, `startNextTrack`) VALUES
(1, 'room1', 'pop', 0, 1, 0, '0000-00-00 00:00:00', '0000-00-00 00:00:00'),
(2, 'room2', 'rock', 1, 0, 2, '2014-11-10 17:00:00', '2014-11-10 17:10:00');

-- --------------------------------------------------------

--
-- Structure de la table `djmusic_tracks`
--

CREATE TABLE IF NOT EXISTS `djmusic_tracks` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userId` varchar(256) NOT NULL,
  `url` varchar(1024) NOT NULL,
  `title` varchar(256) NOT NULL,
  `artist` varchar(256) NOT NULL,
  `album` varchar(256) NOT NULL,
  `genre` varchar(256) NOT NULL,
  `length` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=5 ;

--
-- Contenu de la table `djmusic_tracks`
--

INSERT INTO `djmusic_tracks` (`id`, `userId`, `url`, `title`, `artist`, `album`, `genre`, `length`) VALUES
(1, '12345', 'https://www.youtube.com/watch?v=8a8PonO1bl0', 'In My Head', 'Queens Of The Stone Age', 'NFSU2 - Soundtrack', 'Rock', 239),
(2, '12345', 'https://www.youtube.com/watch?v=450p7goxZqg', 'All of me', 'John Legend', 'Youtube', 'Pop', 460),
(3, '12345', 'https://www.youtube.com/watch?v=KrtcxBHqskk', 'Fade Away', 'Zack Hemsey', 'RONIN', 'Instrumental', 480),
(4, '123456', 'https://www.youtube.com/watch?v=KrtcxBHqskk', 'Fade Away', 'Zack Hemsey', 'RONIN', 'Instrumental', 480);

-- --------------------------------------------------------

--
-- Structure de la table `djmusic_users`
--

CREATE TABLE IF NOT EXISTS `djmusic_users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `user_name` varchar(256) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=5 ;

--
-- Contenu de la table `djmusic_users`
--

INSERT INTO `djmusic_users` (`id`, `user_id`, `user_name`) VALUES
(2, 12345, 'seradu'),
(3, 123456, 'seradu10'),
(4, 487956542, 'toto');

-- --------------------------------------------------------

--
-- Structure de la table `djmusic_usersinrooms`
--

CREATE TABLE IF NOT EXISTS `djmusic_usersinrooms` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userId` int(11) NOT NULL,
  `roomId` int(11) NOT NULL,
  `turnInRoom` int(11) NOT NULL,
  `playlistId` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=3 ;

--
-- Contenu de la table `djmusic_usersinrooms`
--

INSERT INTO `djmusic_usersinrooms` (`id`, `userId`, `roomId`, `turnInRoom`, `playlistId`) VALUES
(1, 12345, 2, 0, 1),
(2, 123456, 2, 1, 2);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
