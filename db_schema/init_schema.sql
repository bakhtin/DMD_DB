SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

CREATE SCHEMA IF NOT EXISTS `dmd_db` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ;
USE `dmd_db` ;

-- -----------------------------------------------------
-- Table `dmd_db`.`issue_name`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dmd_db`.`issue_name` ;

CREATE TABLE IF NOT EXISTS `dmd_db`.`issue_name` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(1023) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dmd_db`.`issue_type`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dmd_db`.`issue_type` ;

CREATE TABLE IF NOT EXISTS `dmd_db`.`issue_type` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `type` VARCHAR(254) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dmd_db`.`affiliation`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dmd_db`.`affiliation` ;

CREATE TABLE IF NOT EXISTS `dmd_db`.`affiliation` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(1023) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dmd_db`.`publisher`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dmd_db`.`publisher` ;

CREATE TABLE IF NOT EXISTS `dmd_db`.`publisher` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dmd_db`.`publication`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dmd_db`.`publication` ;

CREATE TABLE IF NOT EXISTS `dmd_db`.`publication` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(1023) NOT NULL,
  `issn` VARCHAR(9) NULL,
  `isbn` VARCHAR(13) NULL,
  `doi` VARCHAR(31) NULL,
  `pubdate` DATE NULL,
  `pages` VARCHAR(20) NULL,
  `volume` INT NULL,
  `abstract` TEXT NULL,
  `url` VARCHAR(2083) NULL,
  `pub_number` VARCHAR(45) NULL,
  `issue_name_id` INT NULL,
  `issue_type_id` INT NULL,
  `affiliation_id` INT NULL,
  `publisher_id` INT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `issn_UNIQUE` (`issn` ASC),
  UNIQUE INDEX `isbn_UNIQUE` (`isbn` ASC),
  UNIQUE INDEX `doi_UNIQUE` (`doi` ASC),
  INDEX `fk_publication_issue_name1_idx` (`issue_name_id` ASC),
  INDEX `fk_publication_issue_type1_idx` (`issue_type_id` ASC),
  INDEX `fk_publication_affiliation1_idx` (`affiliation_id` ASC),
  INDEX `fk_publication_publisher1_idx` (`publisher_id` ASC),
  CONSTRAINT `fk_publication_issue_name1`
    FOREIGN KEY (`issue_name_id`)
    REFERENCES `dmd_db`.`issue_name` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_publication_issue_type1`
    FOREIGN KEY (`issue_type_id`)
    REFERENCES `dmd_db`.`issue_type` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_publication_affiliation1`
    FOREIGN KEY (`affiliation_id`)
    REFERENCES `dmd_db`.`affiliation` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_publication_publisher1`
    FOREIGN KEY (`publisher_id`)
    REFERENCES `dmd_db`.`publisher` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dmd_db`.`author`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dmd_db`.`author` ;

CREATE TABLE IF NOT EXISTS `dmd_db`.`author` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(254) NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `name_UNIQUE` (`name` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dmd_db`.`keyword_word`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dmd_db`.`keyword` ;

CREATE TABLE IF NOT EXISTS `dmd_db`.`keyword` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `word` VARCHAR(254) NOT NULL,
  `type` ENUM('thesaurusterms', 'controlledterms', 'uncontrolledterms'),
  PRIMARY KEY (`id`),
  UNIQUE INDEX `word_UNIQUE` (`word` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dmd_db`.`publication_author`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dmd_db`.`publication_author` ;

CREATE TABLE IF NOT EXISTS `dmd_db`.`publication_author` (
  `publication_id` INT NOT NULL,
  `author_id` INT NOT NULL,
  PRIMARY KEY (`publication_id`, `author_id`),
  INDEX `fk_publication_has_author_author1_idx` (`author_id` ASC),
  INDEX `fk_publication_has_author_publication_idx` (`publication_id` ASC),
  CONSTRAINT `fk_publication_has_author_publication`
    FOREIGN KEY (`publication_id`)
    REFERENCES `dmd_db`.`publication` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_publication_has_author_author1`
    FOREIGN KEY (`author_id`)
    REFERENCES `dmd_db`.`author` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dmd_db`.`keyword_type`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dmd_db`.`keyword_type` ;

CREATE TABLE IF NOT EXISTS `dmd_db`.`keyword_type` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `type` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `type_UNIQUE` (`type` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dmd_db`.`keyword_word_type`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dmd_db`.`keyword_word_type` ;

CREATE TABLE IF NOT EXISTS `dmd_db`.`keyword_word_type` (
  `keyword_word_id` INT NOT NULL,
  `keyword_type_id` INT NOT NULL,
  `publication_id` INT NOT NULL,
  PRIMARY KEY (`keyword_word_id`, `keyword_type_id`, `publication_id`),
  INDEX `fk_keyword_word_has_keyword_type_keyword_type1_idx` (`keyword_type_id` ASC),
  INDEX `fk_keyword_word_has_keyword_type_keyword_word1_idx` (`keyword_word_id` ASC),
  INDEX `fk_keyword_word_type_publication1_idx` (`publication_id` ASC),
  CONSTRAINT `fk_keyword_word_has_keyword_type_keyword_word1`
    FOREIGN KEY (`keyword_word_id`)
    REFERENCES `dmd_db`.`keyword_word` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_keyword_word_has_keyword_type_keyword_type1`
    FOREIGN KEY (`keyword_type_id`)
    REFERENCES `dmd_db`.`keyword_type` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_keyword_word_type_publication1`
    FOREIGN KEY (`publication_id`)
    REFERENCES `dmd_db`.`publication` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
