package de.unidisk.rest;

import de.unidisk.IKeyword;
import de.unidisk.IOverview;
import de.unidisk.IScores;
import de.unidisk.ITopic;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * TODO:
 * This should split in seperate request with their own urls accordingly
 */
@Path("/legacy")
public class LegacyService {

    @Inject
    private IOverview overview;

    @Inject
    private IKeyword keyword;

    @Inject
    private IScores scores;

    @Inject
    private ITopic topic;

    @Path("/handledb")
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    public Response getDBData(LegacyRequestData legacyDBRequest) {
        LoadPurpose loadPurpose = legacyDBRequest.getLoadPurpose();
        Object result = null;
        switch (loadPurpose) {
            case overview: {
                result = overview.getOverview();

               /* $mysql_Result = $database->getOverview();
                echo json_encode($mysql_Result);
                echo "#";
                $mysql_Result = $database->getSumOfRows();
                echo json_encode($mysql_Result);*/
                break;
            }
            case saveCampaign: {
            /*    $database->newCampaign($_POST['Name']);
                $database->createTable($_POST['Name']);
                break;*/

                break;
            }
            case deleteElement:
                /*$mysql_Result = $database->deleteElement($_POST['element'], $_POST['campaign']);*/
                break;
            case loadScoreStich:
                /*$mysql_Result = $database->loadScoreStich($_POST['group'], $_POST['campaign']);
                echo json_encode($mysql_Result);*/
                break;
            case loadVarSolrSum:
            /*    $mysql_Result = $database->loadVarSolrSum($_POST['campaign']);
                echo json_encode($mysql_Result);*/
                break;
            case saveStichVarMeta:
                /*$database->saveStichVarMeta($_POST['stich'], $_POST['vari'], $_POST['campaign']);*/
                break;
            case loadStichVarMeta:
                /*
                $mysql_Result = $database->loadStichVarMeta($_POST['campaign']);
                if (sizeof($mysql_Result) > 0) {
                    echo json_encode($mysql_Result);
                }
                echo "#";
                $mysql_Result = $database->loadScoreStich($_POST['groupStich'], $_POST['campaign']);
                if (sizeof($mysql_Result) > 0) {
                    echo json_encode($mysql_Result);
                }
                echo "#";
                $mysql_Result = $database->loadScoreVar($_POST['groupVar'], $_POST['campaign']);
                if (sizeof($mysql_Result) > 0) {
                    echo json_encode($mysql_Result);
                }*/

                break;
        }


        return Response.ok(result, MediaType.APPLICATION_JSON).build();
    }


}
