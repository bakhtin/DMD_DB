package core;

import core.sys.exceptions.SQLError;
import core.sys.managers.DBManager;
import core.sys.managers.SQL;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         11/4/2015
 */
public class DBTest {
    public static void main(String[] args) throws SQLError {
        DBManager DB = new DBManager("baza.db");
        SQL sql = new SQL();

        sql.execute("CREATE TABLE IF NOT EXISTS dmd_db.issue_name (\n" +
                "  id INT NOT NULL AUTO_INCREMENT,\n" +
                "  name VARCHAR(512) NOT NULL,\n" +
                "  PRIMARY KEY (id),\n" +
                "  UNIQUE INDEX name_UNIQUE (name ASC))\n" +
                "ENGINE = InnoDB;\n" +
                "\n" +
                "\n" +
                "-- -----------------------------------------------------\n" +
                "-- Table dmd_db.issue_type\n" +
                "-- -----------------------------------------------------\n" +
                "CREATE TABLE IF NOT EXISTS dmd_db.issue_type (\n" +
                "  id INT NOT NULL AUTO_INCREMENT,\n" +
                "  type VARCHAR(254) NOT NULL,\n" +
                "  PRIMARY KEY (id),\n" +
                "  UNIQUE INDEX type_UNIQUE (type ASC))\n" +
                "ENGINE = InnoDB;\n" +
                "\n" +
                "\n" +
                "-- -----------------------------------------------------\n" +
                "-- Table dmd_db.affiliation\n" +
                "-- -----------------------------------------------------\n" +
                "CREATE TABLE IF NOT EXISTS dmd_db.affiliation (\n" +
                "  id INT NOT NULL AUTO_INCREMENT,\n" +
                "  name VARCHAR(512) NOT NULL,\n" +
                "  PRIMARY KEY (id),\n" +
                "  UNIQUE INDEX name_UNIQUE (name ASC))\n" +
                "ENGINE = InnoDB;\n" +
                "\n" +
                "\n" +
                "-- -----------------------------------------------------\n" +
                "-- Table dmd_db.publisher\n" +
                "-- -----------------------------------------------------\n" +
                "CREATE TABLE IF NOT EXISTS dmd_db.publisher (\n" +
                "  id INT NOT NULL AUTO_INCREMENT,\n" +
                "  name VARCHAR(45) NOT NULL,\n" +
                "  PRIMARY KEY (id),\n" +
                "  UNIQUE INDEX name_UNIQUE (name ASC))\n" +
                "ENGINE = InnoDB;\n" +
                "\n" +
                "\n" +
                "-- -----------------------------------------------------\n" +
                "-- Table dmd_db.publication\n" +
                "-- -----------------------------------------------------\n" +
                "CREATE TABLE IF NOT EXISTS dmd_db.publication (\n" +
                "  id INT NOT NULL AUTO_INCREMENT,\n" +
                "  title VARCHAR(1023) NOT NULL,\n" +
                "  issn VARCHAR(9) NULL,\n" +
                "  isbn VARCHAR(13) NULL,\n" +
                "  doi VARCHAR(31) NULL,\n" +
                "  pubdate DATE NULL,\n" +
                "  pages VARCHAR(20) NULL,\n" +
                "  volume INT NULL,\n" +
                "  abstract TEXT NULL,\n" +
                "  url VARCHAR(2083) NULL,\n" +
                "  pub_number VARCHAR(45) NULL,\n" +
                "  issue_name_id INT NULL,\n" +
                "  issue_type_id INT NULL,\n" +
                "  affiliation_id INT NULL,\n" +
                "  publisher_id INT NULL,\n" +
                "  PRIMARY KEY (id),\n" +
                "  INDEX fk_publication_issue_name1_idx (issue_name_id ASC),\n" +
                "  INDEX fk_publication_issue_type1_idx (issue_type_id ASC),\n" +
                "  INDEX fk_publication_affiliation1_idx (affiliation_id ASC),\n" +
                "  INDEX fk_publication_publisher1_idx (publisher_id ASC),\n" +
                "  UNIQUE INDEX doi_UNIQUE (doi ASC),\n" +
                "  CONSTRAINT fk_publication_issue_name1\n" +
                "    FOREIGN KEY (issue_name_id)\n" +
                "    REFERENCES dmd_db.issue_name (id)\n" +
                "    ON DELETE NO ACTION\n" +
                "    ON UPDATE NO ACTION,\n" +
                "  CONSTRAINT fk_publication_issue_type1\n" +
                "    FOREIGN KEY (issue_type_id)\n" +
                "    REFERENCES dmd_db.issue_type (id)\n" +
                "    ON DELETE NO ACTION\n" +
                "    ON UPDATE NO ACTION,\n" +
                "  CONSTRAINT fk_publication_affiliation1\n" +
                "    FOREIGN KEY (affiliation_id)\n" +
                "    REFERENCES dmd_db.affiliation (id)\n" +
                "    ON DELETE NO ACTION\n" +
                "    ON UPDATE NO ACTION,\n" +
                "  CONSTRAINT fk_publication_publisher1\n" +
                "    FOREIGN KEY (publisher_id)\n" +
                "    REFERENCES dmd_db.publisher (id)\n" +
                "    ON DELETE NO ACTION\n" +
                "    ON UPDATE NO ACTION)\n" +
                "ENGINE = InnoDB;\n" +
                "\n" +
                "\n" +
                "-- -----------------------------------------------------\n" +
                "-- Table dmd_db.author\n" +
                "-- -----------------------------------------------------\n" +
                "CREATE TABLE IF NOT EXISTS dmd_db.author (\n" +
                "  id INT NOT NULL AUTO_INCREMENT,\n" +
                "  name VARCHAR(254) NULL,\n" +
                "  PRIMARY KEY (id),\n" +
                "  UNIQUE INDEX name_UNIQUE (name ASC))\n" +
                "ENGINE = InnoDB;\n" +
                "\n" +
                "\n" +
                "-- -----------------------------------------------------\n" +
                "-- Table dmd_db.keyword\n" +
                "-- -----------------------------------------------------\n" +
                "CREATE TABLE IF NOT EXISTS dmd_db.keyword (\n" +
                "  id INT NOT NULL AUTO_INCREMENT,\n" +
                "  word VARCHAR(254) NOT NULL,\n" +
                "  PRIMARY KEY (id),\n" +
                "  UNIQUE INDEX word_UNIQUE (word ASC))\n" +
                "ENGINE = InnoDB;\n" +
                "\n" +
                "\n" +
                "-- -----------------------------------------------------\n" +
                "-- Table dmd_db.publication_author\n" +
                "-- -----------------------------------------------------\n" +
                "CREATE TABLE IF NOT EXISTS dmd_db.publication_author (\n" +
                "  publication_id INT NOT NULL,\n" +
                "  author_id INT NOT NULL,\n" +
                "  PRIMARY KEY (publication_id, author_id),\n" +
                "  INDEX fk_publication_has_author_author1_idx (author_id ASC),\n" +
                "  INDEX fk_publication_has_author_publication_idx (publication_id ASC),\n" +
                "  CONSTRAINT fk_publication_has_author_publication\n" +
                "    FOREIGN KEY (publication_id)\n" +
                "    REFERENCES dmd_db.publication (id)\n" +
                "    ON DELETE NO ACTION\n" +
                "    ON UPDATE NO ACTION,\n" +
                "  CONSTRAINT fk_publication_has_author_author1\n" +
                "    FOREIGN KEY (author_id)\n" +
                "    REFERENCES dmd_db.author (id)\n" +
                "    ON DELETE NO ACTION\n" +
                "    ON UPDATE NO ACTION)\n" +
                "ENGINE = InnoDB;\n" +
                "\n" +
                "\n" +
                "-- -----------------------------------------------------\n" +
                "-- Table dmd_db.publication_keyword\n" +
                "-- -----------------------------------------------------\n" +
                "CREATE TABLE IF NOT EXISTS dmd_db.publication_keyword (\n" +
                "  keyword_id INT NOT NULL,\n" +
                "  publication_id INT NOT NULL,\n" +
                "  type ENUM('thesaurusterms','controlledterms','uncontrolledterms') NOT NULL,\n" +
                "  PRIMARY KEY (keyword_id, publication_id, type),\n" +
                "  INDEX fk_keyword_has_publication_publication1_idx (publication_id ASC),\n" +
                "  INDEX fk_keyword_has_publication_keyword1_idx (keyword_id ASC),\n" +
                "  CONSTRAINT fk_keyword_has_publication_keyword1\n" +
                "    FOREIGN KEY (keyword_id)\n" +
                "    REFERENCES dmd_db.keyword (id)\n" +
                "    ON DELETE NO ACTION\n" +
                "    ON UPDATE NO ACTION,\n" +
                "  CONSTRAINT fk_keyword_has_publication_publication1\n" +
                "    FOREIGN KEY (publication_id)\n" +
                "    REFERENCES dmd_db.publication (id)\n" +
                "    ON DELETE NO ACTION\n" +
                "    ON UPDATE NO ACTION)\n" +
                "ENGINE = InnoDB;\n" +
                "\n" +
                "\n" +
                "-- -----------------------------------------------------\n" +
                "-- Table dmd_db.publication_keyword\n" +
                "-- -----------------------------------------------------\n" +
                "CREATE TABLE IF NOT EXISTS dmd_db.publication_keyword (\n" +
                "  keyword_id INT NOT NULL,\n" +
                "  publication_id INT NOT NULL,\n" +
                "  type ENUM('thesaurusterms','controlledterms','uncontrolledterms') NOT NULL,\n" +
                "  PRIMARY KEY (keyword_id, publication_id, type),\n" +
                "  INDEX fk_keyword_has_publication_publication1_idx (publication_id ASC),\n" +
                "  INDEX fk_keyword_has_publication_keyword1_idx (keyword_id ASC),\n" +
                "  CONSTRAINT fk_keyword_has_publication_keyword1\n" +
                "    FOREIGN KEY (keyword_id)\n" +
                "    REFERENCES dmd_db.keyword (id)\n" +
                "    ON DELETE NO ACTION\n" +
                "    ON UPDATE NO ACTION,\n" +
                "  CONSTRAINT fk_keyword_has_publication_publication1\n" +
                "    FOREIGN KEY (publication_id)\n" +
                "    REFERENCES dmd_db.publication (id)\n" +
                "    ON DELETE NO ACTION\n" +
                "    ON UPDATE NO ACTION)\n" +
                "ENGINE = InnoDB;\n" +
                "\n" +
                "\n" +
                "-- -----------------------------------------------------\n" +
                "-- Table dmd_db.user\n" +
                "-- -----------------------------------------------------\n" +
                "CREATE TABLE IF NOT EXISTS dmd_db.user (\n" +
                "  id INT NOT NULL AUTO_INCREMENT,\n" +
                "  login VARCHAR(45) NOT NULL,\n" +
                "  password VARCHAR(78) NOT NULL,\n" +
                "  PRIMARY KEY (id),\n" +
                "  UNIQUE INDEX login_UNIQUE (login ASC))\n" +
                "ENGINE = InnoDB;\n" +
                "\n" +
                "\n" +
                "-- -----------------------------------------------------\n" +
                "-- Table dmd_db.session\n" +
                "-- -----------------------------------------------------\n" +
                "CREATE TABLE IF NOT EXISTS dmd_db.session (\n" +
                "  session_id VARCHAR(60) NOT NULL,\n" +
                "  user_id INT NULL,\n" +
                "  expire_time DATETIME NULL,\n" +
                "  INDEX fk_session_users1_idx (user_id ASC),\n" +
                "  PRIMARY KEY (user_id),\n" +
                "  UNIQUE INDEX session_id_UNIQUE (session_id ASC),\n" +
                "  CONSTRAINT fk_session_users1\n" +
                "    FOREIGN KEY (user_id)\n" +
                "    REFERENCES dmd_db.user (id)\n" +
                "    ON DELETE NO ACTION\n" +
                "    ON UPDATE NO ACTION)\n" +
                "ENGINE = InnoDB;\n" +
                "\n" +
                "\n" +
                "-- -----------------------------------------------------\n" +
                "-- Table dmd_db.role\n" +
                "-- -----------------------------------------------------\n" +
                "CREATE TABLE IF NOT EXISTS dmd_db.role (\n" +
                "  id INT NOT NULL AUTO_INCREMENT,\n" +
                "  role VARCHAR(45) NULL,\n" +
                "  PRIMARY KEY (id))\n" +
                "ENGINE = InnoDB;\n" +
                "\n" +
                "\n" +
                "-- -----------------------------------------------------\n" +
                "-- Table dmd_db.user_role\n" +
                "-- -----------------------------------------------------\n" +
                "CREATE TABLE IF NOT EXISTS dmd_db.user_role (\n" +
                "  user_id INT NOT NULL,\n" +
                "  role_id INT NOT NULL,\n" +
                "  PRIMARY KEY (user_id, role_id),\n" +
                "  INDEX fk_users_has_role_role1_idx (role_id ASC),\n" +
                "  INDEX fk_users_has_role_users1_idx (user_id ASC),\n" +
                "  CONSTRAINT fk_users_has_role_users1\n" +
                "    FOREIGN KEY (user_id)\n" +
                "    REFERENCES dmd_db.user (id)\n" +
                "    ON DELETE NO ACTION\n" +
                "    ON UPDATE NO ACTION,\n" +
                "  CONSTRAINT fk_users_has_role_role1\n" +
                "    FOREIGN KEY (role_id)\n" +
                "    REFERENCES dmd_db.role (id)\n" +
                "    ON DELETE NO ACTION\n" +
                "    ON UPDATE NO ACTION)\n" +
                "ENGINE = InnoDB;");

    }
}
