package core.sqlparser;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TBaseType;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.*;
import gudusoft.gsqlparser.stmt.*;
import org.junit.Test;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 * @date 10/21/2015
 */
public class SQLParserTest {
    @Test
    public void testSQL(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);  // db vendor
        /*             if (db == 1){
                 dbVendor = EDbVendor.dbvmssql;
             }else if(db == 2){
                 dbVendor = EDbVendor.dbvoracle;
             }else if(db == 3){
                 dbVendor = EDbVendor.dbvmysql;
             }else if(db == 4){
                 dbVendor = EDbVendor.dbvdb2;
             }else if(db == 5){
                 dbVendor = EDbVendor.dbvpostgresql;
             }else if(db == 6){
                 dbVendor = EDbVendor.dbvteradata;
             }*/

        /*sqlparser.sqltext = "CREATE TABLE dmd_db.issue_type ("+
        "id INT, "+
        "type VARCHAR(254),"+
        "PRIMARY KEY (id))"+
        "ENGINE = InnoDB";*/
        /*sqlparser.sqltext = "SELECT e.last_name AS name,\n" +
                " e.commission_pct comm,\n" +
                " e.salary * 12 \"Annual Salary\"\n" +
                "FROM scott.employees AS e\n" +
                "WHERE e.salary > 1000\n" +
                "ORDER BY\n" +
                " e.first_name,\n" +
                " e.last_name;";*/

        //sqlparser.sqltext = "INSERT INTO `affiliation` VALUES (110894,'\\\"A.I. Cuza\\\" Univ., Iasi, Romania'),(116002,'\\\"Al. I. Cuza\\\" Univ., Romania'),(34860,'\\\"Almaz\\\" Sci. Ind. Corp., Moscow'),(88740,'\\\"ARGUS-RPH\\\" Ltd., St. Petersburg, Russia'),(68438,'\\\"Aurel Vlaicu\\\" Univ. of Arad'),(118322,'\\\"Boris Kidric\\\" Institute of Nuclear Science, Vinca, Belgrade, Yugoslavia'),(252683,'\\\"Boris Kidric\\\", Belgrade, Yugoslavia'),(91865,'\\\"Circuit Qualification\\\" Res. Group, TIMA Lab., Grenoble, France'),(234439,'\\\"D.Obradovic\\\" High Sch., Novi Knezevac, Serbia'),(70359,'\\\"De Montfort\\'\\' University'),(151764,'\\\"DELTA\\\" S.P.E., Kiev, Ukraine'),(187701,'\\\"Dunarea de Jos\\\" Univ. of Galati'),(73000,'\\\"E. De Castro\\\" Advanced Research Center on Electronic Systems and Department of Electronics, Computer Science and Systems, University of Bologna, Viale Risorgimento 2, 1-40136 Bologna, Italy. Tel. +39-051-20-93016, Fax. -93779, E-mail: mrudan@arces.unibo.it'),(246335,'\\\"Eye Microsurgery\\\" Research & Technology Center'),(171769,'\\\"Gh. Asachi\\\" Tech. Univ., Iasi, Romania'),(26303,'\\\"Gh. Asachi\\\" Technical University'),(26301,'\\\"Gh. Asachi\\\" Technical University of Iasi'),(72590,'\\\"Inf. & Meas. Equip.\\\" Chair, Ufa State Aviation Tech. Univ., Russia'),(230149,'\\\"Istituto di Elettrotecnica\\\", Roma, Italy'),(88756,'\\\"Istok\\\" Fed. State-Owned Unitary Res. & Production Enterprise, Moscow Region, Russia'),(261786,'\\\"Kerry\\\" Barnham Sussex, England'),(246436,'\\\"KPI\\\" Res. Inst. of Telecommun., Ukraine Nat. Tech. Univ., Kiev'),(173077,'\\\"La Sapienza\\\" University of Rome, Rome, Italy'),(175714,'\\\"Laboratoire de G&#233;nie Industriel et de Production M&#233;canique\\\", Universit&#233; de Metz - LGIPM-CEMA - Ile du Saulcy, F-57045 METZ Cedex 01, sauvey@univ-metz.fr'),(91382,'\\\"Le Moulin de Paradis\\\", Martiques, France'),(34790,'\\\"LETI\\\", St. Petersburg ElectroTechnical Univ., Russia'),(147951,'\\\"Lucian Blaga\\\" Univ., Sibiu, Romania'),(171007,'\\\"Matematica Aplicada\\\" Dept., Univ. of Valladolid, Spain'),(63925,'\\\"Nello Carrara\\\" Res. Inst. on Electromagn. Waves (IROE), CNR, Florence, Italy'),(212064,'\\\"Ovidius\\\" Univ., Constanta, Romania'),(70365,'\\\"Politehnica University of Bucharest'),(111949,'\\\"Politehnica\\\" Bucharest, CETTI, Bucharest'),(99358,'\\\"Politehnica\\\" Univ. of Bucharest, Romania'),(134234,'\\\"Politehnica\\\" Univ., Bucharest, Romania'),(93899,'\\\"Politehnica\\\" University of Bucharest'),(260199,'\\\"Politehnica\\\" University of Bucharest,'),(70369,'\\\"Politehnica\\\" University of Timisoara'),(70362,'\\\"Politehnica\\\" University Timisoara'),(238426,'\\\"Rade Koncar\\\" -Institute, Zagreb, Yugoslavia'),(261479,'\\\"RADEM\\\" Joint-Stock Company, Moscow, Russia'),(263013,'\\\"Radioavionika\\\" Corp., Saint-Petersburg, USA'),(234253,'\\\"Radiosrtim\\\" Ltd., Moscow, Russia'),(112744,'\\\"Rafael\\\", Israel'),(264536,'\\\"RMI\\\", Dnepropertrovsk, Ukraine'),(86793,'\\\"Rudi Cajavec\\\" Beograd, Yugoslavija'),(88749,'\\\"Sci-Res. Inst. of Radio Meas.\\\", JSC, Kharkov, Ukraine'),(171718,'\\\"Stefan eel Mare\\\" Univ., Suceava, Romania'),(88650,'\\\"Svetlana-Elektronpribor\\\" Design Bur., St.Petersburg, Russia'),(32109,'\\\"TAMag Iberica S.L\\\", Madrid, Spain'),(138048,'\\\"Telecommun.\\\" Fac., Nat. Univ. \\\"Lvivska Polytechnika\\\", Lviv, Ukraine'),(26310,'\\\"Transilvania\\\" University of Brasov'),(217646,'#84, 3809-45 St., Calgary, Alta., Canada'),(290966,'& Square Lane, Pittsford, NY, USA'),(86279,'&#197;bo Akademi'),(236348,'&#199;ukurova University, Adana, Turkey'),(86199,'&#201;cole Polytechnique de Montr&#233;al'),(66827,'&#201;cole Polytechnique de Montr&#233;al, Canada'),(74320,'&#201;cole Polytechnique F&#233;d&#233;rale de Lausanne, CH-1015 Switzerland');";
        sqlparser.sqltext = "DROP TABLE IF EXISTS `role`;";//"CREATE TABLE `affiliation` (`id` int(11) NOT NULL AUTO_INCREMENT,`name` varchar(512) NOT NULL,PRIMARY KEY (`id`),UNIQUE KEY `name_UNIQUE` (`name`)) ENGINE=InnoDB AUTO_INCREMENT=291323 DEFAULT CHARSET=latin1;";
        int ret = sqlparser.parse();
        if (ret == 0){
            for(int i=0;i<sqlparser.sqlstatements.size();i++){
                analyzeStmt(sqlparser.sqlstatements.get(i));
                System.out.println("");
            }
        }else{
            System.out.println(sqlparser.getErrormessage());
        }

    }

    protected static void analyzeStmt(TCustomSqlStatement stmt){

        switch(stmt.sqlstatementtype){
            case sstselect:
                analyzeSelectStmt((TSelectSqlStatement)stmt);
                break;
            case sstupdate:
                analyzeUpdateStmt((TUpdateSqlStatement) stmt);
                break;
            case sstinsert:
                analyzeInsertStmt((TInsertSqlStatement) stmt);
                break;
            case sstcreatetable:
                analyzeCreateTableStmt((TCreateTableSqlStatement) stmt);
                break;
            case sstaltertable:
                analyzeAlterTableStmt((TAlterTableStatement) stmt);
                break;
            case sstcreateview:
                analyzeCreateViewStmt((TCreateViewSqlStatement) stmt);
                break;
            default:
                System.out.println(stmt.sqlstatementtype.toString());
        }
    }

    protected static void printConstraint(TConstraint constraint, Boolean outline) {

        if (constraint.getConstraintName() != null) {
            System.out.println("\t\tconstraint name:" + constraint.getConstraintName().toString());
        }

        switch (constraint.getConstraint_type()) {
            case notnull:
                System.out.println("\t\tnot null");
                break;
            case primary_key:
                System.out.println("\t\tprimary key");
                if (outline) {
                    String lcstr = "";
                    if (constraint.getColumnList() != null) {
                        for (int k = 0; k < constraint.getColumnList().size(); k++) {
                            if (k != 0) {
                                lcstr = lcstr + ",";
                            }
                            lcstr = lcstr + constraint.getColumnList().getObjectName(k).toString();
                        }
                        System.out.println("\t\tprimary key columns:" + lcstr);
                    }
                }
                break;
            case unique:
                System.out.println("\t\tunique key");
                if (outline) {
                    String lcstr = "";
                    if (constraint.getColumnList() != null) {
                        for (int k = 0; k < constraint.getColumnList().size(); k++) {
                            if (k != 0) {
                                lcstr = lcstr + ",";
                            }
                            lcstr = lcstr + constraint.getColumnList().getObjectName(k).toString();
                        }
                    }
                    System.out.println("\t\tcolumns:" + lcstr);
                }
                break;
            case check:
                System.out.println("\t\tcheck:" + constraint.getCheckCondition().toString());
                break;
            case foreign_key:
            case reference:
                System.out.println("\t\tforeign key");
                if (outline) {
                    String lcstr = "";
                    if (constraint.getColumnList() != null) {
                        for (int k = 0; k < constraint.getColumnList().size(); k++) {
                            if (k != 0) {
                                lcstr = lcstr + ",";
                            }
                            lcstr = lcstr + constraint.getColumnList().getObjectName(k).toString();
                        }
                    }
                    System.out.println("\t\tcolumns:" + lcstr);
                }
                System.out.println("\t\treferenced table:" + constraint.getReferencedObject().toString());
                if (constraint.getReferencedColumnList() != null) {
                    String lcstr = "";
                    for (int k = 0; k < constraint.getReferencedColumnList().size(); k++) {
                        if (k != 0) {
                            lcstr = lcstr + ",";
                        }
                        lcstr = lcstr + constraint.getReferencedColumnList().getObjectName(k).toString();
                    }
                    System.out.println("\t\treferenced columns:" + lcstr);
                }
                break;
            default:
                break;
        }
    }

    protected static void printObjectNameList(TObjectNameList objList) {
        for (int i = 0; i < objList.size(); i++) {
            System.out.println(objList.getObjectName(i).toString());
        }

    }

    protected static void printColumnDefinitionList(TColumnDefinitionList cdl) {
        for (int i = 0; i < cdl.size(); i++) {
            System.out.println(cdl.getColumn(i).getColumnName());
        }
    }

    protected static void printConstraintList(TConstraintList cnl) {
        for (int i = 0; i < cnl.size(); i++) {
            printConstraint(cnl.getConstraint(i), true);
        }
    }

    protected static void printAlterTableOption(TAlterTableOption ato) {
        System.out.println(ato.getOptionType());
        switch (ato.getOptionType()) {
            case AddColumn:
                printColumnDefinitionList(ato.getColumnDefinitionList());
                break;
            case ModifyColumn:
                printColumnDefinitionList(ato.getColumnDefinitionList());
                break;
            case AlterColumn:
                System.out.println(ato.getColumnName().toString());
                break;
            case DropColumn:
                System.out.println(ato.getColumnName().toString());
                break;
            case SetUnUsedColumn:  //oracle
                printObjectNameList(ato.getColumnNameList());
                break;
            case DropUnUsedColumn:
                break;
            case DropColumnsContinue:
                break;
            case RenameColumn:
                System.out.println("rename " + ato.getColumnName().toString() + " to " + ato.getNewColumnName().toString());
                break;
            case ChangeColumn:   //MySQL
                System.out.println(ato.getColumnName().toString());
                printColumnDefinitionList(ato.getColumnDefinitionList());
                break;
            case RenameTable:   //MySQL
                System.out.println(ato.getColumnName().toString());
                break;
            case AddConstraint:
                printConstraintList(ato.getConstraintList());
                break;
            case AddConstraintIndex:    //MySQL
                if (ato.getColumnName() != null) {
                    System.out.println(ato.getColumnName().toString());
                }
                printObjectNameList(ato.getColumnNameList());
                break;
            case AddConstraintPK:
            case AddConstraintUnique:
            case AddConstraintFK:
                if (ato.getConstraintName() != null) {
                    System.out.println(ato.getConstraintName().toString());
                }
                printObjectNameList(ato.getColumnNameList());
                break;
            case ModifyConstraint:
                System.out.println(ato.getConstraintName().toString());
                break;
            case RenameConstraint:
                System.out.println("rename " + ato.getConstraintName().toString() + " to " + ato.getNewConstraintName().toString());
                break;
            case DropConstraint:
                System.out.println(ato.getConstraintName().toString());
                break;
            case DropConstraintPK:
                break;
            case DropConstraintFK:
                System.out.println(ato.getConstraintName().toString());
                break;
            case DropConstraintUnique:
                if (ato.getConstraintName() != null) { //db2
                    System.out.println(ato.getConstraintName());
                }

                if (ato.getColumnNameList() != null) {//oracle
                    printObjectNameList(ato.getColumnNameList());
                }
                break;
            case DropConstraintCheck: //db2
                System.out.println(ato.getConstraintName());
                break;
            case DropConstraintPartitioningKey:
                break;
            case DropConstraintRestrict:
                break;
            case DropConstraintIndex:
                System.out.println(ato.getConstraintName());
                break;
            case DropConstraintKey:
                System.out.println(ato.getConstraintName());
                break;
            case AlterConstraintFK:
                System.out.println(ato.getConstraintName());
                break;
            case AlterConstraintCheck:
                System.out.println(ato.getConstraintName());
                break;
            case CheckConstraint:
                break;
            case OraclePhysicalAttrs:
            case toOracleLogClause:
            case OracleTableP:
            case MssqlEnableTrigger:
            case MySQLTableOptons:
            case Db2PartitioningKeyDef:
            case Db2RestrictOnDrop:
            case Db2Misc:
            case Unknown:
                break;
        }

    }

    protected static void analyzeCreateViewStmt(TCreateViewSqlStatement pStmt) {
        TCreateViewSqlStatement createView = pStmt;
        System.out.println("View name:" + createView.getViewName().toString());
        TViewAliasClause aliasClause = createView.getViewAliasClause();
        for (int i = 0; i < aliasClause.getViewAliasItemList().size(); i++) {
            System.out.println("View alias:" + aliasClause.getViewAliasItemList().getViewAliasItem(i).toString());
        }

        System.out.println("View subquery: \n" + createView.getSubquery().toString());
    }

    protected static void analyzeSelectStmt(TSelectSqlStatement pStmt){
        System.out.println("\nSelect:");
        if (pStmt.isCombinedQuery()){
            String setstr="";
            switch (pStmt.getSetOperator()){
                case 1: setstr = "union";break;
                case 2: setstr = "union all";break;
                case 3: setstr = "intersect";break;
                case 4: setstr = "intersect all";break;
                case 5: setstr = "minus";break;
                case 6: setstr = "minus all";break;
                case 7: setstr = "except";break;
                case 8: setstr = "except all";break;
            }
            System.out.printf("set type: %s\n",setstr);
            System.out.println("left select:");
            analyzeSelectStmt(pStmt.getLeftStmt());
            System.out.println("right select:");
            analyzeSelectStmt(pStmt.getRightStmt());
            if (pStmt.getOrderbyClause() != null){
                System.out.printf("order by clause %s\n",pStmt.getOrderbyClause().toString());
            }
        }else{
            //select list
            for(int i=0; i < pStmt.getResultColumnList().size();i++){
                TResultColumn resultColumn = pStmt.getResultColumnList().getResultColumn(i);
                System.out.printf("Column: %s, Alias: %s\n", resultColumn.getExpr().toString(), (resultColumn.getAliasClause() == null) ? "" : resultColumn.getAliasClause().toString());
            }

            //from clause, check this document for detailed information
            //http://www.sqlparser.com/sql-parser-query-join-table.php
            for(int i=0;i<pStmt.joins.size();i++){
                TJoin join = pStmt.joins.getJoin(i);
                switch (join.getKind()){
                    case TBaseType.join_source_fake:
                        System.out.printf("table: %s, alias: %s\n", join.getTable().toString(), (join.getTable().getAliasClause() != null) ? join.getTable().getAliasClause().toString() : "");
                        break;
                    case TBaseType.join_source_table:
                        System.out.printf("table: %s, alias: %s\n", join.getTable().toString(), (join.getTable().getAliasClause() != null) ? join.getTable().getAliasClause().toString() : "");
                        for(int j=0;j<join.getJoinItems().size();j++){
                            TJoinItem joinItem = join.getJoinItems().getJoinItem(j);
                            System.out.printf("Join type: %s\n",joinItem.getJoinType().toString());
                            System.out.printf("table: %s, alias: %s\n",joinItem.getTable().toString(),(joinItem.getTable().getAliasClause() !=null)?joinItem.getTable().getAliasClause().toString():"");
                            if (joinItem.getOnCondition() != null){
                                System.out.printf("On: %s\n",joinItem.getOnCondition().toString());
                            }else  if (joinItem.getUsingColumns() != null){
                                System.out.printf("using: %s\n",joinItem.getUsingColumns().toString());
                            }
                        }
                        break;
                    case TBaseType.join_source_join:
                        TJoin source_join = join.getJoin();
                        System.out.printf("table: %s, alias: %s\n", source_join.getTable().toString(), (source_join.getTable().getAliasClause() != null) ? source_join.getTable().getAliasClause().toString() : "");

                        for(int j=0;j<source_join.getJoinItems().size();j++){
                            TJoinItem joinItem = source_join.getJoinItems().getJoinItem(j);
                            System.out.printf("source_join type: %s\n",joinItem.getJoinType().toString());
                            System.out.printf("table: %s, alias: %s\n",joinItem.getTable().toString(),(joinItem.getTable().getAliasClause() !=null)?joinItem.getTable().getAliasClause().toString():"");
                            if (joinItem.getOnCondition() != null){
                                System.out.printf("On: %s\n",joinItem.getOnCondition().toString());
                            }else  if (joinItem.getUsingColumns() != null){
                                System.out.printf("using: %s\n",joinItem.getUsingColumns().toString());
                            }
                        }

                        for(int j=0;j<join.getJoinItems().size();j++){
                            TJoinItem joinItem = join.getJoinItems().getJoinItem(j);
                            System.out.printf("Join type: %s\n",joinItem.getJoinType().toString());
                            System.out.printf("table: %s, alias: %s\n",joinItem.getTable().toString(),(joinItem.getTable().getAliasClause() !=null)?joinItem.getTable().getAliasClause().toString():"");
                            if (joinItem.getOnCondition() != null){
                                System.out.printf("On: %s\n",joinItem.getOnCondition().toString());
                            }else  if (joinItem.getUsingColumns() != null){
                                System.out.printf("using: %s\n",joinItem.getUsingColumns().toString());
                            }
                        }

                        break;
                    default:
                        System.out.println("unknown type in join!");
                        break;
                }
            }

            //where clause
            if (pStmt.getWhereClause() != null){
                System.out.printf("where clause: \n%s\n", pStmt.getWhereClause().toString());
            }

            // group by
            if (pStmt.getGroupByClause() != null){
                System.out.printf("group by: \n%s\n", pStmt.getGroupByClause().toString());
            }

            // order by
            if (pStmt.getOrderbyClause() != null){
                System.out.printf("order by: \n%s\n", pStmt.getOrderbyClause().toString());
            }

            // for update
            if (pStmt.getForUpdateClause() != null){
                System.out.printf("for update: \n%s\n",pStmt.getForUpdateClause().toString());
            }

            // top clause
            if (pStmt.getTopClause() != null){
                System.out.printf("top clause: \n%s\n",pStmt.getTopClause().toString());
            }

            // limit clause
            if (pStmt.getLimitClause() != null){
                System.out.printf("top clause: \n%s\n",pStmt.getLimitClause().toString());
            }
        }
    }

    protected static void analyzeInsertStmt(TInsertSqlStatement pStmt) {
        if (pStmt.getTargetTable() != null) {
            System.out.println("Table name:" + pStmt.getTargetTable().toString());
        }

        System.out.println("insert value type:" + pStmt.getValueType());

        if (pStmt.getColumnList() != null) {
            System.out.println("columns:");
            for (int i = 0; i < pStmt.getColumnList().size(); i++) {
                System.out.println("\t" + pStmt.getColumnList().getObjectName(i).toString());
            }
        }

        if (pStmt.getValues() != null) {
            System.out.println("values:");
            for (int i = 0; i < pStmt.getValues().size(); i++) {
                TMultiTarget mt = pStmt.getValues().getMultiTarget(i);
                for (int j = 0; j < mt.getColumnList().size(); j++) {
                    System.out.println("\t" + mt.getColumnList().getResultColumn(j).toString());
                }
            }
        }

        if (pStmt.getSubQuery() != null) {
            analyzeSelectStmt(pStmt.getSubQuery());
        }
    }

    protected static void analyzeUpdateStmt(TUpdateSqlStatement pStmt) {
        System.out.println("Table Name:" + pStmt.getTargetTable().toString());
        System.out.println("set clause:");
        for (int i = 0; i < pStmt.getResultColumnList().size(); i++) {
            TResultColumn resultColumn = pStmt.getResultColumnList().getResultColumn(i);
            TExpression expression = resultColumn.getExpr();
            System.out.println("\tcolumn:" + expression.getLeftOperand().toString() + "\tvalue:" + expression.getRightOperand().toString());
        }
        if (pStmt.getWhereClause() != null) {
            System.out.println("where clause:\n" + pStmt.getWhereClause().getCondition().toString());
        }
    }

    protected static void analyzeAlterTableStmt(TAlterTableStatement pStmt) {
        System.out.println("Table Name:" + pStmt.getTableName().toString());
        System.out.println("Alter table options:");
        for (int i = 0; i < pStmt.getAlterTableOptionList().size(); i++) {
            printAlterTableOption(pStmt.getAlterTableOptionList().getAlterTableOption(i));
        }
    }

    protected static void analyzeCreateTableStmt(TCreateTableSqlStatement pStmt) {
        System.out.println("Table Name:" + pStmt.getTargetTable().toString());
        System.out.println("Columns:");
        TColumnDefinition column;
        for (int i = 0; i < pStmt.getColumnList().size(); i++) {
            column = pStmt.getColumnList().getColumn(i);
            System.out.println("\tname:" + column.getColumnName().toString());
            System.out.println("\tdatetype:" + column.getDatatype().toString());
            if (column.getDefaultExpression() != null) {
                System.out.println("\tdefault:" + column.getDefaultExpression().toString());
            }
            if (column.isNull()) {
                System.out.println("\tnull: yes");
            }
            if (column.getConstraints() != null) {
                System.out.println("\tinline constraints:");
                for (int j = 0; j < column.getConstraints().size(); j++) {
                    printConstraint(column.getConstraints().getConstraint(j), false);
                }
            }
            System.out.println("");
        }

        if (pStmt.getTableConstraints().size() > 0) {
            System.out.println("\toutline constraints:");
            for (int i = 0; i < pStmt.getTableConstraints().size(); i++) {
                printConstraint(pStmt.getTableConstraints().getConstraint(i), true);
                System.out.println("");
            }
        }
    }

}
