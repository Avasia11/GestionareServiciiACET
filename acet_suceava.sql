-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jan 11, 2025 at 02:38 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `acet_suceava`
--

-- --------------------------------------------------------

--
-- Table structure for table `consum`
--

CREATE TABLE `consum` (
  `id` int(10) UNSIGNED NOT NULL,
  `id_utilizator` int(10) UNSIGNED NOT NULL,
  `luna` varchar(20) NOT NULL,
  `an` int(11) NOT NULL,
  `consum` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `consum`
--

INSERT INTO `consum` (`id`, `id_utilizator`, `luna`, `an`, `consum`) VALUES
(1, 2, 'Ianuarie', 2023, 12.00),
(2, 2, 'Februarie', 2023, 13.00);

-- --------------------------------------------------------

--
-- Table structure for table `facturi`
--

CREATE TABLE `facturi` (
  `id` int(10) UNSIGNED NOT NULL,
  `id_utilizator` int(10) UNSIGNED NOT NULL,
  `perioada` varchar(50) NOT NULL,
  `suma` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `facturi`
--

INSERT INTO `facturi` (`id`, `id_utilizator`, `perioada`, `suma`) VALUES
(1, 2, 'Ianuarie 2023', 60.00),
(2, 2, 'Februarie 2023', 65.00);

-- --------------------------------------------------------

--
-- Table structure for table `probleme`
--

CREATE TABLE `probleme` (
  `id` int(10) UNSIGNED NOT NULL,
  `id_utilizator` int(10) UNSIGNED NOT NULL,
  `data` date NOT NULL,
  `descriere` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `rapoarte`
--

CREATE TABLE `rapoarte` (
  `id` int(10) UNSIGNED NOT NULL,
  `id_utilizator` int(10) UNSIGNED NOT NULL,
  `prioritate` varchar(50) NOT NULL,
  `raport` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `rapoarte`
--

INSERT INTO `rapoarte` (`id`, `id_utilizator`, `prioritate`, `raport`) VALUES
(1, 2, 'Urgentă', 'ma doare pla');

-- --------------------------------------------------------

--
-- Table structure for table `utilizatori`
--

CREATE TABLE `utilizatori` (
  `id` int(10) UNSIGNED NOT NULL,
  `nume` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `parola` varchar(255) NOT NULL,
  `tip_utilizator` enum('admin','client') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `utilizatori`
--

INSERT INTO `utilizatori` (`id`, `nume`, `email`, `parola`, `tip_utilizator`) VALUES
(1, 'Admin', 'admin@test.com', '123', 'admin'),
(2, 'test', 'test', 'test', 'client');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `consum`
--
ALTER TABLE `consum`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FOREIGN KEY` (`id_utilizator`);

--
-- Indexes for table `facturi`
--
ALTER TABLE `facturi`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FOREIGN KEY` (`id_utilizator`);

--
-- Indexes for table `probleme`
--
ALTER TABLE `probleme`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FOREIGN KEY` (`id_utilizator`);

--
-- Indexes for table `rapoarte`
--
ALTER TABLE `rapoarte`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_utilizator_raport` (`id_utilizator`);

--
-- Indexes for table `utilizatori`
--
ALTER TABLE `utilizatori`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `Pass` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `consum`
--
ALTER TABLE `consum`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `facturi`
--
ALTER TABLE `facturi`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `probleme`
--
ALTER TABLE `probleme`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `rapoarte`
--
ALTER TABLE `rapoarte`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `utilizatori`
--
ALTER TABLE `utilizatori`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `consum`
--
ALTER TABLE `consum`
  ADD CONSTRAINT `consum_utilizatori` FOREIGN KEY (`id_utilizator`) REFERENCES `utilizatori` (`id`);

--
-- Constraints for table `facturi`
--
ALTER TABLE `facturi`
  ADD CONSTRAINT `facturi_utilizator` FOREIGN KEY (`id_utilizator`) REFERENCES `utilizatori` (`id`);

--
-- Constraints for table `probleme`
--
ALTER TABLE `probleme`
  ADD CONSTRAINT `probleme_utilizatori` FOREIGN KEY (`id_utilizator`) REFERENCES `utilizatori` (`id`);

--
-- Constraints for table `rapoarte`
--
ALTER TABLE `rapoarte`
  ADD CONSTRAINT `fk_utilizator_raport` FOREIGN KEY (`id_utilizator`) REFERENCES `utilizatori` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
