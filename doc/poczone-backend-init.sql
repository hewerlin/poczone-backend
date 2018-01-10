CREATE TABLE `users` (
	`userID` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	`username` VARCHAR(50) NOT NULL,
	`password` CHAR(97) NOT NULL,
	PRIMARY KEY (`userID`),
	UNIQUE INDEX `uniqueUsername` (`username`)
)
COLLATE='utf8_general_ci'
ENGINE=MyISAM;

CREATE TABLE `spaces` (
	`spaceID` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	`spaceUUID` CHAR(12) NOT NULL DEFAULT '0',
	`name` VARCHAR(50) NOT NULL DEFAULT '0',
	`app` VARCHAR(10) NOT NULL DEFAULT '0',
	PRIMARY KEY (`spaceID`),
	UNIQUE INDEX `Schlüssel 2` (`spaceUUID`)
)
COLLATE='utf8_general_ci'
ENGINE=MyISAM;

CREATE TABLE `spaceusers` (
	`userID` INT(10) UNSIGNED NOT NULL,
	`spaceID` INT(10) UNSIGNED NOT NULL,
	`level` ENUM('READER','WRITER','ADMIN') NOT NULL,
	PRIMARY KEY (`userID`, `spaceID`)
)
COLLATE='utf8_general_ci'
ENGINE=MyISAM;

CREATE TABLE `spacedata` (
	`spaceID` INT(10) UNSIGNED NOT NULL,
	`itemID` VARCHAR(50) NOT NULL COLLATE 'utf8_bin',
	`modified` BIGINT(20) UNSIGNED NOT NULL,
	`json` LONGTEXT NOT NULL,
	PRIMARY KEY (`spaceID`, `itemID`),
	INDEX `byChangeDate` (`spaceID`, `modified`)
)
COLLATE='utf8_general_ci'
ENGINE=MyISAM;

CREATE TABLE `coupons` (
	`couponID` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	`coupon` CHAR(12) NOT NULL COLLATE 'ascii_general_ci',
	`spaceID` INT(10) UNSIGNED NOT NULL,
	`level` ENUM('READER','WRITER','ADMIN') NOT NULL,
	`usedCount` INT(10) UNSIGNED NOT NULL DEFAULT '0',
	`usedLimit` INT(10) UNSIGNED NULL DEFAULT NULL,
	`expireTime` TIMESTAMP NULL DEFAULT NULL,
	PRIMARY KEY (`couponID`),
	UNIQUE INDEX `uniqueCoupon` (`coupon`)
)
COLLATE='utf8_general_ci'
ENGINE=MyISAM;
