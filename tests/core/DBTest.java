package core;

import core.sys.exceptions.RecordStatus;
import core.sys.exceptions.SQLError;
import core.sys.managers.DBManager;

import java.io.IOException;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         11/4/2015
 */
public class DBTest {
    public static void main(String[] args) throws SQLError, IOException, RecordStatus {
        DBManager DB = new DBManager("baza.db");

        DB.cursor.execute("CREATE TABLE affiliation (\n" +
                "  id int(11) NOT NULL AUTO_INCREMENT,\n" +
                "  name varchar(512) NOT NULL,\n" +
                "  PRIMARY KEY (id),\n" +
                "  UNIQUE KEY name_UNIQUE (name)\n" +
                ") ENGINE=InnoDB AUTO_INCREMENT=291323 DEFAULT CHARSET=latin1;\n" +
                "INSERT INTO affiliation VALUES (110894,'\\\"A.I. Cuza\\\" Univ., Iasi, Romania'),(116002,'\\\"Al. I. Cuza\\\" Univ., Romania'),(34860,'\\\"Almaz\\\" Sci. Ind. Corp., Moscow'),(88740,'\\\"ARGUS-RPH\\\" Ltd., St. Petersburg, Russia'),(68438,'\\\"Aurel Vlaicu\\\" Univ. of Arad'),(118322,'\\\"Boris Kidric\\\" Institute of Nuclear Science, Vinca, Belgrade, Yugoslavia'),(252683,'\\\"Boris Kidric\\\", Belgrade, Yugoslavia'),(91865,'\\\"Circuit Qualification\\\" Res. Group, TIMA Lab., Grenoble, France'),(234439,'\\\"D.Obradovic\\\" High Sch., Novi Knezevac, Serbia'),(70359,'\\\"De Montfort\\'\\' University'),(151764,'\\\"DELTA\\\" S.P.E., Kiev, Ukraine'),(187701,'\\\"Dunarea de Jos\\\" Univ. of Galati'),(73000,'\\\"E. De Castro\\\" Advanced Research Center on Electronic Systems and Department of Electronics, Computer Science and Systems, University of Bologna, Viale Risorgimento 2, 1-40136 Bologna, Italy. Tel. +39-051-20-93016, Fax. -93779, E-mail: mrudan@arces.unibo.it'),(246335,'\\\"Eye Microsurgery\\\" Research & Technology Center'),(171769,'\\\"Gh. Asachi\\\" Tech. Univ., Iasi, Romania'),(26303,'\\\"Gh. Asachi\\\" Technical University'),(26301,'\\\"Gh. Asachi\\\" Technical University of Iasi'),(72590,'\\\"Inf. & Meas. Equip.\\\" Chair, Ufa State Aviation Tech. Univ., Russia'),(230149,'\\\"Istituto di Elettrotecnica\\\", Roma, Italy'),(88756,'\\\"Istok\\\" Fed. State-Owned Unitary Res. & Production Enterprise, Moscow Region, Russia'),(261786,'\\\"Kerry\\\" Barnham Sussex, England'),(246436,'\\\"KPI\\\" Res. Inst. of Telecommun., Ukraine Nat. Tech. Univ., Kiev'),(173077,'\\\"La Sapienza\\\" University of Rome, Rome, Italy'),(175714,'\\\"Laboratoire de G&#233;nie Industriel et de Production M&#233;canique\\\", Universit&#233; de Metz - LGIPM-CEMA - Ile du Saulcy, F-57045 METZ Cedex 01, sauvey@univ-metz.fr'),(91382,'\\\"Le Moulin de Paradis\\\", Martiques, France'),(34790,'\\\"LETI\\\", St. Petersburg ElectroTechnical Univ., Russia'),(147951,'\\\"Lucian Blaga\\\" Univ., Sibiu, Romania'),(171007,'\\\"Matematica Aplicada\\\" Dept., Univ. of Valladolid, Spain'),(63925,'\\\"Nello Carrara\\\" Res. Inst. on Electromagn. Waves (IROE), CNR, Florence, Italy'),(212064,'\\\"Ovidius\\\" Univ., Constanta, Romania')");

    }
}
