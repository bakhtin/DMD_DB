package core.sqlparser;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TBaseType;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TJoin;
import gudusoft.gsqlparser.nodes.TJoinItem;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import org.junit.Test;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         10/21/2015
 */
public class SQLParserTest {
    protected static void analyzeStmt(TCustomSqlStatement stmt) {

        switch (stmt.sqlstatementtype) {
            case sstselect:
                analyzeSelectStmt((TSelectSqlStatement) stmt);
                break;
            case sstupdate:
                break;
            case sstcreatetable:
                break;
            case sstaltertable:
                break;
            case sstcreateview:
                break;
            default:
                System.out.println(stmt.sqlstatementtype.toString());
        }
    }

    protected static void analyzeSelectStmt(TSelectSqlStatement pStmt) {
        System.out.println("\nSelect statement:");
        if (pStmt.isCombinedQuery()) {
            String setstr = "";
            switch (pStmt.getSetOperator()) {
                case 1:
                    setstr = "union";
                    break;
                case 2:
                    setstr = "union all";
                    break;
                case 3:
                    setstr = "intersect";
                    break;
                case 4:
                    setstr = "intersect all";
                    break;
                case 5:
                    setstr = "minus";
                    break;
                case 6:
                    setstr = "minus all";
                    break;
                case 7:
                    setstr = "except";
                    break;
                case 8:
                    setstr = "except all";
                    break;
            }
            System.out.printf("set type: %s\n", setstr);
            System.out.println("left select:");
            analyzeSelectStmt(pStmt.getLeftStmt());
            System.out.println("right select:");
            analyzeSelectStmt(pStmt.getRightStmt());
            if (pStmt.getOrderbyClause() != null) {
                System.out.printf("order by clause %s\n", pStmt.getOrderbyClause().toString());
            }
        }else{
            //select list
            for(int i=0; i < pStmt.getResultColumnList().size();i++){
                TResultColumn resultColumn = pStmt.getResultColumnList().getResultColumn(i);
                System.out.printf("\tColumn: %s, Alias: %s\n",resultColumn.getExpr().toString(), (resultColumn.getAliasClause() == null)?"":resultColumn.getAliasClause().toString());
            }

            //from clause, check this document for detailed information
            //http://www.sqlparser.com/sql-parser-query-join-table.php
            for(int i=0;i<pStmt.joins.size();i++){
                TJoin join = pStmt.joins.getJoin(i);
                switch (join.getKind()){
                    case TBaseType.join_source_fake:
                        System.out.printf("\ntabledescriptor: \n\t%s, alias: %s\n",join.getTable().toString(),(join.getTable().getAliasClause() !=null)?join.getTable().getAliasClause().toString():"");
                        break;
                    case TBaseType.join_source_table:
                        System.out.printf("\ntabledescriptor: \n\t%s, alias: %s\n",join.getTable().toString(),(join.getTable().getAliasClause() !=null)?join.getTable().getAliasClause().toString():"");
                        for(int j=0;j<join.getJoinItems().size();j++){
                            TJoinItem joinItem = join.getJoinItems().getJoinItem(j);
                            System.out.printf("Join type: %s\n",joinItem.getJoinType().toString());
                            System.out.printf("tabledescriptor: %s, alias: %s\n",joinItem.getTable().toString(),(joinItem.getTable().getAliasClause() !=null)?joinItem.getTable().getAliasClause().toString():"");
                            if (joinItem.getOnCondition() != null){
                                System.out.printf("On: %s\n",joinItem.getOnCondition().toString());
                            }else  if (joinItem.getUsingColumns() != null){
                                System.out.printf("using: %s\n",joinItem.getUsingColumns().toString());
                            }
                        }
                        break;
                    case TBaseType.join_source_join:
                        TJoin source_join = join.getJoin();
                        System.out.printf("\ntabledescriptor: \n\t%s, alias: %s\n",source_join.getTable().toString(),(source_join.getTable().getAliasClause() !=null)?source_join.getTable().getAliasClause().toString():"");

                        for(int j=0;j<source_join.getJoinItems().size();j++){
                            TJoinItem joinItem = source_join.getJoinItems().getJoinItem(j);
                            System.out.printf("source_join type: %s\n",joinItem.getJoinType().toString());
                            System.out.printf("tabledescriptor: %s, alias: %s\n",joinItem.getTable().toString(),(joinItem.getTable().getAliasClause() !=null)?joinItem.getTable().getAliasClause().toString():"");
                            if (joinItem.getOnCondition() != null){
                                System.out.printf("On: %s\n",joinItem.getOnCondition().toString());
                            }else  if (joinItem.getUsingColumns() != null){
                                System.out.printf("using: %s\n",joinItem.getUsingColumns().toString());
                            }
                        }

                        for(int j=0;j<join.getJoinItems().size();j++){
                            TJoinItem joinItem = join.getJoinItems().getJoinItem(j);
                            System.out.printf("Join type: %s\n",joinItem.getJoinType().toString());
                            System.out.printf("tabledescriptor: %s, alias: %s\n",joinItem.getTable().toString(),(joinItem.getTable().getAliasClause() !=null)?joinItem.getTable().getAliasClause().toString():"");
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
            if (pStmt.getWhereClause() != null) {
                System.out.printf("\nwhere clause: \n\t%s\n", pStmt.getWhereClause().getCondition().toString());
            }

            // group by
            if (pStmt.getGroupByClause() != null) {
                System.out.printf("\ngroup by: \n\t%s\n", pStmt.getGroupByClause().toString());
            }

            // order by
            if (pStmt.getOrderbyClause() != null) {
                System.out.printf("\norder by:");
                for (int i = 0; i < pStmt.getOrderbyClause().getItems().size(); i++) {
                    System.out.printf("\n\t%s", pStmt.getOrderbyClause().getItems().getOrderByItem(i).toString());

                }
            }

            // for update
            if (pStmt.getForUpdateClause() != null) {
                System.out.printf("for update: \n%s\n", pStmt.getForUpdateClause().toString());
            }

            // top clause
            if (pStmt.getTopClause() != null) {
                System.out.printf("top clause: \n%s\n", pStmt.getTopClause().toString());
            }

            // limit clause
            if (pStmt.getLimitClause() != null) {
                System.out.printf("top clause: \n%s\n", pStmt.getLimitClause().toString());
            }
        }
    }

    @Test
    public void testSQL() {

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);

        sqlparser.sqltext = "INSERT INTO `affiliation` VALUES (110894,'\\\"A.I. Cuza\\\" Univ., Iasi, Romania'),(116002,'\\\"Al. I. Cuza\\\" Univ., Romania'),(34860,'\\\"Almaz\\\" Sci. Ind. Corp., Moscow'),(88740,'\\\"ARGUS-RPH\\\" Ltd., St. Petersburg, Russia'),(68438,'\\\"Aurel Vlaicu\\\" Univ. of Arad'),(118322,'\\\"Boris Kidric\\\" Institute of Nuclear Science, Vinca, Belgrade, Yugoslavia'),(252683,'\\\"Boris Kidric\\\", Belgrade, Yugoslavia'),(91865,'\\\"Circuit Qualification\\\" Res. Group, TIMA Lab., Grenoble, France'),(234439,'\\\"D.Obradovic\\\" High Sch., Novi Knezevac, Serbia'),(70359,'\\\"De Montfort\\'\\' University'),(151764,'\\\"DELTA\\\" S.P.E., Kiev, Ukraine'),(187701,'\\\"Dunarea de Jos\\\" Univ. of Galati'),(73000,'\\\"E. De Castro\\\" Advanced Research Center on Electronic Systems and Department of Electronics, Computer Science and Systems, University of Bologna, Viale Risorgimento 2, 1-40136 Bologna, Italy. Tel. +39-051-20-93016, Fax. -93779, E-mail: mrudan@arces.unibo.it'),(246335,'\\\"Eye Microsurgery\\\" Research & Technology Center'),(171769,'\\\"Gh. Asachi\\\" Tech. Univ., Iasi, Romania'),(26303,'\\\"Gh. Asachi\\\" Technical University'),(26301,'\\\"Gh. Asachi\\\" Technical University of Iasi'),(72590,'\\\"Inf. & Meas. Equip.\\\" Chair, Ufa State Aviation Tech. Univ., Russia'),(230149,'\\\"Istituto di Elettrotecnica\\\", Roma, Italy'),(88756,'\\\"Istok\\\" Fed. State-Owned Unitary Res. & Production Enterprise, Moscow Region, Russia'),(261786,'\\\"Kerry\\\" Barnham Sussex, England'),(246436,'\\\"KPI\\\" Res. Inst. of Telecommun., Ukraine Nat. Tech. Univ., Kiev'),(173077,'\\\"La Sapienza\\\" University of Rome, Rome, Italy'),(175714,'\\\"Laboratoire de G&#233;nie Industriel et de Production M&#233;canique\\\", Universit&#233; de Metz - LGIPM-CEMA - Ile du Saulcy, F-57045 METZ Cedex 01, sauvey@univ-metz.fr'),(91382,'\\\"Le Moulin de Paradis\\\", Martiques, France'),(34790,'\\\"LETI\\\", St. Petersburg ElectroTechnical Univ., Russia'),(147951,'\\\"Lucian Blaga\\\" Univ., Sibiu, Romania'),(171007,'\\\"Matematica Aplicada\\\" Dept., Univ. of Valladolid, Spain'),(63925,'\\\"Nello Carrara\\\" Res. Inst. on Electromagn. Waves (IROE), CNR, Florence, Italy'),(212064,'\\\"Ovidius\\\" Univ., Constanta, Romania'),(70365,'\\\"Politehnica University of Bucharest'),(111949,'\\\"Politehnica\\\" Bucharest, CETTI, Bucharest'),(99358,'\\\"Politehnica\\\" Univ. of Bucharest, Romania'),(134234,'\\\"Politehnica\\\" Univ., Bucharest, Romania'),(93899,'\\\"Politehnica\\\" University of Bucharest'),(260199,'\\\"Politehnica\\\" University of Bucharest,'),(70369,'\\\"Politehnica\\\" University of Timisoara'),(70362,'\\\"Politehnica\\\" University Timisoara'),(238426,'\\\"Rade Koncar\\\" -Institute, Zagreb, Yugoslavia'),(261479,'\\\"RADEM\\\" Joint-Stock Company, Moscow, Russia'),(263013,'\\\"Radioavionika\\\" Corp., Saint-Petersburg, USA'),(234253,'\\\"Radiosrtim\\\" Ltd., Moscow, Russia'),(112744,'\\\"Rafael\\\", Israel'),(264536,'\\\"RMI\\\", Dnepropertrovsk, Ukraine'),(86793,'\\\"Rudi Cajavec\\\" Beograd, Yugoslavija'),(88749,'\\\"Sci-Res. Inst. of Radio Meas.\\\", JSC, Kharkov, Ukraine'),(171718,'\\\"Stefan eel Mare\\\" Univ., Suceava, Romania'),(88650,'\\\"Svetlana-Elektronpribor\\\" Design Bur., St.Petersburg, Russia'),(32109,'\\\"TAMag Iberica S.L\\\", Madrid, Spain'),(138048,'\\\"Telecommun.\\\" Fac., Nat. Univ. \\\"Lvivska Polytechnika\\\", Lviv, Ukraine'),(26310,'\\\"Transilvania\\\" University of Brasov'),(217646,'#84, 3809-45 St., Calgary, Alta., Canada'),(290966,'& Square Lane, Pittsford, NY, USA'),(86279,'&#197;bo Akademi'),(236348,'&#199;ukurova University, Adana, Turkey'),(86199,'&#201;cole Polytechnique de Montr&#233;al'),(66827,'&#201;cole Polytechnique de Montr&#233;al, Canada'),(74320,'&#201;cole Polytechnique F&#233;d&#233;rale de Lausanne, CH-1015 Switzerland');";

        int ret = sqlparser.parse();
        if (ret == 0) {
            for (int i = 0; i < sqlparser.sqlstatements.size(); i++) {
                analyzeStmt(sqlparser.sqlstatements.get(i));
                System.out.println("");
            }
        } else {
            System.out.println(sqlparser.getErrormessage());
        }

    }
}
