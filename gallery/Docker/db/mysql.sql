-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Dec 05, 2025 at 03:37 PM
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
-- Database: `future_proof_db`
--
CREATE DATABASE IF NOT EXISTS `future_proof_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `future_proof_db`;

-- --------------------------------------------------------

--
-- Table structure for table `project`
--

CREATE TABLE `project` (
                           `ProjectID` bigint(20) NOT NULL,
                           `ProjectName` varchar(255) DEFAULT NULL,
                           `ProjectDescription` varchar(500) NOT NULL,
                           `UserID` bigint(20) DEFAULT NULL,
                           `Category` varchar(255) DEFAULT NULL,
                           `ProjectDescriptionSummary` varchar(255) DEFAULT NULL,
                           `ProjectHeroImage` varchar(255) DEFAULT NULL,
                           `CreationDate` date NOT NULL DEFAULT current_timestamp(),
                           `additionalDoc` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `project`
--

INSERT INTO `project` (`ProjectID`, `ProjectName`, `ProjectDescription`, `UserID`, `Category`, `ProjectDescriptionSummary`, `ProjectHeroImage`, `CreationDate`, `additionalDoc`) VALUES
                                                                                                                                                                                     (1, 'sea life', 'The oceans are home to some of the most magnificent creatures both great and small, wild and wonderful. Yet human interference is damaging marine ecosystems around the world causing a vast decline in marine life and the health of the oceans. But there are a few inspiring individuals out there who have dedicated their lives to ocean conservation and helping the creatures of the great wild blue. ', 1, 'Digital Media', 'a look at sea life in winter', 'p1sea.jpg', '2023-09-22', 'x.doc'),
                                                                                                                                                                                     (2, 'winter view', 'If you think wintertime is an excuse to put your camera away and stay warm indoors, then you need to read this article! Winter photography can be one of the most rewarding challenges in photography, whether you are interested in landscapes, cityscapes, seascapes, wildlife, or even portraits!\r\n\r\nIn this article, we will cover a variety of tips, ideas, and examples; everything from the technical to the creative! You might be surprised, too–some of these tips go against the conventional wisdom you’', 2, 'Digital Media', 'winter in the lock down', 'p2snow.jpg', '2023-09-23', 'y.doc'),
                                                                                                                                                                                     (3, '3D cartoons', '3D character modeling is a graphic design technique that creates a three-dimensional digital representation of a surface or an object. Artists use specific software, start with a simple shape, and slowly enrich it with more details', 1, 'IT', 'a collection of 3D Disney characters', 'p3disney.jpg', '2023-09-23', 'x.doc'),
                                                                                                                                                                                     (6, 'Man-in-the-Moon', 'What is organic chemistry? Organic chemistry is the field of chemistry over the study of organic substances and compounds – that is, those that contain carbon in their molecular structure, combined with other elements such as hydrogen, nitrogen, oxygen, and sulfur.', 1, 'organic chemistry', 'The Effect of Gamma Rays on Man-in-the-Moon Marigolds', 'p6artshowcase.jpg', '2023-09-23', 'p.doc'),
                                                                                                                                                                                     (7, 'lizs best project', 'great project', 4, 'ART', 'more details', 'small_cute_cat.png', '2024-11-03', 'no'),
                                                                                                                                                                                     (30, 'test', 'test', NULL, 'test', 'test', NULL, '2025-11-25', NULL),
                                                                                                                                                                                     (31, 'testing pagination', 'testing123', NULL, 'test', 'test', NULL, '2025-11-25', NULL),
                                                                                                                                                                                     (32, NULL, '', NULL, NULL, NULL, NULL, '2025-11-25', NULL),
                                                                                                                                                                                     (33, NULL, '', NULL, NULL, NULL, NULL, '2025-11-25', NULL),
                                                                                                                                                                                     (34, NULL, '', NULL, NULL, NULL, NULL, '2025-11-25', NULL),
                                                                                                                                                                                     (35, NULL, '', NULL, NULL, NULL, NULL, '2025-11-25', NULL),
                                                                                                                                                                                     (36, NULL, '', NULL, NULL, NULL, NULL, '2025-11-25', NULL),
                                                                                                                                                                                     (37, NULL, '', NULL, NULL, NULL, NULL, '2025-11-25', NULL),
                                                                                                                                                                                     (38, NULL, '', NULL, NULL, NULL, NULL, '2025-11-25', NULL),
                                                                                                                                                                                     (39, NULL, '', NULL, NULL, NULL, NULL, '2025-11-25', NULL),
                                                                                                                                                                                     (40, NULL, '', NULL, NULL, NULL, NULL, '2025-11-25', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `savedproject`
--

CREATE TABLE `savedproject` (
                                `SavedId` bigint(20) NOT NULL,
                                `EmployerUserId` bigint(20) NOT NULL,
                                `ProjectID` bigint(20) NOT NULL,
                                `Note` varchar(1000) DEFAULT NULL,
                                `CreatedAt` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `savedproject`
--

INSERT INTO `savedproject` (`SavedId`, `EmployerUserId`, `ProjectID`, `Note`, `CreatedAt`) VALUES
                                                                                               (1, 35, 7, '', '2025-10-24 09:46:07'),
                                                                                               (2, 35, 2, 'ytdyuiop', '2025-10-24 09:59:01'),
                                                                                               (7, 35, 6, 'jam', '2025-10-25 20:14:57');

-- --------------------------------------------------------

--
-- Table structure for table `showcase`
--

CREATE TABLE `showcase` (
                            `ShowcaseId` bigint(20) NOT NULL,
                            `Name` varchar(255) DEFAULT NULL,
                            `Description` varchar(255) DEFAULT NULL,
                            `Image` varchar(255) DEFAULT NULL,
                            `Status` varchar(255) DEFAULT NULL,
                            `CreatedBy` bigint(20) NOT NULL,
                            `CreatedAt` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `showcase`
--

INSERT INTO `showcase` (`ShowcaseId`, `Name`, `Description`, `Image`, `Status`, `CreatedBy`, `CreatedAt`) VALUES
                                                                                                              (1, 'Art Showcase', 'a collection of art pieces to celebrate the beauty of winter', 'chemshowcase.jpg', 'PENDING', 35, '2025-11-07 14:03:44'),
                                                                                                              (2, 'bio-pharma showcase', 'A look at bio-pharma in limerick', 'chemshowcase.jpg', 'LIVE', 35, '2025-11-07 14:03:44'),
                                                                                                              (3, 'Sea times', 'a collection of immersive digital pieces to celebrate the beauty of the sea ', 'artshowcase.jpg', 'LIVE', 35, '2025-11-07 14:03:44'),
                                                                                                              (34, 'jfklv', 'sdgdsg', 'danhengJPG.jpg', 'LIVE', 6, '2025-11-25 03:28:44');

-- --------------------------------------------------------

--
-- Table structure for table `showcaseproject`
--

CREATE TABLE `showcaseproject` (
                                   `ShowcaseProjectID` bigint(20) NOT NULL,
                                   `ProjectId` bigint(20) DEFAULT NULL,
                                   `ShowcaseID` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `showcaseproject`
--

INSERT INTO `showcaseproject` (`ShowcaseProjectID`, `ProjectId`, `ShowcaseID`) VALUES
    (11, 2, 3);

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
                        `userid` bigint(20) NOT NULL,
                        `FirstName` varchar(50) NOT NULL,
                        `surname` varchar(225) DEFAULT NULL,
                        `EmailAddress` varchar(100) NOT NULL,
                        `UserType` varchar(50) NOT NULL,
                        `UserName` varchar(50) NOT NULL,
                        `password` varchar(225) DEFAULT NULL,
                        `organization` varchar(225) DEFAULT NULL,
                        `ProfilePicture` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`userid`, `FirstName`, `surname`, `EmailAddress`, `UserType`, `UserName`, `password`, `organization`, `ProfilePicture`) VALUES
                                                                                                                                                (1, 'TRYANxxx', 'Ryanxxxxx', 'TRyan@gmail.comx', 'STUDENT', 'TRYAN', 'pass123', 'litx', NULL),
                                                                                                                                                (2, 'Mary', 'Smith', 'MS@homail.com', 'STUDENT', 'MSith', '123pass', '', NULL),
                                                                                                                                                (3, 'Peter', 'Carr', 'PC@somebusiness.ie', 'VIEWER', 'PCbus', 'PC123', 'buzz tech - 3d modelling', NULL),
                                                                                                                                                (4, 'Elizabeth', 'Bourke', 'Elizabeth.Bourke@TUS.ie', 'Admin', 'EBourke', 'Bourke123', '', NULL),
                                                                                                                                                (6, 'Tom', 'Smith', 'xx@jj.com', 'ADMIN', 'tsmith', 'pass123', '', '6_1763641255561_kubry.jpg'),
                                                                                                                                                (28, 'Elizabeth', 'Bourke', 'Elizabeth_Bourke@hotmail.com', '', 'EB', '123', 'lit', NULL),
                                                                                                                                                (29, 'Elizabeth2', 'Bourke', 'Elizabeth_Bourke2@hotmail.com', '', '22', '22', '22', NULL),
                                                                                                                                                (30, 'l', 'l', 'Elizabeth_Bourkejj@hotmail.com', '', 'l', 'l', 'l', NULL),
                                                                                                                                                (31, 'Elizabeth', 'Bourke', 'Elizabeth33_Bourke@hotmail.com', '', '33', '33', '33', NULL),
                                                                                                                                                (32, 'Elizabeth', 'Bourke', 'Elizabeth_Bourke66@hotmail.com', 'STUDENT', '66', '66', '66', NULL),
                                                                                                                                                (33, 'Elizabeth', 'Bourke', 'Elizabethmmm_Bourke@hotmail.com', 'STUDENT', 'mm', 'mm', 'mm', NULL),
                                                                                                                                                (34, 'Elizabeth', 'Bourke', 'Elizabethttt_Bourke@hotmail.com', 'STUDENT', 'EB444', '123', 'lit', NULL),
                                                                                                                                                (35, 'jamtest', 'test', 'testjam@gmail.com', 'employer', 'testingjam', 'testjam', 'test', '35_kubry.jpg'),
                                                                                                                                                (36, 'janice', 'xie', 'abc@gmail.com', 'STUDENT', 'test', '12345', '', NULL),
                                                                                                                                                (37, 'emp', 'loyer', 'emp123@gmail.com', 'STUDENT', 'employerTest', 'emp123', '', NULL);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `project`
--
ALTER TABLE `project`
    ADD PRIMARY KEY (`ProjectID`),
  ADD KEY `UserID` (`UserID`);

--
-- Indexes for table `savedproject`
--
ALTER TABLE `savedproject`
    ADD PRIMARY KEY (`SavedId`),
  ADD UNIQUE KEY `uq_employer_project` (`EmployerUserId`,`ProjectID`),
  ADD KEY `fk_saved_project` (`ProjectID`);

--
-- Indexes for table `showcase`
--
ALTER TABLE `showcase`
    ADD PRIMARY KEY (`ShowcaseId`),
  ADD KEY `fk_showcase_user` (`CreatedBy`);

--
-- Indexes for table `showcaseproject`
--
ALTER TABLE `showcaseproject`
    ADD PRIMARY KEY (`ShowcaseProjectID`),
  ADD KEY `ShowcaseID` (`ShowcaseID`),
  ADD KEY `ProjectID` (`ProjectId`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
    ADD PRIMARY KEY (`userid`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `project`
--
ALTER TABLE `project`
    MODIFY `ProjectID` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=41;

--
-- AUTO_INCREMENT for table `savedproject`
--
ALTER TABLE `savedproject`
    MODIFY `SavedId` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `showcase`
--
ALTER TABLE `showcase`
    MODIFY `ShowcaseId` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=35;

--
-- AUTO_INCREMENT for table `showcaseproject`
--
ALTER TABLE `showcaseproject`
    MODIFY `ShowcaseProjectID` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
    MODIFY `userid` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=38;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `project`
--
ALTER TABLE `project`
    ADD CONSTRAINT `FK84avpir3s8alvr5yud0yh8d7b` FOREIGN KEY (`UserID`) REFERENCES `user` (`userid`);

--
-- Constraints for table `savedproject`
--
ALTER TABLE `savedproject`
    ADD CONSTRAINT `fk_saved_employer` FOREIGN KEY (`EmployerUserId`) REFERENCES `user` (`userid`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_saved_project` FOREIGN KEY (`ProjectID`) REFERENCES `project` (`ProjectID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `showcase`
--
ALTER TABLE `showcase`
    ADD CONSTRAINT `fk_showcase_user` FOREIGN KEY (`CreatedBy`) REFERENCES `user` (`userid`) ON DELETE CASCADE;

--
-- Constraints for table `showcaseproject`
--
ALTER TABLE `showcaseproject`
    ADD CONSTRAINT `FK6ge3kqk7na2e01frc8rj08y9w` FOREIGN KEY (`ShowcaseID`) REFERENCES `showcase` (`ShowcaseId`),
  ADD CONSTRAINT `FKgt6y2lqndsh5b1gs0nxci6wk5` FOREIGN KEY (`ProjectId`) REFERENCES `project` (`ProjectID`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
