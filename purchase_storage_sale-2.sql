-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Sep 21, 2023 at 11:06 PM
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `purchase_storage_sale`
--

-- --------------------------------------------------------

--
-- Table structure for table `customer`
--

CREATE TABLE `customer` (
  `Customer_ID` int(11) NOT NULL,
  `Forname` varchar(255) NOT NULL,
  `Lastname` varchar(255) NOT NULL,
  `city` varchar(255) NOT NULL,
  `Longitude` decimal(10,6) NOT NULL,
  `Latitude` decimal(10,6) NOT NULL,
  `revenue` double(40,3) DEFAULT NULL,
  `Timestamp` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `customer`
--

INSERT INTO `customer` (`Customer_ID`, `Forname`, `Lastname`, `city`, `Longitude`, `Latitude`, `revenue`, `Timestamp`) VALUES
(1, 'Emma', 'Fischer', 'Muenchen', 11.574097, 48.359899, 624110.126, '2023-09-17 14:25:50'),
(2, 'Noah', 'Becker', 'Berlin', 13.403320, 52.703019, 452366.515, '2023-09-17 14:25:50'),
(3, 'Benjamin', 'Keller', 'Hamburg', 10.036011, 53.693454, 854821.214, '2023-09-17 14:25:50'),
(4, 'Lucas', 'Lehmann', 'Frankfurt', 8.723145, 50.183933, 517533.971, '2023-09-17 14:25:50'),
(5, 'Klaus', 'Mayer', 'Muenster', 7.646484, 51.937492, 520303.379, '2023-09-17 14:25:50');

-- --------------------------------------------------------

--
-- Table structure for table `purchase`
--

CREATE TABLE `purchase` (
  `P_ID` int(11) NOT NULL,
  `Supplier_ID` int(11) NOT NULL,
  `Storage_ID` int(11) NOT NULL,
  `Quantity` double NOT NULL,
  `PricePerTon` double NOT NULL,
  `Totalcosts` double(40,3) NOT NULL,
  `Gravelcosts` double(40,3) NOT NULL,
  `Deliverycosts` double(40,3) NOT NULL,
  `Timestamp` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `sale`
--

CREATE TABLE `sale` (
  `S_ID` int(11) NOT NULL,
  `C_ID` int(11) NOT NULL,
  `Quantity` double(40,3) NOT NULL,
  `OurPricePerTon` double(40,3) NOT NULL,
  `Margin` decimal(40,3) NOT NULL,
  `TotalRevenue` double(40,3) NOT NULL,
  `Deliverycosts_Customer` double(40,3) NOT NULL,
  `Profit` double(40,3) NOT NULL,
  `DiscountRate` double(40,3) NOT NULL,
  `Timestamp` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `storage`
--

CREATE TABLE `storage` (
  `Storage_ID` int(11) NOT NULL,
  `Capacity` double NOT NULL,
  `FillLevel` decimal(40,4) NOT NULL,
  `City` varchar(255) NOT NULL,
  `Latitude` decimal(10,6) NOT NULL,
  `Longitude` decimal(10,6) NOT NULL,
  `Timestamp` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `storage`
--

INSERT INTO `storage` (`Storage_ID`, `Capacity`, `FillLevel`, `City`, `Latitude`, `Longitude`, `Timestamp`) VALUES
(1, 16729.1, 0.9951, 'Köln', 50.938361, 6.959974, '2023-09-21 20:48:19'),
(2, 44472.7, 0.6719, 'Hamburg', 53.550341, 10.000654, '2023-09-21 20:48:19'),
(3, 35475.1, 0.0792, 'Berlin', 52.517037, 13.388860, '2023-09-21 20:48:19');

-- --------------------------------------------------------

--
-- Table structure for table `supplier`
--

CREATE TABLE `supplier` (
  `Supplier_ID` int(11) NOT NULL,
  `Name` varchar(255) NOT NULL,
  `City` varchar(255) NOT NULL,
  `Longitude` decimal(10,6) NOT NULL,
  `Latitude` decimal(10,6) NOT NULL,
  `PricePerTon` double DEFAULT NULL,
  `Timestamp` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `supplier`
--

INSERT INTO `supplier` (`Supplier_ID`, `Name`, `City`, `Longitude`, `Latitude`, `PricePerTon`, `Timestamp`) VALUES
(1, 'SchotterProfi', 'Muenchen', 11.575382, 48.137108, 40.598, '2023-09-21 20:48:19'),
(2, 'KiesMeister-AG', 'FrankfurtAmMain', 8.682092, 50.110644, 52.294, '2023-09-21 20:48:19'),
(3, 'SchotterDirekt-GmbH', 'Stuttgart', 9.180013, 48.778449, 59.098, '2023-09-21 20:48:19'),
(4, 'SplitterLieferant-GmbH', 'Duesseldorf', 6.776314, 51.225402, 53.832, '2023-09-21 20:48:19'),
(5, 'SchotterSpezialist-GmbH', 'Leipzig', 12.374733, 51.340632, 47.375, '2023-09-21 20:48:19'),
(6, 'KiesWelt-GmbH', 'Berlin', 13.379974, 52.425873, 40.683, '2023-09-21 20:48:19'),
(7, 'SchotterLand-GmbH', 'Dortmund', 7.465298, 51.513587, 36.909, '2023-09-21 20:48:19'),
(8, 'KiesKraft-GmbH', 'Koeln', 6.896667, 50.898702, 33.424, '2023-09-21 20:48:19');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `customer`
--
ALTER TABLE `customer`
  ADD PRIMARY KEY (`Customer_ID`);

--
-- Indexes for table `purchase`
--
ALTER TABLE `purchase`
  ADD PRIMARY KEY (`P_ID`) USING BTREE;

--
-- Indexes for table `sale`
--
ALTER TABLE `sale`
  ADD PRIMARY KEY (`S_ID`);

--
-- Indexes for table `storage`
--
ALTER TABLE `storage`
  ADD PRIMARY KEY (`Storage_ID`);

--
-- Indexes for table `supplier`
--
ALTER TABLE `supplier`
  ADD PRIMARY KEY (`Supplier_ID`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `customer`
--
ALTER TABLE `customer`
  MODIFY `Customer_ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `purchase`
--
ALTER TABLE `purchase`
  MODIFY `P_ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23155;

--
-- AUTO_INCREMENT for table `sale`
--
ALTER TABLE `sale`
  MODIFY `S_ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=50650;

--
-- AUTO_INCREMENT for table `storage`
--
ALTER TABLE `storage`
  MODIFY `Storage_ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `supplier`
--
ALTER TABLE `supplier`
  MODIFY `Supplier_ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
