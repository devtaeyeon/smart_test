//package co.irexnet.waio.WAIO_ServerAgent.controller;
//
//import co.irexnet.waio.WAIO_ServerAgent.dto.AccessTokenDTO;
//import co.irexnet.waio.WAIO_ServerAgent.dto.TagManageDTO;
//import co.irexnet.waio.WAIO_ServerAgent.service.DatabaseServiceImpl;
//import co.irexnet.waio.WAIO_ServerAgent.util.CommonValue;
//import co.irexnet.waio.WAIO_ServerAgent.util.ProcessCodeList;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
/**
 * @deprecated 24-11-17 기준 사용하지 않는 컨트롤러
 */
//@RestController
//@EnableSwagger2
//@Slf4j
//public class TagManageController
//{
//    @Autowired
//    DatabaseServiceImpl databaseService;
//
//    @Autowired
//    ProcessCodeList processCodeList;
//
//    @RequestMapping(value = "/tagmanage", method = RequestMethod.POST)
//    public ResponseEntity<String> postTagManage(
//            @RequestHeader("X-ACCESS-TOKEN") String token, @RequestBody TagManageDTO tagManage)
//    {
//        log.debug("Recv postTagManage, tagManage:[{}]", tagManage);
//
//        // Check tagManage
//        if(tagManage.getAi_cd() == null || tagManage.getProc_cd() == null ||
//                tagManage.getSer() == null || tagManage.getLoc() == null || tagManage.getItm() == null ||
//                tagManage.getTag_sn() == null || tagManage.getDp() == null || tagManage.getTag_ty() == null)
//        {
//            String strErrorBody = "{\"reason\":\"tag manage data must not null.\"}";
//            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
//        }
//
//        // Check code
//        if(processCodeList.isExist(tagManage.getAi_cd()) == false)
//        {
//            String strErrorBody = "{\"reason\":\"Invalid algorithm code\"}";
//            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
//        }
//        if(processCodeList.isExist(tagManage.getProc_cd()) == false)
//        {
//            String strErrorBody = "{\"reason\":\"Invalid process code\"}";
//            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
//        }
//
//        // Check Access Token
//        boolean bFindToken = false;
//        int nAuthority;
//        AccessTokenDTO accessTokenDTO = databaseService.getToken(token);
//        if(accessTokenDTO != null)
//        {
//            nAuthority = accessTokenDTO.getUsr_auth();
//            if(nAuthority == CommonValue.USER)
//            {
//                String strErrorBody = "{\"reason\":\"" + HttpStatus.FORBIDDEN.getReasonPhrase() + "\"}";
//                return new ResponseEntity<>(strErrorBody, HttpStatus.FORBIDDEN);
//            }
//            else if(nAuthority == CommonValue.NONE_AUTHORITY)
//            {
//                String strErrorBody = "{\"reason\":\"Authority is not set.\"}";
//                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
//            }
//            else
//            {
//                bFindToken = true;
//            }
//        }
//
//        if(bFindToken == true)
//        {
//            // Insert tag_manage
//            int nResult = databaseService.addTagManage(tagManage);
//            log.debug("Insert tag_manage, result:[{}]", nResult);
//            if(nResult > 0)
//            {
//                return new ResponseEntity<>("", HttpStatus.CREATED);
//            }
//            else
//            {
//                String strErrorBody = "{\"reason\":\"" + HttpStatus.CONFLICT.getReasonPhrase() + "\"}";
//                return new ResponseEntity<>(strErrorBody, HttpStatus.CONFLICT);
//            }
//        }
//        else
//        {
//            String strErrorBody = "{\"reason\":\"" + HttpStatus.UNAUTHORIZED.getReasonPhrase() + "\"}";
//            return new ResponseEntity<>(strErrorBody, HttpStatus.UNAUTHORIZED);
//        }
//    }
//
//    @RequestMapping(value = "/tagmanage", method = RequestMethod.GET)
//    public ResponseEntity<String> getTagManage(@RequestHeader("X-ACCESS-TOKEN") String token)
//    {
//        log.debug("Recv getTagManage");
//
//        // Check Access Token
//        boolean bFindToken = false;
//        int nAuthority;
//        AccessTokenDTO accessTokenDTO = databaseService.getToken(token);
//        if(accessTokenDTO != null)
//        {
//            nAuthority = accessTokenDTO.getUsr_auth();
//            if(nAuthority == CommonValue.USER)
//            {
//                String strErrorBody = "{\"reason\":\"" + HttpStatus.FORBIDDEN.getReasonPhrase() + "\"}";
//                return new ResponseEntity<>(strErrorBody, HttpStatus.FORBIDDEN);
//            }
//            else if(nAuthority == CommonValue.NONE_AUTHORITY)
//            {
//                String strErrorBody = "{\"reason\":\"Authority is not set.\"}";
//                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
//            }
//            else
//            {
//                bFindToken = true;
//            }
//        }
//
//        if(bFindToken == true)
//        {
//            List<TagManageDTO> tagManageList = databaseService.getAllTagManage();
//            log.debug("getAllTagManage, result:[{}]", tagManageList.size());
//            if(tagManageList.size() > 0)
//            {
//                // Make Response Body
//                Map<String, Object> responseBody = new HashMap<>();
//                responseBody.put("tagmanage", tagManageList);
//
//                ObjectMapper objectMapper = new ObjectMapper();
//                String strBody = "";
//                try
//                {
//                    strBody = objectMapper.writeValueAsString(responseBody);
//                }
//                catch(JsonProcessingException e)
//                {
//                    String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
//                    return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
//                }
//                return new ResponseEntity<>(strBody, HttpStatus.OK);
//            }
//            else
//            {
//                String strErrorBody = "{\"reason\":\"Empty tag_manage\"}";
//                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
//            }
//        }
//        else
//        {
//            String strErrorBody = "{\"reason\":\"" + HttpStatus.UNAUTHORIZED.getReasonPhrase() + "\"}";
//            return new ResponseEntity<>(strErrorBody, HttpStatus.UNAUTHORIZED);
//        }
//    }
//
//    @RequestMapping(value = "/tagmanage", method = RequestMethod.PUT)
//    public ResponseEntity<String> putTagManage(
//            @RequestHeader("X-ACCESS-TOKEN") String token, @RequestBody TagManageDTO tagManage)
//    {
//        log.debug("Recv putTagManage, tagManage:[{}]", tagManage);
//
//        // Check tagManage
//        if(tagManage.getAi_cd() == null || tagManage.getProc_cd() == null ||
//                tagManage.getSer() == null || tagManage.getLoc() == null || tagManage.getItm() == null ||
//                tagManage.getTag_sn() == null || tagManage.getDp() == null || tagManage.getTag_ty() == null)
//        {
//            String strErrorBody = "{\"reason\":\"tag manage data must not null.\"}";
//            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
//        }
//
//        // Check code
//        if(processCodeList.isExist(tagManage.getAi_cd()) == false)
//        {
//            String strErrorBody = "{\"reason\":\"Invalid algorithm code\"}";
//            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
//        }
//        if(processCodeList.isExist(tagManage.getProc_cd()) == false)
//        {
//            String strErrorBody = "{\"reason\":\"Invalid process code\"}";
//            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
//        }
//
//        // Check Access Token
//        boolean bFindToken = false;
//        int nAuthority;
//        AccessTokenDTO accessTokenDTO = databaseService.getToken(token);
//        if(accessTokenDTO != null)
//        {
//            nAuthority = accessTokenDTO.getUsr_auth();
//            if(nAuthority == CommonValue.USER)
//            {
//                String strErrorBody = "{\"reason\":\"" + HttpStatus.FORBIDDEN.getReasonPhrase() + "\"}";
//                return new ResponseEntity<>(strErrorBody, HttpStatus.FORBIDDEN);
//            }
//            else if(nAuthority == CommonValue.NONE_AUTHORITY)
//            {
//                String strErrorBody = "{\"reason\":\"Authority is not set.\"}";
//                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
//            }
//            else
//            {
//                bFindToken = true;
//            }
//        }
//
//        if(bFindToken == true)
//        {
//            int nResult = databaseService.modTagManage(tagManage);
//
//            if(nResult > 0)
//            {
//                // Make Response Body
//                Map<String, Object> responseBody = new HashMap<>();
//                responseBody.put("tagmanage", tagManage);
//
//                ObjectMapper objectMapper = new ObjectMapper();
//                String strBody = "";
//                try
//                {
//                    strBody = objectMapper.writeValueAsString(responseBody);
//                }
//                catch(JsonProcessingException e)
//                {
//                    String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
//                    return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
//                }
//                return new ResponseEntity<>(strBody, HttpStatus.OK);
//            }
//            else
//            {
//                String strErrorBody = "{\"reason\":\"" + HttpStatus.NOT_FOUND.getReasonPhrase() + "\"}";
//                return new ResponseEntity<>(strErrorBody, HttpStatus.NOT_FOUND);
//            }
//        }
//        else
//        {
//            String strErrorBody = "{\"reason\":\"" + HttpStatus.UNAUTHORIZED.getReasonPhrase() + "\"}";
//            return new ResponseEntity<>(strErrorBody, HttpStatus.UNAUTHORIZED);
//        }
//    }
//
//    @RequestMapping(value = "/tagmanage", method = RequestMethod.DELETE)
//    public ResponseEntity<String> deleteTagManage(
//            @RequestHeader("X-ACCESS-TOKEN") String token, @RequestBody TagManageDTO tagManage)
//    {
//        log.debug("Recv deleteTagManage, tagManage:[{}]", tagManage);
//
//        // Check tagManage
//        if(tagManage.getAi_cd() == null || tagManage.getProc_cd() == null ||
//                tagManage.getSer() == null || tagManage.getLoc() == null || tagManage.getItm() == null ||
//                tagManage.getTag_sn() == null)
//        {
//            String strErrorBody = "{\"reason\":\"tag manage data must not null.\"}";
//            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
//        }
//
//        // Check code
//        if(processCodeList.isExist(tagManage.getAi_cd()) == false)
//        {
//            String strErrorBody = "{\"reason\":\"Invalid algorithm code\"}";
//            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
//        }
//        if(processCodeList.isExist(tagManage.getProc_cd()) == false)
//        {
//            String strErrorBody = "{\"reason\":\"Invalid process code\"}";
//            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
//        }
//
//        // Check Access Token
//        boolean bFindToken = false;
//        int nAuthority;
//        AccessTokenDTO accessTokenDTO = databaseService.getToken(token);
//        if(accessTokenDTO != null)
//        {
//            nAuthority = accessTokenDTO.getUsr_auth();
//            if(nAuthority == CommonValue.USER)
//            {
//                String strErrorBody = "{\"reason\":\"" + HttpStatus.FORBIDDEN.getReasonPhrase() + "\"}";
//                return new ResponseEntity<>(strErrorBody, HttpStatus.FORBIDDEN);
//            }
//            else if(nAuthority == CommonValue.NONE_AUTHORITY)
//            {
//                String strErrorBody = "{\"reason\":\"Authority is not set.\"}";
//                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
//            }
//            else
//            {
//                bFindToken = true;
//            }
//        }
//
//        if(bFindToken == true)
//        {
//            int nResult = databaseService.delTagManage(tagManage);
//            log.info("delTagManage, result:[{}]", nResult);
//
//            if(nResult > 0)
//            {
//                return new ResponseEntity<>("", HttpStatus.NO_CONTENT);
//            }
//            else
//            {
//                String strErrorBody = "{\"reason\":\"" + HttpStatus.NOT_FOUND.getReasonPhrase() + "\"}";
//                return new ResponseEntity<>(strErrorBody, HttpStatus.NOT_FOUND);
//            }
//        }
//        else
//        {
//            String strErrorBody = "{\"reason\":\"" + HttpStatus.UNAUTHORIZED.getReasonPhrase() + "\"}";
//            return new ResponseEntity<>(strErrorBody, HttpStatus.UNAUTHORIZED);
//        }
//    }
//}
